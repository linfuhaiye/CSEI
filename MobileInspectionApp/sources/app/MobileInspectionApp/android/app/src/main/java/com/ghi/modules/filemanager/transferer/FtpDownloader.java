package com.ghi.modules.filemanager.transferer;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.ghi.modules.filemanager.IFileManager;
import com.ghi.modules.filemanager.task.Task;
import com.ghi.modules.filemanager.task.TaskAlreadyExistsException;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FTP下载器
 *
 * @author Alex
 */
public final class FtpDownloader implements IDownloader {
    /**
     * 文件管理器
     */
    private final IFileManager fileManager;

    /**
     * 线程池
     */
    private final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    /**
     * 下载任务列表
     */
    private final ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param fileManager 文件管理器
     */
    public FtpDownloader(final IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * 检查是否命中缓存
     *
     * @param task 任务
     * @return 是否命中缓存
     */
    private static boolean isHitCache(final Task task) {
        // 生成路径
        String localPath = task.getLocalPath() + File.separator + task.getFilename();

        // 检查是否命中缓存
        if (FileUtils.isFileExists(task.getCache())) {
            task.setStatus(Task.Status.Running);
            FileUtils.copy(task.getCache(), localPath);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 连接服务器
     *
     * @param client 客户端
     * @param task   任务
     * @throws IOException 异常
     */
    private static void connect(final FTPClient client, final Task task) throws IOException {
        client.setControlEncoding("UTF-8");

        // 连接服务器
        client.connect(task.getIp(), task.getPort());
        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new IOException("connect fail: " + reply);
        }

        // 登录
        boolean login = client.login(task.getUsername(), task.getPassword());
        if (!login) {
            client.disconnect();
            throw new IOException("connect fail: " + reply);
        }

        reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new IOException("connect fail: " + reply);
        }

        // 配置
        FTPClientConfig config = new FTPClientConfig(client.getSystemType().split(" ")[0]);
        config.setServerLanguageCode("zh");
        client.configure(config);

        // 设置模式
        if (task.isActiveMode()) {
            client.enterLocalActiveMode();
        } else {
            client.enterLocalPassiveMode();
        }

        // 支持二进制文件
        client.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
    }

    /**
     * 断开连接
     *
     * @param client 客户端
     */
    private static void disconnect(final FTPClient client) {
        if (client.isConnected()) {
            try {
                client.abort();
                client.logout();
                client.disconnect();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
    }

    /**
     * 下载
     *
     * @param client      客户端
     * @param task        任务
     * @param fileManager 文件管理器
     * @throws Exception 异常
     */
    private static void download(final FTPClient client, final Task task, final IFileManager fileManager) throws Exception {
        // 生成路径
        String remotePath = task.getRemotePath() + File.separator + task.getFilename();
        String localPath = task.getLocalPath() + File.separator + task.getFilename();

        // 获取文件大小
        FTPFile file = client.mlistFile(remotePath);
        task.setSize(file.getSize());
        task.setStatus(Task.Status.Running);

        // 接收文件
        try (OutputStream os = new FileOutputStream(localPath); CountingOutputStream cos = new CountingOutputStream(os) {
            @Override
            protected void beforeWrite(int n) {
                super.beforeWrite(n);
                task.setProcessed(getCount());
                fileManager.onProgress(task.getUuid(), getCount(), task.getSize());
            }
        }) {
            client.retrieveFile(remotePath, cos);
        }

        // 复制文件
        if (task.getCopyTo() != null) {
            FileUtils.copy(localPath, task.getCopyTo());
        }
    }

    @Override
    public void download(final Task task) {
        threadPool.execute(() -> {
            FTPClient client = null;
            Task t = null;

            try {
                // 创建任务
                client = new FTPClient();
                t = createTask(task, client);
                t.setStatus(Task.Status.Connecting);
                LogUtils.d("ftp download start:" + task);

                // 重试
                int retry = IFileManager.RETRY_COUNT;
                while (retry-- > 0) {
                    try {
                        // 命中缓存则不下载
                        if (isHitCache(t)) {
                            break;
                        }

                        connect(client, t);
                        download(client, t, fileManager);
                        break;
                    } catch (Exception e) {
                        LogUtils.e(e);
                        if (retry == 0) {
                            throw e;
                        }
                    }
                }

                LogUtils.d("ftp download success");
                t.setStatus(Task.Status.Complete);
                fileManager.onComplete(t.getUuid());
            } catch (TaskAlreadyExistsException e) {
                LogUtils.e(e);
            } catch (Exception e) {
                LogUtils.e(e);
                if (t != null) {
                    t.setStatus(Task.Status.Fail);
                }
            } finally {
                if (client != null) {
                    disconnect(client);
                }

                if (t != null) {
                    destroyTask(t);
                }
            }
        });
    }

    @Override
    public void stopTask(final String uuid) throws Exception {
        Task task = tasks.get(uuid);
        if (task != null) {
            ((FTPClient) task.getContext()).abort();
        }
    }

    @Override
    public void stopAllTasks() {
        for (Task task : tasks.values()) {
            try {
                ((FTPClient) task.getContext()).abort();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
    }

    /**
     * 创建任务
     *
     * @param task   任务
     * @param client 客户端
     * @return 任务
     * @throws Exception 异常
     */
    private Task createTask(final Task task, final FTPClient client) throws Exception {
        task.setStatus(Task.Status.Idle);
        task.setUpload(false);
        task.setContext(client);

        if (tasks.containsKey(task.getUuid())) {
            throw new TaskAlreadyExistsException();
        } else {
            tasks.put(task.getUuid(), task);
        }

        return task;
    }

    /**
     * 销毁任务
     *
     * @param task 任务
     */
    private void destroyTask(final Task task) {
        tasks.remove(task.getUuid());
    }
}

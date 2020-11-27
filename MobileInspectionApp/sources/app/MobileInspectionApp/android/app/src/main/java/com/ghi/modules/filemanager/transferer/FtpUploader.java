package com.ghi.modules.filemanager.transferer;

import com.blankj.utilcode.util.LogUtils;
import com.ghi.modules.filemanager.IFileManager;
import com.ghi.modules.filemanager.task.Task;
import com.ghi.modules.filemanager.task.TaskAlreadyExistsException;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FTP上传器
 *
 * @author Alex
 */
public final class FtpUploader implements IUploader {

    /**
     * 文件管理器
     */
    private IFileManager fileManager;

    /**
     * 线程池
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(3);

    /**
     * 上传任务列表
     */
    private ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param fileManager 文件管理器
     */
    public FtpUploader(final IFileManager fileManager) {
        this.fileManager = fileManager;
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
     * 上传
     *
     * @param client      客户端
     * @param task        任务
     * @param fileManager 文件管理器
     * @throws Exception 异常
     */
    private static void upload(final FTPClient client, final Task task, final IFileManager fileManager) throws Exception {
        // 生成路径
        String remotePath = task.getRemotePath();
        String localPath = task.getLocalPath() + File.separator + task.getFilename();

        File file = new File(localPath);
        task.setSize(file.length());
        task.setStatus(Task.Status.Running);
        // 接收文件
        try (InputStream is = new FileInputStream(file); CountingInputStream cis = new CountingInputStream(is) {
            @Override
            protected void afterRead(int n) {
                super.afterRead(n);
                task.setProcessed(getCount());
                fileManager.onProgress(task.getUuid(), getCount(), task.getSize());
            }

        }) {
            client.setFileType(client.BINARY_FILE_TYPE);
            createDirectory(remotePath, client);
            client.makeDirectory(remotePath);
            client.changeWorkingDirectory(remotePath);
            client.storeFile(task.getFilename(), cis);
        }
    }

    /**
     * 改变目录路径
     *
     * @param directory
     * @param client
     * @return
     */
    private static boolean changeWorkingDirectory(String directory, FTPClient client) {
        boolean flag = true;
        try {
            flag = client.changeWorkingDirectory(directory);
            if (flag) {
                LogUtils.d("进入文件夹" + directory + " 成功！");

            } else {
                LogUtils.d("进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断ftp服务器文件是否存在
     *
     * @param path
     * @param client
     * @return
     * @throws IOException
     */
    public static boolean existFile(String path, FTPClient client) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = client.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建 TODO
     *
     * @param remote
     * @return
     * @throws IOException
     */
    private static boolean createDirectory(String remote, FTPClient client) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory), client)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + "/" + subDirectory;
                if (!existFile(path, client)) {
                    if (client.makeDirectory(subDirectory)) {
                        changeWorkingDirectory(subDirectory, client);
                    } else {
                        LogUtils.d("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(subDirectory, client);
                    }
                } else {
                    changeWorkingDirectory(subDirectory, client);
                }
                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }


    @Override
    public void upload(Task task) throws Exception {
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
                        connect(client, t);
                        upload(client, t, fileManager);
                        break;
                    } catch (Exception e) {
                        LogUtils.e(e);
                        if (retry == 0) {
                            throw e;
                        }
                    }
                }

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
    public void stopTask(String uuid) throws Exception {
        Task task = tasks.get(uuid);
        if (task != null) {
            ((FTPClient) task.getContext()).abort();
        }
    }

    @Override
    public void stopAllTasks() throws Exception {
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
        task.setUpload(true);
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

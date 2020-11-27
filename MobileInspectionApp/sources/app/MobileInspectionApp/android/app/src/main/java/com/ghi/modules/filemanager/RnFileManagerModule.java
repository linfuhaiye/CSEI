package com.ghi.modules.filemanager;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.filemanager.task.Task;
import com.ghi.modules.filemanager.task.TaskGroup;
import com.ghi.modules.filemanager.transferer.FtpDownloader;
import com.ghi.modules.filemanager.transferer.FtpUploader;
import com.ghi.modules.filemanager.transferer.IDownloader;
import com.ghi.modules.filemanager.transferer.IUploader;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件管理器
 *
 * @author etrit
 */
public final class RnFileManagerModule extends ReactContextBaseJavaModule implements LifecycleEventListener, IFileManager {
    /**
     * 下载任务列表
     */
    private final ConcurrentHashMap<String, TaskGroup> downloadTasks = new ConcurrentHashMap<>();

    /**
     * 上传任务列表
     */
    private final ConcurrentHashMap<String, TaskGroup> uploadTasks = new ConcurrentHashMap<>();

    /**
     * 下载器
     */
    private final IDownloader downloader = new FtpDownloader(this);

    /**
     * 上传器
     */
    private final IUploader uploader = new FtpUploader(this);

    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
    }

    @Override
    public void onHostDestroy() {
        try {
            downloader.stopAllTasks();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "_FileManager";
    }

    @Override
    public void onProgress(String taskId, long processed, long size) {
        LogUtils.d("onProgress: " + taskId + ", " + processed + "/" + size);
    }

    @Override
    public void onComplete(String taskId) {
        LogUtils.d("onComplete: " + taskId);
    }

    /**
     * 获取下载任务
     *
     * @param uuid    任务组索引
     * @param promise 承诺
     */
    @ReactMethod
    public void getDownloadTasks(final String uuid, final Promise promise) {
        if (uuid == null) {
            promise.resolve(JsonUtils.fromObject(downloadTasks));
            return;
        }

        TaskGroup group = downloadTasks.get(uuid);
        if (group == null) {
            promise.resolve(null);
            return;
        }

        promise.resolve(JsonUtils.fromObject(new ConcurrentHashMap<String, TaskGroup>() {{
            put(group.getUuid(), group);
        }}));
    }

    /**
     * 下载
     *
     * @param tasks   任务组
     * @param promise 承诺
     */
    @ReactMethod
    public void download(final String tasks, final Promise promise) {
        try {
            // 解析任务组
            ArrayList<TaskGroup> taskGroups = JsonUtils.toObject(tasks, new TypeToken<ArrayList<TaskGroup>>() {
            }.getType());
            // 遍历任务组
            for (TaskGroup taskGroup : taskGroups) {
                // 添加下载任务
                for (Task task : taskGroup.getTasks()) {
                    // 设置任务组索引
                    task.setTaskGroupId(taskGroup.getUuid());
                    downloader.download(task);
                }

                this.downloadTasks.put(taskGroup.getUuid(), taskGroup);
            }

            promise.resolve(true);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(false);
        }
    }

    /**
     * 取消下载任务
     *
     * @param promise 承诺
     */
    @ReactMethod
    public void cancelDownload(final Promise promise) {
        try {
            downloader.stopAllTasks();
            downloadTasks.clear();
            promise.resolve(true);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(true);
        }
    }

    /**
     * 获取上传任务
     *
     * @param uuid    任务组索引
     * @param promise 承诺
     */
    @ReactMethod
    public void getUploadTasks(final String uuid, final Promise promise) {
        if (uuid == null) {
//            promise.resolve(JsonUtils.fromObject(new ArrayList(uploadTasks.values())));
            promise.resolve(JsonUtils.fromObject(uploadTasks));
            return;
        }

        TaskGroup group = uploadTasks.get(uuid);
        if (group == null) {
            promise.resolve(null);
            return;
        }

        promise.resolve(JsonUtils.fromObject(new ConcurrentHashMap<String, TaskGroup>() {{
            put(group.getUuid(), group);
        }}));
    }

    /**
     * 上传
     *
     * @param tasks   任务组
     * @param promise 承诺
     */
    @ReactMethod
    public void upload(final String tasks, final Promise promise) {
        try {
            // 解析任务组
            ArrayList<TaskGroup> taskGroups = JsonUtils.toObject(tasks, new TypeToken<ArrayList<TaskGroup>>() {
            }.getType());
            // 遍历任务组
            for (TaskGroup taskGroup : taskGroups) {
                // 添加下载任务
                for (Task task : taskGroup.getTasks()) {
                    // 设置任务组索引
                    task.setTaskGroupId(taskGroup.getUuid());
                    uploader.upload(task);
                }

                this.uploadTasks.put(taskGroup.getUuid(), taskGroup);
            }

            promise.resolve(true);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(false);
        }
    }

    /**
     * 取消上传任务
     *
     * @param promise 承诺
     */
    @ReactMethod
    public void cancelUpload(final Promise promise) {
        try {
            uploader.stopAllTasks();
            uploadTasks.clear();
            promise.resolve(true);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(false);
        }
    }

}

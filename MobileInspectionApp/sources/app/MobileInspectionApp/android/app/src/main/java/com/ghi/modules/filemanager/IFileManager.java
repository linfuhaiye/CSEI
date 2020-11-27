package com.ghi.modules.filemanager;

/**
 * 文件管理器接口
 *
 * @author Alex
 */
public interface IFileManager {
    /**
     * 传输重试次数
     */
    int RETRY_COUNT = 3;

    /**
     * 传输进度事件回调函数
     *
     * @param taskId    任务索引
     * @param processed 已传输长度
     * @param size      文件长度
     */
    void onProgress(final String taskId, final long processed, final long size);

    /**
     * 传输完成事件回调函数
     *
     * @param taskId 任务索引
     */
    void onComplete(final String taskId);
}

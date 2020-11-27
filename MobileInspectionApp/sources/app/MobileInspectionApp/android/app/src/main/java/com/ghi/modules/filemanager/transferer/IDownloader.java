package com.ghi.modules.filemanager.transferer;

import com.ghi.modules.filemanager.task.Task;

/**
 * 下载器
 *
 * @author Alex
 */
public interface IDownloader {
    /**
     * 下载
     *
     * @param task 任务
     * @throws Exception 异常
     */
    void download(final Task task) throws Exception;

    /**
     * 停止任务
     *
     * @param uuid 索引
     * @throws Exception 异常
     */
    void stopTask(final String uuid) throws Exception;

    /**
     * 停止所有任务
     *
     * @throws Exception 异常
     */
    void stopAllTasks() throws Exception;
}

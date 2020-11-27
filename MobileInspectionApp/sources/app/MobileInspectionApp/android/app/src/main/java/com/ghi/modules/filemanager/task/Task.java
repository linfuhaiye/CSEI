package com.ghi.modules.filemanager.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 传输任务
 *
 * @author Alex
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    /**
     * 索引
     */
    private String uuid;

    /**
     * 任务组索引
     */
    private String taskGroupId;

    /**
     * 状态
     */
    private Status status;

    /**
     * 上下文
     */
    private transient Object context;

    /**
     * 服务器IP地址
     */
    private String ip;

    /**
     * 端口
     */
    private int port;

    /**
     * 远程路径
     */
    private String remotePath;

    /**
     * 本地路径
     */
    private String localPath;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 是否上传
     */
    private boolean isUpload;

    /**
     * 文件长度
     */
    private long size;

    /**
     * 已传输长度
     */
    private long processed;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否为主动模式
     */
    private boolean isActiveMode;

    /**
     * 缓存文件路径
     */
    private String cache;

    /**
     * 复制路径
     */
    private String copyTo;

    /**
     * 状态
     */
    public enum Status {
        /**
         * 空闲
         */
        Idle,

        /**
         * 连接中
         */
        Connecting,

        /**
         * 执行中
         */
        Running,

        /**
         * 暂停
         */
        Paused,

        /**
         * 完成
         */
        Complete,

        /**
         * 失败
         */
        Fail
    }
}

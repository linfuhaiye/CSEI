package com.ghi.modules.filemanager.task;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 传输任务组
 *
 * @author etrit
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class TaskGroup {
    /**
     * 索引
     */
    private String uuid;

    /**
     * 任务列表
     */
    private List<Task> tasks;
}

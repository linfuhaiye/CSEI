package com.ghi.modules.pdfviewer.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改日志
 *
 * @author etrit
 */
@Getter
@Setter
@AllArgsConstructor
public final class ModifyLog {
    /**
     * 域名
     */
    private String fieldName;

    /**
     * 旧值
     */
    private String oldValue = "";

    /**
     * 新值
     */
    private String newValue = "";

    /**
     * 时间
     */
    private String time;

    /**
     * 用户名
     */
    private String user;

    public ModifyLog(final String fieldName, final String oldValue, final String newValue) {
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}


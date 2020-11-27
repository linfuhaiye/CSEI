package com.ghi.modules.pdfviewer.data.validators;

import com.ghi.modules.pdfviewer.data.processors.Context;

import lombok.Getter;
import lombok.Setter;

/**
 * 校验器
 *
 * @author Alex
 */
@Getter
@Setter
public abstract class Validator {
    /**
     * 错误域名
     */
    private String errorFieldName;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 校验内容
     *
     * @param context 上下文
     * @return 是否通过
     */
    public abstract boolean validate(final Context context);

    /**
     * 设置错误信息
     *
     * @param fieldName 域名
     * @param message   消息
     */
    protected void setLastError(final String fieldName, final String message) {
        this.errorFieldName = fieldName;
        this.errorMessage = message;
    }
}

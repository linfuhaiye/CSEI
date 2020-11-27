package com.ghi.modules.pdfviewer.data.entities;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * 检验结果
 *
 * @author Alex
 */
@Getter
@Setter
public final class CheckResult {
    /**
     * 校验记录项
     */
    @SerializedName("checkitem")
    private String checkItem;

    /**
     * 检验记录默认值
     */
    @SerializedName("checkvalue")
    private String checkValue;
}

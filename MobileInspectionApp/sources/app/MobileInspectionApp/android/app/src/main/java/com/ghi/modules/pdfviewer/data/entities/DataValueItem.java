package com.ghi.modules.pdfviewer.data.entities;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据值
 *
 * @author Alex
 */
@Getter
@Setter
public final class DataValueItem {
    /**
     * 检验记录观测数据项的后缀
     */
    @SerializedName("gcres")
    private String gcres;

    /**
     * 检验记录结果数据项的后缀
     */
    @SerializedName("clres")
    private String clres;

    /**
     * 表单域的工具提示
     */
    @SerializedName("tip")
    private String tip;

    /**
     * 百分号
     */
    @SerializedName("percent")
    private String percent;
}

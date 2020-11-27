package com.ghi.modules.pdfviewer.data.entities;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改结果
 *
 * @author Alex
 */
@Getter
@Setter
@AllArgsConstructor
public final class Change {
    /**
     * 等级
     */
    @SerializedName("level")
    public int level;

    /**
     * 结果
     */
    @SerializedName("result")
    public String result;

    /**
     * 值
     */
    @SerializedName("value")
    public String value;
}

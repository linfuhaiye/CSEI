package com.ghi.modules.pdfviewer.data.entities;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 修改结果
 *
 * @author Alex
 */
@Getter
@Setter
public final class ChangeResult {
    @SerializedName("level")
    private int level;

    @SerializedName("result")
    private Map<String, String> result = new HashMap<String, String>();
}

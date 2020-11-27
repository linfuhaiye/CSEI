package com.ghi.modules.pdfviewer.data.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 结果
 *
 * @author Alex
 */
@Getter
@Setter
public class ResultItem {
    @SerializedName("dataitem")
    private String dataItem;

    @SerializedName("ispercent")
    private int isPercent = -1;

    @SerializedName("tip")
    private String tip;

    @SerializedName("datavalue")
    private List<DataValueItem> dataValue = new ArrayList<>();
}

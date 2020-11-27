package com.ghi.modules.pdfviewer.data.processors.association;

import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 复制联动配置
 *
 * @author Alex
 */
@Getter
@Setter
public final class CopyingConfiguration implements BindingData.BindingDataEventListener {
    /**
     * 域名
     */
    private static final String FIELD_NAME = "copy_fields";

    /**
     * 源域名
     */
    @SerializedName("copy_from")
    private String from;

    /**
     * 目标域名
     */
    @SerializedName("copy_to")
    private String to;

    /**
     * 双向绑定数据集合
     */
    private BindingDataMap map;

    /**
     * 绑定数据
     *
     * @param map            双向绑定数据集合
     * @param configurations 配置
     */
    public static void bind(final BindingDataMap map, final Map<String, JsonElement> configurations) {
        JsonArray array = JsonUtils.getJsonArray(configurations, FIELD_NAME);
        if (array == null) {
            return;
        }

        for (JsonElement element : array) {
            CopyingConfiguration configuration = JsonUtils.toObject(element, CopyingConfiguration.class);
            if (configuration == null) {
                continue;
            }

            BindingData data = map.get(configuration.from);
            if (data != null) {
                configuration.setMap(map);
                data.addListener(configuration);
            }
        }
    }

    @Override
    public void onChanged(BindingData bindingData, Object oldValue, Object newValue) {
        // 当fname匹配copy_from自动将fvalue填入到copy_to
        map.setValue(to, newValue);
    }
}

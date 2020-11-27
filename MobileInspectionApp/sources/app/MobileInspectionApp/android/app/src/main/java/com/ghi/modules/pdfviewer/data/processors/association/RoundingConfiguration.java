package com.ghi.modules.pdfviewer.data.processors.association;

import com.blankj.utilcode.util.LogUtils;
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
 * 修约配置
 *
 * @author etrit
 */
@Getter
@Setter
public final class RoundingConfiguration implements BindingData.BindingDataEventListener {
    /**
     * 域名
     */
    private static final String FIELD_NAME = "xiuyue_fields";

    /**
     * 圆整规则
     */
    private static final String RULE_ROUND = "Round";

    /**
     * 复制规则
     */
    private static final String RULE_COPY = "Copy";

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
     * 修约规则
     */
    @SerializedName("rule")
    private String rule;

    /**
     * 精度
     */
    @SerializedName("accuracy")
    private String accuracy;

    /**
     * 值域
     */
    @SerializedName("limit")
    private String limit;

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
            RoundingConfiguration configuration = JsonUtils.toObject(element, RoundingConfiguration.class);
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
        try {
            if (RULE_ROUND.equalsIgnoreCase(rule)) {
                // rule 为Round 则把fvalue的值经过修约之后填入到copy_to配置的域中，accuracy为小数位。Limit为修约规则，目前修约规则大部分都没实现，先按照四舍六入五成双来计算
                int length = Math.abs(Integer.parseInt(accuracy));
                map.setValue(to, String.format("%." + length + "f", Double.valueOf(newValue.toString())));
            } else if (RULE_COPY.equalsIgnoreCase(rule)) {
                // rule如果为copy，则直接复制，不需要修约
                map.setValue(to, newValue);
            }
        } catch (Exception e) {
            LogUtils.e("execute fail: " + rule);
            LogUtils.e(e);
        }
    }
}

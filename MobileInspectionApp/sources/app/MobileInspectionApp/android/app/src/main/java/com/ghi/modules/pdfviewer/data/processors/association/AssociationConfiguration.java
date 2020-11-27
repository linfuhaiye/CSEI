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
 * 关联配置
 *
 * @author Alex
 */
@Getter
@Setter
public final class AssociationConfiguration implements BindingData.BindingDataEventListener {
    /**
     * 域名
     */
    private static final String FIELD_NAME = "guanlian_fields";

    /**
     * 相等规则
     */
    private static final String RULE_EQUALS = "=";

    /**
     * 不相等规则
     */
    private static final String RULE_UNEQUALS = "<>";

    /**
     * 乘法规则
     */
    private static final String RULE_MULTIPLY = "*";

    /**
     * 双向绑定数据集合
     */
    private BindingDataMap map;

    /**
     * 源域名
     */
    @SerializedName("copy_from")
    private String from;

    /**
     * 源规则
     */
    @SerializedName("from_rule")
    private String fromRule;

    /**
     * 源预期值
     */
    @SerializedName("from_value")
    private String fromValue;

    /**
     * 目标域名
     */
    @SerializedName("copy_to")
    private String to;

    /**
     * 目标值
     */
    @SerializedName("to_value")
    private String toValue;

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
            AssociationConfiguration configuration = JsonUtils.toObject(element, AssociationConfiguration.class);
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
            // 当fname匹配copy_from判断fvalue和from_value的值满足from_value的关系，对copy_to进行设置值，有等于，不等，还有乘法“*”把fvalue乘以to_value的值，再填入copy_to中
            if (RULE_EQUALS.equalsIgnoreCase(fromRule)) {
                if ((newValue != null) && newValue.equals(fromValue)) {
                    map.setValue(to, toValue);
                }
            } else if (RULE_UNEQUALS.equalsIgnoreCase(fromRule)) {
                if ((newValue != null) && !newValue.equals(fromValue)) {
                    map.setValue(to, toValue);
                }
            } else if (RULE_MULTIPLY.equalsIgnoreCase(fromRule)) {
                // TODO: 检查此规则
                if ((newValue != null) && !newValue.equals(fromValue)) {
                    map.setValue(to, String.valueOf(Float.parseFloat(newValue.toString()) * Float.parseFloat(toValue)));
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }
}

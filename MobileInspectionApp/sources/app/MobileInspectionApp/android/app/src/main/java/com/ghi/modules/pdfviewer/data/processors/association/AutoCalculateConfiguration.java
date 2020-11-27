package com.ghi.modules.pdfviewer.data.processors.association;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 自动计算配置
 *
 * @author Alex
 */
@Getter
@Setter
public final class AutoCalculateConfiguration implements BindingData.BindingDataEventListener {
    /**
     * 域名
     */
    private static final String FIELD_NAME = "auto_count";

    /**
     * 域名
     */
    @SerializedName("left")
    private String left;

    /**
     * 域名
     */
    @SerializedName("right")
    private String right;

    /**
     * 规则
     */
    @SerializedName("rule")
    private String rule;

    /**
     * 圆整
     */
    @SerializedName("Round")
    private String round;

    /**
     * 双向绑定数据集合
     */
    private BindingDataMap map;

    /**
     * 变量列表
     */
    private String[] variables;

    /**
     * 构造函数
     *
     * @param configuration 配置
     */
    public AutoCalculateConfiguration(final AutoCalculateConfiguration configuration) {
        this.left = configuration.left;
        this.right = configuration.right;
        this.rule = configuration.rule;
        this.round = configuration.round;
        this.map = configuration.map;
        this.variables = configuration.variables;
    }

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
            AutoCalculateConfiguration configuration = JsonUtils.toObject(element, AutoCalculateConfiguration.class);
            if (configuration == null) {
                continue;
            }

            if (!StringUtils.isEmpty(configuration.left)) {
                // 拆解域名添加多个配置
                String[] fields = configuration.left.split(",");
                for (String field : fields) {
                    BindingData data = map.get(field);
                    if (data != null) {
                        AutoCalculateConfiguration c = new AutoCalculateConfiguration(configuration);
                        c.setMap(map);
                        c.setVariables(fields);
                        data.addListener(c);
                    }
                }
            }
        }
    }

    @Override
    public void onChanged(BindingData bindingData, Object oldValue, Object newValue) {
        // Fname满足left其中的一个域名，则将rule中的公式带入left中的域的值，按照顺序$1$为替换第一个，$2$为替换第二个，作为计算公式计算出结果根据round修约之后填入right中
        Context rhino = null;
        try {
            // 替换数据
            String rule = this.rule;
            for (int i = 0; i < variables.length; i++) {
                BindingData data = map.get(variables[i]);
                if (data != null) {
                    rule = rule.replace("$" + (i + 1) + "$", data.getValue().toString());
                } else {
                    LogUtils.e("missing variable: " + variables[i]);
                    return;
                }
            }

            // 动态执行
            rhino = Context.enter();
            rhino.setOptimizationLevel(-1);
            ScriptableObject scope = rhino.initStandardObjects();
            Object evaluate = rhino.evaluateString(scope, rule, "JavaScript", 1, null);

            // 修约
            int length = Math.abs(Integer.parseInt(round));
            map.setValue(right, String.format("%." + length + "f", Double.valueOf(evaluate.toString())));
        } catch (Exception e) {
            LogUtils.e("execute fail: " + this.rule);
            LogUtils.e(e);
        } finally {
            if (rhino != null) {
                Context.exit();
            }
        }
    }
}

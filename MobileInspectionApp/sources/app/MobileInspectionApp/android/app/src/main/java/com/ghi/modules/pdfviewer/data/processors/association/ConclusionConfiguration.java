package com.ghi.modules.pdfviewer.data.processors.association;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 结论配置
 *
 * @author Alex
 */
@Getter
@Setter
public final class ConclusionConfiguration implements BindingData.BindingDataEventListener {
    /**
     * 域名
     */
    private static final String FIELD_NAME = "Rguanlian_fields";

    /**
     * 是否启用结论配置域名
     */
    private static final String HAS_DATA = "hasdataR";

    /**
     * 结论等级域名
     */
    private static final String R_LEVEL = "Rlevel";

    /**
     * 启用结论配置
     */
    private static final String ENABLED = "1";

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
     * 双向绑定数据集合
     */
    private BindingDataMap map;

    /**
     * 等级列表
     */
    private Map<String, Level> levels;

    /**
     * 变量列表
     */
    private String[] variables;

    /**
     * 构造函数
     *
     * @param configuration 配置
     */
    public ConclusionConfiguration(final ConclusionConfiguration configuration) {
        this.left = configuration.left;
        this.right = configuration.right;
        this.map = configuration.map;
        this.levels = configuration.levels;
        this.variables = configuration.variables;
    }

    /**
     * 绑定数据
     *
     * @param map            双向绑定数据集合
     * @param configurations 配置
     */
    public static void bind(final BindingDataMap map, final Map<String, JsonElement> configurations) {
        // 判断是否启用结论配置
        String enabled = JsonUtils.getValue(configurations, HAS_DATA);
        if (!ENABLED.equals(enabled)) {
            return;
        }

        // 获取等级列表
        List<Level> list = JsonUtils.getArray(configurations, R_LEVEL, new TypeToken<ArrayList<Level>>() {
        }.getType());
        if (list == null) {
            return;
        }

        Map<String, Level> levels = new HashMap<>(list.size());
        for (Level level : list) {
            levels.put(level.getResult(), level);
        }

        // 绑定数据
        JsonArray array = JsonUtils.getJsonArray(configurations, FIELD_NAME);
        if (array == null) {
            return;
        }

        for (JsonElement element : array) {
            ConclusionConfiguration configuration = JsonUtils.toObject(element, ConclusionConfiguration.class);
            if (configuration == null) {
                continue;
            }

            if (!StringUtils.isEmpty(configuration.left)) {
                // 拆解域名添加多个配置
                String[] fields = configuration.left.split(",");
                for (String field : fields) {
                    BindingData data = map.get(field);
                    if (data != null) {
                        ConclusionConfiguration c = new ConclusionConfiguration(configuration);
                        c.setMap(map);
                        c.setLevels(levels);
                        c.setVariables(fields);
                        data.addListener(c);
                    }
                }
            }
        }
    }

    @Override
    public void onChanged(BindingData bindingData, Object oldValue, Object newValue) {
        // 当fname满足left的其中一个域时，判断left中的多个域，取其中值对应的level最低的conclude值，填入right中，level判断根据Rlevel来
        try {
            // 获取所有数据
            Level minLevel = null;
            for (String variable : variables) {
                BindingData data = map.get(variable);
                if (data != null) {
                    String result = data.getValue().toString();
                    Level l = levels.get(result);
                    if (l == null) {
                        return;
                    }

                    // 获取最小等级
                    if (minLevel == null) {
                        minLevel = l;
                    } else if (l.getLevel() < minLevel.getLevel()) {
                        minLevel = l;
                    }
                }
            }

            // 填入right中
            if (minLevel != null) {
                map.setValue(right, minLevel.getConclude());
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 等级
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Level {
        /**
         * 结果
         */
        private String result;

        /**
         * 等级
         */
        private int level;

        /**
         * 结论
         */
        private String conclude;
    }
}

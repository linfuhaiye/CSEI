package com.ghi.modules.pdfviewer.data.processors;

import com.ghi.miscs.FileUtils;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * 原始记录数据处理器
 *
 * @author Alex
 */
public final class TestLogDataProcessor {
    /**
     * 文件名
     */
    private static final String FILENAME = "/testlogdata.ses";

    /**
     * 获取原始记录数据路径
     *
     * @param path       父路径
     * @param reportCode 报告号
     * @return 路径
     */
    public static String getFilename(final String path, final String reportCode) {
        return path + File.separator + reportCode + FILENAME;
    }

    /**
     * 读取原始记录数据
     *
     * @param path       路径
     * @param reportCode 报告号
     * @return 数据
     */
    public static Map<String, JsonElement> read(final String path, final String reportCode) {
        // 读取原始记录配置文件
        String content = FileUtils.read(getFilename(path, reportCode));
        if (content == null) {
            return null;
        }

        HashMap<String, JsonElement> map = new HashMap<String, JsonElement>();
        // JSON字符串转换为JSON对象
        JsonObject object = JsonParser.parseString(content).getAsJsonObject();
        // 根据配置名称查找键值
        JsonArray jsonArray = object.get("testlogdata").getAsJsonArray();
        // 获取取首个元素
        JsonObject json = (JsonObject) jsonArray.get(0);
        // 映射为键值对
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    /**
     * 写入原始记录数据
     *
     * @param path       路径
     * @param reportCode 报告号
     * @param map        绑定数据列表
     * @return 是否成功
     * @throws JSONException 异常
     */
    public static boolean write(final String path, final String reportCode, final BindingDataMap map) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        for (BindingData bindingData : map.values()) {
            // 过滤域名为fileversion的值
            if (!"fileversion".equals(bindingData.getName())) {
                jsonObject.put(bindingData.getName(), bindingData.getValue());
            }
        }

        final JSONObject root = new JSONObject();
        root.put("testlogdata", jsonObject);

        return FileUtils.write(getFilename(path, reportCode), JsonUtils.fromObject(root));
    }

    /**
     * 填充数据
     *
     * @param path       路径
     * @param reportCode 报告号
     * @param map        绑定数据列表
     * @throws Exception 异常
     */
    public static void fill(final String path, final String reportCode, final BindingDataMap map) throws Exception {
        // 读取数据源
        Map<String, JsonElement> data = read(path, reportCode);
        if (map == null) {
            throw new FileNotFoundException();
        }

        // 遍历填充所有表单域
        for (BindingData bindingData : map.values()) {
            String value = JsonUtils.getValue(data, bindingData.getName());
            if (value != null) {
                bindingData.setValue(value);
            }
        }
    }
}

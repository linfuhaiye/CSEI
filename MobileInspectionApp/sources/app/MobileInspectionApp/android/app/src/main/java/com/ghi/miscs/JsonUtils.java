package com.ghi.miscs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 *
 * @author Alex
 */
public final class JsonUtils {
    /**
     * 获取元素
     *
     * @param map JSON散列表
     * @param key 键值
     * @return 元素
     */
    public static JsonElement getElement(final Map<String, JsonElement> map, final String key) {
        if ((map == null) || (!map.containsKey(key))) {
            return null;
        }

        return map.get(key);
    }

    /**
     * 获取值
     *
     * @param map JSON散列表
     * @param key 键值
     * @return 值
     */
    public static String getValue(final Map<String, JsonElement> map, final String key) {
        final JsonElement element = getElement(map, key);
        if (element == null) {
            return null;
        }

        return element.getAsString();
    }

    /**
     * 获取数组
     *
     * @param map JSON散列表
     * @param key 键值
     * @return 数组
     */
    public static JsonArray getJsonArray(final Map<String, JsonElement> map, final String key) {
        final JsonElement element = getElement(map, key);
        if (element == null) {
            return null;
        }

        return element.getAsJsonArray();
    }

    /**
     * 获取数组
     *
     * @param map  JSON散列表
     * @param key  键值
     * @param type 对象类型
     * @param <T>  类型
     * @return 数组
     */
    public static <T> List<T> getArray(final Map<String, JsonElement> map, final String key, final Type type) {
        JsonArray array = getJsonArray(map, key);
        if (array == null) {
            return null;
        }

        String json = fromObject(array);
        if (json == null) {
            return null;
        }

        return JsonUtils.toObject(json, type);
    }

    /**
     * JSON字符串转对象
     *
     * @param json  JSON字符串
     * @param clazz 类型
     * @param <T>   对象类型
     * @return 对象
     */
    public static <T> T toObject(final String json, final Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    /**
     * JSON字符串转对象
     *
     * @param json JSON字符串
     * @param type 类型
     * @param <T>  对象类型
     * @return 对象
     */
    public static <T> T toObject(final String json, final Type type) {
        return new Gson().fromJson(json, type);
    }

    /**
     * JSON对象转对象
     *
     * @param element JSON对象
     * @param clazz   类型
     * @param <T>     对象类型
     * @return 对象
     */
    public static <T> T toObject(final JsonElement element, final Class<T> clazz) {
        return toObject(fromObject(element), clazz);
    }

    /**
     * 对象转JSON
     *
     * @param object 对象
     * @return JSON字符串
     */
    public static String fromObject(final Object object) {
        return new Gson().toJson(object);
    }
}

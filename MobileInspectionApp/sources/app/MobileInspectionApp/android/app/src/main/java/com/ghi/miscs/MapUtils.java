package com.ghi.miscs;

import java.util.Map;

/**
 * 散列表工具
 *
 * @author etrit
 */
public final class MapUtils {
    /**
     * 设置值
     *
     * @param map      列表
     * @param key      键
     * @param runnable 设置值执行接口
     * @param <K>      键类型
     * @param <V>      值类型
     */
    public static <K, V> void setValue(final Map<K, V> map, final K key, final SetValueRunnable<V> runnable) {
        V v = map.get(key);
        if (v != null) {
            runnable.run(v);
        }
    }

    /**
     * 判断值相等
     *
     * @param map      列表
     * @param key      键
     * @param runnable 判断值相等执行接口
     * @param <K>      键类型
     * @return 是否相等
     */
    public static <K, V> boolean equals(final Map<K, V> map, final K key, final EqualsRunnable<V> runnable) {
        V v = map.get(key);
        return (v != null) && runnable.run(v);
    }

    /**
     * 设置值执行接口
     *
     * @param <V> 值类型
     */
    public interface SetValueRunnable<V> {
        /**
         * 执行
         *
         * @param value 值
         */
        void run(final V value);
    }

    /**
     * 判断值相等执行接口
     *
     * @param <V> 值类型
     */
    public interface EqualsRunnable<V> {
        /**
         * 执行
         *
         * @param value 值
         * @return 是否相等
         */
        boolean run(final V value);
    }
}

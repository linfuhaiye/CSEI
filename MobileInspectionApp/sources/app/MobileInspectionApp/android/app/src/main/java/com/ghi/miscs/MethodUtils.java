package com.ghi.miscs;

import android.view.View;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.UIManagerModule;

/**
 * 方法调用工具
 *
 * @author Alex
 */
public final class MethodUtils {
    /**
     * 执行组件方法
     *
     * @param context     上下文
     * @param componentId 组件索引
     * @param executor    执行器
     * @param <T>         组件类型
     */
    @SuppressWarnings("unchecked")
    public static <T> void invokeComponentMethod(final ReactApplicationContext context, final int componentId, final Executor<T> executor) {
        UIManagerModule uiManager = context.getNativeModule(UIManagerModule.class);
        uiManager.addUIBlock(nativeViewHierarchyManager -> {
            View view = nativeViewHierarchyManager.resolveView(componentId);
            executor.run((T) view);
        });
    }

    /**
     * 组件方法执行器
     *
     * @param <T> 组件类型
     * @author Alex
     */
    @FunctionalInterface
    public interface Executor<T> {
        /**
         * 执行
         *
         * @param t 组件对象
         */
        void run(T t);
    }

    /**
     * 对象处理执行器
     *
     * @param <T> 对象类型
     */
    @FunctionalInterface
    public interface Consumer<T> {
        /**
         * 处理
         *
         * @param t 对象
         */
        void accept(final T t);
    }

    /**
     * 预测
     *
     * @param <T> 值类型
     */
    @FunctionalInterface
    public interface Predicate<T> {
        /**
         * 测试
         *
         * @param t 值
         * @return 是否符合
         */
        boolean test(final T t);
    }
}

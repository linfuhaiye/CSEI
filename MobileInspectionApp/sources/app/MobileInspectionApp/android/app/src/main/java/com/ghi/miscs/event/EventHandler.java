package com.ghi.miscs.event;

import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

/**
 * 事件处理器
 *
 * @param <T> 类型
 * @author Alex
 */
public abstract class EventHandler<T extends EventListener> {
    /**
     * 监听器列表
     */
    private transient final List<T> listeners = new LinkedList<>();

    /**
     * 触发事件
     *
     * @param arguments 参数
     */
    public synchronized void invoke(final Object... arguments) {
        for (T listener : listeners) {
            onInvoke(listener, arguments);
        }
    }

    /**
     * 注册监听器
     *
     * @param listener 监听器
     */
    public synchronized void addListener(final T listener) {
        listeners.add(listener);
    }

    /**
     * 注销监听器
     *
     * @param listener 监听器
     */
    public synchronized void removeListener(final T listener) {
        listeners.remove(listener);
    }

    /**
     * 触发事件
     *
     * @param listener  监听器
     * @param arguments 参数
     */
    protected abstract void onInvoke(final T listener, final Object... arguments);
}

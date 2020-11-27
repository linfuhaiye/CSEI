package com.ghi.miscs.event;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * 消息发送器
 *
 * @author Alex
 */
public final class RnEventEmitter {
    /**
     * 提示事件
     */
    public static final String EVENT_TOAST = "EVENT_TOAST";

    /**
     * 发送事件
     *
     * @param context   上下文
     * @param eventName 事件名称
     * @param params    参数
     */
    public static void emit(ReactApplicationContext context, String eventName, Object params) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}

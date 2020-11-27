package com.ghi.modules.system;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * 系统模块
 *
 * @author Alex
 */
public final class RnSystemModule extends ReactContextBaseJavaModule {
    /**
     * 用户
     */
    public static String user;

    @NonNull
    @Override
    public String getName() {
        return "_System";
    }

    /**
     * 设置用户
     *
     * @param user    用户
     * @param promise 承诺
     */
    @ReactMethod
    public void setUser(final String user, final Promise promise) {
        RnSystemModule.user = user;
        promise.resolve(true);
    }
}

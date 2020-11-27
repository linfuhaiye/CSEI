package com.ghi.modules.encrypt;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.fjsei.sm.FtpUtils;
import org.fjsei.sm.Sm2Utils;
import org.fjsei.sm.Sm4Utils;

/**
 * 加解密模块
 *
 * @author Alex
 */
public final class RnEncryptModule extends ReactContextBaseJavaModule {
    @NonNull
    @Override
    public String getName() {
        return "Encrypt";
    }

    /**
     * SM2加密
     *
     * @param key     密钥
     * @param raw     数据
     * @param promise 承诺
     */
    @ReactMethod
    public void sm2Encrypt(final String key, final String raw, final Promise promise) {
        try {
            String result = Sm2Utils.encrypt(key, raw);
            promise.resolve(result);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(null);
        }
    }

    /**
     * SM2解密
     *
     * @param key     密钥
     * @param raw     数据
     * @param promise 承诺
     */
    @ReactMethod
    public void sm2Decrypt(final String key, final String raw, final Promise promise) {
        try {
            String result = Sm2Utils.decrypt(key, raw);
            promise.resolve(result);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(null);
        }
    }

    /**
     * SM4加密
     *
     * @param key     密钥
     * @param raw     数据
     * @param promise 承诺
     */
    @ReactMethod
    public void sm4Encrypt(final String key, final String raw, final Promise promise) {
        try {
            String result = Sm4Utils.encrypt(key, raw);
            promise.resolve(result);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(null);
        }
    }

    /**
     * SM4解密
     *
     * @param key     密钥
     * @param raw     数据
     * @param promise 承诺
     */
    @ReactMethod
    public void sm4Decrypt(final String key, final String raw, final Promise promise) {
        try {
            String result = Sm4Utils.decrypt(key, raw);
            promise.resolve(result);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(null);
        }
    }

    /**
     * 生成SM4密钥
     *
     * @param promise 承诺
     */
    @ReactMethod
    public void generateSm4Key(final Promise promise) {
        try {
            String result = Sm4Utils.generateKey();
            promise.resolve(result);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(null);
        }
    }

    /**
     * FTP相关信息解密
     *
     * @param raw     数据
     * @param promise 承诺
     */
    @ReactMethod
    public void ftpDecrypt(final String raw, final Promise promise) {
        try {
            String result = FtpUtils.decrypt(raw);
            promise.resolve(result);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(null);
        }
    }
}

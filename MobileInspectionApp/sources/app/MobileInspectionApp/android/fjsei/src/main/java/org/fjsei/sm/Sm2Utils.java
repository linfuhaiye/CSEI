package org.fjsei.sm;

import org.bouncycastle.crypto.engines.SM2Engine;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;

/**
 * SM2工具
 *
 * @author Alex
 */
public final class Sm2Utils {
    /**
     * 公钥前缀长度
     */
    private static final int PUBLIC_KEY_PREFIX = 1;

    /**
     * 密钥长度
     */
    private static final int KEY_LENGTH = 32;

    /**
     * 虚拟公钥
     */
    private static final String DUMMY_PUBLIC_KEY_X = "9EF573019D9A03B16B0BE44FC8A5B4E8E098F56034C97B312282DD0B4810AFC3";

    /**
     * 虚拟公钥
     */
    private static final String DUMMY_PUBLIC_KEY_Y = "CC759673ED0FC9B9DC7E6FA38F0E2B121E02654BF37EA6B63FAF2A0D6013EADF";

    /**
     * 虚拟私钥
     */
    private static final String DUMMY_PRIVATE_KEY = "FAB8BBE670FAE338C9E9382B9FB6485225C11A3ECB84C938F10F20A93B6215F0";

    /**
     * 加密
     *
     * @param publicKey 公钥(标识字节 + X + Y)
     * @param content   内容
     * @return 加密内容
     */
    public static String encrypt(final String publicKey, final String content) {
        if (publicKey == null || publicKey.length() != (PUBLIC_KEY_PREFIX + KEY_LENGTH + KEY_LENGTH) * 2) {
            return null;
        }

        SM2 sm2 = new SM2(DUMMY_PRIVATE_KEY, publicKey.substring(PUBLIC_KEY_PREFIX * 2, (PUBLIC_KEY_PREFIX + KEY_LENGTH) * 2), publicKey.substring((PUBLIC_KEY_PREFIX + KEY_LENGTH) * 2));
        sm2.setMode(SM2Engine.Mode.C1C2C3);
        return sm2.encryptBcd(content, KeyType.PublicKey);
    }

    /**
     * 解密
     *
     * @param privateKey 私钥
     * @param content    内容
     * @return 解密内容
     */
    public static String decrypt(final String privateKey, final String content) {
        if (privateKey == null || privateKey.length() != KEY_LENGTH * 2) {
            return null;
        }

        SM2 sm2 = new SM2(privateKey, DUMMY_PUBLIC_KEY_X, DUMMY_PUBLIC_KEY_Y);
        sm2.setMode(SM2Engine.Mode.C1C2C3);
        return StrUtil.utf8Str(sm2.decryptFromBcd(content, KeyType.PrivateKey));
    }
}

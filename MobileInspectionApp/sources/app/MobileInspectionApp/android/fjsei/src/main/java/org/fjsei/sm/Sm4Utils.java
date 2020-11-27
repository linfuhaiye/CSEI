package org.fjsei.sm;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * SM4工具
 *
 * @author Alex
 */
public final class Sm4Utils {
    /**
     * 加密
     *
     * @param key     密钥
     * @param content 内容
     * @return 加密内容
     */
    public static String encrypt(final String key, final String content) {
        SymmetricCrypto sm4 = SmUtil.sm4(HexUtil.decodeHex(key));
        return sm4.encryptBase64(content);
    }

    /**
     * 解密
     *
     * @param key     密钥
     * @param content 内容
     * @return 解密内容
     */
    public static String decrypt(final String key, final String content) {
        SymmetricCrypto sm4 = SmUtil.sm4(HexUtil.decodeHex(key));
        return sm4.decryptStr(content, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 生成密钥
     *
     * @return 密钥
     */
    public static String generateKey() {
        SymmetricCrypto sm4 = SmUtil.sm4();
        return HexUtil.encodeHexStr(sm4.getSecretKey().getEncoded());
    }
}
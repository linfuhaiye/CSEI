package org.fjsei.sm;

/**
 * FTP加解密工具
 *
 * @author etrit
 */
public final class FtpUtils {
    /**
     * 解密数据
     */
    private static final byte[] CRYPT = "FJInstituteSpecialEquipmentSupervision&Inspection".getBytes();

    /**
     * 解密
     *
     * @param content 内容
     * @return 解密内容
     */
    public static String decrypt(final String content) {
        byte[] source = content.getBytes();
        int length = source.length;

        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) ((int) source[i] ^ (int) CRYPT[i]);
        }

        return new String(result);
    }
}

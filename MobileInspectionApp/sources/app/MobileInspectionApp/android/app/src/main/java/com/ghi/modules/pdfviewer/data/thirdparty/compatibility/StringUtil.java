package com.ghi.modules.pdfviewer.data.thirdparty.compatibility;

import com.blankj.utilcode.util.StringUtils;

import java.util.Arrays;

/**
 * 字符串工具
 *
 * @author Alex
 */
public final class StringUtil {
    /**
     * 移除最后一个字节
     */
    public static String removeFromStart(String src, char lastDelete) {
        if (src != null && src.contains(String.valueOf(lastDelete))) {
            char[] arr = src.toCharArray();
            int position = 0;
            int len = arr.length;
            for (int i = len - 1; i >= 0; i--) {
                if (arr[i] == lastDelete) {
                    position = i;
                    break;
                }
            }
            char[] newarr = Arrays.copyOfRange(arr, position + 1, arr.length);
            return String.valueOf(newarr);
        } else {
            return src;
        }
    }

    /**
     * 移除最后一个字节
     */
    public static String removeFromEnd(String src, char lastDelete) {
        char[] arr = src.toCharArray();
        int deleteNum = 0;
        int len = arr.length;
        for (int i = len - 1; i >= 0; i--) {
            if (arr[i] != lastDelete) {
                deleteNum++;
            } else {
                deleteNum++;
                break;
            }
        }
        char[] newarr = Arrays.copyOf(arr, arr.length - deleteNum);
        return String.valueOf(newarr);
    }

    public static boolean isXMwith(String beg, String end, String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }

        String regex = "^" + beg + "[0-9,.]+" + end + "$";
        return str.matches(regex);
    }

    public static boolean isChinese(String str) {
        String regex = "^[\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }
}

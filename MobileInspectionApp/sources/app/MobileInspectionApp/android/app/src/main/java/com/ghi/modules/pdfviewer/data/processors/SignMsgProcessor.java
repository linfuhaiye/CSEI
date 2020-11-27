package com.ghi.modules.pdfviewer.data.processors;

import com.ghi.miscs.FileUtils;

import java.io.File;

/**
 * 签名信息处理器
 *
 * @author Alex
 */
public class SignMsgProcessor {
    /**
     * 文件名
     */
    private static final String FILENAME = "/signmsg.ses";

    /**
     * 获取签名信息路径
     *
     * @param path       父路径
     * @param reportCode 报告号
     * @return 路径
     */
    public static String getFilename(final String path, final String reportCode) {
        return path + File.separator + reportCode + FILENAME;
    }

    /**
     * 读取签名信息
     *
     * @param path       路径
     * @param reportCode 报告号
     * @return 签名信息
     */
    public static String read(final String path, final String reportCode) {
        return FileUtils.read(getFilename(path, reportCode));
    }

    /**
     * 写入签名信息
     *
     * @param path       路径
     * @param reportCode 报告号
     * @param signMsg    签名信息
     * @return 是否成功
     */
    public static boolean write(final String path, final String reportCode, final String signMsg) {
        return FileUtils.write(getFilename(path, reportCode), signMsg);
    }
}

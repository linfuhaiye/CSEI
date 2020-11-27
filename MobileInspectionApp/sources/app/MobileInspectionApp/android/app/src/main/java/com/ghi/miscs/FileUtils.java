package com.ghi.miscs;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * 文件工具
 *
 * @author Alex
 */
public final class FileUtils {
    /**
     * 读取文件
     *
     * @param path 路径
     * @return 文件内容
     */
    public static String read(final String path) {
        return FileIOUtils.readFile2String(path, StandardCharsets.UTF_8.name());
    }

    /**
     * 写入文件
     *
     * @param path    路径
     * @param content 文件内容
     * @return 是否成功
     */
    public static boolean write(final String path, final String content) {
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8.name())) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            LogUtils.e(e);
            return false;
        }
    }
}

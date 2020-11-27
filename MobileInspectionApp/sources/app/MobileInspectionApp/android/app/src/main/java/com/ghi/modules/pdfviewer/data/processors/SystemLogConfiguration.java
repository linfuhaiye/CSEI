package com.ghi.modules.pdfviewer.data.processors;

import com.ghi.miscs.FileUtils;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.ghi.modules.pdfviewer.data.entities.SystemLog;

import java.io.File;

/**
 * 系统文件配置
 *
 * @author Alex
 */
public final class SystemLogConfiguration extends Processor {
    /**
     * 文件名
     */
    private static final String FILENAME = "/systemlog.ses";

    /**
     * 构造函数
     *
     * @param context   上下文
     * @param arguments 参数
     */
    public SystemLogConfiguration(Context context, Object... arguments) {
        super(context, arguments);
    }

    /**
     * 获取系统文件配置路径
     *
     * @param path       父路径
     * @param reportCode 报告号
     * @return 路径
     */
    public static String getFilename(final String path, final String reportCode) {
        return path + File.separator + reportCode + FILENAME;
    }

    /**
     * 读取系统文件配置
     *
     * @param path       路径
     * @param reportCode 报告号
     * @return 配置
     */
    public static SystemLog read(final String path, final String reportCode) {
        // 读取系统文件配置文件
        String content = FileUtils.read(getFilename(path, reportCode));
        if (content == null) {
            return null;
        }

        return JsonUtils.toObject(content, SystemLog.class);
    }

    /**
     * 写入系统文件配置
     *
     * @param path       路径
     * @param reportCode 报告号
     * @param systemLog  配置
     * @return 是否成功
     */
    public static boolean write(final String path, final String reportCode, final SystemLog systemLog) {
        return FileUtils.write(getFilename(path, reportCode), JsonUtils.fromObject(systemLog));
    }

    @Override
    public void onLoadDocumentComplete(final BindingDataMap map) {
        final SystemLog systemLog = context.getSystemLog();
        if (context.isFirstOpenWithEmptyFile() || context.isFirstOpenWithFileCopied() || context.isFirstOpenWithDataCopied() || context.isReinspection()) {
            // 首次打开、文件复制打开、数据复制打开、复检首次打开
            // 设置为非首次打开
            systemLog.setFirstopen(1);
        }
    }
}

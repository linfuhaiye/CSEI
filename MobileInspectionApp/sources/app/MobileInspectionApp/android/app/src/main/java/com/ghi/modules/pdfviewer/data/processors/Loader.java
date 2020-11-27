package com.ghi.modules.pdfviewer.data.processors;

import com.ghi.modules.pdfviewer.PdfViewer;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.entities.ControlInformation;
import com.ghi.modules.pdfviewer.data.entities.ModifyLog;
import com.ghi.modules.pdfviewer.data.entities.SystemLog;
import com.ghi.modules.pdfviewer.data.entities.TaskInformation;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * PDF文档加载器
 *
 * @author Alex
 */
@Getter
@Setter
public final class Loader {
    /**
     * 密码
     */
    public static final String PASSWORD = "sesadmin";

    /**
     * 文件名
     */
    private static final String FILENAME = "/testlog.pdf";

    /**
     * 阅读器
     */
    private final PdfViewer viewer;

    /**
     * 数据处理器
     */
    private final List<Processor> processors = new LinkedList<>();

    /**
     * 上下文
     */
    private Context context;

    /**
     * 构造函数
     *
     * @param viewer 阅读器
     */
    public Loader(final PdfViewer viewer) {
        this.viewer = viewer;
    }

    /**
     * 获取PDF文档路径
     *
     * @param path       父路径
     * @param reportCode 报告号
     * @return 路径
     */
    public static String getPdfFilename(final String path, final String reportCode) {
        return path + File.separator + reportCode + FILENAME;
    }

    /**
     * 加载PDF文档
     *
     * @param path            路径
     * @param taskInformation 任务信息
     * @throws Exception 异常
     */
    public void loadDocument(final String path, final TaskInformation taskInformation) throws Exception {
        if (context != null) {
            context.close();
            context = null;
        }

        final SystemLog systemLog = SystemLogConfiguration.read(path, taskInformation.getReportCode());
        final List<Map<String, List<ModifyLog>>> changeLog = ModifyLogProcessor.read(path, taskInformation.getReportCode());
        final Map<String, String> information = SourceProcessor.read(path, taskInformation.getReportCode());
        final ControlInformation controlInformation = SourceProcessor.readControlInformation(path, taskInformation.getReportCode());
        final Map<String, JsonElement> configurations = TestLogConfigurationProcessor.read(path, taskInformation.getReportCode(), taskInformation.getEqpType());

        context = new Context(viewer, path, taskInformation, systemLog, changeLog, information, controlInformation, configurations);
        processors.add(new SourceProcessor(context, path, information, configurations));
        processors.add(new TestLogConfigurationProcessor(context, path, configurations));
        processors.add(new SystemLogConfiguration(context, path));
        processors.add(new MiscProcessor.Cl1FieldProcessor(context, path));
        processors.add(new MiscProcessor.Cl2FieldProcessor(context, path));
        processors.add(new MiscProcessor.SetEqpCodeFieldProcessor(context, path));
        processors.add(new MiscProcessor.DateFieldProcessor(context, path));
        processors.add(new MiscProcessor.FailedFieldProcessor(context, path));

        for (Processor processor : processors) {
            processor.onInitialize(context.getMap());
        }

        for (Processor processor : processors) {
            processor.onLoadDocument(context.getMap(), context.getFields());
        }

        // TODO: 结论清空
        // TODO: 报告号字段重新初始化
        // TODO: 清空签字信息、签字日期、结论
        // TODO: 固化到PDF文档中

        // 注册数据改变监听器
        context.registerDataChangedListener();
    }

    /**
     * 获取域
     *
     * @param name 域名
     * @return 域
     */
    public BindingData getField(final String name) {
        return context.getMap().get(name);
    }

    /**
     * 初始化不合格域
     */
    private void initializeFailedFields() {
    }
}

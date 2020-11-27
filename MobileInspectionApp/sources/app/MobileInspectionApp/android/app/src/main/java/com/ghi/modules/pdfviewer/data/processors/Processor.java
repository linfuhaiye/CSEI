package com.ghi.modules.pdfviewer.data.processors;

import com.ghi.modules.pdfviewer.data.core.BindingDataMap;

import java.util.Map;

/**
 * 数据处理器
 *
 * @author etrit
 */
public abstract class Processor {
    /**
     * 上下文
     */
    protected Context context;

    /**
     * 构造函数
     *
     * @param context   上下文
     * @param arguments 参数
     */
    public Processor(final Context context, final Object... arguments) {
        this.context = context;
    }

    /**
     * 构造函数
     */
    private Processor() {
    }

    /**
     * 初始化
     *
     * @param map 绑定数据列表
     */
    public void onInitialize(final BindingDataMap map) {
    }

    /**
     * 加载文档处理函数
     *
     * @param map    绑定数据列表
     * @param fields 域列表
     */
    public void onLoadDocument(final BindingDataMap map, final Map<String, String> fields) {
    }

    /**
     * 加载文档完成处理函数
     *
     * @param map 绑定数据列表
     */
    public void onLoadDocumentComplete(final BindingDataMap map) {
    }
}

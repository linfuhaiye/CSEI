package com.ghi.modules.pdfviewer;

import android.app.Activity;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.fxcrt.RectF;
import com.foxit.sdk.common.fxcrt.RectFArray;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.TextPage;
import com.foxit.sdk.pdf.interform.Field;
import com.foxit.sdk.pdf.interform.Form;
import com.foxit.uiextensions.UIExtensionsManager;
import com.ghi.miscs.PdfUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.entities.TaskInformation;
import com.ghi.modules.pdfviewer.data.processors.Loader;

import lombok.Getter;

/**
 * PDF阅读器
 *
 * @author Alex
 */
@Getter
public final class PdfViewer {
    /**
     * 文件名
     */
    private static final String FILENAME = "/testlog.pdf";

    /**
     * 上下文
     */
    private final ReactApplicationContext reactContext;

    /**
     * 视图
     */
    private final Activity activity;

    /**
     * 界面扩展管理器
     */
    private final UIExtensionsManager uiExtensionsManager;

    /**
     * PDF文档加载器
     */
    private final Loader loader;

    /**
     * 构造函数
     *
     * @param reactContext        上下文
     * @param activity            视图
     * @param uiExtensionsManager 界面扩展管理器
     */
    public PdfViewer(final ReactApplicationContext reactContext, final Activity activity, final UIExtensionsManager uiExtensionsManager) {
        this.reactContext = reactContext;
        this.activity = activity;
        this.uiExtensionsManager = uiExtensionsManager;
        this.loader = new Loader(this);
    }

    /**
     * 打开PDF文档
     *
     * @param path 路径
     */
    public void openDocument(final String path) {
        uiExtensionsManager.openDocument(path, null);
    }

    /**
     * 打开PDF文档
     *
     * @param path            路径
     * @param taskInformation 任务信息
     */
    public void openDocument(final String path, final TaskInformation taskInformation) {
        uiExtensionsManager.getPDFViewCtrl().registerDocEventListener(new PDFViewCtrl.IDocEventListener() {
            @Override
            public void onDocWillOpen() {
            }

            @Override
            public void onDocOpened(PDFDoc pdfDoc, int i) {
                if (pdfDoc != null) {
                    try {
                        loader.loadDocument(path, taskInformation);
                    } catch (Exception e) {
                        ToastUtils.showLong("打开PDF文档失败!");
                        LogUtils.e(e);
                    }
                }
            }

            @Override
            public void onDocWillClose(PDFDoc pdfDoc) {
            }

            @Override
            public void onDocClosed(PDFDoc pdfDoc, int i) {
            }

            @Override
            public void onDocWillSave(PDFDoc pdfDoc) {
            }

            @Override
            public void onDocSaved(PDFDoc pdfDoc, int i) {
            }
        });

        uiExtensionsManager.openDocument(Loader.getPdfFilename(path, taskInformation.getReportCode()), ConvertUtils.string2Bytes(Loader.PASSWORD));
    }

    /**
     * 获取域内容
     *
     * @param name     域名
     * @param offsetX1 横向偏移
     * @param offsetX2 横向偏移
     * @param offsetY1 纵向偏移
     * @param offsetY2 横向偏移
     * @return 域内容
     */
    public String getFieldValue(final String name, final int offsetX1, int offsetX2, final int offsetY1, final int offsetY2) throws PDFException {
        final PDFDoc document = uiExtensionsManager.getPDFViewCtrl().getDoc();
        if (!document.hasForm()) {
            return null;
        }

        final Form form = new Form(document);
        final Field field = form.getField(0, name);
        if (field == null) {
            return null;
        }

        // Get rectangle, in PDF coordinate system.
        final PDFPage page = PdfUtils.getPage(field);
        final RectF rect = field.getControl(page, 0).getWidget().getRect();
        final TextPage textPage = new TextPage(page, TextPage.e_ParseTextNormal);
        if (textPage.isEmpty()) {
            return null;
        }

        // Get text position
        final RectF textRect = new RectF(rect.getLeft() + offsetX1, rect.getBottom() + offsetY2, rect.getLeft() + offsetX2, rect.getTop() + offsetY1);

        // Get text rectangle array
        RectFArray array = textPage.getTextRectArrayByRect(textRect);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.getSize(); ++i) {
            sb.append(textPage.getTextInRect(array.getAt(i)));
        }

        return sb.toString();
    }

    /**
     * 获取域
     *
     * @param name 域名
     * @return 域
     */
    public BindingData getField(final String name) {
        return loader.getField(name);
    }

    /**
     * 跳转到指定页面
     *
     * @param page 页面索引
     */
    public void gotoPage(final int page) {
        uiExtensionsManager.getPDFViewCtrl().gotoPage(page);
    }
}

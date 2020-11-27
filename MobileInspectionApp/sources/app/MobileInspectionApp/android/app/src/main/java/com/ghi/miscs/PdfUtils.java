package com.ghi.miscs;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Progressive;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.interform.ChoiceOption;
import com.foxit.sdk.pdf.interform.ChoiceOptionArray;
import com.foxit.sdk.pdf.interform.Field;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF工具类
 *
 * @author Alex
 */
public final class PdfUtils {
    /**
     * 是否为空表单域
     *
     * @param field 表单域
     * @return 是否为空
     */
    public static boolean isEmptyField(final Field field) {
        try {
            return StringUtils.isTrimEmpty(field.getValue());
        } catch (PDFException e) {
            LogUtils.e(e);
            return true;
        }
    }

    /**
     * 设置表单域为默认值
     *
     * @param field 表单域
     */
    public static void setDefaultValue(final Field field) {
        try {
            if (!StringUtils.isTrimEmpty(field.getDefaultValue())) {
                field.setValue(field.getDefaultValue());
            }
        } catch (PDFException e) {
            LogUtils.e(e);
        }
    }

    /**
     * 获取下拉菜单表单域值
     *
     * @param field 表单域
     * @return 内容数组
     * @throws PDFException 异常
     */
    public static List<String> getComboBoxOptions(final Field field) throws PDFException {
        List<String> list = new ArrayList<>((int) field.getOptions().getSize());
        ChoiceOptionArray options = field.getOptions();
        for (long i = 0; i < options.getSize(); i++) {
            list.add(options.getAt(i).getOption_value());
        }

        return list;
    }

    /**
     * 设置下拉菜单表单域
     *
     * @param field 表单域
     * @param array 内容数组
     */
    public static void setComboBoxField(final Field field, final List<String> array) {
        try {
            ChoiceOptionArray options = new ChoiceOptionArray();
            for (int i = 0; i < array.size(); i++) {
                String item = array.get(i);
                options.add(new ChoiceOption(item, item, false, false));
            }

            field.setOptions(options);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 设置下拉菜单表单域
     *
     * @param data  绑定数据
     * @param array 内容数组
     */
    public static void setComboBoxOptions(final BindingData data, final JsonArray array) {
        List<String> list = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            String item = array.get(i).getAsString();
            list.add(item);
        }

        setComboBoxOptions(data, list);
    }

    /**
     * 设置下拉菜单表单域
     *
     * @param data  绑定数据
     * @param array 内容数组
     */
    public static void setComboBoxOptions(final BindingData data, final List<String> array) {
        try {
            data.setOptions(array);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 添加书签
     *
     * @param document  PDF文档
     * @param title     标题
     * @param pageIndex 页码
     */
    public static void addBookmark(final PDFDoc document, final String title, final int pageIndex) {
        try {
            document.insertReadingBookmark(document.getReadingBookmarkCount(), title, pageIndex);
        } catch (PDFException e) {
            LogUtils.e(e);
        }
    }

    /**
     * 获取域所在页面
     *
     * @param field 域
     * @return 页面
     */
    public static PDFPage getPage(final Field field) {
        if (field == null) {
            return null;
        }

        try {
            PDFPage page = field.getControl(0).getWidget().getPage();
            // 解析页面
            if (!page.isParsed()) {
                Progressive parse = page.startParse(PDFPage.e_ParsePageNormal, null, false);
                int state = Progressive.e_ToBeContinued;
                while (state == Progressive.e_ToBeContinued) {
                    state = parse.resume();
                }
            }

            return page;
        } catch (PDFException e) {
            LogUtils.e(e);
            return null;
        }
    }

    /**
     * 跳转到指定页面
     *
     * @param ctrl  视图控制器
     * @param field 域
     */
    public static void gotoPage(final PDFViewCtrl ctrl, final BindingData field) {
        if (field == null) {
            return;
        }

        PDFPage page = getPage(field.getField());
        if (page != null) {
            gotoPage(ctrl, page);
        }
    }

    /**
     * 跳转到指定页面
     *
     * @param ctrl 视图控制器
     * @param page 页面
     */
    public static void gotoPage(final PDFViewCtrl ctrl, final PDFPage page) {
        try {
            ctrl.gotoPage(page.getIndex());
        } catch (PDFException e) {
            LogUtils.e(e);
        }
    }
}

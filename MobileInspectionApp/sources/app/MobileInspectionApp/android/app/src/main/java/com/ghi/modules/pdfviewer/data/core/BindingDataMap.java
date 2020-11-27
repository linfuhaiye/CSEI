package com.ghi.modules.pdfviewer.data.core;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.foxit.sdk.PDFException;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.sdk.pdf.interform.Field;
import com.foxit.sdk.pdf.interform.Form;
import com.foxit.uiextensions.Module;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.form.FormFillerModule;
import com.foxit.uiextensions.annots.form.MyFormFillerAnnotHandler;
import com.ghi.miscs.MapUtils;
import com.ghi.miscs.MethodUtils;
import com.ghi.miscs.PdfUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 双向绑定数据集合
 *
 * @author Alex
 */
public class BindingDataMap extends ConcurrentHashMap<String, BindingData> {
    /**
     * 创建双向绑定数据集合
     *
     * @param uiExtensionsManager 界面扩展管理器
     * @return 双向绑定数据集合
     * @throws PDFException 异常
     */
    public static BindingDataMap create(final UIExtensionsManager uiExtensionsManager) throws PDFException {
        BindingDataMap map = new BindingDataMap();
        PDFDoc document = uiExtensionsManager.getPDFViewCtrl().getDoc();

        // 判断文档中是否存在表单
        if (!document.hasForm()) {
            return map;
        }

        // 抽取表单域构造绑定数据列表
        Form form = new Form(document);
        int count = form.getFieldCount(null);
        for (int i = 0; i < count; i++) {
            // 创建双向绑定数据
            Field field = form.getField(i, null);
            map.put(field.getName(), new BindingData(field.getName(), field.getAlternateName(), field.getValue(), PdfUtils.getComboBoxOptions(field), form, field, field.getType(), field.getFlags()));
        }

        // 双向绑定
        FormFillerModule filler = (FormFillerModule) uiExtensionsManager.getModuleByName(Module.MODULE_NAME_FORMFILLER);
        MyFormFillerAnnotHandler handler = (MyFormFillerAnnotHandler) filler.getAnnotHandler();
        handler.setFormEvent(new MyFormFillerAnnotHandler.IFormEvent() {
            @Override
            public void onFieldChanged(final Field field, final Object newValue) {
                try {
                    // 设置双向绑定数据内容
                    BindingData bindingData = map.get(field.getName());
                    if (bindingData != null) {
                        LogUtils.d("sync field: " + field.getName() + ", " + newValue);
                        bindingData.setOneWay(true);
                        bindingData.setValue(newValue);
                        bindingData.setOneWay(false);
                    }
                } catch (PDFException e) {
                    LogUtils.e(e);
                }
            }
        });

        return map;
    }

    /**
     * 获取值
     *
     * @param key 键值
     * @param <T> 类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(final String key) {
        final BindingData data = get(key);
        if (data == null) {
            return null;
        }

        return (T) data.getValue();
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     */
    public void setValue(final String key, final Object value) {
        if (StringUtils.isEmpty(key)) {
            return;
        }

        MapUtils.setValue(this, key, (bindingData) -> bindingData.setValue(value));
    }

    /**
     * 判断值相等
     *
     * @param key   键
     * @param value 值
     * @return 是否相等l
     */
    public boolean equals(final String key, final Object value) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }

        return MapUtils.equals(this, key, (bindingData) -> bindingData.getValue().equals(value));
    }

    /**
     * 遍历列表
     *
     * @param action 动作
     */
    public void foreach(MethodUtils.Consumer<BindingData> action) {
        for (BindingData data : values()) {
            action.accept(data);
        }
    }

    /**
     * 过滤列表
     *
     * @param predicate 过滤器
     * @return 列表
     */
    public List<BindingData> filter(final MethodUtils.Predicate<BindingData> predicate) {
        List<BindingData> result = new ArrayList<>();
        for (BindingData data : values()) {
            if (predicate.test(data)) {
                result.add(data);
            }
        }

        return result;
    }
}

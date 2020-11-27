package com.ghi.modules.pdfviewer.data.core;

import com.blankj.utilcode.util.LogUtils;
import com.foxit.sdk.pdf.interform.Field;
import com.foxit.sdk.pdf.interform.Form;
import com.ghi.miscs.PdfUtils;
import com.ghi.miscs.event.EventHandler;

import java.util.EventListener;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 双向绑定数据
 *
 * @author Alex
 */
@Getter
@Setter
@NoArgsConstructor
public final class BindingData extends EventHandler<BindingData.BindingDataEventListener> {
    /**
     * 名称
     */
    private String name;

    /**
     * 别名
     */
    private String alternateName;

    /**
     * 描述
     */
    private String description;

    /**
     * 值
     */
    private Object value;

    /**
     * 选项
     */
    private Object options;

    /**
     * 表单
     */
    private transient Form form;

    /**
     * 域
     */
    private transient Field field;

    /**
     * 类型, 参考com.foxit.sdk.pdf.interform.Field类型定义
     */
    private int type;

    /**
     * 标志, 参考com.foxit.sdk.pdf.interform.Field类型定义
     */
    private int flags;

    /**
     * 双向绑定数据锁
     */
    private transient boolean lock;

    /**
     * 单向绑定
     */
    private transient boolean oneWay;

    /**
     * 构造函数
     *
     * @param name          名称
     * @param alternateName 别名
     * @param value         值
     * @param options       选项
     * @param form          表单
     * @param field         域
     * @param type          类型
     * @param flags         标志
     */
    public BindingData(String name, String alternateName, String value, List<String> options, Form form, Field field, int type, int flags) {
        super();
        this.name = name;
        this.alternateName = alternateName;
        this.value = value;
        this.options = options;
        this.form = form;
        this.field = field;
        this.type = type;
        this.flags = flags;
    }

    /**
     * 设置别名
     *
     * @param value 别名
     */
    public void setAlternateName(final String value) {
        try {
            field.setAlternateName(value);
        } catch (Exception e) {
            LogUtils.e("BindingData setAlternateName fail: " + name);
            LogUtils.e(e);
        }
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(final Object value) {
        Object oldValue = this.value;
        this.value = value;
        // 双向绑定
        try {
            // 填充可能在JNI发生异常
            if (!oneWay) {
                field.setValue((String) value);
            }
            // 触发数据改变事件
            invoke(this, oldValue, value);
        } catch (Exception e) {
            LogUtils.e("BindingData setValue fail: " + name);
            LogUtils.e(e);
        }
    }

    /**
     * 设置选项
     *
     * @param options 选项
     */
    @SuppressWarnings("unchecked")
    public void setOptions(final Object options) {
        this.options = options;
        // 双向绑定
        try {
            // 填充可能在JNI发生异常
            PdfUtils.setComboBoxField(field, (List<String>) options);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    public synchronized void invoke(final Object... arguments) {
        // 防止递归触发事件
        if (lock) {
            return;
        }

        lock = true;
        super.invoke(arguments);
        lock = false;
    }

    @Override
    protected void onInvoke(final BindingDataEventListener listener, final Object... arguments) {
        listener.onChanged((BindingData) arguments[0], arguments[1], arguments[2]);
    }

    /**
     * 绑定数据改变事件监听器
     */
    public interface BindingDataEventListener extends EventListener {
        /**
         * 数据改变事件处理函数
         *
         * @param bindingData 绑定数据
         * @param oldValue    旧数据
         * @param newValue    新数据
         */
        void onChanged(final BindingData bindingData, final Object oldValue, final Object newValue);
    }
}
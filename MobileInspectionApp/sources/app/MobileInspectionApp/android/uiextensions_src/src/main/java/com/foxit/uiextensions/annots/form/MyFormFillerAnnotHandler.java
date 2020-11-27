package com.foxit.uiextensions.annots.form;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.pdf.annots.Annot;
import com.foxit.sdk.pdf.annots.Widget;
import com.foxit.sdk.pdf.interform.ChoiceOptionArray;
import com.foxit.sdk.pdf.interform.Field;
import com.foxit.uiextensions.utils.thread.AppThreadManager;

import java.util.Calendar;

/**
 * 表单填充处理器
 *
 * @author Alex
 */
public class MyFormFillerAnnotHandler extends FormFillerAnnotHandler {
    /**
     * 表单事件接口
     */
    private IFormEvent formEvent;

    /**
     * 构造函数
     *
     * @param context          上下文
     * @param parent           父组件
     * @param pdfViewCtrl      视图控制器
     * @param formFillerModule 模块
     */
    public MyFormFillerAnnotHandler(Context context, ViewGroup parent, PDFViewCtrl pdfViewCtrl, FormFillerModule formFillerModule) {
        super(context, parent, pdfViewCtrl, formFillerModule);
    }

    /**
     * 获取下拉菜单表单域值
     *
     * @param field 表单域
     * @return 内容数组
     * @throws PDFException 异常
     */
    public static String[] getComboBoxOptions(final Field field) throws PDFException {
        final int length = (int) field.getOptions().getSize();
        final String[] list = new String[length + 1];
        ChoiceOptionArray options = field.getOptions();
        for (int i = 0; i < options.getSize(); i++) {
            list[i] = options.getAt(i).getOption_value();
        }
        list[length] = "空";

        return list;
    }

    /**
     * 设置表单事件接口
     *
     * @param formEvent 表单事件接口
     */
    public void setFormEvent(IFormEvent formEvent) {
        this.formEvent = formEvent;
    }

    /**
     * 导航到上一个域
     */
    public void preNavigation() {
        AppThreadManager.getInstance().startThread(preNavigation);
    }

    /**
     * 导航到下一个域
     */
    public void nextNavigation() {
        AppThreadManager.getInstance().startThread(nextNavigation);
    }

    @Override
    public void onAnnotDeselected(Annot annot, boolean needInvalid) {
        boolean isModify = mIsModify;
        super.onAnnotDeselected(annot, needInvalid);

        try {
            if ((formEvent != null) && isModify) {
                Field field = ((Widget) annot).getField();
                onFieldChanged(field, field.getValue());
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFieldChanged(final Field field, final Object newValue) {
        super.onFieldChanged(field, newValue);
        formEvent.onFieldChanged(field, newValue);
    }

    @Override
    protected boolean shouldShowInputSoft(Annot annot) {
        if (annot == null) {
            return false;
        }

        if (!(annot instanceof Widget)) {
            return false;
        }

        final int type = FormFillerUtil.getAnnotFieldType(annot);
        try {
            if (type == Field.e_TypeTextField) {
                // 日期选择对话框不显示输入法
                return !((Widget) annot).getField().getName().toLowerCase().contains("date");
            } else if (type == Field.e_TypeComboBox) {
                // 下拉选择框不显示输入法
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected boolean shouldShowEditor(final Annot annot) {
        if (!(annot instanceof Widget)) {
            return false;
        }

        final int type = FormFillerUtil.getAnnotFieldType(annot);
        try {
            final Field field = ((Widget) annot).getField();
            if (type == Field.e_TypeTextField) {
                // 日期选择对话框不显示输入法
                if (field.getName().toLowerCase().contains("date")) {
                    getDatePickerDialog(field, new IFormEvent() {
                        @Override
                        public void onFieldChanged(Field field, Object newValue) {
                            try {
                                field.setValue(newValue.toString());
                                MyFormFillerAnnotHandler.this.onFieldChanged(field, field.getValue());
                            } catch (PDFException e) {
                                e.printStackTrace();
                            }
                        }
                    }).show();
                    return true;
                }
            } else if (type == Field.e_TypeComboBox) {
                // 下拉选择框不显示输入法
                getDropdown(field, new IFormEvent() {
                    @Override
                    public void onFieldChanged(Field field, Object newValue) {
                        try {
                            field.setValue(newValue.toString());
                            MyFormFillerAnnotHandler.this.onFieldChanged(field, field.getValue());
                        } catch (PDFException e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取日期选择对话框
     *
     * @param field 域
     * @param event 表单事件处理函数
     * @return 对话框
     */
    private DatePickerDialog getDatePickerDialog(final Field field, final IFormEvent event) {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog dialog = new DatePickerDialog(mContext, null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        final DatePicker datePicker = dialog.getDatePicker();

        dialog.setTitle("请选择日期");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final int year = datePicker.getYear();
                final int month = datePicker.getMonth();
                final int day = datePicker.getDayOfMonth();
                event.onFieldChanged(field, year + "-" + (month + 1) + "-" + day);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "/", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                event.onFieldChanged(field, "/");
            }
        });

        return dialog;
    }

    /**
     * 获取下拉选择框
     *
     * @param field 域
     * @param event 表单事件处理函数
     * @return 对话框
     * @throws PDFException 异常
     */
    private AlertDialog.Builder getDropdown(final Field field, final IFormEvent event) throws PDFException {
        final String[] options = getComboBoxOptions(field);

        // 获取选中项目
        int selected = 0;
        for (; selected < options.length - 1; ++selected) {
            if (options[selected].equals(field.getValue())) {
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setSingleChoiceItems(options, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String value = which == options.length - 1 ? "" : options[which];
                event.onFieldChanged(field, value);
                dialog.cancel();
            }
        });

        return builder;
    }

    /**
     * 表单事件接口
     */
    public interface IFormEvent {
        /**
         * 域内容改变事件
         *
         * @param field    域
         * @param newValue 新值
         */
        void onFieldChanged(Field field, Object newValue);
    }
}

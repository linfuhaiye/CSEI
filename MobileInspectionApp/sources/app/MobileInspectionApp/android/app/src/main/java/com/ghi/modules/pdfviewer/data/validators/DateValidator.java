package com.ghi.modules.pdfviewer.data.validators;

import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.processors.Context;
import com.ghi.modules.pdfviewer.data.thirdparty.compatibility.StringUtil;

/**
 * 日期表单域校验器
 *
 * @author Alex
 */
public final class DateValidator extends Validator {
    @Override
    public boolean validate(final Context context) {
        for (BindingData bindingData : context.getMap().values()) {
            if (bindingData.getValue() == null && StringUtil.isXMwith("DATE_P", "", bindingData.getName())) {
                setLastError(bindingData.getName(), "日期必填不能为空");
                return false;
            }
        }

        return true;
    }
}

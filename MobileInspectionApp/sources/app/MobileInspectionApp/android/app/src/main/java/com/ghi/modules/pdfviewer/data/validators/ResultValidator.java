package com.ghi.modules.pdfviewer.data.validators;

import com.blankj.utilcode.util.StringUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.ghi.modules.pdfviewer.data.processors.Context;

/**
 * 观测数据及测量结果记录->结果判定校验校验器
 *
 * @author Alex
 */
public final class ResultValidator extends Validator {
    private static final String[] TEMP = {"AIR_TEMP", "V_FLOAT"};

    @Override
    public boolean validate(final Context context) {
        final BindingDataMap map = context.getMap();
        for (String temp : TEMP) {
            final BindingData formField = map.get(temp);
            if (formField != null) {
                String value = formField.getValue() == null ? "" : formField.getValue().toString();
                if (StringUtils.isEmpty(value.trim())) {
                    setLastError(formField.getName(), "现场检验条件的确认结果不符合要求");
                    return false;
                }
            }
        }

        return true;
    }
}

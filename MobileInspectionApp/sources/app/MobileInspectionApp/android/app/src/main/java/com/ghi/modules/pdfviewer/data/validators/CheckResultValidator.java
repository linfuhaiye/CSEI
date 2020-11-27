package com.ghi.modules.pdfviewer.data.validators;

import com.blankj.utilcode.util.StringUtils;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.FieldNames;
import com.ghi.modules.pdfviewer.data.entities.CheckResult;
import com.ghi.modules.pdfviewer.data.processors.Context;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 检验结果校验器
 *
 * @author Alex
 */
public final class CheckResultValidator extends Validator {
    @Override
    public boolean validate(final Context context) {
        final List<CheckResult> checkResults = JsonUtils.getArray(context.getConfigurations(), FieldNames.CHECK_RESULT, new TypeToken<ArrayList<CheckResult>>() {
        }.getType());
        if (checkResults == null) {
            return true;
        }

        for (CheckResult result : checkResults) {
            if (StringUtils.isEmpty(result.getCheckItem())) {
                continue;
            }

            final String[] names = result.getCheckItem().split(",");
            final String[] values = result.getCheckValue().split(",");

            for (int i = 0; i < names.length; ++i) {
                final BindingData field = context.getMap().get(names[i]);
                if (field == null) {
                    continue;
                }

                final String fieldValue = field.getValue() == null ? "" : field.getValue().toString().trim();
                if (!StringUtils.isEmpty(fieldValue)) {
                    if (values[i].contains("!")) {
                        final String select = values[i].substring(1);
                        if (select.equals(fieldValue)) {
                            setLastError(names[i], "您选中的" + names[i] + "项值为" + fieldValue +
                                    ",必须除了" + select + "值之外;");
                            return false;
                        } else if (!values[i].equals(fieldValue)) {
                            setLastError(names[i], "您选中的" + names[i] + "项值" + fieldValue +
                                    ",必须设为" + values[i] + ";");
                            return false;
                        }
                    }
                } else {
                    setLastError(names[i], names[i] + "不能为空;");
                    return false;
                }
            }
        }

        return true;
    }
}

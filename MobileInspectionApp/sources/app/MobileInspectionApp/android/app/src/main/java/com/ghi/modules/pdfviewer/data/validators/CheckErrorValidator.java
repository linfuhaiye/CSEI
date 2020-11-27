package com.ghi.modules.pdfviewer.data.validators;

import com.blankj.utilcode.util.StringUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.ghi.modules.pdfviewer.data.processors.Context;
import com.ghi.modules.pdfviewer.data.thirdparty.compatibility.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * 检验不合格记录校验器
 *
 * @author Alex
 */
public final class CheckErrorValidator extends Validator {
    private static final String REMARK = "remark";
    private static final String PROID = "proId";

    @Override
    public boolean validate(final Context context) {
        final BindingDataMap map = context.getMap();
        final List<Map<String, String>> failedFields = context.getFailedFields();

        for (Map<String, String> failedField : failedFields) {
            final String value = failedField.get(REMARK) == null ? "" : failedField.get(REMARK);
            if (value == null || StringUtils.isEmpty(value.trim())) {
                final String fieldName = StringUtil.removeFromStart(failedField.get(PROID), '/');
                final BindingData formField = map.get(fieldName + ".1");
                if (formField != null) {
                    setLastError(formField.getName(), fieldName + "项里至少要描述一个问题");
                    return false;
                }
            }
        }

        return true;
    }
}

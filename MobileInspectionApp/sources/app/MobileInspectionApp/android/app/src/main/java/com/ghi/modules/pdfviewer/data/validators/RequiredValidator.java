package com.ghi.modules.pdfviewer.data.validators;

import com.blankj.utilcode.util.StringUtils;
import com.foxit.sdk.pdf.interform.Field;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.processors.Context;

/**
 * 必填表单域校验器
 *
 * @author Alex
 */
public final class RequiredValidator extends Validator {
    /**
     * 是否为必填域
     *
     * @param bindingData 域
     * @return 是否为必填域
     */
    public static boolean isRequired(final BindingData bindingData) {
        return ((bindingData.getFlags() & Field.e_FlagRequired) == Field.e_FlagRequired);
    }

    @Override
    public boolean validate(final Context context) {
        for (BindingData bindingData : context.getMap().values()) {
            if (bindingData.getName().matches("^SignDate[0-9]")) {
                continue;
            }

            final String name = bindingData.getValue() == null ? "" : bindingData.getValue().toString();
            if (isRequired(bindingData) && StringUtils.isEmpty(name.trim())) {
                setLastError(bindingData.getName(), bindingData.getAlternateName() + "是必填的");
                return false;
            }
        }

        return true;
    }
}

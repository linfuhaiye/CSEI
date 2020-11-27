package com.ghi.modules.pdfviewer.data.validators;

import com.blankj.utilcode.util.StringUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.ghi.modules.pdfviewer.data.processors.Context;
import com.ghi.modules.pdfviewer.data.thirdparty.compatibility.XmlUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 测量设备性能表单域校验器
 *
 * @author Alex
 */
public final class MeasureValidator extends Validator {
    private static final Pattern P = Pattern.compile("[^0-9]");

    @Override
    public boolean validate(final Context context) {
        final BindingDataMap map = context.getMap();
        for (BindingData bindingData : context.getMap().values()) {
            if (!bindingData.getName().matches("^MERSURE+[0-9]+$")) {
                continue;
            }

            final String fieldValue = bindingData.getValue() == null ? "" : bindingData.getValue().toString();
            if (!StringUtils.isEmpty(fieldValue.trim())) {
                Matcher matcher = P.matcher(bindingData.getName());
                final int index = Integer.parseInt(matcher.replaceAll("").trim());
                final BindingData formFieldSSTA = map.get(XmlUtil.measure_ssta + index);
                final BindingData formFieldFSTA = map.get(XmlUtil.measure_fsta + index);
                final BindingData formFieldCode = map.get(XmlUtil.measure_cod + index);
                final BindingData formFieldType = map.get(XmlUtil.measure_type + index);
                if (formFieldCode == null || formFieldType == null || formFieldCode == null
                        || formFieldType == null) {
                    setLastError(bindingData.getName(), "当前PDF的表单域的命名格式不对");
                    return false;
                }

                final String ssta = formFieldSSTA.getValue() == null ? "" : formFieldSSTA.getValue().toString();
                final String fsta = formFieldFSTA.getValue() == null ? "" : formFieldFSTA.getValue().toString();
                final String type = formFieldType.getValue() == null ? "" : formFieldType.getValue().toString();
                final String code = formFieldCode.getValue() == null ? "" : formFieldCode.getValue().toString();
                if (StringUtils.isEmpty(ssta.trim()) || (StringUtils.isEmpty(fsta.trim()))
                        || StringUtils.isEmpty(type.trim()) || StringUtils.isEmpty(code.trim())) {
                    setLastError(bindingData.getName(), "设备名称不为空时,型号、编号都是必填");
                    return false;
                }
            }
        }

        return true;
    }
}

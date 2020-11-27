package com.ghi.modules.pdfviewer.data.processors;

import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.ghi.modules.pdfviewer.data.core.FieldNames;
import com.ghi.modules.pdfviewer.data.entities.SystemLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 杂项处理器
 *
 * @author etrit
 */
public final class MiscProcessor {
    public final static class Cl1FieldProcessor extends Processor {
        public Cl1FieldProcessor(final Context context, final Object... arguments) {
            super(context, arguments);
        }

        @Override
        public void onLoadDocument(final BindingDataMap map, final Map<String, String> fields) {
            if (context.isFirstOpenWithEmptyFile() || context.isFirstOpenWithFileCopied() || context.isFirstOpenWithDataCopied()) {
                // 首次打开、文件复制打开、数据复制打开
                // 域名为cl1的域赋值index.value := index.value + EQP_COD;
                map.setValue("cl1", map.get("cl1") + context.getTaskInformation().getEqpCode());
            }
        }
    }

    public final static class Cl2FieldProcessor extends Processor {
        public Cl2FieldProcessor(final Context context, final Object... arguments) {
            super(context, arguments);
        }

        @Override
        public void onLoadDocument(final BindingDataMap map, final Map<String, String> fields) {
            if (context.isFirstOpenWithEmptyFile() || context.isFirstOpenWithFileCopied() || context.isFirstOpenWithDataCopied()) {
                // 首次打开、文件复制打开、数据复制打开
                if (context.isRegularInspection()) {
                    // 根据是否限速器校验（可通过服务器给的值），定检情况
                    if (context.isLimit()) {
                        // 如果为是cl2的值index.value := index.value + EQP_COD;
                        map.setValue("cl2", map.get("cl2") + context.getTaskInformation().getEqpCode());
                        // 域名为2.11.3的值填入▽
                        map.setValue("2.11.3", "▽");
                        // TODO: 并且触发值填入的联动动作改变对应的描述
                    } else {
                        // 如果是否，cl2填入值“材料名称《》，编号：／”
                        map.setValue("cl2", "材料名称《》，编号：／");
                        // 2.11.3设置为“／”
                        map.setValue("2.11.3", "／");
                    }
                } else if (context.isSupervision()) {
                    // 监检情况cl2的值清空，2.11.3设置为“／”
                    map.setValue("cl2", "");
                    map.setValue("2.11.3", "／");
                }
            }
        }
    }

    public final static class SetEqpCodeFieldProcessor extends Processor {
        public SetEqpCodeFieldProcessor(final Context context, final Object... arguments) {
            super(context, arguments);
        }

        @Override
        public void onLoadDocument(final BindingDataMap map, final Map<String, String> fields) {
            if (context.isFirstOpenWithEmptyFile() || context.isFirstOpenWithFileCopied() || context.isFirstOpenWithDataCopied()) {
                // 首次打开、文件复制打开、数据复制打开
                // 设备号要改成当前的设备号
                map.setValue(FieldNames.EQP_COD, context.getTaskInformation().getEqpCode());
            }
        }
    }

    public final static class DateFieldProcessor extends Processor {
        public DateFieldProcessor(final Context context, final Object... arguments) {
            super(context, arguments);
        }

        @Override
        public void onLoadDocument(final BindingDataMap map, final Map<String, String> fields) {
            if (context.isFirstOpenWithEmptyFile() || context.isFirstOpenWithFileCopied() || context.isFirstOpenWithDataCopied() || context.isReinspection()) {
                // 首次打开、文件复制打开、数据复制打开、复检首次打开
                // 数据加载完后，根据下检日期规则计算一次下检日期。
                context.calculateEffDate();
            }
        }
    }

    public final static class FailedFieldProcessor extends Processor {
        public FailedFieldProcessor(final Context context, final Object... arguments) {
            super(context, arguments);
        }

        @Override
        public void onLoadDocument(final BindingDataMap map, final Map<String, String> fields) {
            if (context.isReinspection()) {
                // 复检原始记录首次打开
                // 数据加载阶段就读取不合格项目列表
                final List<BindingData> failedFields = map.filter((bindingData) -> bindingData.getName().startsWith("cod_bhg"));
                final List<BindingData> remarkFields = map.filter((bindingData) -> bindingData.getName().startsWith("remark"));
                // 列表长度必须一致
                if (failedFields.size() == remarkFields.size()) {
                    final List<Map<String, String>> result = new ArrayList<>(failedFields.size());
                    for (int i = 0; i < failedFields.size(); i++) {
                        // 构造不合格项目
                        Map<String, String> item = new HashMap<String, String>(2);
                        item.put("proId", (String) failedFields.get(i).getValue());
                        item.put("remark", (String) remarkFields.get(i).getValue());
                        result.add(item);
                    }

                    // 设置不合格项目列表
                    context.setFailedFields(result);

                    // 把不合格项目数量和不合格标志写入系统日志文件noitemcount，noitemflag
                    final SystemLog systemLog = context.getSystemLog();
                    systemLog.setNoitemflag(result.size() > 0 ? "1" : "0");
                    systemLog.setNoitemcount(String.valueOf(result.size()));
                }
            }
        }
    }
}

package com.ghi.modules.pdfviewer;

import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.ghi.miscs.JsonUtils;
import com.ghi.miscs.MethodUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.entities.TaskInformation;
import com.ghi.modules.pdfviewer.data.processors.SourceProcessor;
import com.ghi.modules.pdfviewer.data.processors.TestLogConfigurationProcessor;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * PDF阅读器模块
 *
 * @author Alex
 */
public final class RnPdfViewerModule extends ReactContextBaseJavaModule {
    /**
     * 上下文
     */
    private final ReactApplicationContext context;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public RnPdfViewerModule(final ReactApplicationContext context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "_PdfViewerApi";
    }

    /**
     * 打开PDF文档
     *
     * @param componentId 组件索引
     * @param path        路径
     * @param promise     承诺
     */
    @ReactMethod
    public void openDocument(final int componentId, final String path, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                viewer.openDocument(path);
                promise.resolve(true);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 打开PDF文档
     *
     * @param componentId     组件索引
     * @param path            路径
     * @param taskInformation 任务信息
     * @param promise         承诺
     */
    @ReactMethod
    public void openDocument(final int componentId, final String path, final String taskInformation, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                viewer.openDocument(path, JsonUtils.toObject(taskInformation, TaskInformation.class));
                promise.resolve(true);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 获取域内容
     *
     * @param componentId 组件索引
     * @param name        域名
     * @param offsetX1    横向偏移
     * @param offsetX2    横向偏移
     * @param offsetY1    纵向偏移
     * @param offsetY2    横向偏移
     * @param promise     承诺
     */
    @ReactMethod
    public void getFieldValue(final int componentId, final String name, final int offsetX1, int offsetX2, final int offsetY1, final int offsetY2, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                final String value = viewer.getFieldValue(name, offsetX1, offsetX2, offsetY1, offsetY2);
                promise.resolve(value);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(null);
            }
        });
    }

    /**
     * 获取域信息
     *
     * @param componentId 组件索引
     * @param fields      请求域信息
     * @param promise     承诺
     */
    @ReactMethod
    public void getFields(final int componentId, final String fields, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();

                final ArrayList<RequestFieldInfo> fieldInfoArray = JsonUtils.toObject(fields, new TypeToken<ArrayList<RequestFieldInfo>>() {
                }.getType());
                if (fieldInfoArray == null) {
                    promise.resolve(null);
                    return;
                }

                final List<FieldInfo> list = new ArrayList<>(fieldInfoArray.size());
                for (RequestFieldInfo field : fieldInfoArray) {
                    final String title = viewer.getFieldValue(field.name, field.offsetX1, field.offsetX2, field.offsetY1, field.offsetY2);
                    final BindingData data = viewer.getField(field.name);
                    list.add(new FieldInfo(title, data));
                }

                promise.resolve(JsonUtils.fromObject(list));
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(null);
            }
        });
    }

    /**
     * 设置域值
     *
     * @param componentId 组件索引
     * @param fieldsValue 域值
     * @param promise     承诺
     */
    @ReactMethod
    public void setFieldsValue(final int componentId, final String fieldsValue, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();

                final ArrayList<FieldValue> fieldValueArray = JsonUtils.toObject(fieldsValue, new TypeToken<ArrayList<FieldValue>>() {
                }.getType());
                if (fieldValueArray == null) {
                    promise.resolve(false);
                    return;
                }

                for (FieldValue field : fieldValueArray) {
                    final BindingData data = viewer.getField(field.name);
                    data.setValue(field.getValue());
                }

                promise.resolve(true);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 读取设备信息
     *
     * @param path            路径
     * @param taskInformation 任务信息
     * @param promise         承诺
     */
    @ReactMethod
    public void getInformation(final String path, final String taskInformation, final Promise promise) {
        try {
            final TaskInformation taskInformation1 = JsonUtils.toObject(taskInformation, TaskInformation.class);
            final Map<String, String> information = SourceProcessor.read(path, taskInformation1.getReportCode());
            promise.resolve(JsonUtils.fromObject(information));
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(null);
        }
    }

    /**
     * 读取模板配置
     *
     * @param path            路径
     * @param taskInformation 任务信息
     * @param key             键值
     * @param promise         承诺
     */
    @ReactMethod
    public void getTestLogConfiguration(final String path, final String taskInformation, final String key, final Promise promise) {
        try {
            final TaskInformation taskInformation1 = JsonUtils.toObject(taskInformation, TaskInformation.class);
            final Map<String, JsonElement> configurations = TestLogConfigurationProcessor.read(path, taskInformation1.getReportCode(), taskInformation1.getEqpType());
            final JsonElement element = JsonUtils.getElement(configurations, key);
            if (element == null) {
                promise.resolve(null);
                return;
            }

            final String value = element.toString();
            promise.resolve(value);
        } catch (Exception e) {
            LogUtils.e(e);
            promise.resolve(null);
        }
    }

    /**
     * 保存
     *
     * @param componentId 组件索引
     * @param promise     承诺
     */
    @ReactMethod
    public void save(final int componentId, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                final boolean result = viewer.getLoader().getContext().save();
                promise.resolve(result);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 复制PDF文件
     *
     * @param componentId 组件索引
     * @param reportCodes 报告号列表
     * @param promise     承诺
     */
    @ReactMethod
    public void copyPdf(final int componentId, final String reportCodes, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                final ArrayList<String> reportCodeArray = JsonUtils.toObject(reportCodes, new TypeToken<ArrayList<String>>() {
                }.getType());
                if (reportCodeArray == null) {
                    promise.resolve(false);
                    return;
                }

                viewer.getLoader().getContext().copyPdf(reportCodeArray);
                promise.resolve(true);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 复制原始记录数据
     *
     * @param componentId 组件索引
     * @param reportCodes 报告号列表
     * @param promise     承诺
     */
    @ReactMethod
    public void copyData(final int componentId, final String reportCodes, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                final ArrayList<String> reportCodeArray = JsonUtils.toObject(reportCodes, new TypeToken<ArrayList<String>>() {
                }.getType());
                if (reportCodeArray == null) {
                    promise.resolve(false);
                    return;
                }

                viewer.getLoader().getContext().copyData(reportCodeArray);
                promise.resolve(true);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 下结论
     *
     * @param componentId 组件索引
     * @param promise     承诺
     */
    @ReactMethod
    public void makeConclusion(final int componentId, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                viewer.getLoader().getContext().makeConclusion();
                promise.resolve(true);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 是否可以检验员签名
     *
     * @param componentId 组件索引
     * @param promise     承诺
     */
    @ReactMethod
    public void canSign(final int componentId, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                promise.resolve(viewer.getLoader().getContext().canSign());
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 检验员签名
     *
     * @param componentId 组件索引
     * @param username    用户名
     * @param password    密码
     * @param promise     承诺
     */
    @ReactMethod
    public void sign(final int componentId, final String username, final String password, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                promise.resolve(viewer.getLoader().getContext().sign(username, password));
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 是否可以校核签名
     *
     * @param componentId 组件索引
     * @param promise     承诺
     */
    @ReactMethod
    public void canCheckSign(final int componentId, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                promise.resolve(viewer.getLoader().getContext().canCheckSign());
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 校核签名
     *
     * @param componentId 组件索引
     * @param username    用户名
     * @param password    密码
     * @param promise     承诺
     */
    @ReactMethod
    public void checkSign(final int componentId, final String username, final String password, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                promise.resolve(viewer.getLoader().getContext().checkSign(username, password));
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 获取书签
     *
     * @param componentId 组件索引
     * @param promise     承诺
     */
    @ReactMethod
    public void getBookmarks(final int componentId, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                promise.resolve(JsonUtils.fromObject(viewer.getLoader().getContext().getBookmarks()));
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(null);
            }
        });
    }

    /**
     * 校核签名
     *
     * @param componentId 组件索引
     * @param page        页面索引
     * @param promise     承诺
     */
    @ReactMethod
    public void gotoPage(final int componentId, final int page, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                viewer.gotoPage(page);
                promise.resolve(true);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 获取设备概况域列表
     *
     * @param componentId 组件索引
     * @param promise     承诺
     */
    @ReactMethod
    public void getDeviceInformationFields(final int componentId, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                final List<BindingData> list = viewer.getLoader().getContext().getDeviceInformationFields();
                promise.resolve(JsonUtils.fromObject(list));
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(null);
            }
        });
    }

    /**
     * 获取检验记录域列表
     *
     * @param componentId 组件索引
     * @param promise     承诺
     */
    @ReactMethod
    public void getInspectionResultFields(final int componentId, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                final List<BindingData> list = viewer.getLoader().getContext().getInspectionResultFields();
                promise.resolve(JsonUtils.fromObject(list));
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(null);
            }
        });
    }

    /**
     * 填充测试数据
     *
     * @param componentId 组件索引
     * @param promise     承诺
     */
    @ReactMethod
    public void fillTestData(final int componentId, final Promise promise) {
        MethodUtils.<View>invokeComponentMethod(context, componentId, (view) -> {
            try {
                final PdfViewer viewer = (PdfViewer) view.getTag();
                viewer.getLoader().getContext().fillTestData();
                promise.resolve(true);
            } catch (Exception e) {
                LogUtils.e(e);
                promise.resolve(false);
            }
        });
    }

    /**
     * 请求域信息
     */
    @Getter
    @Setter
    private static class RequestFieldInfo {
        /**
         * 域名
         */
        private String name;

        /**
         * 横向偏移
         */
        private int offsetX1;

        /**
         * 横向偏移
         */
        private int offsetX2;

        /**
         * 横向偏移
         */
        private int offsetY1;

        /**
         * 横向偏移
         */
        private int offsetY2;
    }

    /**
     * 域信息
     */
    @Getter
    @Setter
    @AllArgsConstructor
    private static class FieldInfo {
        /**
         * 标题
         */
        private final String title;

        /**
         * 域
         */
        private final BindingData field;
    }

    /**
     * 域值
     */
    @Getter
    @Setter
    @AllArgsConstructor
    private static class FieldValue {
        /**
         * 域名
         */
        private final String name;

        /**
         * 值
         */
        private final Object value;
    }
}

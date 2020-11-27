package com.ghi.modules.pdfviewer.data.processors;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.foxit.sdk.PDFException;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.sdk.pdf.ReadingBookmark;
import com.foxit.sdk.pdf.interform.Field;
import com.ghi.miscs.JsonUtils;
import com.ghi.miscs.PdfUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.ghi.modules.pdfviewer.data.core.FieldNames;
import com.ghi.modules.pdfviewer.data.entities.Bookmark;
import com.ghi.modules.pdfviewer.data.processors.association.AssociationConfiguration;
import com.ghi.modules.pdfviewer.data.processors.association.AutoCalculateConfiguration;
import com.ghi.modules.pdfviewer.data.processors.association.ConclusionConfiguration;
import com.ghi.modules.pdfviewer.data.processors.association.CopyingConfiguration;
import com.ghi.modules.pdfviewer.data.processors.association.RoundingConfiguration;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板配置处理器
 *
 * @author Alex
 */
public final class TestLogConfigurationProcessor extends Processor {
    /**
     * 文件名
     */
    private static final String FILENAME = "/testlogcfg.ses";

    /**
     * 模板配置
     */
    private final Map<String, JsonElement> configurations;

    /**
     * 构造函数
     *
     * @param context        上下文
     * @param path           路径
     * @param configurations 模板配置
     * @param arguments      参数
     */
    public TestLogConfigurationProcessor(final Context context, final String path, final Map<String, JsonElement> configurations, final Object... arguments) throws FileNotFoundException {
        super(context, arguments);
        this.configurations = configurations;
    }

    /**
     * 获取系统文件配置路径
     *
     * @param path       父路径
     * @param reportCode 报告号
     * @return 系统文件配置路径
     */
    public static String getFilename(final String path, final String reportCode) {
        return path + File.separator + reportCode + FILENAME;
    }

    /**
     * 读取模板配置
     *
     * @param path       路径
     * @param reportCode 报告号
     * @param key        配置名称
     * @return 配置
     */
    public static Map<String, JsonElement> read(final String path, final String reportCode, final String key) {
        // 读取原始记录配置文件
        final String content = FileIOUtils.readFile2String(getFilename(path, reportCode), StandardCharsets.UTF_8.name());
        if (content == null) {
            return null;
        }

        // JSON字符串转换为JSON对象
        final JsonObject object = JsonParser.parseString(content).getAsJsonObject();
        // 根据配置名称查找键值
        final JsonArray jsonArray = object.get(key).getAsJsonArray();
        // 获取取首个元素
        final JsonObject json = (JsonObject) jsonArray.get(0);
        // 映射为键值对
        final HashMap<String, JsonElement> map = new HashMap<>(json.entrySet().size());
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    /**
     * 读取书签
     *
     * @param document 文档
     * @throws PDFException 异常
     */
    private static void loadPdfBookmarks(final PDFDoc document, final List<Bookmark> bookmarks) throws PDFException {
        final int count = document.getReadingBookmarkCount();
        for (int i = 0; i < count; i++) {
            ReadingBookmark readingBookmark = document.getReadingBookmark(i);
            if (readingBookmark == null) {
                continue;
            }

            bookmarks.add(new Bookmark(readingBookmark.getTitle(), readingBookmark.getPageIndex()));
        }
    }

    /**
     * 读取书签
     *
     * @param configurations 模板配置
     */
    private static void loadBookmarks(final Map<String, JsonElement> configurations, final List<Bookmark> bookmarks) {
        final List<Bookmark> array = JsonUtils.getArray(configurations, FieldNames.BOOKMARKS, new TypeToken<ArrayList<Bookmark>>() {
        }.getType());
        if (array != null) {
            bookmarks.addAll(array);
        }
    }

    @Override
    public void onInitialize(final BindingDataMap map) {
        // 设置修约配置
        RoundingConfiguration.bind(map, configurations);

        // 设置复制联动配置
        CopyingConfiguration.bind(map, configurations);

        // 设置关联配置
        AssociationConfiguration.bind(map, configurations);

        // 设置自动计算配置
        AutoCalculateConfiguration.bind(map, configurations);

        // 设置结论配置
        ConclusionConfiguration.bind(map, configurations);
    }

    @Override
    public void onLoadDocument(final BindingDataMap map, final Map<String, String> fields) {
        if (context.isFirstOpenWithEmptyFile() || context.isFirstOpenWithDataCopied()) {
            // 域类型是下拉框的，从配置testlogcfg.ses读取下拉选项配置到pdf文件中。部分不会变动的已经直接在pdf文件中写入
            for (BindingData bindingData : map.values()) {
                if (bindingData.getType() == Field.e_TypeComboBox) {
                    final JsonElement element = configurations.get(bindingData.getName());
                    if (element != null) {
                        // 设置下拉菜单表单域
                        PdfUtils.setComboBoxOptions(bindingData, element.getAsJsonArray());
                    } else {
                        // 对未配置内容的表单域进行默认填充
                        if (StringUtils.isTrimEmpty((String) bindingData.getValue())) {
                            bindingData.setValue("");
                        }
                    }
                }
            }
        }

        try {
            // 读取书签列表
            final List<Bookmark> bookmarks = new ArrayList<>();
            loadPdfBookmarks(context.getViewer().getUiExtensionsManager().getPDFViewCtrl().getDoc(), bookmarks);
            loadBookmarks(configurations, bookmarks);
            context.setBookmarks(bookmarks);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }
}

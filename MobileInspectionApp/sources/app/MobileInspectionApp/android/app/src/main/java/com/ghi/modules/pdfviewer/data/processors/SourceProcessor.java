package com.ghi.modules.pdfviewer.data.processors;

import com.blankj.utilcode.util.LogUtils;
import com.ghi.miscs.JsonUtils;
import com.ghi.miscs.XmlUtils;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.ghi.modules.pdfviewer.data.core.FieldNames;
import com.ghi.modules.pdfviewer.data.entities.ControlInformation;
import com.google.gson.JsonElement;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源处理器
 *
 * @author Alex
 */
public final class SourceProcessor extends Processor {
    /**
     * 文件名
     */
    private static final String FILENAME = "/source.xml";

    /**
     * 所有域名信息键值
     */
    private static final String EQUIP_INFO = "equipinfo";

    /**
     * 控制键值
     */
    private static final String CONTROL = "control";

    /**
     * 需要复制域名
     */
    private static final String DEFAULT_COPY_FIELDS = "reportno,lognum,EQP_COD,EQP_MOD,FACTORY_COD,OIDNO";

    /**
     * 不复制域名
     */
    private static final String NO_COPY_FIELD = "nocopyfield";

    /**
     * set_nextvalue域名
     */
    private static final String SET_NEXT_VALUE_FIELD = "set_nextvalue";

    /**
     * 改造标志
     */
    private static final String 改造 = "改造";

    /**
     * 基础信息
     */
    private final Map<String, String> information;

    /**
     * 模板配置
     */
    private final Map<String, JsonElement> configurations;

    /**
     * 构造函数
     *
     * @param context        上下文
     * @param path           路径
     * @param information    基础信息
     * @param configurations 模板配置
     * @param arguments      参数
     */
    public SourceProcessor(final Context context, final String path, final Map<String, String> information, final Map<String, JsonElement> configurations, final Object... arguments) throws IOException, DocumentException {
        super(context, arguments);
        this.information = information;
        this.configurations = configurations;
    }

    /**
     * 获取数据源路径
     *
     * @param path       父路径
     * @param reportCode 报告号
     * @return 路径
     */
    public static String getFilename(final String path, final String reportCode) {
        return path + File.separator + reportCode + FILENAME;
    }

    /**
     * 读取数据源
     *
     * @param path       路径
     * @param reportCode 报告号
     * @return 数据源
     * @throws IOException       读取异常
     * @throws DocumentException 文档内容异常
     */
    public static Map<String, String> read(final String path, final String reportCode) throws IOException, DocumentException {
        try (FileInputStream is = new FileInputStream(getFilename(path, reportCode))) {
            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding(StandardCharsets.UTF_8.name());
            Document document = saxReader.read(is);
            return readInformation(document);
        } catch (Exception e) {
            LogUtils.e(e);
            throw e;
        }
    }

    /**
     * 读取控制信息
     *
     * @param path       路径
     * @param reportCode 报告号
     * @return 控制信息
     * @throws IOException       读取异常
     * @throws DocumentException 文档内容异常
     */
    public static ControlInformation readControlInformation(final String path, final String reportCode) throws IOException, DocumentException {
        try (FileInputStream is = new FileInputStream(getFilename(path, reportCode))) {
            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding(StandardCharsets.UTF_8.name());
            Document document = saxReader.read(is);
            return readControlInformation(document);
        } catch (Exception e) {
            LogUtils.e(e);
            throw e;
        }
    }

    /**
     * 获取信息
     *
     * @param document 文档
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> readInformation(final Document document) {
        final Map<String, String> information = new ConcurrentHashMap<>();
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.elements();
        for (Element element : list) {
            if (EQUIP_INFO.equals(element.getName())) {
                List<Element> bean = element.elements();
                information.put(bean.get(0).getStringValue(), bean.get(1).getStringValue());
            }
        }

        return information;
    }

    /**
     * 获取控制信息
     *
     * @param document 文档
     */
    @SuppressWarnings("unchecked")
    private static ControlInformation readControlInformation(final Document document) {
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.elements();
        for (Element element : list) {
            if (CONTROL.equals(element.getName())) {
                return readControlInformation(element);
            }
        }

        return null;
    }

    /**
     * 获取控制信息
     *
     * @param element XML元素
     * @return 控制信息
     */
    @SuppressWarnings("unchecked")
    private static ControlInformation readControlInformation(final Element element) {
        final ControlInformation information = new ControlInformation();
        final List<Element> list = element.elements();
        for (Element item : list) {
            if ("recheck".equals(item.getName())) {
                information.setRecheck(item.getStringValue());
            } else if ("user".equals(item.getName())) {
                final ControlInformation.User user = new ControlInformation.User();
                user.setUsername(XmlUtils.getSubElementValue(item, "username"));
                user.setOldusername(XmlUtils.getSubElementValue(item, "oldusername"));
                user.setUname(XmlUtils.getSubElementValue(item, "uname"));
                user.setPassword(XmlUtils.getSubElementValue(item, "password"));
                user.setPicpwd(XmlUtils.getSubElementValue(item, "picpwd"));
                user.setDIG_CERT_SIGN(XmlUtils.getSubElementValue(item, "DIG_CERT_SIGN"));
                information.getUsers().add(user);
            }
        }

        return information;
    }

    @Override
    public void onLoadDocument(final BindingDataMap map, final Map<String, String> fields) {
        if (context.isFirstOpenWithFileCopied() || context.isFirstOpenWithDataCopied()) {
            // PDF文件复制首次打开
            // testlogcfg.ses文件中的nocopyfield和set_nextvalue的字段名，加上reportno,lognum,EQP_COD,EQP_MOD,FACTORY_COD,OIDNO。
            List<String> copyFields = Arrays.asList(getCopyFields());
            // 以上这些字段重新初始化，即重新读取source.xml的数据
            for (Map.Entry<String, String> entry : information.entrySet()) {
                if (copyFields.contains(entry.getKey())) {
                    map.setValue(entry.getKey(), entry.getValue());
                }
            }

            // 取equipinfo中CONS_TYPE的值，若为“改造”，把source.xml里面的bz3填入域名为bz3中。
            String value = getDeviceInformation(FieldNames.CONS_TYPE);
            if (改造.equals(value)) {
                map.setValue("bz3", getDeviceInformation("bz3"));
            }
        }

        if (context.isFirstOpenWithEmptyFile()) {
            // 设备概况和观测数据部分，将source.xml文件中equipinfo部分的name匹配域名，value结点的值填入域中。
            for (Map.Entry<String, String> entry : information.entrySet()) {
                map.setValue(entry.getKey(), entry.getValue());
            }
            // 取equipinfo中CONS_TYPE的值，若为“改造”，把source.xml里面的bz3填入域名为bz3中。
            String value = getDeviceInformation(FieldNames.CONS_TYPE);
            if (改造.equals(value)) {
                map.setValue("bz3", getDeviceInformation("bz3"));
            }
        }
    }

    /**
     * 获取设备信息
     *
     * @param key 键
     * @return 值
     */
    public String getDeviceInformation(final String key) {
        return information.get(key);
    }

    /**
     * @return 获取复制域
     */
    private String[] getCopyFields() {
        // testlogcfg.ses文件中的nocopyfield和set_nextvalue的字段名，加上reportno,lognum,EQP_COD,EQP_MOD,FACTORY_COD,OIDNO。
        String copyFields = DEFAULT_COPY_FIELDS;

        String field1 = JsonUtils.getValue(configurations, NO_COPY_FIELD);
        if (field1 != null) {
            copyFields += "," + field1;
        }

        String field2 = JsonUtils.getValue(configurations, SET_NEXT_VALUE_FIELD);
        if (field2 != null) {
            copyFields += "," + field2;
        }

        return copyFields.split(",");
    }
}

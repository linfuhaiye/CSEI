package com.ghi.miscs;

import org.dom4j.Element;

import java.util.List;

/**
 * XML工具
 *
 * @author Alex
 */
public final class XmlUtils {
    /**
     * 获取子元素内容
     *
     * @param element     元素
     * @param elementName 子元素名称
     * @return 内容
     */
    @SuppressWarnings("unchecked")
    public static String getSubElementValue(final Element element, final String elementName) {
        final List<Element> list = element.elements(elementName);
        return (list.size() > 0) ? list.get(0).getStringValue() : null;
    }
}

package com.ghi.modules.pdfviewer.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 书签
 *
 * @author Alex
 */
@Getter
@Setter
@AllArgsConstructor
public final class Bookmark {
    /**
     * 标题
     */
    private String title;

    /**
     * 页码
     */
    private int page;
}

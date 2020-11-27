/**
 * Copyright (C) 2003-2020, Foxit Software Inc..
 * All Rights Reserved.
 * <p>
 * http://www.foxitsoftware.com
 * <p>
 * The following code is copyrighted and is the proprietary of Foxit Software Inc.. It is not allowed to
 * distribute any parts of Foxit PDF SDK to third party or public without permission unless an agreement
 * is signed between Foxit Software Inc. and customers to explicitly grant customers permissions.
 * Review legal.txt for additional license and legal information.
 */
package com.foxit.uiextensions.annots.common;


import com.foxit.sdk.PDFException;
import com.foxit.sdk.common.DateTime;
import com.foxit.sdk.pdf.annots.Markup;
import com.foxit.sdk.pdf.annots.Note;
import com.foxit.uiextensions.utils.AppAnnotUtil;

import java.util.ArrayList;
import java.util.List;

public class ReplyTreeNode {
    private ReplyTreeNode mPanent;
    private List<ReplyTreeNode> mChilds = new ArrayList<>();

    private String mParentId;
    private String mId;
    private String mContent;
    private String mAuthor;
    private DateTime mCreateTime;

    public ReplyTreeNode(String id) {
        this.mId = id;
    }

    private ReplyTreeNode(String id, String content, String author, DateTime createTime) {
        this.mId = id;
        this.mContent = content;
        this.mAuthor = author;
        this.mCreateTime = createTime;
    }

    private void addChild(ReplyTreeNode child) {
        child.mParentId = mId;
        child.mPanent = this;
        mChilds.add(child);
    }

    public ReplyTreeNode addChilds(ReplyTreeNode replyNode, Markup annot) {
        try {
            int replyCount = annot.getReplyCount();
            for (int i = 0; i < replyCount; i++) {
                Note note = annot.getReply(i);
                if (note == null || note.isEmpty()) continue;
                ReplyTreeNode childReply = new ReplyTreeNode(note.getUniqueID(), note.getContent(), note.getTitle(), note.getCreationDateTime());
                replyNode.addChild(childReply);

                addChilds(childReply, note);
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return replyNode;
    }

    public void addReply(Markup replyToAnnot, ReplyTreeNode replyNode) {
        try {
            if (replyToAnnot == null || replyToAnnot.isEmpty() || !replyToAnnot.isMarkup()) return;

            List<ReplyTreeNode> childs = replyNode.mChilds;
            for (int i = 0; i < childs.size(); i++) {
                ReplyTreeNode childNode = childs.get(i);
                if (childNode.mPanent != null) {
                    String parentId = childNode.mParentId;
                    if (!parentId.equals(AppAnnotUtil.getAnnotUniqueID(replyToAnnot))) continue;

                    Note note = replyToAnnot.addReply();
                    note.setUniqueID(childNode.mId);
                    note.setContent(childNode.mContent);
                    note.setTitle(childNode.mAuthor);
                    note.setCreationDateTime(childNode.mCreateTime);
                    note.resetAppearanceStream();

                    addReply(note, childNode);
                }
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }
}

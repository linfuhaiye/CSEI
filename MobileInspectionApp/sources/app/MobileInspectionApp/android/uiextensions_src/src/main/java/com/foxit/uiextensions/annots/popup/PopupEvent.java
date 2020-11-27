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
package com.foxit.uiextensions.annots.popup;


import android.graphics.RectF;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.annots.Annot;
import com.foxit.sdk.pdf.annots.Markup;
import com.foxit.sdk.pdf.annots.Popup;
import com.foxit.uiextensions.annots.common.EditAnnotEvent;
import com.foxit.uiextensions.utils.AppDmUtil;
import com.foxit.uiextensions.utils.AppUtil;

class PopupEvent extends EditAnnotEvent {
    private static final float POP_DEFAULT_PDF_WIDTH = 200;
    private static final float POP_DEFAULT_PDF_HEIGHT = 100;

    private Annot mParentAnnot;

    PopupEvent(int eventType, PDFViewCtrl pdfViewCtrl, Annot parentAnnot, PopupUndoItem undoItem) {
        mType = eventType;
        mUndoItem = undoItem;
        mParentAnnot = parentAnnot;
        mPdfViewCtrl = pdfViewCtrl;
    }

    @Override
    public boolean add() {
        try {
            PDFPage page = mParentAnnot.getPage();
            RectF popRectF;
            if (mUndoItem.mBBox == null) {
                RectF pageRectF = new RectF(0, 0, page.getWidth(), page.getHeight());
                RectF parentRect = AppUtil.toRectF(mParentAnnot.getRect());
                int rotation = page.getRotation();
                popRectF = new RectF();
                if (rotation == Constants.e_Rotation90) {
                    popRectF.left = parentRect.left;
                    popRectF.right = popRectF.left + POP_DEFAULT_PDF_WIDTH;
                    popRectF.bottom = pageRectF.right - POP_DEFAULT_PDF_HEIGHT;
                    popRectF.top = pageRectF.right;
                } else if (rotation == Constants.e_Rotation180) {
                    popRectF.left = 0;
                    popRectF.right = popRectF.left + POP_DEFAULT_PDF_WIDTH;
                    popRectF.top = parentRect.top;
                    popRectF.bottom = popRectF.top - POP_DEFAULT_PDF_HEIGHT;
                } else if (rotation == Constants.e_Rotation270) {
                    popRectF.left = parentRect.right;
                    popRectF.right = popRectF.left + POP_DEFAULT_PDF_WIDTH;
                    popRectF.bottom = -POP_DEFAULT_PDF_HEIGHT;
                    popRectF.top = 0;
                } else {
                    popRectF.left = pageRectF.right;
                    popRectF.right = popRectF.left + POP_DEFAULT_PDF_WIDTH;
                    popRectF.bottom = parentRect.bottom;
                    popRectF.top = popRectF.bottom + POP_DEFAULT_PDF_HEIGHT;
                }
            } else {
                popRectF = new RectF(mUndoItem.mBBox);
            }

            Annot annot = page.addAnnot(Annot.e_Popup, AppUtil.toFxRectF(popRectF));
            Popup popup = new Popup(annot);
            if (mUndoItem.mModifiedDate != null && AppDmUtil.isValidDateTime(mUndoItem.mModifiedDate))
                annot.setModifiedDateTime(mUndoItem.mModifiedDate);
            if (mUndoItem.mContents != null)
                annot.setContent(mUndoItem.mContents);
            popup.setUniqueID(mUndoItem.mNM);
            popup.setFlags(mUndoItem.mFlags);
            ((Markup) mParentAnnot).setPopup(popup);
            return true;
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean modify() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

}

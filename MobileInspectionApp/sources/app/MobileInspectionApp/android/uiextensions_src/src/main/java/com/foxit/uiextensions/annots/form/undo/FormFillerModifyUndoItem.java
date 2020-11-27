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
package com.foxit.uiextensions.annots.form.undo;


import android.graphics.RectF;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Font;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.annots.Annot;
import com.foxit.sdk.pdf.annots.Widget;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.common.EditAnnotEvent;
import com.foxit.uiextensions.annots.common.EditAnnotTask;
import com.foxit.uiextensions.annots.form.ChoiceItemInfo;
import com.foxit.uiextensions.annots.form.FormFillerEvent;
import com.foxit.uiextensions.utils.AppDmUtil;
import com.foxit.uiextensions.utils.AppUtil;
import com.foxit.uiextensions.utils.Event;

import java.util.ArrayList;

public class FormFillerModifyUndoItem extends FormFillerUndoItem {
    public RectF mUndoBbox;
    public String mUndoValue;
    public String mUndoFieldName;

    public int mUndoFontId;
    public int mUndoFontColor;
    public float mUndoFontSize;

    public int mUndoFieldFlags;
    public int mUndoCheckedIndex;
    public ArrayList<ChoiceItemInfo> mUndoOptions;

    public String mRedoValue;
    public String mRedoFieldName;
    public RectF mRedoBbox;

    public int mRedoFontId;
    public int mRedoFontColor;
    public float mRedoFontSize;

    public int mRedoFieldFlags;
    public int mRedoCheckedIndex;
    public ArrayList<ChoiceItemInfo> mRedoOptions;

    public FormFillerModifyUndoItem(PDFViewCtrl pdfViewCtrl) {
        mPdfViewCtrl = pdfViewCtrl;
    }

    @Override
    public boolean undo() {
        return modifyAnnot(mUndoBbox,
                mUndoValue,
                mUndoFieldName,
                mUndoFontId,
                mUndoFontColor,
                mUndoFontSize,
                mUndoFieldFlags,
                mUndoCheckedIndex,
                mUndoOptions);
    }

    @Override
    public boolean redo() {
        return modifyAnnot(mRedoBbox,
                mRedoValue,
                mRedoFieldName,
                mRedoFontId,
                mRedoFontColor,
                mRedoFontSize,
                mRedoFieldFlags,
                mRedoCheckedIndex,
                mRedoOptions);
    }

    private boolean modifyAnnot(RectF bbox,
                                String value,
                                String fieldName,
                                int fontId,
                                int fontColor,
                                float fontSize,
                                int flags,
                                int index,
                                ArrayList<ChoiceItemInfo> options) {
        try {
            final PDFPage page = mPdfViewCtrl.getDoc().getPage(mPageIndex);
            final Annot annot = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getAnnot(page, mNM);
            if (!(annot instanceof Widget)) {
                return false;
            }

            final RectF oldBbox = AppUtil.toRectF(annot.getRect());
            mBBox = new RectF(bbox);
            mValue = value;
            mFontId = fontId;
            mFontColor = fontColor;
            mFontSize = fontSize;
            mFieldName = fieldName;
            mModifiedDate = AppDmUtil.currentDateToDocumentDate();
            mOptions = options;
            mFieldFlags = flags;
            mCheckedIndex = index;
            mNeedResetChecked = true;

            FormFillerEvent modifyEvent = new FormFillerEvent(EditAnnotEvent.EVENTTYPE_MODIFY, this, (Widget) annot, mPdfViewCtrl);
            EditAnnotTask task = new EditAnnotTask(modifyEvent, new Event.Callback() {
                @Override
                public void result(Event event, boolean success) {
                    if (success) {
                        if (annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()) {
                            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setCurrentAnnot(null);
                        }

                        ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().onAnnotModified(page, annot);
                        if (mPdfViewCtrl.isPageVisible(mPageIndex)) {
                            try {
                                RectF annotRect = AppUtil.toRectF(annot.getRect());
                                mPdfViewCtrl.convertPdfRectToPageViewRect(annotRect, annotRect, mPageIndex);
                                mPdfViewCtrl.refresh(mPageIndex, AppDmUtil.rectFToRect(annotRect));

                                mPdfViewCtrl.convertPdfRectToPageViewRect(oldBbox, oldBbox, mPageIndex);
                                mPdfViewCtrl.refresh(mPageIndex, AppDmUtil.rectFToRect(oldBbox));
                            } catch (PDFException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            mPdfViewCtrl.addTask(task);
            return true;
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }

}

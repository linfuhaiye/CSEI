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
package com.foxit.uiextensions.annots.freetext.typewriter;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.common.Font;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.annots.DefaultAppearance;
import com.foxit.sdk.pdf.annots.FreeText;
import com.foxit.sdk.pdf.annots.Markup;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.common.EditAnnotEvent;
import com.foxit.uiextensions.utils.AppDmUtil;
import com.foxit.uiextensions.utils.AppUtil;

public class TypewriterEvent extends EditAnnotEvent {

    public TypewriterEvent(int eventType, TypewriterUndoItem undoItem, FreeText typewriter, PDFViewCtrl pdfViewCtrl) {
        mType = eventType;
        mUndoItem = undoItem;
        mAnnot = typewriter;
        mPdfViewCtrl = pdfViewCtrl;
    }

    @Override
    public boolean add() {
        if (mAnnot == null || !(mAnnot instanceof FreeText)) {
            return false;
        }
        FreeText annot = (FreeText) mAnnot;
        try {
            annot.setBorderColor(mUndoItem.mColor);

            annot.setOpacity(mUndoItem.mOpacity);
            if (mUndoItem.mContents != null) {
                annot.setContent(mUndoItem.mContents);
            }

            annot.setFlags(mUndoItem.mFlags);
            if (mUndoItem.mCreationDate != null && AppDmUtil.isValidDateTime(mUndoItem.mCreationDate)) {
                annot.setCreationDateTime(mUndoItem.mCreationDate);
            }

            if (mUndoItem.mModifiedDate != null && AppDmUtil.isValidDateTime(mUndoItem.mModifiedDate)) {
                annot.setModifiedDateTime(mUndoItem.mModifiedDate);
            }

            if (mUndoItem.mAuthor != null) {
                annot.setTitle(mUndoItem.mAuthor);
            }

            if (mUndoItem.mSubject != null) {
                annot.setSubject(mUndoItem.mSubject);
            }

            TypewriterAddUndoItem undoItem = (TypewriterAddUndoItem) mUndoItem;
            DefaultAppearance da = new DefaultAppearance();
            Font font;
            if (undoItem.mFontId >= Font.e_StdIDCourier && undoItem.mFontId <= Font.e_StdIDZapfDingbats)
                font = new Font(undoItem.mFontId);
            else
                font = new Font(Font.e_StdIDCourier);
            da.set(undoItem.mDaFlags, font, undoItem.mFontSize, undoItem.mTextColor);
            annot.setDefaultAppearance(da);
            annot.setIntent(mUndoItem.mIntent);
            annot.setUniqueID(mUndoItem.mNM);
            annot.setRotation(undoItem.mRotation);
            annot.resetAppearanceStream();
            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setDocModified(true);
            return true;
        } catch (PDFException e) {
            if (e.getLastError() == Constants.e_ErrOutOfMemory) {
                mPdfViewCtrl.recoverForOOM();
            }
        }
        return false;
    }

    @Override
    public boolean modify() {
        if (mAnnot == null || !(mAnnot instanceof FreeText)) {
            return false;
        }

        try {
            if (((FreeText) mAnnot).getIntent() == null
                    || !((FreeText) mAnnot).getIntent().equals("FreeTextTypewriter")) {
                return false;
            }
            FreeText annot = (FreeText) mAnnot;
            TypewriterModifyUndoItem undoItem = (TypewriterModifyUndoItem) mUndoItem;
            DefaultAppearance da = annot.getDefaultAppearance();
            int flags = DefaultAppearance.e_FlagFont | DefaultAppearance.e_FlagTextColor | DefaultAppearance.e_FlagFontSize;
            Font font;
            if (undoItem.mFontId >= Font.e_StdIDCourier && undoItem.mFontId <= Font.e_StdIDZapfDingbats)
                font = new Font(undoItem.mFontId);
            else
                font = new Font(Font.e_StdIDCourier);
            da.set(flags, font, undoItem.mFontSize, undoItem.mTextColor);
            annot.setDefaultAppearance(da);
            annot.setOpacity(undoItem.mOpacity);
            annot.move(AppUtil.toFxRectF(mUndoItem.mBBox));
            annot.setModifiedDateTime(AppDmUtil.currentDateToDocumentDate());
            annot.setContent(undoItem.mContents);
            annot.resetAppearanceStream();
            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setDocModified(true);
            return true;
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete() {
        if (mAnnot == null || !(mAnnot instanceof FreeText)) {
            return false;
        }

        try {
            if (((FreeText) mAnnot).getIntent() == null
                    || !((FreeText) mAnnot).getIntent().equals("FreeTextTypewriter")) {
                return false;
            }
            ((Markup)mAnnot).removeAllReplies();
            PDFPage page = mAnnot.getPage();
            page.removeAnnot(mAnnot);
            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setDocModified(true);
            return true;
        } catch (PDFException e) {
            e.printStackTrace();
        }

        return false;
    }
}

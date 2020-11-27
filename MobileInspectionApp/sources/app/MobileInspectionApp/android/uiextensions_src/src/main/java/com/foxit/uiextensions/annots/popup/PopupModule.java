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


import android.content.Context;
import android.util.SparseArray;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.annots.Annot;
import com.foxit.sdk.pdf.annots.Markup;
import com.foxit.sdk.pdf.annots.Note;
import com.foxit.sdk.pdf.annots.Popup;
import com.foxit.uiextensions.AbstractUndo;
import com.foxit.uiextensions.DocumentManager;
import com.foxit.uiextensions.IUndoItem;
import com.foxit.uiextensions.Module;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.AnnotEventListener;
import com.foxit.uiextensions.annots.AnnotUndoItem;
import com.foxit.uiextensions.annots.common.EditAnnotEvent;
import com.foxit.uiextensions.annots.common.EditAnnotTask;
import com.foxit.uiextensions.utils.AppAnnotUtil;
import com.foxit.uiextensions.utils.AppDmUtil;
import com.foxit.uiextensions.utils.AppUtil;

import java.util.HashMap;
import java.util.Map;

public class PopupModule implements Module {
    private Context mContext;
    private PDFViewCtrl mPdfViewCtrl;
    private UIExtensionsManager mUIExtensionsManager;
    private SparseArray<Map<String, PopupUndoItem>> mPopUndoItems;

    public PopupModule(Context context, PDFViewCtrl pdfViewCtrl, PDFViewCtrl.UIExtensionsManager uiExtensionsManager) {
        mContext = context;
        mPdfViewCtrl = pdfViewCtrl;
        mUIExtensionsManager = (UIExtensionsManager) uiExtensionsManager;
        mPopUndoItems = new SparseArray<>();
    }


    @Override
    public String getName() {
        return MODULE_NAME_POPUP;
    }

    @Override
    public boolean loadModule() {
        mPdfViewCtrl.registerDocEventListener(mDocEventListener);
        mUIExtensionsManager.getDocumentManager().registerAnnotEventListener(mAnnotEventListener);
        mUIExtensionsManager.getDocumentManager().registerUndoEventListener(mUndoEventListener);
        return true;
    }

    @Override
    public boolean unloadModule() {
        mPdfViewCtrl.unregisterDocEventListener(mDocEventListener);
        mUIExtensionsManager.getDocumentManager().unregisterAnnotEventListener(mAnnotEventListener);
        mUIExtensionsManager.getDocumentManager().unregisterUndoEventListener(mUndoEventListener);
        return true;
    }

    private AnnotEventListener mAnnotEventListener = new AnnotEventListener() {
        @Override
        public void onAnnotAdded(PDFPage page, Annot annot) {
            if (!isSupportPopup(annot)) return;

            try {
                int pageIndex = page.getIndex();
                final PopupUndoItem undoItem;
                final String parentNM = AppAnnotUtil.getAnnotUniqueID(annot);
                Map<String, PopupUndoItem> pageUndoItems = mPopUndoItems.get(pageIndex);
                if (pageUndoItems != null && pageUndoItems.get(parentNM) != null) {
                    undoItem = pageUndoItems.get(parentNM);
                } else {
                    undoItem = new PopupUndoItem();
                    undoItem.mFlags = Annot.e_FlagPrint | Annot.e_FlagNoZoom | Annot.e_FlagNoRotate;
                    undoItem.mNM = AppDmUtil.randomUUID(null);
                    undoItem.mParentNM = parentNM;
                    undoItem.mPageIndex = pageIndex;
                    undoItem.mModifiedDate = AppDmUtil.currentDateToDocumentDate();
                }

                PopupEvent popupEvent = new PopupEvent(EditAnnotEvent.EVENTTYPE_ADD, mPdfViewCtrl, annot, undoItem);
                EditAnnotTask task = new EditAnnotTask(popupEvent, null);
                mPdfViewCtrl.addTask(task);
            } catch (PDFException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnnotWillDelete(PDFPage page, Annot annot) {
            if (!isSupportPopup(annot)) return;

            try {
                Popup popup = ((Markup) annot).getPopup();
                if (popup.isEmpty()) return;

                int pageIndex = page.getIndex();
                PopupUndoItem undoItem = new PopupUndoItem();
                undoItem.mFlags = Annot.e_FlagPrint | Annot.e_FlagNoZoom | Annot.e_FlagNoRotate;
                undoItem.mNM = AppAnnotUtil.getAnnotUniqueID(popup);
                String parentNM = AppAnnotUtil.getAnnotUniqueID(annot);
                undoItem.mParentNM = parentNM;
                undoItem.mBBox = AppUtil.toRectF(popup.getRect());
                undoItem.mModifiedDate = popup.getModifiedDateTime();
                undoItem.mContents = popup.getContent();
                undoItem.mPageIndex = pageIndex;

                Map<String, PopupUndoItem> pageUndoItems = mPopUndoItems.get(pageIndex);
                if (pageUndoItems == null) {
                    pageUndoItems = new HashMap<>();
                    mPopUndoItems.put(pageIndex, pageUndoItems);
                }
                pageUndoItems.put(parentNM, undoItem);
            } catch (PDFException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnnotDeleted(PDFPage page, Annot annot) {
        }

        @Override
        public void onAnnotModified(PDFPage page, Annot annot) {
        }

        @Override
        public void onAnnotChanged(Annot lastAnnot, Annot currentAnnot) {
        }
    };

    private AbstractUndo.IUndoEventListener mUndoEventListener = new AbstractUndo.IUndoEventListener() {
        @Override
        public void itemWillAdd(DocumentManager dm, IUndoItem item) {
        }

        @Override
        public void itemAdded(DocumentManager dm, IUndoItem item) {
        }

        @Override
        public void itemWillRemoved(DocumentManager dm, IUndoItem item) {
        }

        @Override
        public void itemRemoved(DocumentManager dm, IUndoItem item) {
            if (mPopUndoItems != null) {
                if (item instanceof AnnotUndoItem) {
                    AnnotUndoItem annotUndoItem = (AnnotUndoItem) item;
                    Map<String, PopupUndoItem> pageUndoItems = mPopUndoItems.get(annotUndoItem.mPageIndex);
                    String nm = annotUndoItem.mNM;
                    if (pageUndoItems != null && pageUndoItems.get(nm) != null) {
                        pageUndoItems.remove(nm);
                    }
                }
            }
        }

        @Override
        public void willUndo(DocumentManager dm, IUndoItem item) {
        }

        @Override
        public void undoFinished(DocumentManager dm, IUndoItem item) {
        }

        @Override
        public void willRedo(DocumentManager dm, IUndoItem item) {
        }

        @Override
        public void redoFinished(DocumentManager dm, IUndoItem item) {
        }

        @Override
        public void willClearUndo(DocumentManager dm) {
        }

        @Override
        public void clearUndoFinished(DocumentManager dm) {
            if (mPopUndoItems != null)
                mPopUndoItems.clear();
        }
    };

    private final PDFViewCtrl.IDocEventListener mDocEventListener = new PDFViewCtrl.IDocEventListener() {
        @Override
        public void onDocWillOpen() {
        }

        @Override
        public void onDocOpened(PDFDoc document, int errCode) {
            if (mPopUndoItems != null)
                mPopUndoItems.clear();
        }

        @Override
        public void onDocWillClose(PDFDoc document) {
        }

        @Override
        public void onDocClosed(PDFDoc document, int errCode) {
        }

        @Override
        public void onDocWillSave(PDFDoc document) {
        }

        @Override
        public void onDocSaved(PDFDoc document, int errCode) {
        }
    };

    private boolean isSupportPopup(Annot annot) {
        try {
            if (annot == null || annot.isEmpty()) return false;

            return annot.isMarkup()
                    && annot.getType() != Annot.e_Sound
                    && annot.getType() != Annot.e_FreeText
                    && (annot.getType() != Annot.e_Note || ((Note) annot).getReplyTo().isEmpty());
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }

}

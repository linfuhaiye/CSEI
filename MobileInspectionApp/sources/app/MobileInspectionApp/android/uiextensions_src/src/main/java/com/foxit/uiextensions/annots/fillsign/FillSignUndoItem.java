package com.foxit.uiextensions.annots.fillsign;


import android.graphics.RectF;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.pdf.FillSignObject;
import com.foxit.uiextensions.IUndoItem;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.utils.thread.AppThreadManager;

import java.util.ArrayList;

abstract class FillSignUndoItem implements IUndoItem {
    int mPageIndex;
    int mType;
    int mRotation;

    RectF mRectF;
    String mContent;
    ArrayList<String> mTexts;

    boolean mIsCombText;

    float mFontSize;
    float mCharspace;
    float mLineHeight;

    FillSignToolHandler mToolHandler;
    private PDFViewCtrl mPDFViewCtrl;
    private FormObject mOldFormObj;
    private FormObject mCurFormObj;

    public FillSignUndoItem(PDFViewCtrl pdfViewCtrl, int type) {
        mType = type;
        mPDFViewCtrl = pdfViewCtrl;
    }

    public FillSignUndoItem(PDFViewCtrl pdfViewCtrl, FillSignToolHandler toolHandler) {
        mToolHandler = toolHandler;
        mPDFViewCtrl = pdfViewCtrl;
    }

    void setOldValue(FormObject obj) {
        mOldFormObj = obj;
    }

    void setCurrentValue(FormObject obj) {
        mCurFormObj = obj;
    }

    void updateFillObj(FillSignObject oldFillSignObject, FillSignObject newFillSignObject) {
        if (mOldFormObj != null && mOldFormObj.mFillObj == oldFillSignObject) {
            mOldFormObj.mFillObj = newFillSignObject;
        }
        if (mCurFormObj != null && mCurFormObj.mFillObj == oldFillSignObject) {
            mCurFormObj.mFillObj = newFillSignObject;
        }
    }

    @Override
    public boolean undo() {
        if (mToolHandler.isEditingText()) {
            mToolHandler.endAddTextBox();

            AppThreadManager.getInstance().getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    ((UIExtensionsManager) mPDFViewCtrl.getUIExtensionsManager()).getDocumentManager().undo();
                }
            });
            return false;
        }
        return true;
    }

    @Override
    public boolean redo() {
        if (mToolHandler.isEditingText()) {
            mToolHandler.endAddTextBox();

            AppThreadManager.getInstance().getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    ((UIExtensionsManager) mPDFViewCtrl.getUIExtensionsManager()).getDocumentManager().redo();
                }
            });
            return false;
        }
        return true;
    }

}

class FillSignModifyUndoItem extends FillSignUndoItem {
    int mUndoType;
    RectF mUndoRectF;
    ArrayList<String> mUndoTexts;
    String mUndoContent;
    float mUndoFontSize;
    float mUndoCharspace;

    int mRedoType;
    RectF mRedoRectF;
    String mRedoContent;
    ArrayList<String> mRedoTexts;
    float mRedoFontSize;
    float mRedoCharspace;

    FillSignModifyUndoItem(PDFViewCtrl pdfViewCtrl, int type) {
        super(pdfViewCtrl, type);
    }

    FillSignModifyUndoItem(PDFViewCtrl pdfViewCtrl, FillSignToolHandler toolHandler) {
        super(pdfViewCtrl, toolHandler);
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean redo() {
        return false;
    }
}

class FillSignDeleteUndoItem extends FillSignUndoItem {

    FillSignDeleteUndoItem(PDFViewCtrl pdfViewCtrl, int type) {
        super(pdfViewCtrl, type);
    }

    FillSignDeleteUndoItem(PDFViewCtrl pdfViewCtrl, FillSignToolHandler toolHandler) {
        super(pdfViewCtrl, toolHandler);
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean redo() {
        return false;
    }
}

class FillSignAddUndoItem extends FillSignUndoItem {

    FillSignAddUndoItem(PDFViewCtrl pdfViewCtrl, int type) {
        super(pdfViewCtrl, type);
    }

    FillSignAddUndoItem(PDFViewCtrl pdfViewCtrl, FillSignToolHandler toolHandler) {
        super(pdfViewCtrl, toolHandler);
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean redo() {
        return false;
    }
}
package com.foxit.uiextensions.annots.fillsign;


import android.graphics.PointF;
import android.graphics.RectF;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.common.Font;
import com.foxit.sdk.pdf.FillSign;
import com.foxit.sdk.pdf.FillSignObject;
import com.foxit.sdk.pdf.TextFillSignObject;
import com.foxit.sdk.pdf.TextFillSignObjectData;
import com.foxit.sdk.pdf.TextFillSignObjectDataArray;
import com.foxit.sdk.pdf.graphics.TextState;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.common.EditAnnotEvent;
import com.foxit.uiextensions.utils.AppUtil;

public class FillSignEvent extends EditAnnotEvent {
    FillSignObject mFillSignObj;
    private FillSignUndoItem mUndoItem;

    public FillSignEvent(int eventType, FillSignObject fillObj, FillSignUndoItem undoItem, PDFViewCtrl pdfViewCtrl) {
        mType = eventType;
        mPdfViewCtrl = pdfViewCtrl;
        mFillSignObj = fillObj;
        mUndoItem = undoItem;
    }

    public FillSignEvent(int eventType, FillSignUndoItem undoItem, PDFViewCtrl pdfViewCtrl) {
        mType = eventType;
        mPdfViewCtrl = pdfViewCtrl;
        mUndoItem = undoItem;
    }

    @Override
    public boolean add() {
        try {
            FillSign fillSign = mUndoItem.mToolHandler.getFillSign(mUndoItem.mPageIndex);
            if (fillSign == null) return false;

            int type = mUndoItem.mType;
            if (type == FillSign.e_FillSignObjectTypeText) {
                RectF rectF = mUndoItem.mRectF;
                PointF pointF = new PointF(rectF.left, rectF.bottom);
                float width = Math.abs(rectF.width());
                float height = Math.abs(rectF.height());

                TextFillSignObjectDataArray objectDataArray = new TextFillSignObjectDataArray();
                int size = mUndoItem.mTexts.size();
                for (int i = 0; i < size; i++) {
                    String text = mUndoItem.mTexts.get(i);

                    TextState textState = new TextState();
                    Font font = new Font(Font.e_StdIDCourier);
                    textState.setFont(font);
                    textState.setFont_size(mUndoItem.mFontSize);
                    textState.setCharspace(mUndoItem.mCharspace);

                    TextFillSignObjectData objectData = new TextFillSignObjectData();
                    objectData.setText_state(textState);
                    objectData.setText(text);
                    objectDataArray.add(objectData);
                }
                FillSignObject fillObject = fillSign.addTextObject(
                        objectDataArray,
                        AppUtil.toFxPointF(pointF),
                        width,
                        height,
                        mUndoItem.mRotation,
                        mUndoItem.mIsCombText);
                TextFillSignObject textFillSignObject = new TextFillSignObject(fillObject);
                textFillSignObject.generateContent();
                mFillSignObj = textFillSignObject;
            } else {
                RectF rectF = mUndoItem.mRectF;
                float width;
                float height;
                if (mUndoItem.mRotation == Constants.e_Rotation90 || mUndoItem.mRotation == Constants.e_Rotation270) {
                    width = Math.abs(rectF.height());
                    height = Math.abs(rectF.width());
                } else {
                    width = Math.abs(rectF.width());
                    height = Math.abs(rectF.height());
                }
                PointF addPointF = new PointF(rectF.left, rectF.bottom);
                FillSignObject fillSignObject = fillSign.addObject(type, AppUtil.toFxPointF(addPointF), width, height, mUndoItem.mRotation);
                fillSignObject.generateContent();
                mFillSignObj = fillSignObject;
            }
            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setDocModified(true);
            return true;
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean modify() {
        try {
            if (mFillSignObj != null && mUndoItem.mType != mFillSignObj.getType()) {
                FillSign fillSign = mUndoItem.mToolHandler.getFillSign(mUndoItem.mPageIndex);
                if (fillSign == null) return false;
                fillSign.removeObject(mFillSignObj);

                float width = Math.abs(mUndoItem.mRectF.width());
                float height = Math.abs(mUndoItem.mRectF.height());
                PointF pointF = new PointF(mUndoItem.mRectF.centerX() - width / 2, mUndoItem.mRectF.centerY() - height / 2);

                int rotation = mUndoItem.mRotation;
                if (rotation == Constants.e_Rotation90 || rotation == Constants.e_Rotation270) {
                    mFillSignObj = fillSign.addObject(mUndoItem.mType, AppUtil.toFxPointF(pointF), height, width, rotation);
                } else {
                    mFillSignObj = fillSign.addObject(mUndoItem.mType, AppUtil.toFxPointF(pointF), width, height, rotation);
                }
                mFillSignObj.generateContent();
                ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setDocModified(true);
                return true;
            }

            if (mUndoItem.mType == FillSign.e_FillSignObjectTypeText) {
                FillSign fillSign = mUndoItem.mToolHandler.getFillSign(mUndoItem.mPageIndex);
                if (fillSign == null) return false;
                if (mFillSignObj != null && !mFillSignObj.isEmpty())
                    fillSign.removeObject(mFillSignObj);

                TextFillSignObjectDataArray objectDataArray = new TextFillSignObjectDataArray();
                int size = mUndoItem.mTexts.size();
                for (int i = 0; i < size; i++) {
                    String text = mUndoItem.mTexts.get(i);

                    TextState textState = new TextState();
                    Font font = new Font(Font.e_StdIDCourier);
                    textState.setFont(font);
                    textState.setFont_size(mUndoItem.mFontSize);
                    textState.setCharspace(mUndoItem.mCharspace);

                    TextFillSignObjectData objectData = new TextFillSignObjectData();
                    objectData.setText_state(textState);
                    objectData.setText(text);
                    objectDataArray.add(objectData);
                }

                FillSignObject fillSignObject;
                RectF undoRectF = mUndoItem.mRectF;
                float width = Math.abs(undoRectF.width());
                float height = Math.abs(undoRectF.height());
                PointF pointF = new PointF(undoRectF.centerX() - width / 2, undoRectF.centerY() - height / 2);
                int rotation = mUndoItem.mRotation;
                if (rotation == Constants.e_Rotation90 || rotation == Constants.e_Rotation270) {
                    fillSignObject = fillSign.addTextObject(
                            objectDataArray,
                            AppUtil.toFxPointF(pointF),
                            height,
                            width,
                            rotation,
                            mUndoItem.mIsCombText);
                } else {
                    fillSignObject = fillSign.addTextObject(
                            objectDataArray,
                            AppUtil.toFxPointF(pointF),
                            width,
                            height,
                            rotation,
                            mUndoItem.mIsCombText);
                }
                TextFillSignObject textFillSignObject = new TextFillSignObject(fillSignObject);
                textFillSignObject.generateContent();
                mFillSignObj = textFillSignObject;
            } else {
                RectF rectF = AppUtil.toRectF(mFillSignObj.getRect());
                RectF undoRectF = mUndoItem.mRectF;
                if (!rectF.equals(undoRectF)) {
                    float width = Math.abs(undoRectF.width());
                    float height = Math.abs(undoRectF.height());
                    PointF pointF = new PointF(undoRectF.centerX() - width / 2, undoRectF.centerY() - height / 2);
                    int rotation = mUndoItem.mRotation;
                    if (rotation == Constants.e_Rotation90 || rotation == Constants.e_Rotation270) {
                        mFillSignObj.move(AppUtil.toFxPointF(pointF), height, width, rotation);
                    } else {
                        mFillSignObj.move(AppUtil.toFxPointF(pointF), width, height, rotation);
                    }
                    mFillSignObj.generateContent();
                }
            }

            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setDocModified(true);
            return true;
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete() {
        try {
            FillSign fillSign = mUndoItem.mToolHandler.getFillSign(mUndoItem.mPageIndex);
            if (fillSign == null) return false;
            fillSign.removeObject(mFillSignObj);
            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setDocModified(true);
            return true;
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }
}

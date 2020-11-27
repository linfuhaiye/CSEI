package com.foxit.uiextensions.annots.fillsign;


import android.graphics.RectF;

import com.foxit.sdk.pdf.FillSignObject;

public class FormObject {

    FillSignObject mFillObj;
    int mTag;
    int mPageIndex;
    RectF mBBox;
    String mContent;
    float mFontSize;
    float mCharspace;
    float mSpacing;

    FormObject(int tag, int pageIndex, RectF bbox) {
        mTag = tag;
        mPageIndex = pageIndex;
        mBBox = bbox;
    }

    @Override
    protected FormObject clone() {
        FormObject newObj = new FormObject(mTag, mPageIndex, new RectF(mBBox));
        newObj.mContent = mContent;
        newObj.mFillObj = mFillObj;
        newObj.mFontSize = mFontSize;
        newObj.mCharspace = mCharspace;
        newObj.mSpacing = mSpacing;
        return newObj;
    }

}

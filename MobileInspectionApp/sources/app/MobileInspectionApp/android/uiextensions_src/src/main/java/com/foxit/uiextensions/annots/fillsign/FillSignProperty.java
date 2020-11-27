package com.foxit.uiextensions.annots.fillsign;

import android.content.Context;
import android.graphics.PointF;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.uiextensions.utils.AppDisplay;

class FillSignProperty {
    static final float MIN_FONTSIZE = 6.0f;
    static final float MIN_CHECKSIZE = 10.0f;
    static final float MIN_LINESIZE = 30.0f;
    static final float MIN_RECT_X = 30.0f;
    static final float MIN_RECT_Y = 15.0f;

    float mFontSize = 12.0f; // pt in pdf document unit
    float mFontSpacing = 0.5f; // constant
    float mPadding;

    float mCheckSize = 16.0f;
    PointF mLineSize = new PointF(45, 15);
    PointF mRectSize = new PointF(45, 30);

    float mZoomScale = 1.10f;
    private Context mContext;

    FillSignProperty(Context context) {
        mContext = context;
        mPadding = AppDisplay.getInstance(context).dp2px(5); // 5dp in device space
    }

    void setFontSize(float size) {
        if (size < MIN_FONTSIZE)
            size = MIN_FONTSIZE;
        mFontSize = size;
    }

    float getFontSizeDp(PDFViewCtrl pdfViewCtrl, int pageIndex) {
        float size = FillSignUtils.docToPageViewThickness(pdfViewCtrl, pageIndex, mFontSize);
        size = AppDisplay.getInstance(mContext).px2dp(size);
        return size;
    }

    float getFontSizeDp(PDFViewCtrl pdfViewCtrl, int pageIndex, float docFontSize) {
        float size = FillSignUtils.docToPageViewThickness(pdfViewCtrl, pageIndex, docFontSize);
        size = AppDisplay.getInstance(mContext).px2dp(size);
        return size;
    }

    void setFontSpacing(float spacing) {
        if (spacing < 0.01)
            spacing = 0;
        if (spacing > 100)
            spacing = 100;
        mFontSpacing = spacing;
    }

    void setCheckSize(float size) {
        if (size < MIN_CHECKSIZE)
            size = MIN_CHECKSIZE;
        mCheckSize = size;
    }

    void setLineSize(PointF size) {
        if (size.x < MIN_LINESIZE)
            size.x = MIN_LINESIZE;
        mLineSize.x = size.x;
    }

    void setRectSize(PointF size) {
        if (size.x < MIN_RECT_X)
            size.x = MIN_RECT_X;
        if (size.y < MIN_RECT_Y)
            size.y = MIN_RECT_Y;
        mRectSize.x = size.x;
        mRectSize.y = size.y;
    }

}

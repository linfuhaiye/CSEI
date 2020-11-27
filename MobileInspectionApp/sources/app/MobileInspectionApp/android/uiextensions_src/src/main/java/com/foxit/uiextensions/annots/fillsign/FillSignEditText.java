package com.foxit.uiextensions.annots.fillsign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.foxit.uiextensions.R;
import com.foxit.uiextensions.pdfreader.config.AppBuildConfig;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppResource;
import com.foxit.uiextensions.utils.AppUtil;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class FillSignEditText extends EditText {
    public static final int STYLE_TEXT = 0;
    public static final int STYLE_COMBO_TEXT = 1;

    static final int SPACING = 20;
    int mTextStyle;
    int mPageIndex;
    Point mPvSize;
    PointF mOffsetPt;
    FillSignProperty mProperty;
    private float mMarginRight;

    public LineTexts mLineTexts;
    private Context mContext;

    public FillSignEditText(Context context) {
        super(context);
        init(context);
    }

    public FillSignEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FillSignEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FillSignEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    void init(Context context) {
        mLineTexts = new LineTexts(this);
        mOffsetPt = new PointF();
        mPvSize = new Point();
        mContext = context;
    }

    void setProperty(FillSignProperty property) {
        mProperty = property;
    }

    @Override
    public void setTypeface(@Nullable Typeface tf) {
        super.setTypeface(tf);
        if (mPaint != null)
            mPaint.setTypeface(tf);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        if (mPaint != null)
            mPaint.setTextSize(AppDisplay.getInstance(mContext).dp2px(size));
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    @Override
    public void setLetterSpacing(float letterSpacing) {
        if (AppBuildConfig.SDK_VERSION >= 21) {
            super.setLetterSpacing(letterSpacing);
        }
    }

    public void splitTextLines() {
        mLineTexts.splitLines();
    }

    public void setTextStyle(int style) {
        mTextStyle = style;
    }

    public int getTextStyle() {
        return mTextStyle;
    }

    public void setPageIndex(int pageIndex) {
        mPageIndex = pageIndex;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPvSize(Point size) {
        mPvSize.set(size.x, size.y);
    }


    void setMarginRight(float margin) {
        this.mMarginRight = margin;
    }

    public Point getPvSize() {
        return mPvSize;
    }

    public void setDocLtOffset(PointF pt) {
        mOffsetPt.set(pt);
    }

    public PointF getDocLtOffset() {
        return mOffsetPt;
    }

    @Override
    public void setWidth(int pixels) {
        super.setWidth(pixels);
    }

    @Override
    public void setHeight(int pixels) {
        super.setHeight(pixels);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    Bitmap mBitmap;
    String mBmpStr;
    Paint mPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            return;
        }

        int scrollY = getScrollY();
        int totalHeight = getHeight() + scrollY;

        if (!AppUtil.isEqual(mBmpStr, getText())) {
            mBmpStr = getText().toString();
        }

        // draw divider
        if (mTextStyle == STYLE_COMBO_TEXT) {
            splitTextLines();

            Paint etPaint = getPaint();

            ArrayList<String> lineTexts = mLineTexts.mLineTexts;
            int maxCount = mLineTexts.getMaxLineChars();
            float[] cWidths = new float[maxCount];
            float[] cTmpWidths = new float[1];

            int cIndex = 0;
            for (int i = 0; i < lineTexts.size(); i++) {
                String str = lineTexts.get(i);
                if (str.length() > cIndex) {
                    for (int j = cIndex; j < str.length(); j++) {
                        if (str.charAt(cIndex) == '\n')
                            continue;
                        etPaint.getTextWidths(str, cIndex, cIndex + 1, cTmpWidths);
                        cWidths[cIndex] = cTmpWidths[0];
                        cIndex++;
                    }
                    if (cIndex >= maxCount) {
                        break;
                    }
                }
            }

            float x = 0;
            for (int i = 0; i < maxCount; i++) {
                x += cWidths[i];
                canvas.drawLine(x, 0, x, getHeight() + scrollY, mPaint);
            }

            // draw border
            float borderWidth = AppDisplay.getInstance(mContext).dp2px(1);
            RectF border = new RectF(borderWidth / 2, borderWidth / 2 + scrollY, getWidth() - getPaddingRight() / 2, getHeight() + scrollY - borderWidth / 2);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(borderWidth);
            mPaint.setColor(AppResource.getColor(mContext, R.color.ux_color_blue_ff179cd8, null));
            canvas.drawRect(border, mPaint);

            if (getText().toString().length() >= 2) {
                Bitmap stretchBmp = getStratchBmp();
                canvas.drawBitmap(stretchBmp,
                        getWidth() - stretchBmp.getWidth(),
                        (getHeight() - stretchBmp.getHeight()) / 2,
                        mPaint);
            }
        } else {
            // draw border
            float borderWidth = AppDisplay.getInstance(mContext).dp2px(1);
            RectF border = new RectF(borderWidth / 2, borderWidth / 2 + scrollY, getWidth() - borderWidth / 2, getHeight() + scrollY - borderWidth / 2);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(borderWidth);
            mPaint.setColor(AppResource.getColor(mContext, R.color.ux_color_blue_ff179cd8, null));
            canvas.drawRect(border, mPaint);
        }
    }

    boolean mCaptured;
    PointF mLastPt = new PointF();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        PointF rawPt = new PointF(event.getRawX(), event.getRawY());

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (getText().toString().length() >= 2) {
                    PointF pt = new PointF(event.getX(), event.getY());
                    RectF bmpArea = getStretchBmpArea();
                    if (mTextStyle == STYLE_COMBO_TEXT && bmpArea.contains(pt.x, pt.y)) {
                        mLastPt.set(rawPt);
                        mCaptured = true;
                        splitTextLines();
                        return true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP: {
                if (mCaptured) {
                    if (AppBuildConfig.SDK_VERSION >= 21 && rawPt.x != mLastPt.x) {
                        ArrayList<String> lineTexts = mLineTexts.mLineTexts;

                        float lineMaxWidth = 0;
                        String maxText = "";
                        for (int i = 0; i < lineTexts.size(); i++) {
                            String text = lineTexts.get(i);
                            float width = mPaint.measureText(text);
                            if (width > lineMaxWidth) {
                                lineMaxWidth = width;
                                maxText = text;
                            }
                        }

                        maxText = maxText.replace("\n", "");
                        float dx = rawPt.x - mLastPt.x;
                        float textSize = getTextSize();
                        float spacing = getLetterSpacing();

                        if (spacing == 0) {
                            if (dx > 0 && maxText.length() > 0 && textSize > 0) {
                                spacing = dx / maxText.length() / textSize;
                                mProperty.setFontSpacing(spacing);
                                setLetterSpacing(mProperty.mFontSpacing);
                            }
                        } else {
                            float spacingSize = textSize * spacing;
                            float totalSpacing = spacingSize * maxText.length();
                            if (totalSpacing > 0) {
                                spacing = spacing * (totalSpacing + dx) / totalSpacing;

                                float actualWidth = textSize * spacing * maxText.length() + lineMaxWidth;
                                if (dx > 0 && (actualWidth + getPaddingRight() > getMaxWidth() - mMarginRight))
                                    return true;

                                mProperty.setFontSpacing(spacing);
                                setLetterSpacing(mProperty.mFontSpacing);
                            }
                        }

                        mLastPt.set(rawPt);
                    }

                    if (action == MotionEvent.ACTION_UP) {
                        mCaptured = false;
                    }
                    return true;
                }
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    public static class LineTexts {
        FillSignEditText mFxEdit;
        public ArrayList<String> mLineTexts = new ArrayList<>();

        LineTexts(FillSignEditText fxEdit) {
            mFxEdit = fxEdit;
        }

        public float getMaxLineWidth() {
            Paint etPaint = mFxEdit.getPaint();
            float maxWidth = 0;
            for (int i = 0; i < mLineTexts.size(); i++) {
                float width = etPaint.measureText(mLineTexts.get(i));
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            return maxWidth;
        }

        public int getMaxLineChars() {
            int maxWidth = 0;
            for (int i = 0; i < mLineTexts.size(); i++) {
                String line = mLineTexts.get(i);
                if (line.length() > maxWidth) {
                    maxWidth = line.length();
                }
            }
            return maxWidth;
        }

        public String getMaxLineString() {
            int maxWidth = 0;
            int index = 0;
            for (int i = 0; i < mLineTexts.size(); i++) {
                String line = mLineTexts.get(i);
                if (line.length() > maxWidth) {
                    maxWidth = line.length();
                    index = i;
                }
            }
            return mLineTexts.get(index);
        }

        void splitLines() {
            mLineTexts.clear();

            Layout layout = mFxEdit.getLayout();
            String text = mFxEdit.getText().toString();
            int start = 0;
            int end;
            for (int i = 0, count = mFxEdit.getLineCount(); i < count; i++) {
                end = layout.getLineEnd(i);

                String line = text.substring(start, end);
                start = end;

                mLineTexts.add(line);
            }
        }
    }

    Bitmap mStretchBmp = null;

    Bitmap getStratchBmp() {
        if (mStretchBmp == null) {
            Drawable drawable = AppResource.getDrawable(mContext, R.drawable.fillsign_stratch);
            if (drawable.getCurrent() instanceof BitmapDrawable) {
                mStretchBmp = ((BitmapDrawable) drawable.getCurrent()).getBitmap();
            }
        }
        return mStretchBmp;
    }

    RectF getStretchBmpArea() {
        Bitmap bmp = getStratchBmp();
        RectF rect = new RectF();
        rect.left = getWidth() - bmp.getWidth();
        rect.top = (getHeight() - bmp.getHeight()) / 2;
        rect.right = rect.left + bmp.getWidth();
        rect.bottom = rect.top + bmp.getHeight();
        return rect;
    }

}

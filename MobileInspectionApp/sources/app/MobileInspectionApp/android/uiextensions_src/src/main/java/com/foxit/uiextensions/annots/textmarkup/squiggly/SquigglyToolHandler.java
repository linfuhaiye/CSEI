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
package com.foxit.uiextensions.annots.textmarkup.squiggly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.TextPage;
import com.foxit.sdk.pdf.annots.Annot;
import com.foxit.sdk.pdf.annots.QuadPoints;
import com.foxit.sdk.pdf.annots.QuadPointsArray;
import com.foxit.sdk.pdf.annots.Squiggly;
import com.foxit.uiextensions.R;
import com.foxit.uiextensions.ToolHandler;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.common.EditAnnotEvent;
import com.foxit.uiextensions.annots.common.EditAnnotTask;
import com.foxit.uiextensions.annots.textmarkup.TextMarkupUtil;
import com.foxit.uiextensions.controls.propertybar.MoreTools;
import com.foxit.uiextensions.controls.propertybar.PropertyBar;
import com.foxit.uiextensions.controls.toolbar.BaseBar;
import com.foxit.uiextensions.controls.toolbar.IBaseItem;
import com.foxit.uiextensions.controls.toolbar.PropertyCircleItem;
import com.foxit.uiextensions.controls.toolbar.ToolbarItemConfig;
import com.foxit.uiextensions.controls.toolbar.impl.BaseItemImpl;
import com.foxit.uiextensions.controls.toolbar.impl.PropertyCircleItemImp;
import com.foxit.uiextensions.pdfreader.config.ReadStateConfig;
import com.foxit.uiextensions.utils.AppAnnotUtil;
import com.foxit.uiextensions.utils.AppDmUtil;
import com.foxit.uiextensions.utils.AppUtil;
import com.foxit.uiextensions.utils.Event;

import java.util.ArrayList;

public class SquigglyToolHandler implements ToolHandler {

    private Context mContext;

    private Paint mPaint;
    private int mColor;
    private int mCurrentIndex;
    private int mOpacity;
    public SelectInfo mSelectInfo;
    private RectF mTmpRect;
    private Path mPath;

    private PropertyCircleItem mPropertyItem;
    private IBaseItem mOKItem;
    private IBaseItem mContinuousCreateItem;

    private PropertyBar mPropertyBar;
    private PropertyBar.PropertyChangeListener mPropertyChangeListener;

    private PDFViewCtrl mPdfViewCtrl;
    private UIExtensionsManager mUiextensionsManager;

    private boolean mIsContinuousCreate = false;

    public class SelectInfo {
        public boolean mIsFromTS;
        public int mStartChar;
        public int mEndChar;
        public RectF mBBox;
        public ArrayList<RectF> mRectArray;
        public ArrayList<Boolean> mRectVert;
        public ArrayList<Integer> mRotation;

        public SelectInfo() {
            mBBox = new RectF();
            mRectArray = new ArrayList<RectF>();
            mRectVert = new ArrayList<Boolean>();
            mRotation = new ArrayList<Integer>();
        }

        public void clear() {
            mIsFromTS = false;
            mStartChar = mEndChar = -1;
            mBBox.setEmpty();
            mRectArray.clear();
        }
    }

    public SquigglyToolHandler(Context context, PDFViewCtrl pdfViewCtrl) {
        mContext = context;
        mPdfViewCtrl = pdfViewCtrl;

        init();
    }

    void setPropertyChangeListener(PropertyBar.PropertyChangeListener propertyChangeListener) {
        mPropertyChangeListener = propertyChangeListener;
    }

    private void resetLineData() {
        mSelectInfo.mStartChar = mSelectInfo.mEndChar = -1;
        mSelectInfo.mRectArray.clear();
        mSelectInfo.mBBox.setEmpty();
        mTmpRect.setEmpty();
    }

    private void init() {
        mSelectInfo = new SelectInfo();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        mTmpRect = new RectF();
        mPath = new Path();

        mUiextensionsManager = (UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager();
        //PropertyBar
        mPropertyBar = mUiextensionsManager.getMainFrame().getPropertyBar();

        mUiextensionsManager.getMainFrame().getMoreToolsBar().registerListener(new MoreTools.IMT_MoreClickListener() {
            @Override
            public void onMTClick(int type) {
                mUiextensionsManager.setCurrentToolHandler(SquigglyToolHandler.this);
                mUiextensionsManager.changeState(ReadStateConfig.STATE_ANNOTTOOL);
            }

            @Override
            public int getType() {
                return MoreTools.MT_TYPE_SQUIGGLY;
            }
        });
    }

    protected void unInit() {

    }

    @Override
    public String getType() {
        return ToolHandler.TH_TYPE_SQUIGGLY;
    }

    @Override
    public void onActivate() {
        resetLineData();
        resetPropertyBar();
        resetAnnotBar();
    }

    @Override
    public void onDeactivate() {
    }


    private String getContent(PDFPage page, SelectInfo selectInfo) {
        int start = selectInfo.mStartChar;
        int end = selectInfo.mEndChar;
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        String content = null;
        try {
            TextPage textPage = new TextPage(page,TextPage.e_ParseTextNormal);

            content = textPage.getChars(start, end - start + 1);
        } catch (PDFException e) {
            if (e.getLastError() == Constants.e_ErrOutOfMemory) {
                mPdfViewCtrl.recoverForOOM();
            }
        }
        return content;
    }

    protected void addAnnot(final int pageIndex, final boolean addUndo, final ArrayList<RectF> rectArray, final RectF rectF, final SelectInfo selectInfo, final Event.Callback result) {
        PDFPage page = null;
        Squiggly annot = null;
        try {
            page = mUiextensionsManager.getDocumentManager().getPage(pageIndex, false);
            if (page == null) return;
            annot = (Squiggly) AppAnnotUtil.createAnnot(page.addAnnot(Annot.e_Squiggly, AppUtil.toFxRectF(rectF)), Annot.e_Squiggly);
            if (annot == null) {
                if (!misFromSelector) {
                    if (!mIsContinuousCreate) {
                        ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).setCurrentToolHandler(null);
                    }
                }
                misFromSelector = false;
                return;
            }


        } catch (PDFException e) {
            if (e.getLastError() == Constants.e_ErrOutOfMemory) {
                mPdfViewCtrl.recoverForOOM();
            }
            return;
        }

        final SquigglyAddUndoItem undoItem = new SquigglyAddUndoItem(mPdfViewCtrl);
        undoItem.mType = Annot.e_Squiggly;
        undoItem.mColor = mColor;
        undoItem.mCreationDate = AppDmUtil.currentDateToDocumentDate();
        undoItem.mModifiedDate = AppDmUtil.currentDateToDocumentDate();
        undoItem.mQuadPoints = new QuadPointsArray();
        for (int i = 0; i < rectArray.size(); i++) {
            if (i < selectInfo.mRectVert.size()) {
                RectF rF = new RectF();
                mPdfViewCtrl.convertPageViewRectToPdfRect(rectArray.get(i), rF, pageIndex);
                QuadPoints quadPoint = new QuadPoints();
                if (selectInfo.mRectVert.get(i)) {
                    quadPoint.setFirst(AppUtil.toFxPointF(rF.left, rF.top));
                    quadPoint.setSecond(AppUtil.toFxPointF(rF.left, rF.bottom));
                    quadPoint.setThird(AppUtil.toFxPointF(rF.right, rF.top));
                    quadPoint.setFourth(AppUtil.toFxPointF(rF.right, rF.bottom));
                } else {
                    quadPoint.setFirst(AppUtil.toFxPointF(rF.left, rF.top));
                    quadPoint.setSecond(AppUtil.toFxPointF(rF.right, rF.top));
                    quadPoint.setThird(AppUtil.toFxPointF(rF.left, rF.bottom));
                    quadPoint.setFourth(AppUtil.toFxPointF(rF.right, rF.bottom));
                }
                undoItem.mQuadPoints.add(quadPoint);
            }
        }

        undoItem.mContents = getContent(page, selectInfo);
        undoItem.mNM = AppDmUtil.randomUUID(null);
        undoItem.mSubject = "Squiggly";
        undoItem.mAuthor = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getAnnotAuthor();
        undoItem.mFlags = 4;
        undoItem.mOpacity = mOpacity / 255f;
        undoItem.mPageIndex = pageIndex;

        SquigglyEvent event = new SquigglyEvent(EditAnnotEvent.EVENTTYPE_ADD, undoItem, annot, mPdfViewCtrl);
        final Squiggly finalAnnot = annot;
        final PDFPage finalPage = page;
        EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
            @Override
            public void result(Event event, boolean success) {
                if (success) {
                    ((UIExtensionsManager)mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().onAnnotAdded(finalPage, finalAnnot);
                    if (addUndo) {
                        ((UIExtensionsManager)mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().addUndoItem(undoItem);
                    }

                    if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                        invalidate(pageIndex, rectF, result);
                    }

                    resetLineData();

                    if (!misFromSelector) {
                        if (!mIsContinuousCreate) {
                            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).setCurrentToolHandler(null);
                        }
                    }
                    misFromSelector = false;
                }
            }
        });
        mPdfViewCtrl.addTask(task);
    }

    private boolean misFromSelector = false;

    protected void setFromSelector(boolean b) {
        misFromSelector = b;
    }

    private void invalidate(int pageIndex, RectF dmRectF, final Event.Callback result) {
        if (dmRectF == null) {
            if (result != null) {
                result.result(null, true);
            }
            return;
        }
        RectF rectF = new RectF();
        mPdfViewCtrl.convertPdfRectToPageViewRect(dmRectF, rectF, pageIndex);
        Rect rect = new Rect();
        rectF.roundOut(rect);
        mPdfViewCtrl.refresh(pageIndex, rect);

        if (null != result) {
            result.result(null, false);
        }
    }

    protected void selectCountRect(int pageIndex, SelectInfo selectInfo) {
        if (selectInfo == null) return;

        int start = selectInfo.mStartChar;
        int end = selectInfo.mEndChar;
        if (start == end && start == -1) return;
        if (end < start) {
            int tmp = end;
            end = start;
            start = tmp;
        }

        selectInfo.mRectArray.clear();
        selectInfo.mRectVert.clear();

        try {
            PDFPage page = mUiextensionsManager.getDocumentManager().getPage(pageIndex, false);
            if (page == null) return;
            TextPage textPage = new TextPage(page, TextPage.e_ParseTextNormal);

            int count = textPage.getTextRectCount(start, end - start + 1);

            for (int i = 0; i < count; i++) {
                RectF crect = new RectF(AppUtil.toRectF(textPage.getTextRect(i)));
                mPdfViewCtrl.convertPdfRectToPageViewRect(crect, crect, pageIndex);
                int rotate = textPage.getBaselineRotation(i);
                boolean vert = rotate == 1 || rotate == 3;

                selectInfo.mRectVert.add(vert);
                selectInfo.mRectArray.add(crect);
                selectInfo.mRotation.add(rotate);
                if (i == 0) {
                    selectInfo.mBBox = new RectF(crect);
                } else {
                    reSizeRect(selectInfo.mBBox, crect);
                }
            }
        } catch (PDFException e) {
            if (e.getLastError() == Constants.e_ErrOutOfMemory) {
                mPdfViewCtrl.recoverForOOM();
            }
        }
    }

    private void reSizeRect(RectF MainRt, RectF rect) {
        if (rect.left < MainRt.left) MainRt.left = rect.left;
        if (rect.right > MainRt.right) MainRt.right = rect.right;
        if (rect.bottom > MainRt.bottom) MainRt.bottom = rect.bottom;
        if (rect.top < MainRt.top) MainRt.top = rect.top;
    }

    private boolean onSelectDown(int pageIndex, PointF point, SelectInfo selectInfo) {
        if (selectInfo == null) return false;
        mCurrentIndex = pageIndex;
        selectInfo.mRectArray.clear();
        selectInfo.mStartChar = selectInfo.mEndChar = -1;
        try {
            PDFPage page = mUiextensionsManager.getDocumentManager().getPage(pageIndex, false);
            if (page == null) return false;
            TextPage textPage = new TextPage(page, TextPage.e_ParseTextNormal);

            PointF pagePt = new PointF();
            mPdfViewCtrl.convertPageViewPtToPdfPt(point, pagePt, pageIndex);

            int index = textPage.getIndexAtPos(pagePt.x, pagePt.y, 10);
            if (index >= 0) {
                selectInfo.mStartChar = selectInfo.mEndChar = index;
            }
        } catch (PDFException e) {
            if (e.getLastError() == Constants.e_ErrOutOfMemory) {
                mPdfViewCtrl.recoverForOOM();
            }
            return false;
        }
        return true;
    }

    private boolean onSelectMove(int pageIndex, PointF point, SelectInfo selectInfo) {
        if (selectInfo == null) return false;
        if (mCurrentIndex != pageIndex) return false;
        try {
            PDFPage page = mUiextensionsManager.getDocumentManager().getPage(pageIndex, false);
            if (page == null) {
                return false;
            }
            TextPage textPage = new TextPage(page, TextPage.e_ParseTextNormal);

            PointF pagePt = new PointF();
            mPdfViewCtrl.convertPageViewPtToPdfPt(point, pagePt, mCurrentIndex);

            int index = textPage.getIndexAtPos(pagePt.x, pagePt.y, 10);
            if (index >= 0) {
                if (selectInfo.mStartChar < 0) selectInfo.mStartChar = index;
                selectInfo.mEndChar = index;
            }
        } catch (PDFException e) {
            if (e.getLastError() == Constants.e_ErrOutOfMemory) {
                mPdfViewCtrl.recoverForOOM();
            }
            return false;
        }
        return true;
    }

    protected boolean onSelectRelease(int pageIndex, SelectInfo selectInfo, Event.Callback result) {
        if (selectInfo == null) return false;
        int size = mSelectInfo.mRectArray.size();
        if (size == 0) return false;
        RectF rectF = new RectF();

        mPdfViewCtrl.convertPageViewRectToPdfRect(mSelectInfo.mBBox, rectF, pageIndex);
        addAnnot(pageIndex, true, mSelectInfo.mRectArray, rectF, selectInfo, result);

        return true;
    }

    private void invalidateTouch(SelectInfo selectInfo, int pageIndex) {
        if (selectInfo == null) return;
        RectF rectF = new RectF();
        rectF.set(mSelectInfo.mBBox);
        mPdfViewCtrl.convertPageViewRectToDisplayViewRect(rectF, rectF, pageIndex);
        RectF rF = AppUtil.calculateRect(rectF, mTmpRect);
        Rect rect = new Rect();
        rF.roundOut(rect);
        rect.bottom += 4;
        rect.top -= 4;
        rect.left -= 4;
        rect.right += 4;
        mPdfViewCtrl.invalidate(rect);
        mTmpRect.set(rectF);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(int pageIndex, MotionEvent motionEvent) {
        PointF point = AppAnnotUtil.getPageViewPoint(mPdfViewCtrl, pageIndex, motionEvent);
        int action = motionEvent.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onSelectDown(pageIndex, point, mSelectInfo);
                break;
            case MotionEvent.ACTION_MOVE:
                onSelectMove(pageIndex, point, mSelectInfo);
                selectCountRect(pageIndex, mSelectInfo);
                invalidateTouch(mSelectInfo, pageIndex);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onSelectRelease(pageIndex, mSelectInfo, null);
                return true;
            default:
                break;
        }
        return true;

    }

    @Override
    public boolean onLongPress(int pageIndex, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(int pageIndex, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean isContinueAddAnnot() {
        return mIsContinuousCreate;
    }

    @Override
    public void setContinueAddAnnot(boolean continueAddAnnot) {
        mIsContinuousCreate = continueAddAnnot;
    }

    @Override
    public void onDraw(int pageIndex, Canvas canvas) {
        if (mCurrentIndex != pageIndex || mSelectInfo.mRectArray.size() == 0) return;
        Rect clipRect = canvas.getClipBounds();

        int i = 0;
        PointF startPointF = new PointF();
        PointF endPointF = new PointF();
        RectF widthRect = new RectF();
        int pageRotation;
        try {
            pageRotation = mPdfViewCtrl.getDoc().getPage(pageIndex).getRotation();
        } catch (PDFException e) {
            pageRotation = 0;
        }

        for (RectF rect : mSelectInfo.mRectArray) {
            Rect r = new Rect();
            rect.round(r);
            if (r.intersect(clipRect)) {

                RectF tmpF = new RectF();
                tmpF.set(rect);

                if (i < mSelectInfo.mRectVert.size()) {
                    int rotation = (mSelectInfo.mRotation.get(i) + pageRotation + mPdfViewCtrl.getViewRotation()) % 4;
                    boolean vert = rotation == 1 || rotation == 3;
                    mPdfViewCtrl.convertPageViewRectToPdfRect(rect, widthRect, pageIndex);

                    //reset Paint width
                    if ((widthRect.top - widthRect.bottom) > (widthRect.right - widthRect.left)) {
                        TextMarkupUtil.resetDrawLineWidth(mPdfViewCtrl, pageIndex, mPaint, widthRect.right, widthRect.left);
                    } else {
                        TextMarkupUtil.resetDrawLineWidth(mPdfViewCtrl, pageIndex, mPaint, widthRect.top, widthRect.bottom);
                    }

                    if (vert) {
                        if (rotation == 3) {
                            startPointF.x = tmpF.right - (tmpF.right - tmpF.left) / 8f;
                        } else {
                            startPointF.x = tmpF.left + (tmpF.right - tmpF.left) / 8f;
                        }

                        startPointF.y = tmpF.top;
                        endPointF.x = startPointF.x;
                        endPointF.y = tmpF.bottom;
                    } else {
                        if (rotation == 0) {
                            startPointF.y = tmpF.bottom + (tmpF.bottom - tmpF.top) / 8f;
                        } else {
                            startPointF.y = tmpF.top - (tmpF.bottom - tmpF.top) / 8f;
                        }
                        startPointF.x = tmpF.left;
                        endPointF.x = tmpF.right;
                        endPointF.y = startPointF.y;
                    }

                    canvas.save();
                    drawSquiggly(canvas, startPointF.x, startPointF.y, endPointF.x, endPointF.y, mPaint);
                    canvas.restore();
                }
            }
            i++;
        }
    }

    protected void setPaint(int color, int opacity) {
        mColor = color;
        mOpacity = opacity;
        mPaint.setColor(mColor);
        mPaint.setAlpha(mOpacity);
        setProItemColor(color);
    }

    private void setProItemColor(int color){
        if (mPropertyItem == null) return;
        mPropertyItem.setCentreCircleColor(color);
    }

    private void resetPropertyBar() {
        int[] colors = new int[PropertyBar.PB_COLORS_SQUIGGLY.length];
        long supportProperty = PropertyBar.PROPERTY_COLOR | PropertyBar.PROPERTY_OPACITY;
        System.arraycopy(PropertyBar.PB_COLORS_SQUIGGLY, 0, colors, 0, colors.length);
        colors[0] = PropertyBar.PB_COLORS_SQUIGGLY[0];
        mPropertyBar.setColors(colors);
        mPropertyBar.setProperty(PropertyBar.PROPERTY_COLOR, mColor);
        mPropertyBar.setProperty(PropertyBar.PROPERTY_OPACITY, AppDmUtil.opacity255To100(mOpacity));
        mPropertyBar.reset(supportProperty);
        mPropertyBar.setPropertyChangeListener(mPropertyChangeListener);
    }

    private void resetAnnotBar(){
        mUiextensionsManager.getMainFrame().getToolSetBar().removeAllItems();

        mOKItem = new BaseItemImpl(mContext);
        mOKItem.setTag(ToolbarItemConfig.ITEM_ANNOT_BAR_OK);
        mOKItem.setImageResource(R.drawable.rd_annot_create_ok_selector);
        mOKItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUiextensionsManager.changeState(ReadStateConfig.STATE_EDIT);
                mUiextensionsManager.setCurrentToolHandler(null);
            }
        });

        mPropertyItem = new PropertyCircleItemImp(mContext) {

            @Override
            public void onItemLayout(int l, int t, int r, int b) {

                if (SquigglyToolHandler.this == mUiextensionsManager.getCurrentToolHandler()) {
                    if (mPropertyBar.isShowing()) {
                        Rect rect = new Rect();
                        mPropertyItem.getContentView().getGlobalVisibleRect(rect);
                        mPropertyBar.update(new RectF(rect));
                    }
                }
            }
        };
        mPropertyItem.setTag(ToolbarItemConfig.ITEM_ANNOT_PROPERTY);
        mPropertyItem.setCentreCircleColor(mColor);

        final Rect rect = new Rect();
        mPropertyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPropertyBar.setArrowVisible(true);
                mPropertyItem.getContentView().getGlobalVisibleRect(rect);
                mPropertyBar.show(new RectF(rect), true);
            }
        });

        mContinuousCreateItem = new BaseItemImpl(mContext);
        mContinuousCreateItem.setTag(ToolbarItemConfig.ITEM_ANNOT_BAR_CONTINUE);
        mContinuousCreateItem.setImageResource(getContinuousIcon(mIsContinuousCreate));

        mContinuousCreateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isFastDoubleClick()) {
                    return;
                }

                mIsContinuousCreate = !mIsContinuousCreate;
                mContinuousCreateItem.setImageResource(getContinuousIcon(mIsContinuousCreate));
                AppAnnotUtil.getInstance(mContext).showAnnotContinueCreateToast(mIsContinuousCreate);
            }
        });

        mUiextensionsManager.getMainFrame().getToolSetBar().addView(mPropertyItem, BaseBar.TB_Position.Position_CENTER);
        mUiextensionsManager.getMainFrame().getToolSetBar().addView(mOKItem, BaseBar.TB_Position.Position_CENTER);
        mUiextensionsManager.getMainFrame().getToolSetBar().addView(mContinuousCreateItem, BaseBar.TB_Position.Position_CENTER);
    }

    private int getContinuousIcon(boolean isContinuous){
        int iconId;
        if (isContinuous) {
            iconId = R.drawable.rd_annot_create_continuously_true_selector;
        } else {
            iconId = R.drawable.rd_annot_create_continuously_false_selector;
        }
        return iconId;
    }

    private void drawSquiggly(Canvas canvas, float x, float y, float x2,
                              float y2, Paint paint) {

        PointF step = new PointF((x2 - x) / 8, (y2 - y) / 8);
        TextMarkupUtil.stepNormalize(step, mPaint.getStrokeWidth());
        PointF step1 = new PointF();
        PointF step2 = new PointF();
        step1.set(step);
        step2.set(step);

        TextMarkupUtil.stepRotate(-TextMarkupUtil.SQGPI / 2.0, step1);
        TextMarkupUtil.stepRotate(TextMarkupUtil.SQGPI / 2.0, step2);

        mPath.moveTo(x + step1.x, y + step1.y);
        int i = 1;
        float fX = x, fY = y;
        float fMinX = (x < x2) ? x : x2;
        float fMaxX = (x > x2) ? x : x2;
        float fMinY = (y < y2) ? y : y2;
        float fMaxY = (y > y2) ? y : y2;

        if (x == x2) {
            //Vertical
            fX += step.x * 2.0f;
            while ((fX >= fMinX && fX <= fMaxX) && (fY >= fMinY && fY <= fMaxY)) {
                fY += step.y * 2.0f;
                mPath.lineTo(fX + ((i % 2) == 0 ? step1.x : step2.x), fY + ((i % 2) == 0 ? step1.y : step2.y));
                i++;
            }
        } else {
            //horizontal
            fY += step.y * 2.0f;
            while ((fX >= fMinX && fX <= fMaxX) && (fY >= fMinY && fY <= fMaxY)) {
                fX += step.x * 2.0f;
                mPath.lineTo(fX + ((i % 2) == 0 ? step1.x : step2.x), fY + ((i % 2) == 0 ? step1.y : step2.y));
                i++;
            }
        }

        canvas.drawPath(mPath, paint);
        mPath.rewind();
    }

    protected void removeProbarListener() {
        mPropertyChangeListener = null;
    }

}

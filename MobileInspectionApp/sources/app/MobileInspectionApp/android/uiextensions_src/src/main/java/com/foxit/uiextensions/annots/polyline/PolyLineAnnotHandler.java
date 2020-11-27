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
package com.foxit.uiextensions.annots.polyline;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.common.fxcrt.PointFArray;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.annots.Annot;
import com.foxit.sdk.pdf.annots.BorderInfo;
import com.foxit.sdk.pdf.annots.PolyLine;
import com.foxit.uiextensions.DocumentManager;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.AnnotContent;
import com.foxit.uiextensions.annots.AnnotHandler;
import com.foxit.uiextensions.annots.common.EditAnnotEvent;
import com.foxit.uiextensions.annots.common.EditAnnotTask;
import com.foxit.uiextensions.annots.common.UIAnnotFlatten;
import com.foxit.uiextensions.annots.common.UIAnnotReply;
import com.foxit.uiextensions.annots.multiselect.GroupManager;
import com.foxit.uiextensions.controls.propertybar.AnnotMenu;
import com.foxit.uiextensions.controls.propertybar.PropertyBar;
import com.foxit.uiextensions.utils.AppAnnotUtil;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppDmUtil;
import com.foxit.uiextensions.utils.AppUtil;
import com.foxit.uiextensions.utils.Event;

import java.util.ArrayList;

public class PolyLineAnnotHandler implements AnnotHandler {

    private int mCurrentCtrPointIndex = -1;

    public static final int OPER_DEFAULT = -1;
    public static final int OPER_TRANSLATE = 1;
    public static final int OPER_SCALE = 2;
    private int mLastOper = OPER_DEFAULT;

    private float mCtlPtLineWidth = 2;
    private float mCtlPtRadius = 5;
    private float mCtlPtTouchExt = 20;
    private float mCtlPtDeltyXY = 20;// Additional refresh range

    private int mTempLastColor;
    private int mTempLastOpacity;
    private float mTempLastLineWidth;
    private RectF mTempLastBBox = new RectF();
    private PointFArray mTempLastVertexes;

    private Paint mPathPaint;
    private Paint mFrmPaint;// outline
    private Paint mCtlPtPaint;

    private boolean mIsModify;
    private boolean mTouchCaptured = false;
    private PointF mDownPoint;
    private PointF mLastPoint;

    private ArrayList<Integer> mMenuText;
    private AnnotMenu mAnnotationMenu;

    private Annot mBitmapAnnot;

    private PropertyBar.PropertyChangeListener mPropertyChangeListener;
    private PropertyBar mPropertyBar;

    private Context mContext;
    private PDFViewCtrl mPdfViewCtrl;

    private PointF mDocViewerPt = new PointF(0, 0);
    private RectF mPageViewRect = new RectF(0, 0, 0, 0);

    private RectF mPageDrawRect = new RectF();
    private RectF mInvalidateRect = new RectF(0, 0, 0, 0);
    private RectF mAnnotMenuRect = new RectF(0, 0, 0, 0);
    private RectF mDocViewerBBox = new RectF(0, 0, 0, 0);

    private float mThickness = 0f;
    private PointF[] mVertexes; // in page view
    private PointF mLastDownPoint = new PointF();

    public PolyLineAnnotHandler(Context context, PDFViewCtrl pdfViewCtrl) {
        mContext = context;
        mPdfViewCtrl = pdfViewCtrl;

        mDownPoint = new PointF();
        mLastPoint = new PointF();

        mPathPaint = new Paint();
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);

        PathEffect effect = AppAnnotUtil.getAnnotBBoxPathEffect();
        mFrmPaint = new Paint();
        mFrmPaint.setPathEffect(effect);
        mFrmPaint.setStyle(Paint.Style.STROKE);
        mFrmPaint.setAntiAlias(true);

        mCtlPtPaint = new Paint();

        mMenuText = new ArrayList<Integer>();
    }

    @Override
    public int getType() {
        return Annot.e_PolyLine;
    }

    @Override
    public boolean annotCanAnswer(Annot annot) {
        return true;
    }

    @Override
    public RectF getAnnotBBox(Annot annot) {
        RectF rectF = null;
        try {
            rectF = AppUtil.toRectF(annot.getRect());
        } catch (PDFException e) {

        }
        return rectF;
    }

    @Override
    public boolean isHitAnnot(Annot annot, PointF point) {
        RectF bbox = getAnnotBBox(annot);
        if (bbox == null) return false;
        try {
            mPdfViewCtrl.convertPdfRectToPageViewRect(bbox, bbox, annot.getPage().getIndex());
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return bbox.contains(point.x, point.y);
    }


    @Override
    public void onAnnotSelected(Annot annot, boolean reRender) {
        mCtlPtRadius = AppDisplay.getInstance(mContext).dp2px(mCtlPtRadius);
        mCtlPtDeltyXY = AppDisplay.getInstance(mContext).dp2px(mCtlPtDeltyXY);

        try {
            mTempLastColor = (int) annot.getBorderColor();
            mTempLastOpacity = (int) (((PolyLine) annot).getOpacity() * 255f + 0.5f);
            mTempLastBBox = AppUtil.toRectF(annot.getRect());
            BorderInfo borderInfo = annot.getBorderInfo();
            mTempLastLineWidth = borderInfo.getWidth();
            mTempLastVertexes = ((PolyLine) annot).getVertexes();

            mVertexes = calculateControlPoints(annot);

            RectF _rect = AppUtil.toRectF(annot.getRect());
            mPageViewRect.set(_rect.left, _rect.top, _rect.right, _rect.bottom);
            PDFPage page = annot.getPage();
            int pageIndex = page.getIndex();
            mPdfViewCtrl.convertPdfRectToPageViewRect(mPageViewRect, mPageViewRect, pageIndex);
            prepareAnnotMenu(annot);
            RectF menuRect = new RectF(mPageViewRect);
            mPdfViewCtrl.convertPageViewRectToDisplayViewRect(menuRect, menuRect, pageIndex);
            mAnnotationMenu.show(menuRect);

            preparePropertyBar(!(AppAnnotUtil.isLocked(annot) || AppAnnotUtil.isReadOnly(annot)));

            if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(mPageViewRect));
                if (annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()) {
                    mBitmapAnnot = annot;
                }
            } else {
                mBitmapAnnot = annot;
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    private PointF[] calculateControlPoints(Annot annot) {
        try {
            int pageIndex = annot.getPage().getIndex();
            com.foxit.sdk.common.fxcrt.PointFArray pointFArray = ((PolyLine) annot).getVertexes();
            int count = pointFArray.getSize();
            PointF[] points = new PointF[count];
            for (int i = 0; i < count; i++) {
                points[i] = AppUtil.toPointF(pointFArray.getAt(i));

                mPdfViewCtrl.convertPdfPtToPageViewPt(points[i], points[i], pageIndex);
            }

            return points;
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void prepareAnnotMenu(final Annot annot) {
        resetAnnotationMenuResource(annot);

        mAnnotationMenu.setMenuItems(mMenuText);

        mAnnotationMenu.setListener(new AnnotMenu.ClickListener() {
            @Override
            public void onAMClick(int btType) {
                if (btType == AnnotMenu.AM_BT_DELETE) {
                    if (annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()) {
                        deleteAnnot(annot, true, null);
                    }
                } else if (btType == AnnotMenu.AM_BT_COMMENT) {
                    ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setCurrentAnnot(null);
                    UIAnnotReply.showComments(mPdfViewCtrl, ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getRootView(), annot);
                } else if (btType == AnnotMenu.AM_BT_REPLY) {

                    ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setCurrentAnnot(null);
                    UIAnnotReply.replyToAnnot(mPdfViewCtrl, ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getRootView(), annot);
                } else if (btType == AnnotMenu.AM_BT_STYLE) {

                    RectF rectF = AppUtil.toGlobalVisibleRectF(((UIExtensionsManager)mPdfViewCtrl.getUIExtensionsManager()).getRootView(), mDocViewerBBox);
                    mPropertyBar.show(rectF, false);
                    mAnnotationMenu.dismiss();
                } else if (btType == AnnotMenu.AM_BT_FLATTEN) {
                    ((UIExtensionsManager)mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setCurrentAnnot(null);
                    UIAnnotFlatten.flattenAnnot(mPdfViewCtrl, annot);
                }
            }
        });
    }

    private void preparePropertyBar(boolean isLock) {
        mPropertyBar.setEditable(isLock);
        int[] colors = new int[PropertyBar.PB_COLORS_POLYLINE.length];
        System.arraycopy(PropertyBar.PB_COLORS_POLYLINE, 0, colors, 0, colors.length);
        colors[0] = PropertyBar.PB_COLORS_POLYLINE[0];
        mPropertyBar.setColors(colors);

        DocumentManager documentManager = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager();
        try {
            mPropertyBar.setProperty(PropertyBar.PROPERTY_COLOR, (int) documentManager.getCurrentAnnot().getBorderColor());
            int opacity = AppDmUtil.opacity255To100((int) (((PolyLine) documentManager.getCurrentAnnot()).getOpacity() * 255f + 0.5f));
            mPropertyBar.setProperty(PropertyBar.PROPERTY_OPACITY, opacity);
            mPropertyBar.setProperty(PropertyBar.PROPERTY_LINEWIDTH, documentManager.getCurrentAnnot().getBorderInfo().getWidth());
        } catch (PDFException e) {
            e.printStackTrace();
        }

        mPropertyBar.setArrowVisible(false);
        mPropertyBar.reset(getSupportedProperties());
        mPropertyBar.setPropertyChangeListener(mPropertyChangeListener);
    }

    private long getSupportedProperties() {
        return PropertyBar.PROPERTY_COLOR
                | PropertyBar.PROPERTY_OPACITY
                | PropertyBar.PROPERTY_LINEWIDTH;
    }

    @Override
    public void onAnnotDeselected(Annot annot, boolean needInvalid) {
        mCtlPtRadius = 5;
        mCtlPtDeltyXY = 20;
        // configure annotation menu
        mAnnotationMenu.setListener(null);
        mAnnotationMenu.dismiss();

        if (mPropertyBar.isShowing()) {
            mPropertyBar.dismiss();
        }

        PDFPage page = null;
        try {
            page = annot.getPage();

            if (mIsModify) {
                if (needInvalid) {
                    PointFArray vertexes = ((PolyLine) annot).getVertexes();
                    boolean isVertexModify = false;
                    for (int i = 0; i < vertexes.getSize(); i++) {
                        if (mTempLastVertexes.getAt(i) != vertexes.getAt(i)) isVertexModify = true;
                    }
                    // must calculate BBox again
                    if (mTempLastColor == (int) annot.getBorderColor()
                            && mTempLastLineWidth == annot.getBorderInfo().getWidth()
                            && mTempLastBBox.equals(AppUtil.toRectF(annot.getRect()))
                            && mTempLastOpacity == (int) (((PolyLine) annot).getOpacity() * 255f) && !isVertexModify) {
                        modifyAnnot(page.getIndex(), annot, AppUtil.toRectF(annot.getRect()), (int) annot.getBorderColor(), (int) (((PolyLine) annot).getOpacity() * 255f),
                                annot.getBorderInfo().getWidth(), vertexes, annot.getContent(), false, false, null);
                    } else {
                        modifyAnnot(page.getIndex(), annot, AppUtil.toRectF(annot.getRect()), (int) annot.getBorderColor(), (int) (((PolyLine) annot).getOpacity() * 255f),
                                annot.getBorderInfo().getWidth(), vertexes, annot.getContent(), true, true, null);
                    }
                } else {
                    annot.setBorderColor(mTempLastColor);
                    BorderInfo borderInfo = new BorderInfo();
                    borderInfo.setWidth(mTempLastLineWidth);
                    annot.setBorderInfo(borderInfo);
                    ((PolyLine) annot).setOpacity(mTempLastOpacity / 255f);
                    if (mTempLastVertexes.getSize() > 0)
                        ((PolyLine) annot).setVertexes(mTempLastVertexes);
                    else
                        annot.move(AppUtil.toFxRectF(mTempLastBBox));
                    annot.resetAppearanceStream();
                }
            }

            if (mPdfViewCtrl.isPageVisible(page.getIndex()) && needInvalid) {
                RectF pdfRect = AppUtil.toRectF(annot.getRect());
                RectF viewRect = new RectF(pdfRect.left, pdfRect.top, pdfRect.right, pdfRect.bottom);
                mPdfViewCtrl.convertPdfRectToPageViewRect(viewRect, viewRect, page.getIndex());
                mPdfViewCtrl.refresh(page.getIndex(), AppDmUtil.rectFToRect(viewRect));
            }
            mBitmapAnnot = null;
            mIsModify = false;
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAnnot(int pageIndex, AnnotContent content, boolean addUndo, Event.Callback result) {
    }

    @Override
    public void modifyAnnot(Annot annot, AnnotContent content, boolean addUndo, Event.Callback result) {
        try {
            PDFPage page = annot.getPage();
            int pageIndex = page.getIndex();
            RectF bbox = AppUtil.toRectF(annot.getRect());
            int color = (int) annot.getBorderColor();
            BorderInfo borderInfo = annot.getBorderInfo();
            float lineWidth = borderInfo.getWidth();
            int opacity = (int) (((PolyLine) annot).getOpacity() * 255f);
            String contents = annot.getContent();

            mTempLastColor = (int) annot.getBorderColor();
            mTempLastOpacity = (int) (((PolyLine) annot).getOpacity() * 255f);
            mTempLastLineWidth = annot.getBorderInfo().getWidth();
            mTempLastBBox = AppUtil.toRectF(annot.getRect());
            mTempLastVertexes = ((PolyLine) annot).getVertexes();
            PointFArray vertexes = new PointFArray(mTempLastVertexes);

            if (content.getBBox() != null)
                bbox = content.getBBox();
            if (content.getColor() != 0)
                color = content.getColor();
            if (content.getLineWidth() != 0)
                lineWidth = content.getLineWidth();
            if (content.getOpacity() != 0)
                opacity = content.getOpacity();
            if (content.getContents() != null)
                contents = content.getContents();

            modifyAnnot(pageIndex, annot, bbox, color, opacity, lineWidth, vertexes, contents, true, addUndo, result);
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAnnot(Annot annot, boolean addUndo, Event.Callback result) {
        deleteAnnot(annot, addUndo, result);
    }

    @Override
    public boolean onTouchEvent(int pageIndex, MotionEvent e, Annot annot) {
        // in pageView evX and evY
        PointF point = new PointF(e.getX(), e.getY());
        mPdfViewCtrl.convertDisplayViewPtToPageViewPt(point, point, pageIndex);
        float evX = point.x;
        float evY = point.y;

        int action = e.getAction();
        try {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()
                            && pageIndex == annot.getPage().getIndex()) {
                        mThickness = thicknessOnPageView(pageIndex, annot.getBorderInfo().getWidth());
                        RectF pageViewBBox = AppUtil.toRectF(annot.getRect());
                        mPdfViewCtrl.convertPdfRectToPageViewRect(pageViewBBox, pageViewBBox, pageIndex);
                        RectF pdfRect = AppUtil.toRectF(annot.getRect());
                        mPageViewRect.set(pdfRect.left, pdfRect.top, pdfRect.right, pdfRect.bottom);
                        mPdfViewCtrl.convertPdfRectToPageViewRect(mPageViewRect, mPageViewRect, pageIndex);
                        mPageViewRect.inset(mThickness / 2f, mThickness / 2f);

                        mDownPoint.set(evX, evY);
                        mLastPoint.set(evX, evY);
                        mDocViewerPt.set(e.getX(), e.getY());
                        mLastDownPoint.set(evX, evY);

                        mVertexes = calculateControlPoints(annot);
                        mCurrentCtrPointIndex = getTouchControlPointIndex(mVertexes, evX, evY);
                        if (mCurrentCtrPointIndex != -1) {
                            mTouchCaptured = true;
                            mLastOper = OPER_SCALE;
                            return true;
                        } else if (isHitAnnot(annot, point)) {
                            mTouchCaptured = true;
                            mLastOper = OPER_TRANSLATE;
                            return true;
                        }
                    }
                    return false;
                case MotionEvent.ACTION_MOVE:

                    if (pageIndex == annot.getPage().getIndex()
                            && mTouchCaptured
                            && annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()) {
                        if (evX != mLastPoint.x && evY != mLastPoint.y) {
                            RectF pageViewBBox = AppUtil.toRectF(annot.getRect());
                            mPdfViewCtrl.convertPdfRectToPageViewRect(pageViewBBox, pageViewBBox, pageIndex);
                            float deltaXY = mCtlPtLineWidth + mCtlPtRadius * 2 + 2;// Judging border value
                            switch (mLastOper) {
                                case OPER_TRANSLATE: {
                                    mInvalidateRect.set(pageViewBBox);
                                    mAnnotMenuRect.set(pageViewBBox);
                                    mInvalidateRect.offset(mLastPoint.x - mDownPoint.x, mLastPoint.y - mDownPoint.y);
                                    mAnnotMenuRect.offset(evX - mDownPoint.x, evY - mDownPoint.y);
                                    PointF adjustXY = adjustScalePointF(pageIndex, mAnnotMenuRect, deltaXY);

                                    mInvalidateRect.union(mAnnotMenuRect);

                                    mInvalidateRect.inset(-deltaXY - mCtlPtDeltyXY, -deltaXY - mCtlPtDeltyXY);
                                    mPdfViewCtrl.convertPageViewRectToDisplayViewRect(mInvalidateRect, mInvalidateRect, pageIndex);
                                    mPdfViewCtrl.invalidate(AppDmUtil.rectFToRect(mInvalidateRect));

                                    mPdfViewCtrl.convertPageViewRectToDisplayViewRect(mAnnotMenuRect, mAnnotMenuRect, pageIndex);
                                    if (mAnnotationMenu.isShowing()) {
                                        mAnnotationMenu.dismiss();
                                        mAnnotationMenu.update(mAnnotMenuRect);
                                    }

                                    mLastPoint.set(evX, evY);
                                    mLastPoint.offset(adjustXY.x, adjustXY.y);

                                    for (int i = 0; i < mVertexes.length; i++) {
                                        mVertexes[i].offset(mLastPoint.x - mLastDownPoint.x, mLastPoint.y - mLastDownPoint.y);
                                    }
                                    mLastDownPoint.set(mLastPoint);
                                    break;
                                }
                                case OPER_SCALE: {
                                    if (evX != mLastPoint.x && evY != mLastPoint.y) {
                                        if (!pageViewBBox.contains(evX, evY)) {
                                            mInvalidateRect.set(pageViewBBox);
                                            mAnnotMenuRect.set(pageViewBBox);
                                        } else {
                                            RectF rectF = new RectF();
                                            for (int i = 0; i < mVertexes.length; i++) {
                                                if (i == mCurrentCtrPointIndex) continue;
                                                if (rectF.equals(new RectF(0, 0, 0, 0))) {
                                                    rectF.set(mVertexes[i].x, mVertexes[i].y, mVertexes[i].x, mVertexes[i].y);
                                                } else {
                                                    rectF.union(mVertexes[i].x, mVertexes[i].y);
                                                }

                                            }

                                            mInvalidateRect.set(rectF);
                                            mAnnotMenuRect.set(rectF);
                                        }

                                        mInvalidateRect.union(mLastPoint.x, mLastPoint.y);
                                        mAnnotMenuRect.union(evX, evY);
                                        mInvalidateRect.sort();
                                        mAnnotMenuRect.sort();
                                        mInvalidateRect.union(mAnnotMenuRect);
                                        mInvalidateRect.inset(-mThickness - mCtlPtDeltyXY, -mThickness - mCtlPtDeltyXY);
                                        mPdfViewCtrl.convertPageViewRectToDisplayViewRect(mInvalidateRect, mInvalidateRect, pageIndex);
                                        mPdfViewCtrl.invalidate(AppDmUtil.rectFToRect(mInvalidateRect));

                                        PointF adjustXY = adjustScalePointF(pageIndex, mAnnotMenuRect, deltaXY);

                                        mPdfViewCtrl.convertPageViewRectToDisplayViewRect(mAnnotMenuRect, mAnnotMenuRect, pageIndex);
                                        if (mAnnotationMenu.isShowing()) {
                                            mAnnotationMenu.dismiss();
                                            mAnnotationMenu.update(mAnnotMenuRect);
                                        }

                                        mLastPoint.set(evX, evY);
                                        mLastPoint.offset(adjustXY.x, adjustXY.y);

                                        mVertexes[mCurrentCtrPointIndex].set(mLastPoint);
                                    }
                                    break;
                                }
                                default:
                                    break;
                            }

                            PointFArray pointFArray = new PointFArray();
                            for (int i = 0; i < mVertexes.length; i++) {
                                PointF vertex = new PointF();
                                mPdfViewCtrl.convertPageViewPtToPdfPt(mVertexes[i], vertex, pageIndex);
                                pointFArray.add(AppUtil.toFxPointF(vertex));
                            }
                            ((PolyLine) annot).setVertexes(pointFArray);
                        }
                        return true;
                    }
                    return false;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mTouchCaptured
                            && annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()
                            && pageIndex == annot.getPage().getIndex()) {
                        RectF pageViewRect = AppUtil.toRectF(annot.getRect());
                        mPdfViewCtrl.convertPdfRectToPageViewRect(pageViewRect, pageViewRect, pageIndex);
                        pageViewRect.inset(mThickness / 2, mThickness / 2);

                        switch (mLastOper) {
                            case OPER_TRANSLATE: {
                                mPageDrawRect.set(pageViewRect);
                                mPageDrawRect.offset(mLastPoint.x - mDownPoint.x, mLastPoint.y - mDownPoint.y);
                                break;
                            }
                            case OPER_SCALE: {
                                if (!mDownPoint.equals(mLastPoint.x, mLastPoint.y)) {
                                    if (!pageViewRect.contains(mLastPoint.x, mLastPoint.y)) {
                                        mPageDrawRect.set(pageViewRect);
                                        mPageDrawRect.union(mLastPoint.x, mLastPoint.y);
                                    } else {
                                        RectF rectF = new RectF();
                                        for (int i = 0; i < mVertexes.length; i++) {
                                            if (i == 0) {
                                                rectF.set(mVertexes[i].x, mVertexes[i].y, mVertexes[i].x, mVertexes[i].y);
                                            } else {
                                                rectF.union(mVertexes[i].x, mVertexes[i].y);
                                            }
                                        }
                                        mPageDrawRect.set(rectF);
                                    }
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        if (mLastOper != OPER_DEFAULT && !mDownPoint.equals(mLastPoint.x, mLastPoint.y)) {
                            RectF viewDrawBox = new RectF(mPageDrawRect.left, mPageDrawRect.top, mPageDrawRect.right, mPageDrawRect.bottom);
                            float _lineWidth = annot.getBorderInfo().getWidth();
                            viewDrawBox.inset(-thicknessOnPageView(pageIndex, _lineWidth) / 2, -thicknessOnPageView(pageIndex, _lineWidth) / 2);
                            RectF bboxRect = new RectF(viewDrawBox);
                            mPdfViewCtrl.convertPageViewRectToPdfRect(bboxRect, bboxRect, pageIndex);
                            PointFArray vertexes = new PointFArray();
                            for (int i = 0; i < mVertexes.length; i++) {
                                PointF vertex = new PointF();
                                mPdfViewCtrl.convertPageViewPtToPdfPt(mVertexes[i], vertex, pageIndex);
                                vertexes.add(AppUtil.toFxPointF(vertex));
                            }

                            modifyAnnot(pageIndex, annot, bboxRect, (int) annot.getBorderColor(), (int) (((PolyLine) annot).getOpacity() * 255f), _lineWidth,
                                    vertexes, annot.getContent(), false, false, null);
                            mPdfViewCtrl.convertPageViewRectToDisplayViewRect(viewDrawBox, viewDrawBox, pageIndex);

                            if (mAnnotationMenu.isShowing()) {
                                mAnnotationMenu.update(viewDrawBox);
                            } else {
                                mAnnotationMenu.show(viewDrawBox);
                            }
                        } else {
                            RectF viewDrawBox = new RectF(mPageDrawRect.left, mPageDrawRect.top, mPageDrawRect.right, mPageDrawRect.bottom);
                            float _lineWidth = annot.getBorderInfo().getWidth();
                            viewDrawBox.inset(-thicknessOnPageView(pageIndex, _lineWidth) / 2, -thicknessOnPageView(pageIndex, _lineWidth) / 2);
                            mPdfViewCtrl.convertPageViewRectToDisplayViewRect(viewDrawBox, viewDrawBox, pageIndex);
                            if (mAnnotationMenu.isShowing()) {
                                mAnnotationMenu.update(viewDrawBox);
                            } else {
                                mAnnotationMenu.show(viewDrawBox);
                            }
                        }

                        mTouchCaptured = false;
                        mDownPoint.set(0, 0);
                        mLastPoint.set(0, 0);
                        mLastOper = OPER_DEFAULT;
                        mCurrentCtrPointIndex = -1;
                        return true;
                    }

                    mTouchCaptured = false;
                    mDownPoint.set(0, 0);
                    mLastPoint.set(0, 0);
                    mLastOper = OPER_DEFAULT;
                    mCurrentCtrPointIndex = -1;
                    mTouchCaptured = false;
                    return false;
            }
            return false;

        } catch (PDFException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    private int getTouchControlPointIndex(PointF[] vertexes, float x, float y) {
        if (vertexes == null) return -1;
        RectF area = new RectF();
        int ret = -1;
        for (int i = 0; i < vertexes.length; i++) {
            area.set(vertexes[i].x, vertexes[i].y, vertexes[i].x, vertexes[i].y);
            area.inset(-mCtlPtTouchExt, -mCtlPtTouchExt);
            if (area.contains(x, y)) {
                ret = i;
            }
        }
        return ret;
    }

    private PointF mAdjustPointF = new PointF(0, 0);

    private PointF adjustScalePointF(int pageIndex, RectF rectF, float dxy) {
        float adjustx = 0;
        float adjusty = 0;
        if (mLastOper != OPER_TRANSLATE) {
            rectF.inset(-mThickness / 2f, -mThickness / 2f);
        }

        if ((int) rectF.left < dxy) {
            adjustx = -rectF.left + dxy;
            rectF.left = dxy;
        }
        if ((int) rectF.top < dxy) {
            adjusty = -rectF.top + dxy;
            rectF.top = dxy;
        }

        if ((int) rectF.right > mPdfViewCtrl.getPageViewWidth(pageIndex) - dxy) {
            adjustx = mPdfViewCtrl.getPageViewWidth(pageIndex) - rectF.right - dxy;
            rectF.right = mPdfViewCtrl.getPageViewWidth(pageIndex) - dxy;
        }
        if ((int) rectF.bottom > mPdfViewCtrl.getPageViewHeight(pageIndex) - dxy) {
            adjusty = mPdfViewCtrl.getPageViewHeight(pageIndex) - rectF.bottom - dxy;
            rectF.bottom = mPdfViewCtrl.getPageViewHeight(pageIndex) - dxy;
        }
        mAdjustPointF.set(adjustx, adjusty);
        return mAdjustPointF;
    }

    @Override
    public boolean onLongPress(int pageIndex, MotionEvent motionEvent, Annot annot) {
        return onSingleTapOrLongPress(pageIndex, motionEvent, annot);
    }

    @Override
    public boolean onSingleTapConfirmed(int pageIndex, MotionEvent motionEvent, Annot annot) {
        return onSingleTapOrLongPress(pageIndex, motionEvent, annot);
    }

    @Override
    public boolean shouldViewCtrlDraw(Annot annot) {
        Annot curAnnot = ((UIExtensionsManager)mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot();
        return AppAnnotUtil.isSameAnnot(curAnnot, annot) ? false : true;
    }

    private boolean onSingleTapOrLongPress(int pageIndex, MotionEvent motionEvent, Annot annot) {
        try {
            mDocViewerPt.set(motionEvent.getX(), motionEvent.getY());//display view
            PointF point = new PointF(motionEvent.getX(), motionEvent.getY());
            mPdfViewCtrl.convertDisplayViewPtToPageViewPt(point, point, pageIndex);

            mThickness = thicknessOnPageView(pageIndex, annot.getBorderInfo().getWidth());
            RectF _rect = AppUtil.toRectF(annot.getRect());
            mPageViewRect.set(_rect.left, _rect.top, _rect.right, _rect.bottom);
            mPdfViewCtrl.convertPdfRectToPageViewRect(mPageViewRect, mPageViewRect, pageIndex);
            mPageViewRect.inset(mThickness / 2f, mThickness / 2f);
            if (annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()) {

                if (pageIndex == annot.getPage().getIndex() && isHitAnnot(annot, point)) {
                    return true;
                } else {
                    ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setCurrentAnnot(null);
                    return true;
                }
            } else {
                ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setCurrentAnnot(annot);
                return true;
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }

    private RectF mBBoxInOnDraw = new RectF();
    private DrawFilter mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    @Override
    public void onDraw(int pageIndex, Canvas canvas) {
        Annot annot = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot();
        if (annot == null || !(annot instanceof PolyLine)) {
            return;
        }

        try {
            int annotPageIndex = annot.getPage().getIndex();
            if (AppAnnotUtil.equals(mBitmapAnnot, annot) && annotPageIndex == pageIndex) {
                canvas.save();
                canvas.setDrawFilter(mDrawFilter);
                float thickness = thicknessOnPageView(pageIndex, annot.getBorderInfo().getWidth());
                mPathPaint.setColor((int) annot.getBorderColor());
                mPathPaint.setAlpha((int) (((PolyLine) annot).getOpacity() * 255f));
                mPathPaint.setStrokeWidth(thickness);
                mFrmPaint.setColor((int) annot.getBorderColor());

                PointFArray pointFArray = ((PolyLine) annot).getVertexes();
                int count = pointFArray.getSize();
                PointF[] vertexes = new PointF[count];
                for (int i = 0; i < count; i++) {
                    vertexes[i] = AppUtil.toPointF(pointFArray.getAt(i));
                    mPdfViewCtrl.convertPdfPtToPageViewPt(vertexes[i], vertexes[i], pageIndex);
                    if (i == 0) {
                        mBBoxInOnDraw.set(vertexes[i].x, vertexes[i].y, vertexes[i].x, vertexes[i].y);
                    } else {
                        mBBoxInOnDraw.union(vertexes[i].x, vertexes[i].y);
                    }
                }
                if (annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()) {
                    // add Control Imaginary
                    drawControlImaginary(canvas, vertexes);

                    drawControlPoints(canvas, vertexes);
                }

                mBBoxInOnDraw.inset(-mCtlPtRadius, -mCtlPtRadius);
                mFrmPaint.setStrokeWidth(mCtlPtLineWidth);
                canvas.drawRect(mBBoxInOnDraw, mFrmPaint);
                canvas.restore();
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    protected void onDrawForControls(Canvas canvas) {
        Annot curAnnot = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot();
        if (curAnnot instanceof PolyLine
                && ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getCurrentAnnotHandler() == this) {

            try {
                int annotPageIndex = curAnnot.getPage().getIndex();
                if (mPdfViewCtrl.isPageVisible(annotPageIndex)) {
                    PointFArray pointFArray = ((PolyLine) curAnnot).getVertexes();
                    int count = pointFArray.getSize();
                    PointF[] vertexes = new PointF[count];
                    for (int i = 0; i < count; i++) {
                        vertexes[i] = AppUtil.toPointF(pointFArray.getAt(i));
                        mPdfViewCtrl.convertPdfPtToPageViewPt(vertexes[i], vertexes[i], annotPageIndex);
                        if (i == 0) {
                            mDocViewerBBox.set(vertexes[i].x, vertexes[i].y, vertexes[i].x, vertexes[i].y);
                        } else {
                            mDocViewerBBox.union(vertexes[i].x, vertexes[i].y);
                        }
                    }

                    mPdfViewCtrl.convertPageViewRectToDisplayViewRect(mDocViewerBBox, mDocViewerBBox, annotPageIndex);
                    mAnnotationMenu.update(mDocViewerBBox);
                    if (mPropertyBar.isShowing()) {
                        RectF rectF = AppUtil.toGlobalVisibleRectF(((UIExtensionsManager)mPdfViewCtrl.getUIExtensionsManager()).getRootView(), mDocViewerBBox);
                        mPropertyBar.update(rectF);
                    }
                }
            } catch (PDFException e) {
                e.printStackTrace();
            }
        }
    }


    private Path mImaginaryPath = new Path();

    private void drawControlImaginary(Canvas canvas, PointF[] ctlPts) {
        mImaginaryPath.reset();
        for (int i = 0; i < ctlPts.length - 1; i++) {
            PointF p1 = ctlPts[i];
            PointF p2 = ctlPts[i + 1];

            mImaginaryPath.moveTo(p1.x, p1.y);
            mImaginaryPath.lineTo(p2.x, p2.y);
        }

        canvas.drawPath(mImaginaryPath, mPathPaint);
    }

    private void drawControlPoints(Canvas canvas, PointF[] ctlPts) {
        mCtlPtPaint.setStrokeWidth(mCtlPtLineWidth);
        for (PointF ctlPt : ctlPts) {
            mCtlPtPaint.setColor(Color.WHITE);
            mCtlPtPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(ctlPt.x, ctlPt.y, mCtlPtRadius, mCtlPtPaint);
            mCtlPtPaint.setColor(Color.BLUE);
            mCtlPtPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(ctlPt.x, ctlPt.y, mCtlPtRadius, mCtlPtPaint);
        }
    }

    private void modifyAnnot(final int pageIndex, final Annot annot, RectF bbox, int color, int opacity,
                             float lineWidth, PointFArray vertexes, String contents, boolean isModifyJni,
                             final boolean addUndo, final Event.Callback result) {
        final PolyLineModifyUndoItem undoItem = new PolyLineModifyUndoItem(mPdfViewCtrl);
        undoItem.setCurrentValue(annot);
        undoItem.mPageIndex = pageIndex;
        undoItem.mBBox = new RectF(bbox);
        undoItem.mModifiedDate = AppDmUtil.currentDateToDocumentDate();
        undoItem.mColor = color;
        undoItem.mOpacity = opacity / 255f;
        undoItem.mLineWidth = lineWidth;
        undoItem.mContents = contents;
        undoItem.mVertexes = new PointFArray(vertexes);

        undoItem.mRedoColor = color;
        undoItem.mRedoOpacity = opacity / 255f;
        undoItem.mRedoBbox = new RectF(bbox);
        undoItem.mRedoLineWidth = lineWidth;
        undoItem.mRedoContent = contents;
        undoItem.mRedoVertexes = new PointFArray(vertexes);

        undoItem.mUndoColor = mTempLastColor;
        undoItem.mUndoOpacity = mTempLastOpacity / 255f;
        undoItem.mUndoBbox = new RectF(mTempLastBBox);
        undoItem.mUndoLineWidth = mTempLastLineWidth;
        undoItem.mUndoVertexes = new PointFArray(mTempLastVertexes);
        try {
            undoItem.mUndoContent = annot.getContent();
        } catch (PDFException e) {
            e.printStackTrace();
        }

        if (isModifyJni) {
            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setHasModifyTask(addUndo);
            PolyLineEvent event = new PolyLineEvent(EditAnnotEvent.EVENTTYPE_MODIFY, undoItem, (PolyLine) annot, mPdfViewCtrl);
            EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
                @Override
                public void result(Event event, boolean success) {
                    if (success) {
                        if (addUndo) {
                            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().addUndoItem(undoItem);
                            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setHasModifyTask(false);
                        }
                        RectF tempRectF = mTempLastBBox;
                        if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                            try {
                                RectF annotRectF = AppUtil.toRectF(annot.getRect());
                                mPdfViewCtrl.convertPdfRectToPageViewRect(annotRectF, annotRectF, pageIndex);
                                mPdfViewCtrl.convertPdfRectToPageViewRect(tempRectF, tempRectF, pageIndex);
                                annotRectF.union(tempRectF);
                                annotRectF.inset(-AppAnnotUtil.getAnnotBBoxSpace() - 10, -AppAnnotUtil.getAnnotBBoxSpace() - 10);
                                mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(annotRectF));
                            } catch (PDFException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (result != null) {
                        result.result(null, success);
                    }
                }
            });
            mPdfViewCtrl.addTask(task);
        }

        try {
            if (isModifyJni) {
                ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().onAnnotModified(annot.getPage(), annot);
            }

            mIsModify = true;

            if (!isModifyJni) {
                RectF oldRect = AppUtil.toRectF(annot.getRect());
                annot.setBorderColor(color);
                ((PolyLine) annot).setOpacity(opacity / 255f);
                BorderInfo borderInfo = annot.getBorderInfo();
                borderInfo.setWidth(lineWidth);
                annot.setBorderInfo(borderInfo);
                if (contents != null) {
                    annot.setContent(contents);
                }

                annot.setFlags(annot.getFlags());
                if (vertexes != null) {
                    ((PolyLine) annot).setVertexes(vertexes);
                } else {
                    annot.move(AppUtil.toFxRectF(bbox));
                }

                annot.setModifiedDateTime(AppDmUtil.currentDateToDocumentDate());
                annot.resetAppearanceStream();
                RectF annotRectF = AppUtil.toRectF(annot.getRect());

                if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                    float thickness = thicknessOnPageView(pageIndex, annot.getBorderInfo().getWidth());

                    mPdfViewCtrl.convertPdfRectToPageViewRect(annotRectF, annotRectF, pageIndex);
                    mPdfViewCtrl.convertPdfRectToPageViewRect(oldRect, oldRect, pageIndex);

                    mPdfViewCtrl.convertPageViewRectToDisplayViewRect(annotRectF, annotRectF, pageIndex);
                    mPdfViewCtrl.convertPageViewRectToDisplayViewRect(oldRect, oldRect, pageIndex);
                    annotRectF.union(oldRect);
                    annotRectF.inset(-thickness - mCtlPtRadius - mCtlPtDeltyXY, -thickness - mCtlPtRadius - mCtlPtDeltyXY);
                    mPdfViewCtrl.invalidate(AppDmUtil.rectFToRect(annotRectF));
                }
            }
        } catch (PDFException e) {
            if (e.getLastError() == Constants.e_ErrOutOfMemory) {
                mPdfViewCtrl.recoverForOOM();
            }
            return;
        }
    }

    private void deleteAnnot(final Annot annot, final boolean addUndo, final Event.Callback result) {
        try {
            final RectF viewRect = AppUtil.toRectF(annot.getRect());
            if (annot == ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot()) {
                ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().setCurrentAnnot(null, false);
            }
            final PDFPage page = annot.getPage();
            final int pageIndex = page.getIndex();

            final PolyLineDeleteUndoItem undoItem = new PolyLineDeleteUndoItem(mPdfViewCtrl);
            undoItem.setCurrentValue(annot);
            undoItem.mPageIndex = pageIndex;
            undoItem.mVertexes = ((PolyLine) annot).getVertexes();
            if (AppAnnotUtil.isGrouped(annot))
                undoItem.mGroupNMList = GroupManager.getInstance().getGroupUniqueIDs(mPdfViewCtrl, annot);

            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().onAnnotWillDelete(page, annot);
            PolyLineEvent event = new PolyLineEvent(EditAnnotEvent.EVENTTYPE_DELETE, undoItem, (PolyLine) annot, mPdfViewCtrl);
            if (((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().isMultipleSelectAnnots()) {
                if (result != null) {
                    result.result(event, true);
                }
                return;
            }
            EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
                @Override
                public void result(Event event, boolean success) {
                    if (success) {
                        if (undoItem.mGroupNMList.size() >= 2) {
                            ArrayList<String> newGroupList = new ArrayList<>(undoItem.mGroupNMList);
                            newGroupList.remove(undoItem.mNM);
                            if (newGroupList.size() >= 2)
                                GroupManager.getInstance().setAnnotGroup(mPdfViewCtrl, page, newGroupList);
                            else
                                GroupManager.getInstance().unGroup(page, newGroupList.get(0));
                        }

                        ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().onAnnotDeleted(page, annot);
                        if (addUndo) {
                            ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().addUndoItem(undoItem);
                        }

                        if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                            mPdfViewCtrl.convertPdfRectToPageViewRect(viewRect, viewRect, pageIndex);
                            mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(viewRect));
                        }
                    }

                    if (result != null) {
                        result.result(null, success);
                    }
                }
            });
            mPdfViewCtrl.addTask(task);
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    protected void onColorValueChanged(int color) {
        Annot annot = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot();
        UIExtensionsManager uiExtensionsManager = (UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager();
        try {
            if (annot != null && uiExtensionsManager.getCurrentAnnotHandler() == this && color != annot.getBorderColor()) {
                modifyAnnot(annot.getPage().getIndex(), annot, AppUtil.toRectF(annot.getRect()), color, (int) (((PolyLine) annot).getOpacity() * 255f),
                        annot.getBorderInfo().getWidth(), ((PolyLine) annot).getVertexes(), annot.getContent(), false, false, null);
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    protected void onOpacityValueChanged(int opacity) {
        Annot annot = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot();
        UIExtensionsManager uiExtensionsManager = (UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager();
        try {
            if (annot != null && uiExtensionsManager.getCurrentAnnotHandler() == this
                    && AppDmUtil.opacity100To255(opacity) != (int) (((PolyLine) annot).getOpacity() * 255f)) {
                modifyAnnot(annot.getPage().getIndex(), annot, AppUtil.toRectF(annot.getRect()), (int) annot.getBorderColor(), AppDmUtil.opacity100To255(opacity),
                        annot.getBorderInfo().getWidth(), ((PolyLine) annot).getVertexes(), annot.getContent(), false, false, null);
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    protected void onLineWidthValueChanged(float lineWidth) {
        Annot annot = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().getCurrentAnnot();
        UIExtensionsManager uiExtensionsManager = (UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager();
        try {
            if (annot != null && uiExtensionsManager.getCurrentAnnotHandler() == this && lineWidth != annot.getBorderInfo().getWidth()) {
                RectF bboxRect = AppUtil.toRectF(annot.getRect());
                float deltLineWidth = annot.getBorderInfo().getWidth() - lineWidth;
                modifyAnnot(annot.getPage().getIndex(), annot, bboxRect, (int) annot.getBorderColor(),
                        (int) (((PolyLine) annot).getOpacity() * 255f), lineWidth, ((PolyLine) annot).getVertexes(),
                        annot.getContent(), false, false, null);

                if (mAnnotationMenu.isShowing()) {
                    RectF pageViewBBox = AppUtil.toRectF(annot.getRect());
                    pageViewBBox.inset(deltLineWidth * 0.5f, deltLineWidth * 0.5f);

                    mPdfViewCtrl.convertPdfRectToPageViewRect(pageViewBBox, pageViewBBox, annot.getPage().getIndex());
                    mAnnotationMenu.update(pageViewBBox);
                }
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    private RectF mThicknessRectF = new RectF();

    private float thicknessOnPageView(int pageIndex, float thickness) {
        mThicknessRectF.set(0, 0, thickness, thickness);
        mPdfViewCtrl.convertPdfRectToPageViewRect(mThicknessRectF, mThicknessRectF, pageIndex);
        return Math.abs(mThicknessRectF.width());
    }

    private void resetAnnotationMenuResource(Annot annot) {
        mMenuText.clear();

        if (((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager()).getDocumentManager().canAddAnnot()) {
            mMenuText.add(AnnotMenu.AM_BT_STYLE);
            mMenuText.add(AnnotMenu.AM_BT_COMMENT);
            mMenuText.add(AnnotMenu.AM_BT_REPLY);
            mMenuText.add(AnnotMenu.AM_BT_FLATTEN);
            if (!(AppAnnotUtil.isLocked(annot) || AppAnnotUtil.isReadOnly(annot))) {
                mMenuText.add(AnnotMenu.AM_BT_DELETE);
            }
        } else {
            mMenuText.add(AnnotMenu.AM_BT_COMMENT);
        }
    }

    protected void setAnnotMenu(AnnotMenu annotMenu) {
        mAnnotationMenu = annotMenu;
    }

    protected AnnotMenu getAnnotMenu() {
        return mAnnotationMenu;
    }

    protected void setPropertyBar(PropertyBar propertyBar) {
        mPropertyBar = propertyBar;
    }

    protected PropertyBar getPropertyBar() {
        return mPropertyBar;
    }

    protected void setPropertyChangeListener(PropertyBar.PropertyChangeListener propertyChangeListener) {
        mPropertyChangeListener = propertyChangeListener;
    }

    protected void removePropertyBarListener() {
        mPropertyChangeListener = null;
    }
}

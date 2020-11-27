package com.foxit.uiextensions.annots.fillsign;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.pdf.FillSign;
import com.foxit.sdk.pdf.FillSignObject;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.TextFillSignObject;
import com.foxit.uiextensions.R;
import com.foxit.uiextensions.ToolHandler;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.common.EditAnnotEvent;
import com.foxit.uiextensions.annots.common.EditAnnotTask;
import com.foxit.uiextensions.annots.common.UIAnnotFrame;
import com.foxit.uiextensions.annots.common.UIPopover;
import com.foxit.uiextensions.annots.common.UIPopoverWin;
import com.foxit.uiextensions.controls.toolbar.IBaseItem;
import com.foxit.uiextensions.controls.toolbar.ToolbarItemConfig;
import com.foxit.uiextensions.pdfreader.config.AppBuildConfig;
import com.foxit.uiextensions.utils.AppAnnotUtil;
import com.foxit.uiextensions.utils.AppDevice;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppDmUtil;
import com.foxit.uiextensions.utils.AppKeyboardUtil;
import com.foxit.uiextensions.utils.AppResource;
import com.foxit.uiextensions.utils.AppUtil;
import com.foxit.uiextensions.utils.Event;
import com.foxit.uiextensions.utils.IResult;
import com.foxit.uiextensions.utils.thread.AppThreadManager;

import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;

public class FillSignToolHandler implements ToolHandler {

    private UIExtensionsManager mUIExtensionsManager;
    private PDFViewCtrl mPdfViewCtrl;
    private Context mContext;
    private FillSignModule mFillSignModule;
    private FillSignProperty mProperty;

    private SparseArray<FillSign> mFillSignArrs = new SparseArray<>();
    private ArrayList<FillSignUndoItem> mUndoItemList = new ArrayList<>();

    private FormObject mOldAnnot;
    private FormObject mFocusAnnot;

    private PointF mLongPressPt;
    private PointF mDownPt;
    private PointF mLastPt;

    private float mMoveDist;
    private int mTouchCaptured;
    private int mOp;
    private int mCtl;
    private int mPopoverAction;
    private int mLongPressPage;
    private int mLastLineCount = -1;

    FillSignToolHandler(Context context, FillSignModule fillSignModule, PDFViewCtrl viewCtrl) {
        mContext = context;
        mFillSignModule = fillSignModule;
        mPdfViewCtrl = viewCtrl;
        mUIExtensionsManager = (UIExtensionsManager) viewCtrl.getUIExtensionsManager();

        mDownPt = new PointF();
        mLastPt = new PointF();
        mLongPressPt = new PointF();
        mLongPressPage = -1;

        mProperty = new FillSignProperty(context);
    }

    @Override
    public String getType() {
        return TH_TYPE_FILLSIGN;
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {
        if (_curToolTag() != 0) {
            mFillSignModule.onItemClicked(mFillSignModule.getCurrentItem());
        }
        if (isEditingText()) {
            endAddTextBox(new Event.Callback() {
                @Override
                public void result(Event event, boolean success) {
                    if (isPopoverShowing())
                        dismissPopover();
                    release();
                }
            });
        } else {
            if (isPopoverShowing())
                dismissPopover();
            release();
        }
        mLongPressPage = -1;
        mLongPressPt = null;
    }

    @Override
    public boolean onTouchEvent(int pageIndex, MotionEvent motionEvent) {
        PointF devPoint = new PointF(motionEvent.getX(), motionEvent.getY());
        PointF pageViewPt = new PointF();
        mPdfViewCtrl.convertDisplayViewPtToPageViewPt(devPoint, pageViewPt, pageIndex);
        PointF pdfPt = new PointF(pageViewPt.x, pageViewPt.y);
        mPdfViewCtrl.convertPageViewPtToPdfPt(pdfPt, pdfPt, pageIndex);

        int itemTag = _curToolTag();
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isEditingText() && mFocusAnnot == null) {
                    if (isPopoverShowing()) {// long press popover
                        dismissPopover();
                        mLongPressPt = null;
                        mLongPressPage = -1;
                        mTouchCaptured = 2;
                        return true;
                    }
                }
                if (mFocusAnnot != null && pageIndex == mFocusAnnot.mPageIndex) {
                    RectF pvBox = new RectF(mFocusAnnot.mBBox);
                    mPdfViewCtrl.convertPdfRectToPageViewRect(pvBox, pvBox, pageIndex);

                    if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_LINE) {
                        PointF p1 = new PointF(pvBox.left, pvBox.centerY());
                        PointF p2 = new PointF(pvBox.right, pvBox.centerY());
                        mCtl = UIAnnotFrame.hitLineControlTest(p1, p2, pageViewPt, AppDisplay.getInstance(mContext).getFingerArea() / 5, AppDisplay.getInstance(mContext).getFingerArea() / 4);
                        if (mCtl == 0) {
                            mCtl = UIAnnotFrame.CTL_LEFT_MID;
                        } else if (mCtl == 1) {
                            mCtl = UIAnnotFrame.CTL_RIGHT_MID;
                        }
                    } else if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_RECT) {
                        RectF bounds = UIAnnotFrame.calculateFrameBounds(pvBox, _borderThickness(), AppDisplay.getInstance(mContext).getFingerArea() / 5);
                        mCtl = UIAnnotFrame.hitControlTest(bounds, pageViewPt, AppDisplay.getInstance(mContext).getFingerArea() / 4);
                    } else if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_CHECK
                            || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_X
                            || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_DOT) {
                        RectF bounds = UIAnnotFrame.calculateFrameBounds(pvBox, _borderThickness(), AppDisplay.getInstance(mContext).getFingerArea() / 5);
                        mCtl = UIAnnotFrame.hitCornerControlTest(bounds, pageViewPt, AppDisplay.getInstance(mContext).getFingerArea() / 4);
                    } else {
                        mCtl = UIAnnotFrame.CTL_NONE;
                    }
                    if (mCtl != UIAnnotFrame.CTL_NONE) {
                        mTouchCaptured = 1;
                        mOp = UIAnnotFrame.OP_SCALE;
                        mDownPt.set(pageViewPt);
                        mLastPt.set(pageViewPt);
                        mMoveDist = 0;
                        return true;
                    } else {
                        RectF bbox = new RectF(pvBox);
                        bbox.inset(-AppDisplay.getInstance(mContext).getFingerArea() / 4, -AppDisplay.getInstance(mContext).getFingerArea() / 4);
                        if (bbox.contains(pageViewPt.x, pageViewPt.y)) {
                            mTouchCaptured = 1;
                            mOp = UIAnnotFrame.OP_TRANSLATE;
                            mDownPt.set(pageViewPt);
                            mLastPt.set(pageViewPt);
                            mMoveDist = 0;
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mTouchCaptured == 2) {
                    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                        mTouchCaptured = 0;
                    }
                    return true;
                }
                if (mTouchCaptured == 1 && mFocusAnnot != null && pageIndex == mFocusAnnot.mPageIndex) {

                    if (pageViewPt.x != mLastPt.x || pageViewPt.y != mLastPt.y) {
                        if (isPopoverShowing()) {
                            dismissPopover();
                        }

                        RectF pvBox = new RectF(mFocusAnnot.mBBox);
                        mPdfViewCtrl.convertPdfRectToPageViewRect(pvBox, pvBox, pageIndex);
                        RectF bounds0;
                        RectF bounds1;
                        PointF adjust = new PointF();
                        if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_CHECK
                                || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_X
                                || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_DOT) {
                            bounds0 = UIAnnotFrame.mapFrameBounds(pvBox, _borderThickness(), mOp, mCtl,
                                    mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y, 5, true);
                            bounds1 = UIAnnotFrame.mapFrameBounds(pvBox, _borderThickness(), mOp, mCtl,
                                    pageViewPt.x - mDownPt.x, pageViewPt.y - mDownPt.y, 5, true);

                            if (mOp == UIAnnotFrame.OP_TRANSLATE) {
                                adjust = UIAnnotFrame.getInstance(mContext).calculateCorrection(mPdfViewCtrl, pageIndex, bounds1, mOp, mCtl);
                                UIAnnotFrame.adjustBounds(bounds1, mOp, mCtl, adjust);

                                bounds1.union(bounds0);
                            } else if (mOp == UIAnnotFrame.OP_SCALE) {
                                bounds1.union(bounds0);
                                adjust = UIAnnotFrame.getInstance(mContext).calculateCornerScale(mPdfViewCtrl, pageIndex, pvBox, bounds1, mCtl);
                                UIAnnotFrame.adjustBounds(bounds1, mOp, mCtl, adjust);
                            }

                            PointF lastPointF = new PointF(pageViewPt.x + adjust.x, pageViewPt.y + adjust.y);
                            if (mCtl == UIAnnotFrame.CTL_LEFT_TOP || mCtl == UIAnnotFrame.CTL_RIGHT_TOP) {
                                if (lastPointF.y <= pvBox.bottom)
                                    mLastPt.set(lastPointF.x, lastPointF.y);
                            } else if (mCtl == UIAnnotFrame.CTL_LEFT_BOTTOM || mCtl == UIAnnotFrame.CTL_RIGHT_BOTTOM) {
                                if (lastPointF.y >= pvBox.top)
                                    mLastPt.set(lastPointF.x, lastPointF.y);
                            } else {
                                mLastPt.set(lastPointF.x, lastPointF.y);
                            }
                        } else {
                            if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_TEXT || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
                                float fontSize = mProperty.getFontSizeDp(mPdfViewCtrl, pageIndex, mFocusAnnot.mFontSize);
                                float lineWidth = getLineWidth(mFocusAnnot.mTag, fontSize, mFocusAnnot.mContent);
                                pvBox.right = pvBox.left + lineWidth + _editView().getPaddingLeft() + _editView().getPaddingRight();
                            }

                            bounds0 = UIAnnotFrame.mapFrameBounds(pvBox, _borderThickness(), mOp, mCtl,
                                    mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y, 5);
                            bounds1 = UIAnnotFrame.mapFrameBounds(pvBox, _borderThickness(), mOp, mCtl,
                                    pageViewPt.x - mDownPt.x, pageViewPt.y - mDownPt.y, 5);

                            adjust = UIAnnotFrame.getInstance(mContext).calculateCorrection(mPdfViewCtrl, pageIndex, bounds1, mOp, mCtl);
                            UIAnnotFrame.adjustBounds(bounds1, mOp, mCtl, adjust);
                            bounds1.union(bounds0);

                            mLastPt.set(pageViewPt.x + adjust.x, pageViewPt.y + adjust.y);
                        }

                        float dist = (float) Math.sqrt((mLastPt.x - mDownPt.x) * (mLastPt.x - mDownPt.x) + (mLastPt.y - mDownPt.y) * (mLastPt.y - mDownPt.y));
                        if (dist > mMoveDist) {
                            mMoveDist = dist;
                        }

                        UIAnnotFrame.getInstance(mContext).extentBoundsToContainControl(bounds1);
                        RectF displayRect = new RectF();
                        mPdfViewCtrl.convertPageViewRectToDisplayViewRect(bounds1, displayRect, pageIndex);
                        mPdfViewCtrl.invalidate(AppDmUtil.rectFToRect(displayRect));
                    }

                    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                        if (mFocusAnnot != null) {

                            if (!mLastPt.equals(mDownPt)) {
                                RectF bbox = new RectF(mFocusAnnot.mBBox);
                                mPdfViewCtrl.convertPdfRectToPageViewRect(bbox, bbox, pageIndex);

                                Matrix matrix;
                                if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_CHECK
                                        || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_X
                                        || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_DOT) {
                                    matrix = UIAnnotFrame.calculateOperateMatrix(bbox, mOp, mCtl,
                                            mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y, true);
                                } else {
                                    matrix = UIAnnotFrame.calculateOperateMatrix(bbox, mOp, mCtl,
                                            mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y);
                                }

                                transformAnnot(pageIndex, matrix);
                            } else {
                                showPopover(pageIndex, 0);

                                if (AppDisplay.getInstance(mContext).px2dp(mMoveDist) < 2) {
                                    if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_TEXT || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
                                        _editFocusTextFormObj(pageIndex, mFocusAnnot.mTag);
                                    }
                                }
                            }
                        }

                        mTouchCaptured = 0;
                        mDownPt.set(0, 0);
                        mLastPt.set(0, 0);
                        mOp = UIAnnotFrame.OP_DEFAULT;
                        mCtl = UIAnnotFrame.CTL_NONE;
                        mMoveDist = 0;
                    }
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private float _borderThickness() {
        return AppDisplay.getInstance(mContext).dp2px(10);
    }

    @Override
    public boolean onLongPress(int pageIndex, MotionEvent motionEvent) {
        return onPressCallback(true, pageIndex, motionEvent);
    }

    @Override
    public boolean onSingleTapConfirmed(int pageIndex, MotionEvent motionEvent) {
        return onPressCallback(false, pageIndex, motionEvent);
    }

    private boolean onPressCallback(boolean isLongPress, int pageIndex, MotionEvent motionEvent) {
        final PointF viewPointF = AppAnnotUtil.getPageViewPoint(mPdfViewCtrl, pageIndex, motionEvent);
        final PointF pdfPointF = AppAnnotUtil.getPdfPoint(mPdfViewCtrl, pageIndex, motionEvent);

        int itemTag = _curToolTag();
        if (isEditingText()) {
            endAddTextBox();
            return true;
        } else if (mFocusAnnot != null && mFocusAnnot.mBBox.contains(pdfPointF.x, pdfPointF.y)) {
            return true;
        } else if (focusObjectAtPoint(pageIndex, pdfPointF)) {
            return true;
        } else {
            boolean handled = false;
            if (mFocusAnnot != null) {
                focusObject(pageIndex, null);
                handled = true;
            }

            if (mFillSignModule.getCurrentItem() != null) {
                switch (itemTag) {
                    case ToolbarItemConfig.FILLSIGN_ITEM_TEXT:
                    case ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT:
                        if (isEditingText()) {
                            endAddTextBox();
                        } else {
                            addTextBox(itemTag, pageIndex, viewPointF, true, null, null, false);
                        }
                        return true;
                    case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
                    case ToolbarItemConfig.FILLSIGN_ITEM_X:
                    case ToolbarItemConfig.FILLSIGN_ITEM_DOT:
                    case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
                    case ToolbarItemConfig.FILLSIGN_ITEM_RECT:
                        addFormObject(itemTag, pageIndex, viewPointF, false);
                        return true;
                    case ToolbarItemConfig.FILLSIGN_ITEM_PROFILE:
                        addProfileObject(itemTag, pageIndex, viewPointF);
                        return true;
                }
            }

            if (isLongPress) {
                if (!mUIExtensionsManager.getDocumentManager().hasForm() && mUIExtensionsManager.getDocumentManager().canModifyContents()) {
                    mLongPressPage = pageIndex;
                    mLongPressPt = new PointF(pdfPointF.x, pdfPointF.y);
                    showPopover(pageIndex, 2);
                    return true;
                }
            }
            return handled;
        }
    }

    @Override
    public boolean isContinueAddAnnot() {
        return false;
    }

    @Override
    public void setContinueAddAnnot(boolean continueAddAnnot) {
    }

    void onDrawForControls(Canvas canvas) {
        ToolHandler curToolHandler = mUIExtensionsManager.getCurrentToolHandler();
        if (curToolHandler != this) return;

        int curIndex;
        if (isEditingText()) {
            curIndex = _editView().getPageIndex();
        } else if (mFocusAnnot != null) {
            curIndex = mFocusAnnot.mPageIndex;
        } else {
            curIndex = mLongPressPage;
        }

        if (isEditingText()) {
            if (!mPdfViewCtrl.isPageVisible(curIndex)) {
                dismissPopover();
                return;
            }

            PointF offset = new PointF(_editView().getDocLtOffset().x, _editView().getDocLtOffset().y);
            mPdfViewCtrl.convertPdfPtToPageViewPt(offset, offset, curIndex);
            mPdfViewCtrl.convertPageViewPtToDisplayViewPt(offset, offset, curIndex);
            offset.y -= getBottomOffset();

            int rawScreenHeight = AppDisplay.getInstance(mContext).getRawScreenHeight();
            int navBarHeight = AppDisplay.getInstance(mContext).getRealNavBarHeight();
            int keyboardHeight = AppKeyboardUtil.getKeyboardHeight(mUIExtensionsManager.getRootView());
            if ((offset.y + keyboardHeight + navBarHeight)> rawScreenHeight){
                _editView().setVisibility(View.INVISIBLE);
                dismissPopover();
            } else {
                _editView().setVisibility(View.VISIBLE);

                setEditViewMargin((int) offset.x, (int) (offset.y));

                int storeW = _editView().getPvSize().x;
                int curW = mPdfViewCtrl.getPageViewWidth(curIndex);
                if (curW != storeW) {
                    float fontSize = mProperty.getFontSizeDp(mPdfViewCtrl, curIndex);
                    _editView().setTextSize(fontSize);
                }

                RectF editBox = getEditViewBoxInDv();
                if (_editView().getTextStyle() == FillSignEditText.STYLE_COMBO_TEXT) {
                    editBox.right -= _editView().getPaddingRight() / 2;
                }

                RectF dvBox = new RectF(0, 0,
                        mUIExtensionsManager.getRootView().getWidth(),
                        mUIExtensionsManager.getRootView().getHeight());

                if (RectF.intersects(dvBox, editBox)) {
                    updatePopover(curIndex, mPopoverAction);
                } else {
                    dismissPopover();
                }
            }
        } else if (mFocusAnnot != null) {
            if (!mPdfViewCtrl.isPageVisible(curIndex)) {
                dismissPopover();
                return;
            }

            RectF bbox = new RectF(mFocusAnnot.mBBox);
            mPdfViewCtrl.convertPdfRectToPageViewRect(bbox, bbox, curIndex);
            mPdfViewCtrl.convertPageViewRectToDisplayViewRect(bbox, bbox, curIndex);

            RectF dvBox = new RectF(0, 0,
                    mUIExtensionsManager.getRootView().getWidth(),
                    mUIExtensionsManager.getRootView().getHeight());

            if (RectF.intersects(dvBox, bbox)) {
                updatePopover(curIndex, mPopoverAction);
            } else {
                dismissPopover();
            }
        } else {
            if (mLongPressPt != null) {
                if (!mPdfViewCtrl.isPageVisible(curIndex)) {
                    dismissPopover();
                    return;
                }

                RectF bbox = new RectF(mLongPressPt.x, mLongPressPt.y, mLongPressPt.x + 2, mLongPressPt.y + 2);
                mPdfViewCtrl.convertPdfRectToPageViewRect(bbox, bbox, curIndex);
                mPdfViewCtrl.convertPageViewRectToDisplayViewRect(bbox, bbox, curIndex);

                RectF dvBox = new RectF(0, 0,
                        mPdfViewCtrl.getDisplayViewWidth(),
                        mPdfViewCtrl.getDisplayViewHeight());

                if (RectF.intersects(dvBox, bbox)) {
                    updatePopover(curIndex, 2);
                } else {
                    dismissPopover();
                }
            }
        }
    }

    @Override
    public void onDraw(int pageIndex, Canvas canvas) {
        if (mFocusAnnot == null || mFocusAnnot.mPageIndex != pageIndex) {
            return;
        }

        RectF bbox = new RectF(mFocusAnnot.mBBox);
        mPdfViewCtrl.convertPdfRectToPageViewRect(bbox, bbox, pageIndex);

        int tag = mFocusAnnot.mTag;
        RectF mapBounds;
        RectF drawBounds;
        Matrix matrix;
        if (tag == ToolbarItemConfig.FILLSIGN_ITEM_CHECK
                || tag == ToolbarItemConfig.FILLSIGN_ITEM_X
                || tag == ToolbarItemConfig.FILLSIGN_ITEM_DOT) {
            matrix = UIAnnotFrame.calculateOperateMatrix(bbox, mOp, mCtl, mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y, true);
            mapBounds = UIAnnotFrame.mapFrameBounds(bbox, _borderThickness(), mOp, mCtl, mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y, 5, true);
            drawBounds = UIAnnotFrame.mapFrameBounds(bbox, 0, mOp, mCtl, mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y, 0, true);
        } else {
            matrix = UIAnnotFrame.calculateOperateMatrix(bbox, mOp, mCtl, mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y);
            mapBounds = UIAnnotFrame.mapFrameBounds(bbox, _borderThickness(), mOp, mCtl, mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y, 5);
            drawBounds = UIAnnotFrame.mapFrameBounds(bbox, 0, mOp, mCtl, mLastPt.x - mDownPt.x, mLastPt.y - mDownPt.y, 0);
        }

        int color = AppResource.getColor(mContext, R.color.ux_color_blue_ff179cd8, null);
        int opacity = 255;

        if (!matrix.equals(new Matrix())) {
            switch (tag) {
                case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
                    FillSignPathDraw.drawCheck(canvas, drawBounds, color);
                    break;
                case ToolbarItemConfig.FILLSIGN_ITEM_X:
                    FillSignPathDraw.drawX(canvas, drawBounds, color);
                    break;
                case ToolbarItemConfig.FILLSIGN_ITEM_DOT:
                    FillSignPathDraw.drawDot(canvas, drawBounds, color);
                    break;
                case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
                    FillSignPathDraw.drawLine(mPdfViewCtrl, pageIndex, canvas, drawBounds, color);
                    break;
                case ToolbarItemConfig.FILLSIGN_ITEM_RECT:
                    FillSignPathDraw.drawRect(mPdfViewCtrl, pageIndex, canvas, drawBounds, color);
                    break;
                case ToolbarItemConfig.FILLSIGN_ITEM_TEXT:
                case ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT:
                    float fontSize = mProperty.getFontSizeDp(mPdfViewCtrl, pageIndex, mFocusAnnot.mFontSize);
                    float lineHeight = getLineHeight(fontSize);
                    FillSignPathDraw.drawText(mPdfViewCtrl, pageIndex, canvas, drawBounds, color, mFocusAnnot, 0, lineHeight);
                    break;
            }
        }

        switch (tag) {
            case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
            case ToolbarItemConfig.FILLSIGN_ITEM_X:
            case ToolbarItemConfig.FILLSIGN_ITEM_DOT:
                UIAnnotFrame.getInstance(mContext).drawCorner(canvas, mapBounds, color, opacity);
                break;
            case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
                UIAnnotFrame.getInstance(mContext).drawLineControls(canvas,
                        new PointF(drawBounds.left, drawBounds.centerY()), new PointF(drawBounds.right, drawBounds.centerY()),
                        color, opacity, 0);
                break;
            case ToolbarItemConfig.FILLSIGN_ITEM_RECT:
                UIAnnotFrame.getInstance(mContext).draw(canvas, mapBounds, color, opacity);
                break;
            case ToolbarItemConfig.FILLSIGN_ITEM_TEXT:
            case ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT:
                float fontSize = mProperty.getFontSizeDp(mPdfViewCtrl, pageIndex, mFocusAnnot.mFontSize);
                float lineWidth = getLineWidth(mFocusAnnot.mTag, fontSize, mFocusAnnot.mContent) + _editView().getPaddingRight() + _editView().getPaddingLeft();
                if (lineWidth > drawBounds.width())
                    drawBounds.right = drawBounds.left + lineWidth;
                UIAnnotFrame.getInstance(mContext).drawFrame(canvas, drawBounds, color, opacity);
                break;
        }

        AppThreadManager.getInstance().getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                _adjustNavBarPadding();
            }
        });
    }

    private float getLineWidth(int tag, float fontSize, String content) {
        _editView().setTextSize(fontSize);
        if (tag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
            _editView().setLetterSpacing(mProperty.mFontSpacing);
            _editView().setPadding(0, 0, _editView().getStratchBmp().getWidth(), 0);
        } else {
            _editView().setTextStyle(FillSignEditText.STYLE_TEXT);
            _editView().setLetterSpacing(0);
            _editView().setPadding(0, 0, 0, 0);
        }

        Paint paint = _editView().getPaint();
        float maxWidth = 0;
        ArrayList<String> textArray = FillSignUtils.jniToJavaTextLines(content);
        for (int i = 0; i < textArray.size(); i++) {
            float width = paint.measureText(textArray.get(i));
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    private float getLineHeight(float fontSize) {
        _editView().setTextSize(fontSize);
        return (float) _editView().getLineHeight();
    }

    private int getMaxLines() {
        float lineHeight = _editView().getLineHeight();

        int pageIndex = _editView().mPageIndex;
        PointF dvPt = new PointF(_editView().getDocLtOffset().x, _editView().getDocLtOffset().y);
        mPdfViewCtrl.convertPdfPtToPageViewPt(dvPt, dvPt, pageIndex);
        float maxHeight = mPdfViewCtrl.getPageViewHeight(pageIndex) - dvPt.y;
        return (int) (maxHeight / lineHeight) == 0 ? 1 : (int) (maxHeight / lineHeight);
    }

    private boolean focusObjectAtPoint(final int pageIndex, PointF docPt) {
        if (mUIExtensionsManager.getDocumentManager().hasForm() || !mUIExtensionsManager.getDocumentManager().canModifyContents())
            return false;

        try {
            FillSign fillSign = getFillSign(pageIndex);
            if (fillSign == null) return false;

            final FillSignObject fillSignObject = fillSign.getObjectAtPoint(AppUtil.toFxPointF(docPt));
            if (fillSignObject != null && !fillSignObject.isEmpty()) {

                final RectF objRect = AppUtil.toRectF(fillSignObject.getRect());
                final FormObject formObj = new FormObject(getFillObjTag(fillSignObject), pageIndex, objRect);
                formObj.mFillObj = fillSignObject;
                FillSignUtils.getTextFillSignInfo(fillSignObject, new IResult<String, Float, Float>() {
                    @Override
                    public void onResult(boolean success, String content, Float fontSize, Float charSpace) {
                        if (success) {
                            formObj.mContent = content;
                            formObj.mCharspace = charSpace;
                            formObj.mSpacing = charSpace / fontSize;
                            formObj.mFontSize = fontSize;
                        }
                    }
                });
                focusObject(pageIndex, formObj);
                if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                    RectF objectRect = new RectF(objRect);
                    mPdfViewCtrl.convertPdfRectToPageViewRect(objectRect, objectRect, pageIndex);
                    mPdfViewCtrl.convertPageViewRectToDisplayViewRect(objectRect, objectRect, pageIndex);
                    mPdfViewCtrl.invalidate(AppDmUtil.rectFToRect(objectRect));
                }
                return true;
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return false;
    }

    void onItemClicked(IBaseItem item) {
        if (isEditingText()) {
            endAddTextBox();
        }

        int tag = (int) item.getContentView().getTag();
        if (tag == ToolbarItemConfig.FILLSIGN_ITEM_PROFILE) {
            if (mFocusAnnot != null) {
                focusObject(mFocusAnnot.mPageIndex, null);
            }
        }
    }

    boolean isEditingText() {
        return mEditView != null && mEditView.getParent() != null;
    }

    void setEditText(String text) {
        if (!isEditingText())
            return;

        _editView().setText(text);
    }

    private void showEditView(boolean showKeyboard) {
        _editView();

        if (mEditView.getParent() == null) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            mUIExtensionsManager.getRootView().addView(mEditView, 1, lp);
        }
        mEditView.setText("");
        mEditView.setVisibility(View.VISIBLE);
        if (showKeyboard) {
            AppUtil.showSoftInput(mEditView);
            registerKeyboardListener();
            _keyboardBar().show();
        }
    }

    private void deleteEditView() {
        if (mEditView != null) {
            if (mEditView.getParent() != null) {
                mLastLineCount = -1;
                AppUtil.dismissInputSoft(mEditView);
                mUIExtensionsManager.getRootView().removeView(mEditView);
            }
        }

        dismissPopover();

        AppKeyboardUtil.removeKeyboardListenerForNothingAct(null);
        _keyboardBar().hide();

        jumpToEditView();
    }

    private void jumpToEditView() {
        if (getBottomOffset() != 0) {
            correctDvOffsetValue();
        }
        if (!isEditingText()) {
            setBottomOffset(0, 0);
            return;
        }

        int pageIndex = _editView().getPageIndex();
        if (!mPdfViewCtrl.isPageVisible(pageIndex)) {
            setBottomOffset(0, 0);
            return;
        }

        RectF editBox = getEditViewBoxInDv();
        mPdfViewCtrl.convertDisplayViewRectToPageViewRect(editBox, editBox, pageIndex);
        mPdfViewCtrl.convertPageViewRectToPdfRect(editBox, editBox, pageIndex);

        adjustBoxToDisplayArea(pageIndex, editBox);
        adjustCaretInDocViewer();

        if (getBottomOffset() != 0) {
            correctDvOffsetValue();
        }

        PointF offset = new PointF(_editView().getDocLtOffset().x, _editView().getDocLtOffset().y);
        mPdfViewCtrl.convertPdfPtToPageViewPt(offset, offset, pageIndex);
        mPdfViewCtrl.convertPageViewPtToDisplayViewPt(offset, offset, pageIndex);
        offset.y -= getBottomOffset();
        setEditViewMargin((int) offset.x, (int) (offset.y));

        updatePopover(pageIndex, mPopoverAction);
    }

    private void updatePopover(final int pageIndex, final int action) {
        AppThreadManager.getInstance().getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                showPopover(pageIndex, action);
            }
        });
    }

    private void _adjustNavBarPadding() {
        if (_keyboardBar().isShow()) {
            adjustNavBarPadding();
        }
    }

    private FillSignKeyboardBar mKeyboardBar;

    private FillSignKeyboardBar _keyboardBar() {
        if (mKeyboardBar == null) {
            mKeyboardBar = new FillSignKeyboardBar(mContext, mUIExtensionsManager, this);
        }
        return mKeyboardBar;
    }

    private AppKeyboardUtil.IKeyboardListener mKeyboardListener;

    private AppKeyboardUtil.IKeyboardListener registerKeyboardListener() {
        if (mKeyboardListener == null) {
            mKeyboardListener = new AppKeyboardUtil.IKeyboardListener() {
                @Override
                public void onKeyboardOpened(int keyboardHeight) {
                    _adjustNavBarPadding();
                    AppThreadManager.getInstance().getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            _adjustNavBarPadding();
                            jumpToEditView();
                            _keyboardBar().show();
                        }
                    }, 100);
                }

                @Override
                public void onKeyboardClosed() {
                    if (_editView().getVisibility() == View.INVISIBLE)
                        _editView().setVisibility(View.VISIBLE);
                    _adjustNavBarPadding();
                    AppThreadManager.getInstance().getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            _adjustNavBarPadding();
                            jumpToEditView();
                            _keyboardBar().hide();
                        }
                    }, 100);
                }
            };
        }

        AppKeyboardUtil.addWndKeyboardListenerForNothingAct(mUIExtensionsManager.getAttachedActivity(), mUIExtensionsManager.getRootView(), mKeyboardListener);
        return mKeyboardListener;
    }

    private FillSignEditText mEditView;

    private FillSignEditText _editView() {
        if (mEditView == null) {
            mEditView = new FillSignEditText(mContext.getApplicationContext()) {
                @Override
                public boolean onTouchEvent(MotionEvent event) {
                    return super.onTouchEvent(event);
                }
            };

            mEditView.setProperty(mProperty);
            mEditView.setSingleLine(false);
            mEditView.setText("");
            mEditView.setBackground(null);
            mEditView.setPadding(0, 0, 0, 0);
            mEditView.setInputType(mEditView.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            mEditView.setTextColor(Color.BLACK);
            mEditView.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
            mEditView.setImeOptions(EditorInfo.IME_ACTION_NONE);

            mEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (AppDevice.isChromeOs(mUIExtensionsManager.getAttachedActivity())) {
                            AppThreadManager.getInstance().getMainThreadHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    String str = mEditView.getText() + "\n";
                                    mEditView.setText(str);
                                    mEditView.setSelection(str.length());
                                }
                            });
                        } else {
                            endAddTextBox();
                        }
                        return true;
                    }
                    return false;
                }
            });

            mEditView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (isEditingText()) {
                            float lineCounts = _editView().getLineCount();
                            if (lineCounts + 1 > getMaxLines())
                                return true;

                            int pageIndex = _editView().mPageIndex;
                            PointF offset = new PointF(_editView().getDocLtOffset().x, _editView().getDocLtOffset().y);
                            mPdfViewCtrl.convertPdfPtToPageViewPt(offset, offset, pageIndex);
                            mPdfViewCtrl.convertPageViewPtToDisplayViewPt(offset, offset, pageIndex);
                            offset.y -= getBottomOffset();
                            setEditViewMargin((int) offset.x, (int) (offset.y));
                        }
                    }
                    return false;
                }
            });

            mEditView.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    _keyboardBar().updatePrompt(_editView().getText().toString(), mFillSignModule.mProfileInfo);
                    AppThreadManager.getInstance().getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adjustCaretInDocViewer();

                            int pageIndex = _editView().getPageIndex();
                            int lineCount = _editView().getLineCount();
                            float lineHeight = _editView().getLineHeight();
                            float needH = lineCount * lineHeight;
                            float maxHeight = _editView().getMaxHeight();
                            if (needH > maxHeight) {
                                _editView().setMaxHeight((int) needH);

                                PointF offset = new PointF(_editView().getDocLtOffset().x, _editView().getDocLtOffset().y);
                                mPdfViewCtrl.convertPdfPtToPageViewPt(offset, offset, pageIndex);
                                mPdfViewCtrl.convertPageViewPtToDisplayViewPt(offset, offset, pageIndex);
                                offset.y -= getBottomOffset();
                                offset.y -= (needH - maxHeight);
                                setEditViewMargin((int) offset.x, (int) offset.y);

                                PointF docPt = new PointF(offset.x, offset.y + getBottomOffset());
                                mPdfViewCtrl.convertDisplayViewPtToPageViewPt(docPt, docPt, pageIndex);
                                mPdfViewCtrl.convertPageViewPtToPdfPt(docPt, docPt, pageIndex);
                                _editView().setDocLtOffset(docPt);
                            } else {
                                if (mLastLineCount != -1 && mLastLineCount != lineCount) {
                                    PointF offset = new PointF(_editView().getDocLtOffset().x, _editView().getDocLtOffset().y);
                                    mPdfViewCtrl.convertPdfPtToPageViewPt(offset, offset, pageIndex);
                                    mPdfViewCtrl.convertPageViewPtToDisplayViewPt(offset, offset, pageIndex);
                                    offset.y -= getBottomOffset();
                                    setEditViewMargin((int) offset.x, (int) offset.y);
                                }
                            }
                            updatePopover(_editView().mPageIndex, 0);
                            mLastLineCount = lineCount;
                        }
                    }, 50);
                }
            });

            mEditView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
        }
        return mEditView;
    }

    private boolean mAddLTPointF;

    private void addTextBox(int tag, final int pageIndex, PointF pvPt, boolean showKeyboard, Float docFontSize, String text, boolean ltPt) {
        mAddLTPointF = ltPt;
        showEditView(showKeyboard);

        float textSize = mProperty.getFontSizeDp(mPdfViewCtrl, pageIndex);
        if (docFontSize != null) {
            textSize = mProperty.getFontSizeDp(mPdfViewCtrl, pageIndex, docFontSize);
        }

        _editView().setTextSize(textSize);
        if (tag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
            _editView().setTextStyle(FillSignEditText.STYLE_COMBO_TEXT);
            _editView().setLetterSpacing(mProperty.mFontSpacing);
            _editView().setPadding(0, 0, _editView().getStratchBmp().getWidth(), 0);
        } else {
            _editView().setTextStyle(FillSignEditText.STYLE_TEXT);
            _editView().setLetterSpacing(0);
            _editView().setPadding(0, 0, 0, 0);
        }
        if (text != null) {
            _editView().setText(text);
            _editView().setSelection(_editView().getText().length());
        }

        float textSizePx = _editView().getTextSize();
        RectF textBox = new RectF(pvPt.x, pvPt.y, (pvPt.x + textSizePx / 2), (pvPt.y + textSizePx));
        if (!ltPt) {
            textBox.offset(-textBox.width() / 2, -textBox.height() / 2);
        }

        Paint paint = _editView().getPaint();
        float wordW = paint.measureText(AppResource.getString(mContext, R.string.fx_place_holder));
        float needW = wordW + _editView().getPaddingRight() + _editView().getPaddingLeft();
        float needH = _editView().getLineHeight();

        int mw = (int) (mPdfViewCtrl.getPageViewWidth(pageIndex) - pvPt.x);
        int mh = (int) (mPdfViewCtrl.getPageViewHeight(pageIndex) - pvPt.y);
        if (mw < needW) {
            textBox.offset(mw - needW, 0);
            mw = (int) needW;
        }
        if (mh < needH) {
            textBox.offset(0, mh - needH);
            mh = (int) needH;
        }

        _editView().setMinWidth((int) textBox.width());
        _editView().setMinHeight((int) textBox.height());
        _editView().setMaxWidth(mw);
        _editView().setMaxHeight(mh);
        _editView().setMarginRight(FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, FillSignEditText.SPACING));

        mPdfViewCtrl.convertPageViewRectToDisplayViewRect(textBox, textBox, pageIndex);
        setEditViewMargin((int) textBox.left, (int) textBox.top);

        PointF docPt = new PointF(textBox.left, textBox.top);
        mPdfViewCtrl.convertDisplayViewPtToPageViewPt(docPt, docPt, pageIndex);
        mPdfViewCtrl.convertPageViewPtToPdfPt(docPt, docPt, pageIndex);

        _editView().setPageIndex(pageIndex);
        _editView().setPvSize(new Point(mPdfViewCtrl.getPageViewWidth(pageIndex), mPdfViewCtrl.getPageViewHeight(pageIndex)));
        _editView().setDocLtOffset(docPt);

        if (showKeyboard) {
            showPopover(0, new RectF(textBox));
        }
    }

    void endAddTextBox() {
        endAddTextBox(null);
    }

    void endAddTextBox(final Event.Callback callback) {
        final int pageIndex = _editView().getPageIndex();
        try {
            PDFPage page = mUIExtensionsManager.getDocumentManager().getPage(pageIndex, false);
            int rotation = (page.getRotation() + mPdfViewCtrl.getViewRotation()) % 4;

            if (AppUtil.isEmpty(_editView().getText())) {
                deleteEditView();
                dismissPopover();

                if (mOldAnnot != null) {
                    final FillSignDeleteUndoItem undoItem = new FillSignDeleteUndoItem(mPdfViewCtrl, this);
                    undoItem.mRectF = new RectF(mOldAnnot.mBBox);
                    undoItem.mPageIndex = pageIndex;
                    undoItem.mType = getFillObjType(mOldAnnot.mTag);
                    undoItem.mContent = mOldAnnot.mContent;
                    undoItem.mCharspace = mOldAnnot.mCharspace;
                    undoItem.mFontSize = mOldAnnot.mFontSize;
                    undoItem.mRotation = rotation;

                    FillSignEvent event = new FillSignEvent(EditAnnotEvent.EVENTTYPE_DELETE, mOldAnnot.mFillObj, undoItem, mPdfViewCtrl);
                    EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
                        @Override
                        public void result(Event event, boolean success) {
                            if (success) {
                                undoItem.setOldValue(mOldAnnot.clone());
//                            mUndoItemList.add(undoItem);
//                            mUIExtensionsManager.getDocumentManager().addUndoItem(undoItem);
                                if (mUIExtensionsManager.getCurrentToolHandler() == FillSignToolHandler.this)
                                    focusObject(pageIndex, null);

                                if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                                    RectF viewRect = new RectF(mOldAnnot.mBBox);
                                    mPdfViewCtrl.convertPdfRectToPageViewRect(viewRect, viewRect, pageIndex);
                                    mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(viewRect));
                                }
                            }

                            if (callback != null)
                                callback.result(event, success);
                        }
                    });
                    mPdfViewCtrl.addTask(task);
                }
                return;
            }

            int tag = ToolbarItemConfig.FILLSIGN_ITEM_TEXT;
            if (_editView().getTextStyle() == FillSignEditText.STYLE_COMBO_TEXT) {
                tag = ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT;
            }

            _editView().splitTextLines();
            ArrayList<String> texts = FillSignUtils.javaToJniTextLines(_editView().mLineTexts.mLineTexts);
            float letterSpacing = 0;
            if (AppBuildConfig.SDK_VERSION >= 21)
                letterSpacing = _editView().getLetterSpacing();

            final RectF evBox = getEditViewBoxInDv();
            final RectF docRectF = new RectF();
            mPdfViewCtrl.convertDisplayViewRectToPageViewRect(evBox, docRectF, pageIndex);
            mPdfViewCtrl.convertPageViewRectToPdfRect(docRectF, docRectF, pageIndex);

            float fontSize = FillSignUtils.pageViewToDocThickness(mPdfViewCtrl, pageIndex, _editView().getTextSize());

            float padding = _editView().getPaddingLeft();
            padding = FillSignUtils.pageViewToDocThickness(mPdfViewCtrl, pageIndex, padding);

            float lineHeight = _editView().getLineHeight();
            lineHeight = FillSignUtils.pageViewToDocThickness(mPdfViewCtrl, pageIndex, lineHeight);

            deleteEditView();
            dismissPopover();

            if (mOldAnnot != null) {
                int type = FillSign.e_FillSignObjectTypeText;
                final FillSignModifyUndoItem undoItem = new FillSignModifyUndoItem(mPdfViewCtrl, this);
                undoItem.mPageIndex = pageIndex;
                undoItem.mType = type;
                undoItem.mTexts = new ArrayList<>(texts);
                undoItem.mRectF = new RectF(docRectF);
                undoItem.mFontSize = fontSize;
                undoItem.mCharspace = letterSpacing * fontSize;
                undoItem.mLineHeight = lineHeight;
                undoItem.mIsCombText = tag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT;
                undoItem.mRotation = rotation;

                undoItem.mRedoRectF = new RectF(docRectF);
                undoItem.mRedoTexts = new ArrayList<>(texts);
                undoItem.mRedoFontSize = fontSize;
                undoItem.mRedoCharspace = letterSpacing * fontSize;

                undoItem.mUndoRectF = new RectF(mOldAnnot.mBBox);
                undoItem.mUndoTexts = FillSignUtils.jniToJavaTextLines(mOldAnnot.mContent);
                undoItem.mUndoFontSize = mOldAnnot.mFontSize;
                undoItem.mUndoCharspace = mOldAnnot.mCharspace;

                FillSignEvent event = new FillSignEvent(EditAnnotEvent.EVENTTYPE_MODIFY, undoItem, mPdfViewCtrl);
                final int finalTag = tag;
                EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
                    @Override
                    public void result(Event event, boolean success) {
                        if (success) {
                            try {
                                final FillSignObject newObj = ((FillSignEvent) event).mFillSignObj;
                                RectF newRectF = AppUtil.toRectF(newObj.getRect());
                                final FormObject formObj = new FormObject(finalTag, pageIndex, new RectF(newRectF));
                                formObj.mFillObj = newObj;

                                FillSignUtils.getTextFillSignInfo(newObj, new IResult<String, Float, Float>() {
                                    @Override
                                    public void onResult(boolean success, String content, Float fontsize, Float charSpace) {
                                        if (success) {
                                            formObj.mContent = content;
                                            formObj.mCharspace = charSpace;
                                            formObj.mSpacing = charSpace / fontsize;
                                            formObj.mFontSize = fontsize;
                                        }
                                    }
                                });

                                FillSignObject oldFillObj = mOldAnnot.mFillObj;
                                mOldAnnot.mFillObj = newObj;
                                undoItem.setOldValue(mOldAnnot.clone());
                                undoItem.setCurrentValue(formObj.clone());
//                                mUndoItemList.add(undoItem);
//                                mUIExtensionsManager.getDocumentManager().addUndoItem(undoItem);
//                                updateUndoItem(oldFillObj, newObj);

                                if (mUIExtensionsManager.getCurrentToolHandler() == FillSignToolHandler.this)
                                    focusObject(pageIndex, formObj);

                                if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                                    RectF viewRect = new RectF(newRectF);
                                    mPdfViewCtrl.convertPdfRectToPageViewRect(viewRect, viewRect, pageIndex);
                                    mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(viewRect));
                                }
                            } catch (PDFException e) {
                                e.printStackTrace();
                            }
                        }

                        if (callback != null)
                            callback.result(event, success);
                    }
                });
                mPdfViewCtrl.addTask(task);

            } else {
                final FillSignAddUndoItem undoItem = new FillSignAddUndoItem(mPdfViewCtrl, this);

                undoItem.mPageIndex = pageIndex;
                undoItem.mType = FillSign.e_FillSignObjectTypeText;
                undoItem.mTexts = new ArrayList<>(texts);
                undoItem.mRectF = new RectF(docRectF);
                undoItem.mFontSize = fontSize;
                undoItem.mCharspace = letterSpacing * fontSize;
                undoItem.mLineHeight = lineHeight;
                undoItem.mIsCombText = tag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT;
                undoItem.mRotation = rotation;

                FillSignEvent event = new FillSignEvent(EditAnnotEvent.EVENTTYPE_ADD, undoItem, mPdfViewCtrl);
                final int finalTag = tag;
                EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
                    @Override
                    public void result(Event event, boolean success) {
                        if (success) {
                            try {
                                FillSignObject newFillObject = ((FillSignEvent) event).mFillSignObj;
                                if (newFillObject == null || newFillObject.isEmpty()) return;
                                RectF newRectF = AppUtil.toRectF(newFillObject.getRect());

                                final FormObject formObj = new FormObject(finalTag, pageIndex, new RectF(newRectF));
                                formObj.mFillObj = newFillObject;
                                FillSignUtils.getTextFillSignInfo(newFillObject, new IResult<String, Float, Float>() {
                                    @Override
                                    public void onResult(boolean success, String content, Float fontsize, Float charSpace) {
                                        if (success) {
                                            formObj.mContent = content;
                                            formObj.mCharspace = charSpace;
                                            formObj.mSpacing = charSpace / fontsize;
                                            formObj.mFontSize = fontsize;
                                        }
                                    }
                                });

                                FormObject newObj = formObj.clone();
                                undoItem.setCurrentValue(newObj);
//                                mUndoItemList.add(undoItem);
//                                mUIExtensionsManager.getDocumentManager().addUndoItem(undoItem);
                                if (mUIExtensionsManager.getCurrentToolHandler() == FillSignToolHandler.this)
                                    focusObject(pageIndex, formObj);

                                if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                                    RectF viewRect = new RectF(newRectF);
                                    mPdfViewCtrl.convertPdfRectToPageViewRect(viewRect, viewRect, pageIndex);
                                    mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(viewRect));
                                }
                            } catch (PDFException e) {
                                e.printStackTrace();
                            }
                        }

                        if (callback != null)
                            callback.result(event, success);
                    }
                });
                mPdfViewCtrl.addTask(task);
            }

        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    private void transformAnnot(int pageIndex, Matrix matrix) {
        if (mFocusAnnot == null)
            return;

        RectF bbox = new RectF(mFocusAnnot.mBBox);
        mPdfViewCtrl.convertPdfRectToPageViewRect(bbox, bbox, pageIndex);
        matrix.mapRect(bbox);

        if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_LINE) {
            float defHeight = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, mProperty.mLineSize.y);
            bbox.top = bbox.centerY() - defHeight / 2;
            bbox.bottom = bbox.centerY() + defHeight / 2;
        }

        RectF displayRect = new RectF();
        mPdfViewCtrl.convertPageViewRectToDisplayViewRect(bbox, displayRect, pageIndex);
        showPopover(0, displayRect);

        RectF docRecF = new RectF();
        mPdfViewCtrl.convertPageViewRectToPdfRect(bbox, docRecF, pageIndex);
        mFocusAnnot.mBBox.set(docRecF);

        setProperty(pageIndex, docRecF);
        modifyCurFormObject(pageIndex);
    }

    private void addFormObject(final int tag, final int pageIndex, PointF pvPt, boolean ltPoint) {
        PointF size = new PointF();
        switch (tag) {
            case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
            case ToolbarItemConfig.FILLSIGN_ITEM_X:
            case ToolbarItemConfig.FILLSIGN_ITEM_DOT:
                float checkSize = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, mProperty.mCheckSize);
                size.set(checkSize, checkSize);
                break;
            case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
                float lineSizeX = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, mProperty.mLineSize.x);
                float lineSizeY = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, mProperty.mLineSize.y);
                size.set(lineSizeX, lineSizeY);
                break;
            case ToolbarItemConfig.FILLSIGN_ITEM_RECT:
                float rectSizeX = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, mProperty.mRectSize.x);
                float rctSizeY = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, mProperty.mRectSize.y);
                size.set(rectSizeX, rctSizeY);
                break;
        }

        RectF viewBox = new RectF(pvPt.x, pvPt.y, pvPt.x, pvPt.y);
        if (ltPoint) {
            viewBox.right = viewBox.left + size.x;
            viewBox.bottom = viewBox.top - size.y;
        } else {
            viewBox.inset(-size.x / 2, -size.y / 2);
        }
        PointF adjust = UIAnnotFrame.getInstance(mContext).calculateTranslateCorrection(mPdfViewCtrl, pageIndex, viewBox);
        viewBox.offset(adjust.x, adjust.y);
        RectF docRect = new RectF();
        mPdfViewCtrl.convertPageViewRectToPdfRect(viewBox, docRect, pageIndex);

        try {
            FillSign fillSign = getFillSign(pageIndex);
            if (fillSign == null) return;

            PDFPage page = mUIExtensionsManager.getDocumentManager().getPage(pageIndex, false);
            int rotation = (page.getRotation() + mPdfViewCtrl.getViewRotation()) % 4;

            int objType = getFillObjType(tag);
            final FillSignAddUndoItem undoItem = new FillSignAddUndoItem(mPdfViewCtrl, this);
            undoItem.mType = objType;
            undoItem.mRectF = new RectF(docRect);
            undoItem.mPageIndex = pageIndex;
            undoItem.mRotation = rotation;

            FillSignEvent event = new FillSignEvent(EditAnnotEvent.EVENTTYPE_ADD, undoItem, mPdfViewCtrl);
            EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
                @Override
                public void result(Event event, boolean success) {
                    try {
                        if (success) {
                            FillSignObject newFillObj = ((FillSignEvent) event).mFillSignObj;
                            RectF objRect = AppUtil.toRectF(newFillObj.getRect());
                            FormObject obj = new FormObject(tag, pageIndex, new RectF(objRect));
                            obj.mFillObj = newFillObj;
                            focusObject(pageIndex, obj);

                            FormObject newObj = obj.clone();
                            undoItem.setCurrentValue(newObj);
//                            mUndoItemList.add(undoItem);
//                            mUIExtensionsManager.getDocumentManager().addUndoItem(undoItem);

                            if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                                RectF viewRect = new RectF();
                                mPdfViewCtrl.convertPdfRectToPageViewRect(objRect, viewRect, pageIndex);
                                mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(viewRect));
                            }
                        }
                    } catch (PDFException e) {
                        e.printStackTrace();
                    }
                }
            });
            mPdfViewCtrl.addTask(task);

        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    private void addProfileObject(int tag, int pageIndex, PointF pvPt) {
        mFillSignModule.setCurrentItem(null);
        String content = mFillSignModule.mProfileStr;
        if (AppUtil.isEmpty(content)) return;

        addTextBox(tag, pageIndex, pvPt, false, null, content, false);
        AppThreadManager.getInstance().getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                endAddTextBox();
            }
        }, 50);
    }

    private int getFillObjTag(FillSignObject fillSignObject) {
        try {
            int type = fillSignObject.getType();
            switch (type) {
                case FillSign.e_FillSignObjectTypeText:
                    TextFillSignObject textFillSignObject = new TextFillSignObject(fillSignObject);
                    boolean isComText = textFillSignObject.isCombFieldMode();
                    if (isComText)
                        return ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT;
                    else
                        return ToolbarItemConfig.FILLSIGN_ITEM_TEXT;
                case FillSign.e_FillSignObjectTypeCheckMark:
                    return ToolbarItemConfig.FILLSIGN_ITEM_CHECK;
                case FillSign.e_FillSignObjectTypeCrossMark:
                    return ToolbarItemConfig.FILLSIGN_ITEM_X;
                case FillSign.e_FillSignObjectTypeDot:
                    return ToolbarItemConfig.FILLSIGN_ITEM_DOT;
                case FillSign.e_FillSignObjectTypeLine:
                    return ToolbarItemConfig.FILLSIGN_ITEM_LINE;
                case FillSign.e_FillSignObjectTypeRoundRectangle:
                    return ToolbarItemConfig.FILLSIGN_ITEM_RECT;
                default:
                    return -1;
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getFillObjType(int tag) {
        switch (tag) {
            case ToolbarItemConfig.FILLSIGN_ITEM_TEXT:
            case ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT:
                return FillSign.e_FillSignObjectTypeText;
            case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
                return FillSign.e_FillSignObjectTypeCheckMark;
            case ToolbarItemConfig.FILLSIGN_ITEM_X:
                return FillSign.e_FillSignObjectTypeCrossMark;
            case ToolbarItemConfig.FILLSIGN_ITEM_DOT:
                return FillSign.e_FillSignObjectTypeDot;
            case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
                return FillSign.e_FillSignObjectTypeLine;
            case ToolbarItemConfig.FILLSIGN_ITEM_RECT:
                return FillSign.e_FillSignObjectTypeRoundRectangle;
            default:
                return -1;
        }
    }

    private RectF getEditViewBoxInDv() {
        RectF bbox = new RectF();
        if (mEditView != null) {
            int pageIndex = _editView().getPageIndex();
            PointF dvPt = new PointF(mEditView.getDocLtOffset().x, mEditView.getDocLtOffset().y);
            mPdfViewCtrl.convertPdfPtToPageViewPt(dvPt, dvPt, pageIndex);
            mPdfViewCtrl.convertPageViewPtToDisplayViewPt(dvPt, dvPt, pageIndex);
            bbox.set(dvPt.x, dvPt.y, dvPt.x + mEditView.getWidth(), dvPt.y + mEditView.getHeight());
        }
        return bbox;
    }

    private void focusObject(int pageIndex, FormObject obj) {
        FormObject oldObj = mFocusAnnot;

        if (obj != mFocusAnnot) {
            if (isPopoverShowing()) {
                dismissPopover();
            }
        }

        mFocusAnnot = obj;
        if (obj != null)
            mOldAnnot = obj.clone();
        else
            mOldAnnot = null;

        if (mFocusAnnot != null) {
            showPopover(pageIndex, 0);
        }

        if (mPdfViewCtrl.isPageVisible(pageIndex)) {
            if (oldObj != null) {
                RectF rect = new RectF(oldObj.mBBox);
                mPdfViewCtrl.convertPdfRectToPageViewRect(rect, rect, pageIndex);
                mPdfViewCtrl.convertPageViewRectToDisplayViewRect(rect, rect, pageIndex);

                UIAnnotFrame.getInstance(mContext).extentBoundsToContainControl(rect);
                mPdfViewCtrl.invalidate(AppDmUtil.rectFToRect(rect));
            }
        }

        if (mPdfViewCtrl.isPageVisible(pageIndex)) {
            if (mFocusAnnot != null) {
                RectF rect = new RectF(mFocusAnnot.mBBox);
                mPdfViewCtrl.convertPdfRectToPageViewRect(rect, rect, pageIndex);
                mPdfViewCtrl.convertPageViewRectToDisplayViewRect(rect, rect, pageIndex);
                UIAnnotFrame.getInstance(mContext).extentBoundsToContainControl(rect);
                mPdfViewCtrl.invalidate(AppDmUtil.rectFToRect(rect));
            }
        }
    }

    private void adjustCaretInDocViewer() {
        if (!isEditingText())
            return;

        int pageIndex = _editView().getPageIndex();
        if (!mPdfViewCtrl.isPageVisible(pageIndex)) {
            return;
        }

        if (_editView().getText().toString().length() == 0) {
            return;
        }

        if (getBottomOffset() != 0) {
            correctDvOffsetValue();
        }

        FillSignEditText et = _editView();
        et.splitTextLines();

        ArrayList<String> lines = et.mLineTexts.mLineTexts;
        int caretIndex = et.getSelectionEnd();
        int lineCount = lines.size();
        int lineIndex = 0;
        int charCount = 0;
        for (; lineIndex < lines.size(); lineIndex++) {
            String text = lines.get(lineIndex);
            if (charCount + text.length() >= caretIndex) {
                break;
            }
            charCount += text.length();
        }

        if (lineIndex < lineCount) {
            caretIndex = caretIndex - charCount;
            String str = lines.get(lineIndex);
            str = str.substring(0, caretIndex);

            if (str.length() > 0 && str.charAt(str.length() - 1) == '\n' && lines.size() > lineIndex + 1) {
                lineIndex += 1;
                caretIndex -= str.length();
                str = lines.get(lineIndex);
                str = str.substring(0, caretIndex);
            }

            int scrollY = et.getScrollY();
            float lineHeight = et.getLineHeight();
            float caretY = lineHeight * lineIndex - scrollY;
            float caretX = et.getPaddingLeft();

            if (str.length() > 0) {
                float[] strLens = new float[str.length()];
                et.getPaint().getTextWidths(str, strLens);

                for (int j = 0; j < str.length(); j++) {
                    caretX = caretX + strLens[j];
                }
            }

            RectF caretRt = new RectF(caretX - lineHeight / 2, caretY, caretX + lineHeight / 2, caretY + lineHeight);
            caretRt.offset(et.getLeft(), et.getTop());
            mPdfViewCtrl.convertDisplayViewRectToPageViewRect(caretRt, caretRt, pageIndex);
            mPdfViewCtrl.convertPageViewRectToPdfRect(caretRt, caretRt, pageIndex);
            adjustBoxToDisplayArea(pageIndex, caretRt);

            if (getBottomOffset() != 0) {
                correctDvOffsetValue();
            }
        }
    }

    private boolean isPopoverShowing() {
        return UIPopoverWin.isPopoverShowing();
    }

    private void dismissPopover() {
        UIPopoverWin.dismissPopover();
    }

    private void showPopover(int pageIndex, int action) {
        RectF bbox = new RectF();
        if (isEditingText()) {
            bbox.set(_editView().getLeft(), _editView().getTop(), _editView().getRight(), _editView().getBottom());
        } else if (mFocusAnnot != null) {
            bbox = new RectF(mFocusAnnot.mBBox);
            mPdfViewCtrl.convertPdfRectToPageViewRect(bbox, bbox, pageIndex);
            mPdfViewCtrl.convertPageViewRectToDisplayViewRect(bbox, bbox, pageIndex);
        } else {
            if (action != 2 || !mPdfViewCtrl.isPageVisible(mLongPressPage)) return;

            bbox.set(mLongPressPt.x, mLongPressPt.y, mLongPressPt.x + 2, mLongPressPt.y + 2);
            mPdfViewCtrl.convertPdfRectToPageViewRect(bbox, bbox, pageIndex);
            mPdfViewCtrl.convertPageViewRectToDisplayViewRect(bbox, bbox, pageIndex);
        }

        showPopover(action, bbox);
    }

    private void showPopover(int action, RectF dvBox) {
        mPopoverAction = action;

        Rect dvRect = new Rect();
        mPdfViewCtrl.getGlobalVisibleRect(dvRect);

        Rect rect = new Rect((int) dvBox.left, (int) dvBox.top, (int) dvBox.right, (int) dvBox.bottom);
        rect.offset(dvRect.left, dvRect.top);

        UIPopoverWin.showPopover((FragmentActivity) mUIExtensionsManager.getAttachedActivity(),
                mUIExtensionsManager.getRootView(),
                getPopoverItems(action),
                mClickListener,
                rect,
                getArrowPosition());
    }

    private IBaseItem.OnItemClickListener mClickListener = new IBaseItem.OnItemClickListener() {
        @Override
        public void onClick(IBaseItem item, View v) {
            int curPageIndex = -1;
            if (isEditingText()) {
                curPageIndex = _editView().getPageIndex();
            } else if (mFocusAnnot != null) {
                curPageIndex = mFocusAnnot.mPageIndex;
            } else {
                curPageIndex = mLongPressPage;
            }

            if (!mPdfViewCtrl.isPageVisible(curPageIndex)) {
                return;
            }

            int itemTag = (int) item.getContentView().getTag();
            switch (itemTag) {
                case 1: { // smaller
                    if (isEditingText()) {
                        RectF objRect = new RectF(getEditViewBoxInDv());
                        RectF viewRectF = new RectF();
                        mPdfViewCtrl.convertPdfRectToPageViewRect(objRect, viewRectF, curPageIndex);

                        float objWidth = Math.abs(viewRectF.width());
                        float objHeight = Math.abs(viewRectF.height());
                        float width = objWidth / mProperty.mZoomScale;
                        float height = objHeight / mProperty.mZoomScale;
                        float dx = (objWidth - width) / 2;
                        float dy = (objHeight - height) / 2;
                        viewRectF.inset(dx, dy);

                        RectF docRect = new RectF();
                        mPdfViewCtrl.convertPageViewRectToPdfRect(viewRectF, docRect, curPageIndex);
                        float fontSize = mProperty.mFontSize / mProperty.mZoomScale;
                        if (fontSize < FillSignProperty.MIN_FONTSIZE) return;

                        mProperty.setFontSize(fontSize);
                        _editView().setTextSize(mProperty.getFontSizeDp(mPdfViewCtrl, curPageIndex));
                    } else if (mFocusAnnot != null) {
                        RectF objRect = new RectF(mFocusAnnot.mBBox);
                        RectF viewRectF = new RectF();
                        mPdfViewCtrl.convertPdfRectToPageViewRect(objRect, viewRectF, curPageIndex);

                        float objWidth = Math.abs(viewRectF.width());
                        float objHeight = Math.abs(viewRectF.height());
                        float width = objWidth / mProperty.mZoomScale;
                        float height = objHeight / mProperty.mZoomScale;
                        float dx = (objWidth - width) / 2;
                        float dy = (objHeight - height) / 2;
                        viewRectF.inset(dx, dy);

                        RectF docRect = new RectF();
                        mPdfViewCtrl.convertPageViewRectToPdfRect(viewRectF, docRect, curPageIndex);
                        if (!canZoomOut(mFocusAnnot.mTag, mProperty.mZoomScale, docRect)) return;

                        mFocusAnnot.mBBox.set(docRect);
                        if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_TEXT || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
                            mProperty.setFontSize(mFocusAnnot.mFontSize / mProperty.mZoomScale);
                            mFocusAnnot.mFontSize = mProperty.mFontSize;
                            mFocusAnnot.mCharspace = mProperty.mFontSize * mFocusAnnot.mSpacing;
                        } else {
                            setProperty(curPageIndex, docRect);
                        }
                        modifyCurFormObject(curPageIndex);
                    } else {
                        break;
                    }
                    updatePopover(curPageIndex, mPopoverAction);
                    break;
                }
                case 2: { // larger
                    if (isEditingText()) {
                        RectF rectF = getEditViewBoxInDv();
                        RectF objRect = new RectF(rectF);
                        RectF viewRectF = new RectF();
                        mPdfViewCtrl.convertDisplayViewRectToPageViewRect(objRect, viewRectF, curPageIndex);

                        float objWidth = Math.abs(viewRectF.width());
                        float objHeight = Math.abs(viewRectF.height());

                        float width = objWidth * mProperty.mZoomScale;
                        float height = objHeight * mProperty.mZoomScale;
                        float dx = (objWidth - width) / 2;
                        float dy = (objHeight - height) / 2;
                        viewRectF.inset(dx, dy);

                        boolean canZoom = canZoomIn(mPdfViewCtrl, curPageIndex, viewRectF);
                        if (!canZoom) return;

                        mProperty.setFontSize(mProperty.mFontSize * mProperty.mZoomScale);
                        _editView().setTextSize(mProperty.getFontSizeDp(mPdfViewCtrl, curPageIndex));
                    } else if (mFocusAnnot != null) {
                        RectF objRect = new RectF(mFocusAnnot.mBBox);
                        RectF viewRectF = new RectF();
                        mPdfViewCtrl.convertPdfRectToPageViewRect(objRect, viewRectF, curPageIndex);

                        float objWidth = Math.abs(viewRectF.width());
                        float objHeight = Math.abs(viewRectF.height());

                        float width = objWidth * mProperty.mZoomScale;
                        float height = objHeight * mProperty.mZoomScale;
                        float dx = (objWidth - width) / 2;
                        float dy = (objHeight - height) / 2;
                        viewRectF.inset(dx, dy);

                        boolean canZoom = canZoomIn(mPdfViewCtrl, curPageIndex, viewRectF);
                        if (!canZoom) return;

                        RectF docRect = new RectF();
                        mPdfViewCtrl.convertPageViewRectToPdfRect(viewRectF, docRect, curPageIndex);
                        mFocusAnnot.mBBox.set(docRect);

                        if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_TEXT || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
                            mProperty.setFontSize(mFocusAnnot.mFontSize * mProperty.mZoomScale);
                            mFocusAnnot.mFontSize = mProperty.mFontSize;
                            mFocusAnnot.mCharspace = mProperty.mFontSize * mFocusAnnot.mSpacing;
                        } else {
                            setProperty(curPageIndex, docRect);
                        }
                        modifyCurFormObject(curPageIndex);
                    } else {
                        break;
                    }
                    updatePopover(curPageIndex, mPopoverAction);
                    break;
                }
                case 3: { // more
                    showPopover(curPageIndex, 1);
                    break;
                }
                case 4: // delete
                    if (isEditingText()) {
                        deleteEditView();
                    } else if (mFocusAnnot != null) {
                        deleteCurFormObject(curPageIndex);
                    }
                    break;
                case 5: // check
                case 6: // x
                case 7: // dot
                case 8: // line
                case 9: // rect
                {
                    int formTag = 0;
                    if (itemTag == 5) {
                        formTag = ToolbarItemConfig.FILLSIGN_ITEM_CHECK;
                    } else if (itemTag == 6) {
                        formTag = ToolbarItemConfig.FILLSIGN_ITEM_X;
                    } else if (itemTag == 7) {
                        formTag = ToolbarItemConfig.FILLSIGN_ITEM_DOT;
                    } else if (itemTag == 8) {
                        formTag = ToolbarItemConfig.FILLSIGN_ITEM_LINE;
                    } else if (itemTag == 9) {
                        formTag = ToolbarItemConfig.FILLSIGN_ITEM_RECT;
                    }

                    if (isEditingText()) {
                        deleteEditView();

                        RectF textBox = getEditViewBoxInDv();
                        PointF ltPt = new PointF(textBox.left, textBox.top);
                        mPdfViewCtrl.convertDisplayViewPtToPageViewPt(ltPt, ltPt, curPageIndex);
                        addFormObject(formTag, curPageIndex, ltPt, true);
                    } else if (mFocusAnnot != null) {
                        mFocusAnnot.mTag = formTag;

                        RectF viewRectF = new RectF();
                        mPdfViewCtrl.convertPdfRectToPageViewRect(mFocusAnnot.mBBox, viewRectF, curPageIndex);
                        float centerX = viewRectF.centerX();
                        float centerY = viewRectF.centerY();
                        if (itemTag == 5 || itemTag == 6 || itemTag == 7) {
                            float checkSize = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, curPageIndex, mProperty.mCheckSize);
                            viewRectF.left = centerX - checkSize / 2;
                            viewRectF.right = centerX + checkSize / 2;
                            viewRectF.top = centerY - checkSize / 2;
                            viewRectF.bottom = centerY + checkSize / 2;
                        } else if (itemTag == 8) {
                            float lineSizeX = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, curPageIndex, mProperty.mLineSize.x);
                            float lineSizeY = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, curPageIndex, mProperty.mLineSize.y);

                            viewRectF.left = centerX - lineSizeX / 2;
                            viewRectF.right = centerX + lineSizeX / 2;
                            viewRectF.top = centerY - lineSizeY / 2;
                            viewRectF.bottom = centerY + lineSizeY / 2;
                        } else if (itemTag == 9) {
                            float rectSizeX = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, curPageIndex, mProperty.mRectSize.x);
                            float rectSizeY = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, curPageIndex, mProperty.mRectSize.y);

                            viewRectF.left = centerX - rectSizeX / 2;
                            viewRectF.right = centerX + rectSizeX / 2;
                            viewRectF.top = centerY - rectSizeY / 2;
                            viewRectF.bottom = centerY + rectSizeY / 2;
                        }

                        PointF adjust = UIAnnotFrame.getInstance(mContext).calculateTranslateCorrection(mPdfViewCtrl, curPageIndex, viewRectF);
                        viewRectF.offset(adjust.x, adjust.y);
                        RectF docRect = new RectF();
                        mPdfViewCtrl.convertPageViewRectToPdfRect(viewRectF, docRect, curPageIndex);
                        mFocusAnnot.mBBox.set(docRect);
                        modifyCurFormObject(curPageIndex);
                        updatePopover(curPageIndex, 0);
                    } else {
                        PointF viewPointF = new PointF();
                        mPdfViewCtrl.convertPdfPtToPageViewPt(mLongPressPt, viewPointF, curPageIndex);
                        addFormObject(formTag, curPageIndex, viewPointF, false);
                        mLongPressPage = -1;
                        mLongPressPt = null;
                    }
                    break;
                }
                case 10: // text
                case 11: { // combo text
                    int formTag = 0;
                    if (itemTag == 10) {
                        formTag = ToolbarItemConfig.FILLSIGN_ITEM_TEXT;
                    } else {
                        formTag = ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT;
                    }

                    if (isEditingText()) {
                        if (formTag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
                            _editView().setTextStyle(FillSignEditText.STYLE_COMBO_TEXT);
                            _editView().setLetterSpacing(mProperty.mFontSpacing);
                            _editView().setPadding(0, 0, _editView().getStratchBmp().getWidth(), 0);
                        } else {
                            _editView().setTextStyle(FillSignEditText.STYLE_TEXT);
                            _editView().setLetterSpacing(0);
                            _editView().setPadding(0, 0, 0, 0);
                        }

                        updatePopover(curPageIndex, 0);
                    } else if (mFocusAnnot != null) {
                        _editFocusTextFormObj(curPageIndex, formTag);
                    } else {
                        PointF viewPointF = new PointF();
                        mPdfViewCtrl.convertPdfPtToPageViewPt(mLongPressPt, viewPointF, curPageIndex);
                        addTextBox(formTag, curPageIndex, viewPointF, true, null, null, false);
                        mLongPressPage = -1;
                        mLongPressPt = null;
                    }
                    break;
                }
                case 12: { // more
                    showPopover(curPageIndex, 0);
                    break;
                }
                case 13:
                case 14:
                case 15:
                    break;
                default:
                    break;
            }
        }
    };

    private void _editFocusTextFormObj(int pageIndex, int formTag) {
        if (mFocusAnnot == null) {
            return;
        }
        RectF pvBox = new RectF(mFocusAnnot.mBBox);
        mPdfViewCtrl.convertPdfRectToPageViewRect(pvBox, pvBox, pageIndex);

        float fontSize = 0;
        String text = null;
        int tag = mFocusAnnot.mTag;
        if (tag == ToolbarItemConfig.FILLSIGN_ITEM_TEXT || tag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
            fontSize = mFocusAnnot.mFontSize;
            text = mFocusAnnot.mContent;
        }

        final FormObject oldObj = mOldAnnot;
        deleteFormObject(pageIndex, mFocusAnnot, false, new Event.Callback() {
            @Override
            public void result(Event event, boolean success) {
                mOldAnnot = oldObj;
            }
        });
        addTextBox(formTag, pageIndex, new PointF(pvBox.left, pvBox.top), true, fontSize, text, true);
    }

    private void setEditViewMargin(int l, int t) {
        if (!isEditingText())
            return;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) _editView().getLayoutParams();
        lp.leftMargin = l;
        lp.topMargin = t;
        _editView().setLayoutParams(lp);

        if (_editView().getParent() != null) {
            _editView().getParent().requestLayout();
        }
    }

    private void modifyCurFormObject(int pageIndex) {
        if (mFocusAnnot == null)
            return;

        modifyFormObject(pageIndex, mOldAnnot, mFocusAnnot, true);
    }

    private void modifyFormObject(final int pageIndex, final FormObject oldVal, final FormObject curVal, boolean addUndo) {
        if (oldVal == null || curVal == null)
            return;

        try {
            if (addUndo) {
                final FillSignObject fillSignObject = curVal.mFillObj;
                final FillSignModifyUndoItem undoItem = new FillSignModifyUndoItem(mPdfViewCtrl, this);

                if (fillSignObject.getType() == FillSign.e_FillSignObjectTypeText) {
                    float lineHeight = 0;
                    if (curVal.mFontSize > 0) {
                        float fontSize = mProperty.getFontSizeDp(mPdfViewCtrl, pageIndex, curVal.mFontSize);
                        lineHeight = FillSignUtils.pageViewToDocThickness(mPdfViewCtrl, pageIndex, getLineHeight(fontSize));
                    }

                    undoItem.mFontSize = curVal.mFontSize;
                    undoItem.mTexts = FillSignUtils.jniToJavaTextLines(curVal.mContent);
                    undoItem.mContent = curVal.mContent;
                    undoItem.mCharspace = curVal.mCharspace;
                    undoItem.mLineHeight = lineHeight;
                    undoItem.mIsCombText = (curVal.mTag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT);

                    undoItem.mRedoFontSize = curVal.mFontSize;
                    undoItem.mRedoContent = curVal.mContent;
                    undoItem.mRedoTexts = FillSignUtils.jniToJavaTextLines(curVal.mContent);
                    undoItem.mRedoCharspace = curVal.mCharspace;

                    undoItem.mUndoFontSize = oldVal.mFontSize;
                    undoItem.mUndoContent = oldVal.mContent;
                    undoItem.mUndoTexts = FillSignUtils.jniToJavaTextLines(oldVal.mContent);
                    undoItem.mUndoCharspace = oldVal.mCharspace;
                }

                undoItem.mPageIndex = pageIndex;
                undoItem.mRectF = new RectF(curVal.mBBox);
                undoItem.mType = getFillObjType(curVal.mTag);
                PDFPage page = mUIExtensionsManager.getDocumentManager().getPage(pageIndex, false);
                undoItem.mRotation = (page.getRotation() + mPdfViewCtrl.getViewRotation()) % 4;

                undoItem.mRedoRectF = new RectF(curVal.mBBox);
                undoItem.mRedoType = getFillObjType(curVal.mTag);
                undoItem.mUndoRectF = new RectF(oldVal.mBBox);
                undoItem.mUndoType = getFillObjType(oldVal.mTag);

                final RectF lastRectF = AppUtil.toRectF(fillSignObject.getRect());
                FillSignEvent event = new FillSignEvent(EditAnnotEvent.EVENTTYPE_MODIFY, fillSignObject, undoItem, mPdfViewCtrl);
                EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
                    @Override
                    public void result(Event event, boolean success) {
                        if (success) {
                            try {
                                FillSignObject newFillObj = ((FillSignEvent) event).mFillSignObj;
                                RectF newRectF = AppUtil.toRectF(newFillObj.getRect());
//                                updateUndoItem(curVal.mFillObj, newFillObj);
                                if (mFocusAnnot != null) {
                                    if (mFocusAnnot.mFillObj == curVal.mFillObj) {
                                        mFocusAnnot.mFillObj = newFillObj;
                                        mFocusAnnot.mBBox = new RectF(newRectF);
                                    }
                                    mOldAnnot = mFocusAnnot.clone();
                                } else {
                                    mOldAnnot = null;
                                }

                                undoItem.setOldValue(oldVal.clone());
                                undoItem.setCurrentValue(curVal.clone());
//                                mUndoItemList.add(undoItem);
//                                mUIExtensionsManager.getDocumentManager().addUndoItem(undoItem);

                                if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                                    RectF objRectF = new RectF(newRectF);
                                    mPdfViewCtrl.convertPdfRectToPageViewRect(objRectF, objRectF, pageIndex);
                                    RectF oldRectF = new RectF(lastRectF);
                                    mPdfViewCtrl.convertPdfRectToPageViewRect(oldRectF, oldRectF, pageIndex);
                                    objRectF.union(oldRectF);
                                    float defHeight = FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, mProperty.mLineSize.y);
                                    objRectF.inset(-defHeight, -defHeight);
                                    mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(objRectF));
                                }
                            } catch (PDFException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mPdfViewCtrl.addTask(task);
            } else {
                RectF rectF = new RectF(curVal.mBBox);
                PointF pointF = new PointF(rectF.left, rectF.top);
                FillSignObject fillSignObject = curVal.mFillObj;
                PDFPage page = mUIExtensionsManager.getDocumentManager().getPage(pageIndex, false);
                int rotation = (page.getRotation() + mPdfViewCtrl.getViewRotation()) % 4;
                fillSignObject.move(AppUtil.toFxPointF(pointF), rectF.width(), rectF.height(), rotation);
                fillSignObject.generateContent();

//                updateUndoItem(curVal.mFillObj, fillSignObject);
                if (mFocusAnnot != null && mFocusAnnot.mFillObj == curVal.mFillObj) {
                    mFocusAnnot.mFillObj = fillSignObject;
                    if (mOldAnnot != null)
                        mOldAnnot.mFillObj = fillSignObject;
                }
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    private void deleteCurFormObject(int pageIndex) {
        if (mFocusAnnot == null) return;
        deleteFormObject(pageIndex, mFocusAnnot, true, null);
    }

    private void deleteFormObject(final int pageIndex, final FormObject obj, boolean addUndo, final Event.Callback callback) {
        if (obj == null) return;

        boolean isCurObj = false;
        if (mFocusAnnot != null && mFocusAnnot.mFillObj.equal(obj.mFillObj))
            isCurObj = true;

        try {
            if (addUndo) {
                final FillSignDeleteUndoItem undoItem = new FillSignDeleteUndoItem(mPdfViewCtrl, this);
                undoItem.mPageIndex = obj.mPageIndex;
                undoItem.mType = getFillObjType(obj.mTag);
                undoItem.mRectF = new RectF(obj.mBBox);
                undoItem.mFontSize = obj.mFontSize;
                undoItem.mCharspace = obj.mCharspace;
                undoItem.mContent = obj.mContent;
                undoItem.mTexts = FillSignUtils.jniToJavaTextLines(obj.mContent);
                PDFPage page = mUIExtensionsManager.getDocumentManager().getPage(pageIndex, false);
                undoItem.mRotation = (page.getRotation() + mPdfViewCtrl.getViewRotation()) % 4;

                FillSignEvent event = new FillSignEvent(EditAnnotEvent.EVENTTYPE_DELETE, obj.mFillObj, undoItem, mPdfViewCtrl);
                final boolean finalIsCurObj = isCurObj;
                EditAnnotTask task = new EditAnnotTask(event, new Event.Callback() {
                    @Override
                    public void result(Event event, boolean success) {
                        if (success) {
                            undoItem.setOldValue(obj.clone());
//                            mUndoItemList.add(undoItem);
//                            mUIExtensionsManager.getDocumentManager().addUndoItem(undoItem);

                            if (finalIsCurObj)
                                focusObject(pageIndex, null);

                            if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                                RectF viewRect = new RectF(obj.mBBox);
                                mPdfViewCtrl.convertPdfRectToPageViewRect(viewRect, viewRect, pageIndex);
                                mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(viewRect));
                            }
                        }

                        if (callback != null)
                            callback.result(event, success);

                    }
                });
                mPdfViewCtrl.addTask(task);
            } else {
                if (obj.mFillObj != null) {
                    FillSign fillSign = getFillSign(pageIndex);
                    if (fillSign == null) return;
                    fillSign.removeObject(obj.mFillObj);

                    if (mPdfViewCtrl.isPageVisible(pageIndex)) {
                        RectF viewRect = new RectF(obj.mBBox);
                        mPdfViewCtrl.convertPdfRectToPageViewRect(viewRect, viewRect, pageIndex);
                        mPdfViewCtrl.refresh(pageIndex, AppDmUtil.rectFToRect(viewRect));
                    }
                }

                if (isCurObj)
                    focusObject(pageIndex, null);

                if (callback != null)
                    callback.result(null, true);
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    private int getArrowPosition() {
        return UIPopover.ARROW_AUTO;
    }

    private ArrayList<UIPopoverWin.POPOVER_ITEM> getPopoverItems(int action) {
        ArrayList<UIPopoverWin.POPOVER_ITEM> itemList = new ArrayList<>();
        switch (action) {
            case 0:
                itemList.add(new UIPopoverWin.POPOVER_ITEM(1, R.drawable.fillsign_white_smaller));
                itemList.add(new UIPopoverWin.POPOVER_ITEM(2, R.drawable.fillsign_white_larger));
                itemList.add(new UIPopoverWin.POPOVER_ITEM(3, R.drawable.fillsign_white_more));
                itemList.add(new UIPopoverWin.POPOVER_ITEM(4, R.drawable.fillsign_white_delete));
                break;
            case 1:
                int tag = _curItemOrToolTag();
                if (tag != ToolbarItemConfig.FILLSIGN_ITEM_CHECK) {
                    itemList.add(new UIPopoverWin.POPOVER_ITEM(5, R.drawable.fillsign_white_check));
                }
                if (tag != ToolbarItemConfig.FILLSIGN_ITEM_X) {
                    itemList.add(new UIPopoverWin.POPOVER_ITEM(6, R.drawable.fillsign_white_x));
                }
                if (tag != ToolbarItemConfig.FILLSIGN_ITEM_DOT) {
                    itemList.add(new UIPopoverWin.POPOVER_ITEM(7, R.drawable.fillsign_white_dot));
                }
                if (tag != ToolbarItemConfig.FILLSIGN_ITEM_LINE) {
                    itemList.add(new UIPopoverWin.POPOVER_ITEM(8, R.drawable.fillsign_white_line));
                }
                if (tag != ToolbarItemConfig.FILLSIGN_ITEM_RECT) {
                    itemList.add(new UIPopoverWin.POPOVER_ITEM(9, R.drawable.fillsign_white_rect));
                }
                if (tag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
                    itemList.add(new UIPopoverWin.POPOVER_ITEM(10, R.drawable.fillsign_white_text));
                }
                if (tag == ToolbarItemConfig.FILLSIGN_ITEM_TEXT && AppBuildConfig.SDK_VERSION >= 21) {
                    itemList.add(new UIPopoverWin.POPOVER_ITEM(11, R.drawable.fillsign_white_combo_text));
                }
                itemList.add(new UIPopoverWin.POPOVER_ITEM(12, R.drawable.fillsign_white_more));
                break;
            case 2:
                itemList.add(new UIPopoverWin.POPOVER_ITEM(5, R.drawable.fillsign_white_check));
                itemList.add(new UIPopoverWin.POPOVER_ITEM(6, R.drawable.fillsign_white_x));
                itemList.add(new UIPopoverWin.POPOVER_ITEM(7, R.drawable.fillsign_white_dot));
                itemList.add(new UIPopoverWin.POPOVER_ITEM(8, R.drawable.fillsign_white_line));
                itemList.add(new UIPopoverWin.POPOVER_ITEM(9, R.drawable.fillsign_white_rect));
                itemList.add(new UIPopoverWin.POPOVER_ITEM(10, R.drawable.fillsign_white_text));
                if (AppBuildConfig.SDK_VERSION >= 21) {
                    itemList.add(new UIPopoverWin.POPOVER_ITEM(11, R.drawable.fillsign_white_combo_text));
                }
                break;
        }
        return itemList;
    }

    private int _curToolTag() {
        int tag = 0;
        if (mFillSignModule.getCurrentItem() != null) {
            tag = (int) mFillSignModule.getCurrentItem().getContentView().getTag();
        }
        return tag;
    }

    private int _curItemOrToolTag() {
        if (_curItemTag() != 0)
            return _curItemTag();
        if (_curToolTag() != 0)
            return _curToolTag();
        return 0;
    }

    private int _curItemTag() {
        int tag = 0;
        if (isEditingText()) {
            if (_editView().getTextStyle() == FillSignEditText.STYLE_TEXT) {
                return ToolbarItemConfig.FILLSIGN_ITEM_TEXT;
            } else {
                return ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT;
            }
        }

        if (mFocusAnnot != null) {
            tag = mFocusAnnot.mTag;
        }
        return tag;
    }

    private void correctDvOffsetValue() {
        int offset = getBottomOffset();
        int navBarHeight = mKeyboardBar.getBarHeight();
        int maskHeight = AppKeyboardUtil.getKeyboardHeight(mUIExtensionsManager.getRootView()) + navBarHeight;
        int offset2 = Math.max(0, Math.min(maskHeight, offset));

        if (!mPdfViewCtrl.isContinuous()) {
            int viewerHeight = mPdfViewCtrl.getDisplayViewHeight();
            int pageViewHeight = mPdfViewCtrl.getPageViewHeight(mPdfViewCtrl.getCurrentPage());
            if (pageViewHeight < viewerHeight) {
                if (offset2 + (viewerHeight - pageViewHeight) / 2 > maskHeight) {
                    offset2 = offset2 - (offset2 + (viewerHeight - pageViewHeight) / 2 - maskHeight);
                }
            }
        }

        offset2 = Math.max(0, offset2);
        if (offset2 != offset) {
            setBottomOffset(offset2);
        }
    }

    private void adjustNavBarPadding() {
        Rect padding = mKeyboardBar.getPadding();
        int kbHeight = AppKeyboardUtil.getKeyboardHeight(mUIExtensionsManager.getRootView());
        if (kbHeight != padding.bottom) {
            padding.bottom = kbHeight;
            mKeyboardBar.setPadding(0, 0, 0, Math.max(0, padding.bottom));
        }
    }

    private void adjustBoxToDisplayArea(int pageIndex, RectF docBox) {
        int margin = 100;
        int navBarHeight = mKeyboardBar.getBarHeight();
        int maskHeight = AppKeyboardUtil.getKeyboardHeight(mUIExtensionsManager.getRootView()) + navBarHeight;
        int viewerWidth = mPdfViewCtrl.getDisplayViewWidth();
        int viewerHeight = mPdfViewCtrl.getDisplayViewHeight();

        int offset = getBottomOffset();
        RectF viewArea = new RectF(0, 0, viewerWidth, viewerHeight);
        viewArea.bottom -= maskHeight;
        viewArea.offset(0, offset);

        RectF boxInPv = new RectF(docBox);
        mPdfViewCtrl.convertPdfRectToPageViewRect(boxInPv, boxInPv, pageIndex);

        RectF boxInDv = new RectF(boxInPv);
        mPdfViewCtrl.convertPageViewRectToDisplayViewRect(boxInDv, boxInDv, pageIndex);

        if (viewArea.contains(boxInDv)) {
            return;
        }

        PointF jumpPt = new PointF();
        // left and right
        if (boxInDv.width() > viewArea.width() - margin) {
            if (boxInDv.left < 0) {
                jumpPt.x = boxInDv.left - margin;
            } else if (boxInDv.left > margin) {
                jumpPt.x = boxInDv.left - margin;
            }
        } else if (boxInDv.left < 0) {
            jumpPt.x = boxInDv.left - margin;
        } else if (boxInDv.right > viewArea.right) {
            jumpPt.x = boxInDv.right - viewArea.right + margin;
        }

        // top and bottom
        if (boxInDv.height() < viewArea.height() - margin) {
            if (boxInDv.bottom > viewArea.bottom) {
                jumpPt.y = boxInDv.bottom - viewArea.bottom + margin;
            } else if (boxInDv.top < viewArea.top) {
                jumpPt.y = boxInDv.top - viewArea.top - margin;
            }
            mPdfViewCtrl.convertDisplayViewPtToPageViewPt(jumpPt, jumpPt, pageIndex);
            mPdfViewCtrl.gotoPage(pageIndex, jumpPt.x, jumpPt.y);

            // recalculate box after jump to page
            boxInPv = new RectF(docBox);
            mPdfViewCtrl.convertPdfRectToPageViewRect(boxInPv, boxInPv, pageIndex);

            boxInDv = new RectF(boxInPv);
            mPdfViewCtrl.convertPageViewRectToDisplayViewRect(boxInDv, boxInDv, pageIndex);

            if (boxInDv.bottom > viewArea.bottom) {
                offset += boxInDv.bottom - viewArea.bottom;
                setBottomOffset(offset, maskHeight);
            } else if (boxInDv.top < viewArea.top) {
                offset -= viewArea.top - boxInDv.top;
                setBottomOffset(offset, maskHeight);
            }
        } else {
            jumpPt.y = boxInDv.top - viewArea.top - margin;

            mPdfViewCtrl.convertDisplayViewPtToPageViewPt(jumpPt, jumpPt, pageIndex);
            mPdfViewCtrl.gotoPage(pageIndex, jumpPt.x, jumpPt.y);

            boxInPv = new RectF(docBox);
            mPdfViewCtrl.convertPdfRectToPageViewRect(boxInPv, boxInPv, pageIndex);

            boxInDv = new RectF(boxInPv);
            mPdfViewCtrl.convertPageViewRectToDisplayViewRect(boxInDv, boxInDv, pageIndex);

            if (boxInDv.top > viewArea.top + margin) {
                offset += boxInDv.top - viewArea.top - margin;
                setBottomOffset(offset, maskHeight);
            } else if (boxInDv.top < viewArea.top + margin) {
                offset -= viewArea.top + margin - boxInDv.top;
                setBottomOffset(offset, maskHeight);
            }
        }
    }

    private void setBottomOffset(int offset, int maskHeight) {
        offset = Math.max(0, Math.min(maskHeight, offset));
        setBottomOffset(offset);
    }

    private int mBottomOffset;

    private int getBottomOffset() {
        return -mBottomOffset;
    }

    private void setBottomOffset(int offset) {
        if (mBottomOffset == -offset)
            return;
        mBottomOffset = -offset;
        mPdfViewCtrl.layout(0, mBottomOffset, mPdfViewCtrl.getWidth(), mPdfViewCtrl.getHeight() + mBottomOffset);
    }

    private void updateUndoItem(FillSignObject oldObj, FillSignObject newObj) {
        for (int i = 0; i < mUndoItemList.size(); i++) {
            mUndoItemList.get(i).updateFillObj(oldObj, newObj);
        }
    }

    FillSign getFillSign(int pageIndex) {
        if (pageIndex < 0) return null;

        try {
            FillSign fillSign = mFillSignArrs.get(pageIndex);
            if (fillSign == null) {
                PDFPage page = mUIExtensionsManager.getDocumentManager().getPage(pageIndex, false);
                fillSign = new FillSign(page);
                mFillSignArrs.put(pageIndex, fillSign);
            }
            return fillSign;
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return null;
    }

    void onPagesRemoved(boolean success, int index) {
        if (success) {
            ArrayList<Integer> invalidList = new ArrayList<>();
            SparseArray<FillSign> tempArrs = new SparseArray<>();

            int size = mFillSignArrs.size();
            for (int i = 0; i < size; i++) {
                int pageIndex = mFillSignArrs.keyAt(i);
                FillSign fillSign = mFillSignArrs.valueAt(i);
                if (pageIndex == index) {
                    invalidList.add(index);
                } else if (pageIndex > index) {
                    pageIndex -= 1;
                }
                tempArrs.put(pageIndex, fillSign);
            }

            for (Integer pageIndex : invalidList) {
                tempArrs.remove(pageIndex);
            }
            mFillSignArrs = tempArrs.clone();
            invalidList.clear();
            tempArrs.clear();
        }
    }

    void onPageMoved(boolean success, int index, int dstIndex) {
        if (success) {
            SparseArray<FillSign> tempArrs = new SparseArray<>();
            int size = mFillSignArrs.size();
            for (int i = 0; i < size; i++) {
                int pageIndex = mFillSignArrs.keyAt(i);
                FillSign fillSign = mFillSignArrs.valueAt(i);

                if (index < dstIndex) {
                    if (pageIndex <= dstIndex && pageIndex > index) {
                        pageIndex -= 1;
                    } else if (pageIndex == index) {
                        pageIndex = dstIndex;
                    }
                } else {
                    if (pageIndex >= dstIndex && pageIndex < index) {
                        pageIndex += 1;
                    } else if (pageIndex == index) {
                        pageIndex = dstIndex;
                    }
                }
                tempArrs.put(pageIndex, fillSign);
            }

            mFillSignArrs = tempArrs.clone();
            tempArrs.clear();
        }
    }

    void onPagesInserted(boolean success, int dstIndex, int[] pageRanges) {
        if (success) {
            int offsetIndex = 0;
            for (int i = 0; i < pageRanges.length / 2; i++) {
                offsetIndex += pageRanges[2 * i + 1];
            }
            SparseArray<FillSign> tempArrs = new SparseArray<>();
            int size = mFillSignArrs.size();
            for (int i = 0; i < size; i++) {
                int pageIndex = mFillSignArrs.keyAt(i);
                FillSign fillSign = mFillSignArrs.valueAt(i);

                if (pageIndex >= dstIndex) {
                    pageIndex += offsetIndex;
                }
                tempArrs.put(pageIndex, fillSign);
            }
            mFillSignArrs = tempArrs.clone();
            tempArrs.clear();
        }
    }

    void release() {
        if (mFocusAnnot != null)
            focusObject(mFocusAnnot.mPageIndex, null);

        int size = mFillSignArrs.size();
        for (int i = 0; i < size; i++) {
            FillSign fillSign = mFillSignArrs.valueAt(i);
            if (!fillSign.isEmpty())
                fillSign.delete();
        }
        mFillSignArrs.clear();
        mUndoItemList.clear();
    }

    private void setProperty(int pageIndex, RectF docRectF) {
        RectF viewRect = new RectF();
        mPdfViewCtrl.convertPdfRectToPageViewRect(docRectF, viewRect, pageIndex);
        float actualWidth = FillSignUtils.pageViewToDocThickness(mPdfViewCtrl, pageIndex, Math.abs(viewRect.width()));
        float actualHeight = FillSignUtils.pageViewToDocThickness(mPdfViewCtrl, pageIndex, Math.abs(viewRect.height()));
        if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_CHECK
                || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_X
                || mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_DOT) {
            mProperty.setCheckSize(actualWidth);
        } else if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_LINE) {
            mProperty.setLineSize(new PointF(actualWidth, mProperty.mLineSize.y));
        } else if (mFocusAnnot.mTag == ToolbarItemConfig.FILLSIGN_ITEM_RECT) {
            mProperty.setRectSize(new PointF(actualWidth, actualHeight));
        }
    }

    private boolean canZoomOut(int tag, float scale, RectF docRect) {
        float width = Math.abs(docRect.width());
        float height = Math.abs(docRect.width());

        switch (tag) {
            case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
            case ToolbarItemConfig.FILLSIGN_ITEM_X:
            case ToolbarItemConfig.FILLSIGN_ITEM_DOT:
                return width >= FillSignProperty.MIN_CHECKSIZE;
            case ToolbarItemConfig.FILLSIGN_ITEM_TEXT:
            case ToolbarItemConfig.FILLSIGN_ITEM_PROFILE:
            case ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT:
                return mFocusAnnot.mFontSize / scale >= FillSignProperty.MIN_FONTSIZE;
            case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
                return width >= FillSignProperty.MIN_LINESIZE;
            case ToolbarItemConfig.FILLSIGN_ITEM_RECT:
                return width >= FillSignProperty.MIN_RECT_X && height >= FillSignProperty.MIN_RECT_Y;
            default:
                break;
        }
        return true;
    }

    private boolean canZoomIn(PDFViewCtrl pdfViewCtrl, int pageIndex, RectF rectF) {
        float extent = UIAnnotFrame.getInstance(mContext).getControlExtent();
        if (rectF.left < extent
                || rectF.right > pdfViewCtrl.getPageViewWidth(pageIndex) - extent
                || rectF.top < extent
                || rectF.bottom > pdfViewCtrl.getPageViewHeight(pageIndex) - extent) {
            return false;
        }
        return true;
    }

    void onScaleEnd() {
        int itemTag = _curItemOrToolTag();
        if (itemTag == ToolbarItemConfig.FILLSIGN_ITEM_TEXT
                || itemTag == ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT) {
            if (isEditingText()) {
                PointF offsetPointF = _editView().getDocLtOffset();
                float textSizePx = _editView().getTextSize();
                int pageIndex = _editView().getPageIndex();

                PointF pvPt = new PointF();
                mPdfViewCtrl.convertPdfPtToPageViewPt(offsetPointF, pvPt, pageIndex);
                RectF textBox = new RectF(pvPt.x, pvPt.y, (pvPt.x + textSizePx / 2), (pvPt.y + textSizePx));
                if (!mAddLTPointF) {
                    textBox.offset(-textBox.width() / 2, -textBox.height() / 2);
                }
                int mw = (int) (mPdfViewCtrl.getPageViewWidth(pageIndex) - pvPt.x);
                int mh = (int) (mPdfViewCtrl.getPageViewHeight(pageIndex) - pvPt.y);

                _editView().setMinWidth((int) textBox.width());
                _editView().setMinHeight((int) textBox.height());
                _editView().setMaxWidth(mw);
                _editView().setMaxHeight(mh);
                _editView().setMarginRight(FillSignUtils.docToPageViewThickness(mPdfViewCtrl, pageIndex, FillSignEditText.SPACING));
            }
        }
    }

}

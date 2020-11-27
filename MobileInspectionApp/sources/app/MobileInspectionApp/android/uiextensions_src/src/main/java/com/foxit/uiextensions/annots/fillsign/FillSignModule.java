package com.foxit.uiextensions.annots.fillsign;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.sdk.pdf.annots.Annot;
import com.foxit.uiextensions.Module;
import com.foxit.uiextensions.R;
import com.foxit.uiextensions.ToolHandler;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.AnnotEventListener;
import com.foxit.uiextensions.annots.common.UIBtnImageView;
import com.foxit.uiextensions.annots.common.UIPopover;
import com.foxit.uiextensions.controls.dialog.MatchDialog;
import com.foxit.uiextensions.controls.dialog.UIMatchDialog;
import com.foxit.uiextensions.controls.toolbar.BaseBar;
import com.foxit.uiextensions.controls.toolbar.IBaseItem;
import com.foxit.uiextensions.controls.toolbar.ToolbarItemConfig;
import com.foxit.uiextensions.controls.toolbar.impl.AnnotItemImpl;
import com.foxit.uiextensions.controls.toolbar.impl.BaseBarImpl;
import com.foxit.uiextensions.controls.toolbar.impl.BaseItemImpl;
import com.foxit.uiextensions.pdfreader.IStateChangeListener;
import com.foxit.uiextensions.pdfreader.config.AppBuildConfig;
import com.foxit.uiextensions.pdfreader.config.ReadStateConfig;
import com.foxit.uiextensions.utils.AppAnnotUtil;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppKeyboardUtil;
import com.foxit.uiextensions.utils.AppResource;
import com.foxit.uiextensions.utils.AppUtil;
import com.foxit.uiextensions.utils.OnPageEventListener;
import com.foxit.uiextensions.utils.UIToast;
import com.foxit.uiextensions.utils.thread.AppThreadManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

public class FillSignModule implements Module {
    private int mBlackPopoverHeightDp = 36;

    private ArrayList<Integer> mBtnTags = new ArrayList<>();
    private ArrayList<IBaseItem> mBtnItems = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> mBtnMoreTags = new ArrayList<>();
    private ArrayList<ArrayList<IBaseItem>> mBtnMoreItems = new ArrayList<>();
    private ArrayList<BaseBarImpl> mBtnMoreBars = new ArrayList<>();

    private ArrayList<Integer> mDropDownTagSet = new ArrayList<>();
    private SparseIntArray mBtnDrawableMap = new SparseIntArray();
    private SparseIntArray mWhite2BlackTagMap = new SparseIntArray();

    private FillSignToolHandler mToolHandler;
    private Context mContext;
    private PDFViewCtrl mPDFViewCtrl;
    private UIExtensionsManager mUiExtensionsManager;
    FillSignProfileInfo mProfileInfo;

    private boolean mDocWillClose = false;
    private IBaseItem mFillSignItem;
    private IBaseItem mCurItem;
    String mProfileStr;

    public FillSignModule(Context context, PDFViewCtrl pdfViewCtrl, PDFViewCtrl.UIExtensionsManager uiExtensionsManager) {
        this.mContext = context;
        this.mPDFViewCtrl = pdfViewCtrl;
        this.mUiExtensionsManager = (UIExtensionsManager) uiExtensionsManager;
    }

    @Override
    public String getName() {
        return Module.MODULE_NAME_FIllSIGN;
    }

    @Override
    public boolean loadModule() {
        mProfileInfo = new FillSignProfileInfo(mContext);
        mToolHandler = new FillSignToolHandler(mContext, this, mPDFViewCtrl);

        initButtonList();
        initButtonMoreBar();
        initThemeViews();

        mPDFViewCtrl.registerDrawEventListener(mDrawEventListener);
        mPDFViewCtrl.registerDocEventListener(mDocEventListener);
        mPDFViewCtrl.registerPageEventListener(mPageEventListener);
        mPDFViewCtrl.registerScaleGestureEventListener(mScaleGestureEventListener);
        mUiExtensionsManager.registerToolHandler(mToolHandler);
        mUiExtensionsManager.registerModule(this);
        mUiExtensionsManager.registerToolHandlerChangedListener(mToolHandlerChangedListener);
        mUiExtensionsManager.registerStateChangeListener(mStateChangeListener);
        mUiExtensionsManager.getDocumentManager().registerAnnotEventListener(mAnnotEventListener);
        return true;
    }

    @Override
    public boolean unloadModule() {
        mPDFViewCtrl.unregisterDrawEventListener(mDrawEventListener);
        mPDFViewCtrl.unregisterDocEventListener(mDocEventListener);
        mPDFViewCtrl.unregisterPageEventListener(mPageEventListener);
        mPDFViewCtrl.unregisterScaleGestureEventListener(mScaleGestureEventListener);
        mUiExtensionsManager.unregisterToolHandler(mToolHandler);
        mUiExtensionsManager.unregisterToolHandlerChangedListener(mToolHandlerChangedListener);
        mUiExtensionsManager.unregisterStateChangeListener(mStateChangeListener);
        mUiExtensionsManager.getDocumentManager().unregisterAnnotEventListener(mAnnotEventListener);
        return true;
    }

    private void initButtonList() {
        Integer[] btnTags;
        Integer[] dropDownTagSet;

        if (AppDisplay.getInstance(mContext).isPad()) {
            if (AppBuildConfig.SDK_VERSION >= 21) {
                btnTags = new Integer[]{
                        ToolbarItemConfig.FILLSIGN_ITEM_TEXT,
                        ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT,
                        ToolbarItemConfig.FILLSIGN_ITEM_PROFILE,
                        ToolbarItemConfig.FILLSIGN_ITEM_CHECK,
                        ToolbarItemConfig.FILLSIGN_ITEM_X,
                        ToolbarItemConfig.FILLSIGN_ITEM_DOT,
                        ToolbarItemConfig.FILLSIGN_ITEM_LINE,
                        ToolbarItemConfig.FILLSIGN_ITEM_RECT,
                };
            } else {
                btnTags = new Integer[]{
                        ToolbarItemConfig.FILLSIGN_ITEM_TEXT,
                        ToolbarItemConfig.FILLSIGN_ITEM_PROFILE,
                        ToolbarItemConfig.FILLSIGN_ITEM_CHECK,
                        ToolbarItemConfig.FILLSIGN_ITEM_X,
                        ToolbarItemConfig.FILLSIGN_ITEM_DOT,
                        ToolbarItemConfig.FILLSIGN_ITEM_LINE,
                        ToolbarItemConfig.FILLSIGN_ITEM_RECT,
                };
            }
            dropDownTagSet = new Integer[]{
            };

            for (int i = 0; i < btnTags.length; i++) {
                ArrayList<Integer> moreTags = new ArrayList<>();
                mBtnMoreTags.add(moreTags);
            }
        } else {
            btnTags = new Integer[]{
                    ToolbarItemConfig.FILLSIGN_ITEM_TEXT,
                    ToolbarItemConfig.FILLSIGN_ITEM_PROFILE,
                    ToolbarItemConfig.FILLSIGN_ITEM_CHECK,
                    ToolbarItemConfig.FILLSIGN_ITEM_LINE,
            };

            if (AppBuildConfig.SDK_VERSION >= 21) {
                dropDownTagSet = new Integer[]{
                        ToolbarItemConfig.FILLSIGN_ITEM_TEXT,
                        ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT,
                        //
                        ToolbarItemConfig.FILLSIGN_ITEM_CHECK,
                        ToolbarItemConfig.FILLSIGN_ITEM_X,
                        ToolbarItemConfig.FILLSIGN_ITEM_DOT,
                        //
                        ToolbarItemConfig.FILLSIGN_ITEM_LINE,
                        ToolbarItemConfig.FILLSIGN_ITEM_RECT,
                };
            } else {
                dropDownTagSet = new Integer[]{
                        ToolbarItemConfig.FILLSIGN_ITEM_CHECK,
                        ToolbarItemConfig.FILLSIGN_ITEM_X,
                        ToolbarItemConfig.FILLSIGN_ITEM_DOT,
                        //
                        ToolbarItemConfig.FILLSIGN_ITEM_LINE,
                        ToolbarItemConfig.FILLSIGN_ITEM_RECT,
                };
            }

            for (int i = 0; i < btnTags.length; i++) {
                int tag = btnTags[i];
                ArrayList<Integer> moreTags = new ArrayList<>();

                switch (tag) {
                    case ToolbarItemConfig.FILLSIGN_ITEM_TEXT:
                    case ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT: {
                        if (AppBuildConfig.SDK_VERSION >= 21) {
                            moreTags.add(ToolbarItemConfig.FILLSIGN_ITEM_MORE_TEXT);
                            moreTags.add(ToolbarItemConfig.FILLSIGN_ITEM_MORE_COMBO_TEXT);
                        } else {
                            moreTags = (null);
                        }
                        break;
                    }
                    case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
                    case ToolbarItemConfig.FILLSIGN_ITEM_X:
                    case ToolbarItemConfig.FILLSIGN_ITEM_DOT: {
                        moreTags.add(ToolbarItemConfig.FILLSIGN_ITEM_MORE_CHECK);
                        moreTags.add(ToolbarItemConfig.FILLSIGN_ITEM_MORE_X);
                        moreTags.add(ToolbarItemConfig.FILLSIGN_ITEM_MORE_DOT);
                        break;
                    }
                    case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
                    case ToolbarItemConfig.FILLSIGN_ITEM_RECT: {
                        moreTags.add(ToolbarItemConfig.FILLSIGN_ITEM_MORE_LINE);
                        moreTags.add(ToolbarItemConfig.FILLSIGN_ITEM_MORE_RECT);
                        break;
                    }
                    default:
                        moreTags = (null);
                        break;
                }
                mBtnMoreTags.add(moreTags);
            }
        }

        // button tag and drawable maps
        {
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_TEXT, R.drawable.fillsign_text);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT, R.drawable.fillsign_combo_text);
            //
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_CHECK, R.drawable.fillsign_check);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_X, R.drawable.fillsign_x);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_DOT, R.drawable.fillsign_dot);
            //
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_LINE, R.drawable.fillsign_line);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_RECT, R.drawable.fillsign_rect);
            //
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_PROFILE, R.drawable.fillsign_profile);
            //
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_TEXT, R.drawable.fillsign_white_text);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_COMBO_TEXT, R.drawable.fillsign_white_combo_text);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_CHECK, R.drawable.fillsign_white_check);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_X, R.drawable.fillsign_white_x);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_DOT, R.drawable.fillsign_white_dot);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_LINE, R.drawable.fillsign_white_line);
            mBtnDrawableMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_RECT, R.drawable.fillsign_white_rect);
        }

        // white to black tag map
        {
            mWhite2BlackTagMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_TEXT, ToolbarItemConfig.FILLSIGN_ITEM_TEXT);
            mWhite2BlackTagMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_COMBO_TEXT, ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT);
            mWhite2BlackTagMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_CHECK, ToolbarItemConfig.FILLSIGN_ITEM_CHECK);
            mWhite2BlackTagMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_X, ToolbarItemConfig.FILLSIGN_ITEM_X);
            mWhite2BlackTagMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_DOT, ToolbarItemConfig.FILLSIGN_ITEM_DOT);
            mWhite2BlackTagMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_LINE, ToolbarItemConfig.FILLSIGN_ITEM_LINE);
            mWhite2BlackTagMap.put(ToolbarItemConfig.FILLSIGN_ITEM_MORE_RECT, ToolbarItemConfig.FILLSIGN_ITEM_RECT);
        }

        Collections.addAll(mBtnTags, btnTags);
        Collections.addAll(mDropDownTagSet, dropDownTagSet);
    }

    private void initButtonMoreBar() {
        for (int i = 0; i < mBtnTags.size(); i++) {
            ArrayList<Integer> moreTags = mBtnMoreTags.get(i);

            if (moreTags == null || moreTags.size() == 0) {
                mBtnMoreItems.add(null);
                mBtnMoreBars.add(null);
                continue;
            }

            ArrayList<IBaseItem> moreItems = new ArrayList<>();
            BaseBarImpl baseBar = new BaseBarImpl(mContext);
            baseBar.setBackgroundResource(R.color.ux_color_translucent);
            baseBar.setItemInterval(AppDisplay.getInstance(mContext).dp2px(0.5f));
            baseBar.setHeight(AppDisplay.getInstance(mContext).dp2px(mBlackPopoverHeightDp));

            for (int j = 0; j < moreTags.size(); j++) {
                IBaseItem btnItem = new BaseItemImpl(mContext);
                btnItem.setImageResource(mBtnDrawableMap.get(moreTags.get(j)));
                btnItem.getContentView().setTag(moreTags.get(j));
                btnItem.setOnItemClickListener(mClickListener);
                btnItem.setImagePadding(AppDisplay.getInstance(mContext).dp2px(8), 0, AppDisplay.getInstance(mContext).dp2px(8), 0);
                if (j == 0) {
                    btnItem.setImageTextBackgroundResouce(R.drawable.black_popover_bg_leftbtn);
                } else if (j == moreTags.size() - 1) {
                    btnItem.setImageTextBackgroundResouce(R.drawable.black_popover_bg_rightbtn);
                } else {
                    btnItem.setImageTextBackgroundResouce(R.color.ux_color_black_popover_bg);
                }

                moreItems.add(btnItem);
                baseBar.addView(btnItem, BaseBar.TB_Position.Position_CENTER);
                baseBar.resetMargin(0, 0);
            }

            mBtnMoreItems.add(moreItems);
            mBtnMoreBars.add(baseBar);
        }
    }

    private void initThemeViews() {
        if (mUiExtensionsManager.getConfig().modules.isLoadFillSign()) {
            int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.ux_text_height_toolbar);
            int textColorResId = R.color.ux_text_color_body2_dark;
            int interval = mContext.getResources().getDimensionPixelSize(R.dimen.ux_toolbar_button_icon_text_vert_interval);

            mFillSignItem = new BaseItemImpl(mContext);
            mFillSignItem.setImageResource(R.drawable.rd_bar_fillsign_selector);
            mFillSignItem.setText(AppResource.getString(mContext.getApplicationContext(), R.string.rd_bar_fillsign));
            mFillSignItem.setRelation(IBaseItem.RELATION_BELOW);
            mFillSignItem.setInterval(interval);
            mFillSignItem.setTextSize(AppDisplay.getInstance(mContext).px2dp(textSize));
            mFillSignItem.setTextColorResource(textColorResId);
            mFillSignItem.setTag(ToolbarItemConfig.ITEM_BOTTOMBAR_FILLSIGN);
            mFillSignItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppUtil.isFastDoubleClick()) return;
                    mUiExtensionsManager.triggerDismissMenuEvent();
                    mUiExtensionsManager.setCurrentToolHandler(mToolHandler);
                }
            });

            mUiExtensionsManager.getMainFrame().getBottomToolbar().addView(mFillSignItem, BaseBar.TB_Position.Position_CENTER);
        }
        mBtnItems.clear();
        initButtonItems();
    }

    private void initButtonItems() {
        for (int i = 0; i < mBtnTags.size(); i++) {
            IBaseItem btnItem = new AnnotItemImpl(mContext);
            btnItem.setImageResource(mBtnDrawableMap.get(mBtnTags.get(i)));
            btnItem.getContentView().setTag(mBtnTags.get(i));
            btnItem.setOnItemClickListener(mClickListener);
            if (mDropDownTagSet.contains(mBtnTags.get(i))) {
                btnItem.setOnItemLongPressListener(mLongClickListener);
                btnItem.setBackgroundResource(R.drawable.annot_bar_tag_right);
            }
            mBtnItems.add(btnItem);
        }
    }

    private UIPopover mBlackPopover;
    private RelativeLayout mBlackRootView;
    private BaseBarImpl mCurBlackBar;

    private IBaseItem.OnItemClickListener mClickListener = new IBaseItem.OnItemClickListener() {
        @Override
        public void onClick(IBaseItem item, View v) {
            if (item == null)
                return;
            int tag = (int) item.getContentView().getTag();
            if (tag == 0)
                return;

            switch (tag) {
                case ToolbarItemConfig.FILLSIGN_ITEM_MORE_TEXT:
                case ToolbarItemConfig.FILLSIGN_ITEM_MORE_COMBO_TEXT:
                case ToolbarItemConfig.FILLSIGN_ITEM_MORE_CHECK:
                case ToolbarItemConfig.FILLSIGN_ITEM_MORE_X:
                case ToolbarItemConfig.FILLSIGN_ITEM_MORE_DOT:
                case ToolbarItemConfig.FILLSIGN_ITEM_MORE_LINE:
                case ToolbarItemConfig.FILLSIGN_ITEM_MORE_RECT: {
                    boolean finded = false;
                    for (int i = 0; i < mBtnMoreItems.size(); i++) {
                        ArrayList<IBaseItem> moreItems = mBtnMoreItems.get(i);
                        if (moreItems != null) {
                            for (int j = 0; j < moreItems.size(); j++) {
                                IBaseItem moreBtn = moreItems.get(j);
                                if (moreBtn == item) {
                                    if (mCurItem != null) {
                                        onItemClicked(mCurItem);
                                    }

                                    int blackTag = mWhite2BlackTagMap.get(tag);
                                    int blackDrawable = mBtnDrawableMap.get(blackTag);

                                    IBaseItem btnItem = mBtnItems.get(i);
                                    btnItem.getContentView().setTag(blackTag);
                                    btnItem.setImageResource(blackDrawable);

                                    mBtnTags.remove(i);
                                    mBtnTags.add(i, blackTag);
                                    onItemClicked(btnItem);
                                    finded = true;
                                    break;
                                }
                            }

                            if (finded) {
                                break;
                            }
                        }
                    }
                    if (mBlackPopover != null && mBlackPopover.isShowing()) {
                        mBlackPopover.dismiss();
                    }
                    break;
                }
                default:
                    onItemClicked(item);
                    break;
            }
        }
    };

    private IBaseItem.OnItemLongPressListener mLongClickListener = new IBaseItem.OnItemLongPressListener() {
        @Override
        public boolean onLongPress(IBaseItem item, View v) {
            int tag = (int) item.getContentView().getTag();
            if (tag == 0)
                return false;

            int index = mBtnItems.indexOf(item);
            if (index >= 0) {
                mCurBlackBar = mBtnMoreBars.get(index);

                if (mCurBlackBar != null) {
                    if (mBlackPopover == null) {
                        mBlackRootView = new RelativeLayout(mContext.getApplicationContext());
                        mBlackPopover = UIPopover.create((FragmentActivity) mUiExtensionsManager.getAttachedActivity(), mBlackRootView, true, true);
                    }

                    mBlackRootView.removeAllViews();
                    mBlackRootView.setBackgroundColor(AppResource.getColor(mContext, R.color.ux_color_translucent, null));
                    mBlackRootView.addView(mCurBlackBar.getContentView());

                    Point size = mCurBlackBar.measureSize();
                    Rect rect = new Rect();
                    item.getContentView().getGlobalVisibleRect(rect);

                    int arrowPos = getArrowPosition();
                    mBlackPopover.showAtLocation(mUiExtensionsManager.getRootView(), rect,
                            size.x, AppDisplay.getInstance(mContext).dp2px(mBlackPopoverHeightDp),
                            arrowPos, AppDisplay.getInstance(mContext).dp2px(12));
                }
                return true;
            }
            return false;
        }
    };

    private int getArrowPosition() {
        return UIPopover.ARROW_BOTTOM;
    }

    boolean onItemClicked(IBaseItem item) {
        if (item == null)
            return false;
        int tag = (int) item.getContentView().getTag();
        if (tag == 0)
            return false;

        if (mCurItem != null) {
            int oldTag = (int) mCurItem.getContentView().getTag();
            switch (oldTag) {
                case ToolbarItemConfig.FILLSIGN_ITEM_TEXT:
                case ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT:
                case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
                case ToolbarItemConfig.FILLSIGN_ITEM_X:
                case ToolbarItemConfig.FILLSIGN_ITEM_DOT:
                case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
                case ToolbarItemConfig.FILLSIGN_ITEM_RECT:
                case ToolbarItemConfig.FILLSIGN_ITEM_PROFILE:
                    break;
                default:
                    break;
            }
        }

        setCurrentItem(item);

        switch (tag) {
            case ToolbarItemConfig.FILLSIGN_ITEM_TEXT:
            case ToolbarItemConfig.FILLSIGN_ITEM_COMBO_TEXT:
            case ToolbarItemConfig.FILLSIGN_ITEM_CHECK:
            case ToolbarItemConfig.FILLSIGN_ITEM_X:
            case ToolbarItemConfig.FILLSIGN_ITEM_DOT:
            case ToolbarItemConfig.FILLSIGN_ITEM_LINE:
            case ToolbarItemConfig.FILLSIGN_ITEM_RECT:
                break;
            case ToolbarItemConfig.FILLSIGN_ITEM_PROFILE:
                if (item.isChecked())
                    showProfileDialog();
                return true;
            default:
                break;
        }

        mToolHandler.onItemClicked(item);
        return true;
    }

    IBaseItem getCurrentItem() {
        return mCurItem;
    }

    void setCurrentItem(IBaseItem item) {
        for (int i = 0; i < mBtnItems.size(); i++) {
            if (mBtnItems.get(i) != item) {
                mBtnItems.get(i).setChecked(false);
            } else {
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    mCurItem = item;
                } else {
                    mCurItem = null;
                }
            }
        }

        if (item == null) {
            mCurItem = null;
        }
    }

    public boolean onKeyBack() {
        if (mCurItem != null) {
            onItemClicked(mCurItem);
            return true;
        }
        if (mUiExtensionsManager.getCurrentToolHandler() == mToolHandler) {
            mUiExtensionsManager.setCurrentToolHandler(null);
            return true;
        }
        return false;
    }

    public ToolHandler getToolHandler() {
        return mToolHandler;
    }

    private PDFViewCtrl.IScaleGestureEventListener mScaleGestureEventListener = new PDFViewCtrl.IScaleGestureEventListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mToolHandler.onScaleEnd();
        }
    };

    private PDFViewCtrl.IPageEventListener mPageEventListener = new OnPageEventListener() {

        @Override
        public void onPagesRemoved(boolean success, int[] pageIndexes) {
            for (int i = 0; i < pageIndexes.length; i++)
                mToolHandler.onPagesRemoved(success, pageIndexes[i] - i);
        }

        @Override
        public void onPageMoved(boolean success, int index, int dstIndex) {
            mToolHandler.onPageMoved(success, index, dstIndex);
        }

        @Override
        public void onPagesInserted(boolean success, int dstIndex, int[] pageRanges) {
            mToolHandler.onPagesInserted(success, dstIndex, pageRanges);
        }
    };

    private PDFViewCtrl.IDrawEventListener mDrawEventListener = new PDFViewCtrl.IDrawEventListener() {
        @Override
        public void onDraw(int pageIndex, Canvas canvas) {
            mToolHandler.onDrawForControls(canvas);
        }
    };

    private PDFViewCtrl.IDocEventListener mDocEventListener = new PDFViewCtrl.IDocEventListener() {
        @Override
        public void onDocWillOpen() {
        }

        @Override
        public void onDocOpened(PDFDoc document, int errCode) {
            mDocWillClose = false;
            resetFillSignItem();

            if (mUiExtensionsManager.getCurrentToolHandler() == mToolHandler) {
                resetCustomBar();
            }
        }

        @Override
        public void onDocWillClose(PDFDoc document) {
            mDocWillClose = true;
        }

        @Override
        public void onDocClosed(PDFDoc document, int errCode) {
            mToolHandler.release();
        }

        @Override
        public void onDocWillSave(PDFDoc document) {
            mToolHandler.release();
        }

        @Override
        public void onDocSaved(PDFDoc document, int errCode) {
        }
    };

    private UIExtensionsManager.ToolHandlerChangedListener mToolHandlerChangedListener = new UIExtensionsManager.ToolHandlerChangedListener() {
        @Override
        public void onToolHandlerChanged(ToolHandler oldToolHandler, ToolHandler newToolHandler) {
            if (newToolHandler == mToolHandler && oldToolHandler != mToolHandler) {
                resetCustomBar();
            }
        }
    };

    private IStateChangeListener mStateChangeListener = new IStateChangeListener() {
        @Override
        public void onStateChanged(int oldState, int newState) {
            if (mDocWillClose) return;

            if (newState != ReadStateConfig.STATE_FILLSIGN && mUiExtensionsManager.getCurrentToolHandler() == mToolHandler)
                mUiExtensionsManager.setCurrentToolHandler(null);

            resetFillSignItem();
        }
    };

    private void resetFillSignItem() {
        if (mFillSignItem != null) {
            if (!mUiExtensionsManager.getDocumentManager().hasForm()
                    && mUiExtensionsManager.getDocumentManager().canAddSignature()
                    && mUiExtensionsManager.getDocumentManager().canModifyContents()) {
                mFillSignItem.setEnable(true);
            } else {
                mFillSignItem.setEnable(false);
            }
        }
    }

    private AnnotEventListener mAnnotEventListener = new AnnotEventListener() {

        private List<String> mWillDeleteWidgets = new ArrayList<>();

        @Override
        public void onAnnotAdded(PDFPage page, Annot annot) {
            try {
                if (!annot.isEmpty() && annot.getType() == Annot.e_Widget) {
                    resetFillSignItem();
                }
            } catch (PDFException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnnotWillDelete(PDFPage page, Annot annot) {
            try {
                if (!annot.isEmpty() && annot.getType() == Annot.e_Widget) {
                    mWillDeleteWidgets.add(AppAnnotUtil.getAnnotUniqueID(annot));
                }
            } catch (PDFException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnnotDeleted(PDFPage page, Annot annot) {
            if (mWillDeleteWidgets.size() == 0) return;
            mWillDeleteWidgets.remove(0);
            resetFillSignItem();
        }

        @Override
        public void onAnnotModified(PDFPage page, Annot annot) {
        }

        @Override
        public void onAnnotChanged(Annot lastAnnot, Annot currentAnnot) {
        }
    };

    private void resetCustomBar() {
        // add button to bottom bar
        BaseBarImpl topBar = (BaseBarImpl) mUiExtensionsManager.getMainFrame().getCustomTopbar();
        topBar.removeAllItems();
        topBar.setBackgroundColor(mContext.getResources().getColor(R.color.ux_bg_color_toolbar_light));

        IBaseItem closeItem = new BaseItemImpl(mContext);
        closeItem.setImageResource(R.drawable.rd_reflow_back_selector);
        closeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isFastDoubleClick()) return;
                mUiExtensionsManager.setCurrentToolHandler(null);
                mUiExtensionsManager.getPDFViewCtrl().invalidate();
            }
        });
        IBaseItem titleItem = new BaseItemImpl(mContext);
        titleItem.setText(AppResource.getString(mContext.getApplicationContext(), R.string.rd_bar_fillsign));
        titleItem.setTextSize(AppDisplay.getInstance(mContext).px2dp(mContext.getResources().getDimension(R.dimen.ux_text_height_subhead)));
        titleItem.setTextColor(mContext.getResources().getColor(R.color.ux_text_color_title_dark));

        topBar.addView(closeItem, BaseBar.TB_Position.Position_LT);
        topBar.addView(titleItem, BaseBar.TB_Position.Position_CENTER);

        // add button to bottom bar
        BaseBarImpl bottomBar = (BaseBarImpl) mUiExtensionsManager.getMainFrame().getCustomBottombar();
        bottomBar.removeAllItems();
        bottomBar.setBackgroundColor(AppResource.getColor(mContext, R.color.ux_bg_color_toolbar_light, null));
        if (AppDisplay.getInstance(mContext).isPad()) {
            bottomBar.setItemInterval(AppResource.getDimensionPixelSize(mContext, R.dimen.ux_bottombar_button_space_pad));
        } else {
            bottomBar.setItemInterval(AppResource.getDimensionPixelSize(mContext, R.dimen.ux_bottombar_button_space_phone));
        }

        for (int i = 0; i < mBtnItems.size(); i++) {
            IBaseItem btnItem = mBtnItems.get(i);
            bottomBar.addView(btnItem, BaseBar.TB_Position.Position_CENTER);
            if (mUiExtensionsManager.getDocumentManager().hasForm() || !mUiExtensionsManager.getDocumentManager().canModifyContents()) {
                btnItem.setEnable(false);
            } else {
                btnItem.setEnable(true);
            }
        }
    }

    private void showProfileDialog() {
        final ArrayList<ProFileItem> items = new ArrayList<>();
        final int customStartIndex = loadItemsFromSp(items);

        Activity activity = mUiExtensionsManager.getAttachedActivity();
        final UIMatchDialog dialog = new UIMatchDialog(activity);
        final ViewGroup rootLayout = (ViewGroup) View.inflate(mContext.getApplicationContext(), R.layout.fillsign_profile, null);
        final ScrollView scrollView = rootLayout.findViewById(R.id.fillsign_profile_scrollview);
        final ViewGroup tableView = rootLayout.findViewById(R.id.fillsign_profile_table);
        final View addCustomView = rootLayout.findViewById(R.id.profile_bottom_bar);
        UIBtnImageView addCustomViewIcon = rootLayout.findViewById(R.id.profile_add_custom_icon);
        addCustomView.setVisibility(View.GONE);
        final IBaseItem editItem = new BaseItemImpl(mContext);
        editItem.setText(AppResource.getString(mContext, R.string.fx_string_edit));
        editItem.setTextSize(14.0f);
        editItem.setTextColorResource(R.color.ux_text_color_title_light);

        final View.OnClickListener editListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addCustomView.getVisibility() != View.VISIBLE) {
                    addCustomView.setVisibility(View.VISIBLE);
                    editItem.setText(R.string.fx_string_done);
                } else {
                    addCustomView.setVisibility(View.GONE);
                    editItem.setText(R.string.fx_string_edit);
                }

                boolean editable = addCustomView.getVisibility() == View.VISIBLE;

                if (!editable) {
                    saveItemsToSp(tableView, items);
                    AppKeyboardUtil.hideInputMethodWindow(mContext, dialog.getWindow());
                }

                for (int i = 0; i < items.size(); i++) {
                    ProFileItem item = items.get(i);
                    if (!item.mIsGroupTitle) {
                        if (!editable) {
                            item.mSubjectEt.clearFocus();
                            item.mContentEt.clearFocus();
                        }
                        if (item.mIsCustom) {
                            item.mSubjectEt.setFocusable(editable);
                            item.mSubjectEt.setFocusableInTouchMode(editable);
                            item.mDeleteView.setVisibility(editable ? View.VISIBLE : View.GONE);
                        }
                        if (!AppUtil.isEqual(item.mSpKey, mProfileInfo.KEY_DATE)) {
                            item.mContentEt.setFocusable(editable);
                            item.mContentEt.setFocusableInTouchMode(editable);
                        }
                    }
                }
            }
        };

        for (int i = 0; i < items.size(); i++) {
            ProFileItem item = items.get(i);

            boolean showDivider = true;
            if (i == items.size() - 1) {
                showDivider = false;
            } else if (item.mIsGroupTitle) {
                showDivider = false;
            } else if (items.get(i + 1).mIsGroupTitle) {
                showDivider = false;
            }

            addProfileItem(dialog, tableView, addCustomView, editListener, items, item, showDivider);
        }

        View.OnClickListener addCustomViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.size() == customStartIndex) {
                    ProFileItem item = new ProFileItem(true, true, AppResource.getString(mContext, R.string.fillsign_profile_custom_field), null, null, null);
                    items.add(item);
                    addProfileItem(dialog, tableView, addCustomView, editListener, items, item, false);
                }

                ProFileItem lastItem = items.get(items.size() - 1);
                if (!lastItem.mIsGroupTitle) {
                    lastItem.mDividerView.setVisibility(View.VISIBLE);
                }

                final ProFileItem item = new ProFileItem(false, true, "", AppResource.getString(mContext, R.string.fillsign_profile_type_value), "", null);
                items.add(item);
                addProfileItem(dialog, tableView, addCustomView, editListener, items, item, false);

                AppThreadManager.getInstance().getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                        item.mSubjectEt.requestFocus();
                        AppUtil.showSoftInput(item.mSubjectEt);
                    }
                });
            }
        };
        addCustomView.setOnClickListener(addCustomViewListener);
        addCustomViewIcon.setOnClickListener(addCustomViewListener);

        dialog.setContentView(rootLayout);
        dialog.setTitle(AppResource.getString(mContext, R.string.fillsign_profile_title));
        dialog.setTitlePosition(BaseBar.TB_Position.Position_CENTER);
        if (AppDisplay.getInstance(mContext).isPad()) {
            dialog.setBackButtonVisible(View.GONE);
        } else {
            dialog.setBackButtonVisible(View.VISIBLE);
        }

        BaseBar topBar = dialog.getTitleBar();
        topBar.addView(editItem, BaseBar.TB_Position.Position_RB);
        editItem.setOnClickListener(editListener);
        dialog.showDialog();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final boolean[] isSelctedText = {false};
        dialog.setListener(new MatchDialog.DialogListener() {
            @Override
            public void onResult(long btType) {
                if (btType == MatchDialog.DIALOG_OK) {
                    isSelctedText[0] = true;
                }
                dialog.dismiss();
            }

            @Override
            public void onBackClick() {
            }
        });

        dialog.setOnDLDismissListener(new MatchDialog.DismissListener() {
            @Override
            public void onDismiss() {
                if (!isSelctedText[0]) {
                    onItemClicked(mCurItem);
                }
            }
        });
    }

    private void addProfileItem(final UIMatchDialog dlg, final ViewGroup tableView, final View bottomBar, final View.OnClickListener editListener,
                                final ArrayList<ProFileItem> items, final ProFileItem item, boolean showDivider) {
        if (item.mIsGroupTitle) {
            TextView tv = new TextView(mContext);
            tv.setText(item.mSubject);
            tv.setBackgroundColor(AppResource.getColor(mContext, R.color.ux_color_toolbar, null));
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            tv.setPadding(AppResource.getDimensionPixelSize(mContext, R.dimen.ux_screen_margin_text), 0, 0, 0);
            tv.setTextColor(AppResource.getColor(mContext, R.color.ux_color_grey_ff878787, null));
            tv.setTextSize(AppDisplay.getInstance(mContext).px2dp(AppResource.getDimension(mContext, R.dimen.ux_text_size_12sp)));

            item.mItemRootView = tv;
            tableView.addView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppDisplay.getInstance(mContext).dp2px(26)));
        } else {
            final View itemView = View.inflate(mContext, R.layout.fillsign_profile_item, null);
            EditText subjectEt = (EditText) itemView.findViewById(R.id.fs_item_subject);
            EditText contentEt = (EditText) itemView.findViewById(R.id.fs_item_content);
            View deleteView = itemView.findViewById(R.id.fs_item_delete);

            subjectEt.setText(item.mSubject);

            contentEt.setHint(item.mContentHint);
            contentEt.setText(item.mContent);

            item.mItemRootView = itemView;
            item.mSubjectEt = subjectEt;
            item.mContentEt = contentEt;
            item.mDeleteView = deleteView;

            tableView.addView(itemView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            View dividerView = new View(mContext);
            dividerView.setBackgroundColor(AppResource.getColor(mContext, R.color.ux_color_list_item_divider, null));

            tableView.addView(dividerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppResource.getDimensionPixelSize(mContext, R.dimen.ux_list_item_divier_height)));
            item.mDividerView = dividerView;

            if (!showDivider) {
                dividerView.setVisibility(View.GONE);
            }

            if (bottomBar.getVisibility() == View.VISIBLE) {
                if (item.mIsCustom) {
                    item.mSubjectEt.setFocusable(true);
                    item.mSubjectEt.setFocusableInTouchMode(true);
                }
                item.mContentEt.setFocusable(true);
                item.mContentEt.setFocusableInTouchMode(true);

                if (item.mIsCustom) {
                    deleteView.setVisibility(View.VISIBLE);
                }
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bottomBar.getVisibility() == View.VISIBLE) {
                        return;
                    } else if (AppUtil.isEmpty(item.mContentEt.getText().toString())) {
                        editListener.onClick(null);
                        item.mContentEt.requestFocus();
                        AppUtil.showSoftInput(item.mContentEt);
                        return;
                    }

                    UIToast.getInstance(mContext).show(AppResource.getString(mContext, R.string.fillsign_sign_click_prompt));
                    mProfileStr = item.mContentEt.getText().toString();
                    dlg.getDialogListerner().onResult(UIMatchDialog.DIALOG_OK);
                }
            };

            subjectEt.setOnClickListener(listener);
            contentEt.setOnClickListener(listener);

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _removeProfileItem(items, item, tableView);
                }
            });
        }
    }

    private int loadItemsFromSp(ArrayList<ProFileItem> items) {
        items.add(new ProFileItem(true, false, AppResource.getString(mContext, R.string.fx_string_name), null, null, null));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_full_name), AppResource.getString(mContext, R.string.fillsign_profile_full_name_prompt), mProfileInfo.getFullName(), mProfileInfo.KEY_FULL_NAME));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_first_name), AppResource.getString(mContext, R.string.fillsign_profile_first_name_prompt), mProfileInfo.getFirstName(), mProfileInfo.KEY_FIRST_NAME));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_middle_name), AppResource.getString(mContext, R.string.fillsign_profile_middle_name_prompt), mProfileInfo.getMiddleName(), mProfileInfo.KEY_MIDDLE_NAME));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_last_name), AppResource.getString(mContext, R.string.fillsign_profile_last_name_prompt), mProfileInfo.getLastName(), mProfileInfo.KEY_LAST_NAME));

        items.add(new ProFileItem(true, false, AppResource.getString(mContext, R.string.fx_string_address), null, null, null));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_street_1), AppResource.getString(mContext, R.string.fillsign_profile_street_1_prompt), mProfileInfo.getStreet1(), mProfileInfo.KEY_STREET_1));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_street_2), AppResource.getString(mContext, R.string.fillsign_profile_street_2_prompt), mProfileInfo.getStreet2(), mProfileInfo.KEY_STREET_2));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_city), AppResource.getString(mContext, R.string.fillsign_profile_city_prompt), mProfileInfo.getCity(), mProfileInfo.KEY_CITY));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_state), AppResource.getString(mContext, R.string.fillsign_profile_state_prompt), mProfileInfo.getState(), mProfileInfo.KEY_STATE));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_zip), AppResource.getString(mContext, R.string.fillsign_profile_zip_prompt), mProfileInfo.getPostCode(), mProfileInfo.KEY_POSTCODE));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_country), AppResource.getString(mContext, R.string.fillsign_profile_country_prompt), mProfileInfo.getCountry(), mProfileInfo.KEY_COUNTRY));

        items.add(new ProFileItem(true, false, AppResource.getString(mContext, R.string.fillsign_profile_contact), null, null, null));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_email), AppResource.getString(mContext, R.string.fillsign_profile_email_prompt), mProfileInfo.getEmail(), mProfileInfo.KEY_EMAIL));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_tel), AppResource.getString(mContext, R.string.fillsign_profile_tel_prompt), mProfileInfo.getTel(), mProfileInfo.KEY_TEL));

        items.add(new ProFileItem(true, false, AppResource.getString(mContext, R.string.fillsign_profile_dates), null, null, null));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_date), AppResource.getString(mContext, R.string.fillsign_profile_date_prompt), mProfileInfo.getDate(), mProfileInfo.KEY_DATE));
        items.add(new ProFileItem(false, false, AppResource.getString(mContext, R.string.fillsign_profile_birth_date), AppResource.getString(mContext, R.string.fillsign_profile_birth_date_prompt), mProfileInfo.getBirtyDate(), mProfileInfo.KEY_BIRTH_DATE));

        final int customStartIndex = items.size();

        final ArrayList<String> customFields = mProfileInfo.getCustomFields();
        final ArrayList<String> customValues = mProfileInfo.getCustomValues();
        if (customFields.size() > 0) {
            items.add(new ProFileItem(true, true, AppResource.getString(mContext, R.string.fillsign_profile_custom_field), null, null, null));
            for (int i = 0; i < customFields.size(); i++) {
                String field = customFields.get(i);
                String value = customValues.get(i);
                items.add(new ProFileItem(false, true, field, AppResource.getString(mContext, R.string.fillsign_profile_type_value), value, null));
            }
        }

        return customStartIndex;
    }

    private void saveItemsToSp(ViewGroup tableView, ArrayList<ProFileItem> items) {
        ArrayList<ProFileItem> removeItems = new ArrayList<>();
        ArrayList<String> customFields = new ArrayList<>();
        ArrayList<String> customValues = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            ProFileItem item = items.get(i);
            if (item.mIsGroupTitle)
                continue;

            item.mSubject = item.mSubjectEt.getText().toString();
            item.mContent = item.mContentEt.getText().toString();

            if (!item.mIsCustom) {
                mProfileInfo.setString(item.mSpKey, item.mContent);
            } else {
                if (AppUtil.isEmpty(item.mSubject)) {
                    removeItems.add(item);
                } else {
                    customFields.add(item.mSubject);
                    customValues.add(item.mContent);
                }
            }
        }

        mProfileInfo.saveCustomFields(customFields, customValues);

        for (int i = 0; i < removeItems.size(); i++) {
            _removeProfileItem(items, removeItems.get(i), tableView);
        }

        ProFileItem lastItem = items.get(items.size() - 1);
        if (lastItem.mIsGroupTitle) {
            _removeProfileItem(items, lastItem, tableView);
        }
    }

    private void _removeProfileItem(ArrayList<ProFileItem> items, ProFileItem item, ViewGroup tableView) {
        items.remove(item);
        tableView.removeView(item.mItemRootView);
        tableView.removeView(item.mDividerView);
    }

    static class ProFileItem {
        private boolean mIsGroupTitle;
        private boolean mIsCustom;
        private String mSubject;
        private String mContentHint;
        private String mContent;
        private String mSpKey;

        private View mItemRootView;
        private View mDividerView;
        private EditText mSubjectEt;
        private EditText mContentEt;
        private View mDeleteView;

        ProFileItem(boolean isTitle, boolean isCustom, String subject, String hint, String content, String key) {
            mIsGroupTitle = isTitle;
            mIsCustom = isCustom;
            mSubject = subject;
            mContentHint = hint;
            mContent = content;
            mSpKey = key;
        }
    }

}

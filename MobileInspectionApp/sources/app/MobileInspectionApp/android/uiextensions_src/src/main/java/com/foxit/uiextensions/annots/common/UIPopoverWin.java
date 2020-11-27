package com.foxit.uiextensions.annots.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.foxit.uiextensions.R;
import com.foxit.uiextensions.controls.toolbar.BaseBar;
import com.foxit.uiextensions.controls.toolbar.IBaseItem;
import com.foxit.uiextensions.controls.toolbar.impl.BaseBarImpl;
import com.foxit.uiextensions.controls.toolbar.impl.BaseItemImpl;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppResource;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentActivity;

public class UIPopoverWin extends UIPopupWindow {

    public static UIPopoverWin create(FragmentActivity act, View contentView, boolean blackStyle, boolean showDivider) {
        RelativeLayout rootView0 = new RelativeLayout(act.getApplicationContext());
        UIPopoverWin popupDlg = new UIPopoverWin(act.getApplicationContext(), rootView0);
        popupDlg.setArrowStyle(blackStyle);
        popupDlg.setShowDivider(showDivider);
        if (blackStyle)
            popupDlg.init(act, contentView, "UI_BLACK_POPOVER_FRAGMENT", false, !blackStyle);
        else
            popupDlg.init(act, contentView, "UI_WHITE_POPOVER_FRAGMENT", false, !blackStyle);
        return popupDlg;
    }

    public static final int ARROW_AUTO = 0;
    public static final int ARROW_LEFT = 1;
    public static final int ARROW_TOP = 2;
    public static final int ARROW_RIGHT = 3;
    public static final int ARROW_BOTTOM = 4;
    public static final int ARROW_CENTER = 5;

    boolean mIsBlackStyle;
    boolean mIsShowDivider;
    int mArrowDistance = 12;
    int mArrowWidthDp = 8;
    int mArrowHeightDp = 20;
    int mArrowColorRes = R.color.ux_color_popwnd_background;
    int mArrowPosition = ARROW_LEFT;
    boolean mShowArrow = true;
    ArrowImageView mArrowView;
    int mCornerRadius;
    private Context mContext;

    UIPopoverWin(Context context, View v) {
        super(context, v);

        mContext = context;
        setOutsideTouchable(false);
        setFocusable(false);
    }

    void setArrowStyle(boolean blackStyle) {
        mIsBlackStyle = blackStyle;
        if (blackStyle) {
            mArrowDistance = 12;
            mArrowWidthDp = 8;
            mArrowHeightDp = 20;
            mArrowColorRes = R.color.ux_color_black_popover_bg;
            mCornerRadius = AppDisplay.getInstance(mContext).dp2px(8);
        } else {
            mArrowDistance = 12;
            mArrowWidthDp = 16;
            mArrowHeightDp = 30;
            mArrowColorRes = R.color.ux_color_popwnd_background;
            mCornerRadius = AppDisplay.getInstance(mContext).dp2px(3);
        }
    }

    void setShowDivider(boolean showDivider) {
        mIsShowDivider = showDivider;
    }

    protected void init(FragmentActivity act, View contentView, String fragName, boolean withMask, boolean withShadow) {
        super.init(act, contentView, fragName, withMask, withShadow);

        mArrowView = new ArrowImageView(act);

        mArrowView.setMinimumWidth(AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp));
        mArrowView.setMaxWidth(AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp));
        mArrowView.setMinimumHeight(AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp));
        mArrowView.setMaxHeight(AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp));

        RelativeLayout.LayoutParams angleLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        angleLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        angleLp.addRule(RelativeLayout.CENTER_VERTICAL);
        mArrowView.setLayoutParams(angleLp);

        mRootView0.addView(mArrowView, 1);

        if (mIsBlackStyle) {
            mRootViewWithShadow.setBackgroundResource(R.drawable.black_popover_bg);
            ((RelativeLayout.LayoutParams) mRootViewWithShadow.getLayoutParams()).setMargins(
                    AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp), 0, 0, 0);

            if (mIsShowDivider) {
                RelativeLayout dividerView = new RelativeLayout(act);
                RelativeLayout.LayoutParams dividerLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dividerLp.setMargins(AppDisplay.getInstance(mContext).dp2px(24), 0, AppDisplay.getInstance(mContext).dp2px(24), 0);
                dividerView.setLayoutParams(dividerLp);
                dividerView.setBackgroundColor(AppResource.getColor(mContext, R.color.ux_color_white, null));
                mRootViewWithShadow.addView(dividerView, 0);
            }
        } else {
            angleLp.setMargins(mShadowWidth - AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp), 0, 0, 0);
        }
    }

    public void setBackgroundResource(int resId) {
        mRootViewWithShadow.setBackgroundResource(resId);
    }

    public void setArrowColorRes(int resId) {
        mArrowColorRes = resId;
    }

    public void setShowArrow(boolean isShow) {
        mShowArrow = isShow;
    }

    public void showAtLocation(ViewGroup parent, Rect rect, int contentWidth, int contentHeight, int arrowPosition, int arrowDist) {
        int screenWidth = parent.getWidth();
        int screenHeight = parent.getHeight();
        int width = contentWidth + getShadowLength() * 2;
        int height = contentHeight + getShadowLength() * 2;

        mArrowPosition = arrowPosition;
        mArrowDistance = arrowDist;

        if (mArrowPosition == ARROW_AUTO) {
            if (rect.top - height > 0) {
                mArrowPosition = ARROW_BOTTOM;
            } else if (rect.right + width < screenWidth) {
                mArrowPosition = ARROW_LEFT;
            } else if (rect.left - width > 0) {
                mArrowPosition = ARROW_RIGHT;
            } else if (rect.bottom + height < screenHeight) {
                mArrowPosition = ARROW_TOP;
            } else {
                mArrowPosition = ARROW_CENTER;
            }
        }

        int leftMargin = 0;
        int rightMargin = 0;
        int topMargin = 0;
        int bottomMargin = 0;

        if (mShowArrow) {
            RelativeLayout.LayoutParams angleLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mArrowView.setLayoutParams(angleLp);
            mArrowView.setVisibility(View.VISIBLE);

            int screenInterval = 0;// AppResource.getDimensionPixelSize("", R.dimen.ui_screen_margin_icon);

            if (mArrowPosition == ARROW_LEFT) {
                angleLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                angleLp.addRule(RelativeLayout.CENTER_VERTICAL);

                if (rect.centerY() - height / 2 < screenInterval) {
                    topMargin = height / 2 - rect.centerY() + screenInterval;
                    angleLp.topMargin = height / 2 - AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp) / 2 - topMargin;
                    if (angleLp.topMargin < mCornerRadius + mShadowWidth) {
                        angleLp.topMargin = mCornerRadius + mShadowWidth;
                    }
                    angleLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                } else if (rect.centerY() + height / 2 > screenHeight - screenInterval) {
                    bottomMargin = rect.centerY() + height / 2 - screenHeight + screenInterval;
                    angleLp.bottomMargin = height / 2 - AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp) / 2 - bottomMargin;
                    if (angleLp.bottomMargin < mCornerRadius + mShadowWidth) {
                        angleLp.bottomMargin = mCornerRadius + mShadowWidth;
                    }
                    angleLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                } else {
                    angleLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                }

                if (mIsBlackStyle) {
                    width += AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp);
                    ((RelativeLayout.LayoutParams) mRootViewWithShadow.getLayoutParams()).setMargins(
                            AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp), 0, 0, 0);
                } else {
                    angleLp.leftMargin = mShadowWidth - AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp);
                }
            } else if (mArrowPosition == ARROW_RIGHT) {
                angleLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                angleLp.addRule(RelativeLayout.CENTER_VERTICAL);

                if (rect.centerY() - height / 2 < screenInterval) {
                    topMargin = height / 2 - rect.centerY() + screenInterval;
                    angleLp.topMargin = height / 2 - AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp) / 2 - topMargin;
                    if (angleLp.topMargin < mCornerRadius + mShadowWidth) {
                        angleLp.topMargin = mCornerRadius + mShadowWidth;
                    }
                    angleLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                } else if (rect.centerY() + height / 2 > screenHeight - screenInterval) {
                    bottomMargin = rect.centerY() + height / 2 - screenHeight + screenInterval;
                    angleLp.bottomMargin = height / 2 - AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp) / 2 - bottomMargin;
                    if (angleLp.bottomMargin < mCornerRadius + mShadowWidth) {
                        angleLp.bottomMargin = mCornerRadius + mShadowWidth;
                    }
                    angleLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                } else {
                    angleLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                }

                if (mIsBlackStyle) {
                    width += AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp);
                    ((RelativeLayout.LayoutParams) mRootViewWithShadow.getLayoutParams()).setMargins(
                            0, 0, AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp), 0);
                } else {
                    angleLp.rightMargin = mShadowWidth - AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp);
                }
            } else if (mArrowPosition == ARROW_TOP) {
                angleLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                if (rect.centerX() - width / 2 < screenInterval) {
                    leftMargin = width / 2 + screenInterval - rect.centerX();
                    angleLp.leftMargin = width / 2 - AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp) / 2 - leftMargin;
                    if (angleLp.leftMargin < mCornerRadius + mShadowWidth) {
                        angleLp.leftMargin = mCornerRadius + mShadowWidth;
                    }
                    angleLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                } else if (rect.centerX() + width / 2 > screenWidth - screenInterval) {
                    rightMargin = rect.centerX() + width / 2 - screenWidth + screenInterval;
                    angleLp.rightMargin = width / 2 - AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp) / 2 - rightMargin;
                    if (angleLp.rightMargin < mCornerRadius + mShadowWidth) {
                        angleLp.rightMargin = mCornerRadius + mShadowWidth;
                    }
                    angleLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else {
                    angleLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                }

                if (mIsBlackStyle) {
                    height += AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp);
                    ((RelativeLayout.LayoutParams) mRootViewWithShadow.getLayoutParams()).setMargins(
                            0, AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp), 0, 0);
                } else {
                    angleLp.topMargin = mShadowWidth - AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp);
                }
            } else if (mArrowPosition == ARROW_BOTTOM) {
                angleLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                if (rect.centerX() - width / 2 < screenInterval) {
                    leftMargin = width / 2 + screenInterval - rect.centerX();
                    angleLp.leftMargin = width / 2 - AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp) / 2 - leftMargin;
                    if (angleLp.leftMargin < mCornerRadius + mShadowWidth) {
                        angleLp.leftMargin = mCornerRadius + mShadowWidth;
                    }
                    angleLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                } else if (rect.centerX() + width / 2 > screenWidth - screenInterval) {
                    rightMargin = rect.centerX() + width / 2 - screenWidth + screenInterval;
                    angleLp.rightMargin = width / 2 - AppDisplay.getInstance(mContext).dp2px(mArrowHeightDp) / 2 - rightMargin;
                    if (angleLp.rightMargin < mCornerRadius + mShadowWidth) {
                        angleLp.rightMargin = mCornerRadius + mShadowWidth;
                    }
                    angleLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else {
                    angleLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                }

                if (mIsBlackStyle) {
                    height += AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp);
                    ((RelativeLayout.LayoutParams) mRootViewWithShadow.getLayoutParams()).setMargins(
                            0, 0, 0, AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp));
                } else {
                    angleLp.bottomMargin = mShadowWidth - AppDisplay.getInstance(mContext).dp2px(mArrowWidthDp);
                }
            } else if (mArrowPosition == ARROW_CENTER) {
                mArrowView.setVisibility(View.INVISIBLE);
            }
        } else {
            mArrowView.setVisibility(View.INVISIBLE);
        }

        setWidth(width);
        setHeight(height);

        if (!isShowing()) {
            if (mArrowPosition == ARROW_LEFT) {
                showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
                        rect.right + mArrowDistance,
                        rect.centerY() - mHeight / 2 + topMargin - bottomMargin);
            } else if (mArrowPosition == ARROW_RIGHT) {
                showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
                        rect.left - mArrowDistance - width,
                        rect.centerY() - mHeight / 2 + topMargin - bottomMargin);
            } else if (mArrowPosition == ARROW_TOP) {
                showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
                        rect.centerX() - mWidth / 2,
                        rect.bottom + mArrowDistance);
            } else if (mArrowPosition == ARROW_BOTTOM) {
                showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
                        rect.centerX() - mWidth / 2 + leftMargin - rightMargin,
                        rect.top - mArrowDistance - height);
            } else {
                showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
                        rect.centerX() - mWidth / 2,
                        rect.centerY() - mHeight / 2);
            }
        } else {
            if (mArrowPosition == ARROW_LEFT) {
                update(rect.right + mArrowDistance,
                        rect.centerY() - mHeight / 2 + topMargin - bottomMargin,
                        width, height);
            } else if (mArrowPosition == ARROW_RIGHT) {
                update(rect.left - mArrowDistance - width,
                        rect.centerY() - mHeight / 2 + topMargin - bottomMargin,
                        width, height);
            } else if (mArrowPosition == ARROW_TOP) {
                update(rect.centerX() - mWidth / 2,
                        rect.bottom + mArrowDistance,
                        width, height);
            } else if (mArrowPosition == ARROW_BOTTOM) {
                update(rect.centerX() - mWidth / 2 + leftMargin - rightMargin,
                        rect.top - mArrowDistance - height,
                        width, height);
            } else {
                update(rect.centerX() - mWidth / 2,
                        rect.centerY() - mHeight / 2,
                        width, height);
            }
        }
    }

    public void update(ViewGroup parent, Rect rect, int contentWidth, int contentHeight,int arrowPosition) {
        mArrowPosition = arrowPosition;
        showAtLocation(parent, rect, contentWidth, contentHeight, mArrowPosition, mArrowDistance);
    }

    class ArrowImageView extends AppCompatImageView {
        Paint mPaint = new Paint();
        Path mPath = new Path();

        public ArrowImageView(Context context) {
            super(context);
        }

        public ArrowImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ArrowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
            mPaint.setColor(AppResource.getColor(getContext(), mArrowColorRes, null));

            int width = getWidth();
            int height = getHeight();
            AppDisplay display = AppDisplay.getInstance(getContext());

            mPath.reset();

            switch (mArrowPosition) {
                case ARROW_LEFT:
                    mPath.moveTo(0, height / 2);
                    mPath.lineTo(display.dp2px(mArrowWidthDp), height / 2 - display.dp2px(mArrowHeightDp) / 2);
                    mPath.lineTo(display.dp2px(mArrowWidthDp), height / 2 + display.dp2px(mArrowHeightDp) / 2);
                    mPath.close();
                    break;
                case ARROW_RIGHT:
                    mPath.moveTo(width, height / 2);
                    mPath.lineTo(width - display.dp2px(mArrowWidthDp), height / 2 - display.dp2px(mArrowHeightDp) / 2);
                    mPath.lineTo(width - display.dp2px(mArrowWidthDp), height / 2 + display.dp2px(mArrowHeightDp) / 2);
                    mPath.close();
                    break;
                case ARROW_TOP:
                    mPath.moveTo(width / 2, 0);
                    mPath.lineTo(width / 2 - display.dp2px(mArrowHeightDp) / 2, display.dp2px(mArrowWidthDp));
                    mPath.lineTo(width / 2 + display.dp2px(mArrowHeightDp) / 2, display.dp2px(mArrowWidthDp));
                    mPath.close();
                    break;
                case ARROW_BOTTOM:
                    mPath.moveTo(width / 2, height);
                    mPath.lineTo(width / 2 - display.dp2px(mArrowHeightDp) / 2, height - display.dp2px(mArrowWidthDp));
                    mPath.lineTo(width / 2 + display.dp2px(mArrowHeightDp) / 2, height - display.dp2px(mArrowWidthDp));
                    mPath.close();
                    break;
            }

            canvas.drawPath(mPath, mPaint);
        }
    }

    ;

    public static class POPOVER_ITEM {
        int mTag;
        int mIconRes;
        String mText;

        public POPOVER_ITEM(int tag, int icon) {
            mTag = tag;
            mIconRes = icon;
        }

        public POPOVER_ITEM(int tag, String text) {
            mTag = tag;
            mText = text;
        }
    }

    ;

    public static int mBlackPopoverHeightDp = 36;
    static UIPopoverWin mPopover;
    static RelativeLayout mPopRootView;
    static BaseBarImpl mPopBar;

    static UIPopoverWin _popover(FragmentActivity context) {
        if (mPopover == null) {
            mPopBar = new BaseBarImpl(context.getApplicationContext());

            mPopRootView = new RelativeLayout(context.getApplicationContext());
            mPopRootView.removeAllViews();
            mPopRootView.addView(mPopBar.getContentView());

            mPopover = UIPopoverWin.create(context, mPopRootView, true, true);
        }
        return mPopover;
    }

    public static boolean isPopoverShowing() {
        if (mPopover != null)
            return mPopover.isShowing();
        return false;
    }

    public static void dismissPopover() {
        if (mPopover != null && mPopover.isShowing()) {
            mPopover.dismiss();
        }
    }

    public static void showPopover(FragmentActivity activity, ViewGroup parent, ArrayList<POPOVER_ITEM> itemList, IBaseItem.OnItemClickListener l, Rect rect, int arrowPos) {
        UIPopoverWin popover = _popover(activity);
        BaseBarImpl baseBar = mPopBar;

        Context context = activity.getApplicationContext();
        baseBar.setBackgroundColor(AppResource.getColor(context, R.color.ux_color_translucent, null));
        baseBar.setItemInterval(AppDisplay.getInstance(context).dp2px(0.5f));
        baseBar.setHeight(AppDisplay.getInstance(context).dp2px(mBlackPopoverHeightDp));
        baseBar.removeAllItems();

        for (int i = 0; i < itemList.size(); i++) {
            POPOVER_ITEM item = itemList.get(i);

            IBaseItem btnItem = null;
            if (item.mIconRes != 0) {
                btnItem = new BaseItemImpl(context);
                btnItem.setImageResource(item.mIconRes);
            } else {
                btnItem = new BaseItemImpl(context, item.mText);
                btnItem.setTextColor(AppResource.getColor(context, R.color.ux_color_white, null));
                btnItem.setTextLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                btnItem.setTextPadding(AppDisplay.getInstance(context).dp2px(10), 0, AppDisplay.getInstance(context).dp2px(10), 0);
            }

            btnItem.getContentView().setTag(item.mTag);
            btnItem.setOnItemClickListener(l);
            if (i == 0) {
                btnItem.setImageTextBackgroundResouce(R.drawable.black_popover_bg_leftbtn);
            } else if (i == itemList.size() - 1) {
                btnItem.setImageTextBackgroundResouce(R.drawable.black_popover_bg_rightbtn);
            } else {
                btnItem.setImageTextBackgroundResouce(R.color.ux_color_black_popover_bg);
            }

            baseBar.addView(btnItem, BaseBar.TB_Position.Position_CENTER);
        }

        baseBar.resetMargin(0, 0);
        Point size = baseBar.measureSize();

        if (!popover.isShowing()) {
            popover.showAtLocation(parent, rect,
                    size.x + 30, AppDisplay.getInstance(context).dp2px(mBlackPopoverHeightDp),
                    arrowPos, AppDisplay.getInstance(context).dp2px(12));
        } else {
            popover.update(parent, rect,
                    size.x + 30, AppDisplay.getInstance(context).dp2px(mBlackPopoverHeightDp), arrowPos);
        }
    }

}
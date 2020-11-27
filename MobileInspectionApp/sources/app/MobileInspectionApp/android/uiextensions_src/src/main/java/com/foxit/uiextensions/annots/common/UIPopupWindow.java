package com.foxit.uiextensions.annots.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.foxit.uiextensions.R;
import com.foxit.uiextensions.controls.dialog.IPopupDialog;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppResource;
import com.foxit.uiextensions.utils.AppUtil;

import androidx.fragment.app.FragmentActivity;

public class UIPopupWindow extends PopupWindow implements IPopupDialog {
    protected ViewGroup mRootView0;
    protected ViewGroup mRootViewWithShadow;
    protected View mContentView;
    protected String mFragName;
    protected boolean mWithMask;
    protected boolean mWithShadow;
    protected int mShadowWidth;
    protected int mCornerWidth;

    int mWidth;
    int mHeight;

    private Context mContext;

    public static UIPopupWindow create(FragmentActivity act, View contentView, String notUsed1, boolean notUsed2, boolean withShadow) {
        RelativeLayout rootView0 = new RelativeLayout(act.getApplicationContext());
        UIPopupWindow popupDlg = new UIPopupWindow(act.getApplicationContext(), rootView0);
        popupDlg.init(act, contentView, notUsed1, notUsed2, withShadow);
        return popupDlg;
    }

    protected UIPopupWindow(Context context, View v) {
        super(v, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, false);
        mRootView0 = (RelativeLayout) v;
        mContext = context;

        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        super.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    UIPopupWindow.this.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    protected void init(FragmentActivity act, View contentView, String fragName, boolean withMask, boolean withShadow) {
        mContentView = contentView;
        mFragName = fragName;
        mWithMask = withMask;
        mWithShadow = withShadow;

        mRootViewWithShadow = new RelativeLayout(mContext.getApplicationContext()) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();
                    if (x < mContentView.getLeft()
                            || x > mContentView.getRight()
                            || y < mContentView.getTop()
                            || y > mContentView.getBottom()) {
                        if (UIPopupWindow.this.isShowing()) {
                            UIPopupWindow.this.dismiss();
                        }
                    }
                }
                return super.onTouchEvent(event);
            }
        };

        if (mWithShadow) {
            mShadowWidth = AppResource.getDimensionPixelSize(mContext, R.dimen.ux_popup_window_shadow_length);
            mCornerWidth = AppDisplay.getInstance(mContext).dp2px(2);
            mRootViewWithShadow.setBackgroundResource(R.drawable.popup_dialog_shadow);
            mRootViewWithShadow.setPadding(mShadowWidth, mShadowWidth + mCornerWidth, mShadowWidth, mShadowWidth + mCornerWidth);
        }

        AppUtil.removeViewFromParent(mContentView);
        mRootViewWithShadow.addView(mContentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mRootView0.addView(mRootViewWithShadow);
    }

    public View getRootView() {
        return mRootView0;
    }

    public int getShadowLength() {
        return mShadowWidth;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
    }

    public void setWidth(int width) {
        mWidth = width;
        super.setWidth(width);
    }

    public void setHeight(int height) {
        mHeight = height;
        super.setHeight(height);
    }

    public void update(int width, int height) {
        mWidth = width;
        mHeight = height;
        super.update(width, height);
    }

    @Override
    public void update(int x, int y, int width, int height) {
        mWidth = width;
        mHeight = height;
        super.update(x, y, width, height);
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        if ((gravity & Gravity.RIGHT) == Gravity.RIGHT && x < 0) {
            mRootView0.setScrollX(x);
            x = 0;
        } else {
            mRootView0.setScrollX(0);
        }
        super.showAtLocation(parent, gravity, x, y);
    }

    public void dismiss() {
        super.dismiss();
    }

    public boolean isShowing() {
        return super.isShowing();
    }

}
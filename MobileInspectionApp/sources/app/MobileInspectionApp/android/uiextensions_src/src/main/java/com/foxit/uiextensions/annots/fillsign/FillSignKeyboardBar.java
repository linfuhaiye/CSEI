package com.foxit.uiextensions.annots.fillsign;


import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxit.uiextensions.R;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppKeyboardUtil;
import com.foxit.uiextensions.utils.AppResource;

import java.util.ArrayList;

public class FillSignKeyboardBar {
    private Context mAppContext;
    private FillSignToolHandler mToolHandler;
    private UIExtensionsManager mUIExtensionsManager;
    private RelativeLayout mFormNavigationLayout;
    private LinearLayout mNavBarLayout;

    private boolean mIsShowing;

    FillSignKeyboardBar(Context context, UIExtensionsManager uiExtensionsManager, FillSignToolHandler toolHandler) {
        mUIExtensionsManager = uiExtensionsManager;
        mAppContext = context.getApplicationContext();
        mToolHandler = toolHandler;
    }

    boolean isInited() {
        return mFormNavigationLayout != null;
    }

    void initViews() {
        if (mFormNavigationLayout == null) {
            mFormNavigationLayout = (RelativeLayout) View.inflate(mAppContext, R.layout.fillsign_string_prompt, null);

            mNavBarLayout = mFormNavigationLayout.findViewById(R.id.fillsian_navigation_layout);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mFormNavigationLayout.setPadding(0, 0, 0, 0);

            mUIExtensionsManager.getRootView().addView(mFormNavigationLayout, lp);
            hide();
        }
    }

    private RelativeLayout getLayout() {
        if (!isInited()) {
            initViews();
        }
        return mFormNavigationLayout;
    }

    public boolean isShow() {
        return mIsShowing;
    }

    void _showView(boolean show) {
        getLayout().setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void show() {
        mIsShowing = true;
        if (havePrompts())
            _showView(true);
    }

    public void hide() {
        mIsShowing = false;
        if (!isInited())
            return;
        getLayout().setVisibility(View.INVISIBLE);
    }

    public Rect getPadding() {
        Rect padding = new Rect();
        padding.left = getLayout().getPaddingLeft();
        padding.top = getLayout().getPaddingTop();
        padding.right = getLayout().getPaddingRight();
        padding.bottom = getLayout().getPaddingBottom();
        return padding;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        getLayout().setPadding(left, top, right, bottom);
    }

    public int getBarHeight() {
        if (!isInited()) {
            initViews();
        }
        return mNavBarLayout.getHeight();
    }

    public void updatePrompt(String content, FillSignProfileInfo prompt) {
        getLayout();

        mNavBarLayout.removeAllViews();

        if (content.length() < 2) {
            _showView(false);
            return;
        }

        ArrayList<String> promptList = prompt.getAllPrompts();
        for (int i = 0; i < promptList.size(); i++) {
            String str = promptList.get(i);
            if (str.startsWith(content)) {
                final TextView tv = new TextView(mAppContext);
                tv.setGravity(Gravity.CENTER);
                tv.setBackgroundResource(R.drawable.shape_fillsign_prompt);
                tv.setTextColor(AppResource.getColor(mAppContext, R.color.ux_color_white, null));
                tv.setTextSize(AppDisplay.getInstance(mAppContext).px2dp(AppResource.getDimension(mAppContext, R.dimen.ux_text_height_button)));
                tv.setPadding(AppDisplay.getInstance(mAppContext).dp2px(14), 0, AppDisplay.getInstance(mAppContext).dp2px(14), 0);
                tv.setText(str);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, AppDisplay.getInstance(mAppContext).dp2px(28));
                lp.leftMargin = AppDisplay.getInstance(mAppContext).dp2px(7);
                //lp.rightMargin = AppDisplay.dp2px(7);

                mNavBarLayout.addView(tv, lp);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mToolHandler.setEditText(tv.getText().toString());
                    }
                });
            }
        }

        if (mIsShowing && havePrompts()) {
            _showView(true);
        } else {
            _showView(false);
        }
    }

    boolean havePrompts() {
        int count = mNavBarLayout.getChildCount();
        return count > 0;
    }
}

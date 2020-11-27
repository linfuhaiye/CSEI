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
package com.foxit.uiextensions.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.foxit.uiextensions.R;

public class AppKeyboardUtil {

    public static int getKeyboardHeight(ViewGroup parent) {
        if (mKbProvider != null && mKbProvider.isShowing()) {
            return mKbProvider.getKeyboardHeight();
        }

        if (parent == null)
            return 0;
        Rect r = new Rect();
        parent.getWindowVisibleDisplayFrame(r);
        int readFrameHeight = parent.getRootView().getHeight();

        int screenHeight = AppDisplay.getInstance(parent.getContext()).getRawScreenHeight();
        int navBarHeight = AppDisplay.getInstance(parent.getContext()).getNavBarHeight();
        int viewHeight = screenHeight - navBarHeight;

        int bottomOffset = 0;
        if (readFrameHeight - viewHeight > 0)
            bottomOffset = navBarHeight;
        return readFrameHeight - (r.bottom - r.top) - bottomOffset;
    }

    public interface IKeyboardListener {
        void onKeyboardOpened(int keyboardHeight);

        void onKeyboardClosed();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeKeyboardListener(final View showKeyboardView) {
        if (showKeyboardView.getTag(R.id.keyboard_util) == null) {
            return;
        }
        if (showKeyboardView.getTag(R.id.keyboard_util) instanceof ViewTreeObserver.OnGlobalLayoutListener) {
            if (Build.VERSION.SDK_INT >= 16) {
                showKeyboardView.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener) showKeyboardView.getTag(R.id.keyboard_util));
                showKeyboardView.setTag(R.id.keyboard_util, null);
            }
        }
    }

    public static void setKeyboardListener(final ViewGroup parent, final View showKeyboardView, final IKeyboardListener listener) {
        final int[] mOldKeyboardHeight = new int[1];
        final int[] mKeyboardHeight = new int[1];

        mOldKeyboardHeight[0] = 0;
        ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                parent.getWindowVisibleDisplayFrame(r);
                int screenHeight = parent.getRootView().getHeight();
                int viewHeight = parent.getHeight();
                int navBarHeight = 0;
                if (screenHeight - viewHeight > 0){
                    navBarHeight = AppDisplay.getInstance(parent.getContext()).getNavBarHeight();
                }

                mKeyboardHeight[0] = screenHeight - (r.bottom - r.top) - navBarHeight;

                if (mOldKeyboardHeight[0] != mKeyboardHeight[0]) {
                    if (mOldKeyboardHeight[0] > (parent.getRootView().getHeight() / 5.0) && mKeyboardHeight[0] == 0) {
                        listener.onKeyboardClosed();
                    }
                    mOldKeyboardHeight[0] = mKeyboardHeight[0];
                    if (mKeyboardHeight[0] > (parent.getRootView().getHeight() / 5.0)) {

                        getMainThreadHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Rect r = new Rect();
                                parent.getWindowVisibleDisplayFrame(r);
                                int screenHeight = parent.getRootView().getHeight();
                                int viewHeight = parent.getHeight();
                                int navBarHeight = 0;
                                if (screenHeight - viewHeight > 0){
                                    navBarHeight = AppDisplay.getInstance(parent.getContext()).getNavBarHeight();
                                }

                                int h = screenHeight - (r.bottom - r.top) - navBarHeight;
                                if (h > (parent.getRootView().getHeight() / 5.0)) {
                                    listener.onKeyboardOpened(h);
                                }
                            }
                        }, 300);
                    }
                }
            }
        };
        if (Build.VERSION.SDK_INT >= 16) {
            showKeyboardView.setTag(R.id.keyboard_util, globalLayoutListener);
        }
        showKeyboardView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    private static Handler			mMainThreadHandler;
    private static Handler getMainThreadHandler() {
        if (mMainThreadHandler == null) {
            mMainThreadHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Runnable runnable = (Runnable) msg.obj;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            };
        }
        return mMainThreadHandler;
    }


    /**
     * Hidden input method
     *
     * @param context context
     * @param view view
     */
    public static void hideInputMethodWindow(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideInputMethodWindow(Context context,Window wnd) {
        if (wnd == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusView = wnd.getCurrentFocus();
        if (focusView != null)
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
    }

    /**
     * Show input method
     *
     * @param context context
     * @param view view
     */
    public static void showInputMethodWindow(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    static KeyboardHeightProvider mKbProvider;
    public static void removeKeyboardListenerForNothingAct(final View showKeyboardView) {
        if (mKbProvider != null) {
            mKbProvider.close();
        }
    }

    public static void addWndKeyboardListenerForNothingAct(Activity activity, final View showKeyboardView, final IKeyboardListener listener) {
        mKbProvider = new KeyboardHeightProvider(activity);
        mKbProvider.setKeyboardHeightObserver(listener);
        mKbProvider.start();
    }

    static class KeyboardHeightProvider extends PopupWindow {
        private Activity mAct;
        private View mPopupWnd;
        private View mParentView;
        private IKeyboardListener mListener;
        private int mOldKeyboardHeight;
        private static int mAdjustHeight;

        public KeyboardHeightProvider(Activity activity) {
            super(activity);

            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

            this.mAct = activity;

            LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            this.mPopupWnd = inflator.inflate(R.layout.keyboard_popupwnd, null, false);
            setContentView(mPopupWnd);

            mParentView = activity.findViewById(android.R.id.content);

            setWidth(0);
            setHeight(WindowManager.LayoutParams.MATCH_PARENT);

            mPopupWnd.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mPopupWnd != null) {
                        handleOnGlobalLayout();
                    }
                }
            });
        }

        private void handleOnGlobalLayout() {
            Point screenSize = new Point();
            mAct.getWindowManager().getDefaultDisplay().getSize(screenSize);

            Rect rect = new Rect();
            mPopupWnd.getWindowVisibleDisplayFrame(rect);

            // REMIND, you may like to change this using the fullscreen size of the phone
            // and also using the status bar and navigation bar heights of the phone to calculate
            // the keyboard height. But this worked fine on a Nexus.
            int keyboardHeight = screenSize.y - rect.height();
            if (keyboardHeight < 0 && mAdjustHeight == 0) {
                mAdjustHeight = -keyboardHeight;
            }

            if (keyboardHeight == mOldKeyboardHeight)
                return;

            mOldKeyboardHeight = keyboardHeight;

            if (mListener != null) {
                if (keyboardHeight == 0) {
                    mListener.onKeyboardClosed();
                } else {
                    mListener.onKeyboardOpened(keyboardHeight);
                }
            }
        }

        public void start() {
            if (!isShowing() && mParentView.getWindowToken() != null) {
                setBackgroundDrawable(new ColorDrawable(0));
                showAtLocation(mParentView, Gravity.NO_GRAVITY, 0, 0);
            }
        }

        public void close() {
            this.mListener = null;
            if (isShowing()) {
                dismiss();
            }
        }

        public void setKeyboardHeightObserver(IKeyboardListener observer) {
            this.mListener = observer;
        }

        public int getKeyboardHeight() {
            if (AppDisplay.getInstance(mAct.getApplicationContext()).isLandscape() && !AppDisplay.getInstance(mAct.getApplicationContext()).isPad()) {
                return mOldKeyboardHeight - 1;
            }
            return mOldKeyboardHeight + mAdjustHeight - 1;
        }
    }
}


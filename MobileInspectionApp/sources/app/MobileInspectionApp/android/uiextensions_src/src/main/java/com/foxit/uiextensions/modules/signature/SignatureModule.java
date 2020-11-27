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
package com.foxit.uiextensions.modules.signature;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.uiextensions.Module;
import com.foxit.uiextensions.R;
import com.foxit.uiextensions.ToolHandler;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.controls.toolbar.BaseBar;
import com.foxit.uiextensions.controls.toolbar.IBaseItem;
import com.foxit.uiextensions.controls.toolbar.ToolbarItemConfig;
import com.foxit.uiextensions.controls.toolbar.impl.BaseBarImpl;
import com.foxit.uiextensions.controls.toolbar.impl.BaseItemImpl;
import com.foxit.uiextensions.pdfreader.ILayoutChangeListener;
import com.foxit.uiextensions.pdfreader.IStateChangeListener;
import com.foxit.uiextensions.pdfreader.config.ReadStateConfig;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppResource;
import com.foxit.uiextensions.utils.AppUtil;

/**
 * There are two kinds of signatures for SDK, one is the handwriting signature, which simulate the user writing on
 * pdf page, the other is the digital signature, which follows the industrial PKCS#7 standards.
 */
public class SignatureModule implements Module {

    private Context mContext;
    private ViewGroup mParent;
    private PDFViewCtrl mPdfViewCtrl;
    private SignatureToolHandler mToolHandler;
    private PDFViewCtrl.UIExtensionsManager mUiExtensionsManager;

    private IBaseItem mReadSignItem;
    private IBaseItem mSignListItem;

    public SignatureModule(Context context, ViewGroup parent, PDFViewCtrl pdfViewCtrl, PDFViewCtrl.UIExtensionsManager uiExtensionsManager) {
        mContext = context;
        mParent = parent;
        mPdfViewCtrl = pdfViewCtrl;
        mUiExtensionsManager = uiExtensionsManager;
    }

    public ToolHandler getToolHandler() {
        return mToolHandler;
    }

    @Override
    public String getName() {
        return Module.MODULE_NAME_PSISIGNATURE;
    }

    @Override
    public boolean loadModule() {
        mToolHandler = new SignatureToolHandler(mContext, mParent, mPdfViewCtrl);
        initThemeViews();
        if (mUiExtensionsManager != null && mUiExtensionsManager instanceof UIExtensionsManager) {
            ((UIExtensionsManager) mUiExtensionsManager).registerToolHandler(mToolHandler);
            ((UIExtensionsManager) mUiExtensionsManager).registerModule(this);
            ((UIExtensionsManager) mUiExtensionsManager).registerLayoutChangeListener(mLayoutChangeListener);
            ((UIExtensionsManager) mUiExtensionsManager).registerStateChangeListener(mStateChangeListener);
            ((UIExtensionsManager) mUiExtensionsManager).registerToolHandlerChangedListener(mToolHandlerChangedListener);
        }
        mPdfViewCtrl.registerDrawEventListener(mDrawEventListener);
        mPdfViewCtrl.registerDocEventListener(mDocEventListener);
        return true;
    }

    @Override
    public boolean unloadModule() {
        if (mUiExtensionsManager != null && mUiExtensionsManager instanceof UIExtensionsManager) {
            ((UIExtensionsManager) mUiExtensionsManager).unregisterToolHandler(mToolHandler);
            ((UIExtensionsManager) mUiExtensionsManager).unregisterLayoutChangeListener(mLayoutChangeListener);
            ((UIExtensionsManager) mUiExtensionsManager).unregisterStateChangeListener(mStateChangeListener);
            ((UIExtensionsManager) mUiExtensionsManager).unregisterToolHandlerChangedListener(mToolHandlerChangedListener);
        }
        mPdfViewCtrl.unregisterDrawEventListener(mDrawEventListener);
        mPdfViewCtrl.unregisterDocEventListener(mDocEventListener);
        return true;
    }

    private ILayoutChangeListener mLayoutChangeListener = new ILayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int newWidth, int newHeight, int oldWidth, int oldHeight) {
            mToolHandler.onLayoutChange(v, newWidth, newHeight, oldWidth, oldHeight);
        }
    };

    private PDFViewCtrl.IDocEventListener mDocEventListener = new PDFViewCtrl.IDocEventListener() {
        @Override
        public void onDocWillOpen() {
        }

        @Override
        public void onDocOpened(PDFDoc document, int errCode) {
            if (mToolHandler.isAddCertSignature()) {
                mToolHandler.gotoSignPage();
            }
        }

        @Override
        public void onDocWillClose(PDFDoc document) {
        }

        @Override
        public void onDocClosed(PDFDoc document, int errCode) {
        }

        @Override
        public void onDocWillSave(PDFDoc document) {
        }

        @Override
        public void onDocSaved(PDFDoc document, int errCode) {
        }
    };

    private PDFViewCtrl.IDrawEventListener mDrawEventListener = new PDFViewCtrl.IDrawEventListener() {


        @Override
        public void onDraw(int pageIndex, Canvas canvas) {
            if (mToolHandler != null)
                mToolHandler.onDrawForControls(canvas);
        }
    };

    private IStateChangeListener mStateChangeListener = new IStateChangeListener() {
        @Override
        public void onStateChanged(int oldState, int newState) {
            if (newState != ReadStateConfig.STATE_SIGNATURE && ((UIExtensionsManager) mUiExtensionsManager).getCurrentToolHandler() == mToolHandler)
                ((UIExtensionsManager) mUiExtensionsManager).setCurrentToolHandler(null);

            if (mReadSignItem != null) {
                if (((UIExtensionsManager) mUiExtensionsManager).getDocumentManager().canAddSignature()) {
                    mReadSignItem.setEnable(true);
                } else {
                    mReadSignItem.setEnable(false);
                }
            }
        }
    };

    private UIExtensionsManager.ToolHandlerChangedListener mToolHandlerChangedListener = new UIExtensionsManager.ToolHandlerChangedListener() {
        @Override
        public void onToolHandlerChanged(ToolHandler oldToolHandler, ToolHandler newToolHandler) {
            if (newToolHandler == mToolHandler && oldToolHandler != mToolHandler) {
                resetTopBar();
                resetBottomBar();
            }
        }
    };

    private void initThemeViews() {
        if (((UIExtensionsManager) mUiExtensionsManager).getConfig().modules.isLoadSignature()) {
            int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.ux_text_height_toolbar);
            int textColorResId = R.color.ux_text_color_body2_dark;
            int interval = mContext.getResources().getDimensionPixelSize(R.dimen.ux_toolbar_button_icon_text_vert_interval);

            mReadSignItem = new BaseItemImpl(mContext);
            mReadSignItem.setImageResource(R.drawable.sg_selector);

            mReadSignItem.setText(AppResource.getString(mContext.getApplicationContext(), R.string.rd_bar_sign));
            mReadSignItem.setRelation(IBaseItem.RELATION_BELOW);
            mReadSignItem.setInterval(interval);
            mReadSignItem.setTextSize(AppDisplay.getInstance(mContext).px2dp(textSize));
            mReadSignItem.setTextColorResource(textColorResId);
            mReadSignItem.setTag(ToolbarItemConfig.ITEM_BOTTOMBAR_SIGN);
            mReadSignItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppUtil.isFastDoubleClick()) return;
                    ((UIExtensionsManager) mUiExtensionsManager).triggerDismissMenuEvent();
                    ((UIExtensionsManager) mUiExtensionsManager).setCurrentToolHandler(mToolHandler);
                    ((UIExtensionsManager) mUiExtensionsManager).changeState(ReadStateConfig.STATE_SIGNATURE);
                    resetTopBar();
                    resetBottomBar();
                }
            });

            ((UIExtensionsManager) mUiExtensionsManager).getMainFrame().getBottomToolbar().addView(mReadSignItem, BaseBar.TB_Position.Position_CENTER);
        }
    }

    public boolean onKeyBack() {
        return mToolHandler.onKeyBack();
    }

    private void resetTopBar() {
        BaseBarImpl topBar = (BaseBarImpl) ((UIExtensionsManager) mUiExtensionsManager).getMainFrame().getCustomTopbar();
        topBar.removeAllItems();
        topBar.setBackgroundColor(mContext.getResources().getColor(R.color.ux_bg_color_toolbar_light));

        IBaseItem closeItem = new BaseItemImpl(mContext);
        closeItem.setImageResource(R.drawable.rd_reflow_back_selector);
        closeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isFastDoubleClick()) return;
                ((UIExtensionsManager) mUiExtensionsManager).setCurrentToolHandler(null);
                ((UIExtensionsManager) mUiExtensionsManager).changeState(ReadStateConfig.STATE_NORMAL);
                ((UIExtensionsManager) mUiExtensionsManager).getPDFViewCtrl().invalidate();
            }
        });
        IBaseItem titleItem = new BaseItemImpl(mContext);
        titleItem.setText(AppResource.getString(mContext.getApplicationContext(), R.string.sg_signer_title));
        titleItem.setTextSize(AppDisplay.getInstance(mContext).px2dp(mContext.getResources().getDimension(R.dimen.ux_text_height_subhead)));
        titleItem.setTextColor(mContext.getResources().getColor(R.color.ux_text_color_title_dark));

        topBar.addView(closeItem, BaseBar.TB_Position.Position_LT);
        topBar.addView(titleItem, BaseBar.TB_Position.Position_CENTER);
    }

    private void resetBottomBar() {
        BaseBarImpl bottomBar = (BaseBarImpl) ((UIExtensionsManager) mUiExtensionsManager).getMainFrame().getCustomBottombar();
        bottomBar.removeAllItems();
        bottomBar.setBackgroundColor(mContext.getResources().getColor(R.color.ux_bg_color_toolbar_light));
        bottomBar.setItemInterval(mContext.getResources().getDimensionPixelSize(com.foxit.uiextensions.R.dimen.rd_bottombar_button_space));

        mSignListItem = new BaseItemImpl(mContext) {
            @Override
            public void onItemLayout(int l, int t, int r, int b) {
                if (AppDisplay.getInstance(mContext).isPad()) {

                    if (mToolHandler.getPropertyBar().isShowing()) {
                        Rect rect = new Rect();
                        mSignListItem.getContentView().getGlobalVisibleRect(rect);
                        mToolHandler.getPropertyBar().update(new RectF(rect));
                    }

                }
            }
        };
        mSignListItem.setImageResource(R.drawable.sg_list_selector);
        mSignListItem.setText(AppResource.getString(mContext.getApplicationContext(), R.string.rv_sign_model));
        mSignListItem.setRelation(IBaseItem.RELATION_BELOW);
        mSignListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rect rect = new Rect();
                mSignListItem.getContentView().getGlobalVisibleRect(rect);
                mToolHandler.showSignList(new RectF(rect));
            }
        });
        bottomBar.addView(mSignListItem, BaseBar.TB_Position.Position_CENTER);
    }

}

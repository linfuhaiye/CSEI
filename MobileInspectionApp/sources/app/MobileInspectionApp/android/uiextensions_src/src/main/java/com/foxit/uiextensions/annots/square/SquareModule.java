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
package com.foxit.uiextensions.annots.square;

import android.content.Context;
import android.graphics.Canvas;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.uiextensions.Module;
import com.foxit.uiextensions.ToolHandler;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.AnnotHandler;
import com.foxit.uiextensions.config.Config;
import com.foxit.uiextensions.controls.propertybar.PropertyBar;
import com.foxit.uiextensions.controls.propertybar.imp.AnnotMenuImpl;
import com.foxit.uiextensions.controls.propertybar.imp.PropertyBarImpl;

public class SquareModule implements Module, PropertyBar.PropertyChangeListener{

    private SquareAnnotHandler mAnnotHandler;
    private SquareToolHandler mToolHandler;

    private Context mContext;
    private PDFViewCtrl mPdfViewCtrl;
    private PDFViewCtrl.UIExtensionsManager mUiExtensionsManager;
    public SquareModule(Context context, PDFViewCtrl pdfViewCtrl, PDFViewCtrl.UIExtensionsManager uiExtensionsManager) {
        mContext = context;
        mPdfViewCtrl = pdfViewCtrl;
        mUiExtensionsManager = uiExtensionsManager;
    }

    @Override
    public void onValueChanged(long property, int value) {
        UIExtensionsManager uiExtensionsManager = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager());
        AnnotHandler currentAnnotHandler = uiExtensionsManager.getCurrentAnnotHandler();
        if (property == PropertyBar.PROPERTY_COLOR || property == PropertyBar.PROPERTY_SELF_COLOR) {
            if (uiExtensionsManager.getCurrentToolHandler() == mToolHandler) {
                mToolHandler.changeCurrentColor(value);
            } else if (currentAnnotHandler == mAnnotHandler) {
                if (uiExtensionsManager.canUpdateAnnotDefaultProperties())
                    mToolHandler.changeCurrentColor(value);
                mAnnotHandler.onColorValueChanged(value);
            }
        } else if (property == PropertyBar.PROPERTY_OPACITY) {
            if (uiExtensionsManager.getCurrentToolHandler() == mToolHandler) {
                mToolHandler.changeCurrentOpacity(value);
            } else if (currentAnnotHandler == mAnnotHandler) {
                if (uiExtensionsManager.canUpdateAnnotDefaultProperties())
                    mToolHandler.changeCurrentOpacity(value);
                mAnnotHandler.onOpacityValueChanged(value);
            }
        }
    }

    @Override
    public void onValueChanged(long property, float value) {
        UIExtensionsManager uiExtensionsManager = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager());
        AnnotHandler currentAnnotHandler = uiExtensionsManager.getCurrentAnnotHandler();
        if (property == PropertyBar.PROPERTY_LINEWIDTH) {
            if (uiExtensionsManager.getCurrentToolHandler() == mToolHandler) {
                mToolHandler.changeCurrentThickness(value);
            } else if (currentAnnotHandler == mAnnotHandler) {
                if (uiExtensionsManager.canUpdateAnnotDefaultProperties())
                    mToolHandler.changeCurrentThickness(value);
                mAnnotHandler.onLineWidthValueChanged(value);
            }
        }
    }

    @Override
    public void onValueChanged(long property, String value) {
    }

    @Override
    public String getName() {
        return Module.MODULE_NAME_SQUARE;
    }

    @Override
    public boolean loadModule() {
        mAnnotHandler = new SquareAnnotHandler(mContext, mPdfViewCtrl);
        mToolHandler = new SquareToolHandler(mContext, mPdfViewCtrl);

        mAnnotHandler.setPropertyChangeListener(this);
        mToolHandler.setPropertyChangeListener(this);
        mAnnotHandler.setAnnotMenu(new AnnotMenuImpl(mContext, mPdfViewCtrl));
        mAnnotHandler.setPropertyBar(new PropertyBarImpl(mContext, mPdfViewCtrl));

        if (mUiExtensionsManager != null && mUiExtensionsManager instanceof UIExtensionsManager) {
            ((UIExtensionsManager) mUiExtensionsManager).registerToolHandler(mToolHandler);
            ((UIExtensionsManager) mUiExtensionsManager).registerAnnotHandler(mAnnotHandler);
            ((UIExtensionsManager) mUiExtensionsManager).registerModule(this);

            Config config = ((UIExtensionsManager) mUiExtensionsManager).getConfig();
            mToolHandler.changeCurrentColor(config.uiSettings.annotations.rectangle.color);
            mToolHandler.changeCurrentOpacity((int) (config.uiSettings.annotations.rectangle.opacity * 100));
            mToolHandler.changeCurrentThickness(config.uiSettings.annotations.rectangle.thickness);
        }
        mPdfViewCtrl.registerRecoveryEventListener(memoryEventListener);
        mPdfViewCtrl.registerDrawEventListener(mDrawEventListener);
        return true;
    }

    @Override
    public boolean unloadModule() {
        mToolHandler.removePropertyListener();
        mAnnotHandler.removePropertyListener();

        if (mUiExtensionsManager != null && mUiExtensionsManager instanceof UIExtensionsManager) {
            ((UIExtensionsManager) mUiExtensionsManager).unregisterToolHandler(mToolHandler);
            ((UIExtensionsManager) mUiExtensionsManager).unregisterAnnotHandler(mAnnotHandler);
        }

        mPdfViewCtrl.unregisterRecoveryEventListener(memoryEventListener);
        mPdfViewCtrl.unregisterDrawEventListener(mDrawEventListener);
        return true;
    }

    public ToolHandler getToolHandler() {
        return mToolHandler;
    }

    public AnnotHandler getAnnotHandler() {
        return mAnnotHandler;
    }

    private PDFViewCtrl.IDrawEventListener mDrawEventListener = new PDFViewCtrl.IDrawEventListener() {

        @Override
        public void onDraw(int pageIndex, Canvas canvas) {
            mAnnotHandler.onDrawForControls(canvas);
        }
    };

    PDFViewCtrl.IRecoveryEventListener memoryEventListener = new PDFViewCtrl.IRecoveryEventListener() {
        @Override
        public void onWillRecover() {
            if (mAnnotHandler.getAnnotMenu() != null && mAnnotHandler.getAnnotMenu().isShowing()) {
                mAnnotHandler.getAnnotMenu().dismiss();
            }

            if (mAnnotHandler.getPropertyBar() != null && mAnnotHandler.getPropertyBar().isShowing()) {
                mAnnotHandler.getPropertyBar().dismiss();
            }
        }

        @Override
        public void onRecovered() {
        }
    };
}

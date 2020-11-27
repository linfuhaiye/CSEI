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
package com.foxit.uiextensions.annots.multimedia.screen.image;


import android.content.Context;
import android.graphics.Canvas;
import android.os.Environment;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.uiextensions.Module;
import com.foxit.uiextensions.ToolHandler;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.annots.AnnotHandler;
import com.foxit.uiextensions.annots.multimedia.screen.multimedia.MultimediaManager;
import com.foxit.uiextensions.config.uisettings.annotations.annots.ImageConfig;
import com.foxit.uiextensions.controls.propertybar.PropertyBar;
import com.foxit.uiextensions.controls.propertybar.imp.AnnotMenuImpl;
import com.foxit.uiextensions.controls.propertybar.imp.PropertyBarImpl;
import com.foxit.uiextensions.utils.AppFileUtil;

import java.io.File;

public class PDFImageModule implements Module, PropertyBar.PropertyChangeListener {

    private Context mContext;
    private PDFViewCtrl mPdfViewCtrl;
    private PDFViewCtrl.UIExtensionsManager mUiExtensionsManager;

    private PDFImageAnnotHandler mAnnotHandler;
    private PDFImageToolHandler mToolHandler;

    public PDFImageModule(Context context, PDFViewCtrl pdfViewCtrl, PDFViewCtrl.UIExtensionsManager uiExtensionsManager) {
        this.mContext = context;
        this.mPdfViewCtrl = pdfViewCtrl;
        this.mUiExtensionsManager = uiExtensionsManager;
    }

    public ToolHandler getToolHandler() {
        return mToolHandler;
    }

    @Override
    public String getName() {
        return MODULE_NAME_IMAGE;
    }

    @Override
    public boolean loadModule() {
        mToolHandler = new PDFImageToolHandler(mContext, mPdfViewCtrl);
        mToolHandler.setPropertyChangeListener(this);

        mAnnotHandler = new PDFImageAnnotHandler(mContext, mPdfViewCtrl);
        mAnnotHandler.setPropertyChangeListener(this);
        mAnnotHandler.setAnnotMenu(new AnnotMenuImpl(mContext, mPdfViewCtrl));
        mAnnotHandler.setPropertyBar(new PropertyBarImpl(mContext, mPdfViewCtrl));

        int opacity = 100;
        int rotation = Constants.e_Rotation0;
        int[] rotations = new int[]{0, 90, 180, 270};
        if (mUiExtensionsManager != null && mUiExtensionsManager instanceof UIExtensionsManager) {
            ((UIExtensionsManager) mUiExtensionsManager).registerToolHandler(mToolHandler);
            ((UIExtensionsManager) mUiExtensionsManager).registerAnnotHandler(mAnnotHandler);
            ((UIExtensionsManager) mUiExtensionsManager).registerModule(this);

            ImageConfig config = ((UIExtensionsManager) mUiExtensionsManager).getConfig()
                    .uiSettings.annotations.image;
            opacity = (int) (config.opacity * 100);
            int temp = config.rotation;
            for (int i = 0; i < rotations.length; i++){
                if (rotations[i] == temp){
                    rotation = i;
                }
            }
        }

        mToolHandler.getImageInfo().setRotation(rotation);
        mToolHandler.getImageInfo().setOpacity(opacity);
        mPdfViewCtrl.registerRecoveryEventListener(memoryEventListener);
        mPdfViewCtrl.registerDrawEventListener(mDrawEventListener);
        mPdfViewCtrl.registerDocEventListener(mDocEventListener);
        return true;
    }

    @Override
    public boolean unloadModule() {
        mAnnotHandler.removePropertyBarListener();
        mToolHandler.removePropertyBarListener();

        if (mUiExtensionsManager != null && mUiExtensionsManager instanceof UIExtensionsManager) {
            ((UIExtensionsManager) mUiExtensionsManager).unregisterToolHandler(mToolHandler);
            ((UIExtensionsManager) mUiExtensionsManager).unregisterAnnotHandler(mAnnotHandler);
        }
        mPdfViewCtrl.unregisterRecoveryEventListener(memoryEventListener);
        mPdfViewCtrl.unregisterDrawEventListener(mDrawEventListener);
        mPdfViewCtrl.unregisterDocEventListener(mDocEventListener);

        // delete temp files
        String tempPath = Environment.getExternalStorageDirectory() + "/FoxitSDK/AttaTmp/";
        File tempFile = new File(tempPath);
        if (tempFile.exists()) {
            AppFileUtil.deleteFolder(tempFile, false);
        }
        return true;
    }

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

    @Override
    public void onValueChanged(long property, int value) {
        UIExtensionsManager uiExtensionsManager = ((UIExtensionsManager) mPdfViewCtrl.getUIExtensionsManager());
        AnnotHandler currentAnnotHandler = uiExtensionsManager.getCurrentAnnotHandler();
        if (property == PropertyBar.PROPERTY_OPACITY) {
            if (uiExtensionsManager.getCurrentToolHandler() == mToolHandler) {
                mToolHandler.getImageInfo().setOpacity(value);
            } else if (currentAnnotHandler == mAnnotHandler) {
                if (uiExtensionsManager.canUpdateAnnotDefaultProperties())
                    mToolHandler.getImageInfo().setOpacity(value);
                mAnnotHandler.onOpacityValueChanged(value);
            }
        } else if (property == PropertyBar.PROPERTY_ROTATION) {
            if (uiExtensionsManager.getCurrentToolHandler() == mToolHandler) {
                mToolHandler.getImageInfo().setRotation(value == 0 ? value : 4 - value);
            } else if (currentAnnotHandler == mAnnotHandler) {
                if (uiExtensionsManager.canUpdateAnnotDefaultProperties())
                    mToolHandler.getImageInfo().setRotation(value == 0 ? value : 4 - value);
                mAnnotHandler.onRotationValueChanged(value == 0 ? value : 4 - value);
            }
        }
    }

    @Override
    public void onValueChanged(long property, float value) {
    }

    @Override
    public void onValueChanged(long property, String value) {
    }

    private PDFViewCtrl.IDrawEventListener mDrawEventListener = new PDFViewCtrl.IDrawEventListener() {

        @Override
        public void onDraw(int pageIndex, Canvas canvas) {
            mAnnotHandler.onDrawForControls(canvas);
        }
    };

    private PDFViewCtrl.IDocEventListener mDocEventListener = new PDFViewCtrl.IDocEventListener() {
        @Override
        public void onDocWillOpen() {
        }

        @Override
        public void onDocOpened(PDFDoc document, int errCode) {
        }

        @Override
        public void onDocWillClose(PDFDoc document) {
        }

        @Override
        public void onDocClosed(PDFDoc document, int errCode) {
            MultimediaManager.getInstance().clear();
        }

        @Override
        public void onDocWillSave(PDFDoc document) {
        }

        @Override
        public void onDocSaved(PDFDoc document, int errCode) {
        }
    };

}

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
package com.foxit.uiextensions.modules.thumbnail;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.Task;
import com.foxit.sdk.addon.xfa.XFAPage;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.common.Progressive;
import com.foxit.sdk.common.Renderer;
import com.foxit.sdk.common.fxcrt.Matrix2D;
import com.foxit.sdk.pdf.PDFPage;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.utils.AppDisplay;

public class DrawThumbnailTask extends Task {
    private final Rect mBmpArea;
    private final Point mViewSize;
    private Bitmap mBmp;
    private final PDFPage mPDFPage;
    private DrawThumbnailCallback mCallback;
    private ThumbnailItem mThumbnailItem;
    private int mPageIndex;

    public DrawThumbnailTask(final ThumbnailItem item, final DrawThumbnailCallback callback) {
        super(new CallBack() {
            @Override
            public void result(Task task) {
                item.resetRending(false);
                DrawThumbnailTask task1 = (DrawThumbnailTask) task;
                if (task1.mStatus == STATUS_FINISHED) {
                    if (task1.mCallback != null) {
                        task1.mCallback.result(item, task1, ((DrawThumbnailTask) task).mBmp);
                    }
                }
            }

        });
        mCallback = callback;
        mPDFPage = item.getPage();
        mPageIndex = item.getIndex();
        mViewSize = item.getSize();
        mBmpArea = new Rect(0, 0, mViewSize.x, mViewSize.y);

        mPriority = PRIORITY_PATCH;
        mThumbnailItem = item;
        mThumbnailItem.resetRending(true);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    protected void prepare() {
        if (mBmp == null) {
            if (mBmpArea.width() == 0 || mBmpArea.height() == 0) {
                mStatus = STATUS_ERROR;
                return;
            }
            mBmp = Bitmap.createBitmap(mBmpArea.width(), mBmpArea.height(),
                    Bitmap.Config.RGB_565);
        }
    }

    @Override
    protected void execute() {
        if (mStatus != STATUS_REDAY)
            return;
        mStatus = STATUS_RUNNING;

        if (mBmpArea.width() == 0 || mBmpArea.height() == 0) {
            mStatus = STATUS_ERROR;
            return;
        }

        PDFViewCtrl pdfViewCtrl = mThumbnailItem.getPDFView();
        if (pdfViewCtrl.isDynamicXFA()) {
            renderXFAPage(mPageIndex);
        } else {
            renderPage();
        }
    }

    private void renderPage() {
        try {
            if (mPDFPage.isEmpty()) {
                mErr = Constants.e_ErrHandle;
                mStatus = STATUS_ERROR;
                return;
            }
            PDFViewCtrl pdfViewCtrl = mThumbnailItem.getPDFView();
            if (!mPDFPage.isParsed()) {
                Progressive progressive = mPDFPage.startParse(PDFPage.e_ParsePageNormal, null, false);
                int state = Progressive.e_ToBeContinued;
                while (state == Progressive.e_ToBeContinued) {
                    state = progressive.resume();
                }
            }

            Matrix2D matrix = mPDFPage.getDisplayMatrix(0, 0, mViewSize.x, mViewSize.y, pdfViewCtrl.getViewRotation());
            mBmp.eraseColor(Color.WHITE);
            Renderer render = new Renderer(mBmp, true);
            render.setColorMode(Renderer.e_ColorModeNormal);
            render.setRenderAnnotsForThumbnail(true);
            render.setRenderContentFlags(Renderer.e_RenderPage | Renderer.e_RenderAnnot);
            Progressive progressive = render.startRender(mPDFPage, matrix, null);
            int state = Progressive.e_ToBeContinued;
            while (state == Progressive.e_ToBeContinued) {
                state = progressive.resume();
            }

            pdfViewCtrl.renderRmsWatermark(mPDFPage, render, matrix);
            mErr = Constants.e_ErrSuccess;
            mStatus = STATUS_FINISHED;
        } catch (PDFException e) {
            mErr = e.getLastError();
            mStatus = STATUS_ERROR;
        }
    }

    public ThumbnailItem getThumbnailItem() {
        return mThumbnailItem;
    }

    private void renderXFAPage(int pageIndex) {
        if (mBmp == null) {
            mErr = Constants.e_ErrUnknown;
            mStatus = STATUS_ERROR;
            return;
        }
        try {
            PDFViewCtrl pdfViewCtrl = mThumbnailItem.getPDFView();
            if (pdfViewCtrl.getXFADoc() == null) {
                throw new PDFException(com.foxit.sdk.common.Constants.e_ErrOutOfMemory);
            }

            XFAPage xfaPage = pdfViewCtrl.getXFADoc().getPage(pageIndex);
            if (xfaPage.isEmpty()) {
                mStatus = STATUS_ERROR;
                mErr = Constants.e_ErrHandle;
            }
            Matrix2D matrix = xfaPage.getDisplayMatrix(-mBmpArea.left, -mBmpArea.top, mViewSize.x, mViewSize.y, pdfViewCtrl.getViewRotation());
            mBmp.eraseColor(Color.WHITE);


            Renderer render = new Renderer(mBmp, true);
            render.setColorMode(Renderer.e_ColorModeNormal);

            Progressive progressive = render.startRenderXFAPage(xfaPage, matrix, true, null);
            int state = Progressive.e_ToBeContinued;
            while (state == Progressive.e_ToBeContinued) {
                state = progressive.resume();
            }

            mErr = com.foxit.sdk.common.Constants.e_ErrSuccess;
            mStatus = STATUS_FINISHED;
        } catch (PDFException e) {
            mErr = e.getLastError();
            mStatus = STATUS_ERROR;
        }
    }
}


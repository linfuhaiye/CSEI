package com.foxit.uiextensions.annots.fillsign;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.uiextensions.pdfreader.config.AppBuildConfig;

import java.util.ArrayList;

public class FillSignPathDraw {
    static PointF mDefSize = new PointF(16, 16);
    static Paint mPaint = new Paint();

    public static void drawCheck(Canvas canvas, RectF pvBox, int color) {
        //// Check
        //0 G 1 0 0 1 1.53 7.67 cm
        //1.53 w 1 J
        //0 0 m
        //3.58 -6.13 l
        //12.27 6.13 l
        //S

        canvas.save();

        Matrix matrix = new Matrix();
        matrix.postTranslate(1.53f, 7.67f);
        matrix.postScale(1, -1);
        matrix.postTranslate(0, mDefSize.y);

        float scalex = pvBox.width() / mDefSize.x;
        float scaley = pvBox.height() / mDefSize.y;
        matrix.postScale(scalex, scaley);
        matrix.postTranslate(pvBox.left, pvBox.top);

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(3.58f, -6.13f);
        path.lineTo(12.27f, 6.13f);

        path.transform(matrix);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1.53f * Math.min(scalex, scaley));
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(color);

        canvas.drawPath(path, mPaint);

        canvas.restore();
    }

    public static void drawX(Canvas canvas, RectF pvBox, int color) {
        // X
        // 0 G 1 0 0 1 1.5 1.5 cm
        //1.5 w 1 J
        //0 0 m
        //12.3 12.3 l
        //12.3 0 m
        //0 12.3 l
        //S

        canvas.save();

        Matrix matrix = new Matrix();
        matrix.postTranslate(1.5f, 1.5f);
        matrix.postScale(1, -1);
        matrix.postTranslate(0, mDefSize.y);

        float scalex = pvBox.width() / mDefSize.x;
        float scaley = pvBox.height() / mDefSize.y;
        matrix.postScale(scalex, scaley);
        matrix.postTranslate(pvBox.left, pvBox.top);

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(12.3f, 12.3f);
        path.moveTo(12.3f, 0);
        path.lineTo(0, 12.3f);

        path.transform(matrix);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1.5f * Math.min(scalex, scaley));
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(color);

        canvas.drawPath(path, mPaint);

        canvas.restore();
    }

    public static void drawDot(Canvas canvas, RectF pvBox, int color) {
        // Dot
        //0 g 0 G 1 0 0 1 12.3 7.69 cm
        //0 0 m
        //0 2.55 -2.07 4.61 -4.61 4.61 c
        //-7.16 4.61 -9.22 2.55 -9.22 0 c
        //-9.22 -2.55 -7.16 -4.61 -4.61 -4.61 c
        //-2.07 -4.61 0 -2.55 0 0 c
        //h
        //f

        canvas.save();

        Matrix matrix = new Matrix();
        matrix.postTranslate(12.3f, 7.69f);
        matrix.postScale(1, -1);
        matrix.postTranslate(0, mDefSize.y);

        float scale = pvBox.width() / mDefSize.x;
        matrix.postScale(scale, scale);
        matrix.postTranslate(pvBox.left, pvBox.top);

        Path path = new Path();
        path.moveTo(0, 0);
        path.cubicTo(0f, 2.55f, -2.07f, 4.61f, -4.61f, 4.61f);
        path.cubicTo(-7.16f, 4.61f, -9.22f, 2.55f, -9.22f, 0);
        path.cubicTo(-9.22f, -2.55f, -7.16f, -4.61f, -4.61f, -4.61f);
        path.cubicTo(-2.07f, -4.61f, 0f, -2.55f, 0f, 0);
        path.close();

        path.transform(matrix);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);

        canvas.drawPath(path, mPaint);

        canvas.restore();
    }

    public static void drawLine(PDFViewCtrl pdfViewCtrl, int pageIndex, Canvas canvas, RectF pvBox, int color) {
        // Line
        //16 0 obj
        //<< /Matrix [1 0 0 1 0 0] /Length 61 /Filter /FlateDecode /BBox [0 0 100 100]
        // /Type /XObject /Subtype /Form /FormType 1
        // /_FillSign << /Type /FillSignData /Subtype /line >> >>stream
        //0 TL
        //q
        //q
        //0 G 1 0 0 1 0 0 cm
        //1.54 w 1 J
        //0 50.000000 m
        //100.000000 50.000000 l
        //S
        //Q
        //Q
        //endstream
        //endobj

        float thickness = FillSignUtils.docToPageViewThickness(pdfViewCtrl, pageIndex, 1.54f);

        canvas.save();

        Path path = new Path();
        path.moveTo(pvBox.left, pvBox.centerY());
        path.lineTo(pvBox.right, pvBox.centerY());

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(thickness);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(color);

        canvas.drawPath(path, mPaint);

        canvas.restore();
    }

    public static void drawRect(PDFViewCtrl pdfViewCtrl, int pageIndex, Canvas canvas, RectF pvBox, int color) {
        // Rect
        //<< /Matrix [1 0 0 1 0 0] /Length 112 /Filter /FlateDecode /BBox [0 0 100 100]
        // /Type /XObject /Subtype /Form /FormType 1
        // /_FillSign << /Type /FillSignData /Subtype /roundrect >> >>stream
        //0 TL
        //q
        //q
        //0 G 1 0 0 1 1 25.5 cm
        //1.33 w 1 J
        //0 0 m
        //0 -24.5 0 -24.5 24.5 -24.5 c
        //49 -24.5 l
        //98 -24.5 98 -24.5 98 0 c
        //98 49 l
        //98 73.5 98 73.5 49 73.5 c
        //24.5 73.5 l
        //0 73.5 0 73.5 0 49 c
        //0 0 l
        //S
        //Q
        //Q
        //endstream
        //endobj

        float thickness = FillSignUtils.docToPageViewThickness(pdfViewCtrl, pageIndex, 1.54f);
        PointF defSize = new PointF(100, 100);

        canvas.save();

        Matrix matrix = new Matrix();
        matrix.postTranslate(1, 25.5f);
        matrix.postScale(1, -1);
        matrix.postTranslate(0, defSize.y);

        float scalex = pvBox.width() / defSize.x;
        float scaley = pvBox.height() / defSize.y;
        matrix.postScale(scalex, scaley);
        matrix.postTranslate(pvBox.left, pvBox.top);

        Path path = new Path();
        path.moveTo(0, 0);
        path.cubicTo(0, -24.5f, 0, -24.5f, 24.5f, -24.5f);
        path.lineTo(49, -24.5f);
        path.cubicTo(98, -24.5f, 98f, -24.5f, 98, 0);
        path.lineTo(98, 49);
        path.cubicTo(98, 73.5f, 98, 73.5f, 49, 73.5f);
        path.lineTo(24.5f, 73.5f);
        path.cubicTo(0, 73.5f, 0, 73.5f, 0, 49);
        path.lineTo(0, 0);

        path.transform(matrix);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(thickness);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(color);

        canvas.drawPath(path, mPaint);

        canvas.restore();
    }

    public static void drawText(PDFViewCtrl pdfViewCtrl, int pageIndex, Canvas canvas, RectF pvBox, int color, FormObject formObj, float padding, float lineHeight) {
        canvas.save();
        float fontSize = FillSignUtils.docToPageViewThickness(pdfViewCtrl, pageIndex, formObj.mFontSize);
        float x = pvBox.left + padding;
//        float y = pvBox.top + lineHeight - (lineHeight - fontSize) / 2;
        float y = pvBox.top + fontSize;

        ArrayList<String> textArray = FillSignUtils.jniToJavaTextLines(formObj.mContent);

        mPaint.setTypeface(Typeface.MONOSPACE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(0);
        mPaint.setColor(color);
        mPaint.setTextSize(fontSize); // pixel
        if (AppBuildConfig.SDK_VERSION >= 21) {
            mPaint.setLetterSpacing(formObj.mCharspace / formObj.mFontSize);
        }

        for (int i = 0; i < textArray.size(); i++) {
            canvas.drawText(textArray.get(i), x, y + lineHeight * i, mPaint);
        }
        canvas.restore();
    }
}

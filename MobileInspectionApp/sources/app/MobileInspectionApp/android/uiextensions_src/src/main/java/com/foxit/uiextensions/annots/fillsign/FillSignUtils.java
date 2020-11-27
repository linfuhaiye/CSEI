package com.foxit.uiextensions.annots.fillsign;


import android.graphics.RectF;

import com.foxit.sdk.PDFException;
import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.pdf.FillSign;
import com.foxit.sdk.pdf.FillSignObject;
import com.foxit.sdk.pdf.TextFillSignObject;
import com.foxit.sdk.pdf.TextFillSignObjectData;
import com.foxit.sdk.pdf.TextFillSignObjectDataArray;
import com.foxit.sdk.pdf.graphics.TextState;
import com.foxit.uiextensions.utils.AppUtil;
import com.foxit.uiextensions.utils.IResult;

import java.util.ArrayList;

public class FillSignUtils {

    static float docToPageViewThickness(PDFViewCtrl pdfViewCtrl, int pageIndex, float thickness) {
        RectF rectF = new RectF(0, 0, thickness, thickness);
        if (pdfViewCtrl.convertPdfRectToPageViewRect(rectF, rectF, pageIndex)) {
            return rectF.width();
        }
        return thickness;
    }

    static float pageViewToDocThickness(PDFViewCtrl pdfViewCtrl, int pageIndex, float thickness) {
        RectF rectF = new RectF(0, 0, thickness, thickness);
        if (pdfViewCtrl.convertPageViewRectToPdfRect(rectF, rectF, pageIndex)) {
            return rectF.width();
        }
        return thickness;
    }

    static float displayToPageViewThickness(PDFViewCtrl pdfViewCtrl, int pageIndex, float thickness) {
        RectF rectF = new RectF(0, 0, thickness, thickness);
        if (pdfViewCtrl.convertDisplayViewRectToPageViewRect(rectF, rectF, pageIndex)) {
            return rectF.width();
        }
        return thickness;
    }


    static void setCharspace(FillSignObject fillSignObject, float charspace) {
        try {
            if (fillSignObject.isEmpty()) return;

            int type = fillSignObject.getType();
            if (type != FillSign.e_FillSignObjectTypeText) return;
            TextFillSignObject textFillSignObject = new TextFillSignObject(fillSignObject);

            TextFillSignObjectDataArray dataArray = textFillSignObject.getTextDataArray();
            if (dataArray.getSize() > 0) {
                TextFillSignObjectData objectData = dataArray.getAt(0);
                TextState textState = objectData.getText_state();
                textState.setCharspace(charspace);
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
    }

    static void getTextFillSignInfo(FillSignObject fillSignObject, IResult<String, Float, Float> result) {
        try {
            if (fillSignObject == null || fillSignObject.isEmpty()) return;

            int type = fillSignObject.getType();
            if (type != FillSign.e_FillSignObjectTypeText) return;

            TextFillSignObject textFillSignObject = new TextFillSignObject(fillSignObject);
            TextFillSignObjectDataArray dataArray = textFillSignObject.getTextDataArray();

            long size = dataArray.getSize();
            StringBuilder builder = new StringBuilder();
            float fontSize = 0;
            float charSpace = 0;
            for (int i = 0; i < size; i++) {
                TextFillSignObjectData objectData = dataArray.getAt(i);
                builder.append(objectData.getText());

                TextState textState = objectData.getText_state();
                float font_size = textState.getFont_size();
                if (font_size > fontSize)
                    fontSize = font_size;
                float space = textState.getCharspace();
                if (space > charSpace)
                    charSpace = space;
            }

            result.onResult(true, builder.toString(), fontSize, charSpace);
            return;
        } catch (PDFException e) {
            e.printStackTrace();
        }

        result.onResult(false, null, null, null);
    }

    static ArrayList<String> javaToJniTextLines(ArrayList<String> editTextLines) {
        ArrayList<String> textLines = new ArrayList<>();

        for (int i = 0; i < editTextLines.size(); i++) {
            String text = editTextLines.get(i);
            if (i < editTextLines.size() - 1 && (text.length() == 0 || text.charAt(text.length() - 1) != '\n')) {
                text = text + "\n";
            }
            textLines.add(text);
        }

        return textLines;
    }

    static ArrayList<String> jniToJavaTextLines(String jniContent) {
        ArrayList<String> textLines = new ArrayList<>();
        if (AppUtil.isEmpty(jniContent))
            return textLines;

        while (true) {
            int index = jniContent.indexOf("\n");
            if (index < 0) {
                textLines.add(jniContent);
                break;
            }

            if (index == jniContent.length() - 1) {
                String content = jniContent.substring(0, index + 1);
                textLines.add(content);
            } else {
                String content = jniContent.substring(0, index + 1);
                textLines.add(content);
            }

            if (index < jniContent.length() - 1) {
                jniContent = jniContent.substring(index + 1);
            } else {
                break;
            }
        }

        return textLines;
    }
}

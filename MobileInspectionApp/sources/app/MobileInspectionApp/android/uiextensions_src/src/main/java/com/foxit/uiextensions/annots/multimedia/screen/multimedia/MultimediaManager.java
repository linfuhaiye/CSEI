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
package com.foxit.uiextensions.annots.multimedia.screen.multimedia;


import com.foxit.sdk.PDFException;
import com.foxit.sdk.pdf.Rendition;
import com.foxit.sdk.pdf.actions.Action;
import com.foxit.sdk.pdf.actions.RenditionAction;
import com.foxit.sdk.pdf.annots.Annot;
import com.foxit.sdk.pdf.annots.Screen;
import com.foxit.uiextensions.utils.AppAnnotUtil;

import java.util.HashMap;

public class MultimediaManager {
    private static MultimediaManager instance;

    private HashMap<String, String> mTypeMap = new HashMap<>();

    private MultimediaManager() {
    }

    public static MultimediaManager getInstance() {
        if (instance == null)
            instance = new MultimediaManager();
        return instance;
    }

    public String getTypeString(Annot annot) {
        if (annot == null || annot.isEmpty())
            return "Unknown";

        try {
            Screen screen = (Screen) annot;
            String nm = AppAnnotUtil.getAnnotUniqueID(screen);
            if (mTypeMap.get(nm) != null)
                return mTypeMap.get(nm);

            Action action = screen.getAction();
            if (action.isEmpty()) {
                mTypeMap.put(nm, "Image");
                return "Image";
            } else {
                if (Action.e_TypeRendition != action.getType()) {
                    mTypeMap.put(nm, "Image");
                    return "Image";
                }

                // Note: the action have sub action,but at the moment we only judge the first level.
                RenditionAction renditionAction = new RenditionAction(action);
                if (renditionAction.isEmpty() || renditionAction.getRenditionCount() == 0) {
                    mTypeMap.put(nm, "Image");
                    return "Image";
                } else {
                    Rendition rendition = renditionAction.getRendition(0);
                    if (rendition.isEmpty()) {
                        mTypeMap.put(nm, "Image");
                        return "Image";
                    } else {
                        String mimeType = rendition.getMediaClipContentType();
                        if (mimeType == null || mimeType.isEmpty()) {
                            mTypeMap.put(nm, "Image");
                            return "Image";
                        } else if (mimeType.toLowerCase().contains("audio")) {
                            mTypeMap.put(nm, "Audio");
                            return "Audio";
                        } else if (mimeType.toLowerCase().contains("video")
                                || mimeType.contains("application/x-shockwave-flash")
                                || mimeType.contains("application/futuresplash")) {
                            mTypeMap.put(nm, "Video");
                            return "Video";
                        } else {
                            mTypeMap.put(nm, "Image");
                            return "Image";
                        }
                    }
                }
            }
        } catch (PDFException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public void clear() {
        mTypeMap.clear();
    }
}

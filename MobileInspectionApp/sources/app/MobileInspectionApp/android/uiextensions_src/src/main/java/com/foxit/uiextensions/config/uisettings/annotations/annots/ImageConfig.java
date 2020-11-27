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
package com.foxit.uiextensions.config.uisettings.annotations.annots;


public class ImageConfig extends BaseConfig {


    @Override
    public String getTypeString() {
        return KEY_IMAGE;
    }

    @Override
    public AnnotConfigInfo getAnnotConfigInfo() {
        AnnotConfigInfo info = new AnnotConfigInfo();
        info.defaultRotation = DEFAULT_ROTATION;
        info.defaultOpacity = DEFAULT_OPACITY;
        return info;
    }
}

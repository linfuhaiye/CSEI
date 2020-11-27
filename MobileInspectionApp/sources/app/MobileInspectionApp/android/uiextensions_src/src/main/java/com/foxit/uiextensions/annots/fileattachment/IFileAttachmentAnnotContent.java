package com.foxit.uiextensions.annots.fileattachment;

import com.foxit.sdk.common.DateTime;
import com.foxit.uiextensions.annots.AnnotContent;

public interface IFileAttachmentAnnotContent extends AnnotContent {
    /**
     * Icon Name, one of the icon names: "Graph", "Paperclip", "PushPin", "Tag".
     * @return
     */
    String getIconName();
    String getFilePath();
    String getFileName();
}

package com.ghi.modules.pdfviewer.data.entities;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 数字签名
 *
 * @author Alex
 */
@Getter
@Setter
public class DigitalSign {
    /**
     * changelog.ses文件的MD5哈希值
     */
    private String md5;

    /**
     * 检验员签名后保存数字签名信息数据集合
     */
    private List<SignMsg> signedMsg;

    /**
     * 校核签名后保存数字签名信息
     */
    private SignMsg xhmsg;
}

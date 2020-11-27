package com.ghi.modules.pdfviewer.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 签名信息
 *
 * @author Alex
 */
@Getter
@Setter
@AllArgsConstructor
public class SignMsg {
    /**
     * 用户账号
     */
    private String userid;

    /**
     * 用户数字证书
     */
    private String DIG_CERT_SIGN;

    /**
     * 瑞术返回的签名数据
     */
    private String signeddata;

    /**
     *
     */
    private String ifxh;
}

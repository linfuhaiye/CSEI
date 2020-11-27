package com.ghi.modules.pdfviewer.data.entities;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 签名
 *
 * @author Alex
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Signature {
    /**
     * 域名
     */
    @SerializedName("fieldname")
    private String fieldName;

    /**
     * 用户名
     */
    @SerializedName("user")
    private String user;

    /**
     * 签名日期
     */
    @SerializedName("signdate")
    private String signDate;

    /**
     * 校验和校核签名的区分
     */
    @SerializedName("reason")
    private String reason;

    public Signature(final String user, final String signDate, final String reason) {
        this.user = user;
        this.signDate = signDate;
        this.reason = reason;
    }
}

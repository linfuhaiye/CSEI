package com.ghi.modules.pdfviewer.data.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 控制信息
 *
 * @author Alex
 */
@Getter
@Setter
public final class ControlInformation {
    /**
     * 复检
     */
    private String recheck;

    /**
     * 用户
     */
    private List<User> users = new ArrayList<>();

    /**
     * 用户信息
     */
    @Getter
    @Setter
    public static final class User {
        private String username;
        private String oldusername;
        private String uname;
        private String password;
        private String picpwd;
        private String DIG_CERT_SIGN;
    }
}

package com.ghi.modules.pdfviewer.data.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统文件配置
 *
 * @author Alex
 */
@Getter
@Setter
public final class SystemLog {
    /**
     * 首次打开的标志
     */
    private int firstopen = -1;

    /**
     * 是否是复制打开的标记
     */
    private int copyflag;

    /**
     * 校验
     */
    private String jyqz;

    /**
     * 校核
     */
    private String xhqz;

    /**
     * 校验日期
     */
    private String jyqz_date;

    /**
     * 校核日期
     */
    private String xhqz_date;

    /**
     * 校验复检
     */
    private String re_jyqz;

    /**
     * 校核复检
     */
    private String re_xhqz;

    /**
     * 校验复检日期
     */
    private String re_jyqz_date;

    /**
     * 校核复检日期
     */
    private String re_xhqz_date;

    /**
     * 是否下结论
     */
    private String resultflag;

    /**
     * 不合格的标示
     * 0:没有不合格项
     * 1:有不合格项
     */
    private String noitemflag;

    /**
     * 不合格项
     */
    private String noitemcount;

    /**
     * 复制源设备号
     */
    private String copyfrom_eqp_cod;

    /**
     * 复制数据的标识
     */
    private int copydataflag = 0;

    /**
     * 清除签名相关数据
     */
    public void clearSignatures() {
        jyqz = "";
        jyqz_date = "";
        xhqz = "";
        xhqz_date = "";
        re_jyqz = "";
        re_jyqz_date = "";
        re_xhqz = "";
        re_xhqz_date = "";
    }
}

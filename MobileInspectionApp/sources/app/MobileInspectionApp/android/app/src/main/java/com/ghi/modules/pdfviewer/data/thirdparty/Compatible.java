package com.ghi.modules.pdfviewer.data.thirdparty;

import java.util.Calendar;

/**
 * 旧版本兼容代码
 *
 * @author Alex
 */
public class Compatible {
    public static java.sql.Date getFactEffDate(String ISP_DATE, String TASK_DATE, int ISP_CYCLE, String OPE_TYPE) {
        TASK_DATE = TASK_DATE.substring(0, 10);
        java.sql.Date dt = java.sql.Date.valueOf(TASK_DATE.substring(0, 10));
        java.sql.Date dt1 = java.sql.Date.valueOf(ISP_DATE);
        Calendar cal_TASK_DATE = Calendar.getInstance();
        Calendar cal_ISP_DATE = Calendar.getInstance();
        String COMPUTE_ISP_DATE = "";
        cal_TASK_DATE.setTime(dt);
        cal_ISP_DATE.setTime(dt1);
        // 检验日期和任务日期都按月来计算
        cal_TASK_DATE.set(Calendar.DATE, cal_TASK_DATE.getActualMaximum(Calendar.DATE));
        cal_ISP_DATE.set(Calendar.DATE, cal_ISP_DATE.getActualMaximum(Calendar.DATE));
        if (isIncpOpeType(OPE_TYPE)) {
            COMPUTE_ISP_DATE = ISP_DATE;
        } else {
            if (dt1.compareTo(dt) < 0) {
                // 任务日期减一个月，与检验日期比较，还是大于检验日期，则取检验日期，否则取任务日期
                cal_TASK_DATE.add(Calendar.MONTH, -1);
                if (cal_TASK_DATE.compareTo(cal_ISP_DATE) > 0) {
                    COMPUTE_ISP_DATE = ISP_DATE;
                } else {
                    COMPUTE_ISP_DATE = TASK_DATE;
                }
            } else {
                // 检验日期大于等于任务日期。任务日期加上9个月后与检验日期比较，大则取任务日期，小于等于取检验日期。
                cal_TASK_DATE.add(Calendar.MONTH, 9);
                if (cal_TASK_DATE.compareTo(cal_ISP_DATE) >= 0) {
                    COMPUTE_ISP_DATE = TASK_DATE;
                } else {
                    COMPUTE_ISP_DATE = ISP_DATE;
                }
            }
        }

        // 生成下次检验日期
        return dateAddMonth2LastDate(COMPUTE_ISP_DATE, ISP_CYCLE);
    }

    private static java.sql.Date dateAddMonth2LastDate(String S_DATE, int MONTH) {
        java.sql.Date date_COMPUTE_ISP_DATE = java.sql.Date.valueOf(S_DATE);
        Calendar cal_COMPUTE_ISP_DATE = Calendar.getInstance();
        cal_COMPUTE_ISP_DATE.setTime(date_COMPUTE_ISP_DATE);
        cal_COMPUTE_ISP_DATE.set(Calendar.DATE, cal_COMPUTE_ISP_DATE.getActualMaximum(Calendar.DATE));
        cal_COMPUTE_ISP_DATE.add(Calendar.MONTH, MONTH);
        return new java.sql.Date(cal_COMPUTE_ISP_DATE.getTime().getTime());
    }

    private static boolean isIncpOpeType(String OPE_TYPE) {
        boolean isboolean = false;
        if (OPE_TYPE.equals("2") || OPE_TYPE.equals("16") || OPE_TYPE.equals("14") || OPE_TYPE.equals("15") || OPE_TYPE.equals("10") || OPE_TYPE.equals("602") || OPE_TYPE.equals("614") || OPE_TYPE.equals("615") || OPE_TYPE.equals("616")
                || OPE_TYPE.equals("610")) {
            isboolean = true;
        }
        return isboolean;
    }
}

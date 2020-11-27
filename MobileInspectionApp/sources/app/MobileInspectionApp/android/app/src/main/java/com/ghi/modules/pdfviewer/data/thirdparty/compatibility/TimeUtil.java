package com.ghi.modules.pdfviewer.data.thirdparty.compatibility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具
 *
 * @author Alex
 */
public final class TimeUtil {
    public static String getYearAndMonth(String date) {
        String dateReturn = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = sdf.parse(date);
            dateReturn = sdf.format(date1);
        } catch (ParseException e) {
            System.out.println("报错");
            e.printStackTrace();
        }
        return dateReturn;
    }
}

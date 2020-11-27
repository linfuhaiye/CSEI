package com.ghi.modules.pdfviewer.data.thirdparty.compatibility;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DigitalSignatureUtil {
    /**
     * 签名地址
     */
    public static final String SIGN_URL = "http://101.37.96.27:7100/SignAndVerifyService/sign";
//    public static final String SIGN_URL="http://testservice.ruizhengtong.com:8150/demo/sign";
    /**
     * 验签地址
     */
    public static final String VERIFY_URL = "http://101.37.96.27:7100/SignAndVerifyService/verify";
//    public static final String VERIFY_URL="http://testservice.ruizhengtong.com:8150/demo/verify";


    /**
     * 获取瑞术签名所需的参数数据
     * Revision Trail: (Date/Author/Description)
     * 2018年5月28日 Json Lai CREATE
     *
     * @param changelogFilePath
     * @return
     * @author Json Lai
     */
    public static String getSignRequestParams(String appName, String changelogFilePath) {
        try {
            JSONObject messageHead = new JSONObject();//消息头
            messageHead.put("BIZCODE", "BIZ201");//接口业务代码  固定值    签名：BIZ201  验签：BIZ202
            messageHead.put("TRANSID", DigitalSignatureUtil.getTransid());//20位唯一流水,组成方式：”RS”＋8位日期＋10位唯一数，每天从0000000001开始，如RS201803050000000001
            messageHead.put("TIMESTAMP", DigitalSignatureUtil.getTimestamp());//系统时间戳,格式为:YYYYMMDDHHmmssnnn
            messageHead.put("SYSID", "221CFACF9FDD49A7B12972914ECFB02F");//签名授权码  固定值  瑞术提供
            messageHead.put("UNIT", "7acea590ac3f11e895aa005056a72395");//固定值  瑞术提供
            //签名参数
            JSONObject messageBody = new JSONObject();//消息体
            messageBody.put("appName", appName);//证书名称
            messageBody.put("msg", MD5Util.md5HashCode(new File(changelogFilePath)));//原文信息
            messageBody.put("iAlgorithm", "2");//加密算法
            messageHead.put("DATA", messageBody.toString());//具体业务数据

            return messageHead.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取瑞术验签所需的参数数据
     * Revision Trail: (Date/Author/Description)
     * 2018年5月28日 Json Lai CREATE
     *
     * @param changelogFilePath
     * @return
     * @author Json Lai
     */
    public static String getCheckSignRequestParams(String signedMsg, String changelogFilePath) {
        try {
            JSONObject messageHead = new JSONObject();//消息头
            messageHead.put("BIZCODE", "BIZ202");//接口业务代码  固定值    签名：BIZ201  验签：BIZ202
            messageHead.put("TRANSID", DigitalSignatureUtil.getTransid());//20位唯一流水,组成方式：”RS”＋8位日期＋10位唯一数，每天从0000000001开始，如RS201803050000000001
            messageHead.put("TIMESTAMP", DigitalSignatureUtil.getTimestamp());//系统时间戳,格式为:YYYYMMDDHHmmssnnn
            messageHead.put("SYSID", "9253664BA1CF4B74A36F25EFA97F1F59");//签名授权码  固定值  瑞术提供
            messageHead.put("UNIT", "7acea590ac3f11e895aa005056a72395");//固定值  瑞术提供
            //签名参数
            JSONObject messageBody = new JSONObject();//消息体
            messageBody.put("msg", MD5Util.md5HashCode(new File(changelogFilePath)));//原文信息
            messageBody.put("signedMsg", signedMsg);//签名数据
            messageHead.put("DATA", messageBody.toString());//具体业务数据

            return messageHead.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTransid() {
        return "FJSEIS" + formatDateTime("yyyyMMdd") + getRandom(6);
    }

    public static String getTimestamp() {
        return formatDateTime("yyyyMMddHHmmssSSSS");
    }

    public static String formatDateTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String getRandom(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}

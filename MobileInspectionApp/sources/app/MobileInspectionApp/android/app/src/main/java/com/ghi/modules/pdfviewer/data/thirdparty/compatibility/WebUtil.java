package com.ghi.modules.pdfviewer.data.thirdparty.compatibility;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.entities.DigitalSign;
import com.ghi.modules.pdfviewer.data.entities.SignMsg;
import com.ghi.modules.pdfviewer.data.processors.ModifyLogProcessor;
import com.ghi.modules.pdfviewer.data.processors.SignMsgProcessor;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 网络请求服务
 *
 * @author Alex
 */
public final class WebUtil {
    private static final String NAME_SPACE = "http://webservice.org";
    private static final String IP = "http://27.151.117.66:9922";
    private static final String WDSL_LINK = "/fjsei/services/ispMgeWebService";

    private static WebUtil instance;
    private String strJson = "";
    private ResultMessageListener mListener;

    public static WebUtil getInstance() {
        if (instance == null) {
            instance = new WebUtil();
        }

        return instance;
    }

    public static void postDigitalSign(final String path, final String reportCode, final String appName, final String userId, final String reason) {
        RequestParams params = new RequestParams(DigitalSignatureUtil.SIGN_URL);
        params.setBodyContent(DigitalSignatureUtil.getSignRequestParams(appName, ModifyLogProcessor.getFilename(path, reportCode)));

        params.setBodyContentType("application/json");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtils.d("onSuccess: " + result);
                String jsonString = result;
                String resultCode = JsonUtil.toString(jsonString, "RESULTCODE");
                if ("0000".equals(resultCode)) {// 签名成功
                    jsonString = JsonUtil.toString(jsonString, "DATA");
                    String signeddata = JsonUtil.toString(jsonString, "signedMsg");
                    String signMsgString = SignMsgProcessor.read(path, reportCode);
                    DigitalSign digitalSign;
                    if (signMsgString != null) {
                        digitalSign = JsonUtils.toObject(signMsgString, DigitalSign.class);
                        if (reason.equals(XmlUtil.FIRST_SIGN)) {// 检验员签字
                            List<SignMsg> signMsgs = digitalSign.getSignedMsg();
                            if (signMsgs == null) {
                                signMsgs = new ArrayList<SignMsg>();
                            }
                            signMsgs.add(new SignMsg(userId, appName, signeddata, "0"));
                            digitalSign.setSignedMsg(signMsgs);
                        } else {
                            // 校核签字
                            digitalSign.setXhmsg(new SignMsg(userId, appName, signeddata, "1"));
                        }
                    } else {
                        digitalSign = new DigitalSign();
                        if (reason.equals(XmlUtil.FIRST_SIGN)) {// 检验员签字
                            List<SignMsg> signMsgs = new ArrayList<>();
                            signMsgs.add(new SignMsg(userId, appName, signeddata, "0"));
                            digitalSign.setSignedMsg(signMsgs);
                        } else {// 校核签字
                            digitalSign.setXhmsg(new SignMsg(userId, appName, signeddata, "1"));
                        }
                    }
                    digitalSign.setMd5(MD5Util.md5HashCode(new File(ModifyLogProcessor.getFilename(path, reportCode))));
                    SignMsgProcessor.write(path, reportCode, JsonUtils.fromObject(digitalSign));
                    ToastUtils.showShort("数字签名成功");
                } else {// 获取证书失败
                    ToastUtils.showShort(JsonUtil.toString(jsonString, "RESULTMSG"));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.showShort("数字签名失败");
                LogUtils.e("onFailure: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void runWebService(String methodName, String strJson, ResultMessageListener mListener) {
        this.strJson = strJson;
        this.mListener = mListener;
        new MyAsyncTask().execute(methodName);
    }

    public boolean runGetWebService(String methodName, String strJson, ResultMessageListener mListener) {
        this.strJson = strJson;
        this.mListener = mListener;
        try {
            String result = new MyAsyncTask().execute(methodName).get();
            if (TextUtils.isEmpty(result)) {
                return false;
            } else {
                ErrorCode code = new Gson().fromJson(result, ErrorCode.class);
                return code.getErrorCode() == 0;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }

        return false;
    }

    private String request(String methodName) {
        try {
            SoapObject request = new SoapObject(NAME_SPACE, methodName);
            request.addProperty("JSON_STR", strJson);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = request;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE ht = new HttpTransportSE(IP + WDSL_LINK);
            ht.call(NAME_SPACE + File.pathSeparator + methodName, envelope);
            String strProperty = null;
            if (envelope.bodyIn instanceof SoapFault) {
                ErrorCode code = new ErrorCode(2, "操作失败!", null);
                strProperty = new Gson().toJson(code);
            } else {
                SoapObject bodyIn = (SoapObject) envelope.bodyIn;
                strProperty = bodyIn.getProperty(0).toString();
            }
            if (strProperty != null) {
                LogUtils.d("result_", strProperty);
            }
            return strProperty;
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface ResultMessageListener {
        void onResponse(int status, String flag, String result);
    }

    /**
     * 异步下载数据
     *
     * @author MyAsyn
     */
    private class MyAsyncTask extends AsyncTask<String, Void, String> {
        private String methodName;

        @Override
        protected String doInBackground(String... params) {
            methodName = params[0];
            return request(methodName);
        }

        @Override
        protected void onPostExecute(String result) {
            if (mListener != null) {
                if (StringUtils.isEmpty(result)) {
                    mListener.onResponse(0, methodName, result);
                } else {
                    // System.out.println("result=="+result);
                    mListener.onResponse(1, methodName, result);
                }
            }
        }
    }

    /**
     * 错误码
     */
    @Getter
    @Setter
    @AllArgsConstructor
    private class ErrorCode {
        @Expose
        private Integer ErrorCode;
        @Expose
        private String ErrorDetail;
        @Expose
        private String ErrorDescriptor;
    }
}

package com.ghi.modules.pdfviewer.data.thirdparty.compatibility;

import android.util.Pair;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.foxit.sdk.PDFException;
import com.ghi.miscs.JsonUtils;
import com.ghi.modules.pdfviewer.data.core.BindingData;
import com.ghi.modules.pdfviewer.data.core.BindingDataMap;
import com.ghi.modules.pdfviewer.data.core.FieldNames;
import com.ghi.modules.pdfviewer.data.entities.Change;
import com.ghi.modules.pdfviewer.data.entities.ChangeResult;
import com.ghi.modules.pdfviewer.data.entities.DataValueItem;
import com.ghi.modules.pdfviewer.data.entities.ResultItem;
import com.ghi.modules.pdfviewer.data.entities.Signature;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML工具
 *
 * @author Alex
 */
public final class XmlUtil {
    private static final String EQUIP_INFOS = "equipinfos";
    private static final String EQUIP_INFO = "equipinfo";
    private static final String MEASURES = "measures";
    private static final String items = "items";
    private static final String NO_PASS_DESCS = "nopassdescs";
    private static final String conclude = "conclude";
    private static final String NAME = "name";
    private static final String RESULT = "result";
    private static final String USER = "user";
    private static final String SIGN_DATE = "signdate";
    private static final String VALUE = "value";
    private static final String DESC = "desc";
    private static final String no = "no";
    private static final String none = "none";
    private static final String code = "code";
    private static final String bhg = "bhg";
    private static final String COD_BHG = "cod_bhg";
    private static final String DES_BHG = "des_bhg";
    private static final String RES_BHG = "res_bhg";
    private static final String DATE_BHG = "date_bhg";
    private static final String RE_INSPECTION_VALUE = "reispdatavalue";
    private static final String RE_INSPECTION_RESULT = "reispresult";
    private static final String measure = "MERSURE";
    public static final String measure_type = "MERSURE_TYPE";
    public static final String measure_cod = "MERSURE_COD";
    public static final String measure_ssta = "MERSURE_SSTA";
    public static final String measure_fsta = "MERSURE_FSTA";
    private static final String measure_id = "MERSURE_ID";
    private static final String RIGHT = "√";
    private static final String WRONG = "×";
    private static final String DEL_OPERATOR = "▽";
    public static final String FIRST_SIGN = "firstSign";
    public static final String SECOND_SIGN = "secondSign";
    private static final String ISP_USERS = "ispusers";
    private static final String CLIENT_TYPE = "clienttype";
    private static final String CONCLUDE_REP = "concludeRep";
    private static final String VER_USERS = "verusers";
    private static final Pattern p = Pattern.compile("[^0-9]");

    public static boolean createRepFile(String path, final BindingDataMap map, Map<String, JsonElement> testlogItem, List<ChangeResult> changeResults, List<Signature> signatures, boolean isCheck, Map<String, String> fieldMap, String IS_LOG_SIGN) {
        try {
            int measureSize = 1;
            File f = new File(path);
            if (!f.exists()) {
                f.createNewFile();
            }

            List<ResultItem> resultItems = JsonUtils.getArray(testlogItem, FieldNames.RESULT_ITEM, new TypeToken<ArrayList<ResultItem>>() {
            }.getType());

            String hasdataR = testlogItem.get("hasdataR").getAsString();
            Map<String, Change> defMap = new HashMap<String, Change>();

            Element root = DocumentHelper.createElement("config");
            Document document = DocumentHelper.createDocument(root);

            // 给根节点添加孩子节点
            Element equInfosEle = root.addElement(EQUIP_INFOS);
            Element itemsEle = root.addElement(items);
            Element measuresEle = root.addElement(MEASURES);
            Element nopassdecsesEle = root.addElement(NO_PASS_DESCS);
            Element concludeEle = root.addElement(conclude);

            // PROJ_ITEM0-n ALT_PROJS
            Element ALT_PROJS = root.addElement("ALT_PROJS");
            String PROJ_ITEM = "PROJ_ITEM";
            int PROJ_ITEM_number = 0;
            StringBuilder ALT_PROJS_str = new StringBuilder();

            BindingData pdfField = map.get(PROJ_ITEM + PROJ_ITEM_number);
            while (pdfField != null) {
                // 工具提示
                if ("1".equals(pdfField.getValue())) {
                    ALT_PROJS_str.append(pdfField.getAlternateName()).append(",");
                }
                PROJ_ITEM_number++;
                pdfField = map.get(PROJ_ITEM + PROJ_ITEM_number);
            }
            if (ALT_PROJS_str != null && ALT_PROJS_str.length() > 0) {
                ALT_PROJS_str = new StringBuilder(ALT_PROJS_str.substring(0, ALT_PROJS_str.length() - 1));
            }
            ALT_PROJS.addText(ALT_PROJS_str.toString());

            final List<BindingData> fields = new ArrayList<>(map.values());
            int count = fields.size();
            int size = changeResults.size();
            StringBuilder BzBuffer = new StringBuilder();
            for (int i = 0; i < count; i++) {
                final BindingData formField = fields.get(i);
                String val = formField.getValue() == null ? "" : formField.getValue().toString();// 有些数据存在前后空格，去掉后，检验监察比对会出现不一致情况
                String fieldName = formField.getName();
                if ("MGE_DEPT_TYPE".equals(fieldName) || "MGE_DEPT_TYPE_NAME".equals(fieldName) || "SECUDEPT_ADDR".equals(fieldName) || "SECUDEPT_NAME".equals(fieldName) || "SECUORSAFE_DEPT_NAME".equals(fieldName)) {
                    continue;
                }

                String regex1 = "^[0-9,.]+$";
                String regex2 = "^R[0-9,.]+$";
                if (fieldName.matches(regex1) || fieldName.matches(regex2)) {
                    Element itemEle = itemsEle.addElement("item");
                    itemEle.addElement(NAME).addText(formField.getName());
                    itemEle.addElement(VALUE).addText(val);
                    for (ResultItem item : resultItems) {
                        addBigItem(map, fieldName, itemEle, item);
                    }
                    checkItemlevel(changeResults, defMap, size, val, fieldName);
                } else {
                    if (formField.getName().contains(measure_ssta)) {
                        measureSize = addMeasure(map, measureSize, measuresEle, formField, val);
                    } else if (formField.getName().matches("^bhg+[0-9]+$")) {
                        addBhg(map, nopassdecsesEle, formField);
                    } else if (formField.getName().matches("^bz+[0-9]+$")) {
                        Element equInfoEle = equInfosEle.addElement(EQUIP_INFO);
                        equInfoEle.addElement(NAME).addText(formField.getName());
                        equInfoEle.addElement(VALUE).addText(val);
                        BzBuffer.append(formField.getValue() == null ? "" : formField.getValue());
                    } else {
                        if (!formField.getName().contains(none) && !formField.getName().contains(measure) && !formField.getName().contains(bhg) && !formField.getName().contains("_GCRES") && !formField.getName().contains("_CLRES")
                                && !StringUtil.isXMwith("D", "", formField.getName()) && !formField.getName().contains("ispuser") && !formField.getName().contains("jyuser") && !StringUtil.isXMwith("E", "", formField.getName())
                                && !StringUtil.isXMwith("DATE_", "", formField.getName()) && !formField.getName().contains("bz") && !StringUtil.isXMwith("DATE_P", "", formField.getName())) {
                            if (formField.getName().equals(RESULT)) {
                                concludeEle.addText(formField.getValue() == null ? "" : formField.getValue().toString());
                                String concludeRepStr = formField.getValue().toString();
                                String concludeRepStr1 = "";
                                if (concludeRepStr == null || concludeRepStr.length() <= 0) {
                                    concludeRepStr1 = "";
                                } else {
                                    for (int j = 0; j < concludeRepStr.length(); j++) {
                                        concludeRepStr1 += concludeRepStr.substring(j, j + 1) + "   ";
                                    }
                                    concludeRepStr1 = concludeRepStr1.substring(0, concludeRepStr1.length() - 3);
                                }
                                root.addElement(CONCLUDE_REP).addText(concludeRepStr1);
                            } else {
                                Element equInfoEle = equInfosEle.addElement(EQUIP_INFO);
                                equInfoEle.addElement(NAME).addText(formField.getName());
                                equInfoEle.addElement(VALUE).addText(val);
                            }
                        } else {
                            List<String> list = new ArrayList<String>();
                            String[] text = new String[0];
                            try {
                                text = testlogItem.get("set_nextvalue").toString().split(",");
                            } catch (NullPointerException e) {
                                LogUtils.e(e);
                            }

                            Collections.addAll(list, text);
                            if (list.contains(formField.getName())) {
                                Element equInfoEle = equInfosEle.addElement(EQUIP_INFO);
                                equInfoEle.addElement(NAME).addText(formField.getName());
                                equInfoEle.addElement(VALUE).addText(val);
                            }
                        }
                    }
                }
            }
            if (BzBuffer.length() == 0) {
                BzBuffer.append("/");
            }
            fieldMap.put("Memo", BzBuffer.toString());
            fieldMap.put("logMemo", BzBuffer.toString());
            // 分支机构字段ID
            // 添加不存在的表单域
            for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
                if ("MGE_DEPT_TYPE".equals(entry.getKey()) || "MGE_DEPT_TYPE_NAME".equals(entry.getKey()) || "SECUDEPT_ADDR".equals(entry.getKey()) || "SECUDEPT_NAME".equals(entry.getKey()) || "SECUORSAFE_DEPT_NAME".equals(entry.getKey())) {
                    continue;
                }
                Element equInfoEle = equInfosEle.addElement(EQUIP_INFO);
                equInfoEle.addElement(NAME).addText(entry.getKey());
                String val = entry.getValue();
                equInfoEle.addElement(VALUE).addText(val);
            }
            // 分支机构保存
            String MGE_DEPT_TYPE_NAME = "";
            final BindingData MGE_DEPT_TYPE_NAMEfield = map.get("MGE_DEPT_TYPE_NAME");
            if (MGE_DEPT_TYPE_NAMEfield == null) {
                MGE_DEPT_TYPE_NAME = fieldMap.get("MGE_DEPT_TYPE_NAME");
            } else {
                MGE_DEPT_TYPE_NAME = MGE_DEPT_TYPE_NAMEfield.getValue().toString();
            }

            addElement(equInfosEle, "MGE_DEPT_TYPE_NAME", MGE_DEPT_TYPE_NAME);
            if ("无内设管理部门".equals(MGE_DEPT_TYPE_NAME)) {
                // addelement方法时候要把前面循环equInfos节点和fieldMap的时候字段剔除
                addElement(equInfosEle, "MGE_DEPT_TYPE", "0");
                addElement(equInfosEle, "SECUDEPT_NAME", "/");
                addElement(equInfosEle, "SECUDEPT_ADDR", "/");
                addElement(equInfosEle, "SECUORSAFE_DEPT_NAME", "/");
            }
            if ("内设管理部门".equals(MGE_DEPT_TYPE_NAME)) {
                addElement(equInfosEle, "MGE_DEPT_TYPE", "1");
                addElement(equInfosEle, "SECUDEPT_NAME", "/");
                addElement(equInfosEle, "SECUDEPT_ADDR", "/");
                addElement(equInfosEle, "SECUORSAFE_DEPT_NAME", "/");
            }
            if ("内设分支机构".equals(MGE_DEPT_TYPE_NAME)) {
                // 分支机构地址按照原值即可
                addElement(equInfosEle, "MGE_DEPT_TYPE", "2");
                String SECUDEPT_NAME = "";
                String SECUDEPT_ADDR = "";
                final BindingData SECUDEPT_NAMEfield = map.get("SECUDEPT_NAME");
                final BindingData SECUDEPT_ADDRfield = map.get("SECUDEPT_ADDR");
                if (SECUDEPT_NAMEfield == null) {
                    final BindingData SECUORSAFE_DEPT_NAMEfield = map.get("SECUORSAFE_DEPT_NAME");
                    if (SECUORSAFE_DEPT_NAMEfield == null) {
                        SECUDEPT_NAME = fieldMap.get("SECUDEPT_NAME");
                    } else {
                        SECUDEPT_NAME = SECUORSAFE_DEPT_NAMEfield.getValue().toString();
                    }
                } else {
                    SECUDEPT_NAME = SECUDEPT_NAMEfield.getValue().toString();
                }
                if (SECUDEPT_ADDRfield != null) {
                    SECUDEPT_ADDR = SECUDEPT_ADDRfield.getValue().toString();
                }
                addElement(equInfosEle, "SECUDEPT_NAME", SECUDEPT_NAME);
                addElement(equInfosEle, "SECUORSAFE_DEPT_NAME", SECUDEPT_NAME);
                addElement(equInfosEle, "SECUDEPT_ADDR", SECUDEPT_ADDR);
            }
            final BindingData effDateField = map.get("EFF_DATE");
            String EFF_DATE = "";
            if (effDateField != null) {
                EFF_DATE = map.get("EFF_DATE").getValue().toString();
            }
            if (EFF_DATE != null && EFF_DATE.length() >= 0 && !"/".equals(EFF_DATE)) {
                Element equInfoEle = equInfosEle.addElement(EQUIP_INFO);
                equInfoEle.addElement(NAME).addText("EFF_DATE_YM");
                equInfoEle.addElement(VALUE).addText(TimeUtil.getYearAndMonth(EFF_DATE));
            }
            // 验签是否成功标识-1为，不需要电子签字的报告 0为验签不成功的 1为验签成功的
            if (!StringUtils.isEmpty(IS_LOG_SIGN)) {
                Element equInfo = equInfosEle.addElement(EQUIP_INFO);
                equInfo.addElement(NAME).addText("IS_LOG_SIGN");
                equInfo.addElement(VALUE).addText(IS_LOG_SIGN);
            }
            // 添加大项
            if (!"1".equals(hasdataR)) {
                for (Map.Entry<String, Change> entry : defMap.entrySet()) {
                    Element itemEle = itemsEle.addElement("item");
                    itemEle.addElement(NAME).addText("R" + entry.getKey());
                    itemEle.addElement(VALUE).addText(entry.getValue().value);
                    itemEle.addElement("rep_val").addText(entry.getValue().value);
                }
            }
            // 添加签名
            StringBuilder buffer = new StringBuilder();
            for (Signature signature : signatures) {
                if (signature.getReason().equals(SECOND_SIGN)) {
                    root.addElement(VER_USERS).addText(signature.getUser());
                } else {
                    if (StringUtils.isEmpty(buffer.toString())) {
                        if (!StringUtils.isEmpty(signature.getUser())) {
                            buffer.append(signature.getUser());
                        }
                    } else {
                        if (!StringUtils.isEmpty(signature.getUser())) {
                            buffer.append(",").append(signature.getUser());
                        }
                    }
                }
            }
            if (!StringUtils.isEmpty(buffer.toString())) {
                String strIspusers = buffer.toString().trim();
                while (strIspusers.endsWith(",")) {
                    strIspusers = strIspusers.substring(0, strIspusers.length() - 1);
                }
                root.addElement(ISP_USERS).addText(strIspusers);
            }
            root.addElement(CLIENT_TYPE).addText("0");
            Element equInfoEle = equInfosEle.addElement(EQUIP_INFO);
            equInfoEle.addElement(NAME).addText("CLIENTTYPE");
            equInfoEle.addElement(VALUE).addText("0");
            // 判断是否是复检
            isCheckSign(signatures, isCheck, root, equInfosEle, map);

            OutputFormat format = new OutputFormat("	", true);
            // 设置编码格式
            format.setEncoding("UTF-8");
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(path), format);
            xmlWriter.write(document);
            xmlWriter.close();
            return true;
        } catch (Exception e) {
            LogUtils.e(e);
        }

        return false;
    }

    /**
     * 添加检测记录大项的节点,对应配置testlog.ses文件的result_item参数
     */
    private static void addBigItem(final BindingDataMap map, final String fieldName, final Element itemEle, final ResultItem item) {
        if (item.getDataItem().equals(fieldName)) {
            switch (item.getIsPercent()) {
                case 0:
                    StringBuffer buffer = new StringBuffer();
                    for (DataValueItem dataValueItem : item.getDataValue()) {
                        BindingData fieldGcres = map.get(dataValueItem.getClres());
                        BindingData fieldClres = map.get(dataValueItem.getGcres());
                        if (fieldGcres == null || fieldClres == null) {
                            break;
                        }

                        String result = fieldClres.getValue() == null ? "" : fieldClres.getValue().toString();
                        if (!StringUtils.isEmpty(result) && !"/".equals(result) && !"／".equals(result)) {
                            if (StringUtils.isEmpty(buffer.toString())) {
                                buffer.append(fieldGcres.getAlternateName()).append(" ").append(result).append(fieldClres.getAlternateName());
                            } else {
                                buffer.append(";").append(fieldGcres.getAlternateName()).append(" ").append(result).append(fieldClres.getAlternateName());
                            }
                        }
                    }
                    if (!StringUtils.isEmpty(buffer)) {
                        itemEle.addElement(RESULT).addText(buffer.toString());
                    }

                    break;
                case 1:
                    StringBuffer buffer2 = new StringBuffer();
                    for (DataValueItem dataValueItem : item.getDataValue()) {
                        BindingData percentField = map.get(dataValueItem.getPercent());
                        if (percentField != null) {
                            String value = percentField.getValue() == null ? "" : percentField.getValue().toString();
                            if (!StringUtils.isEmpty(value.trim())) {
                                if (!StringUtils.isEmpty(buffer2.toString())) {
                                    buffer2.append("\n");
                                }
                                if (!StringUtils.isEmpty(dataValueItem.getTip())) {
                                    buffer2.append(dataValueItem.getTip() + ":" + value + percentField.getAlternateName() == null ? "%" : value + percentField.getAlternateName());
                                } else {
                                    buffer2.append(value + percentField.getAlternateName() == null ? "%" : value + percentField.getAlternateName());
                                }
                            }
                        }
                    }
                    if (!StringUtils.isEmpty(buffer2)) {
                        if (!StringUtils.isEmpty(item.getTip())) {
                            itemEle.addElement(RESULT).addText(item.getTip() + ":" + buffer2.toString());
                        } else {
                            itemEle.addElement(RESULT).addText(buffer2.toString());
                        }
                    }

                    break;
                case 2:
                    StringBuffer buffer3 = new StringBuffer();
                    for (DataValueItem dataValueItem : item.getDataValue()) {
                        BindingData percentField = map.get(dataValueItem.getPercent());
                        if (percentField != null) {
                            String value = percentField.getValue() == null ? "" : percentField.getValue().toString();
                            if (!StringUtils.isEmpty(value.trim())) {
                                if (!StringUtils.isEmpty(buffer3)) {
                                    buffer3.append("\n");
                                }
                                if (!StringUtils.isEmpty(dataValueItem.getTip())) {
                                    String dataValue = dataValueItem.getTip().replace("$1$", value);
                                    buffer3.append(dataValue);
                                }
                            }
                        }
                    }
                    if (!StringUtils.isEmpty(buffer3)) {
                        if (!StringUtils.isEmpty(item.getTip())) {
                            itemEle.addElement(RESULT).addText(item.getTip() + ":" + buffer3.toString());
                        } else {
                            itemEle.addElement(RESULT).addText(buffer3.toString());
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 小项的等级判断
     */
    private static void checkItemlevel(final List<ChangeResult> changeResults, final Map<String, Change> defMap, final int size, final String val, final String fieldName) {
        Pair<String, String> pair = new Pair<>(StringUtil.removeFromEnd(fieldName, '.'), val);
        if (defMap.containsKey(pair.first)) {
            for (int index = 0; index < size; index++) {
                ChangeResult result = changeResults.get(index);
                if (defMap.get(pair.first).level <= result.getLevel()) {
                    break;
                }
                if (result.getLevel() == index) {
                    if (result.getResult().containsKey(pair.second)) {
                        defMap.put(pair.first, new Change(index, pair.second, result.getResult().get(pair.second)));
                        break;
                    }
                }
            }
        } else {
            for (int index = 0; index < size; index++) {
                ChangeResult result = changeResults.get(index);
                if (result.getLevel() == index) {
                    if (result.getResult().containsKey(pair.second)) {
                        defMap.put(pair.first, new Change(index, pair.second, result.getResult().get(pair.second)));
                        break;
                    }
                }
            }
        }
    }

    /**
     * 添加仪器
     */
    private static int addMeasure(final BindingDataMap map, int measureSize, final Element measuresEle, final BindingData formfield, String val) {
        int index = getIndex(formfield.getName());
        BindingData formFieldFSTA = map.get(measure_fsta + index);
        if (formFieldFSTA != null) {
            String nameClose = formFieldFSTA.getValue() == null ? "" : formFieldFSTA.getValue().toString();
            if (!StringUtils.isEmpty(val) && !StringUtils.isEmpty(nameClose)) {
                if (formfield.getValue().equals(RIGHT) && formFieldFSTA.getValue().equals(RIGHT)) {
                    Element measureEle = measuresEle.addElement("measure");
                    measureEle.addElement(no).addText(measureSize + "");
                    BindingData formFieldName = map.get(measure + index);
                    BindingData formFieldCode = map.get(measure_cod + index);
                    BindingData formFieldType = map.get(measure_type + index);
                    if (formFieldName != null && formFieldCode != null && formFieldType != null) {
                        String nameValue = formFieldName.getValue() == null ? "" : formFieldName.getValue().toString();
                        measureEle.addElement(NAME).addText(nameValue);
                        String codeValue = formFieldCode.getValue() == null ? "" : formFieldCode.getValue().toString();
                        measureEle.addElement(code).addText(codeValue);
                        measureSize++;
                    }
                }
            }
        }

        return measureSize;
    }

    private static int getIndex(String name) {
        Matcher matcher = p.matcher(name);
        return Integer.parseInt(matcher.replaceAll("").trim());
    }

    /**
     * 添加不合格记录
     */
    private static void addBhg(final BindingDataMap map, final Element nopassdecsesEle, final BindingData formfield) throws PDFException {
        Matcher matcher = p.matcher(formfield.getName());
        int index = Integer.parseInt(matcher.replaceAll("").trim());
        String no = formfield.getValue() == null ? "" : formfield.getValue().toString();
        if (!StringUtils.isEmpty(no.trim())) {
            Element nopassdescEle = nopassdecsesEle.addElement("nopassdesc");
            nopassdescEle.addElement("no").addText(formfield.getValue().toString());
            setNopassdese(nopassdescEle, NAME, map, COD_BHG, index);
            setNopassdese(nopassdescEle, DESC, map, DES_BHG, index);
            // 评估报告旧的，已换成reispdatavalue，直接置空，保证上传没有问题
            nopassdescEle.addElement("reispvalue").addText("");
            setNopassdese(nopassdescEle, RE_INSPECTION_VALUE, map, RES_BHG, index);
            setNopassdese(nopassdescEle, RE_INSPECTION_RESULT, map, DATE_BHG, index);
        }
    }

    /**
     * 设置不合格描述
     */
    private static void setNopassdese(Element nopassdescEle, String eleName, final BindingDataMap map, String name, int index) throws PDFException {
        final BindingData formField = map.get(name + index);
        if (formField != null) {
            String value = formField.getValue() == null ? "" : formField.getValue().toString();
            nopassdescEle.addElement(eleName).addText(value);
        }
    }

    /**
     * 添加元素
     */
    private static void addElement(Element equInfosElement, String ename, String evalue) {
        final Element equipInfo = equInfosElement.addElement(EQUIP_INFO);
        equipInfo.addElement(NAME).setText(ename);
        equipInfo.addElement(VALUE).addCDATA(evalue);
    }

    private static void isCheckSign(List<Signature> signatures, boolean isCheck, Element root, Element equInfosEle, final BindingDataMap map) throws PDFException {
        Element signsEle;
        Element signsEle2;

        if (!isCheck) {
            signsEle = root.addElement("sign1");
            signsEle2 = root.addElement("sign2");
            root.addElement("sign3");
            root.addElement("sign4");
        } else {
            root.addElement("sign1");
            root.addElement("sign2");
            signsEle = root.addElement("sign3");
            signsEle2 = root.addElement("sign4");
        }

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
        String signDate = format.format(date);
        if (!signatures.isEmpty()) {
            for (Signature signaTure : signatures) {
                if (signaTure.getReason().equals(FIRST_SIGN)) {
                    signsEle.addElement(USER).addText(signaTure.getUser());
                }
            }
            final BindingData formField = map.get("SignDate1");
            if (formField != null) {
                String dataValue = formField.getValue() == null ? "" : formField.getValue().toString();
                if (!StringUtils.isEmpty(VALUE.trim())) {
                    signsEle.addElement(SIGN_DATE).addText(dataValue);
                } else {
                    for (Signature signature : signatures) {
                        if (signature.getReason().equals(FIRST_SIGN)) {
                            if (StringUtils.isEmpty(signature.getSignDate())) {
                                signsEle.addElement(SIGN_DATE).addText(signDate);
                            } else {
                                signsEle.addElement(SIGN_DATE).addText(signature.getSignDate());
                            }
                            break;
                        }
                    }
                }
            } else {
                for (Signature signature : signatures) {
                    if (signature.getReason().equals(FIRST_SIGN)) {
                        if (StringUtils.isEmpty(signature.getSignDate())) {
                            signsEle.addElement(SIGN_DATE).addText(signDate);
                        } else {
                            signsEle.addElement(SIGN_DATE).addText(signature.getSignDate());
                        }
                        break;
                    }
                }
            }
        }

        if (!signatures.isEmpty()) {
            for (Signature signature : signatures) {
                if (signature.getReason().equals(SECOND_SIGN)) {
                    signsEle2.addElement(USER).addText(signature.getUser());
                    final BindingData formField = map.get("SignDate2");
                    if (formField != null) {
                        String dataValue = formField.getValue() == null ? "" : formField.getValue().toString();
                        if (!StringUtils.isEmpty(VALUE.trim())) {
                            signsEle2.addElement(SIGN_DATE).addText(dataValue);
                        } else {
                            if (StringUtils.isEmpty(signature.getSignDate())) {
                                signsEle2.addElement(SIGN_DATE).addText(signDate);
                            } else {
                                signsEle.addElement(SIGN_DATE).addText(signature.getSignDate());
                            }
                        }
                    } else {
                        if (StringUtils.isEmpty(signature.getSignDate())) {
                            signsEle2.addElement(SIGN_DATE).addText(signDate);
                        } else {
                            signsEle2.addElement(SIGN_DATE).addText(signature.getSignDate());
                        }
                    }
                }
            }
        }

        if (isCheck) {
            Element equInfoEle = equInfosEle.addElement(EQUIP_INFO);
            equInfoEle.addElement(NAME).addText("rechecktime");
            final BindingData formField = map.get("date_bhg0");
            String dataValue = "";
            if (formField != null) {
                dataValue = formField.getValue() == null ? "" : formField.getValue().toString();
            }
            // 获取复检日期,去原始记录
            if (!"".equals(dataValue)) {
                for (int i = 1; i < 60; i++) {
                    final BindingData bhgformField = map.get(DATE_BHG + i);
                    if (bhgformField != null) {
                        String dateBhgValue = bhgformField.getValue() == null ? "" : bhgformField.getValue().toString();
                        if (dateBhgValue.compareTo(dataValue) > 0) {
                            dataValue = dateBhgValue;
                        }
                    }
                }
            }
            equInfoEle.addElement(VALUE).addText(dataValue);
        }
    }
}

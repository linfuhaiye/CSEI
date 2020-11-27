package com.foxit.uiextensions.annots.fillsign;


import android.content.Context;

import com.foxit.uiextensions.utils.AppSharedPreferences;
import com.foxit.uiextensions.utils.AppUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FillSignProfileInfo {
    private final String PREF_FILE_NAME = "fill_sign_prompt";

    final String KEY_FULL_NAME = "full_name";
    final String KEY_FIRST_NAME = "first_name";
    final String KEY_MIDDLE_NAME = "middle_name";
    final String KEY_LAST_NAME = "last_name";
    final String KEY_STREET_1 = "street_1";
    final String KEY_STREET_2 = "street_2";
    final String KEY_CITY = "city";
    final String KEY_STATE = "state";
    final String KEY_POSTCODE = "postcode";
    final String KEY_COUNTRY = "country";
    final String KEY_EMAIL = "email";
    final String KEY_TEL = "tel";
    final String KEY_DATE = "date";
    final String KEY_BIRTH_DATE = "birth_date";

    private final String KEY_CUSTOM_FIELDS = "custom_fileds"; // split by '\n'
    private final String KEY_CUSTOM_VALUES = "custom_values"; // split by '\n'
    private String mCustomSplit = "\n";
    private String mCustomSeround = "1234567890";

    private AppSharedPreferences mSp;

    FillSignProfileInfo(Context context) {
        mSp = AppSharedPreferences.getInstance(context);
    }

    String getFullName() {
        return mSp.getString(PREF_FILE_NAME, KEY_FULL_NAME, "");
    }

    void setFullName(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_FULL_NAME, value);
    }

    String getFirstName() {
        return mSp.getString(PREF_FILE_NAME, KEY_FIRST_NAME, "");
    }

    void setFirstName(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_FIRST_NAME, value);
    }

    String getMiddleName() {
        return mSp.getString(PREF_FILE_NAME, KEY_MIDDLE_NAME, "");
    }

    void setMiddleName(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_MIDDLE_NAME, value);
    }

    String getLastName() {
        return mSp.getString(PREF_FILE_NAME, KEY_LAST_NAME, "");
    }

    void setLastName(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_LAST_NAME, value);
    }

    String getStreet1() {
        return mSp.getString(PREF_FILE_NAME, KEY_STREET_1, "");
    }

    void setStreet1(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_STREET_1, value);
    }

    String getStreet2() {
        return mSp.getString(PREF_FILE_NAME, KEY_STREET_2, "");
    }

    void setStreet2(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_STREET_2, value);
    }

    String getCity() {
        return mSp.getString(PREF_FILE_NAME, KEY_CITY, "");
    }

    void setCity(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_CITY, value);
    }

    String getState() {
        return mSp.getString(PREF_FILE_NAME, KEY_STATE, "");
    }

    void setState(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_STATE, value);
    }

    String getPostCode() {
        return mSp.getString(PREF_FILE_NAME, KEY_POSTCODE, "");
    }

    void setPostCode(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_POSTCODE, value);
    }

    String getCountry() {
        return mSp.getString(PREF_FILE_NAME, KEY_COUNTRY, "");
    }

    void setCountry(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_COUNTRY, value);
    }

    String getEmail() {
        return mSp.getString(PREF_FILE_NAME, KEY_EMAIL, "");
    }

    void setEmail(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_EMAIL, value);
    }

    String getTel() {
        return mSp.getString(PREF_FILE_NAME, KEY_TEL, "");
    }

    void setTel(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_TEL, value);
    }

    String getDate() {
        //String val = mSp.getString(PREF_FILE_NAME, KEY_DATE, "");
        return DateFormat.getDateInstance().format(new Date());
    }

    void setDate(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_DATE, value);
    }

    String getBirtyDate() {
        return mSp.getString(PREF_FILE_NAME, KEY_BIRTH_DATE, "");
    }

    void setBirtyDate(String value) {
        mSp.setString(PREF_FILE_NAME, KEY_BIRTH_DATE, value);
    }

    void setString(String key, String value) {
        mSp.setString(PREF_FILE_NAME, key, value);
    }

    private ArrayList<String> _getCustomValues(String key) {
        //saveCustomFields(new ArrayList<String>(), new ArrayList<String>());
        ArrayList<String> fieldList = new ArrayList<>();

        String val = mSp.getString(PREF_FILE_NAME, key, mCustomSeround + mCustomSeround);
        if (val.length() < mCustomSeround.length() * 2)
            return fieldList;

        val = val.substring(mCustomSeround.length(), val.length());
        val = val.substring(0, val.length() - mCustomSeround.length());

        if (val.length() == 0)
            return fieldList;

        while (true) {
            int index = val.indexOf(mCustomSplit);
            if (index < 0)
                break;

            String content = val.substring(0, index);
            fieldList.add(content);

            if (index < val.length() - 1) {
                val = val.substring(index + 1);
            } else {
                break;
            }
        }

        return fieldList;
    }

    private void _saveCustomValues(String key, ArrayList<String> vals) {
        StringBuilder strBuf = new StringBuilder();
        for (int i = 0; i < vals.size(); i++) {
            strBuf.append(vals.get(i) + mCustomSplit);
        }
        String content = strBuf.toString();
        content = mCustomSeround + content + mCustomSeround;
        mSp.setString(PREF_FILE_NAME, key, content);
    }

    ArrayList<String> getCustomFields() {
        return _getCustomValues(KEY_CUSTOM_FIELDS);
    }

    ArrayList<String> getCustomValues() {
        return _getCustomValues(KEY_CUSTOM_VALUES);
    }

    void saveCustomFields(ArrayList<String> fields, ArrayList<String> vals) {
        _saveCustomValues(KEY_CUSTOM_FIELDS, fields);
        _saveCustomValues(KEY_CUSTOM_VALUES, vals);
    }

    ArrayList<String> getAllPrompts() {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> tmpList = new ArrayList<>();

        tmpList.add(getFullName());
        tmpList.add(getFirstName());
        tmpList.add(getMiddleName());
        tmpList.add(getLastName());

        tmpList.add(getStreet1());
        tmpList.add(getStreet2());
        tmpList.add(getCity());
        tmpList.add(getState());
        tmpList.add(getPostCode());
        tmpList.add(getCountry());

        tmpList.add(getEmail());
        tmpList.add(getTel());

        tmpList.add(getDate());
        tmpList.add(getBirtyDate());

        final ArrayList<String> customFields = getCustomValues();
        if (customFields.size() > 0) {
            tmpList.addAll(customFields);
        }

        for (int i = 0; i < tmpList.size(); i++) {
            String content = tmpList.get(i);
            if (!AppUtil.isEmpty(content)) {
                result.add(content);
            }
        }
        return result;
    }

}

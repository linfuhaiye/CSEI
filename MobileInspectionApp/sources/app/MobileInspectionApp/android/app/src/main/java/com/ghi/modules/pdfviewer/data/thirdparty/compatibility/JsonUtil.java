package com.ghi.modules.pdfviewer.data.thirdparty.compatibility;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * JSON工具
 *
 * @author Alex
 */
public class JsonUtil {
    /**
     * TODO 将对象转换为json字符串
     * Revision Trail: (Date/Author/Description)
     * 2016年6月7日 Json Lai CREATE
     *
     * @param obj
     * @return
     * @author Json Lai
     */
    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampAdapter()).create();
        return gson.toJson(obj);
    }

    /**
     * TODO 将json字符串转为指定的对象
     * Revision Trail: (Date/Author/Description)
     * 2016年6月7日 Json Lai CREATE
     *
     * @param json
     * @param cls
     * @return
     * @author Json Lai
     */
    public static <T> T objectFromJson(String json, Class<T> cls) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampAdapter()).create();
        return gson.fromJson(json, cls);
    }

    /**
     * TODO 将json字符串转为指定的ArrayList
     * Revision Trail: (Date/Author/Description)
     * 2016年6月7日 Json Lai CREATE
     *
     * @param json
     * @param cls
     * @return
     * @throws JSONException
     * @author Json Lai
     */
    public static <T> ArrayList<T> listFromJson(String json, Class<T> cls) {
        ArrayList<T> list = new ArrayList<T>();
        try {
            if (TextUtils.isEmpty(json)) {
                return list;
            }
            JSONArray array = new JSONArray(json);
            int len = 0;
            T t;
            if (array != null && (len = array.length()) > 0) {
                for (int i = 0; i < len; i++) {
                    t = JsonUtil.objectFromJson(array.getString(i), cls);
                    if (t != null) {
                        list.add(t);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将json字符串转为指定的集合的hashMap
     * Revision Trail: (Date/Author/Description)
     * 2016年7月27日 Json Lai CREATE
     *
     * @param json
     * @param cls
     * @return
     * @author Json Lai
     */
    public static <T> TreeMap<String, List<T>> jsonToTreeMap(String json, Class<T> cls) {
        Map<String, List<T>> map = new TreeMap<String, List<T>>();
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject jsonMap = new JSONObject(json);
            @SuppressWarnings("unchecked")
            Iterator<String> it = jsonMap.keys();
            while (it.hasNext()) {
                String key = it.next();
                String string = toString(json, key);
                map.put(key, listFromJson(string, cls));
            }
            return (TreeMap<String, List<T>>) map;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json字符串取单个键值对
     * Revision Trail: (Date/Author/Description)
     * 2016年7月25日 Json Lai CREATE
     *
     * @param jsonString,string
     * @return
     * @author Json Lai
     */
    public static String toString(String jsonString, String string) {
        String returnString = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            returnString = jsonObject.getString(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    /**
     * json字符串取单个键值对
     * Revision Trail: (Date/Author/Description)
     * 2016年11月4日 Timer He CREATE
     *
     * @param jsonString
     * @param string
     * @return
     * @author Timer He
     */
    public static boolean toBoolean(String jsonString, String string) {
        boolean returnBoolean = false;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            returnBoolean = jsonObject.getBoolean(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnBoolean;
    }

    /**
     * json字符串取单个键值对
     * Revision Trail: (Date/Author/Description)
     * 2016年7月25日 Json Lai CREATE
     *
     * @param jsonString,string
     * @return
     * @author Json Lai
     */
    public static int toInt(String jsonString, String string) {
        int num = 0;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            num = jsonObject.getInt(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * TODO json转map
     * Revision Trail: (Date/Author/Description)
     * 2016年8月2日 Wade CREATE
     *
     * @param jsonStr
     * @return
     * @author Wade
     */
    public static HashMap<String, String> toHashMap(String jsonStr) {
        HashMap<String, String> data = new HashMap<String, String>();
        // 将json字符串转换成jsonObject
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonStr);
            Iterator it = jsonObject.keys();
            // 遍历jsonObject数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value = (jsonObject.get(key) + "");
                data.put(key, value);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

    /**
     * <P><B>Description: </B> 时间转换器  </P>
     * Revision Trail: (Date/Author/Description)
     * 2016年7月29日 Timer He CREATE
     *
     * @author Timer He
     * @version 1.0
     */
    public static class TimestampAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

        @Override
        public Timestamp deserialize(JsonElement json, Type type, JsonDeserializationContext arg2)
                throws JsonParseException {
            if (json == null) {
                return null;
            } else {
                try {
                    return new Timestamp(json.getAsLong());
                } catch (Exception e) {
                    return null;
                }
            }
        }

        @Override
        public JsonElement serialize(Timestamp src, Type type, JsonSerializationContext arg2) {
            String value = "";
            if (src != null) {
                value = String.valueOf(src.getTime());
            }
            return new JsonPrimitive(value);
        }

    }
}

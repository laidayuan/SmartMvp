package com.dada.marsframework.model;

/**
 * Created by laidayuan on 2018/1/31.
 */


import com.alibaba.fastjson.JSON;
import com.dada.marsframework.utils.LogUtils;
import com.dada.marsframework.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseHelper extends BaseModel {


    /**
     *
     */
    private static final long serialVersionUID = 1173660454853478713L;

    public static String getString(JSONObject rootObject, String key) {
        JSONObject jObj = (JSONObject) getLastObj(rootObject, key);
        if (jObj != null) {
            if (key.contains(".")) {
                String notes[] = key.split("\\.");
                return jObj.optString(notes[notes.length - 1]);
            } else {
                return jObj.optString(key);
            }

        }

        return null;
    }

    public static <T extends BaseModel> T getModel(JSONObject rootObject,
                                                   String key, Class<T> clazz) {
        JSONObject jObj = (JSONObject) getLastObj(rootObject, key);
        if (jObj != null && StringUtils.isNotEmpty(key)) {
            if (key.contains(".")) {
                String notes[] = key.split("\\.");
                JSONObject item = jObj.optJSONObject(notes[notes.length - 1]);
                T itemModel;
                try {
                    itemModel = clazz.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException("create IDelegate error : " + e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("create IDelegate error : " + e);
                }

                mParser.parseJSONObject(item, itemModel);

                return itemModel;
            } else {
                JSONObject item = jObj.optJSONObject(key);
                T itemModel;
                try {
                    itemModel = clazz.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException("create IDelegate error : " + e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("create IDelegate error : " + e);
                }

                mParser.parseJSONObject(item, itemModel);

                return itemModel;
            }

        }

        return null;
    }

    public static boolean getBoolean(JSONObject rootObject, String key) {
        JSONObject jObj = (JSONObject) getLastObj(rootObject, key);
        if (jObj != null) {
            if (key.contains(".")) {
                String notes[] = key.split("\\.");
                return jObj.optBoolean(notes[notes.length - 1]);
            } else {
                return jObj.optBoolean(key);
            }

        }

        return false;
    }

    public static int getInteger(JSONObject rootObject, String key) {
        JSONObject jObj = (JSONObject) getLastObj(rootObject, key);
        if (jObj != null) {
            // LogUtil.e("contains = " + key.contains("."));
            if (key.contains(".")) {

                String notes[] = key.split("\\.");

                if (notes != null && notes.length > 0) {
                    return jObj.optInt(notes[notes.length - 1]);
                } else {
                    return jObj.optInt(key);
                }

            } else {
                // LogUtil.e("jObj = " + jObj + ", key = " + key +
                // ", jObj.optInt(key) = " + jObj.optInt(key));
                return jObj.optInt(key);
            }

        }

        return -1;
    }

    public static double getDouble(JSONObject rootObject, String key) {
        JSONObject jObj = (JSONObject) getLastObj(rootObject, key);
        if (jObj != null) {
            if (key.contains(".")) {

                String notes[] = key.split("\\.");

                if (notes != null && notes.length > 0) {
                    return jObj.optDouble(notes[notes.length - 1]);
                } else {
                    return jObj.optDouble(key);
                }

            } else {
                return jObj.optDouble(key);
            }

        }

        return -1;
    }

    public static long getLong(JSONObject rootObject, String key) {
        JSONObject jObj = (JSONObject) getLastObj(rootObject, key);
        if (jObj != null) {
            if (key.contains(".")) {

                String notes[] = key.split("\\.");

                if (notes != null && notes.length > 0) {
                    return jObj.optLong(notes[notes.length - 1]);
                } else {
                    return jObj.optLong(key);
                }

            } else {
                return jObj.optLong(key);
            }

        }

        return -1;
    }

    public static Object getLastObj(JSONObject rootObject, String key) {

        if (rootObject != null && StringUtils.isNotEmpty(key)) {
            if (key.contains(".")) {
                String notes[] = key.split("\\.");
                if (notes != null) {
                    Object object = rootObject;
                    for (int i = 0; i < notes.length - 1; i++) {
                        JSONObject jObj = (JSONObject) object;
                        Object obj = jObj.opt(notes[i]);
                        if (obj instanceof JSONObject) {
                            object = obj;
                        } else if (obj instanceof JSONArray) {
                            JSONArray list = (JSONArray) obj;
                            if (list != null && list.length() > 0) {
                                object = list.opt(0);
                            }

                        }

                    }

                    return object;
                }

            } else {
                return rootObject;//.opt(key);
            }

        }

        return null;
    }

    public static String getString(String json, String key) {
        if (StringUtils.isEmpty(json)) {

            return null;
        }

        try {
            return getString(new JSONObject(json), key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends BaseModel> T getModel(String json, String key,
                                                   Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {

            return null;
        }

        try {
            return getModel(new JSONObject(json), key, clazz);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static boolean getBoolean(String json, String key) {
        if (StringUtils.isEmpty(json)) {

            return false;
        }

        try {
            return getBoolean(new JSONObject(json), key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public static int getInteger(String json, String key) {
        if (StringUtils.isEmpty(json)) {

            return -1;
        }

        try {
            return getInteger(new JSONObject(json), key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }

    public static double getDouble(String json, String key) {
        if (StringUtils.isEmpty(json)) {

            return -1;
        }

        try {
            return getDouble(new JSONObject(json), key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    public static long getLong(String json, String key) {
        if (StringUtils.isEmpty(json)) {

            return -1;
        }

        try {
            return getLong(new JSONObject(json), key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    public static Object getLastObjEx(JSONObject rootObject, String key) {

        if (rootObject != null && StringUtils.isNotEmpty(key)) {
            // LogUtils.e("rootObject = " + rootObject.toString() + ", key = " +
            // key);
            if (key.contains(".")) {
                String notes[] = key.split("\\.");
                if (notes != null) {
                    Object object = rootObject;
                    try {
                        for (int i = 0; i < notes.length; i++) {

                            JSONObject jObj = (JSONObject) object;
                            if (jObj != null) {
                                object = jObj.opt(notes[i]);
                            }

                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        LogUtils.d(e.toString());
                    }

                    return object;
                }

            } else {
                return rootObject.opt(key);
            }

        } else {
            return rootObject;
        }

        return null;
    }

    public static <T extends BaseModel> List getModelList(String json,
                                                          String key, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {

            return null;
        }

        try {
            return getModelList(new JSONObject(json), key, clazz);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends BaseModel> List getModelList(
            JSONObject rootObject, String key, Class<T> clazz) {
        try {
            JSONArray jArray = (JSONArray) getLastObjEx(rootObject, key);
            if (jArray != null) {
                List list = new ArrayList();

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject item = jArray.optJSONObject(i);

                    T itemModel;
                    try {
                        itemModel = clazz.newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException("create IDelegate error");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("create IDelegate error");
                    }

                    mParser.parseJSONObject(item, itemModel);

                    list.add(itemModel);
                }

                return list;
            }
        } catch (Exception e) {
            LogUtils.d(e.toString());
            e.printStackTrace();
        }

        return null;
    }


    public static  <T extends BaseModel> T parseObject(String json, Class cls) {
        if (cls != null && json != null) {
            return (T) JSON.parseObject(json, cls);
        }

        return null;
    }

    public static <T extends BaseModel> List<T> parseArray(String json, Class cls) {
        if (cls != null && json != null) {
            return JSON.parseArray(json, cls);
        }

        return null;
    }
}

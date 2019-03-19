package com.dada.marsframework.network;


import com.dada.marsframework.base.FrameworkConfig;
import com.dada.marsframework.model.BaseModel;
import com.dada.marsframework.model.ParseHelper;
import com.dada.marsframework.utils.LogUtils;
import com.dada.marsframework.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by laidayuan on 2018/1/31.
 */

public class RequestHelper {
    //upload params
    protected HashMap<String, Object> paramMap;

    //upload files
    protected HashMap<String, Object> fileMap;

    //upload action
    protected String action = "";

    public String loading;
    public String loadingSucc;
    public JSONObject mJsonObject;
    protected int code = -1;
    protected int status = -1;

    protected String message;
    private boolean isPost = true;

    private long startRequstTime;

    private long endRequestTime;

    public RequestHelper() {
        startRequstTime = System.currentTimeMillis();
        LogUtils.d("RequestHelper  startRequstTime = " + startRequstTime);

    }


    public HashMap<String, Object> getParamMap() {
        if (paramMap == null) {
            paramMap = new HashMap<String, Object>();
        }

        return paramMap;
    }

    public HashMap<String, Object> getFileMap() {
        return fileMap;
    }

    public RequestHelper setMethod(boolean isPost) {
        this.isPost = isPost;

        return this;
    }

    public boolean getMethod() {
        return isPost;
    }

    public boolean isOk() {
        return true;
    }

    public int getCode() {
        if (mJsonObject == null) {
            return -1;
        }

        return code;
    }

    public int getStatus() {
        if (mJsonObject == null) {
            return -1;
        }

        return mJsonObject.optInt("status");
    }

    public String getMessage() {
        if (mJsonObject == null) {
            return "";
        }

        return mJsonObject.optString("msg");
    }

    public boolean isKickOut() {

        return false;
    }

    public String getUrl() {
        String url = FrameworkConfig.HostUrl;
        if (!getMethod() && paramMap != null && paramMap.size() > 0) {
            StringBuilder sb = new StringBuilder(url);
            sb.append("?");
            Iterator iter = paramMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();

                sb.append(entry.getKey());
                sb.append("=");
                try {
                    sb.append(URLEncoder.encode(entry.getValue().toString(), "GBK"));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                sb.append("&");
            }

            sb = sb.deleteCharAt(sb.length() - 1);
            LogUtils.d("url = " + sb.toString());

            return sb.toString();
        }

        return url;
    }


	public static RequestHelper create(String action,
			HashMap<String, Object> paramMap) {
		RequestHelper helper = new RequestHelper();
		helper.action = action;
		helper.paramMap = paramMap;
		if (paramMap == null) {
			helper.paramMap = new HashMap<String, Object>();
		}

		return helper;
	}

    public static RequestHelper create(String cls, String action,
                                       HashMap<String, Object> paramMap) {
        try {
            Class<?> instance = Class.forName(cls);
            create(instance, action, paramMap);
        } catch (Exception e)  {

        }

        return null;
    }

    public static RequestHelper create(Class cls, String action,
                                       HashMap<String, Object> paramMap) {
        try {
            RequestHelper helper = (RequestHelper)cls.newInstance();
            helper.action = action;
            helper.paramMap = paramMap;
            if (paramMap == null) {
                helper.paramMap = new HashMap<String, Object>();
            }

            return helper;
        } catch (Exception e)  {

        }

        return null;
    }


    public RequestHelper setAction(String action) {
        this.action = action;
        return this;
    }

    public String getAction() {

        return action;
    }

    public boolean equalsAction(String action) {

        return (!StringUtils.isEmpty(action) && !StringUtils.isEmpty(this.action) && action.equalsIgnoreCase(this.action));
    }

    public RequestHelper addFile(String key, Object value) {
        if (key == null || value == null) {
            return this;
        }

        if (fileMap == null) {
            fileMap = new HashMap<String, Object>();
        }

        fileMap.put(key, value);

        return this;
    }


    public RequestHelper addFiles(HashMap<String, String> params) {
        if (params == null || params.size() == 0) {
            return this;
        }

        if (fileMap == null) {
            fileMap = new HashMap<String, Object>();
        }

        fileMap.putAll(params);

        return this;
    }

    public RequestHelper addParam(String key, Object value) {
        if (key == null || value == null) {
            return this;
        }

        if (paramMap == null) {
            paramMap = new HashMap<String, Object>();
        }

        paramMap.put(key, value);

        return this;
    }

    public RequestHelper addParams(HashMap<String, Object> params) {
        if (params == null || params.size() == 0) {
            return this;
        }

        if (paramMap == null) {
            paramMap = new HashMap<String, Object>();
        }

        paramMap.putAll(params);

        return this;
    }

    public RequestHelper addClientInfo() {
        if (paramMap == null) {
            return this;
        }

        paramMap.put("deviceType", "android");
        paramMap.put("networkType", "wifi");
        paramMap.put("appChannel", "appstore");
        paramMap.put("appToken", "xmu_03120044");

        return this;
    }


    public void parse(String result) {
        LogUtils.d("helper", "result = " + result);
        if (StringUtils.isNotEmpty(result)) {

            try {
                JSONObject jsonObj = new JSONObject(result);
                mJsonObject = jsonObj;
                if (jsonObj != null) {
                    code = jsonObj.optInt("code");
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    public void finish() {
        endRequestTime = System.currentTimeMillis();
        LogUtils.d(getAction() + " costTime : " + (endRequestTime - startRequstTime));
    }

    public String getString(String key) {
        return ParseHelper.getString(mJsonObject, key);
    }


    public <T extends BaseModel> T getModel(String key, Class<T> clazz) {

        return ParseHelper.getModel(mJsonObject, key, clazz);
    }

    public boolean getBoolean(String key) {

        return ParseHelper.getBoolean(mJsonObject, key);
    }


    public int getInteger(String key) {

        return ParseHelper.getInteger(mJsonObject, key);
    }


    public long getLong(String key) {

        return ParseHelper.getLong(mJsonObject, key);
    }

    public double getDouble(String key) {

        return ParseHelper.getDouble(mJsonObject, key);
    }

    public <T extends BaseModel> List getModelList(String key, Class<T> clazz) {
        return ParseHelper.getModelList(mJsonObject, key, clazz);
    }

    public Object getLastObj(String key) {
        return ParseHelper.getLastObjEx(mJsonObject, key);
    }

    public boolean request() {
        String url = FrameworkConfig.HostUrl;// + getAction();
        //addClientInfo();
        LogUtils.e(url);
        HashMap<String, Object> p = getParamMap();
        //LogUtils.e(p.toString());
        String result = OkhttpManager.getInstance().requestSync(url, p);
        parse(result);

        return true;
    }
}


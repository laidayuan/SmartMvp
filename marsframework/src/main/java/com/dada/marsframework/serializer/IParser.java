package com.dada.marsframework.serializer;

import org.json.JSONObject;

public interface IParser {
	
	/**
	 * 将JSONObject 解析成对应的实体类。
	 * @param instance 将当前对象.
	 * @param jsonObj 要被解析的 Json 对象。
	 */
	void parseJSONObject(JSONObject jsonObj, Object instance);
	
	/**
	 * 将JSONObject 的串 解析成对应的实体类。
	 * @param instance 将当前对象.
	 * @param json 要被解析的String 
	 */
	void parseJSONString(String json, Object instance);

}
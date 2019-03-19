package com.dada.marsframework.serializer;

import org.json.JSONObject;

public interface ISerializer {
	/**
	 * 将当前对象序列化为JSONObject对象。
	 * @param instance 将当前对象.
	 * @return 被序列化的JSONObject 对象
	 */
	JSONObject serializeToJSONObject(Object instance);
	
	/**
	 * 将当前对象序列化为String。
	 * @param instance 将当前对象.
	 * @return 被序列化的string。
	 */
	String serializeToString(Object instance);
}

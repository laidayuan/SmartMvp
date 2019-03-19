package com.dada.marsframework.serializer;


import com.dada.marsframework.utils.LogUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DefaultSerializer implements ISerializer {

	private final String mTag = this.getClass().getSimpleName();

	@Override
	public JSONObject serializeToJSONObject(Object instance) {

		JSONObject jsonObject = new JSONObject();
		Field[] fields = instance.getClass().getFields();

		for (Field field : fields) {
			try {
				boolean hasAnnotation = field
						.isAnnotationPresent(JSONKey.class);
				if (hasAnnotation) {
					String[] keys = field.getAnnotation(JSONKey.class).keys();
					// 不需要考虑 key 值多对一的情况。
					if (null == keys || keys[0] == null) {
						continue;
					}
					Object fieldValue = field.get(instance);
					if (fieldValue == null) {
						continue;
					}
					if (field.getType() == Boolean.TYPE) {
						jsonObject.put(keys[0], field.getBoolean(instance));
					} else if (field.getType() == Long.TYPE) {
						jsonObject.put(keys[0], field.getLong(instance));
					} else if (field.getType() == Double.TYPE) {
						jsonObject.put(keys[0], field.getDouble(instance));
					} else if (field.getType() == Integer.TYPE) {
						jsonObject.put(keys[0], field.getInt(instance));
					} else if (field.get(instance) instanceof String) {
						jsonObject.put(keys[0], String.valueOf(fieldValue));
					} else if (fieldValue instanceof List<?>) {
						jsonObject.put(keys[0],
								toJsonArray((List<?>) fieldValue));
					} else {
						JSONObject obj = toJsonObject(fieldValue);
						jsonObject.put(keys[0], obj);
					}
				}
			} catch (IllegalArgumentException e) {
				if (null != e) {
					LogUtils.e(mTag, e.getMessage());
				}
			} catch (JSONException e) {
				if (null != e) {
					LogUtils.e(mTag, e.getMessage());
				}
			} catch (IllegalAccessException e) {
				if (null != e) {
					LogUtils.e(mTag, e.getMessage());
				}
			}
		}
		return jsonObject;

	}

	@Override
	public String serializeToString(Object instance) {
		return serializeToJSONObject(instance).toString();
	}

	private JSONArray toJsonArray(List<?> list) {
		if (null == list) {
			return null;
		}
		Iterator<?> iterator = list.iterator();
		return toJsonArray(iterator);
	}

	private JSONArray toJsonArray(Iterator<?> iterator) {
		if (null == iterator) {
			return null;
		}
		JSONArray jsonArray = new JSONArray();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			JSONObject jsonObject = toJsonObject(next);
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}

	private JSONObject toJsonObject(Object obj) {
		if (obj == null) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			Field[] fields = obj.getClass().getFields();
			for (Field field : fields) {
				boolean hasAnnotation = field
						.isAnnotationPresent(JSONKey.class);
				if (hasAnnotation) {
					String[] keys = field.getAnnotation(JSONKey.class).keys();
					if (null == keys) {
						continue;
					}
					Object fieldValue = field.get(obj);
					if (null == fieldValue) {
						continue;
					}
					if (field.getType() == Boolean.TYPE) {
						jsonObject.put(keys[0], field.getBoolean(obj));
					} else if (field.getType() == Long.TYPE) {
						jsonObject.put(keys[0], field.getLong(obj));
					} else if (field.getType() == Double.TYPE) {
						jsonObject.put(keys[0], field.getDouble(obj));
					} else if (field.getType() == Integer.TYPE) {
						jsonObject.put(keys[0], field.getInt(obj));
					} else if (field.get(obj) instanceof String) {
						jsonObject.put(keys[0], String.valueOf(fieldValue));
					} else if (fieldValue instanceof List<?>) {
						jsonObject.put(keys[0],
								toJsonArray((List<?>) fieldValue));
					} else {
						JSONObject object = toJsonObject(fieldValue);
						jsonObject.put(keys[0], object);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			if (null != e) {
				LogUtils.e(mTag, e.getMessage());
			}
		} catch (IllegalAccessException e) {
			if (null != e) {
				LogUtils.e(mTag, e.getMessage());
			}
		} catch (JSONException e) {
			if (null != e) {
				LogUtils.e(mTag, e.getMessage());
			}
		}
		return jsonObject;
	}

}

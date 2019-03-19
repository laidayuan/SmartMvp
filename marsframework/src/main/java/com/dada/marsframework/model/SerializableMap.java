package com.dada.marsframework.model;


import com.dada.marsframework.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SerializableMap implements Serializable {
	private Map<String, Object> map;
	
	public SerializableMap(HashMap<String, String> stringMap) {
		map = new HashMap<String, Object>();
		
		Iterator<Map.Entry<String, String>> it = stringMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();

			map.put(entry.getKey(), entry.getValue());
		}
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public int size() {
		return map.size();
	}

	public Object getValueByKey(String key) {
		return map.get(key);
	}

	public String toString() {

		return map.toString();
	}

	public HashMap<String, String> getStringMap() {
		HashMap<String, String> stringMap = new HashMap<String, String>();

		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();

			String strV = (String) entry.getValue();
			if (StringUtils.isNotEmpty(strV)) {
				stringMap.put(entry.getKey(), strV);
			}
			
		}
		
		return stringMap;
	}
}
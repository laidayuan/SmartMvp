package com.dada.marsframework.model;


import com.dada.marsframework.serializer.JSONKey;

public class PagerModel extends BaseModel {
	 
	
	@JSONKey(keys = "count", type = Integer.class)
	public int count;
	
	@JSONKey(keys = "currentPage", type = Integer.class)
	public int currentPage;
	
	@JSONKey(keys = "endIndex", type = Integer.class)
	public int endIndex;
	
	@JSONKey(keys = "nextPage", type = Integer.class)
	public int nextPage;
	
	@JSONKey(keys = "pageCount", type = Integer.class)
	public int pageCount;
	
	@JSONKey(keys = "pageSize", type = Integer.class)
	public int pageSize;
	
}

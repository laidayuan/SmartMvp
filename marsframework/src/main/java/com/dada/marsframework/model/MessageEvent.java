package com.dada.marsframework.model;

import com.alibaba.fastjson.JSON;

/**
 * Created by laidayuan on 2018/10/23.
 */

public class MessageEvent {
    private int eventId; //消息的id

    private String eventName;//消息的名字

    public String jstring; //如果是对象用方法访问，如果是string直接属性访问

    public void setData(Object obj) {
        if (obj != null) {
            jstring = JSON.toJSONString(obj);
        }
    }

    public <T extends BaseModel> T getData(Class cls) {
        return ParseHelper.parseObject(jstring, cls);
    }

    public void setEventId(int eid) {
        eventId = eid;
    }

    public void setEventName(String name) {
        eventName = name;
    }


    public int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }


    public boolean isEvent(int id) {
        return (eventId == id);
    }

    public boolean isEvent(String event) {
        return (event!=null&&eventName!=null&&event.equalsIgnoreCase(eventName));
    }

}

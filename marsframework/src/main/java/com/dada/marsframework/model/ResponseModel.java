package com.dada.marsframework.model;

import java.util.List;

public class ResponseModel<T extends SuperModel> {

    private int status;

    private int code;
    private String msg;

    private List<T> list;
    private T content;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getData() {
        return content;
    }

    public void setData(T data) {
        this.content = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }



}

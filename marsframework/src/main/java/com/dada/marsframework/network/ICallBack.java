package com.dada.marsframework.network;

/**
 * Created by laidayuan on 2018/1/30.
 */

public interface ICallBack {
    void onSuccess(int statusCode, Object result);
    void onProgress(int progress);
    void onFail(int statusCode, Object result);
}

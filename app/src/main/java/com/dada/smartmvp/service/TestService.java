package com.dada.smartmvp.service;

import android.app.IntentService;
import android.content.Intent;

import com.dada.marsframework.utils.LogUtils;

public class TestService extends IntentService {

    public TestService() {
        super("TestService");
        LogUtils.e("TestService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtils.e("onStart");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.e("onHandleIntent");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("线程结束运行...");
    }
}

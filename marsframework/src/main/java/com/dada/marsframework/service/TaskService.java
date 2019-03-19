package com.dada.marsframework.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.dada.marsframework.utils.LogUtils;

/**
 * Created by laidayuan on 2018/3/23.
 */

public class TaskService extends IntentService {

    private static final String TAG = "TaskService";

    public TaskService() {
        super(TAG);
        // TODO Auto-generated constructor stub
    }

    public TaskService(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {// 工作线程中执行
        if (intent == null) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }

        String action = bundle.getString("action");

        LogUtils.d("onHandleIntent  action = " + action);

    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d("onBind");
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        LogUtils.d("onCreate  ------  ");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogUtils.d("onStart  ------  ");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d("onStartCommand  ------  ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(enabled);
        LogUtils.d("setIntentRedelivery  ------  ");
    }

    @Override
    public void onDestroy() {
        LogUtils.d("onDestroy  ------  ");
        super.onDestroy();

    }
}

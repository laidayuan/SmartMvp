package com.dada.marsframework.base;


import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.dada.marsframework.R;
import com.dada.marsframework.keeplive.KeepLive;
import com.dada.marsframework.keeplive.config.ForegroundNotification;
import com.dada.marsframework.keeplive.config.ForegroundNotificationClickListener;
import com.dada.marsframework.keeplive.config.KeepLiveService;
import com.dada.marsframework.utils.SystemUtils;

/**
 * Created by laidayuan on 2018/10/21.
 */


public abstract class BaseApplication extends Application {

    private static BaseApplication appliction = null;

    public static BaseApplication getInstance() {

        return appliction;
    }


    //用于处理在super oncreate之前的操作
    public void onBeforeOnCreate() {

    }

    //初始化操作
    public void initApplication() {

    }

    @Override
    public void onCreate() {
        onBeforeOnCreate();
        super.onCreate();
        if (SystemUtils.isMainProcess(this)) {
            appliction = this;
        }

        initApplication();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }



    @Override
    public void onTerminate() {
        super.onTerminate();

    }


    protected void enableAlive() {
        //定义前台服务的默认样式。即标题、描述和图标
        ForegroundNotification foregroundNotification = new ForegroundNotification("测试","描述", R.mipmap.ic_launcher,
                //定义前台服务的通知点击事件
                new ForegroundNotificationClickListener() {

                    @Override
                    public void foregroundNotificationClick(Context context, Intent intent) {
                    }
                });
        //启动保活服务
        KeepLive.startWork(this, KeepLive.RunMode.ENERGY, foregroundNotification,
                //你需要保活的服务，如socket连接、定时任务等，建议不用匿名内部类的方式在这里写
                new KeepLiveService() {
                    /**
                     * 运行中
                     * 由于服务可能会多次自动启动，该方法可能重复调用
                     */
                    @Override
                    public void onWorking() {

                    }
                    /**
                     * 服务终止
                     * 由于服务可能会被多次终止，该方法可能重复调用，需同onWorking配套使用，如注册和注销broadcast
                     */
                    @Override
                    public void onStop() {

                    }
                }
        );
    }

}
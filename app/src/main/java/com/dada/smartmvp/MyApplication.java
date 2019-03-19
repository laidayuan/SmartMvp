package com.dada.smartmvp;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.dada.marsframework.anr.ANRWatchDog;
import com.dada.marsframework.base.BaseApplication;
import com.dada.marsframework.base.FrameworkConfig;
import com.dada.marsframework.utils.CrashHandler;
import com.dada.marsframework.utils.LogUtils;
import com.dada.marsframework.utils.SystemUtils;
import com.dada.marsframework.utils.ToastUtils;

/**
 * Created by laidayuan on 2018/10/23.
 */

public class MyApplication extends BaseApplication {
    private static final String TAG = MyApplication.class.getSimpleName();


    @Override
    public void initApplication() {
//        LogUtils.e(TAG, "AssistantApplication initApplication");

        //ANRWatchDog.create().begin();
        //CrashHandler.create(this);
        if (SystemUtils.isMainProcess(this)) {
            FrameworkConfig.setLogEnable(true);
            //CrashHandler.create(this);
            //CrashReport.initCrashReport(getApplicationContext(), "d557332538", FrameworkConfig.getLogEnable());
            //initRealm();
            FrameworkConfig.setDefaultFolder("KingAssistant");
            //FrameworkConfig.setHostUrl("http://www.mumu51.com/tdv/api/json_api.php");
            FrameworkConfig.setHostUrl("http://fy.iciba.com/");
            //FrameworkConfig.enableAlive(this, getPackageName(), MainActivity.class);

            ToastUtils.init();

            enableAlive();
        }
    }


    @Override
    public void onTerminate() {
        LogUtils.e(TAG, "onTerminate ");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        LogUtils.e(TAG, "onLowMemory ");
        super.onLowMemory();

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        //Beta.installTinker();
    }


}

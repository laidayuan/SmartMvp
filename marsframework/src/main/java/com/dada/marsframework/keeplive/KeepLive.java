package com.dada.marsframework.keeplive;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.dada.marsframework.base.BaseApplication;
import com.dada.marsframework.base.FrameworkConfig;
import com.dada.marsframework.keeplive.config.ForegroundNotification;
import com.dada.marsframework.keeplive.config.KeepLiveService;
import com.dada.marsframework.keeplive.config.NotificationUtils;
import com.dada.marsframework.keeplive.receiver.NotificationClickReceiver;
import com.dada.marsframework.keeplive.receiver.OnepxReceiver;
import com.dada.marsframework.keeplive.service.JobHandlerService;
import com.dada.marsframework.keeplive.service.LocalService;
import com.dada.marsframework.keeplive.service.RemoteService;
import com.dada.marsframework.utils.LogUtils;

import java.lang.ref.WeakReference;

/**
 * 保活工具
 */
public final class KeepLive {
    /**
     * 运行模式
     */
    public static enum RunMode {
        /**
         * 省电模式
         * 省电一些，但保活效果会差一点
         */
        ENERGY,
        /**
         * 流氓模式
         * 相对耗电，但可造就不死之身
         */
        ROGUE
    }
    public static ForegroundNotification foregroundNotification = null;
    public static KeepLiveService keepLiveService = null;
    public static RunMode runMode = null;

    public static int activityCount = 0;
    public static boolean inBackground = false;


    /**
     * 启动保活
     *
     * @param application            your application
     * @param foregroundNotification 前台服务
     * @param keepLiveService        保活业务
     */
    public static void startWork(final Application application, RunMode runMode, ForegroundNotification foregroundNotification, KeepLiveService keepLiveService) {
        if (isMain(application)) {
            KeepLive.foregroundNotification = foregroundNotification;
            KeepLive.keepLiveService = keepLiveService;
            KeepLive.runMode = runMode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// > 5.0
                //启动定时器，在定时器中启动本地服务和守护进程
                Intent intent = new Intent(application, JobHandlerService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// >= 8.0
                    application.startForegroundService(intent);
                } else {
                    application.startService(intent);
                }
            } else {// < 5.0
                //启动本地服务
                Intent localIntent = new Intent(application, LocalService.class);
                //启动守护进程
                Intent guardIntent = new Intent(application, RemoteService.class);
                application.startService(localIntent);
                application.startService(guardIntent);
            }

            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    LogUtils.e("onActivityCreated: " + activity.getClass().getSimpleName());

                }

                @Override
                public void onActivityStarted(Activity activity) {
                    if (!activity.getClass().getSimpleName().contains("OnePixelActivity")) {
                        ++activityCount;
                        LogUtils.e("onActivityStarted: " + activity.getClass().getSimpleName());
                        if (inBackground) {
                            //OnepxReceiver.goForeground(application);
                            OnepxReceiver.stopMusic(application);
                        }

                        inBackground = false;
                    }
                }

                @Override
                public void onActivityResumed(Activity activity) {

                    //LogUtils.e("onActivityResumed: " + activity.getClass().getSimpleName());
                }

                @Override
                public void onActivityPaused(Activity activity) {

                    //LogUtils.e("onActivityPaused: " + activity.getClass().getSimpleName());
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    if (!activity.getClass().getSimpleName().contains("OnePixelActivity")) {
                        --activityCount;
                        LogUtils.e("onActivityStopped: " + activity.getClass().getSimpleName());
                        if (activityCount == 0) {
                            inBackground = true;
                            //OnepxReceiver.goBackground(application);
                            OnepxReceiver.palyMusic(application);
                        }
                    }
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                    LogUtils.e("onActivitySaveInstanceState: " + activity.getClass().getSimpleName());
                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                    LogUtils.e("onActivityDestroyed: " + activity.getClass().getSimpleName());

                }
            });
        }
    }


    public static void notification(Service s) {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (KeepLive.foregroundNotification != null) {
                Intent intent2 = new Intent(BaseApplication.getInstance(), NotificationClickReceiver.class);
                intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
                Notification notification = NotificationUtils.createNotification(s, KeepLive.foregroundNotification.getTitle(), KeepLive.foregroundNotification.getDescription(), KeepLive.foregroundNotification.getIconRes(), intent2);
                s.startForeground(FrameworkConfig.ServiceNotification_ID, notification);
            }
        //}
    }

    private static boolean isMain(Application application) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager mActivityManager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        String packageName = application.getPackageName();
        if (processName.equals(packageName)) {
            return true;
        }
        return false;
    }
}

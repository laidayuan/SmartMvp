package com.dada.marsframework.base;

import android.app.ActivityManager;
import android.content.Context;

import com.dada.marsframework.utils.SystemUtils;

/**
 * Created by laidayuan on 2018/10/21.
 */
public class FrameworkConfig {

    private static boolean LogEnable = true;

    public static int ServiceNotification_ID = 13691;

    public static final String FILE_NAME = "darwin_config";

    public static String getHostUrl() {
        return HostUrl;
    }

    public static void setHostUrl(String url) {
        HostUrl = url;
    }

    public static String HostUrl = "";


    //控制是否打印log
    public static boolean getLogEnable() {
        return LogEnable;
    }

    public static void setLogEnable(boolean enable) {
        LogEnable = enable;
    }

    private static long maxImageFileCacheSize = 200*1024*1024;//default 200M


    public static void setMaxImageFileCacheSize(long size) {

        maxImageFileCacheSize = size;
    }

    public static long getMaxImageFileCacheSize() {
        return maxImageFileCacheSize;
    }


    public static final String Action_Schedule_Alarm = "Action_schedule_alarm";

    public static final String Action_Restart_Service = "Action_Restart_Service";

    public static final String Action_alarm_message = "android.com.alarm.message";

    public static final String Action_Click_Notification = "android.com.click.notification";

    public static final String UserFile = "com.darwin.user.file";

    public static final String LoginUser = "LoginUser";

    public static final int NOTIFICATION_ID = 0x11;
    public static final int NOTIFICATION_ID2 = 0x11;

    public static final int JOB_REFRESH_TIME = 2*60*1000;

    public static final int JOB_ALARM_TIME = 2*60*1000;

    public static final int TEST_ALARM_TIME = 30*1000;

    public static Class jumpActivity = null;

    public static void enableAlive(Context context, String processNames, Class activity) {
        jumpActivity = activity;
        //系统版本小于5或者大于8的不支持
        if (!SystemUtils.isAndroid5() /*|| SystemUtils.isAndroid8()*/ || jumpActivity == null) {
            return;
        }

        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;

                break;
            }
        }

    }


    public static String getDefaultFolder() {
        return defaultFolder;
    }

    public static void setDefaultFolder(String folder) {
        defaultFolder = folder;
    }

    private static String defaultFolder = "darwin";
}

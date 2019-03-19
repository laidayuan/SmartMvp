package com.dada.marsframework.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by laidayuan on 2018/10/22.
 */

public class ProcessUtils {

    public static boolean isMainProcess(Context context) {
        String processName = getProcessName(context, android.os.Process.myPid());
        LogUtils.e("pid : " + android.os.Process.myPid() + "   processName : " + processName);
        if (processName != null) {
            boolean defaultProcess = processName.equals(context.getPackageName());
            if (defaultProcess) {
                return true;
            }
        }

        return false;
    }

    public static boolean findProcessByName(Context context, String name) {
        if (context == null || name == null) {
            return false;
        }

        String processName = getProcessName(context, android.os.Process.myPid());
        LogUtils.e("pid : " + android.os.Process.myPid() + "   processName : " + processName);
        if (processName != null) {
            return processName.contains(name);
        }

        return false;
    }


    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }

        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }

        return null;
    }


    /**
     把Task移动到前台显示
     */
    public static void moveTaskToFront(Context context) {
        if (context == null)return;
        ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
                mAm.moveTaskToFront(rti.id, 0);

                return;
            }
        }
    }
}

package com.dada.marsframework.keeplive.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.dada.marsframework.keeplive.OnePixelActivity;
import com.dada.marsframework.utils.LogUtils;

import java.util.List;

@SuppressWarnings(value = {"unchecked", "deprecation"})
public final class OnepxReceiver extends BroadcastReceiver {

    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";


    android.os.Handler mHander;
    static boolean appIsForeground = false;

    public OnepxReceiver() {
        mHander = new android.os.Handler(Looper.getMainLooper());
    }

    public static void goBackground(final Context context) {
        try {
            Intent it = new Intent(context, OnePixelActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopMusic(context);
    }

    public static void goForeground(final Context context) {
        context.sendBroadcast(new Intent("finish activity"));
        if (!appIsForeground) {
            appIsForeground = false;
            try {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                home.addCategory(Intent.CATEGORY_HOME);
                context.getApplicationContext().startActivity(home);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        palyMusic(context);
    }

    public static void stopMusic(final Context context) {
        //通知屏幕已点亮，停止播放无声音乐
        context.sendBroadcast(new Intent("_ACTION_SCREEN_ON"));
    }

    public static void palyMusic(final Context context) {
        //通知屏幕已关闭，开始播放无声音乐
        context.sendBroadcast(new Intent("_ACTION_SCREEN_OFF"));
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {    //屏幕关闭的时候接受到广播
            appIsForeground = IsForeground(context);
            goBackground(context);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {   //屏幕打开的时候发送广播  结束一像素
            goForeground(context);
        } else if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            LogUtils.i("reason: " + reason);

            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                // 短按Home键
                LogUtils.i("homekey");

            }
            else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                // 长按Home键 或者 activity切换键
                LogUtils.i("long press home key or activity switch");

            }
            else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                // 锁屏
                LogUtils.i("lock");
            }
            else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                // samsung 长按Home键
                LogUtils.i("assist");


            }
        }
    }

    public boolean IsForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}

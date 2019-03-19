package com.dada.marsframework.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.inputmethod.InputMethodManager;
import android.widget.RemoteViews;

import com.dada.marsframework.R;
import com.dada.marsframework.base.BaseApplication;
import com.dada.marsframework.base.FrameworkConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;

/**
 * Created by laidayuan on 2018/10/21.
 */

public class SystemUtils {
    /**
     * 获取当前可用内存大小
     *
     * @return
     */
    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(context, mi.availMem);
    }

    public static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";
        long initial_memory = 0L;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            String str2 = localBufferedReader.readLine();
            String[] arrayOfString = str2.split("\\s+");
            String[] var8 = arrayOfString;
            int var9 = arrayOfString.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                String num = var8[var10];
                Log.i(str2, num + "\t");
            }

            initial_memory = (long)(Integer.valueOf(arrayOfString[1]) * 1024);
            localBufferedReader.close();
        } catch (IOException var12) {
            ;
        }

        return Formatter.formatFileSize(context, initial_memory);
    }

    public static String getPhoneInfo(Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService("phone");
        String mtyb = Build.BRAND;
        String mtype = Build.MODEL;
        return "品牌:" + mtyb + "，型号:" + mtype + "，版本: Android" + Build.VERSION.RELEASE + "，总内存:" + getTotalMemory(context) + "，当前可用内存:" + getAvailMemory(context);
    }

    public static String getSrceenSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        return ""+display.getWidth() + "*" + display.getHeight();
    }

    public static String getDeviceType() {
        return Build.MODEL;
    }

    /**
     * 安全退出应用程序.
     * 退出应用时需要处理运行中保留的状态
     * 如后台下载，通知栏的类容
     */
    public static void exit(Context context) {
        try {
            // 停止所有状态栏通知
            NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mgr.cancelAll();

        } catch (Exception e) {
            e.printStackTrace();
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }


    public static boolean isMainProcess(Context context) {
        String processName = getProcessName(context, android.os.Process.myPid());
        LogUtils.e("process id:" + android.os.Process.myPid() + "   process name:" + processName);
        if (processName != null) {
            boolean defaultProcess = processName.equals(context.getPackageName());
            if (defaultProcess) {
                return true;
            }
        }

        return false;
    }

    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager)context.getSystemService("activity");
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        } else {
            Iterator var4 = runningApps.iterator();
            ActivityManager.RunningAppProcessInfo procInfo;
            do {
                if (!var4.hasNext()) {
                    return null;
                }

                procInfo = (ActivityManager.RunningAppProcessInfo)var4.next();
            } while(procInfo.pid != pid);

            return procInfo.processName;
        }
    }

    /**
     * 获取手机系统SDK版本
     *
     * @return 如API 17 则返回 17
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取系统版本
     *
     * @return 形如2.3.3
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 调用系统发送短信
     */
    public static void sendSMS(Context cxt, String smsBody) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        cxt.startActivity(intent);
    }

    /**
     * 判断网络是否连接
     */
    public static boolean checkNet(Context context) {
        if (context == null) {
            context = BaseApplication.getInstance();
        }

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null;// 网络是否连接
    }


    /**
     * 判断是否为wifi联网
     */
    public static boolean isWiFi(Context cxt) {
        ConnectivityManager cm = (ConnectivityManager) cxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // wifi的状态：ConnectivityManager.TYPE_WIFI
        // 3G的状态：ConnectivityManager.TYPE_MOBILE
        NetworkInfo.State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        return NetworkInfo.State.CONNECTED == state;
    }

    /**
     * 隐藏系统键盘
     *
     * <br>
     * <b>警告</b> 必须是确定键盘显示时才能调用
     */
    public static void hideKeyBoard(Activity aty) {
        ((InputMethodManager) aty
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(
                        aty.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }



    /**
     * 获取当前应用程序的版本号
     */
    public static String getAppVersionName(Context context) {
        String version = "0";
        try {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(SystemUtils.class.getName()
                    + "the application not found");
        }
        return version;
    }

    /**
     * 获取当前应用程序的版本号
     */
    public static int getAppVersionCode(Context context) {
        int version = 0;
        try {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(SystemUtils.class.getName()
                    + "the application not found");
        }
        return version;
    }

    /**
     * 回到home，后台运行
     */
    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

    /**
     * 获取应用签名
     *
     * @param context
     * @param pkgName
     */
    public static String getSign(Context context, String pkgName) {
        try {
            PackageInfo pis = context.getPackageManager().getPackageInfo(
                    pkgName, PackageManager.GET_SIGNATURES);
            return hexdigest(pis.signatures[0].toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(SystemUtils.class.getName() + "the "
                    + pkgName + "'s application not found");
        }
    }

    /**
     * 将签名字符串转换成需要的32位签名
     */
    private static String hexdigest(byte[] paramArrayOfByte) {
        final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97,
                98, 99, 100, 101, 102 };
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            for (int i = 0, j = 0;; i++, j++) {
                if (i >= 16) {
                    return new String(arrayOfChar);
                }
                int k = arrayOfByte[i];
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                arrayOfChar[++j] = hexDigits[(k & 0xF)];
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取设备的可用内存大小
     *
     * @param cxt
     *            应用上下文对象context
     * @return 当前内存大小
     */
    public static int getDeviceUsableMemory(Context cxt) {
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 返回当前系统的可用内存
        return (int) (mi.availMem / (1024 * 1024));
    }

    /**
     * 清理后台进程与服务
     *
     * @param cxt
     *            应用上下文对象context
     * @return 被清理的数量
     */
    public static int gc(Context cxt) {
        long i = getDeviceUsableMemory(cxt);
        int count = 0; // 清理掉的进程数
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 获取正在运行的service列表
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(100);
        if (serviceList != null)
            for (ActivityManager.RunningServiceInfo service : serviceList) {
                if (service.pid == android.os.Process.myPid())
                    continue;
                try {
                    android.os.Process.killProcess(service.pid);
                    count++;
                } catch (Exception e) {
                    e.getStackTrace();
                    continue;
                }
            }

        // 获取正在运行的进程列表
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        if (processList != null)
            for (ActivityManager.RunningAppProcessInfo process : processList) {
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                if (process.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    // pkgList 得到该进程下运行的包名
                    String[] pkgList = process.pkgList;
                    for (String pkgName : pkgList) {
                        // KJLoger.debug("======正在杀死包名：" + pkgName);
                        try {
                            am.killBackgroundProcesses(pkgName);
                            count++;
                        } catch (Exception e) { // 防止意外发生
                            e.getStackTrace();
                            continue;
                        }
                    }
                }
            }
        // KJLoger.debug("清理了" + (getDeviceUsableMemory(cxt) - i) + "M内存");
        return count;
    }



    public static void openAppDetailSetting(Context context) {
        try {
            if (context == null) {
                return;
            }

//            Intent localIntent = new Intent();
//            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (Build.VERSION.SDK_INT >= 9) {
//                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
//            } else if (Build.VERSION.SDK_INT <= 8) {
//                localIntent.setAction(Intent.ACTION_VIEW);
//                localIntent.setClassName("com.android.settings",
//                        "com.android.settings.InstalledAppDetails");
//                localIntent.putExtra("com.android.settings.ApplicationPkgName",
//                        context.getPackageName());
//            }
//
//            context.startActivity(localIntent);

            // vivo 点击设置图标>加速白名单>我的app
            //      点击软件管理>软件管理权限>软件>我的app>信任该软件
            Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
            if(appIntent != null){
                context.startActivity(appIntent);

                return;
            }

            // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
            //      点击权限隐私>自启动管理>我的app
            appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
            if(appIntent != null){
                context.startActivity(appIntent);

                return;
            }

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(Build.VERSION.SDK_INT >= 9){
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if(Build.VERSION.SDK_INT <= 8){
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
                intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }

            context.startActivity(intent);

        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }


    public static String getMetaData(Context context, String key) {
        if (StringUtils.isNotEmpty(key)) {
            ApplicationInfo appInfo;
            try {
                appInfo = context.getPackageManager().getApplicationInfo(
                        context.getPackageName(), PackageManager.GET_META_DATA);
                return appInfo.metaData.getString(key);
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return null;
    }

    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager =
                (ActivityManager) context.getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        boolean result = false;

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                if (runningAppProcessInfo.processName.equals(packageName)) {
                    int status = runningAppProcessInfo.importance;
                    if (status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                            || status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }


    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForegroundEx(Context context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager =
                (ActivityManager) context.getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {

                return true;
            }
        }
        return false;
    }


    /**
     * 判断当前应用程序是否后台运行
     */
    public static boolean isBackground(Context context) {
        try {
            ActivityManager activityManager =
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses =
                    activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(context.getPackageName())) {
                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                        // 后台运行
                        return true;
                    } else {
                        // 前台运行
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            // 会出现空错误
        }
        return false;
    }

    /**
     * 判断手机是否处理睡眠
     */
    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr =
                (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        return isSleeping;
    }


    public static void sleep(long seconds) {
        try {
            Thread.sleep(seconds*1000);    //睡10秒，坐等ANR
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(Context context, String action) {

        broadcast(context, action, null);
    }

    public static final String Action_Broadcast = "Framework_Action_Broadcast";
    public static void broadcast(Context context, String action, Bundle bundle) {
        Intent it = new Intent(Action_Broadcast);
        it.putExtra("action", action);
        if (bundle != null) {
            it.putExtras(bundle);
        }
        context.sendBroadcast(it);
    }


    public static void startService(Context context, Class cls) {
        if (SystemUtils.isAndroid8()) {
            startForegroundService(context, cls);
        } else {
            context.startService(new Intent(context, cls));
        }

    }

    @TargetApi(26)
    public static void startForegroundService(Context context, Class cls) {

        context.startForegroundService(new Intent(context, cls));
    }

    @TargetApi(26)
    public static void setNotification8(Service service, int nid, Class cls) {
        String channelId = service.getPackageName();
        String channelName = "notification";
        NotificationChannel notificationChannel = null;
        notificationChannel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) service.getSystemService(Service.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);

        Notification.Builder builder = new Notification.Builder(service);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVisibility(Notification.VISIBILITY_SECRET);
        builder.setPriority(Notification.PRIORITY_MIN);
        builder.setContentTitle(service.getString(R.string.app_name));
        builder.setContentText(service.getString(R.string.app_desc));
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setChannelId(channelId);

        Intent hangIntent = new Intent();
        hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        hangIntent.setClass(service, cls);
        //hangIntent.setAction(FrameworkConfig.Action_Click_Notification);
        PendingIntent hangPendingIntent = PendingIntent.getActivity(service, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(hangPendingIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        RemoteViews view = new RemoteViews(service.getPackageName(), R.layout.layout_notification);
        // PendingIntent clickIntent = PendingIntent.getActivity(service,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent it = new Intent(FrameworkConfig.Action_Click_Notification);
        PendingIntent clickIntent = PendingIntent.getBroadcast(service, 0, it, PendingIntent.FLAG_CANCEL_CURRENT);
        view.setOnClickPendingIntent(R.id.item_action, clickIntent);

        notification.contentView = view;

        //notification.bigContentView = view;
        //builder.setCustomContentView(view);
        //notification.defaults = Notification.DEFAULT_ALL;
        //manager.notify(nid, notification);
        try {
            service.startForeground(nid, notification);

        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    public static void startForeground(Service service, int nid, Class cls) {
        //NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //manager.notify(nid, new Notification());
            service.startForeground(nid, new Notification());

        } else {
            //API 18以上，发送Notification并将其置为前台后
            if (isAndroid8()) {
                setNotification8(service, nid, cls);
            } else{
                Notification.Builder builder = new Notification.Builder(service);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setVisibility(Notification.VISIBILITY_SECRET);
                builder.setPriority(Notification.PRIORITY_MIN);
                builder.setContentTitle(service.getString(R.string.app_name));
                builder.setContentText(service.getString(R.string.app_desc));
                builder.setAutoCancel(true);
                builder.setOngoing(false);

                Intent hangIntent = new Intent();
                hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                hangIntent.setClass(service, cls);
                //hangIntent.setAction(FrameworkConfig.Action_Click_Notification);
                PendingIntent hangPendingIntent = PendingIntent.getActivity(service, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                builder.setContentIntent(hangPendingIntent);
                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                RemoteViews view = new RemoteViews(service.getPackageName(), R.layout.layout_notification);
                // PendingIntent clickIntent = PendingIntent.getActivity(service,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Intent it = new Intent(FrameworkConfig.Action_Click_Notification);
                PendingIntent clickIntent = PendingIntent.getBroadcast(service, 0, it, PendingIntent.FLAG_CANCEL_CURRENT);
                view.setOnClickPendingIntent(R.id.item_action, clickIntent);

                notification.contentView = view;

                //notification.bigContentView = view;
                //builder.setCustomContentView(view);
                //notification.defaults = Notification.DEFAULT_ALL;
                //manager.notify(nid, notification);
                try {
                    service.startForeground(nid, notification);

                } catch (Exception e) {
                    LogUtils.e(e.toString());
                }
            }
        }

        //manager.cancel(nid);
    }

    public static void cancelNotify(Context context, int nid) {
        NotificationManager cancelNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        cancelNotificationManager.cancel(nid);
        cancelNotificationManager.cancelAll();
    }

    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }

        return false;
    }




    public static void alarm(Context context, long stepTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(FrameworkConfig.Action_alarm_message);
        //PendingIntent sender = PendingIntent.getBroadcast(context, 0x11, intent, 0);
        //PendingIntent sender = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, FrameworkConfig.JOB_ALARM_TIME, sender);
        PendingIntent sender = PendingIntent.getBroadcast(context,
                0x1111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = System.currentTimeMillis();//SystemClock.elapsedRealtime();//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setWindow(AlarmManager.RTC_WAKEUP, triggerAtTime+stepTime, 3000, sender);
            //am.set(AlarmManager.RTC_WAKEUP, triggerAtTime+stepTime, sender);
        } else {
            am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, FrameworkConfig.TEST_ALARM_TIME, sender);
        }
    }

    public static boolean isAndroid7() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
    }

    public static boolean isAndroid8() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O);
    }


    public static boolean isAndroid5() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }


    /**
     * 判断 悬浮窗口权限是否打开
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean checkAlertWindowsPermission(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1));
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }


    public static void setUnLock(final Activity context) {
        try {
            Settings.System.putInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE);

        } catch (Exception e) {
            LogUtils.e("e = " + e);
        }
    }

    public static int getLockTimeout(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception e) {
            LogUtils.e("e = " + e);
        }

        return -1;
    }

    public static void setLockTimeout(Context context, int timeout) {
        try {
            Settings.System.putInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, timeout);

        } catch (Exception e) {
            LogUtils.e("e = " + e);
        }
    }


    public static void requestPermissions(final Activity context, String... permissions) {
        PermissionHelper.requestPermission(context, new PermissionHelper.ICallBack() {
            @Override
            public void onAction(int status) {
                LogUtils.e("status = " + status);
                if (status == 0) {
                }
            }

            @Override
            public void onError(String error) {
                LogUtils.e("error = " + error);
            }

            @Override
            public void OnComplete() {
                LogUtils.e("OnComplete --- ");

            }
        }, permissions);
    }
}

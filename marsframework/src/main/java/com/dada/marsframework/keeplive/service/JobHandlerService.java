package com.dada.marsframework.keeplive.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.dada.marsframework.base.FrameworkConfig;
import com.dada.marsframework.keeplive.KeepLive;
import com.dada.marsframework.keeplive.config.NotificationUtils;
import com.dada.marsframework.keeplive.receiver.NotificationClickReceiver;
import com.dada.marsframework.utils.LogUtils;

import java.util.Iterator;
import java.util.List;


/**
 * 定时器
 * 安卓5.0及以上
 */
@SuppressWarnings(value={"unchecked", "deprecation"})
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class JobHandlerService extends JobService {
    private JobScheduler mJobScheduler;
    private int jobId = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("onStartCommand ---- ");
        startService(this);
        jobId = startId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            configJob(jobId);
        }
        return START_STICKY;
    }

    private void configJob(int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            JobInfo.Builder builder = new JobInfo.Builder(startId++,
                    new ComponentName(getPackageName(), JobHandlerService.class.getName()));
            if (Build.VERSION.SDK_INT >= 24) {
                builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS); //执行的最小延迟时间
                builder.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);  //执行的最长延时时间
                builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
                builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
            } else {
                builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
            }
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            //builder.setRequiresCharging(true); // 当插入充电器，执行该任务
            mJobScheduler.schedule(builder.build());
            /*if (mJobScheduler.schedule(builder.build()) <= 0) {
                Log.e("JobHandlerService", "工作失败");
            } else {
                Log.e("JobHandlerService", "工作成功");
            }*/
        }
    }

    private void startService(Context context){
        KeepLive.notification(this);
        //启动本地服务
        Intent localIntent = new Intent(context, LocalService.class);
        //启动守护进程
        Intent guardIntent = new Intent(context, RemoteService.class);
        startService(localIntent);
        startService(guardIntent);
    }
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        LogUtils.e("onStartJob ---- ");
        if (!isServiceRunning(getApplicationContext(), "com.dada.marsframework.keeplive.service.LocalService") || !isRunningTaskExist(getApplicationContext(), getPackageName()+":remote")) {
            startService(this);
        }

        configJob(jobId);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        LogUtils.e("onStopJob ---- ");
        if (!isServiceRunning(getApplicationContext(), "com.dada.marsframework.keeplive.service.LocalService") || !isRunningTaskExist(getApplicationContext(), getPackageName()+":remote")) {
            startService(this);
        }

        configJob(jobId);

        return false;
    }
    private boolean isServiceRunning(Context ctx, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        Iterator<ActivityManager.RunningServiceInfo> l = servicesList.iterator();
        while (l.hasNext()) {
            ActivityManager.RunningServiceInfo si = l.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }
    private boolean isRunningTaskExist(Context context, String processName){
        ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info:processList){
            if (info.processName.equals(processName)){
                return true;
            }
        }
        return false;
    }
}

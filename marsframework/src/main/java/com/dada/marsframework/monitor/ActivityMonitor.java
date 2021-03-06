package com.dada.marsframework.monitor;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import com.dada.marsframework.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by laidayuan on 2018/10/22.
 */

public class ActivityMonitor implements Application.ActivityLifecycleCallbacks {


    private static ActivityMonitor sInstance;

    private boolean mIsFrameMonitor = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mPaused = true;
    private Runnable mCheckForegroundRunnable;
    private boolean mForeground = false;
    //当前Activity的弱引用
    private WeakReference<Activity> mActivityReference;

    protected final String TAG = "MyActivityLifeCycle";

    public static final int ACTIVITY_ON_RESUME = 0;
    public static final int ACTIVITY_ON_PAUSE = 1;

    private ActivityMonitor() {

    }

    public static ActivityMonitor getInstance() {
        if (sInstance==null) {
            synchronized(ActivityMonitor.class) {
                if (sInstance==null) {
                    sInstance = new ActivityMonitor();
                }
            }
        }

        return sInstance;
    }

    public void enableFrameMonitor(boolean enable) {
        mIsFrameMonitor = enable;
    }


    public Activity getCurrentActivity() {
        if (mActivityReference != null) {
            return mActivityReference.get();
        }
        return null;
    }

    public boolean isForground() {
        return mForeground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        LogUtils.d(TAG, activity.getLocalClassName() + " " + "onActivityCreated");
        mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtils.d(TAG, activity.getLocalClassName() + " " + "onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        String activityName = activity.getClass().getName();
        notifyActivityChanged(activity, activityName, ACTIVITY_ON_RESUME);
        mPaused = false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && mIsFrameMonitor
                /*&& ConfigManager.getInstance().isFrameSkipCheckOn()*//*配置是否开启统计*/) {
            FrameSkipMonitor.getInstance().setActivityName(activityName);
            FrameSkipMonitor.getInstance().onActivityResume();
            if (!mForeground) {
                FrameSkipMonitor.getInstance().start();
            }
        }
        mForeground = true;
        if (mCheckForegroundRunnable != null) {
            mHandler.removeCallbacks(mCheckForegroundRunnable);
        }
        mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {//pause事件后是否在前台要分情况判断
        notifyActivityChanged(activity, activity.getClass().getName(), ACTIVITY_ON_PAUSE);
        mPaused = true;
        if (mCheckForegroundRunnable != null) {
            mHandler.removeCallbacks(mCheckForegroundRunnable);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            FrameSkipMonitor.getInstance().onActivityPause();

        mHandler.postDelayed(mCheckForegroundRunnable = new Runnable() {
            @Override
            public void run() {
                if (mPaused && mForeground) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && mIsFrameMonitor
                             /*&& ConfigManager.getInstance().isFrameSkipCheckOn()*//*配置是否开启统计*/) {
                        FrameSkipMonitor.getInstance().report();
                    }
                    mForeground = false;
                }
            }
        }, 1000);

    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtils.d(TAG, activity.getLocalClassName() + " " + "onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtils.d(TAG, activity.getLocalClassName() + " " + "onActivityDestroyed");
    }

    private void notifyActivityChanged(Context context, String activityName, int lifeState) {
        Intent intent = new Intent("com.dada.android.activity_change");
        intent.putExtra("activity_name", activityName);
        intent.putExtra("activity_life", lifeState);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

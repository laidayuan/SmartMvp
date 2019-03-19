package com.dada.marsframework.monitor;

/**
 * Created by laidayuan on 2018/10/22.
 */


import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.Choreographer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by guangcheng.fan on 2016/10/10.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class FrameSkipMonitor implements Choreographer.FrameCallback{
    protected final String TAG = "FrameSkipMonitor";
    private static final long ONE_FRAME_TIME = 16600000; // 1 Frame time cost
    private static final long MIN_FRAME_TIME = ONE_FRAME_TIME * 3; // 3 Frame time cost
    private static final long MAX_FRAME_TIME = 60 * ONE_FRAME_TIME; // 60 Frame time cost, not record some special cases.

    private static final String SKIP_EVENT_NAME = "frame_skip";

    private static FrameSkipMonitor sInstance;

    private long mLastFrameNanoTime = 0;
    private HashMap<String, Long> mSkipRecordMap;
    private HashMap<String, Long> mActivityShowTimeMap;
    private String mActivityName;
    private long mActivityStartTime = 0;

    private FrameSkipMonitor() {
        mSkipRecordMap = new HashMap<>();
        mActivityShowTimeMap = new HashMap<>();
    }

    public static FrameSkipMonitor getInstance() {
        if (sInstance==null) {
            synchronized(FrameSkipMonitor.class) {
                if (sInstance==null) {
                    sInstance = new FrameSkipMonitor();
                }
            }
        }

        return sInstance;
    }

    public void setActivityName(String activityName) {
        mActivityName = activityName;
    }

    public void start() {
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (mLastFrameNanoTime != 0) {
            long frameInterval = frameTimeNanos - mLastFrameNanoTime;
            if (frameInterval > MIN_FRAME_TIME && frameInterval < MAX_FRAME_TIME) {
                long time = 0;
                if (mSkipRecordMap.containsKey(mActivityName)) {
                    time = mSkipRecordMap.get(mActivityName);
                }
                mSkipRecordMap.put(mActivityName, time + frameInterval);
            }
        }
        mLastFrameNanoTime = frameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
        Runtime.getRuntime().maxMemory();
    }

    public void report() {
        Choreographer.getInstance().removeFrameCallback(this);
        Iterator iter = mSkipRecordMap.entrySet().iterator();
        StringBuilder pages = new StringBuilder();
        StringBuilder skips = new StringBuilder();
        long skipFrames = 0;
        long skipFramesPerSecond = 0;
        long activityShowTime = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            pages.append((String) entry.getKey()).append(",\n");
            skipFrames = ((long) entry.getValue()/ONE_FRAME_TIME);

            if (mActivityShowTimeMap.containsKey(entry.getKey())) {
                activityShowTime = mActivityShowTimeMap.get(entry.getKey());
            }
            skipFramesPerSecond = skipFrames;//activityShowTime <= 0 ? -1 : skipFrames * 1000 / activityShowTime;
            skips.append(Long.toString(skipFramesPerSecond)).append(",\n");
        }
        HashMap map = new HashMap();
        map.put("page", pages.toString());
        map.put("time", skips.toString());
        mSkipRecordMap.clear();
        Log.d(TAG, "page：\n" + pages.toString() + "time：\n" + skips.toString());
    }

    public void onActivityResume() {
        mActivityStartTime = System.currentTimeMillis();
    }

    public void onActivityPause() {
        long activityShowInterval = System.currentTimeMillis() - mActivityStartTime;
        long time = 0;
        if (mActivityShowTimeMap.containsKey(mActivityName)) {
            time = mActivityShowTimeMap.get(mActivityName);
        }
        mActivityShowTimeMap.put(mActivityName, time + activityShowInterval);
    }
}

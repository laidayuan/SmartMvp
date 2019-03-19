package com.dada.marsframework.anr;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dada.marsframework.utils.CrashHandler;


public class ANRWatchDog extends Thread {

    public static final int MESSAGE_WATCHDOG_TIME_TICK = 0;

    /**
     * 判定Activity发生了ANR的时间，必须要小于5秒，否则等弹出ANR，可能就被用户立即杀死了。
     */
    public static final int ACTIVITY_ANR_TIMEOUT = 5000;

    private static int lastTimeTick = -1;
    private static int timeTick = 0;

    private volatile boolean stop = false;

    private static ANRWatchDog instance = null;

    private ANRWatchDog() {
    }

    public static ANRWatchDog create() {
        if (instance==null) {
            synchronized(ANRWatchDog.class) {
                if (instance==null) {
                    instance = new ANRWatchDog();
                    instance.stop = false;
                }
            }
        }

        return instance;
    }

    public void begin() {
        create();
        instance.start();
    }

    public synchronized void finish() {
        stop = true;
        instance = null;
    }

    //Looper.getMainLooper()
    private Handler watchDogHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            timeTick++;
            timeTick = timeTick % Integer.MAX_VALUE;
        }
    };

    @Override
    public void run() {
        while (true) {
            if (stop) {
                return;
            }

            watchDogHandler.sendEmptyMessage(MESSAGE_WATCHDOG_TIME_TICK);
            try {
                Thread.sleep(ACTIVITY_ANR_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //如果相等，说明过了ACTIVITY_ANR_TIMEOUT的时间后watchDogHandler仍没有处理消息，已经ANR了
            if (timeTick == lastTimeTick) {
                throw new ANRException();
            } else {
                lastTimeTick = timeTick;
            }
        }
    }

    public class ANRException extends RuntimeException {
        public ANRException() {
            super("***********发生ANR了，主人救救我！***********");
            Thread mainThread = Looper.getMainLooper().getThread();
            setStackTrace(mainThread.getStackTrace());
            //CrashHandler.printStackTrace(mainThread.getStackTrace());
        }
    }

}

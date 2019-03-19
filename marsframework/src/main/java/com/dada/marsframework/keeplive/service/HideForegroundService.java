package com.dada.marsframework.keeplive.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;

import com.dada.marsframework.base.FrameworkConfig;
import com.dada.marsframework.keeplive.KeepLive;
import com.dada.marsframework.keeplive.config.NotificationUtils;
import com.dada.marsframework.keeplive.receiver.NotificationClickReceiver;
import com.dada.marsframework.utils.LogUtils;

/**
 * 隐藏前台服务通知
 */
public class HideForegroundService extends Service {
    private android.os.Handler handler;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        if (handler == null){
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("stopForeground ---- ");
                stopForeground(true);
                stopSelf();
            }
        }, 2000);
        return START_NOT_STICKY;
    }


    private void startForeground() {
        LogUtils.e("startForeground ---- ");
//        if (KeepLive.foregroundNotification != null) {
//            Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
//            intent.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
//            Notification notification = NotificationUtils.createNotification(this, KeepLive.foregroundNotification.getTitle(), KeepLive.foregroundNotification.getDescription(), KeepLive.foregroundNotification.getIconRes(), intent);
//            startForeground(FrameworkConfig.ServiceNotification_ID, notification);
//        }

        KeepLive.notification(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

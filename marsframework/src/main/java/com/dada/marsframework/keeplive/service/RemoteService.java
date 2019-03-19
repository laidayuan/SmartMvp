package com.dada.marsframework.keeplive.service;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.dada.marsframework.base.FrameworkConfig;
import com.dada.marsframework.keeplive.KeepLive;
import com.dada.marsframework.keeplive.config.NotificationUtils;
import com.dada.marsframework.keeplive.receiver.NotificationClickReceiver;
import com.dada.marsframework.utils.LogUtils;


/**
 * 守护进程
 */
@SuppressWarnings(value={"unchecked", "deprecation"})
public final class RemoteService extends Service {
    private MyBilder mBilder;


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate ---- ");
        if (mBilder == null){
            mBilder = new MyBilder();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("onStartCommand ---- ");
        try {
            this.bindService(new Intent(RemoteService.this, LocalService.class),
                    connection, Context.BIND_ABOVE_CLIENT);
        }catch (Exception e){
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy ---- ");
        unbindService(connection);
    }
    private final class MyBilder extends GuardAidl.Stub {

        @Override
        public void wakeUp(String title, String discription, int iconRes) throws RemoteException {
            LogUtils.e("wakeUp ---- " + Build.VERSION.SDK_INT);
//            if(Build.VERSION.SDK_INT < 25){
//                Intent intent2 = new Intent(getApplicationContext(), NotificationClickReceiver.class);
//                intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
//                Notification notification = NotificationUtils.createNotification(RemoteService.this, title, discription, iconRes, intent2);
//                RemoteService.this.startForeground(FrameworkConfig.ServiceNotification_ID, notification);
//            }

            //KeepLive.notification(RemoteService.this);
        }

    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e("onServiceDisconnected ---- ");
            Intent remoteService = new Intent(RemoteService.this,
                    LocalService.class);
            RemoteService.this.startService(remoteService);
            RemoteService.this.bindService(new Intent(RemoteService.this,
                    LocalService.class), connection, Context.BIND_ABOVE_CLIENT);
            PowerManager pm = (PowerManager) RemoteService.this.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            if (isScreenOn){
                sendBroadcast(new Intent("_ACTION_SCREEN_ON"));
            }else{
                sendBroadcast(new Intent("_ACTION_SCREEN_OFF"));
            }
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e("onServiceConnected ---- ");
        }
    };

}

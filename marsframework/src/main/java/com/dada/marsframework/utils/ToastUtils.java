package com.dada.marsframework.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dada.marsframework.R;
import com.dada.marsframework.base.BaseApplication;
import com.dada.marsframework.widget.svprogresshud.SVProgressHUD;

/**
 * Created by laidayuan on 2018/10/21.
 */


public class ToastUtils {
    private static Context context = BaseApplication.getInstance();// App生命周期中唯一Context，BaseApplication继承Application
    private static LayoutInflater inflater = LayoutInflater.from(context);
    private static View myToastView = inflater.inflate(
            R.layout.layout_toast, null);
    private static TextView msgView = (TextView) myToastView
            .findViewById(R.id.tv_message);

    private static final int TYPE_CODE_SUCCESS = 0x01;
    private static final int TYPE_CODE_ERROR = 0x02;
    private static final int DEFAULT_TIME_DELAY = 50;// 单位：毫秒

    private static Toast toast;// 系统提示类
    private static Handler handler;

    private static SVProgressHUD mSVProgressHUD = null;


    public void showProgress(Context ctx, int resId) {
        showProgress(ctx, context.getResources().getString(resId));
    }

    @SuppressLint("NewApi")
    public static void showProgress(final Context ctx, final String text) {
        if (StringUtils.isNotEmpty(text)) {
            mSVProgressHUD = new SVProgressHUD(ctx);
            mSVProgressHUD.showWithStatus(text);

            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    stopProgress();
                }
            }, 30 * 1000);
        }
    }

    public static void stopProgress() {
        if (mSVProgressHUD != null && mSVProgressHUD.isShowing()) {
            // mSVProgressHUD.dismiss();
            mSVProgressHUD.dismissImmediately();
            handler.removeCallbacksAndMessages(null);
            mSVProgressHUD = null;
        }
    }

    public static void init() {
        toast = new Toast(context);
        handler = new Handler(Looper.getMainLooper());

    }

    public static void toast(String msg) {
        if (show(TYPE_CODE_SUCCESS, msg)) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 30);
            msgView.setLayoutParams(params);
        }
    }

    public static void toastError(String msg) {
        show(TYPE_CODE_ERROR, msg);
    }

    private static boolean show(final int typeCode, final String msg) {
        if (context == null || SystemUtils.isBackground(context) == true// 如果APP回到后台，则不显示
                || StringUtils.isEmpty(msg)) {
            return false;
        }

        if (toast == null) {// 防止重复提示：不为Null，即全局使用同一个Toast实例
            toast = new Toast(context);
        }

        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                msgView.setText(msg);
                toast.setView(myToastView);
//				toast.setDuration(Toast.LENGTH_SHORT);

                AlphaAnimation animation = new AlphaAnimation(1, 0);
                animation.setDuration(3000);
                animation.setFillBefore(true);
                msgView.startAnimation(animation);
                toast.show();
            }
        }, DEFAULT_TIME_DELAY);

        return true;
    }

}


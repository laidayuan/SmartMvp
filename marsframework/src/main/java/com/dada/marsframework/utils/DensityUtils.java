package com.dada.marsframework.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.dada.marsframework.base.BaseApplication;

/**
 * Created by laidayuan on 2018/2/12.
 */

public class DensityUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, r.getDisplayMetrics());
        return (int) px;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取dialog宽度
     */
    public static int getDialogW(Context aty) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = aty.getResources().getDisplayMetrics();
        int w = dm.widthPixels - 100;
        // int w = aty.getWindowManager().getDefaultDisplay().getWidth() - 100;
        return w;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenW(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenH(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int result = 0;
        int resourceId = activity.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        int screenHeight = dm.heightPixels - result;
        return screenHeight;
    }

    @SuppressLint("NewApi")
    public static float getScreenWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.getWindowManager().getDefaultDisplay()
                    .getRealMetrics(metrics);
        } else {
            // TODO this is not correct, but we don't really care pre-kitkat
            activity.getWindowManager().getDefaultDisplay()
                    .getMetrics(metrics);
        }

        //float widthDp = metrics.widthPixels / metrics.density;
        //float heightDp = metrics.heightPixels / metrics.density;
        return metrics.heightPixels;
    }

    /**
     * 适配的主要代码
     *
     * @param activity        上下文
     * @param sizeInPx        你要适配的相应尺寸
     * @param isVerticalSlide 水平还是垂直为参考
     */
    private static void adaptScreen(final Activity activity,
                                    final int sizeInPx,
                                    final boolean isVerticalSlide) {
        // 系统的屏幕尺寸
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        // app整体的屏幕尺寸
        final DisplayMetrics appDm = BaseApplication.getInstance().getResources().getDisplayMetrics();
        // activity的屏幕尺寸
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        if (isVerticalSlide) {
            activityDm.density = activityDm.widthPixels / (float) sizeInPx;
            LogUtils.e("", "adaptScreen: "+activityDm.widthPixels );
        } else {
            activityDm.density = activityDm.heightPixels / (float) sizeInPx;
        }

        // 字体的缩放因子，这个是通过一个比例计算得来的！
        activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
        // 计算得到相应的dpi
        activityDm.densityDpi = (int) (160 * activityDm.density);

        //进行相应的赋值操作
        appDm.density = activityDm.density;
        appDm.scaledDensity = activityDm.scaledDensity;
        appDm.densityDpi = activityDm.densityDpi;
    }


    public static void cancelAdaptScreen(final Activity activity) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = BaseApplication.getInstance().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        activityDm.density = systemDm.density;
        activityDm.scaledDensity = systemDm.scaledDensity;
        activityDm.densityDpi = systemDm.densityDpi;

        appDm.density = systemDm.density;
        appDm.scaledDensity = systemDm.scaledDensity;
        appDm.densityDpi = systemDm.densityDpi;
    }


    public static void restoreAdaptScreen(Activity activity, boolean isVerticalSlide, int sizeInPx) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = BaseApplication.getInstance().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        if (isVerticalSlide) {
            activityDm.density = activityDm.widthPixels / (float) sizeInPx;
        } else {
            activityDm.density = activityDm.heightPixels / (float) sizeInPx;
        }
        activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
        activityDm.densityDpi = (int) (160 * activityDm.density);

        appDm.density = activityDm.density;
        appDm.scaledDensity = activityDm.scaledDensity;
        appDm.densityDpi = activityDm.densityDpi;
    }


    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }


    /**
     * Return whether adapt screen.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAdaptScreen() {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = BaseApplication.getInstance().getResources().getDisplayMetrics();
        return systemDm.density != appDm.density;
    }





}

package com.dada.marsframework.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.lang.reflect.Method;

/**
 * Created by laidayuan on 2018/2/12.
 */

public class ActivityUtils {


    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }


    public static void jumpActivity(Context context, Class cls) {
        jumpActivity(context, cls, null);
    }

    public static void jumpActivity(Context context, Class cls,
                                    Bundle bundle) {
        jumpActivity(context, cls, bundle, false);
    }

    public static void jumpActivity(Context context, Class cls,
                                    Bundle bundle, boolean bTop) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        intent.setClass(context, cls);
        if (bTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        context.startActivity(intent);
    }

    public static void jumpActivityWithIntent(Activity context,
                                              Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void jumpActivityForResult(Activity context, Class cls,
                                             int requestCode) {
        jumpActivityForResult(context, cls, requestCode, null);
    }

    public static void jumpActivityForResult(Activity context, Class cls,
                                             int requestCode, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        intent.setClass(context, cls);
        context.startActivityForResult(intent, requestCode);
    }

    public static void convertActivityFromTranslucent(Activity activity) {
		/*try {
			Method method = Activity.class.getDeclaredMethod(
					"convertFromTranslucent", null);
			method.setAccessible(true);
			method.invoke(mActivity, null);
		} catch (Throwable t) {
		}*/

        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent", new Class<?>[]{});

            method.setAccessible(true);
            method.invoke(activity, new Object[] {});

        } catch (Throwable ignored) {
        }
    }

    @SuppressLint("NewApi")
    public static void convertActivityToTranslucent(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains(
                        "TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }

            Method[] methods = Activity.class.getDeclaredMethods();
            if (Build.VERSION.SDK_INT <= 19) {
                Method method = Activity.class.getDeclaredMethod(
                        "convertToTranslucent",
                        translucentConversionListenerClazz);
                method.setAccessible(true);
                method.invoke(activity, new Object[] { null });
            } else {
                Method method = Activity.class.getDeclaredMethod(
                        "convertToTranslucent",
                        translucentConversionListenerClazz,
                        ActivityOptions.class);
                method.setAccessible(true);
                method.invoke(activity, new Object[] { null, null });
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

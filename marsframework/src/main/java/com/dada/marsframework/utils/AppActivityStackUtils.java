package com.dada.marsframework.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import com.dada.marsframework.base.BaseApplication;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Created by yinzhiqiang on 2018/6/26.
 */

public class AppActivityStackUtils {
    private static Stack<Activity> activityStack;
    private static AppActivityStackUtils instance = null;
    private static Map<String, Activity> destoryMap = new HashMap<String, Activity>();

    private AppActivityStackUtils() {
    }

    public static AppActivityStackUtils create() {
        if (instance==null) {
            synchronized(AppActivityStackUtils.class) {
                if (instance==null) {
                    instance = new AppActivityStackUtils();
                }
            }
        }

        return instance;
    }

    /**
     * 获取当前Activity栈中元素个数
     */
    public int getCount() {
        return activityStack.size();
    }

    /**
     * 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        if (activity != null) {
            WeakReference<Activity> activityWeak = new WeakReference<Activity>(activity);
            activityStack.add(activityWeak.get());
        }
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    public Activity topActivity() {
        if (activityStack == null) {
            throw new NullPointerException(
                    "Activity stack is Null,your Activity must extend KJActivity");
        }
        if (activityStack.isEmpty()) {
            return null;
        }
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
    public Activity findActivity(Class<?> cls) {
        if (activityStack == null) {
            return null;
        }

        Activity activity = null;
        for (Activity aty : activityStack) {
            if (aty.getClass().equals(cls)) {
                activity = aty;
                break;
            }
        }
        return activity;
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    public void finishActivity() {
        if (activityStack == null) {
            return;
        }
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            WeakReference<Activity> activityWeak = new WeakReference<Activity>(activity);
            activityStack.remove(activityWeak.get());
            if (!activity.isFinishing()) {
                activity.finish();// 此处不用finish
            }
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    public synchronized void finishActivity(Class<?> cls) {
        if (activityStack == null) {
            return;
        }

        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     *
     * @param cls
     */
    public void finishOthersActivity(Class<?> cls) {
        if (activityStack == null) {
            return;
        }

        for (Activity activity : activityStack) {
            if (!(activity.getClass().equals(cls))) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null) {
            return;
        }

        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public void resetApp(Class<?> cls, Class<?> clsMain) {
        finishOthersActivity(cls);
        if (activityStack != null) {
            if (activityStack.size() > 0) {
                Activity activity = activityStack.lastElement();
                if (activity != null) {
                    Intent it = new Intent();
                    it.setClass(activity, clsMain);
                    activity.startActivity(it);
                }
            } else {
                Intent it = new Intent();
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                it.setClass(BaseApplication.getInstance(), clsMain);
                BaseApplication.getInstance().startActivity(it);
            }
        }

    }

    public void resetApp(Class<?> cls) {
        finishAllActivity();
        Intent it = new Intent();
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        it.setClass(BaseApplication.getInstance(), cls);
        BaseApplication.getInstance().startActivity(it);
    }

    // @Deprecated
    // public void AppExit(Context cxt) {
    // appExit(cxt);
    // }

    /**
     * 应用程序退出
     */
    public void appExit(Context context) {
        try {
            finishAllActivity();
            Runtime.getRuntime().exit(0);
        } catch (Exception e) {
            Runtime.getRuntime().exit(-1);
        }
    }

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */

    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        for (String key : keySet) {
            destoryMap.get(key).finish();
        }
    }
}

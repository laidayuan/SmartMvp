package com.dada.marsframework.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * Created by laidayuan on 2018/3/27.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final Context mContext;
    // log文件的后缀名
    public static final String FILE_NAME_SUFFIX = "app_crash.log";
    private static CrashHandler sInstance = null;
    private Thread.UncaughtExceptionHandler oldUncaughtExceptionHandler;

    private CrashHandler(Context cxt) {
        // 将当前实例设为系统默认的异常处理器
        oldUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 获取Context，方便内部使用
        mContext = cxt;
    }

    public synchronized static CrashHandler create(Context cxt) {
        if (sInstance == null) {
            sInstance = new CrashHandler(cxt);
        }
        return sInstance;
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        LogUtils.e("uncaughtException ex = " + ex);

        // 导出异常信息到SD卡中
        try {
            //String crash = getCrashDetail(mContext, ex);
            //LogUtils.e("uncaughtException crash = " + crash);
            if (oldUncaughtExceptionHandler != null && ex != null) {
                oldUncaughtExceptionHandler.uncaughtException(thread, ex);
            }

            saveToSDCard(ex);
        } catch (Exception e) {
            LogUtils.e("uncaughtException e = " + e.toString());
        } finally {
            // ex.printStackTrace();// 调试时打印日志信息

            System.exit(0);
        }
    }

    public static String getCrashDetail(Context context, Throwable ex) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        StringBuilder sb = new StringBuilder();
        sb.append("App版本(Version): ");
        sb.append(pi.versionName);
        sb.append('_');
        sb.append(pi.versionCode);
        sb.append("\n");

        // android版本号
        sb.append("android版本号(OS Version): ");
        sb.append(Build.VERSION.RELEASE);
        sb.append("_");
        sb.append(Build.VERSION.SDK_INT);
        sb.append("\n");

        // 手机制造商
        sb.append("制造商(Vendor): ");
        sb.append(Build.MANUFACTURER);
        sb.append("\n");

        // 手机型号
        sb.append("型号(Model): ");
        sb.append(Build.MODEL);
        sb.append("\n");
        // cpu架构
        sb.append("cpu架构(CPU ABI): ");
        sb.append(Build.CPU_ABI);
        sb.append("\n");

        sb.append("错误详情: ");
        sb.append(ex);
        sb.append("\n");
        sb.append(ex.getMessage());
        sb.append("\n");
        if (ex == null) {
            return sb.toString();
        }

        Throwable cause = ex;
        while (cause.getCause() != null) {
            //cause.printStackTrace();
            cause = cause.getCause();
        }

        StackTraceElement elements[] = cause.getStackTrace();
        if (elements == null) {
            return sb.toString();
        }

        for (StackTraceElement element : elements) {
            String info = generateTraceTag(element);
            sb.append(info);
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String printStackTrace(StackTraceElement elements[]) {
        StringBuilder sb = new StringBuilder();
        if (elements == null) {
            return sb.toString();
        }

        for (StackTraceElement element : elements) {
            String info = generateTraceTag(element);
            sb.append(info);
            sb.append("\n");
        }

        return sb.toString();
    }


    private String saveToSDCard(Throwable ex) throws Exception {
        if (ex == null) {
            return null;
        }

        File file = FileUtils.getLogFile(""+System.currentTimeMillis() + ".txt");
        boolean append = false;
        if (System.currentTimeMillis() - file.lastModified() > 5000) {
            append = true;
        }

        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
        // 导出发生异常的时间
        pw.println(TimeUtils.formatNow());

        // 导出手机信息
        dumpPhoneInfo(pw);
        pw.println();

        // 导出异常的调用栈信息
        ex.printStackTrace(pw);
        pw.println();

        // 导出调用的类，方法的信息
        dumpTraceInfo(ex, pw);

        String result = pw.toString();
        pw.close();

        return result;
    }


    public static String generateTraceTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d) ---> "; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        String methodName = caller.getMethodName();
        StringBuilder sb = new StringBuilder();

        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, methodName, caller.getLineNumber()); // 替换

        return tag;
    }


    private void dumpTraceInfo(Throwable ex, PrintWriter pw) throws PackageManager.NameNotFoundException {
        StackTraceElement elements[] = ex.getStackTrace();
        for (StackTraceElement element : elements) {
            String info = generateTraceTag(element);
            pw.println(info);
            pw.println();
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        // 应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi =
                pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);
        pw.println();

        // android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        pw.println();

        // 手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        pw.println();

        // 手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
        pw.println();

        // cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
        pw.println();
    }
}

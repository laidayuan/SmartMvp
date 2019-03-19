package com.dada.marsframework.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by laidayuan on 2018/10/21.
 */

public class ViewUtils {
    /**
     * 截图
     *
     * @param v
     *            需要进行截图的控件
     * @return 该控件截图的Bitmap对象。
     */
    public static Bitmap captureView(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        return v.getDrawingCache();
    }

    /**
     * 创建快捷方式
     *
     * @param cxt
     *            Context
     * @param icon
     *            快捷方式图标
     * @param title
     *            快捷方式标题
     * @param cls
     *            要启动的类
     */
    public void createDeskShortCut(Context cxt, int icon, String title,
                                   Class<?> cls) {
        // 创建快捷方式的Intent
        Intent shortcutIntent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        // 快捷图片
        Parcelable ico = Intent.ShortcutIconResource.fromContext(
                cxt.getApplicationContext(), icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ico);
        Intent intent = new Intent(cxt, cls);
        // 下面两个属性是为了当应用程序卸载时桌面上的快捷方式会删除
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        cxt.sendBroadcast(shortcutIntent);
    }


    public static void customTextColor(TextView view, int color, int start, int end) {

        if (view == null || view.getText() == null || start < 0 || end > view.getText().length()) {
            return;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(view.getText());

        ForegroundColorSpan valueSpan = new ForegroundColorSpan(color);

        builder.setSpan(valueSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        view.setText(builder);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getViewById(View view, int viewId) {

        return (T) view.findViewById(viewId);
    }

    /**
     *  SparseArray这个类，优化过的存储integer和object键值对的hashmap
     *  只需静态调用getViewFromHolder这个方法填入当前Adapter的 convertView 与 子控件的id,就可以实现复用。
     *
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getViewFromHolder(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }

        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }

        return (T) childView;
    }


    public static boolean setTextViewById(View view, int viewId, String text) {
        if (view == null || StringUtils.isEmpty(text)) {
            return false;
        }

        TextView textView = getViewById(view, viewId);
        setTextView(textView, text);

        return true;
    }

    public static boolean setTextView(TextView textView, String text) {
        if (textView == null || StringUtils.isEmpty(text)) {
            return false;
        }

        textView.setText(text);

        return true;
    }

    public static void customTextSize(TextView view, float size, int start,
                                      int end) {

        if (view == null || view.getText() == null || start < 0
                || end > view.getText().length()) {
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(
                view.getText());
        RelativeSizeSpan valueSpan = new RelativeSizeSpan(size);
        builder.setSpan(valueSpan, start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(builder);
    }
}

package com.dada.marsframework.widget;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.dada.marsframework.R;


/**
 * Created by laidayuan on 2018/3/23.
 */

public class CustomPopupDialog {
    private Context mContext;
    private AlertDialog mDialog;
    private LayoutParams layoutParams;

    public CustomPopupDialog(Context context, int layout, boolean bottom) {
        mContext = context;
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(context).create();
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();
            initView(layout, bottom);
        }

    }


    public CustomPopupDialog(Context context, View layout, boolean bottom) {
        mContext = context;
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(context).create();
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();
            mDialog.setContentView(layout);
            initUI(bottom);
        }

    }

    public void setAsSystemAlert() {

        mDialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    public void initView(int nRes, boolean bottom) {
        mDialog.setContentView(nRes);
        initUI(bottom);
    }

    public void setRateWidth(float rateW) {
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay();
        layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.width = (int) (d.getWidth() * rateW);
        mDialog.getWindow().setAttributes(layoutParams);
    }

    private void initUI(boolean bottom) {
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay();
        layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.width = (int) (d.getWidth() * 1);
        mDialog.getWindow().setAttributes(layoutParams);

        if (bottom) {
            mDialog.getWindow().setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
            mDialog.getWindow().setWindowAnimations(R.style.AnimBottomDialog);
        }

        mDialog.setCanceledOnTouchOutside(true);

    }

    public <T extends View> T getViewById(int resId) {
        if (mDialog != null) {
            return (T) mDialog.findViewById(resId);
        }

        return null;
    }

    public void setOnClickListener(int viewId, OnClickListener l) {
        mDialog.findViewById(viewId).setOnClickListener(l);
    }

    public void setCanceledOnTouchOutside(boolean bOutCancel) {
        mDialog.setCanceledOnTouchOutside(bOutCancel);
    }


    public void dismiss() {
        mDialog.dismiss();
    }

}

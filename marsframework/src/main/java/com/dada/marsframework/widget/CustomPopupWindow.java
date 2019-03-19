package com.dada.marsframework.widget;

/**
 * Created by laidayuan on 2018/3/23.
 */

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.dada.marsframework.R;


public class CustomPopupWindow extends PopupWindow {

    Context mContext;
    View rootView;

    public CustomPopupWindow(Context context, int layout) {
        mContext = context;
        rootView = View.inflate(mContext, layout, null);
        rootView.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.anim_fade_in));
        LinearLayout popup_layout = (LinearLayout) rootView.findViewById(R.id.popup_layout);
        if (popup_layout != null) {
            popup_layout.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_push_bottom_in));
        }

        View empty_view = rootView.findViewById(R.id.empty_view);
        if (empty_view != null) {
            empty_view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    dismiss();
                }

            });
        }


        setHeight(LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(rootView);

        update();

    }



    public CustomPopupWindow(Context context, View layout) {
        mContext = context;
        rootView = layout;
        rootView.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.anim_fade_in));
        LinearLayout popup_layout = (LinearLayout) rootView.findViewById(R.id.popup_layout);
        if (popup_layout != null) {
            popup_layout.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_push_bottom_in));
        }

        View empty_view = rootView.findViewById(R.id.empty_view);
        if (empty_view != null) {
            empty_view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    dismiss();
                }

            });
        }


        setHeight(LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(rootView);

        update();

    }

    public void show(View parent, boolean bottom, boolean width,boolean height) {

        setWidth(width?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT);
        setHeight(height?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT);
        showAtLocation(parent, bottom?Gravity.BOTTOM:Gravity.CENTER, 0, 0);
    }

    public void show(View parent,int off,boolean width){
        setWidth(width?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT);
        showAsDropDown(parent,off,0,0);
    }

    public void show(View parent,boolean width,int x,int y,int gravity) {
        setWidth(width?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT);
        showAsDropDown(parent,x,y,gravity);
    }

    public <T extends View> T getViewById(int resId) {
        if (rootView != null) {
            return (T) rootView.findViewById(resId);
        }

        return null;
    }

    public void setOnClickListener(int resId, OnClickListener l) {
        rootView.findViewById(resId).setOnClickListener(l);
    }

}
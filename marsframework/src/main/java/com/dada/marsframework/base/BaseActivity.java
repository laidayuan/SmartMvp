package com.dada.marsframework.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dada.marsframework.R;
import com.dada.marsframework.model.MessageEvent;
import com.dada.marsframework.mvp.BasePresenter;
import com.dada.marsframework.mvp.GenericHelper;
import com.dada.marsframework.utils.LogUtils;
import com.dada.marsframework.widget.actionbar.*;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;



/**
 * <pre>
 *     author: laidayuan
 *                                      ___           ___           ___         ___
 *         _____                       /  /\         /__/\         /__/|       /  /\
 *        /  /::\                     /  /::\        \  \:\       |  |:|      /  /:/
 *       /  /:/\:\    ___     ___    /  /:/\:\        \  \:\      |  |:|     /__/::\
 *      /  /:/~/::\  /__/\   /  /\  /  /:/~/::\   _____\__\:\   __|  |:|     \__\/\:\
 *     /__/:/ /:/\:| \  \:\ /  /:/ /__/:/ /:/\:\ /__/::::::::\ /__/\_|:|____    \  \:\
 *     \  \:\/:/~/:/  \  \:\  /:/  \  \:\/:/__\/ \  \:\~~\~~\/ \  \:\/:::::/     \__\:\
 *      \  \::/ /:/    \  \:\/:/    \  \::/       \  \:\  ~~~   \  \::/~~~~      /  /:/
 *       \  \:\/:/      \  \::/      \  \:\        \  \:\        \  \:\         /__/:/
 *        \  \::/        \__\/        \  \:\        \  \:\        \  \:\        \__\/
 *         \__\/                       \__\/         \__\/         \__\/
 *     blog  : http://***.com
 *     time  : 2019/3/7
 *     desc  : BaseActivity about initialization
 * </pre>
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {
    protected T presenter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detach();
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onBeforeCreate();
        try {
            presenter = GenericHelper.newPresenter(this);
            if (presenter != null) {
                presenter.attach();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
        }

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // 页面已被启用，但因内存不足页面被系统销毁过
            // page start when the page was destroyed by the system due to insufficient memory
            onBundleHandle(savedInstanceState);
        } else {
            // 第一次进入页面获取上个页面传递过来的数据
            // handle intent when first enter the page
            Intent intent = getIntent();
            if (intent != null) {
                onIntentHandle(intent);
            }
        }

        setContentView(getLayoutId());
        ButterKnife.bind(this);
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }

        mHandler = new MyHandler(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uiEvent(MessageEvent messageEvent) {


    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void backEvent(MessageEvent messageEvent) {


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            onIntentHandle(intent);
        }

    }

    public abstract int getLayoutId();

    public void onBeforeCreate() {

    }

    public void onIntentHandle(@NonNull Intent intent) {}


    public void onBundleHandle(@NonNull Bundle savedInstanceState) {

    }




    protected ActionBar actionBar;

    protected MyHandler mHandler;

    public class MyHandler extends Handler {
        WeakReference<BaseActivity> mActivityReference;

        public MyHandler(BaseActivity activity) {
            mActivityReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseActivity activity = mActivityReference.get();
            if (activity != null) {
                activity.handleMyMessage(msg);
            }
        }

    }

    public void handleMyMessage(Message msg) {

    }

    public ActionBar getMyActionBar() {
        if (actionBar == null) {
            View barView = bindView(R.id.actionbar);
            if (barView instanceof ActionBar) {
                actionBar = (ActionBar) barView;

            }

        }

        if (actionBar == null) {
            LogUtils.e("actionBar == null --- -----");
        }

        return actionBar;
    }

    protected <T extends View> T bindView(int nResId) {
        return (T) findViewById(nResId);
    }

    public void setBackAction() {
        if (getMyActionBar() != null) {
            getMyActionBar().setHomeAction(new BackAction());
        }
    }


    private class BackAction extends AbstractAction {
        public BackAction() {
            super(R.mipmap.ic_back);
        }

        @Override
        public void performAction(View view) {

            finish();
        }
    }

    public void setTitleText(String text) {
        if (getMyActionBar() != null && text != null) {
            getMyActionBar().setTitle(text);
        }
    }

    public void setTitleText(int text) {
        if (getMyActionBar() != null && text != 0) {
            getMyActionBar().setTitle(text);
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                );
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Return whether touch the view.
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
}

package com.dada.smartmvp;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.dada.marsframework.BuildConfig;
import com.dada.marsframework.base.BaseActivity;
import com.dada.marsframework.monitor.ThreadMonitor;
import com.dada.marsframework.monitor.util.LifecycleUtils;
import com.dada.marsframework.monitor.util.shake.ShakeManager;
import com.dada.marsframework.utils.FileUtils;
import com.dada.marsframework.utils.SystemUtils;
import com.dada.smartmvp.test.TestActivity;

import java.io.File;

import butterknife.BindView;

public class MainActivity extends BaseActivity<HomePresenter> implements HomeContract.View {

    @BindView(R.id.tv_hello)
    TextView tv_hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter.loadText();
        //registerDebugService();
        tv_hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, TestActivity.class);
                startActivity(it);
            }
        });

    }

    private void test() {
        SystemUtils.sleep(10);
    }

    private void registerDebugService() {
        // 下面这个写法是如果开启了minifyEnabled true，则编译时候下面代码会自动优化。
        // 因为BuildConfig.DEBUG编译期会自动被修改为
        // public static final boolean DEBUG = false;
        // 也就是说if后面的代码永远都不会被执行到，编译器会自动删除正常情况下跑不到的代码。
        // 同时如果所有调用LifecycleUtils和ShakeManager的地方都做了类似处理，
        // 那么LifecycleUtils和ShakeManager则永远不会访问到，也会导致这两个类被自动删除。
        // 下面写法实际效果就是，debug时候下面代码全部存在，LifecycleUtils和ShakeManager类也存在，
        // 但是在release的时候，下面代码，包括LifecycleUtils和ShakeManager类都会被删除。
        // 建议自己反编译release和debug生成的apk，实际对比看下效果。
        if (!BuildConfig.DEBUG) {
            return;
        }
        new LifecycleUtils.Builder()
                .build()
                .register();
        new ShakeManager(this).registerShakeDetector();
        ThreadMonitor.getInstance().install();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    public void setText(String text) {
        //TextView tv_hello = findViewById(R.id.tv_hello);

        tv_hello.setText(text);
    }
}

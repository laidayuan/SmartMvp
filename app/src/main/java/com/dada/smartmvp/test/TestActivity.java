package com.dada.smartmvp.test;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dada.marsframework.base.BaseActivity;
import com.dada.smartmvp.test.TestContract;
import com.dada.smartmvp.test.TestPresenter;
import com.dada.marsframework.utils.SwipeBackLayout;
import com.dada.smartmvp.R;
/**
 * Created by laidayuan on 2018/10/21.
 */

public class TestActivity extends BaseActivity<TestPresenter> implements TestContract.View {
    @Override
    public int getLayoutId() {

        return R.layout.activity_test;
    }

    @Override
    public void method() {

    }

    @Override
    public void setText() {

    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout.attachTo(this);

        presenter.loadText();
    }
}

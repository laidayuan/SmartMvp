package com.dada.smartmvp.retrfit;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dada.marsframework.base.BaseActivity;
import com.dada.marsframework.utils.SwipeBackLayout;
import com.dada.smartmvp.R;
import com.dada.smartmvp.retrfit.RetrfitContract;
import com.dada.smartmvp.retrfit.RetrfitPresenter;

public class RetrfitActivity extends BaseActivity<RetrfitPresenter> implements RetrfitContract.View {
    @Override
    public int getLayoutId() {

        return R.layout.activity_test;
    }


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout.attachTo(this);

        presenter.load();
    }

    @Override
    public void update() {

    }
}

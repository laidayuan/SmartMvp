package com.dada.smartmvp.test;

import com.dada.marsframework.model.ResponseModel;
import com.dada.marsframework.model.SuperModel;
import com.dada.marsframework.mvp.BasePresenter;
import com.dada.marsframework.utils.LogUtils;
import com.dada.smartmvp.model.TranslationModel;
import com.dada.smartmvp.test.TestContract;
import com.dada.smartmvp.test.TestModel;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by laidayuan on 2018/10/21.
 */

public class TestPresenter extends BasePresenter<TestContract.View, TestContract.Model> implements TestContract.Presenter {
    @Override
    public void method() {


    }

    @Override
    public void loadText() {
        DisposableObserver disposableObserver = new DisposableObserver<TranslationModel>() {//ui线程执行
            @Override
            public void onNext(TranslationModel responseModel) {
                if (responseModel != null) {
                    responseModel.show();
                } else {
                    LogUtils.e("responseModel == null");
                }
                view.setText();
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e("onError = " + e.toString());
            }
        };

        toSubscribe(model.getCall(), disposableObserver);

    }
}

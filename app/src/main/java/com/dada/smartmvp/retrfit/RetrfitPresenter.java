package com.dada.smartmvp.retrfit;

import com.dada.marsframework.model.ResponseModel;
import com.dada.marsframework.mvp.BasePresenter;
import com.dada.marsframework.utils.LogUtils;
import com.dada.smartmvp.model.TranslationModel;
import com.dada.smartmvp.retrfit.RetrfitContract;

import io.reactivex.observers.DisposableObserver;

public class RetrfitPresenter extends BasePresenter<RetrfitContract.View, RetrfitContract.Model> implements RetrfitContract.Presenter {
    @Override
    public void load() {
        DisposableObserver disposableObserver = new DisposableObserver<ResponseModel<TranslationModel>>() {//ui线程执行
            @Override
            public void onNext(ResponseModel<TranslationModel> responseModel) {
                if (responseModel != null) {
                    responseModel.getData().show();
                } else {
                    LogUtils.e("responseModel == null");
                }
                view.update();
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

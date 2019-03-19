package com.dada.smartmvp.test;

import com.dada.marsframework.model.ResponseModel;
import com.dada.marsframework.model.SuperModel;
import com.dada.marsframework.mvp.Contract;
import com.dada.smartmvp.model.TranslationModel;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.http.GET;

/**
 * Created by laidayuan on 2018/10/21.
 */

public class TestContract {
    interface View extends Contract.View {
        void method();

        void setText();
    }

    interface Presenter extends Contract.Presenter {
        void method();

        void loadText();
    }

    interface Model {
        @GET("test")
        Observable<ResponseModel<SuperModel>> test();

        @GET("ajax.php?a=fy&f=auto&t=auto&w=hello%20world")
        Observable<TranslationModel> getCall();
    }
}

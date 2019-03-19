package com.dada.smartmvp.retrfit;

import com.dada.marsframework.model.ResponseModel;
import com.dada.marsframework.model.SuperModel;
import com.dada.marsframework.mvp.Contract;
import com.dada.smartmvp.model.TranslationModel;

import io.reactivex.Observable;
import retrofit2.http.GET;

public class RetrfitContract {
    interface View extends Contract.View {
        void update();
    }

    interface Presenter extends Contract.Presenter {
        void load();
    }

    interface Model {
        @GET("ajax.php?a=fy&f=auto&t=auto&w=hello%20world")
        Observable<ResponseModel<TranslationModel>> getCall();
    }
}

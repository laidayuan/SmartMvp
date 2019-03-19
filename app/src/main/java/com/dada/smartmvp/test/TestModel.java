package com.dada.smartmvp.test;

import com.dada.marsframework.model.ResponseModel;
import com.dada.marsframework.model.SuperModel;
import com.dada.marsframework.network.BaseApi;
import com.dada.marsframework.network.RetrofitHelper;
import com.dada.smartmvp.test.TestContract;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.http.GET;

/**
 * Created by laidayuan on 2018/10/21.
 */

public interface TestModel extends TestContract.Model {

    @GET("test")
    Observable<ResponseModel<SuperModel>> test();

}

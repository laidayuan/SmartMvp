package com.dada.marsframework.network;

import com.dada.marsframework.base.FrameworkConfig;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private volatile static RetrofitHelper mRetrofitHelper;

    private Retrofit mRetrofit = null;

    private RetrofitHelper() {
        getRetrofit();
    }

    public static RetrofitHelper getInstance() {
        if (mRetrofitHelper == null) {
            synchronized (RetrofitHelper.class) {
                if (mRetrofitHelper == null) {
                    mRetrofitHelper = new RetrofitHelper();
                }
            }
        }

        return mRetrofitHelper;
    }


    private Retrofit getRxRetrofit(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(OkhttpManager.getInstance().getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();

        return retrofit;
    }

    public void changeHost(String host) {
        mRetrofit = getRxRetrofit(host);
    }

    private Retrofit getRxRetrofit() {
        return getRxRetrofit(FrameworkConfig.getHostUrl());
    }

    public Retrofit getRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = getRxRetrofit();
        }

        return mRetrofit;
    }

    public <T> T create(Class<T> service) {
        return getRetrofit().create(service);
    }



}

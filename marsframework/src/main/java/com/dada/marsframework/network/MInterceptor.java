package com.dada.marsframework.network;

/**
 * Created by laidayuan on 2018/1/30.
 */

import android.content.Context;


import com.dada.marsframework.base.BaseApplication;
import com.dada.marsframework.utils.NetUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 可以用okhttp的拦截器Interceptor来实现在本地修改响应头的内容,
 * 可以在里面判断wifi,4g,3g来实现不同的缓存策略。
 * Created by
 */

public class MInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        /*Request request = chain.request();
        //TODO 如果没有网络，则启用缓存 FORCE_CACHE
        Context context = BaseApplication.getInstance();
        if (!NetUtils.isConnected(context)) {
            request = request
                    .newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }

        Response originalResponse = chain.proceed(request);
        return originalResponse
                .newBuilder()
                //在这里生成新的响应并修改它的响应头
                .header("Cache-Control", "public,max-age=3600")
                .removeHeader("Pragma").build();*/
        return cacheHttp(chain);
    }

    private Response cacheHttp(Chain chain) throws IOException {
        CacheControl.Builder cacheBuilder = new CacheControl.Builder();

        cacheBuilder.maxAge(0, TimeUnit.SECONDS); //这个是控制缓存的最大生命时间

        cacheBuilder.maxStale(28, TimeUnit.DAYS);

        CacheControl cacheControl = cacheBuilder.build();

        Request  request = chain.request();

        Context context = BaseApplication.getInstance();
        if (!NetUtils.isConnected(context)) {

            request= request.newBuilder().cacheControl(cacheControl).build();
        }

        Response originalResponse = chain.proceed(request);
        if (NetUtils.isConnected(context)) {
             int maxAge = 30; //这个是控制缓存的最大生命时间 如果为0，则不读缓存
             return originalResponse.newBuilder()
                     .removeHeader("Pragma")
                     .header("Cache-Control", "public ,max-age=" +maxAge)
                     .build();

         } else {
             // 如果没有网络，则返回缓存未过期一个月的数据
             int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
             return originalResponse.newBuilder()
                     .removeHeader("Pragma")
                     .header("Cache-Control", "public, only-if-cached,max-stale=" + maxStale)
                     .build();
         }
    }

}

package com.dada.marsframework.network;

import com.dada.marsframework.utils.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                //.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Connection", "keep-alive")
                //.addHeader("Accept", "*/*")
                .method(originalRequest.method(), originalRequest.body());
        //requestBuilder.addHeader("Authorization", "Bearer " + BaseConstant.TOKEN);//添加请求头信息，服务器进行token有效性验证
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }


}
package com.dada.marsframework.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;

import com.dada.marsframework.monitor.FrameSkipMonitor;
import com.dada.marsframework.utils.FileUtils;
import com.dada.marsframework.utils.LogUtils;
import com.dada.marsframework.utils.StringUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Created by laidayuan on 2018/1/30.
 */


public class OkhttpManager {

    private static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType TYPE_FORM = MediaType.parse("text/html; charset=utf-8");
    private static final MediaType TYPE_PNG = MediaType.parse("image/jpg");
    private static int DEFAULT_TIMEOUT = 30;

    private OkHttpClient mClient;
    private volatile static OkhttpManager okhttpManager;
    private Picasso picasso;
    private OkHttpDownLoader okHttpDownLoader;

    private OkhttpManager() {
        if (mClient == null) {
            //https://www.cnblogs.com/Jason-Jan/p/8010795.html  具体设置
            MInterceptor interceptor = new MInterceptor();
            mClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(interceptor)
                    /*.authenticator(new Authenticator() { //token 验证
                        @javax.annotation.Nullable
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            //取出本地的refreshToken
                            String refreshToken = "sssgr122222222";

                            // 通过一个特定的接口获取新的token，此处要用到同步的retrofit请求
                            ApiService service = ServiceManager.getService(ApiService.class);
                            Call<String> call = service.refreshToken(refreshToken);

                            //要用retrofit的同步方式
                            String newToken = call.execute().body();

                            return response.request().newBuilder()
                                    .header("token", newToken)
                                    .build();
                        }
                    })*/
                    .addInterceptor(interceptor) //缓存
                    .addInterceptor(new LogInterceptor()) //日志
                    //.addInterceptor(new HeaderInterceptor()) //头部参数
                    .cache(new Cache(new File(FileUtils.getCachesPath()), 1024 * 1024 * 100))
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        }
    }

    public static OkhttpManager getInstance() {
        if (okhttpManager == null) {
            synchronized (OkhttpManager.class) {
                if (okhttpManager == null) {
                    okhttpManager = new OkhttpManager();
                }
            }
        }

        return okhttpManager;
    }

    public OkHttpClient getOkHttpClient() {
        return mClient;
    }

    public void clear() {
        if (mClient != null) {
            mClient.dispatcher().cancelAll();
        }
    }

    /**
     * 带参数的异步get请求
     *
     * @param url
     * @param params
     * @param mCallBack
     * @throws Exception
     */
    public void loadDataByGet(String url, Map params, final ICallBack mCallBack) throws Exception {
        loadDataByGet(propertURLStr(url, params), mCallBack);
    }


    public void loadDataByGet(String url, final ICallBack mCallBack) throws Exception {
        if (!StringUtils.isUrlValid(url)) {
            if (mCallBack != null) {
                LogUtils.e(url + " url is not available!");
                mCallBack.onFail(404, url + "url is not available!");
            }
            return;
        }

        Request request = new Request.Builder().url(url).build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e("yjl", "onFailure:" + e.getMessage());
                if (mCallBack != null) {
                    mCallBack.onFail(100000, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            String result = readInputStream(response.body().byteStream());
                            mCallBack.onSuccess(response.code(), result);
                        } catch (IOException e) {
                            mCallBack.onFail(response.code(), response.message());
                            e.printStackTrace();
                        }

                    }
                } else {
                    if (response != null) {
                        try {
                            String result = readInputStream(response.body().byteStream());
                            mCallBack.onSuccess(response.code(), result);
                        } catch (IOException e) {

                            mCallBack.onFail(response.code(), response.message());
                            e.printStackTrace();
                        }
                    }
                    //throw new IOException("Unexpected code " + response);
                }

                LogUtils.e("onResponse:" + response.message());
            }
        });
    }



    public void loadDataByPost(String url, String json, final ICallBack mCallBack) throws Exception {
        //RequestBody body = RequestBody.create(TYPE_JSON, json);
        if (!StringUtils.isUrlValid(url)) {
            LogUtils.e(url + " url is not available!");
            if (mCallBack != null) {
                mCallBack.onFail(404, url + "url is not available!");
            }
            return;
        }
        FormBody.Builder builder = new FormBody.Builder();
        if (json != null) {
            Map map = JSON.parseObject(json, Map.class);
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
                builder.add("" + entry.getKey(), "" + entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e("yjl", "onFailure:" + e.getMessage());
                if (mCallBack != null) {
                    mCallBack.onFail(100000, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            String result = readInputStream(response.body().byteStream());
                            mCallBack.onSuccess(response.code(), result);
                        } catch (IOException e) {
                            mCallBack.onFail(response.code(), response.message());
                            e.printStackTrace();
                        }

                    }
                } else {
                    if (response != null) {
                        try {
                            String result = readInputStream(response.body().byteStream());
                            mCallBack.onSuccess(response.code(), result);
                        } catch (IOException e) {
                            mCallBack.onFail(response.code(), response.message());
                            e.printStackTrace();
                        }
                    }
                }

                LogUtils.e("onResponse:" + response.message());
            }
        });
    }


    public void loadDataByPost(final String url, Map param, final ICallBack mCallBack) throws Exception {
        LogUtils.d("url:" + url);
        LogUtils.d("param:" + param);
        if (!StringUtils.isUrlValid(url)) {
            if (mCallBack != null) {
                LogUtils.e(url + " url is not available!");
                mCallBack.onFail(404, url + "url is not available!");
            }
            return;
        }
        FormBody.Builder builder = new FormBody.Builder();
        //Map map = JSON.parseObject(json, Map.class);
        if (param != null) {
            Iterator it = param.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
                builder.add("" + entry.getKey(), "" + entry.getValue());
            }
        }

        final Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                LogUtils.e("yjl", "onFailure:" + e.getMessage());
                if (mCallBack != null) {
                    mCallBack.onFail(100000, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                LogUtils.e("onResponse:" + response.message());
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            String result = readInputStream(response.body().byteStream());
                            mCallBack.onSuccess(response.code(), result);
                        } catch (IOException e) {
                            mCallBack.onFail(response.code(), response.message());
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (response != null) {
                        try {
                            String result = readInputStream(response.body().byteStream());
                            mCallBack.onSuccess(response.code(), result);
                        } catch (IOException e) {
                            mCallBack.onFail(response.code(), response.message());
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 头像上传
     *
     * @param url
     * @param param
     * @param mCallBack
     * @throws Exception
     */

    public void postPic(String url, Map param, final ICallBack mCallBack) throws Exception {
        MultipartBody.Builder mBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (param != null) {
            String picUrl = param.get("filedata").toString();
            File picFile = new File(picUrl);
            if (picFile.exists()) {
                mBody.addFormDataPart("pic", picFile.getName(), RequestBody.create(
                        TYPE_PNG, picFile));
            }

            FormBody.Builder builder = new FormBody.Builder();
            Iterator it = param.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
                mBody.addFormDataPart("" + entry.getKey(), "" + entry.getValue());
            }

            final Request request = new Request.Builder()
                    .url(url)
                    .post(mBody.build())
                    .build();
            Call call = mClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (mCallBack != null) {
                        mCallBack.onFail(404, e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                String result = readInputStream(response.body().byteStream());
                                mCallBack.onSuccess(response.code(), result);
                            } catch (IOException e) {
                                mCallBack.onFail(response.code(), response.message());
                                e.printStackTrace();
                            }


                        }
                    } else {
                        if (response != null) {
                            try {
                                String result = readInputStream(response.body().byteStream());
                                mCallBack.onSuccess(response.code(), result);
                            } catch (IOException e) {
                                mCallBack.onFail(response.code(), response.message());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }


    }

    /**
     * 从输入流中读取数据
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    private static synchronized String readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }

        String data = outStream.toString("UTF-8");
        outStream.close();
        inStream.close();

        return data;
    }


    public String propertURLStr(String url, Map<String, String> queries) throws Exception {
        return propertURL(url, queries).toString();
    }

    /**
     * 配置url 格式
     *
     * @param url
     * @param queries
     * @return
     */
    public URL propertURL(String url, Map<String, String> queries) {
        StringBuilder sb = new StringBuilder();
        URL u = null;
        sb.append(url);

        if (queries != null && queries.size() > 0) {
            if (!url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue())
                        .append("&");
            }
            sb.setLength(sb.length() - 1);
        }

        try {
            u = new URL(sb.toString());
        } catch (MalformedURLException e) {
        }

        return u;
    }

    /**
     * 使用Picasso +okhttp3 实现的图片加载
     * 也可以使用image util加载图片
     *
     * @param mContext     context
     * @param url          图片URL
     * @param view         显示图片的imageView
     * @param defaultImage 下载过程中显示的图片
     * @param failImage    下载失败显示的图片
     * @throws Exception
     */
    public void loadImage(Context mContext, String url, ImageView view, int defaultImage, int failImage) throws Exception {
        if (!StringUtils.isUrlValid(url)) {
            LogUtils.e(url + " url is not available!");
            return;
        }
        if (okHttpDownLoader == null) {
            okHttpDownLoader = new OkHttpDownLoader(mClient);
        }
        if (picasso == null) {
            picasso = new Picasso.Builder(mContext)
                    .downloader(new OkHttpDownLoader(mClient))
                    .build();
            Picasso.setSingletonInstance(picasso);
        }

        RequestCreator requestCreator = Picasso.with(mContext).load(url);
        if (defaultImage > 0) {
            requestCreator.placeholder(defaultImage);
        }
        if (failImage > 0) {
            requestCreator.placeholder(failImage);
        }
        requestCreator.into(view);
//        Picasso.with(mContext)
//                //load()下载图片
//                .load(url)
//
//                //下载中显示的图片
//                .placeholder(defaultImage)
//
//                //下载失败显示的图片
//                .error(failImage)
//
//                //init()显示到指定控件
//                .into(view);

    }

    /**
     * 加载图拍，显示默认图片
     *
     * @param mContext
     * @param url
     * @param view
     * @param defaultImage
     * @throws Exception
     */
    public void loadImage(Context mContext, String url, ImageView view, int defaultImage) throws Exception {
        loadImage(mContext, url, view, defaultImage, defaultImage);
    }

    public String requestSync(String url, Map params) {
        try {
            Request request = null;
            if (params != null) {
                String content = JSON.toJSONString(params);
                MediaType contentType = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(contentType, content);

                request = new Request.Builder()
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("User-Agent", "MSIE")
                        .addHeader("Content-Length", "content_length")
                        .addHeader("Connection", "close")
                        .url(url)
                        .post(body)
                        //.method("post", null)
                        .build();
            } else {
                request = new Request.Builder()
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("User-Agent", "MSIE")
                        .addHeader("Content-Length", "content_length")
                        .addHeader("Connection", "close")
                        .url(url)
                        //.method("post", null)
                        .build();
            }

            String method = request.method();
            Response response = mClient.newCall(request).execute();
            if (response!=null && response.isSuccessful()) {
                String result = readInputStream(response.body().byteStream());

                return result;
            } else {
                LogUtils.d("request fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
        }

        return null;
    }


    public String requestSyncEx(String url, Map params) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                Iterator it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
                    builder.add("" + entry.getKey(), "" + entry.getValue());
                }
            }

            Request request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();

            Response response = mClient.newCall(request).execute();
            if (response!=null && response.isSuccessful()) {
                String result = readInputStream(response.body().byteStream());

                return result;
            } else {
                LogUtils.d("request fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
        }

        return null;
    }

    public Bitmap requestImageSync(String url) {
        if (!StringUtils.isUrlValid(url)) {
            LogUtils.e("invalidate url : " + url);
            return null;
        }

        try {
            Request request = new Request.Builder().url(url).build();
            Response response = mClient.newCall(request).execute();
            if (response!=null&&response.isSuccessful()) {
                byte[] bytes = response.body().bytes();

                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean requestFileSync(String url, Map params, String savePath, final ICallBack mCallBack) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                Iterator it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
                    builder.add("" + entry.getKey(), "" + entry.getValue());
                }
            }

            Request request = new Request.Builder()
                    .url(url).get()
                    //.post(builder.build())
                    .build();
            Response response = mClient.newCall(request).execute();
            if (response!=null && response.isSuccessful()) {
                ResponseBody body = response.body();
                long total = body.contentLength();
                File file = new File(savePath);
                is = body.byteStream();
                byte[] buf = new byte[2048];
                int len = 0;
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 100.0f / total);
                    if (mCallBack != null) {
                        mCallBack.onProgress(progress);
                    }
                }

                if (mCallBack != null) {
                    mCallBack.onSuccess(200, "ok");
                }

                fos.flush();

                return true;
            } else {
                LogUtils.d("request fail");
                if (mCallBack != null) {
                    mCallBack.onFail(0, "response fail");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
            if (mCallBack != null) {
                mCallBack.onFail(0, e.toString());
            }
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }

        return false;
    }




}

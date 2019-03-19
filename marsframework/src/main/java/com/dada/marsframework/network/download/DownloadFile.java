package com.dada.marsframework.network.download;

import com.dada.marsframework.model.ResponseModel;
import com.dada.marsframework.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 下载文件，监听进度
 */
public class DownloadFile {

    private static final String TAG = "DownloadFile";
    private static final int DEFAULT_TIMEOUT = 15;
    public Retrofit retrofit;


    public DownloadFile(String url, DownloadProgressListener listener) {

        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public void download(String url, DisposableObserver disposableObserver) {

//        retrofit.create(DownloadService.class)
//                .download(url)
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .map(new Func1<ResponseBody, InputStream>() {
//                    @Override
//                    public InputStream call(ResponseBody responseBody) {
//                        return responseBody.byteStream();
//                    }
//                })
//                .observeOn(Schedulers.computation())
//                .doOnNext(new Action1<InputStream>() {
//                    @Override
//                    public void call(InputStream inputStream) {
//                        try {
//                            FileUtils.writeFile(inputStream, file);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(null);

        Observable<ResponseBody> observable = retrofit.create(DownloadService.class).download(url);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
    }




    public static boolean writeResponseBodyToDisk(ResponseBody body, String path) {
        try {
            File futureStudioIconFile = new File(path);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    LogUtils.d("file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public interface DownloadService {
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String url);
    }
}

package com.dada.marsframework.network.upload;

import com.dada.marsframework.model.ResponseModel;
import com.dada.marsframework.utils.LogUtils;

import io.reactivex.observers.DefaultObserver;

public abstract class FileUploadObserver<T> extends DefaultObserver<T> {

    @Override
    public void onNext(T t) {
        onUpLoadSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onUpLoadFail(e);
    }

    @Override
    public void onComplete() {

    }
    //监听进度的改变
    public void onProgressChange(long bytesWritten, long contentLength) {
        onProgress((int) (bytesWritten*100 / contentLength));
    }

    //上传成功的回调
    public abstract void onUpLoadSuccess(T t);

    //上传失败回调
    public abstract void onUpLoadFail(Throwable e);

    //上传进度回调
    public abstract void onProgress(int progress);

}

/*
    FileUploadObserver<ResponseBody> fileUploadObserver = new FileUploadObserver<ResponseBody>() {
        @Override
        public void onUpLoadSuccess(ResponseBody responseBody) {
            //上传成功
        }

        @Override
        public void onUpLoadFail(Throwable e) {
            //上传失败
        }

        @Override
        public void onProgress(int progress) {
            //progress 0-100
            //dialog.setProgress(progress);
        }

        @Override
        public void onNext(ResponseBody responseBody) {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Throwable e) {

        }
    }; */

    //UploadFileRequestBody uploadFileRequestBody = new UploadFileRequestBody(file, fileUploadObserver);
    //MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), uploadFileRequestBody);


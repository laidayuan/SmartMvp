package com.dada.marsframework.utils;

import android.Manifest;
import android.app.Activity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by laidayuan on 2018/2/11.
 */

public class PermissionHelper {

    public interface ICallBack {
        public void onAction(int status);

        public void onError(String error);

        public void OnComplete();
    }

    public static void requestPermission(Activity activity, final ICallBack callback, String... permissions) {
        if (activity == null) return;
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.requestEach(permissions).subscribe(new Consumer<Permission>() {
                                                                    @Override
                                                                    public void accept(Permission permission) {
                                                                        LogUtils.e("Permission result " + permission);
                                                                        if (permission.granted) {
                                                                            if (callback != null) {
                                                                                callback.onAction(0);
                                                                            }
                                                                        } else if (permission.shouldShowRequestPermissionRationale) {
                                                                            if (callback != null) {
                                                                                callback.onAction(1);
                                                                            }
                                                                        } else {
                                                                            if (callback != null) {
                                                                                callback.onAction(1);
                                                                            }
                                                                        }
                                                                    }
                                                                }, new Consumer<Throwable>() {
                                                                    @Override
                                                                    public void accept(Throwable t) {
                                                                        LogUtils.e("onError", t.toString());
                                                                        if (callback != null) {
                                                                            callback.onError(t.toString());
                                                                        }
                                                                    }
                                                                },
                new Action() {
                    @Override
                    public void run() {
                        LogUtils.e("OnComplete");
                        if (callback != null) {
                            callback.OnComplete();
                        }
                    }
                });
    }
}

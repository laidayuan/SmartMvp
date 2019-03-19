package com.dada.marsframework.mvp;

import com.dada.marsframework.network.BaseApi;
import com.dada.marsframework.network.RetrofitHelper;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by laidayuan on 2018/10/21.
 */

public class BasePresenter <T extends Contract.View, E> implements Contract.Presenter {
    public static final int RETRY_COUNT = 0;

    protected T view;
    protected E model;

    private CompositeDisposable mCompositeDisposable = null;

//    public void init(Object view, Object model) {
//        this.view = (T) view;
//        this.model = (E) model;
//    }

    public void init(Object view, Class<E> model) {
        this.view = (T) view;
        this.model = RetrofitHelper.getInstance().getRetrofit().create(model);
    }

    /**
     * 当Activity的onCreate或Fragment的onAttach方法执行时将会调用 <br />
     * call this method when Activity's onCreate or Fragment's onAttach method called
     */
    public void attach() {}

    /**
     * 当onDestroy或onDetach方法执行时将会调用 <br />
     * call this method when Activity's onDestroy or Fragment's onDetach method called
     */
    public void detach() {
        view = null;
        model = null;

        cancel();
    }

    public void cancel() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    protected T getView() {
        return view;
    }

    protected E getModel() {
        return model;
    }

    protected CompositeDisposable getCompositeDisposable() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }

        return mCompositeDisposable;
    }

    /**
     * view是否还存在 <br />
     * whether view exist
     */
    public boolean isAttachView()
    {
        return view != null;
    }

    public void register(Disposable disposable) {
        if (disposable != null) {
            getCompositeDisposable().add(disposable);
        }
    }

    /**
     * 设置订阅 和 所在的线程环境
     */
    public <T> void toSubscribe(Observable<T> o, DisposableObserver<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(RETRY_COUNT)//请求失败重连次数
                .subscribe(s);
                //.subscribeWith(s);
        register(s);

    }
}

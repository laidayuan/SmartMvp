package com.dada.smartmvp;

import com.dada.marsframework.mvp.BasePresenter;

/**
 * Created by laidayuan on 2018/10/21.
 */

public class HomePresenter extends BasePresenter<HomeContract.View, HomeModel> implements HomeContract.Presenter {

    public void loadText() {
        String text = getModel().loadText();
        getView().setText(text);
    }
}

package com.dada.smartmvp.model;

import com.dada.marsframework.model.SuperModel;
import com.dada.marsframework.utils.LogUtils;

public class TranslationModel extends SuperModel {

    private String from;
    private String to;
    private String vendor;
    private String out;
    private int errNo;

    //定义 输出返回数据 的方法
    public void show() {
        LogUtils.e(""+from);
        LogUtils.e(""+to);
        LogUtils.e(""+vendor);
        LogUtils.e(""+out);
        LogUtils.e(""+errNo);
    }

}

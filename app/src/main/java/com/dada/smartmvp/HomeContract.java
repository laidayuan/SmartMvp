package com.dada.smartmvp;

import android.app.Activity;

import java.util.List;
import com.dada.marsframework.mvp.Contract;

/**
 * Created by laidayuan on 2018/10/21.
 */


public interface HomeContract {
    interface Presenter extends Contract.Presenter{
        public void loadText();
    }

    interface View extends Contract.View{
        public void setText(String text);

    }

    interface Model extends Contract.Model {
        public String loadText();
    }
}

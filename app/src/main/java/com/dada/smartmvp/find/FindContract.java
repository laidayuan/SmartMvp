package com.dada.smartmvp.find;

import com.dada.marsframework.mvp.Contract;

/**
 * Created by laidayuan on 2018/10/22.
 */

public class FindContract {
    interface View extends Contract.View {
        void method();
    }

    interface Presenter extends Contract.Presenter {
        void method();
    }

    interface Model extends Contract.Model {
        void method();
    }
}

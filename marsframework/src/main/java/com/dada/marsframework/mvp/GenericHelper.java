package com.dada.marsframework.mvp;

/**
 * Created by laidayuan on 2018/10/21.
 */


import com.dada.marsframework.utils.LogUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class GenericHelper {

    public static <T> Class<T> getGenericClass(Class<?> klass, Class<?> filterClass) {
        Type type = klass.getGenericSuperclass();
        if (type == null || !(type instanceof ParameterizedType)) return null;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        for (Type t : types) {
            Class<T> tClass = (Class<T>) t;
            if (filterClass.isAssignableFrom(tClass)) {
                return tClass;
            }
        }

        return null;
//        if(types == null || types.length == 0) return null;
//        return (Class<T>) types[0];
    }

    public static <T> Class<T> getGenericInterface(Class<?> klass, String filter) {
        Type type = klass.getGenericSuperclass();
        if (type == null || !(type instanceof ParameterizedType)) return null;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        for (Type t : types) {
            Class<T> tClass = (Class<T>) t;
            if (tClass.getSimpleName().contains(filter)) {
                return tClass;
            }
        }

        return null;
//        if(types == null || types.length == 0) return null;
//        return (Class<T>) types[0];
    }


    private static Class<?> getViewClass(Class<?> klass, Class<?> filterClass) {
        for (Class c : klass.getInterfaces()) {
            if (filterClass.isAssignableFrom(c)) return klass;
        }

        return getViewClass(klass.getSuperclass(), filterClass);
    }


    public static <T> T newPresenter(Object obj) {
        if (!Contract.View.class.isInstance(obj)) {
            throw new RuntimeException("no implement Contract.BaseView");
        }
        try {
            Class<?> currentClass = obj.getClass();
            Class<?> viewClass = getViewClass(currentClass, Contract.View.class);
            Class<?> presenterClass = getGenericClass(viewClass, Contract.Presenter.class);
            //Class<?> presenterClass = getGenericClass(currentClass, Contract.Presenter.class);
            Class modelClass = getGenericInterface(presenterClass, "Model");
//            Constructor construct = presenterClass.getConstructor(viewClass, modelClass);
            BasePresenter<?, ?> basePresenter = (BasePresenter<?, ?>) presenterClass.newInstance();
            //basePresenter.init(obj, modelClass.newInstance());
            basePresenter.init(obj, modelClass);
            return (T) basePresenter;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
        }

        throw new RuntimeException("instance of presenter fail\n" +
                " Remind presenter need to extends BasePresenter");
    }

}
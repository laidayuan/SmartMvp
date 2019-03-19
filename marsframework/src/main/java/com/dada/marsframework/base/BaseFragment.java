package com.dada.marsframework.base;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dada.marsframework.model.MessageEvent;
import com.dada.marsframework.mvp.BasePresenter;
import com.dada.marsframework.mvp.GenericHelper;
import com.dada.marsframework.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by laidayuan on 2018/10/21.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    protected T presenter;

    private View rootView;

    /**
     * Fragment是否可见状态 <br /> whether the Fragment is visible
     */
    private boolean isFragmentVisible;

    /**
     * Layout已经初始化完成 <br /> Layout has been initialized
     */
    private boolean isPrepared;

    /**
     * 是否第一次加载       <br /> whether first load
     */
    private boolean isFirstLoad = true;

    /**
     * <pre>
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     * 一般用于PagerAdapter需要刷新各个子Fragment的场景
     * 不要new 新的 PagerAdapter 而采取reset数据的方式
     * 所以要求Fragment重新走initData方法
     * 故使用 {@link #setForceLoad(boolean)}来让Fragment下次执行initData
     * </pre>
     *
     * force load
     * <pre>
     *     ignore isFirstLoad value, but still keep Visible & Prepared.
     *     use this when PagerAdapter need refresh multiple Fragment.
     *
     * </pre>
     */
    private boolean forceLoad = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            presenter = GenericHelper.newPresenter(this);
            if (presenter != null) {
                presenter.attach();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        onBeforeCreate();
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            onBundleHandle(savedInstanceState);
        }

        Bundle bundle = getArguments();
        if (bundle != null && bundle.size() > 0) {
            onArgumentsHandle(bundle);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        isFirstLoad = true;
        isPrepared = true;
        if (!isInViewPager()) {
            isFragmentVisible = true;
        }

        onInit();
        lazyLoad();
        return rootView;
    }

    /***
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint <br /> If used with ViewPager, the call to setUserVisibleHint
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged. 此时您因该让 {@link #isInViewPager()} 返回false <br />
     * If use by FragmentTransaction show or hide, the call to onHiddenChanged. now you need return false in {@link #isInViewPager()}
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     * visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onInvisible();
        } else {
            onVisible();
        }
    }

    public void onVisible() {
        isFragmentVisible = true;
        lazyLoad();
    }

    public void onInvisible() {
        isFragmentVisible = false;
    }


    public void onBeforeCreate() { }



    public void onBundleHandle(@NonNull Bundle savedInstanceState) { }

    public void onArgumentsHandle(@NonNull Bundle bundle) { }

    public void onLazyLoad() { }



    /**
     * 在这里面进行初始化
     */
    protected void onInit() {
        ButterKnife.bind(this, getRootView());
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }


    /**
     * 获取布局的id

     */
    protected abstract int getLayoutId();

    public View getRootView() {
        return this.rootView;
    }

    private void lazyLoad() {
        if (isPrepared() && isFragmentVisible()) {
            if (isForceLoad() || isFirstLoad()) {
                forceLoad = false;
                isFirstLoad = false;
                onLazyLoad();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrepared = false;

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        //ButterKnife.bind(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.detach();
        }
    }

    public void setForceLoad(boolean forceLoad) {
        this.forceLoad = forceLoad;
    }


    public boolean isForceLoad() {
        return forceLoad;
    }


    public boolean isPrepared() {
        return isPrepared;
    }


    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    public boolean isFragmentVisible() {
        return isFragmentVisible;
    }

    public boolean isInViewPager() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uiEvent(MessageEvent messageEvent) {


    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void backEvent(MessageEvent messageEvent) {


    }

}

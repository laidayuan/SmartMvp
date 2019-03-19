package com.dada.marsframework.base;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dada.marsframework.mvp.BasePresenter;
import com.dada.marsframework.utils.StringUtils;
import com.dada.marsframework.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laidayuan on 2018/10/21.
 */

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    public Activity mContext;
    protected LayoutInflater mInflater;
    protected ArrayList<T> mDataList;

    private BasePresenter presenter;
    public static final int START_VIEWID = 99000000;

    public BaseAdapter(Activity context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mDataList = new ArrayList<T>();
    }


    public void setPresenter(BasePresenter presenter) {
        this.presenter = presenter;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void addDataList(List<T> list) {
        if (list != null && list.size() > 0) {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }

    }

    public void addItem(T item) {
        if (item != null) {
            mDataList.add(item);
            notifyDataSetChanged();
        }

    }

    public void addHeadList(List<T> list) {
        if (list != null && list.size() > 0) {
            mDataList.addAll(0, list);
            notifyDataSetChanged();
        }

    }

    public void remove(int pos) {
        if (mDataList != null && mDataList.size() > pos) {
            // Object object = getItem(pos);
            // mDataList.remove(object);
            mDataList.remove(pos);
            notifyDataSetChanged();
        }

    }

    public void clear(boolean bRefresh) {
        if (mDataList != null) {
            mDataList.clear();
            if (bRefresh) {
                notifyDataSetChanged();
            }
        }
    }

    public void clear() {
        clear(true);

    }

    @Override
    public int getCount() {
        if (mDataList == null) {
            return 0;
        } else {
            return mDataList.size();
        }
    }

    @Override
    public T getItem(int pos) {
        if (mDataList == null || pos >= mDataList.size()) {
            return null;
        } else {
            return mDataList.get(pos);
        }
    }

    @Override
    public long getItemId(int pos) {
        // TODO Auto-generated method stub
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // SparseArray<View> holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    getViewLayout(), null);
        }

        updateView(position, convertView, false);

        return convertView;
    }

    public abstract void updateView(int pos, View convertView, boolean bOut);

    public abstract int getViewLayout();

    public void loadImage(ImageView imageView, String url, int nDeafultImage) {
        if (presenter != null && imageView != null) {
            imageView.setTag(url);
            //presenter.loadImageView(mContext, imageView, url, nDeafultImage);
        }
    }

    public static void setTextView(View convertView, int viewId, String text) {
        TextView textView = ViewUtils.getViewFromHolder(convertView, viewId);
        if (textView != null) {
            textView.setText(text);
        }

    }

    public void setImageView(View convertView, int viewId, String url, int nDeafultImage) {
        ImageView imageView = ViewUtils.getViewFromHolder(convertView, viewId);
        if (imageView != null) {

            if (StringUtils.isNotEmpty(url)) {
                loadImage(imageView, url, nDeafultImage);
            } else {
                imageView.setImageResource(nDeafultImage);
            }

        }
    }

    public static void setImageView(View convertView, int viewId,
                                    int nDeafultImage) {
        ImageView imageView = ViewUtils.getViewFromHolder(convertView, viewId);
        if (imageView != null) {

            imageView.setImageResource(nDeafultImage);
        }
    }

    public static void hideView(View convertView, int viewId, boolean bHide) {
        View view = ViewUtils.getViewFromHolder(convertView, viewId);
        if (view != null) {

            view.setVisibility(bHide ? View.GONE : View.VISIBLE);
        }
    }


}


package com.dada.marsframework.widget;

/**
 * Created by laidayuan on 2018/3/23.
 */

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dada.marsframework.R;


public class CustomHorizontalScrollView extends HorizontalScrollView implements
        OnClickListener {
    private Context mContext;

    private LinearLayout contentLayout;
    private OnItemListener mOnItemListener = null;
    private int totalIndexs;

    public static final int START_POS = 10000;

    private int titleColorNor = 0xff888888;
    private int titleColorSel = 0xffffffff;


    public interface OnItemListener {
        public void onItemClick(View view, int pos);
        public boolean onItemCheck(View view, boolean isCheck);
    }


    public void setTitleColorNor(int color) {
        titleColorNor = color;
    }

    public void setTitleColorSel(int color) {
        titleColorSel = color;
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    public void setOnItemListener(OnItemListener l) {

        this.mOnItemListener = l;
    }

    public CustomHorizontalScrollView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    LayoutInflater mInflate;

    private void init() {
        mInflate = LayoutInflater.from(mContext);
        contentLayout = (LinearLayout) mInflate.inflate(
                R.layout.layout_horizontal_scroll, null);

        contentLayout.setBackgroundResource(R.color.full_transparent);
        addView(contentLayout);

    }

    @SuppressLint("ResourceAsColor")
    public void initWidget(List<String> titles) {

        clearViews();

        totalIndexs = titles.size();

        for (int i = 0; i < totalIndexs; i++) {

            if (!TextUtils.isEmpty(titles.get(i))) {

                LinearLayout view = (LinearLayout) mInflate.inflate(
                        R.layout.layout_tab_button, null);
                // button.setBackgroundResource(titles[i]);
                TextView text = (TextView) view.findViewById(R.id.text);
                text.setText(titles.get(i));

                view.setOnClickListener(this);
                view.setId(i+START_POS);
                view.setBackgroundResource(R.color.full_transparent);

                android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                // lp.weight = 1;
                view.setLayoutParams(lp);
                contentLayout.addView(view);
                if (i == 0) {

                    text.setTextColor(titleColorSel);


                } else {
                    text.setTextColor(titleColorNor);
                }
            }

        }

    }


    public void clearViews() {
        if (contentLayout != null) {
            contentLayout.removeAllViews();
        }
    }

    public void addItemView(View view, int pos) {

        addItemView(view, pos, 0);
    }

    public void addItemView(View view, int pos, int offset) {
        if (view == null) {
            return;
        }

        view.setOnClickListener(this);
        view.setId(pos+START_POS);
        view.setBackgroundResource(R.color.full_transparent);
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        // lp.weight = 1;
        if (offset > 0) {
            lp.rightMargin = offset;
        }

        view.setLayoutParams(lp);
        contentLayout.addView(view);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        int pos = v.getId() - START_POS;

        selectedTab(pos);
        if (mOnItemListener != null) {
            mOnItemListener.onItemClick(v, pos);
        }
    }

    public void setItemCheck(int pos) {
        View layout = contentLayout.getChildAt(pos);
        if (mOnItemListener != null) {
            mOnItemListener.onItemCheck(layout, true);
        }
    }


    public View getItemView(int pos) {
        return contentLayout.getChildAt(pos);
    }


    public void selectedTab(int position) {

        int nOffset = 0;
        for (int i = 0; i < contentLayout.getChildCount(); i++) {
            View layout = contentLayout.getChildAt(i);
            layout.setSelected(position == i);
            if (mOnItemListener != null) {
                mOnItemListener.onItemCheck(layout, position == i);
            }

            if (i < position) {
                nOffset += layout.getWidth();
            }

            TextView text = (TextView) layout.findViewById(R.id.text);
            if (text == null) {
                continue;
            }

            if (i == position) {
                text.setTextColor(titleColorSel);
            } else {
                text.setTextColor(titleColorNor);
            }
        }

        smoothScrollTo(nOffset, 0);
    }




    /***
     * 从左到右 0 －－ 1 －－2 .。。
     *
     * @param id
     */
    public void moveSection(int id) {
        View tv = new View(mContext);
        tv.setId(id);
        onClick(tv);
    }

    public void onScrollViews(View v, boolean bLeft) {
        View view = v;


        int length = contentLayout.getMeasuredWidth();
        int currentX = getScrollX();
        int width = getWidth();
        int nextPos = view.getLeft();

        if (bLeft) {
            if (nextPos > currentX + width) {
                smoothScrollTo(nextPos - view.getWidth(), 0);
            } else if (nextPos + view.getWidth() <= length
                    && length - width >= currentX && currentX < nextPos) {

            } else if (currentX < nextPos) {
                smoothScrollTo(currentX - view.getWidth(), 0);
            } else if (currentX >= nextPos) {
                smoothScrollTo(view.getLeft() - view.getWidth(), 0);
            } else {
                smoothScrollTo(0, 0);
            }
        } else {

            if (currentX > nextPos) {
                smoothScrollTo(nextPos - view.getWidth(), 0);
            } else if (currentX + view.getWidth() < length) {
                if (currentX + width >= (view.getWidth() + nextPos)) {
                    smoothScrollTo(currentX + view.getWidth() / 2, 0);
                } else {
                    smoothScrollTo(currentX + view.getWidth(), 0);
                }

            } else {
                smoothScrollTo(length - getWidth(), 0);
            }
        }
    }



}

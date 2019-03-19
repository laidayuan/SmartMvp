package com.dada.marsframework.widget.banner;

import java.io.Serializable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dada.marsframework.R;
import com.dada.marsframework.utils.DensityUtils;


public class PageIndicator extends LinearLayout implements Serializable {

    private Context context;

    private ImageView[] indicatorImageView;

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

    }


    public void clear() {
        removeAllViews();
    }



    public void initIndicators(int aNum) {
        removeAllViews();

        int indicatorSize = DensityUtils.dip2px(context, 6f);
        int indicatorMargin = DensityUtils.dip2px(context, 4f);

        indicatorImageView = new ImageView[aNum];

        for (int i = 0; i < aNum; i++) {
            indicatorImageView[i] = new ImageView(context);
            indicatorImageView[i].setBackgroundResource(R.drawable.ic_position_indicator);
            LayoutParams params = new LayoutParams(indicatorSize, indicatorSize);
            params.setMargins(indicatorMargin, 0, indicatorMargin, 0);
            indicatorImageView[i].setLayoutParams(params);
            
            addView(indicatorImageView[i]);
        }
    }

    public void setIndicatorFocus(int aIndex) {
        if (indicatorImageView == null) {
            return;
        }

        int index = aIndex % indicatorImageView.length;

        for (int i = 0; i < indicatorImageView.length; i++) {
        	//indicatorImageView[i].setColorFilter(i == index ? 0xffffffff : 0x80ffffff);
            indicatorImageView[i].getBackground().setLevel(i == index ? 1 : 0);
        }
    }
}

package com.dada.marsframework.widget.banner;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dada.marsframework.R;


/**
 * Created by laidayuan on 16/1/21.
 */
public class AdvHeaderHelper {

    private AutoScrollViewPager mViewPager = null;
    private PageIndicator mPageIndicator = null;

    private View mContentView;
    private AdvPagerAdapterEx mAdvPagerAdapter;
    private AdvListener mAdvListener;
    
    public interface AdvListener {
    	
    	public void onAdvPageSelected(View view, int pos);
    	public void onAdvImageView(ImageView imageView, int pos);
    	public void onAdvItemClick(int pos);
    	
    }
    
    
    public void setAdvListener(AdvListener l) {
    	mAdvListener = l;
    	if (mAdvPagerAdapter != null) {
    		mAdvPagerAdapter.setAdvListener(l);
    	}
    }

    public AdvHeaderHelper() {

    }

    public View initAdvHeader(Context c, FragmentManager fm, int layout) {
        View view = LayoutInflater.from(c).inflate(layout,
                null);
        mViewPager = (AutoScrollViewPager) view
                .findViewById(R.id.adv_viewpager);

        mPageIndicator = (PageIndicator) view
                .findViewById(R.id.indicator_layout);

        mAdvPagerAdapter = new AdvPagerAdapterEx(c, fm, mViewPager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setViewPagerScrollSpeed();
        mContentView = view;
        
        return view;
    }
    
    public View initAdvHeader(View view, Context c, FragmentManager fm) {
        mViewPager = (AutoScrollViewPager) view
                .findViewById(R.id.adv_viewpager);

        mPageIndicator = (PageIndicator) view
                .findViewById(R.id.indicator_layout);

        mAdvPagerAdapter = new AdvPagerAdapterEx(c, fm, mViewPager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setViewPagerScrollSpeed();
        mContentView = view;

        return view;
    }


    public void clear() {
    	if (mPageIndicator != null) {
    		mPageIndicator.clear();
    	}
        
    	if (mAdvPagerAdapter != null) {
    		mAdvPagerAdapter.clear();
    	}
        
    }


    @SuppressWarnings("deprecation")
	public void setContentSize(int nSize) {
        if (nSize == 0) {
            return;
        }

        clear();
        mAdvPagerAdapter.setCount(nSize);
        if (nSize > 1) {
        	if (mPageIndicator != null) {
        		mPageIndicator.initIndicators(nSize);
                mPageIndicator.setIndicatorFocus(0);
        	}
            
            mViewPager.setCurrentItem(0, false);
            if (mAdvListener != null) {
        		mAdvListener.onAdvPageSelected(mContentView, 0);
        	}
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageSelected(int pos) {
                    // TODO Auto-generated method stub
                    //BannerModel item = dataList.get(pos);
                	if (mAdvListener != null) {
                		mAdvListener.onAdvPageSelected(mContentView, pos);
                	}
                	
                	if (mPageIndicator != null) {
                		mPageIndicator.setIndicatorFocus(pos);
                	}
                    
                }
            });
        } else {
        	if (mPageIndicator != null) {
        		mPageIndicator.clear();
        	}
            
            mViewPager.setOnPageChangeListener(null);
        }

        onAdvResume();
    }

    public void onAdvResume() {
        mViewPager.onShow();

    }

    public void onAdvPause() {
        mViewPager.onHide();

    }
}


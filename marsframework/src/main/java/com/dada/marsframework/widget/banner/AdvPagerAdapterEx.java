package com.dada.marsframework.widget.banner;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class AdvPagerAdapterEx extends PagerAdapter {


    private Context mContext;

    private int mCount;

    public AdvPagerAdapterEx(Context context, FragmentManager fm, ViewPager pager) {
   
    	mContext = context;
        pager.setAdapter(this);
    }


    public void setCount(int count) {
    	mCount = count;

        notifyDataSetChanged();
    }

    public void clear() {

    	mCount = 0;

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {

        return mCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View view, int position, Object object) 
	{
		((ViewPager) view).removeView((View)object);
	}

	@Override
	public Object instantiateItem(View view, final int position) 
	{

		ImageView imageView = new ImageView(mContext);

		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
		imageView.setPadding(0, 0, 0, 0);
		((ViewPager) view).addView(imageView, 0);

		if (mAdvListener != null) {
			mAdvListener.onAdvImageView(imageView, position);
		}
		
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (mAdvListener != null) {
					mAdvListener.onAdvItemClick(position);
				}
			}
			
		});

		return imageView;
	}

	AdvHeaderHelper.AdvListener mAdvListener;
    public void setAdvListener(AdvHeaderHelper.AdvListener l) {
    	mAdvListener = l;
    }
    

}

package com.dada.marsframework.widget.banner;

import java.lang.reflect.Field;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by laidayuan on 16/1/21.
 */
public class AutoScrollViewPager extends ViewPager {

	private int AUTO_SCROLL_DURATION = 4000;

	private boolean ENABLE_FLING_ACTION = true;

	// private boolean RIGHT_DIRECT = true;

	private Handler mHandler;

	public AutoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHandler = new Handler();
		mHandler.postDelayed(mRunnable, AUTO_SCROLL_DURATION);

	}

	public AutoScrollViewPager(Context context) {
		super(context);
		mHandler = new Handler();
		mHandler.postDelayed(mRunnable, AUTO_SCROLL_DURATION);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			ENABLE_FLING_ACTION = false;
			onHide();
			break;

		case MotionEvent.ACTION_MOVE:
			ENABLE_FLING_ACTION = false;
			// getParent().requestDisallowInterceptTouchEvent(true);
			onHide();
			break;

		case MotionEvent.ACTION_UP:
			ENABLE_FLING_ACTION = true;
			onShow();
			break;

		default:
			ENABLE_FLING_ACTION = true;
			onShow();
			break;

		}

		super.onTouchEvent(event);

		return true;
	}

	private void autoScrollGallery() {
		if (!ENABLE_FLING_ACTION) {
			return;
		}

		int index = getCurrentItem();

		int nCount = getAdapter().getCount();

		/*
		 * if (index + 1 == nCount) { RIGHT_DIRECT = false; }
		 * 
		 * if (index == 0) { RIGHT_DIRECT = true; }
		 * 
		 * if (RIGHT_DIRECT) { ++index; } else { --index; }
		 */

		if (nCount > 0) {
			index = (++index) % nCount;
		}

		setCurrentItem(index);
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (ENABLE_FLING_ACTION) {
				autoScrollGallery();
				mHandler.postDelayed(mRunnable, AUTO_SCROLL_DURATION);
			}

		}
	};

	public void onShow() {
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, AUTO_SCROLL_DURATION);

	}

	public void onHide() {
		mHandler.removeCallbacks(mRunnable);

	}

	float x = 0;
	float y = 0;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			x = ev.getX();
			y = ev.getY();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			float moveX = ev.getX();
			float moveY = ev.getY();
			float absX = Math.abs(moveX - x);
			float absY = Math.abs(moveY - y);
			if (absX > absY) {
				// 这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。
				getParent().requestDisallowInterceptTouchEvent(true);
			}

			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
		default: {
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		}

		return super.dispatchTouchEvent(ev);
	}

	public void setViewPagerScrollSpeed() {
		try {
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			if (mScroller != null) {
				mScroller.setAccessible(true);
				FixedSpeedScroller scroller = new FixedSpeedScroller(
						this.getContext());
				mScroller.set(this, scroller);
			}

		} catch (NoSuchFieldException e) {

		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
	}

	public class FixedSpeedScroller extends Scroller {
		private int mDuration = 1600;

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator,
				boolean flywheel) {
			super(context, interpolator, flywheel);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}
	}
}
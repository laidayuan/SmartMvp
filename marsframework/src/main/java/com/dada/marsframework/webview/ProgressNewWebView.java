package com.dada.marsframework.webview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.dada.marsframework.R;
import com.dada.marsframework.utils.DensityUtils;
/**
 * Created by laidayuan on 2018/2/13.
 */

public class ProgressNewWebView extends WebView {

    private ProgressBar progressbar;
    private ProgressWebView.OnScrollChangedListener mOnScrollChangedListener;

    public ProgressNewWebView(Context context) {
        super(context);
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setProgressDrawable(context.getResources().getDrawable(
                (
                        R.drawable.seekerbar_recomment_webview_corner)));
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                DensityUtils.dip2px(context, 3), 0, 0));
        progressbar.setVisibility(GONE);
        addView(progressbar);
        // setWebViewClient(new WebViewClient(){});
        setWebChromeClient(new WebChromeClient());
    }

    public ProgressNewWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setProgressDrawable(context.getResources().getDrawable(
                (R.drawable.seekerbar_recomment_webview_corner)));
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                DensityUtils.dip2px(context, 3), 0, 0));
        progressbar.setVisibility(GONE);
        addView(progressbar);
        // setWebViewClient(new WebViewClient(){});
        setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }

    }

    public void setProgress(int newProgress) {
        if (newProgress == 100) {
            progressbar.setVisibility(GONE);
        } else {
            if (progressbar.getVisibility() == GONE)
                progressbar.setVisibility(VISIBLE);
            progressbar.setProgress(newProgress);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScroll(l, t, oldl, oldt);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
    }


    @Override
    public void setOverScrollMode(int mode) {
        super.setOverScrollMode(mode);
        //ScreenUtils.restoreAdaptScreen();
    }

    public ProgressWebView.OnScrollChangedListener getOnScrollChangedListener() {
        return mOnScrollChangedListener;
    }

    public void setOnScrollChangedListener(
            final ProgressWebView.OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public static interface OnScrollChangedListener {
        public void onScroll(int l, int t, int oldl, int oldt);
    }
}

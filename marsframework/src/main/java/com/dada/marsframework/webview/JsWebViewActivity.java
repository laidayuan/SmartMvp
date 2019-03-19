package com.dada.marsframework.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dada.marsframework.R;
import com.dada.marsframework.base.BaseActivity;
import com.dada.marsframework.utils.LogUtils;
import com.dada.marsframework.utils.StringUtils;
import com.dada.marsframework.utils.SwipeBackLayout;
import com.dada.marsframework.utils.ToastUtils;
import com.dada.marsframework.widget.alertView.AlertView;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by laidayuan on 2018/2/12.
 */

public class JsWebViewActivity extends BaseActivity {

    //@BindView(R.id.webview)
    ProgressWebView webview;

    //@BindView(R.id.layout_webview)
    LinearLayout mLayout;


    //@BindView(R.id.floatbutton)
    TextView floatbutton;

    protected String mUrl;

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;

    public int getLayoutId() {
        return R.layout.activity_webview;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout.attachTo(this);
        setBackAction();
        mLayout = (LinearLayout) findViewById(R.id.layout_webview);
        floatbutton = (TextView) findViewById(R.id.floatbutton);
        floatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webview != null) {
                    webview.scrollTo(0, 0);
                }
            }
        });

        initWebView();
        initData();
    }

    protected ProgressWebView getWebView() {
        return webview;
    }


    @Override
    public void onPause() {
        super.onPause();

        webview.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        webview.onResume();
    }

    @Override
    public void onDestroy() {
        if (webview != null) {
            webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            ((ViewGroup) webview.getParent()).removeView(webview);
            webview.stopLoading();
            webview.getSettings().setJavaScriptEnabled(false);
            webview.clearHistory();
            webview.removeAllViews();
            webview.destroy();
            webview = null;
        }

        super.onDestroy();
    }

    //安全漏洞修复 1
    public void disableFileAccess() {
        // 须要使用 file 协议
        WebSettings settings = webview.getSettings();
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setSavePassword(false);
        // 禁止 file 协议载入 JavaScript
        if (mUrl.startsWith("file://")) {
            settings.setJavaScriptEnabled(false);
        } else {
            settings.setJavaScriptEnabled(true);
        }
    }

    public void diableMethod() {
        webview.removeJavascriptInterface("searchBoxJavaBridge_");
        webview.removeJavascriptInterface("accessibility");
        webview.removeJavascriptInterface("accessibilityTraversal");
    }

    protected void initWebView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webview = new ProgressWebView(getApplicationContext());
        diableMethod();
        webview.setLayoutParams(params);
        mLayout.addView(webview);

        //接收html发过来的消息
        webview.setDefaultHandler(new DefaultHandler());
//        webview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        webview.setInitialScale(28);
        WebSettings settings = webview.getSettings();
        settings.setUseWideViewPort(true);
        // settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);

        settings.setTextSize(WebSettings.TextSize.NORMAL);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        webview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
                        webview.goBack(); // 后退

                        return true; // 已处理
                    } else if (keyCode == KeyEvent.KEYCODE_BACK) {

                    }
                }

                return false;
            }
        });

        webview.setWebViewClient(new BridgeWebViewClient(webview) {

            @Override
            @Deprecated
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                try {// API21之前屏蔽广告
                    String hostName = new URL(url).getHost();
                    if (checkHostName(hostName)) {//检查是不是自己APP的域名,不是则不允许加载
                        return super.shouldInterceptRequest(view, url);
                    } else {
                        LogUtils.e("no my host!" + hostName);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //不是自己的APP域名,返回一个null,则屏蔽了广告
                return new WebResourceResponse(null, null, null);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest
                    request) {//高版本屏蔽广告同理
                String url = request.getUrl().toString();
                if (Build.VERSION.SDK_INT >= 21) {// API21后,屏蔽广告
                    String hostName = request.getUrl().getHost();
                    if (hostName != null) {
                        if (checkHostName(hostName)) {
                        } else {
                            return new WebResourceResponse(null, null, null);
                        }
                    }
                }

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 可以拦截h5内部跳转，修改url
                // view.loadUrl(url);
                //L.e("shouldOverrideUrlLoading   url = " + url);
                //TODO 拦截url，防止加载不在合法列表的主机地址
                if (!TextUtils.isEmpty(url) && url.contains("share://main")) {
                    shareMethod(url);
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                //L.e("onReceivedSslError   ");
                new AlertView(getString(R.string.string_alert_tip), getString(R.string.string_alert_sslerror), getString(R.string.string_alert_back),
                        new String[]{getString(R.string.string_alert_sure)}, null, JsWebViewActivity.this, AlertView.Style.Alert,
                        new AlertView.OnItemClickListener() {
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    if (handler != null) {//证书错误，存在隐患
                                        handler.proceed();
                                    }
                                } else {
                                    if (handler != null) {
                                        handler.cancel();
                                    }
                                }
                            }
                        }).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //L.e("onPageFinished  url = " + url);
                //view.getSettings().setJavaScriptEnabled(true);
                super.onPageFinished(view, url);
                setTitleText(view.getTitle());
                //addImageClickListener();

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //L.e("onPageStarted  url = " + url);
                //view.getSettings().setJavaScriptEnabled(true);
                super.onPageStarted(view, url, favicon);
            }

        });

        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                webview.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitleText(title);
            }

            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                LogUtils.e("openFileChooser acceptType = " + acceptType);
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                LogUtils.e("onShowFileChooser filePathCallback = " + filePathCallback.toString());
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    ToastUtils.toast("打开失败");
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                LogUtils.e("openFileChooser capture = " + capture);
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                LogUtils.e("openFileChooser uploadMsg = " + uploadMsg.toString());
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });

        //webview.addJavascriptInterface(new ImageJavascriptInterface(this), "imagelistener");
        getWebView().registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                LogUtils.e("handler = imagelistener, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");

            }
        });

        webview.setOnScrollChangedListener(new ProgressWebView.OnScrollChangedListener() {

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                //float nY = webview.getHeight() + webview.getScrollY();
                float nHeight = webview.getHeight();//webview.getContentHeight() * webview.getScale();
                //float screenWidth = DensityUtils.getScreenWidth(JsWebViewActivity.this);
                //L.e("t = " + t + ", nHeight = " + nHeight + ", oldt = " + oldt);
                if (t >= nHeight / 2) {//返回顶部
                    floatbutton.setVisibility(View.VISIBLE);
                } else {
                    floatbutton.setVisibility(View.GONE);
                }
            }
        });

    }

    private boolean checkHostName(String host) {
        if (StringUtils.isEmpty(host)) {
            return false;
        }

        return true;
    }

    public class ImageJavascriptInterface {

        private Context context;

        public ImageJavascriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openImage(String img) {
            LogUtils.e("openImage: " + img);
        }
    }


    //@OnClick(R.id.floatbutton)
    public void onScrollTop(View view) {
        if (webview != null) {
            webview.scrollTo(0, 0);
        }
    }

    protected void initData() {
        Intent it = getIntent();
        final Bundle bundle = it.getExtras();// it.getBundleExtra("bundle");
        if (bundle == null) {
            LogUtils.e("bundle is null");
            return;
        }

        mUrl = bundle.getString("url");
        webview.post(new Runnable() {
            @Override
            public void run() {
                if (mUrl != null) {
                    if (mUrl != null && !mUrl.contains("http://") && !mUrl.contains("https://")) {
                        mUrl = "http://" + mUrl;
                    }

                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("User-Agent", "Android");
                    if (webview != null) {
                        webview.loadUrl(mUrl, map);
                    }
                } else if (bundle.containsKey("file")) {
                    String fileName = bundle.getString("file");
                    if (fileName != null) {
                        String strUrl = "file:///android_asset/" + fileName;
                        webview.loadUrl(strUrl);
                    }
                } else {
                    String html = bundle.getString("html");
                    if (html != null) {
                        webview.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    }
                }
            }
        });
    }


    protected void shareMethod(String url) {

    }


    private void addImageClickListener() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，
        //函数的功能是在图片点击的时候调用本地java接口并传递url过去

//        webview.loadUrl("javascript:(function(){" +
//                "var objs = document.getElementsByTagName(\"img\"); " +
//                "for(var i=0;i<objs.length;i++)  " +
//                "{"
//                + "    objs[i].onclick=function()  " +
//                "    {  "
//                + "        window.WebViewJavascriptBridge.callHandler('imagelistener', {'param': this.src}, function(responseData) {} );  " +
//                "    }  " +
//                "}" +
//                "})()");

        LogUtils.e("addImageClickListener -- ");
        webview.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistener.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                //file:///storage/emulated/0/darwin/logs/1522125185411.txt
                LogUtils.e("onActivityResult data = " + intent.getData());
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else {
            ToastUtils.toast("加载失败");
        }
    }


    public static boolean isBlank(WebView webview) {
        WebBackForwardList flist = webview.copyBackForwardList();
        if (flist != null && flist.getSize() > 0) {
            for (int i = 0; i < flist.getSize(); i++) {
                WebHistoryItem item = flist.getItemAtIndex(i);
                if (item != null && item.getUrl().equalsIgnoreCase("about:blank")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void clear(WebView webView, boolean loadBlank) {
        if (webView != null) {
            webView.clearHistory();
            while (webView.canGoBack()) {
                webView.goBack();
            }

            if (loadBlank) {
                webView.loadUrl("about:blank");
            }
        }
    }

    public static void destroy(WebView webView) {
        if (webView != null) {
            webView.stopLoading();
            webView.removeAllViews();
            ((ViewGroup)webView.getParent()).removeView(webView);
            while (webView.canGoBack()) {
                webView.goBack();
            }

            webView.clearCache(true);
            webView.clearHistory();
            webView.destroy();
        }
    }
}

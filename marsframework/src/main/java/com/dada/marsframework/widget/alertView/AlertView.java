package com.dada.marsframework.widget.alertView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.dada.marsframework.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by laidayuan on 2018/3/23.
 */

public class AlertView {

    public interface OnDismissListener {
        public void onDismiss(Object o);
    }

    public interface OnItemClickListener {
        public void onItemClick(Object o, int position);
    }

    public static enum Style {
        ActionSheet, Alert
    }

    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
    public static final int HORIZONTAL_BUTTONS_MAXCOUNT = 2;
    public static final String OTHERS = "others";
    public static final String DESTRUCTIVE = "destructive";
    public static final String CANCEL = "cancel";
    public static final String TITLE = "title";
    public static final String MSG = "msg";
    public static final int CANCELPOSITION = -1;// 点击取消按钮返回 －1，其他按钮从0开始算

    private String title;
    private String msg;
    private List<String> mDestructive;
    private List<String> mOthers;
    private String cancel;
    private ArrayList<String> mDatas = new ArrayList<String>();

    private Context context;
    private ViewGroup contentContainer;
    private ViewGroup decorView;// activity的根View
    private ViewGroup rootView;// AlertView 的 根View
    private ViewGroup loAlertHeader;// 窗口headerView

    private Style style = Style.Alert;

    private OnDismissListener onDismissListener;
    private OnItemClickListener onItemClickListener;
    private boolean isDismissing;

    private Animation outAnim;
    private Animation inAnim;
    private int gravity = Gravity.CENTER;

    private boolean itemGravityLeft = false;

    public void setItemGravityLeft() {
        itemGravityLeft = true;
    }

    public AlertView(String title, String msg, String cancel,
                     String[] destructive, String[] others, Context context,
                     Style style, OnItemClickListener onItemClickListener, boolean itemGravityLeft) {
        this.context = context;
        if (style != null)
            this.style = style;
        this.onItemClickListener = onItemClickListener;

        this.itemGravityLeft = itemGravityLeft;
        initData(title, msg, cancel, destructive, others);
        initViews();
        init();
        initEvents();
    }

    public AlertView(String title, String msg, String cancel,
                     String[] destructive, String[] others, Context context,
                     Style style, OnItemClickListener onItemClickListener) {
        this.context = context;
        if (style != null)
            this.style = style;
        this.onItemClickListener = onItemClickListener;

        initData(title, msg, cancel, destructive, others);
        initViews();
        init();
        initEvents();
    }

    /**
     * 获取数据
     */
    protected void initData(String title, String msg, String cancel,
                            String[] destructive, String[] others) {

        this.title = title;
        this.msg = msg;
        if (destructive != null) {
            this.mDestructive = Arrays.asList(destructive);
            this.mDatas.addAll(mDestructive);
        }
        if (others != null) {
            this.mOthers = Arrays.asList(others);
            this.mDatas.addAll(mOthers);
        }
        if (cancel != null) {
            this.cancel = cancel;
            if (style == Style.Alert
                    && mDatas.size() < HORIZONTAL_BUTTONS_MAXCOUNT) {
                this.mDatas.add(0, cancel);
            }
        }

    }

    protected void initViews() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView()
                .findViewById(android.R.id.content);
        rootView = (ViewGroup) layoutInflater.inflate(
                R.layout.layout_alertview, decorView, false);
        rootView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        contentContainer = (ViewGroup) rootView
                .findViewById(R.id.content_container);
        int margin_alert_left_right = 0;
        switch (style) {
            case ActionSheet:
                params.gravity = Gravity.BOTTOM;
                margin_alert_left_right = context.getResources()
                        .getDimensionPixelSize(
                                R.dimen.margin_actionsheet_left_right);
                params.setMargins(margin_alert_left_right, 0,
                        margin_alert_left_right, margin_alert_left_right);
                contentContainer.setLayoutParams(params);
                gravity = Gravity.BOTTOM;
                initActionSheetViews(layoutInflater);
                break;
            case Alert:
                params.gravity = Gravity.CENTER;
                margin_alert_left_right = context.getResources()
                        .getDimensionPixelSize(R.dimen.margin_alert_left_right);
                params.setMargins(margin_alert_left_right, 0,
                        margin_alert_left_right, 0);
                contentContainer.setLayoutParams(params);
                gravity = Gravity.CENTER;
                initAlertViews(layoutInflater);
                break;
        }
    }

    protected void initHeaderView(ViewGroup viewGroup) {
        loAlertHeader = (ViewGroup) viewGroup.findViewById(R.id.loAlertHeader);
        // 标题和消息
        TextView tvAlertTitle = (TextView) viewGroup
                .findViewById(R.id.tvAlertTitle);
        TextView tvAlertMsg = (TextView) viewGroup
                .findViewById(R.id.tvAlertMsg);
        if (title != null) {
            tvAlertTitle.setText(title);
        } else {
            tvAlertTitle.setVisibility(View.GONE);
        }
        if (msg != null) {
            tvAlertMsg.setText(msg);
        } else {
            tvAlertMsg.setVisibility(View.GONE);
        }
    }

    protected void initListView() {
        ListView alertButtonListView = (ListView) contentContainer
                .findViewById(R.id.alertButtonListView);
        // 把cancel作为footerView
        if (cancel != null && style == Style.Alert) {
            View itemView = LayoutInflater.from(context).inflate(
                    R.layout.item_alertbutton, null);
            TextView tvAlert = (TextView) itemView.findViewById(R.id.tvAlert);
            tvAlert.setText(cancel);
            tvAlert.setClickable(true);
            tvAlert.setTypeface(Typeface.DEFAULT_BOLD);
            tvAlert.setTextColor(context.getResources().getColor(
                    R.color.textColor_alert_button_cancel));
            tvAlert.setBackgroundResource(R.drawable.bg_alertbutton_bottom);
            tvAlert.setOnClickListener(new OnTextClickListener(CANCELPOSITION));
            alertButtonListView.addFooterView(itemView);
        }

        AlertViewAdapter adapter = new AlertViewAdapter(mDatas, mDestructive);
        if (itemGravityLeft) {
            adapter.setAlignLeft();
        }

        alertButtonListView.setAdapter(adapter);
        alertButtonListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int position, long l) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(AlertView.this,
                                    position);
                        dismiss();
                    }
                });
    }

    protected void initActionSheetViews(LayoutInflater layoutInflater) {
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(
                R.layout.layout_alertview_actionsheet, contentContainer);
        initHeaderView(viewGroup);

        initListView();
        TextView tvAlertCancel = (TextView) contentContainer
                .findViewById(R.id.tvAlertCancel);
        if (cancel != null) {
            tvAlertCancel.setVisibility(View.VISIBLE);
            tvAlertCancel.setText(cancel);
        }
        tvAlertCancel
                .setOnClickListener(new OnTextClickListener(CANCELPOSITION));
    }

    protected void initAlertViews(LayoutInflater layoutInflater) {

        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(
                R.layout.layout_alertview_alert, contentContainer);
        initHeaderView(viewGroup);

        int position = 0;
        // 如果总数据小于等于HORIZONTAL_BUTTONS_MAXCOUNT，则是横向button
        if (mDatas.size() <= HORIZONTAL_BUTTONS_MAXCOUNT) {
            ViewStub viewStub = (ViewStub) contentContainer
                    .findViewById(R.id.viewStubHorizontal);
            viewStub.inflate();
            LinearLayout loAlertButtons = (LinearLayout) contentContainer
                    .findViewById(R.id.loAlertButtons);
            for (int i = 0; i < mDatas.size(); i++) {
                // 如果不是第一个按钮
                if (i != 0) {
                    // 添加上按钮之间的分割线
                    View divier = new View(context);
                    divier.setBackgroundColor(context.getResources().getColor(
                            R.color.bgColor_divier));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            (int) context.getResources().getDimension(
                                    R.dimen.size_divier),
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    loAlertButtons.addView(divier, params);
                }
                View itemView = LayoutInflater.from(context).inflate(
                        R.layout.item_alertbutton, null);
                TextView tvAlert = (TextView) itemView
                        .findViewById(R.id.tvAlert);
                tvAlert.setClickable(true);

                // 设置点击效果
                if (mDatas.size() == 1) {
                    tvAlert.setBackgroundResource(R.drawable.bg_alertbutton_bottom);
                } else if (i == 0) {// 设置最左边的按钮效果
                    tvAlert.setBackgroundResource(R.drawable.bg_alertbutton_left);
                } else if (i == mDatas.size() - 1) {// 设置最右边的按钮效果
                    tvAlert.setBackgroundResource(R.drawable.bg_alertbutton_right);
                }
                String data = mDatas.get(i);
                tvAlert.setText(data);

                // 取消按钮的样式
                if (data == cancel) {
                    tvAlert.setTypeface(Typeface.DEFAULT_BOLD);
                    tvAlert.setTextColor(context.getResources().getColor(
                            R.color.textColor_alert_button_cancel));
                    tvAlert.setOnClickListener(new OnTextClickListener(
                            CANCELPOSITION));
                    position = position - 1;
                }
                // 高亮按钮的样式
                else if (mDestructive != null && mDestructive.contains(data)) {
                    tvAlert.setTextColor(context.getResources().getColor(
                            R.color.textColor_alert_button_destructive));
                }

                tvAlert.setOnClickListener(new OnTextClickListener(position));
                position++;
                loAlertButtons.addView(itemView, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            }
        } else {
            ViewStub viewStub = (ViewStub) contentContainer
                    .findViewById(R.id.viewStubVertical);
            viewStub.inflate();
            initListView();
        }
    }

    protected void init() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    protected void initEvents() {
    }

    public AlertView addExtView(View extView) {
        loAlertHeader.addView(extView);
        return this;
    }

    /**
     * show的时候调用
     *
     * @param view
     *            这个View
     */
    private void onAttached(View view) {
        decorView.addView(view);
        if (inAnim != null) {
            contentContainer.startAnimation(inAnim);
        }

    }

    /**
     * 添加这个View到Activity的根视图
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
    }

    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {
        View view = decorView.findViewById(R.id.outmost_container);
        return view != null;
    }

    public void dismiss() {
        if (isDismissing) {
            return;
        }

        isDismissing = true;
        if (outAnim != null) {
            // 消失动画
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    decorView.post(new Runnable() {
                        @Override
                        public void run() {
                            // 从activity根视图移除
                            decorView.removeView(rootView);
                            isDismissing = false;
                            if (onDismissListener != null) {
                                onDismissListener.onDismiss(AlertView.this);
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            contentContainer.startAnimation(outAnim);
        } else {
            decorView.removeView(rootView);
            isDismissing = false;
            if (onDismissListener != null) {
                onDismissListener.onDismiss(AlertView.this);
            }
        }

    }

    public Animation getInAnimation() {
        int res = AlertAnimateUtil.getAnimationResource(this.gravity, true);
        if (res != -1) {
            return AnimationUtils.loadAnimation(context, res);
        }
        return null;
    }

    public Animation getOutAnimation() {
        int res = AlertAnimateUtil.getAnimationResource(this.gravity, false);
        if (res != -1) {
            return AnimationUtils.loadAnimation(context, res);
        }
        return null;
    }

    public AlertView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    class OnTextClickListener implements View.OnClickListener {

        private int position;

        public OnTextClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(AlertView.this, position);
            dismiss();
        }
    }

    /**
     * 主要用于拓展View的时候有输入框，键盘弹出则设置MarginBottom往上顶，避免输入法挡住界面
     */
    public void setMarginBottom(int marginBottom) {
        int margin_alert_left_right = context.getResources()
                .getDimensionPixelSize(R.dimen.margin_alert_left_right);
        params.setMargins(margin_alert_left_right, 0, margin_alert_left_right,
                marginBottom);
        contentContainer.setLayoutParams(params);
    }

    public AlertView setCancelable(boolean isCancelable) {
        View view = rootView.findViewById(R.id.outmost_container);

        if (isCancelable) {
            view.setOnTouchListener(onCancelableTouchListener);
        } else {
            view.setOnTouchListener(null);
        }
        return this;
    }

    /**
     * Called when the user touch on black overlay in order to dismiss the
     * dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };
}

/**
 * public class MainActivity extends Activity implements OnItemClickListener,
 * OnDismissListener {
 *
 * private AlertView mAlertView;//避免创建重复View，先创建View，然后需要的时候show出来，推荐这个做法
 * private AlertView mAlertViewExt;//窗口拓展例子 private EditText etName;//拓展View内容
 * private InputMethodManager imm;
 *
 * @Override protected void onCreate(Bundle savedInstanceState) {
 *           super.onCreate(savedInstanceState);
 *           setContentView(R.layout.activity_main); imm =
 *           (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
 *           mAlertView = new AlertView("标题", "内容", "取消", new String[]{"确定"},
 *           null, this, AlertView.Style.Alert,
 *           this).setCancelable(true).setOnDismissListener(this); //拓展窗口
 *           mAlertViewExt = new AlertView("提示", "请完善你的个人资料！", "取消", null, new
 *           String[]{"完成"}, this, AlertView.Style.Alert, this); ViewGroup
 *           extView = (ViewGroup)
 *           LayoutInflater.from(this).inflate(R.layout.alertext_form,null);
 *           etName = (EditText) extView.findViewById(R.id.etName);
 *           etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
 * @Override public void onFocusChange(View view, boolean focus) { //输入框出来则往上移动
 *           boolean isOpen=imm.isActive();
 *           mAlertViewExt.setMarginBottom(isOpen&&focus ? 120 :0);
 *           System.out.println(isOpen); } });
 *           mAlertViewExt.addExtView(extView); }
 *
 *           public void alertShow1(View view) { mAlertView.show(); }
 *
 *           public void alertShow2(View view) { new AlertView("标题", "内容", null,
 *           new String[]{"确定"}, null, this, AlertView.Style.Alert,
 *           this).show(); }
 *
 *           public void alertShow3(View view) { new AlertView(null, null, null,
 *           new String[]{"高亮按钮1", "高亮按钮2", "高亮按钮3"}, new String[]{"其他按钮1",
 *           "其他按钮2", "其他按钮3", "其他按钮4", "其他按钮5", "其他按钮6", "其他按钮7", "其他按钮8",
 *           "其他按钮9", "其他按钮10", "其他按钮11", "其他按钮12"}, this,
 *           AlertView.Style.Alert, this).show(); }
 *
 *           public void alertShow4(View view) { new AlertView("标题", null, "取消",
 *           new String[]{"高亮按钮1"}, new String[]{"其他按钮1", "其他按钮2", "其他按钮3"},
 *           this, AlertView.Style.ActionSheet, this).show(); }
 *
 *           public void alertShow5(View view) { new AlertView("标题", "内容", "取消",
 *           null, null, this, AlertView.Style.ActionSheet,
 *           this).setCancelable(true).show(); }
 *
 *           public void alertShow6(View view) { new AlertView("上传头像", null,
 *           "取消", null, new String[]{"拍照", "从相册中选择"}, this,
 *           AlertView.Style.ActionSheet, this).show(); }
 *
 *           public void alertShowExt(View view) { mAlertViewExt.show(); }
 *           private void closeKeyboard() { //关闭软键盘
 *           imm.hideSoftInputFromWindow(etName.getWindowToken(),0); //恢复位置
 *           mAlertViewExt.setMarginBottom(0); }
 * @Override public void onItemClick(Object o,int position) { closeKeyboard();
 *           //判断是否是拓展窗口View，而且点击的是非取消按钮 if(o == mAlertViewExt && position !=
 *           AlertView.CANCELPOSITION){ String name =
 *           etName.getText().toString(); if(name.isEmpty()){
 *           Toast.makeText(this, "啥都没填呢", Toast.LENGTH_SHORT).show(); } else{
 *           Toast.makeText(this, "hello,"+name, Toast.LENGTH_SHORT).show(); }
 *
 *           return; } Toast.makeText(this, "点击了第" + position + "个",
 *           Toast.LENGTH_SHORT).show(); }
 * @Override public void onDismiss(Object o) { closeKeyboard();
 *           Toast.makeText(this, "消失了", Toast.LENGTH_SHORT).show(); }
 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if (keyCode
 *           == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
 *           if(mAlertView!=null && mAlertView.isShowing()){
 *           mAlertView.dismiss(); return false; } }
 *
 *           return super.onKeyDown(keyCode, event);
 *
 *           } }
 */

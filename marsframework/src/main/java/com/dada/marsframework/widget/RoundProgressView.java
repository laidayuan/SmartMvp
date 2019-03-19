package com.dada.marsframework.widget;

/**
 * Created by laidayuan on 2018/3/23.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dada.marsframework.R;


public class RoundProgressView extends AppCompatTextView {
    /**
     * 画笔对象的引用
     */
    private final Paint paint;

    private final Paint paintText;


    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;


    /**
     * 圆环进度条的宽度
     */
    private float roundProgressWidth;

    /**
     * 最大进度
     */
    private long max;

    /**
     * 当前进度
     */
    private long progress;


    /*** 结束时间 */
    private long endTime;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    private boolean mTickerStopped = true;
    private Runnable mTicker;
    public static long distanceTime;



    public void start(long time) {
        this.endTime = System.currentTimeMillis() + time*1000;
        setMax(time);
        startTimer();
        invalidate();
    }

    public void finish() {
        if (listener != null) {
            listener.finish();
        }

        setClickable(true);
        mTickerStopped = true;

        removeCallbacks(null);
        if (mBackgroudRes > 0) {
            setBackgroundResource(mBackgroudRes);
        }
    }

    public RoundProgressView(Context context) {
        this(context, null);
    }

    public RoundProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();
        paintText = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressView);

        // 获取自定义属性和默认值

        roundProgressColor = mTypedArray.getColor(
                R.styleable.RoundProgressView_timerColor, Color.GREEN);
        textColor = mTypedArray.getColor(
                R.styleable.RoundProgressView_timerTextColor, Color.GREEN);
        textSize = mTypedArray.getDimension(
                R.styleable.RoundProgressView_timerTextSize, 16);

        roundProgressWidth = mTypedArray.getDimension(
                R.styleable.RoundProgressView_timerWidth, 3);
        max = mTypedArray.getInteger(R.styleable.RoundProgressView_timerMax, 100);

        mTypedArray.recycle();
    }

    public void setProgress(long progress, long max) {
        this.progress = progress;
        this.max = max;

        setMax(max);
        setProgress(progress);
    }


    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        finish();
    }


    private void startTimer() {
        setClickable(false);
        mTickerStopped = false;

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {

            @Override
            public void run() {
                if (mTickerStopped) {
                    return;
                }

                next();
            }
        };

        mTicker.run();
    }


    private void next() {
        distanceTime = endTime - System.currentTimeMillis();
        distanceTime /= 1000;

        postDelayed(mTicker, 1000);

        // 设置每一秒的系统进程
        if (distanceTime > 0) {
            setProgress(distanceTime);

        } else {
            setProgress(distanceTime);

            finish();

        }
    }


    public interface OnProgressListener {
        void finish();
    }

    private OnProgressListener listener;

    public void setOnProgressListener(OnProgressListener listener) {
        this.listener = listener;
    }


    private int mBackgroudRes;
    public void setBackgroud(int nResId) {
        mBackgroudRes = nResId;
    }


// suport wrap_content
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//        if (widthSpecMode == MeasureSpec.AT_MOST
//                && heightSpecMode == MeasureSpec.AT_MOST) {
//            setMeasuredDimension(200, 200);
//        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
//            setMeasuredDimension(200, heightSpecSize);
//        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
//            setMeasuredDimension(widthSpecSize, 200);
//        }
//    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (!mTickerStopped) {

            setBackground(null);
            //setText("");

            //adjust padding
            final int paddingLeft = getPaddingLeft();
            final int paddingRight = getPaddingRight();
            final int paddingTop = getPaddingTop();
            final int paddingBottom = getPaddingBottom();
            int width = getWidth() - paddingLeft - paddingRight;
            int height = getHeight() - paddingTop - paddingBottom;

            // 获取圆心的x坐标
            int centreX = width / 2;
            int centreY = height / 2;

            // 圆环的半径
            int radius = (int) (centreY - roundProgressWidth / 2);

            paint.setAntiAlias(true); // 消除锯齿

            /**
             * 画进度百分比
             */
            //红心字
            //paint.setStrokeWidth(0);
            paintText.setAntiAlias(true);
            paintText.setColor(textColor);
            paintText.setTextSize(textSize);
            paintText.setTypeface(Typeface.DEFAULT); // 设置字体 DEFAULT_BOLD
            paintText.setFakeBoldText(false);
            // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
            String times = "";
            if (distanceTime == 0 || distanceTime < 0) {
                // 倒计时已结束

            } else {
                // times = dealTime(distanceTime);
                times = dealSecond(distanceTime);
            }

            float textWidth = paintText.measureText(times); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            canvas.drawText(times, paddingLeft + centreX - textWidth / 2, paddingTop + centreY
                    + textSize / 2 - 3, paintText); // 画出进度百分比
            /**
             * 画圆弧 ，画圆环的进度
             */
            // 设置进度是实心还是空心
            paint.setStrokeWidth(roundProgressWidth); // 设置圆环的宽度
            paint.setColor(roundProgressColor); // 设置进度的颜色
            RectF oval = new RectF(paddingLeft + centreX - radius, paddingTop + centreY - radius, paddingLeft + centreX
                    + radius, paddingTop + centreY + radius); // 用于定义的圆弧的形状和大小的界限

            paint.setStyle(Paint.Style.STROKE);
            // 根据进度画圆弧
            canvas.drawArc(oval, -90, -360 * distanceTime / max, false,
                    paint);
        } else {
            super.onDraw(canvas);
        }
    }

    public synchronized long getMax() {
        return max;
    }

    /**
     * deal time string
     *
     * @param time
     * @return
     */
    public static String dealTime(long time) {
        StringBuffer returnString = new StringBuffer();
        long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(second));
        returnString.append(minutesStr).append(":").append(secondStr);
        return returnString.toString();
    }

    public static String dealSecond(long time) {

        return String.valueOf(time);
    }

    /**
     * format time
     *
     * @param timeStr
     * @return
     */
    private static String timeStrFormat(String timeStr) {
        switch (timeStr.length()) {
            case 1:
                timeStr = "0" + timeStr;
                break;
        }
        return timeStr;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(long max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized long getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(long progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }



    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getRoundTextColor() {
        return textColor;
    }

    public void setRoundTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getRoundTextSize() {
        return textSize;
    }

    public void setRoundTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setRoundProgressWidth(int width) {
        roundProgressWidth = width;
    }



}


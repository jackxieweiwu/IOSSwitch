package com.kot32.library.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.kot32.library.R;
import com.kot32.library.color.Colors;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kot32 on 16/4/12.
 */
public class IOSSwitch extends View {

    // 颜色
    private int            frameColor              = Colors.IOS_GRAY; // 外框的颜色
    private int            ballColor               = Color.WHITE;     // 圆球颜色
    private int            bgColorUncheckedUnTaped = Color.WHITE;     // 还没选中时但渐变前的背景颜色
    private int            bgColor                 = Colors.IOS_GREEN;
    // 状态
    private boolean        isChecked               = false;

    // 尺寸
    private int            width;
    private int            height;
    private int            frameStrokeWidth        = 6;
    private int            ballStrokeWidth         = 3;

    // 工具
    private Paint          mPaint;

    // 动画相关参数
    private ObjectAnimator ballScaleAnim;
    private ObjectAnimator backgroundScaleAnim;
    private float          mBallLength             = width;           // 小球长度
    private float          mBackgroundStrokeWidth  = frameStrokeWidth; // 背景矩形的边框宽度
    private float          mBallStartX             = 0;               // 小球的起始坐标

    private boolean        isAnimating             = false;

    private class MyHandler extends Handler {

        private WeakReference<Context> activityWeakReference;

        public MyHandler(Context activity){
            activityWeakReference = new WeakReference<Context>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Context activity = activityWeakReference.get();
            if (activity != null) {

                switch (msg.what) {
                    case 1:
                        onDown();
                        onTapUp();
                        break;
                    case 2:
                        onCheckedChangeListener.onCheckedChanged(isChecked);
                        postInvalidate();
                        break;
                }

            }
        }
    }

    private MyHandler               animHandler = new MyHandler(getContext());

    private OnCheckedChangeListener onCheckedChangeListener;

    {
        onCheckedChangeListener = new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(boolean isChecked) {

            }
        };
    }

    public IOSSwitch(Context context){
        super(context);
        init();
    }

    public IOSSwitch(Context context, AttributeSet attrs){
        super(context, attrs);
        setAttributes(context, attrs);
        init();
    }

    protected void setAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IOSSwitch);

        ballColor = a.getColor(R.styleable.IOSSwitch_ballColor, Color.WHITE);
        bgColor = a.getColor(R.styleable.IOSSwitch_bgColor, Colors.IOS_GREEN);
        isChecked = a.getBoolean(R.styleable.IOSSwitch_checked, false);

        a.recycle();

    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        if (isChecked) {
            isChecked = !isChecked;
            animHandler.sendEmptyMessageDelayed(1, 200);
        }
    }

    private void onDown() {
        if (!isChecked) {

            // 按下时小球变宽一点点,同时灰色背景向内收缩
            ballScaleAnim = ofFloat(IOSSwitch.this, "mBallLength", mBallLength * 1.2f);
            ballScaleAnim.setDuration(300);
            ballScaleAnim.setInterpolator(new DecelerateInterpolator());
            ballScaleAnim.start();

            // 白色背景收缩
            backgroundScaleAnim = ofFloat(IOSSwitch.this, "mBackgroundStrokeWidth", width / 2);
            backgroundScaleAnim.setDuration(400);
            backgroundScaleAnim.setInterpolator(new DecelerateInterpolator());
            backgroundScaleAnim.start();

        } else {
            // 按钮的起始位置向前移动
            ballScaleAnim = ofFloat(IOSSwitch.this, "mBallStartX", mBallStartX - (mBallLength * 0.2f));
            ballScaleAnim.setDuration(300);
            ballScaleAnim.setInterpolator(new DecelerateInterpolator());
            ballScaleAnim.start();
            mBallLength = mBallLength * 1.2f;
        }
    }

    private void onTapUp() {
        if (ballScaleAnim != null) ballScaleAnim.cancel();
        // if (backgroundScaleAnim != null) backgroundScaleAnim.cancel();
        // 小球恢复
        ObjectAnimator ballScaleAnim = ofFloat(IOSSwitch.this, "mBallLength", height);
        ballScaleAnim.setDuration(300);
        ballScaleAnim.setInterpolator(new DecelerateInterpolator());
        ballScaleAnim.start();
        if (!isOutSide) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isChecked = !isChecked;
                    animHandler.sendEmptyMessage(2);
                }
            });
            if (isChecked) {
                off(animatorSet);
            } else {
                on(animatorSet);
            }
        } else {
            if (!isChecked) {
                // 背景恢复
                ObjectAnimator backgroundScaleAnim = ofFloat(IOSSwitch.this, "mBackgroundStrokeWidth", frameStrokeWidth);
                backgroundScaleAnim.setDuration(300);
                backgroundScaleAnim.setInterpolator(new DecelerateInterpolator());
                backgroundScaleAnim.start();

            } else {
                // 小球恢复
                mBallLength = height;
                ballScaleAnim = ofFloat(IOSSwitch.this, "mBallStartX", mBallStartX + (mBallLength * 0.2f));
                ballScaleAnim.setDuration(300);
                ballScaleAnim.setInterpolator(new DecelerateInterpolator());
                ballScaleAnim.start();

            }

        }

    }

    private void on(AnimatorSet animatorSet) {
        // 按钮移动

        ObjectAnimator ballTransAnim1 = ofFloat(IOSSwitch.this, "mBallStartX", width - frameStrokeWidth - height
                                                                               + frameStrokeWidth + ballStrokeWidth);
        ballTransAnim1.setDuration(250);
        ballTransAnim1.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator ballTransAnim2 = ofFloat(IOSSwitch.this, "mBallStartX", width - frameStrokeWidth - height);
        ballTransAnim2.setDuration(80);
        ballTransAnim2.setInterpolator(new DecelerateInterpolator());
        animatorSet.playSequentially(ballTransAnim1, ballTransAnim2);
        animatorSet.start();

        // 背景改变
        ObjectAnimator bgColorAnim = ObjectAnimator.ofInt(this, "frameColor", frameColor, bgColor);
        bgColorAnim.setEvaluator(new ArgbEvaluator());
        bgColorAnim.setDuration(200);
        bgColorAnim.start();
    }

    private void off(AnimatorSet animatorSet) {
        // 按钮移动

        ObjectAnimator ballTransAnim1 = ofFloat(IOSSwitch.this, "mBallStartX", -frameStrokeWidth - ballStrokeWidth);
        ballTransAnim1.setDuration(250);

        ballTransAnim1.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator ballTransAnim2 = ofFloat(IOSSwitch.this, "mBallStartX", 0);
        ballTransAnim2.setDuration(80);
        ballTransAnim2.setInterpolator(new DecelerateInterpolator());

        animatorSet.playSequentially(ballTransAnim1, ballTransAnim2);
        animatorSet.start();
        // 背景恢复
        ObjectAnimator bgColorAnim = ObjectAnimator.ofInt(this, "frameColor", frameColor, Colors.IOS_GRAY);
        bgColorAnim.setEvaluator(new ArgbEvaluator());
        bgColorAnim.setDuration(200);
        bgColorAnim.start();
        // 背景遮罩恢复
        ObjectAnimator backgroundScaleAnim = ofFloat(IOSSwitch.this, "mBackgroundStrokeWidth", frameStrokeWidth);
        backgroundScaleAnim.setDuration(300);
        backgroundScaleAnim.setInterpolator(new DecelerateInterpolator());
        backgroundScaleAnim.start();
    }

    private boolean isOutSide = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOutSide = false;
                if (isAnimating) {
                    return false;
                }
                onDown();
                return true;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                if (y < -100 || y > height + 100) {
                    isOutSide = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isOutSide = true;
                break;
            case MotionEvent.ACTION_UP:
                onTapUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (width > 0 && height > 0) {
            initData();
        }
        // 画框
        drawFrame(canvas);
        // 画背景
        drawBackground(canvas);
        // 画小球
        drawBall(canvas);
    }

    private void drawFrame(Canvas canvas) {
        mPaint.setColor(frameColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        RectF tmpRect = new RectF();
        tmpRect.set(0, 0, width, height);
        canvas.drawRoundRect(tmpRect, height / 2, height / 2, mPaint);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(bgColorUncheckedUnTaped);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        RectF tmpRect = new RectF();
        tmpRect.set(mBackgroundStrokeWidth, mBackgroundStrokeWidth, width - mBackgroundStrokeWidth,
                    height - mBackgroundStrokeWidth);
        canvas.drawRoundRect(tmpRect, height / 2, height / 2, mPaint);
    }

    private void drawBall(Canvas canvas) {
        // 先画外框（与边框同色），再画内部球
        mPaint.setShadowLayer(10, 0, 12, Colors.IOS_SHADOW);
        mPaint.setColor(frameColor);

        RectF tmpRect = new RectF();

        float right = mBallLength + mBallStartX;
        right = getRightLimit(right);

        tmpRect.set(mBallStartX + frameStrokeWidth, frameStrokeWidth, right, height - frameStrokeWidth);
        canvas.drawRoundRect(tmpRect, height / 2, height / 2, mPaint);
        mPaint.clearShadowLayer();

        mPaint.setColor(ballColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tmpRect = new RectF();
        right = mBallLength - ballStrokeWidth + mBallStartX;
        right = getRightLimit(right);
        tmpRect.set(mBallStartX + frameStrokeWidth + ballStrokeWidth, frameStrokeWidth + ballStrokeWidth, right,
                    height - ballStrokeWidth - frameStrokeWidth);

        canvas.drawRoundRect(tmpRect, height / 2, height / 2, mPaint);

    }

    private float getRightLimit(float right) {
        if (!isChecked) {
            if (right > width) right = width;
        } else {
            if (right >= width - frameStrokeWidth - ballStrokeWidth) {
                right = width - frameStrokeWidth - ballStrokeWidth;
            }

        }
        return right;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec + 10, heightMeasureSpec + 20);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

    }

    private AtomicBoolean mIsInit = new AtomicBoolean(false);

    private void initData() {
        if (mIsInit.compareAndSet(false, true)) {

            mBallLength = height;
            mBackgroundStrokeWidth = frameStrokeWidth;
            if ((width > 0 && height > 0) && (width <= height)) {
                Log.e(getClass().getSimpleName(), "IOSSwitch 设定的长度不能小于高度");
            }
        }

    }

    public float getMBallLength() {
        return mBallLength;
    }

    public void setMBallLength(float mBallLength) {
        this.mBallLength = mBallLength;
        postInvalidate();
    }

    public float getMBackgroundStrokeWidth() {
        return mBackgroundStrokeWidth;
    }

    public void setMBackgroundStrokeWidth(float mBackgroundStrokeWidth) {
        this.mBackgroundStrokeWidth = mBackgroundStrokeWidth;
        postInvalidate();
    }

    public float getMBallStartX() {
        return mBallStartX;
    }

    public void setMBallStartX(float mBallStartX) {
        this.mBallStartX = mBallStartX;
        postInvalidate();
    }

    public int getFrameColor() {
        return frameColor;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    private ObjectAnimator ofFloat(Object target, String name, float... values) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(target, name, values);
        oa.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimating = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isAnimating = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
            }
        });
        return oa;
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void toggle() {
        if (isAnimating) {
            return;
        }
        animHandler.sendEmptyMessage(1);
    }
}

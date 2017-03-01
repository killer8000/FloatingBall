package com.example.ndh.floatingball.sdk;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;


/**
 * @deprecated Created by ndh on 16/12/14.
 */

public class FloatingLayout extends FrameLayout {
    private int layoutCount = 0;
    private float angle = 0;
    private float startAngle = 60;
    private float maxSweepAngle;
    private boolean drawArc = false;
    private boolean needOpen = false;
    private RectF mRfOut;
    private RectF mRfIn;

    public FloatingLayout(Context context) {
        this(context, null);
    }

    public FloatingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint mPaint;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        //必须要有背景颜色,否则drawCircle 画不出来
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawArc) {
            mPaint.setColor(Color.parseColor("#66000000"));
            mPaint.setAlpha((int) ((angle) * 102 / maxSweepAngle));
            canvas.drawArc(mRfOut, startAngle, angle, true, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setAlpha((int) ((angle) * 102 / maxSweepAngle));
            canvas.drawArc(mRfIn, startAngle, angle, true, mPaint);
        }
    }


    public boolean toggle() {
        needOpen = !needOpen;
        if (needOpen) {
            open();
            return true;
        } else {
            close();
            return false;
        }
    }

    private void close() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "", maxSweepAngle, 0f)
                .setDuration(800);
        animator.setRepeatCount(0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drawArc = true;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawArc = false;
                isOpen = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle = (float) animation.getAnimatedValue();
                angle = angle <= maxSweepAngle ? angle : maxSweepAngle;
                layoutCount = (int) (angle / (1.0f * maxSweepAngle / cCount));
                postInvalidate();
                requestLayout();
            }
        });
        animator.start();

    }

    private void open() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "", 0f, maxSweepAngle)
                .setDuration(800);
        animator.setRepeatCount(0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drawArc = true;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isOpen = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle = (float) animation.getAnimatedValue();
                angle = angle <= maxSweepAngle ? angle : maxSweepAngle;
                layoutCount = (int) (angle / (1.0f * maxSweepAngle / cCount));
                postInvalidate();
                requestLayout();
            }
        });
        animator.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);


        inCircle = getChildAt(0).getWidth() / 2;
        outCircle = inCircle + getChildAt(1).getWidth();
        //控件的大小为2倍 FloatingView大小
        setMeasuredDimension(outCircle * 2, outCircle * 2);
    }

    int outCircle = 0;
    int inCircle = 0;
    int measuredWidth = 0;
    int measuredHeight = 0;
    int cCount = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mRfOut = new RectF(measuredWidth / 2 - outCircle, measuredHeight / 2 - outCircle, measuredWidth / 2 + outCircle, measuredHeight / 2 + outCircle);
        mRfIn = new RectF(measuredWidth / 2 - (inCircle), measuredHeight / 2 - (inCircle), measuredWidth / 2 + (inCircle), measuredHeight / 2 + (inCircle));
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        cCount = getChildCount();
        maxSweepAngle = (cCount + 2) * 30;
        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        double base = getChildAt(0).getWidth() * 0.5 + getChildAt(1).getWidth() / 2;
        /**
         * 遍历所有childView根据其宽和高，以及margin进行布局
         */
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            int cl = 0, ct = 0, cr = 0, cb = 0;
            if (i == 0) {
                cl = getMeasuredWidth() / 2 - cWidth / 2 + cParams.leftMargin;
                ct = getMeasuredHeight() / 2 - cHeight / 2 + cParams.topMargin;
            } else {
                if (i <= layoutCount) {
                    cl = getWidth() / 2 - cWidth / 2 - cParams.leftMargin
                            - cParams.rightMargin + (int) (base * Math.cos(Math.PI * (45 * i + 45) / 180));
                    ct = getHeight() / 2 - cHeight / 2 + cParams.topMargin + (int) (base * Math.sin(Math.PI * (i * 45 + 45) / 180));
                } else {
                    cl = 1000000;
                    ct = 1000000;
                }
            }
            cr = cl + cWidth + cParams.rightMargin;
            cb = cHeight + ct + cParams.bottomMargin;
            childView.layout(cl, ct, cr, cb);
        }
    }

    boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }
}

package com.example.ndh.floatingball.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.ndh.floatingball.R;
import com.example.ndh.floatingball.util.Utils;

/**
 * Created by ndh on 16/12/13.
 */

public class FloatingView extends View {
    //阻力效果
    private int scale = 10;
    private int width = 25;
    private int r = width;
    private float startX;
    private float startY;
    //记录手指的移动
    private float moveX;
    private float moveY;
    //中间的圆球
    private Bitmap pinWheelBmp;
    private String mLeft = "left";
    private String mUp = "up";
    private String mRight = "right";
    private String mDown = "down";
    private float textAlpha = 0;
    private Paint mPaint;
    //长按震动，提醒用户可以在屏幕上拖拽控件到任意位置
    private static Vibrator sVibrator;
    boolean isMoving;

    private String drawText;
    private float textX;
    private float textY;
    //定位文字的位置
    private RectF rectFtop;
    private RectF rectFleft;
    private RectF rectFright;
    private RectF rectFdown;
    private RectF textRect;
    //控制圆球的拖动距离
    private float x;
    private float y;
    // 让圆球可以有一定拖出圆圈范围的效果
    private final float circleScale = 3.0f / 4;
    //判断是否点击
    private boolean click;
    //判断是否长按
    private boolean longClick;

    public FloatingView(Context context) {
        this(context, null);
    }

    public FloatingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setTextSize(10);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        setBackgroundColor(Color.TRANSPARENT);
        r = width = Utils.dp2pix(getContext(), width);
        sVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(2 * r, 2 * r);
    }

    @Override
    protected void onDraw(Canvas canvas) {
       Log.d("ndh--", "alpha=" + textAlpha + ",r=" + r * circleScale + "r--" + r + "circleScale=" + circleScale);
        mPaint.setARGB((int) (100), 0, 0, 0);
        int width = getMeasuredWidth() / 2;
        int height = getMeasuredHeight() / 2;
        // 限定圆球移动的圆圈，该圆圈小于圆球的移动范围
        canvas.drawCircle(width, height, r * circleScale, mPaint);
        pinWheelBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        // 限定圆球的大小
        pinWheelBmp = Bitmap.createScaledBitmap(pinWheelBmp, r, r, true);
        Matrix matrix = new Matrix();
        if (within(moveX, moveY)) {
            x = moveX;
            y = moveY;
        }
        //通过matrix控制圆球的移动
        matrix.preTranslate(getMeasuredWidth() / 2 - pinWheelBmp.getWidth() / 2 + x / scale, getMeasuredHeight() / 2 - pinWheelBmp.getHeight() / 2 + y / scale);
        canvas.drawBitmap(pinWheelBmp, matrix, null);
        if (!TextUtils.isEmpty(drawText)) {
            mPaint.setColor(Color.WHITE);
            mPaint.setAlpha((int) ((textAlpha / r) * 255));
            canvas.drawText(drawText, textX, textY, mPaint);
            mPaint.setAlpha(255);
        }
//        canvas.drawPicture();

    }

    private static Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("ndh--", "vibrate---");
            sVibrator.vibrate(Config.VIBRATE_TIME);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //rawX表示距离整个屏幕的x距离  getX表示距离父控件x距离
                startX = event.getRawX();
                startY = event.getRawY();
                click = true;
                longClick = true;
                if (!FloatingWindowManager.create(getContext()).isOpen()) {
                    postDelayed(sRunnable, Config.WAITING_TIME);
                }

            case MotionEvent.ACTION_MOVE:
                // 该view 是通过FloatinigWindowManager加载到窗体,由于 menuItem也是通过该管理器加载的,
                //需要在menuItem加载进窗体的时候 禁止FloatingView的拖拽效果
                if (FloatingWindowManager.create(getContext()).isOpen()) {
                    return true;
                }
                if (canDrag) {
                    moveX = event.getRawX() - startX;
                    moveY = event.getRawY() - startY;
                    if (Math.abs(moveX) > r / 10 || Math.abs(moveY) > r / 10) {
                        removeCallbacks(sRunnable);
                        isMoving = true;
                        click = false;
                        longClick = false;
                        drawText();
                        postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                removeCallbacks(sRunnable);
                isMoving = false;
                if (!isLongClick(event) && click) {
                    FloatingViewManager.create().post(FloatingViewListener.CLICK);
                    break;
                }
                if (Math.abs(moveX / scale) > (r / 4) && Math.abs(moveX) > Math.abs(moveY)) {
                    //左右
                    if (moveX < 0) {
                        FloatingViewManager.create().post(FloatingViewListener.RIGHT);
                    } else {
                        FloatingViewManager.create().post(FloatingViewListener.LEFT);
                    }
                }
                if (Math.abs(moveY / scale) > (r / 4) && Math.abs(moveX) < Math.abs(moveY)) {
                    //上下
                    if (moveY < 0) {
                        FloatingViewManager.create().post(FloatingViewListener.DOWN);
                    } else {
                        FloatingViewManager.create().post(FloatingViewListener.UP);
                    }
                }
                moveX = 0;
                moveY = 0;
                textAlpha = 0;
                postInvalidate();
                break;
        }
        return true;

    }

    private boolean isLongClick(MotionEvent event) {
        long downTime = event.getDownTime();
        long upTime = event.getEventTime();
        if (longClick && (upTime - downTime > Config.WAITING_TIME)) {
            FloatingViewManager.create().post(FloatingViewListener.LONG_CLICK);
            click = false;
            longClick = false;
            return true;
        }
        return false;
    }

    // 画 四个方位的字
    private void drawText() {
        if (Math.abs(moveX) > r / 10 && Math.abs(moveX) > Math.abs(moveY)) {
            textAlpha = Math.abs((int) (moveX * 1.5) / scale);
            //左右方向
            if (moveX < 0) {
                drawText = mRight;
                textRect = rectFright;
                textX = getMeasuredWidth() / 2 + getMeasuredWidth() / 4;
            } else {
                drawText = mLeft;
                textRect = rectFleft;
                textX = getMeasuredWidth() / 2 - getMeasuredWidth() / 4;
            }

        }
        if (Math.abs(moveY) > r / 10 && Math.abs(moveX) < Math.abs(moveY)) {
            textAlpha = Math.abs((int) (moveY * 1.5) / scale);
            //上下方向
            if (moveY < 0) {
                drawText = mDown;
                textRect = rectFdown;
            } else {
                drawText = mUp;
                textRect = rectFtop;
            }
            textX = getMeasuredWidth() / 2;
        }
        textAlpha = textAlpha >= r ? r : textAlpha;
        if (null != textRect)
            textY = (textRect.bottom + textRect.top - mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top) / 2;
    }

    // 限制圆球只能在一个圆形范围移动，该方法只能放在onDraw里面，千万不要放到action_move里面，因为postInvalidate有延时，会导致实际滑动距离偏大
    private boolean within(float startX, float startY) {
        if (null == pinWheelBmp) {
            return false;
        }
        double temp = (Math.sqrt((startX / scale) * (startX / scale) + (startY / scale) * (startY / scale)));
        return temp <= r / 2;
    }

    /**
     * 设置左上右下的文字提示
     *
     * @param left
     * @param up
     * @param right
     * @param down
     */
    @NotProguard
    public void setTips(String left, String up, String right, String down) {
        mLeft = left;
        mUp = up;
        mRight = right;
        mDown = down;
    }
    @NotProguard
    public boolean isMoving() {
        return isMoving;
    }

    boolean canDrag = true;
    @NotProguard
    public void setCanDrag(boolean flag) {
        canDrag = flag;
    }

    /**
     * the width/height of this view
     *
     * @return
     */
    @NotProguard
    public int[] getSize() {
        int[] size = new int[2];
        size[0] = getMeasuredWidth();
        size[1] = getMeasuredHeight();
//        PackageManager pm=get
//        ApplicationInfo applicationInfo
        return size;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //以下四个矩形是为了定位 上下左右 四个方位上的字的位置
        rectFtop = new RectF(getMeasuredWidth() / 2 - r * circleScale, getMeasuredHeight() / 2 - r * circleScale, getMeasuredWidth() / 2 + r * circleScale, getMeasuredHeight() / 2);
        rectFleft = new RectF(getMeasuredWidth() / 2 - r * circleScale, getMeasuredHeight() / 2 - r * circleScale, getMeasuredWidth() / 2, getMeasuredHeight() / 2 + r * circleScale);
        rectFright = new RectF(getMeasuredWidth() / 2, getMeasuredHeight() / 2 - r * circleScale, getMeasuredWidth() / 2 + r * circleScale, getMeasuredHeight() / 2 + r * circleScale);
        rectFdown = new RectF(getMeasuredWidth() / 2 - r * circleScale, getMeasuredHeight() / 2, getMeasuredWidth() / 2 + r * circleScale, getMeasuredHeight() / 2 + r * circleScale);

    }
}

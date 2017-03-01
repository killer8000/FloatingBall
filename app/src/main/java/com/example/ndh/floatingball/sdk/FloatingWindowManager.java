package com.example.ndh.floatingball.sdk;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.ndh.floatingball.util.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndh on 16/12/16.
 */

public class FloatingWindowManager {
    private static WeakReference<Context> mContext;
    //弱引用
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private int duration = Config.DURATION;
    private boolean canLayout = true;//避免快速点击圆球，展开和关闭的动画重复执行，这将导致界面布局异常
    private Point p = new Point();
    private List<View> attachToWindows = new ArrayList<>();
    /**
     * true 表示可以开启  false 表示可以关闭
     */
    private boolean toggle = true;
    private int base = Config.BASE;
    private ObjectAnimator mOpenAnimator;
    private ObjectAnimator mCloseAnimator;

    private FloatingWindowManager() {
        Log.d("ndh--", "createFloatView");
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) mContext.get().getSystemService(mContext.get().WINDOW_SERVICE);
        //设置window type 搞成toast方式 不需要添加权限
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        p = Utils.getScreenSize(mContext.get());
        base = Utils.dp2pix(mContext.get(), base);
        FloatingViewManager.create().register(new FloatingViewListener() {
            @Override
            public void onFinish(int flag) {
                switch (flag) {
                    case FloatingViewListener.DOWN:

                        ActionManager.create().doAction(mContext.get(), Config.MenuPosition.DOWN);
                        break;
                    case FloatingViewListener.UP:
                        //回到桌面
                        ActionManager.create().doAction(mContext.get(), Config.MenuPosition.UP);
                        break;
                    case FloatingViewListener.LEFT:
                        //静音
                        ActionManager.create().doAction(mContext.get(), Config.MenuPosition.LEFT);
                        break;
                    case FloatingViewListener.RIGHT:
                        // 指定开启系统相机的Action
                        ActionManager.create().doAction(mContext.get(), Config.MenuPosition.RIGHT);
                        break;
                    case FloatingViewListener.CLICK:
                        int[] points = new int[2];
                        mFloatView.getLocationOnScreen(points);
                        points[0] = points[0] + mFloatView.getMeasuredWidth() / 2;
                        points[1] = points[1];
                        FloatingWindowManager.create(mContext.get()).toggle(points);

                }
            }
        });
    }

    public static FloatingWindowManager create(Context context) {
        mContext = new WeakReference<Context>(context);

        return SingleInstance.INSTANCE;
    }

    @NotProguard
    public void init() {
        if (isAttachToWindow()) {
            removeAllView();
        }
        ActionManager.create().initAction(mContext.get());
        MenuItemManager.create(mContext.get()).createMenuItem();
        createFloatView();
    }

    private static class SingleInstance {
        public static final FloatingWindowManager INSTANCE = new FloatingWindowManager();
    }

    @NotProguard
    public void toggle(int[] centerPositions) {
        if (!canLayout) {
            return;
        }

        List<View> list = MenuItemManager.create(mContext.get()).getListOfViews();

        if (toggle) {
            open(list, centerPositions);
        } else {
            close(list);
        }
        toggle = !toggle;
    }

    private void attach(View v, int x, int y) {
        if (wmParams == null || mWindowManager == null || v == null) {
            throw new RuntimeException("windowManager not exists / view is null");
        }
        //说明已经当前view已经添加到window上了
        if (attachToWindows.contains(v)) {
            return;
        }
        wmParams.x = x;
        wmParams.y = y;
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(v, wmParams);
        attachToWindows.add(v);
        Log.d("ndh--", "attach--v=" + v + ",x=" + x + ",y=" + y);
    }

    private void setPosition(int x, int y) {
        if (null != wmParams) {
            wmParams.x = x;
            wmParams.y = y;
        }

    }

    private void update(View v) {
        if (!toggle) {
            return;
        }
        if (null != mWindowManager && null != v)
            mWindowManager.updateViewLayout(v, wmParams);

    }

    /**
     * 所有的view的移除都由该方法提供
     *
     * @param v
     */
    private void detach(View v) {
        Log.d("ndh--", "detach--v=" + v);
        if (attachToWindows.contains(v) && null != mWindowManager && null != v) {
            mWindowManager.removeView(v);
            attachToWindows.remove(v);
        }
        toggle = true;
    }

    private boolean isAttachToWindow() {
        return attachToWindows.size() > 0 ? true : false;
    }

    private int getExSize() {
        return base;
    }

    private void close(final List<View> list) {
        //只有5个menu 没个menu摆放距离相差角度45度
        mCloseAnimator = ObjectAnimator.ofFloat(this, "", 0, 45 * 5f)
                .setDuration(duration);
        mCloseAnimator.setRepeatCount(0);
        mCloseAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tempClose.clear();
                canLayout = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (null != mCloseAnimator) {
                    mCloseAnimator = null;
                }
                canLayout = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (null != mCloseAnimator) {
                    mCloseAnimator = null;
                }
                canLayout = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mCloseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                removeView(animation, list);

            }
        });
        mCloseAnimator.start();

    }

    private void removeView(ValueAnimator animation, List<View> list) {
        float value = (float) animation.getAnimatedValue();
        if (list.size() == 0) {
            return;
        }
        int count = (int) (value / (45 * 5 / list.size()));
        for (int i = 0; i < count; i++) {
            //避免重复移除view
            if (tempClose.contains(list.get(list.size() - i - 1))) {
                continue;
            }
            detach(list.get(list.size() - i - 1));
            tempClose.add(list.get(list.size() - i - 1));
        }

    }

    List<View> tempOpen = new ArrayList<View>();
    List<View> tempClose = new ArrayList<View>();


    private void open(final List<View> list, final int[] centerPositions) {

        mOpenAnimator = ObjectAnimator.ofFloat(this, "", 0f, 45 * 5)
                .setDuration(duration);
        mOpenAnimator.setRepeatCount(0);
        mOpenAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 动画前清空集合，在动画执行过程中会 为集合赋值 见addView方法
                tempOpen.clear();
                canLayout = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画
                if (null != mOpenAnimator) {
                    mOpenAnimator = null;
                }
                canLayout = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (null != mOpenAnimator) {
                    mOpenAnimator = null;
                }
                canLayout = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mOpenAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                addView(animation, list, centerPositions);
            }
        });
        mOpenAnimator.start();

    }

    private void addView(ValueAnimator animation, List<View> list, int[] centerPositions) {
        float value = (float) animation.getAnimatedValue();
        if (list.size() == 0) {
            return;
        }
        // 这里通过改变count实现依次展开效果
        int count = (int) (value / (45 * 5 / list.size()));
        for (int i = 0; i < count; i++) {
            // 避免重复添加view
            if (tempOpen.contains(list.get(i))) {
                continue;
            }
            //必须先测量一下 否则拿不到宽/高
            if (0 == list.get(i).getMeasuredWidth()) {
                list.get(i).measure(View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                        .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            }
            if (p.x / 2 > centerPositions[0]) {
                //圆球在屏幕右方 则menu 在圆球左方展开 注意45为弧度，需要转换为角度才能使用
                wmParams.x = centerPositions[0] - list.get(i).getMeasuredWidth() / 2 - (int) (base * Math.cos(Math.PI * (45 * i + 90) / 180));
            } else {
                //圆球在屏幕左方 则menu 在圆球右方展开
                wmParams.x = centerPositions[0] - list.get(i).getMeasuredWidth() / 2 + (int) (base * Math.cos(Math.PI * (45 * i + 90) / 180));

            }
            wmParams.y = centerPositions[1] - list.get(i).getMeasuredHeight() / 2 + (int) (base * Math.sin(Math.PI * (i * 45 + 90) / 180));
            attach(list.get(i), wmParams.x, wmParams.y);
            tempOpen.add(list.get(i));
        }
    }

    @NotProguard
    public boolean isOpen() {
        return !toggle;
    }

    FloatingView mFloatView;

    @NotProguard
    public void createFloatView() {

        mFloatView = new FloatingView(mContext.get().getApplicationContext());
        FloatingWindowManager.create(mContext.get().getApplicationContext()).attach(mFloatView, Utils.getScreenSize(mContext.get().getApplicationContext()).x, Utils.getScreenSize(mContext.get().getApplicationContext()).y / 2);
        mFloatView.setTips(ActionManager.create().getAction(Config.MenuPosition.LEFT), ActionManager.create().getAction(Config.MenuPosition.UP), ActionManager.create().getAction(Config.MenuPosition.RIGHT), ActionManager.create().getAction(Config.MenuPosition.DOWN));
        final int width = mFloatView.getMeasuredWidth() * 2;
        final int height = mFloatView.getMeasuredHeight() * 2;
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mFloatView.isMoving() || event.getEventTime() - event.getDownTime() < Config.WAITING_TIME) {
                    //在非移动的情况下,将触摸事件给会floatingView
                    return false;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int[] posionts = new int[2];
                        mFloatView.getLocationOnScreen(posionts);
                        //getRawX是触摸位置相对于屏幕的坐标
                        if (Math.abs(event.getRawX() - posionts[0]) > width || Math.abs(event.getRawY() - posionts[1]) > height) {
                            setPosition((int) event.getRawX() - width, (int) event.getRawY() - height - 25);
                            update(mFloatView);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        int[] posionts1 = new int[2];
                        mFloatView.getLocationOnScreen(posionts1);
                        // 屏幕上方预留 展开的高度
//                        if (Math.abs(posionts1[0]) < getExSize()) {
//                            setPosition((int) event.getRawX() - width + getExSize(), getExSize());
//                        }
//                        if (Utils.getScreenSize(mContext.get().getApplicationContext()).x - (Math.abs(posionts1[0])) - width < getExSize()) {
//                            setPosition(Utils.getScreenSize(mContext.get().getApplicationContext()).x - (Math.abs(posionts1[0])) - getExSize(), getExSize());
//                        }
                        if (Math.abs(posionts1[1]) < getExSize()) {
                            setPosition((int) event.getRawX() - width, getExSize());
//                            update(mFloatView);
                        }
                        //屏幕下方预留 展开的高度
                        if (Utils.getScreenSize(mContext.get().getApplicationContext()).y - (Math.abs(posionts1[1]) + mFloatView.getMeasuredHeight()) < getExSize()) {
                            setPosition((int) event.getRawX() - width, Utils.getScreenSize(mContext.get().getApplicationContext()).y - getExSize() - mFloatView.getMeasuredHeight() - MenuItemManager.create(mContext.get().getApplicationContext()).getItemHeight() / 2);
//                            update(mFloatView);
                        }
                        update(mFloatView);
                }

                return true;
            }
        });
    }

    @NotProguard
    public void removeAllView() {
        if (mFloatView != null) {
            detach(mFloatView);
            //移除悬浮窗口
            mFloatView = null;
        }
        List<View> list = MenuItemManager.create(mContext.get()).getListOfViews();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                detach(list.get(i));
            }
        }
        MenuItemManager.create(mContext.get()).clear();
    }
}

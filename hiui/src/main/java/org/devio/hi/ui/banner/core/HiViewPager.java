package org.devio.hi.ui.banner.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**
 * 自定义的ViewPager，实现了以下功能：
 * 1.自动播放
 * 实现了自动翻页的ViewPager
 */
public class HiViewPager extends ViewPager {

    //滚动的时间间隔
    private int mIntervalTime;
    /**
     * 是否开启自动轮播
     */
    private boolean mAutoPlay = true;
    private boolean isLayout;

    //结合Handler和Runnable，来实现定时播放的功能
    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {

        public void run() {
            next();
            mHandler.postDelayed(this, mIntervalTime);//延时一定时间执行下一次
        }

    };


    public HiViewPager(@NonNull Context context) {
        super(context);
    }

    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
        if (!mAutoPlay) {
            mHandler.removeCallbacks(mRunnable);
        }
    }


    /**
     * 在自动滚动期间，如果用户有触摸事件，则停止自动播放。当用户松开手时，再继续自动播放
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                start();
                break;
            default:
                stop();
                break;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        isLayout = true;
    }

    /**
     * 通过反射，去获取ViewPager.java的私有变量“mFirstLayout”，以更改mFirstLayout的值，解决RecyclerView + ViewPager结合使用的一个已知问题
     * 
     * 问题细节：
     * ViewPager里有一个私有变量mFirstLayout，它是表示是不是第一次显示布局，如果是true，则使用无动画的方式显示当前item；
     * 如果是false，则使用动画方式显示当前item。
     * 
     * 当ViewPager滚动上去后，因为RecyclerView的回收机制，ViewPager会走onDetachFromWindow，当再次滚动下来时，ViewPager会走onAttachedToWindow。
     * 在onAttachedToWindow中，mFirstLayout被重置为true，所以下一次滚动就没有动画。
     * 
     * 所以这里我们需要重写onAttachedToWindow，通过反射的方式将私有变量“mFirstLayout”设置为false，
     * 
     * fix 使用RecyclerView + ViewPager bug https://blog.csdn.net/u011002668/article/details/72884893
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isLayout && getAdapter() != null && getAdapter().getCount() > 0) {
            try {
                //通过getDeclaredField获取私有变量"mFirstLayout"
                //setAccessible(true)的意思是设置该字段为可访问字段，即使是private，也可以强制访问
                Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
                mFirstLayout.setAccessible(true);
                mFirstLayout.set(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        start();
    }

    /**
     * ViewPager的onDetachFromWindow方法会把动画直接停掉，需要想办法进行保护
     * 当activitydestroy的时候，给自定义ViewPager一个标志位hasActivityDestroy，只有hasActivityDestroy为true的时候，才调用父类的super.onDetachedFromWindow();
     */
    @Override
    protected void onDetachedFromWindow() {
        //fix 使用RecyclerView + ViewPager bug
        if (((Activity) getContext()).isFinishing()) {
            super.onDetachedFromWindow();
        }
        stop();
    }

    /**
     * 利用反射，设置HiBannerScroller中的duration，从而设置ViewPager的滚动速度
     *
     * @param duration page切换的时间长度
     */
    public void setScrollDuration(int duration) {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            //通过getDeclaredField获取私有变量"mScroller"
                //setAccessible(true)的意思是设置该字段为可访问字段，即使是private，也可以强制访问
            scrollerField.setAccessible(true);
            scrollerField.set(this, new HiBannerScroller(getContext(), duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置页面停留时间
     *
     * @param intervalTime 停留时间单位毫秒
     */
    public void setIntervalTime(int intervalTime) {
        this.mIntervalTime = intervalTime;
    }

    public void start() {
        mHandler.removeCallbacksAndMessages(null);
        if (mAutoPlay) {
            mHandler.postDelayed(mRunnable, mIntervalTime);
        }
    }

    /**
     * 停止Timer
     */
    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 设置下一个要显示的item，并返回item的pos
     *
     * @return 下一个要显示item的pos
     */
    private int next() {
        int nextPosition = -1;

        if (getAdapter() == null || getAdapter().getCount() <= 1) {
            stop();
            return nextPosition;
        }
        nextPosition = getCurrentItem() + 1;
        //下一个索引大于adapter的view的最大数量时重新开始，实现无限轮播
        if (nextPosition >= getAdapter().getCount()) {
            nextPosition = ((HiBannerAdapter) getAdapter()).getFirstItem();
        }
        setCurrentItem(nextPosition, true);
        return nextPosition;
    }
}

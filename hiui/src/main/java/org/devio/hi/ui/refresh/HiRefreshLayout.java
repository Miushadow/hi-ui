package org.devio.hi.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import org.devio.hi.library.log.HiLog;
import org.devio.hi.ui.refresh.HiOverView.HiRefreshState;

/**
 * 下拉刷新界面的外层容器View
 */
public class HiRefreshLayout extends FrameLayout implements HiRefresh {
    private static final String TAG = HiRefreshLayout.class.getSimpleName();
    private HiRefreshState mState;
    private GestureDetector mGestureDetector;
    private AutoScroller mAutoScroller;
    private HiRefresh.HiRefreshListener mHiRefreshListener;
    protected HiOverView mHiOverView;
    //下拉距离的Y轴坐标
    private int mLastY;
    //刷新时是否禁止滚动
    private boolean disableRefreshScroll;

    public HiRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HiRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HiRefreshLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), hiGestureDetector);
        mAutoScroller = new AutoScroller();
    }

    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinished() {
        final View head = getChildAt(0);
        HiLog.i(this.getClass().getSimpleName(), "refreshFinished head-bottom:" + head.getBottom());
        mHiOverView.onFinish();
        mHiOverView.setState(HiRefreshState.STATE_INIT);
        final int bottom = head.getBottom();
        if (bottom > 0) {
            //下over pull 200，height 100
             //  bottom  =100 ,height 100
            recover(bottom);
        }
        mState = HiRefreshState.STATE_INIT;
    }

    @Override
    public void setRefreshListener(HiRefresh.HiRefreshListener hiRefreshListener) {
        mHiRefreshListener = hiRefreshListener;
    }

    /**
     * 设置下拉刷新的视图
     *
     * @param hiOverView
     */
    @Override
    public void setRefreshOverView(HiOverView hiOverView) {
        if (this.mHiOverView != null) {
            removeView(mHiOverView);
        }
        this.mHiOverView = hiOverView;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mHiOverView, 0, params);
    }

    HiGestureDetector hiGestureDetector = new HiGestureDetector() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float disX, float disY) {
            //横向滑动，或刷新被禁止则不处理
            if (Math.abs(disX) > Math.abs(disY) || mHiRefreshListener != null && !mHiRefreshListener.enableRefresh()) {
                return false;
            }
            
            //刷新时是否禁止滑动
            if (disableRefreshScroll && mState == HiRefreshState.STATE_REFRESH) {
                return true;
            }

            View head = getChildAt(0);
            View child = HiScrollUtil.findScrollableChild(HiRefreshLayout.this);
            //如果列表发生了滚动则不处理
            if (HiScrollUtil.childScrolled(child)) {
                return false;
            }

            //没有刷新或没有达到可以刷新的距离，且头部已经划出或下拉
            if ((mState != HiRefreshState.STATE_REFRESH || head.getBottom() <= mHiOverView.mPullRefreshHeight) && (head.getBottom() > 0 || disY <= 0.0F)) {
                //还在滑动中
                if (mState != HiRefreshState.STATE_OVER_RELEASE) {
                    int speed;
                    //根据阻尼，计算出滑动的速度
                    if (child.getTop() < mHiOverView.mPullRefreshHeight) {
                        speed = (int) (mLastY / mHiOverView.minDamp);
                    } else {
                        speed = (int) (mLastY / mHiOverView.maxDamp);
                    }
                    //如果是正在刷新状态，则不允许在滑动的时候改变状态
                    boolean bool = moveDown(speed, true);
                    mLastY = (int) (-disY);
                    return bool;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    };

    /**
     * 对手势事件进行拦截并处理
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //事件分发处理
        if (!mAutoScroller.isFinished()) {
            return false;
        }

        //获取整个ViewGroup的最外层容器
        View head = getChildAt(0);
        //该手势判断为用户松开手的事件
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {
            //如果head底部坐标不为0，说明顶部视图已经被用户拉下来了
            if (head.getBottom() > 0) {
                if (mState != HiRefreshState.STATE_REFRESH) {//如果当前组件的状态不是正在刷新的状态
                    //将组件滚回原位置
                    recover(head.getBottom());
                    return false;
                }
            }
            mLastY = 0;
        }

        //判断用户下拉一直没有松手的状态
        boolean consumed = mGestureDetector.onTouchEvent(ev);
        HiLog.i(TAG, "gesture consumed：" + consumed);
        if ((consumed || (mState != HiRefreshState.STATE_INIT && mState != HiRefreshState.STATE_REFRESH)) && head.getBottom() != 0) {
            //让父类感受不到真实事件，由我们直接进行伪消费
            ev.setAction(MotionEvent.ACTION_CANCEL);
            return super.dispatchTouchEvent(ev);
        }

        if (consumed) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //定义head和child的排列位置
        View head = getChildAt(0);
        View child = getChildAt(1);
        if (head != null && child != null) {
            HiLog.i(TAG, "onLayout head-height:" + head.getMeasuredHeight());
            int childTop = child.getTop();
            if (mState == HiRefreshState.STATE_REFRESH) {
                head.layout(0, mHiOverView.mPullRefreshHeight - head.getMeasuredHeight(), right, mHiOverView.mPullRefreshHeight);
                child.layout(0, mHiOverView.mPullRefreshHeight, right, mHiOverView.mPullRefreshHeight + child.getMeasuredHeight());
            } else {
                //left,top,right,bottom
                head.layout(0, childTop - head.getMeasuredHeight(), right, childTop);
                child.layout(0, childTop, right, childTop + child.getMeasuredHeight());
            }

            View other;
            //让HiRefreshLayout节点下两个以上的child能够不跟随手势移动以实现一些特殊效果，如悬浮的效果
            for (int i = 2; i < getChildCount(); ++i) {
                other = getChildAt(i);
                other.layout(0, top, right, bottom);
            }
            HiLog.i(TAG, "onLayout head-bottom:" + head.getBottom());
        }
    }

    //让视图滚动回指定位置
    private void recover(int dis) {//dis =200  200-100
        if (mHiRefreshListener != null && dis > mHiOverView.mPullRefreshHeight) {
            mAutoScroller.recover(dis - mHiOverView.mPullRefreshHeight);
            mState = HiRefreshState.STATE_OVER_RELEASE;
        } else {
            mAutoScroller.recover(dis);
        }
    }

    /**
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量
     * @param nonAuto 是否非自动滚动触发
     * @return
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {
        HiLog.i("111", "changeState:" + nonAuto);
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;

        HiLog.i("-----", "moveDown head-bottom:" + head.getBottom() + ",child.getTop():" + child.getTop() + ",offsetY:" + offsetY);
        //异常情况的补充
        if (childTop <= 0) {
            HiLog.i(TAG, "childTop<=0,mState" + mState);
            //遇到这种异常情况时，对OffsetY进行重置
            offsetY = -child.getTop();
            //移动head与child的位置，到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (mState != HiRefreshState.STATE_REFRESH) {
                mState = HiRefreshState.STATE_INIT;
            }
        } else if (mState == HiRefreshState.STATE_REFRESH && childTop > mHiOverView.mPullRefreshHeight) {
            //如果正在下拉刷新中，禁止继续下拉
            return false;
        } else if (childTop <= mHiOverView.mPullRefreshHeight) {//还没超出设定的刷新距离
            //判断头部是否开始显示
            if (mHiOverView.getState() != HiRefreshState.STATE_VISIBLE && nonAuto) {
                mHiOverView.onVisible();
                mHiOverView.setState(HiRefreshState.STATE_VISIBLE);
                mState = HiRefreshState.STATE_VISIBLE;
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (childTop == mHiOverView.mPullRefreshHeight && mState == HiRefreshState.STATE_OVER_RELEASE) {
                HiLog.i(TAG, "refresh，childTop：" + childTop);
                refresh();
            }
        } else {
            if (mHiOverView.getState() != HiRefreshState.STATE_OVER && nonAuto) {
                //告诉HiOverView，已超出刷新位置
                mHiOverView.onOver();
                mHiOverView.setState(HiRefreshState.STATE_OVER);
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }
        if (mHiOverView != null) {
            //告诉HiOverView下拉刷新滚动的距离
            mHiOverView.onScroll(head.getBottom(), mHiOverView.mPullRefreshHeight);
        }
        return true;
    }


    /**
     * 刷新
     */
    private void refresh() {
        if (mHiRefreshListener != null) {
            mState = HiRefreshState.STATE_REFRESH;
            mHiOverView.onRefresh();
            mHiOverView.setState(HiRefreshState.STATE_REFRESH);
            mHiRefreshListener.onRefresh();
        }
    }


    /**
     * 借助Scroller实现视图的自动滚动
     * https://juejin.im/post/5c7f4f0351882562ed516ab6
     */
    private class AutoScroller implements Runnable {
        private Scroller mScroller;
        private int mLastY;
        private boolean mIsFinished;

        AutoScroller() {
            mScroller = new Scroller(getContext(), new LinearInterpolator());
            mIsFinished = true;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {//还未滚动完成
                moveDown(mLastY - mScroller.getCurrY(), false);
                mLastY = mScroller.getCurrY();
                post(this);
            } else {
                removeCallbacks(this);
                mIsFinished = true;
            }
        }

        /*
         * 触发滚动
         */
        void recover(int dis) {
            if (dis <= 0) {
                return;
            }
            removeCallbacks(this);
            mLastY = 0;
            mIsFinished = false;
            mScroller.startScroll(0, 0, 0, dis, 300);
            post(this);
        }

        boolean isFinished() {
            return mIsFinished;
        }

    }
}
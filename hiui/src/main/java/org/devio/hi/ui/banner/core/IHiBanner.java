package org.devio.hi.ui.banner.core;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import org.devio.hi.ui.banner.HiBanner;
import org.devio.hi.ui.banner.indicator.HiIndicator;

import java.util.List;

/**
 * HiBanner对外统一接口
 */
public interface IHiBanner {
    
    //通过注入layoutResId，为Banner设置需加载的视图；通过注入HiBannerMo，为Banner设置具体的数据
    void setBannerData(@LayoutRes int layoutResId, @NonNull List<? extends HiBannerMo> models);

    //通过注入HiBannerMo，为Banner设置具体的数据（该方法存在一个默认的视图）
    void setBannerData(@NonNull List<? extends HiBannerMo> models);

    //为Banner设置指示器（如底部的原点指示器）
    void setHiIndicator(HiIndicator hiIndicator);

    //设置是否自动播放
    void setAutoPlay(boolean autoPlay);

    //设置是否循环播放
    void setLoop(boolean loop);

    //设置页面停留时间
    void setIntervalTime(int intervalTime);

    //设置与Banner进行数据绑定的Adapter
    void setBindAdapter(IBindAdapter bindAdapter);

    //设置页面变换的监听器
    void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

    //设置Banner点击后的监听器
    void setOnBannerClickListener(HiBanner.OnBannerClickListener onBannerClickListener);

    //设置滚动的速度
    void setScrollDuration(int duration);

    //Banner点击后的监听事件接口
    interface OnBannerClickListener {
        void onBannerClick(@NonNull HiBannerAdapter.HiBannerViewHolder viewHolder, @NonNull HiBannerMo bannerMo, int position);
    }
}

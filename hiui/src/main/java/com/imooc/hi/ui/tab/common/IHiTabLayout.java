package com.imooc.hi.ui.tab.common;

import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IHiTabLayout<Tab extends ViewGroup, D> {

    /**
     * 通过数据data查找对应的tab
     * @param data
     * @return
     */
    Tab findTab(@NonNull D data);

    /**
     * 添加Tab被选中时的listener
     * @param listener
     */
    void addTabSelectedChangeListener(OnTabSelectedListener<D> listener);

    /**
     * 设置默认选中
     * @param defaultInfo
     */
    void defaultSelected(@NonNull D defaultInfo);

    /**
     * 对数据进行初始化，填充信息
     * @param infoList
     */
    void inflateInfo(@NonNull List<D> infoList);

    /**
     * Tab被选中时的监听器
     * @param <D>
     */
    interface OnTabSelectedListener<D> {
        void onTabSelectedChange(int index, @Nullable D prevInfo, @NonNull D nextInfo);
    }


}

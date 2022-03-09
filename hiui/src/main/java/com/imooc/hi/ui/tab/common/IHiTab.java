package com.imooc.hi.ui.tab.common;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

/**
 * HiTab对外接口
 */
public interface IHiTab<D> extends IHiTabLayout.OnTabSelectedListener<D> {

    /**
     * 设置Tab中的数据
     */
    void setHiTabInfo(@NonNull D data);

    /**
     * 动态修改某个item的高度
     * @param height
     */
    void resetHeight(@Px int height);


}

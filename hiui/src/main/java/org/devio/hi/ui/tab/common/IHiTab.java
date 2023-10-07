package org.devio.hi.ui.tab.common;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

/**
 * 底部单个Tab控件的对外接口
 */
public interface IHiTab<D> extends IHiTabLayout.OnTabSelectedListener<D> {

    /**
     为Tab设置数据
     */
    void setHiTabInfo(@NonNull D data);

    /**
     * 动态修改Tab的高度
     *
     * @param height
     */
    void resetHeight(@Px int height);

}

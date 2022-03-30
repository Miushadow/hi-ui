package com.imooc.hi.ui.tab.bottom;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imooc.hi.ui.R;
import com.imooc.hi.ui.tab.common.IHiTab;

import javax.xml.transform.Templates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HiTabBottom extends RelativeLayout implements IHiTab<HiTabBottomInfo<?>> {

    private HiTabBottomInfo<?> tabInfo;
    private ImageView tabImageView;
    private TextView tabIconView;
    private TextView tabNameView;

    public HiTabBottom(Context context) {
        this(context, null);
    }

    public HiTabBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HiTabBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.hi_tab_bottom, this);
        tabImageView = findViewById(R.id.iv_image);
        tabIconView = findViewById(R.id.tv_icon);
        tabNameView = findViewById(R.id.tv_name);
    }

    @Override
    public void setHiTabInfo(@NonNull HiTabBottomInfo<?> data) {
        this.tabInfo = data;
        //重新设置info时，选中状态是false，初始化状态为true
        inflateInfo(false, true);
    }

    /**
     * 将数据绑定到视图中
     * @param selected HiTabBottom是否被选中
     * @param init 是否是初始化
     */
    private void inflateInfo(boolean selected, boolean init) {
        if (tabInfo.tabType == HiTabBottomInfo.TabType.ICON) {
            if (init) {
                tabImageView.setVisibility(GONE);
                tabIconView.setVisibility(VISIBLE);
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), tabInfo.iconFont);
                tabIconView.setTypeface(typeface);
                //判断tabInfo中传入的name是否为空，不为空则将name显示到视图中
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }
        }




    }

    public HiTabBottomInfo<?> getTabInfo() {
        return tabInfo;
    }

    public ImageView getTabImageView() {
        return tabImageView;
    }

    public TextView getTabIconView() {
        return tabIconView;
    }

    public TextView getTabNameView() {
        return tabNameView;
    }

    @Override
    public void resetHeight(int height) {

    }

    @Override
    public void onTabSelectedChange(int index, @Nullable HiTabBottomInfo<?> prevInfo, @NonNull HiTabBottomInfo<?> nextInfo) {

    }
}

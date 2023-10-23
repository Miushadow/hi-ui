package org.devio.hi.ui.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import org.devio.hi.ui.R;
import org.devio.hi.ui.tab.common.IHiTab;

/**
 * HiTabBottom: 底部单个Tab控件
 *
 * 1.由于该控件包含图标和文字两个部分，并且需要动态设置图标的高度，所以需要继承RelativeLayout
 * 2.需要实现IHiTab接口，通过setHiTabInfo这个方法为控件来注入数据HiTabBottomInfo
 *
 * 备注：由于目前还不确定用户传什么颜色，所以HiTabBottomInfo里面接受的泛型暂时先传"?"
 */
public class HiTabBottom extends RelativeLayout implements IHiTab<HiTabBottomInfo<?>> {

    //HiTabBottom所对应的具体数据
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

    /**
     所有的构造方法都调用这个参数最多的构造方法
     */
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

    /**
     * 将传入的TabBottom数据存到类变量tabInfo中，并将数据以初始化的形式填充到Tab中
     * 填充数据时，由于是初始化，所以selected为false，init为true
     */
    @Override
    public void setHiTabInfo(@NonNull HiTabBottomInfo hiTabBottomInfo) {
        this.tabInfo = hiTabBottomInfo;
        inflateInfo(false, true);
    }

    public HiTabBottomInfo getHiTabInfo() {
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

    /**
     * 动态修改某个tab的高度，这也是为什么要使用动态布局的原因
     *
     * @param height
     */
    @Override
    public void resetHeight(@Px int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        getTabNameView().setVisibility(View.GONE);
    }

    /**
     * 为HiTabBottom视图进行数据填充
     *
     * @param selected 判断HiTabBottom是否被选中
     * @param init 通过该参数来标识是否是填充初始化数据
     */
    private void inflateInfo(boolean selected, boolean init) {
        if (tabInfo.tabType == HiTabBottomInfo.TabType.ICON) {
            if (init) {
                tabImageView.setVisibility(GONE);
                tabIconView.setVisibility(VISIBLE);
                //通过TypeFace中的API，将传入的iconFont设置给tabIconView
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), tabInfo.iconFont);
                tabIconView.setTypeface(typeface);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }

            //如果被选中了，需要将TextColor设置成选中状态的Color，否则使用默认Color
            if (selected) {
                tabIconView.setText(TextUtils.isEmpty(tabInfo.selectedIconName) ? tabInfo.defaultIconName : tabInfo.selectedIconName);
                tabIconView.setTextColor(getTextColor(tabInfo.tintColor));
                tabNameView.setTextColor(getTextColor(tabInfo.tintColor));
            } else {
                tabIconView.setText(tabInfo.defaultIconName);
                tabIconView.setTextColor(getTextColor(tabInfo.defaultColor));
                tabNameView.setTextColor(getTextColor(tabInfo.defaultColor));
            }

        } else if (tabInfo.tabType == HiTabBottomInfo.TabType.BITMAP) {
            if (init) {
                tabImageView.setVisibility(VISIBLE);
                tabIconView.setVisibility(GONE);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }
            //如果被选中了，则将Bitmap设置成对应的选中状态下的Bitmap，否则使用默认Bitmap
            if (selected) {
                tabImageView.setImageBitmap(tabInfo.selectedBitmap);
            } else {
                tabImageView.setImageBitmap(tabInfo.defaultBitmap);
            }
        }
    }

    @Override
    public void onTabSelectedChange(int index, @Nullable HiTabBottomInfo<?> prevInfo, @NonNull HiTabBottomInfo<?> nextInfo) {
        /*
            针对两种情况：
            1.重复选择同一Tab
            2.TabBottom的选中发生了改变，但是跟当前Tab无关
         */
        if (prevInfo != tabInfo && nextInfo != tabInfo || prevInfo == nextInfo) {
            return;
        }
        /*
            1.prevInfo == tabInfo: 表示从当前的tab跳到了其它tab
            2.prevInfo != tabInfo: 表示从其它tab跳到当前tab
         */
        if (prevInfo == tabInfo) {
            inflateInfo(false, false);
        } else {
            inflateInfo(true, false);
        }
    }

    @ColorInt
    private int getTextColor(Object color) {
        if (color instanceof String) {
            return Color.parseColor((String) color);
        } else {
            return (int) color;
        }
    }
}

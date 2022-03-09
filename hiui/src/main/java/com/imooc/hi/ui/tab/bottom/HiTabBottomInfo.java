package com.imooc.hi.ui.tab.bottom;

import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;

/**
 * 单个底部导航Tab的数据模型
 */
//支持Color的泛型，为了color的可扩展性，不仅支持int类型的color，也支持string类型的color
public class HiTabBottomInfo<Color> {

    //内部枚举类，TabType类型，既支持BITMAP，也支持IconFont（字体图标，布局中用TextView来创建）
    public enum TabType {
        BITMAP, ICON
    }

    public Class<? extends Fragment> fragment;
    public String name;
    public Bitmap defaultBitmap;
    public Bitmap selectedBitmap;
    public String iconFont;
    /**
     * Tips：在Java代码中直接设置iconFont字符串无效，需要定义在string.xml
     */
    public String defaultIconName;
    public String selectedIconName;
    public Color defaultColor;
    public Color tintColor;
    public TabType tabType;

    public HiTabBottomInfo(String name, Bitmap defaultBitmap, Bitmap selectedBitmap) {
        this.name = name;
        this.defaultBitmap = defaultBitmap;
        this.selectedBitmap = selectedBitmap;
    }

    public HiTabBottomInfo(String name, String iconFont, String defaultIconName, String selectedIconName, Color defaultColor, Color tintColor) {
        this.name = name;
        this.iconFont = iconFont;
        this.defaultIconName = defaultIconName;
        this.selectedIconName = selectedIconName;
        this.defaultColor = defaultColor;
        this.tintColor = tintColor;
    }
}

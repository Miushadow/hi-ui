package org.devio.hi.ui.tab.bottom;

import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;


/**
 * HiTabBottomInfo: 单个Tab对应的实体数据类
 *
 * 该类为泛型类，支持传入Color这样一个泛型，将Color设计为泛型的意义是为了Color的扩展性。
 * 在该类中，不仅支持int类型的Color，也支持String类型的Color
 */
public class HiTabBottomInfo<Color>{

    /**
     枚举：
     enum关键字用于声明枚举，后面的TabType为定义的枚举名称，里面的BITMAP和ICON为枚举的成员
     如果没有显示地声明基础类型，那么意味着它对应的基础类型为int
     */
    public enum TabType {
        BITMAP, ICON
    }

    /**
     * 成员变量介绍：
     * 1.fragment: 因为Tab切换时，会将相关的页面也进行切换，所以这里持有一个fragment的成员变量，用于页面的动态切换
     * 2.iconFont: 字体图标，通过字体工具将字符串转换成矢量化图标，具有与分辨率无关的特性，并且很容易调整大小和颜色等属性
     */
    public Class<? extends Fragment> fragment;
    public String name;
    public Bitmap defaultBitmap;
    public Bitmap selectedBitmap;
    public String iconFont;
    public String defaultIconName;
    public String selectedIconName;
    public Color defaultColor;
    public Color tintColor;
    public TabType tabType;

    /**
     传递Bitmap的构造方法
     */
    public HiTabBottomInfo(String name, Bitmap defaultBitmap, Bitmap selectedBitmap) {
        this.name = name;
        this.defaultBitmap = defaultBitmap;
        this.selectedBitmap = selectedBitmap;
        this.tabType = TabType.BITMAP;
    }

    /**
     传递IconFont的构造方法
     */
    public HiTabBottomInfo(String name, String iconFont, String defaultIconName, String selectedIconName, Color defaultColor, Color tintColor) {
        this.name = name;
        this.iconFont = iconFont;
        this.defaultIconName = defaultIconName;
        this.selectedIconName = selectedIconName;
        this.defaultColor = defaultColor;
        this.tintColor = tintColor;
        this.tabType = TabType.ICON;
    }
}

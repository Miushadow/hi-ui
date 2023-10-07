package org.devio.hi.ui.tab.common;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * 外层容器控件的对外接口
 *
 * 接口后面的<>符号表示这是一个泛型接口
 * 泛型简述：
 * 泛型的本质就是类型参数化，换句话说，就是在定义函数、类或者接口时，将某些数据的类型也定义为参数形式（类型形参），在使用
 * 时再传入具体的类型（类型实参）。
 *
 * 针对本例，表示这是一个泛型接口，在实现该接口时需要传入两个具体的类型参数：
 * #1.Tab extends ViewGroup：表示需要传入一个继承于ViewGroup的任意类
 * #2.D：此处D可以随便写为任意标识，常见的如T、E、K、V等形式的参数都经常用于表示泛型。由于我们要传入一个数据类型的参数，
 * 所以我们将第二个参数名字定位D（Data）
 *
 * 从本例可以看出，泛型可以让函数、接口和类的设计更加灵活。比如设计该接口时，其中的一些方法的定义与Tab中的数据有关，但我
 * 定义接口时并不知道后续Tab有哪些种类，对应的数据是哪种类型，所以这里我就设计了两个类型参数，在实现该接口时再传入。
 */
public interface IHiTabLayout<Tab extends ViewGroup, D> {

    /**
     根据具体的数据来查找对应的Tab
     */
    Tab findTab(@NonNull D data);

    /**
     * 为Tab添加被选中后的监听器
     */
    void addTabSelectedChangeListener(OnTabSelectedListener<D> listener);

    /**
     设置默认选中的数据
     */
    void setDefaultSelectedTab(@NonNull D defaultInfo);

    /**
     对数据列表进行相关的初始化信息填充
     */
    void inflateInfo(@NonNull List<D> infoList);

    /**
     当Tab被选中时回调
     会通知对应的监听器，告诉它被选择的Tab的位置，以及上一个Tab对应的数据，以及下一个Tab对应的数据
     */
    interface OnTabSelectedListener<D> {
        void onTabSelectedChange(int index, @Nullable D prevInfo, @NonNull D nextInfo);
    }
}

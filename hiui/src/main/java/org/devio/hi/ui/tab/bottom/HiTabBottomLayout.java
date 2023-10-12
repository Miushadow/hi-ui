package org.devio.hi.ui.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.devio.hi.library.util.HiDisplayUtil;
import org.devio.hi.library.util.HiViewUtil;
import org.devio.hi.ui.R;
import org.devio.hi.ui.tab.common.IHiTabLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 外层容器控件，用于实现对单Tab组件的管理，继承于FrameLayout，大小是整个Content
 */
public class HiTabBottomLayout extends FrameLayout implements IHiTabLayout<HiTabBottom, HiTabBottomInfo<?>> {

    /**
     * 成员变量分析：
     * 1.tabSelectedChangeListeners: 通过一个集合来存放所有注册的OnTabSelectedListener
     * 2.selectedInfo: 保存当前被选中Tab所对应的数据
     * 3.bottomAlpha: TabBottom整体透明度
     * 4.tabBottomHeight: TabBottom高度
     * 5.bottomLineHeight: TabBottom的头部线条高度
     * 6.bottomLineColor: TabBottom的头部线条颜色
     * 7.infoList: TabBottom所对应的数据列表
     */
    private List<OnTabSelectedListener<HiTabBottomInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();
    private HiTabBottomInfo<?> selectedInfo;
    private float bottomAlpha = 1f;
    private static float tabBottomHeight = 50;
    private float bottomLineHeight = 0.5f;
    private String bottomLineColor = "#dfe0e1";
    private List<HiTabBottomInfo<?>> infoList;

    public HiTabBottomLayout(@NonNull Context context) {
        this(context, null);
    }

    public HiTabBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /*
     * 所有构造方法最后都走到参数最多的这个构造方法
     */
    public HiTabBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 将数据列表中的数据添加到视图中
     */
    @Override
    public void inflateInfo(@NonNull List<HiTabBottomInfo<?>> infoList) {
        if (infoList.isEmpty()) {
            return;
        }
        this.infoList = infoList;
        /*
            多次调用inflateInfo填充数据时，移除之前已经添加的View，防止数据重复添加
            遍历视图中的子View，将除了第0个元素以外的子View都移除(第0个View是中间部分的视图，不用移除)
         */
        for (int i = getChildCount() - 1; i > 0; i--) {
            removeViewAt(i);
        }
        selectedInfo = null;
        addBackground();
        /*
            清除之前添加的HiTabBottom listener
            Tips：Java foreach remove问题，这里如果用for循环来对集合进行遍历并删除集合中的元素时会报错，因为我们不能
            既对集合做增删改查的同时又对集合做遍历，解决方案是用迭代器来实现
         */
        Iterator<OnTabSelectedListener<HiTabBottomInfo<?>>> iterator = tabSelectedChangeListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof HiTabBottom) {
                iterator.remove();
            }
        }
        /*
            手动创建底部Tab的FrameLayout，设置其高度，并通过infoList的数量来计算出每一个Tab的宽度
         */
        FrameLayout ll = new FrameLayout(getContext());
        int height = HiDisplayUtil.dp2px(tabBottomHeight, getResources());
        int width = HiDisplayUtil.getDisplayWidthInPx(getContext()) / infoList.size();
        //设置一个TAG，方便与其他Tab进行区分
        ll.setTag(TAG_TAB_BOTTOM);
        for (int i = 0; i < infoList.size(); i++) {
            final HiTabBottomInfo<?> info = infoList.get(i);
            /*
             * Tips：为何不用LinearLayout：因为TabBottom控件的高度是可以动态调整的，我们设置图标时是可以设置为超出其默认高度的。
             * 如果使用LinearLayout得话，当动态改变child大小后Gravity.BOTTOM会失效，所以我们使用FrameLayout来构建底部布局
            */
            LayoutParams params = new LayoutParams(width, height);
            params.gravity = Gravity.BOTTOM;
            params.leftMargin = i * width;

            /*
                实例化TabBottom，为每一个TabBottom实例都注册一个tabSelectedChangeListener，并将每一个TabBottom
                对应的数据添加进去
             */
            HiTabBottom tabBottom = new HiTabBottom(getContext());
            tabSelectedChangeListeners.add(tabBottom);
            tabBottom.setHiTabInfo(info);
            //将继承于RelativeLayout的控件HiTabBottom按照从左到右的顺序进行排列，添加到FrameLayout中
            ll.addView(tabBottom, params);
            tabBottom.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //处理回调，当单个Tab被点击时，通知所有的Listener，处理选中的逻辑。
                    onSelected(info);
                }
            });
        }
        LayoutParams flPrams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        flPrams.gravity = Gravity.BOTTOM;
        addBottomLine();
        //将FrameLayout添加到整个容器的底部
        addView(ll, flPrams);
        //修复内容区域的底部部分内容被TabBottom遮挡的问题
        fixContentView();
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<HiTabBottomInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    public void setTabAlpha(float alpha) {
        this.bottomAlpha = alpha;
    }

    public void setTabHeight(float tabHeight) {
        this.tabBottomHeight = tabHeight;
    }

    public void setBottomLineHeight(float bottomLineHeight) {
        this.bottomLineHeight = bottomLineHeight;
    }

    public void setBottomLineColor(String bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
    }

    /**
     * 通过数据来找到对应的Tab
     * 
     * 这里需要为FrameLayout设置一个Tag，然后通过这个Tag找到对应的ViewGroup，
     * 再遍历这个ViewGroup，针对每个Tab，通过getHiTabInfo获取对应的info，进行比对
     */
    @Nullable
    @Override
    public HiTabBottom findTab(@NonNull HiTabBottomInfo<?> info) {
        ViewGroup ll = findViewWithTag(TAG_TAB_BOTTOM);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof HiTabBottom) {
                HiTabBottom tab = (HiTabBottom) child;
                if (tab.getHiTabInfo() == info) {
                    return tab;
                }
            }
        }
        return null;
    }

    @Override
    public void setDefaultSelectedTab(@NonNull HiTabBottomInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    /**
     * 遍历所有的监听器，通知它们TabBottom的选择产生了变化，并调用它们的回调onTabSelectedChange
     */
    private void onSelected(@NonNull HiTabBottomInfo<?> nextInfo) {
        for (OnTabSelectedListener<HiTabBottomInfo<?>> listener : tabSelectedChangeListeners) {
            listener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        this.selectedInfo = nextInfo;
    }

    /**
     * 添加底部的分割线条
     */
    private void addBottomLine() {
        View bottomLine = new View(getContext());
        bottomLine.setBackgroundColor(Color.parseColor(bottomLineColor));

        LayoutParams bottomLineParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HiDisplayUtil.dp2px(bottomLineHeight, getResources()));
        bottomLineParams.gravity = Gravity.BOTTOM;
        bottomLineParams.bottomMargin = HiDisplayUtil.dp2px(tabBottomHeight - bottomLineHeight, getResources());
        addView(bottomLine, bottomLineParams);
        bottomLine.setAlpha(bottomAlpha);
    }

    /**
     * 动态添加背景色
     */
    private void addBackground() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hi_bottom_layout_bg, null);

        LayoutParams params =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HiDisplayUtil.dp2px(tabBottomHeight, getResources()));
        params.gravity = Gravity.BOTTOM;
        addView(view, params);
        view.setAlpha(bottomAlpha);
    }

    private static final String TAG_TAB_BOTTOM = "TAG_TAB_BOTTOM";

    /**
     * 修复内容区域底部部分内容被TabBottom遮挡的问题
     * 加入HiTabBottomLayout后，会导致内容列表部分最底部的内容被遮挡，这时应该将被遮挡的内容往上移动
     */
    private void fixContentView() {
        if (!(getChildAt(0) instanceof ViewGroup)) {
            return;
        }
        ViewGroup rootView = (ViewGroup) getChildAt(0);
        //targetView为HiTabBottomLayout上方的列表视图，可能是RecyclerView/ScrollView/AbsListView中的一种
        ViewGroup targetView = HiViewUtil.findTypeView(rootView, RecyclerView.class);
        if (targetView == null) {
            targetView = HiViewUtil.findTypeView(rootView, ScrollView.class);
        }
        
        if (targetView == null) {
            targetView = HiViewUtil.findTypeView(rootView, AbsListView.class);
        }
        //将视图列表与底部TabView设置padding
        if (targetView != null) {
            targetView.setPadding(0, 0, 0, HiDisplayUtil.dp2px(tabBottomHeight, getResources()));
            targetView.setClipToPadding(false);
        }
    }

    public static void clipBottomPadding(ViewGroup targetView) {
        if (targetView != null) {
            targetView.setPadding(0, 0, 0, HiDisplayUtil.dp2px(tabBottomHeight));
            targetView.setClipToPadding(false);
        }
    }

    public void resizeHiTabBottomLayout() {
        int width = HiDisplayUtil.getDisplayWidthInPx(getContext()) / infoList.size();
        ViewGroup frameLayout = (ViewGroup) getChildAt(getChildCount() - 1);
        int childCount = frameLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View button = frameLayout.getChildAt(i);
            FrameLayout.LayoutParams params = (LayoutParams) button.getLayoutParams();
            params.width = width;
            params.leftMargin = i * width;
            button.setLayoutParams(params);
        }
    }
}

package com.zhiyicx.thinksnsplus.widget.coordinatorlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @Author Jliuer
 * @Date 2017/09/01/11:40
 * @Email Jliuer@aliyun.com
 * @Description 聊天选择位置，解决地图滚动冲突
 */
public class EventBehavior extends AppBarLayout.Behavior {

    private int mParentHeight;
    private View locationView;
    private View markView;
    private View mapView;

    public EventBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventBehavior() {
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        abl.setClipChildren(false);
        locationView = parent.findViewWithTag("location");
        markView = parent.findViewWithTag("mark");
        mapView = parent.findViewWithTag("map");
        mParentHeight = abl.getHeight();
        return super.onLayoutChild(parent, abl, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        int offset = (mParentHeight - child.getBottom()) / 2;
        mapView.setTranslationY(offset);
        locationView.setTranslationY(-offset);
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return super.onTouchEvent(parent, child, ev);
    }

    public void setDragCallback(@Nullable DragCallback callback) {
        super.setDragCallback(callback);
    }


}

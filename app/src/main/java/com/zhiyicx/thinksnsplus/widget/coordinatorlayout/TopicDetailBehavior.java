package com.zhiyicx.thinksnsplus.widget.coordinatorlayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/13:41
 * @Email Jliuer@aliyun.com
 * @Description 话题详情
 */
public class TopicDetailBehavior extends AppBarLayout.Behavior {
    private static final float MAX_SCALE_HEIGHT = 1200;
    private static final int TAG = R.id.iv_topic_bg;

    private View mScaleView;
    private float mTotalDy;
    private float scale = 1.0f;
    private float yy;
    private int height;
    private boolean isRecovering;
    private boolean isAnimate;

    private boolean isRefreshing = false;
    private boolean isDoRefresh = false;

    public TopicDetailBehavior() {
    }

    public TopicDetailBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        boolean up = dy > 0;
        boolean downScale = dy < 0;
        boolean needScale = getTopAndBottomOffset() >= 0 && downScale || scale > 1f;

        if (height == 0) {
            height = mScaleView.getHeight();
        }
        if (needScale && !isRecovering && type == ViewCompat.TYPE_TOUCH) {
            if (up) {
                consumed[1] = dy;
            }
            scale(dy);
        } else {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        }
        LogUtils.d("onNestedPreScroll::");

    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (yy == 0) {
                yy = ev.getY();
            }
        }
        boolean consume = super.onInterceptTouchEvent(parent, child, ev);
        LogUtils.d("onInterceptTouchEvent::" + consume);
        return consume;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent event) {
        if (height == 0) {
            if (mScaleView == null) {
                return super.onTouchEvent(parent, child, event);
            }
            height = mScaleView.getHeight();
        }
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                yy = event.getY();
                LogUtils.d("onTouchEvent:ACTION_DOWN:");
                break;
            case MotionEvent.ACTION_MOVE:
                if (yy != 0) {
                    float distance = event.getY() - yy;
                    yy = event.getY();
                    int dy = (int) distance / 2;
                    if ((getTopAndBottomOffset() >= 0 &&
                            mScaleView.getScaleX() == 1.0 && dy > 0) || mScaleView.getScaleX() > 1) {
                        LogUtils.d("onTouchEvent:ACTION_MOVE:scale");
                        scale(-dy);
                        // 自主缩放的时候要拦截一下 behavior 的默认事件
                        return true;
                    } else {
                        LogUtils.d("onTouchEvent:ACTION_MOVE:");
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                yy = 0;
                LogUtils.d("onTouchEvent:ACTION_CANCEL:");
                if (scale > 1) {
                    recovery();
                }
                break;
            default:
        }
        return super.onTouchEvent(parent, child, event);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        isAnimate = true;
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        if (velocityY > 100) {
            isAnimate = false;
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {
        if (scale > 1) {
            recovery();
        }
        super.onStopNestedScroll(coordinatorLayout, abl, target, type);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        if (mScaleView == null) {
            mScaleView = parent.findViewById(TAG);
            if (height == 0) {
                height = mScaleView.getHeight();
            }
        }
        abl.addOnOffsetChangedListener((appBarLayout, i) -> {
                    float point = (float) Math.abs(i) / (float) appBarLayout.getTotalScrollRange();
                    if (onRefreshChangeListener != null) {
                        int alpha = Math.round(point * 255);
                        if (alpha > 255) {
                            alpha = 255;
                        }
                        String hex = Integer.toHexString(alpha).toUpperCase();
                        if (hex.length() == 1) {
                            hex = "0" + hex;
                        }
                        String titleColor = "#" + hex + "000000";
                        String bgColor = "#" + hex + "ffffff";
                        onRefreshChangeListener.alphaChange(point, Color.parseColor(titleColor), Color.parseColor(bgColor),
                                UIUtils.getColor(Color.WHITE, Color.BLACK, point));
                    }
                }
        );
        setDragCallback(new DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                // AppBarLayout 内部本身的判断似乎有bug , 所以在这里拦截一下
                return true;
            }
        });
        return super.onLayoutChild(parent, abl, layoutDirection);
    }

    private void scale(int dy) {
        mTotalDy += -dy;
        mTotalDy = Math.min(mTotalDy, MAX_SCALE_HEIGHT);
        scale = mTotalDy / MAX_SCALE_HEIGHT;
        scale = Math.max(1f, 1f + scale);
        if (onRefreshChangeListener != null && !isRefreshing && scale > 1.1) {
            isRefreshing = true;
            onRefreshChangeListener.onRefreshShow();
        }
        if (scale > 1.5f) {
            scale = 1.5f;
        }
        if (scale == 1.0 && onRefreshChangeListener != null) {
            onRefreshChangeListener.stopRefresh();
            stopRefreshing();
        }
        ViewHelper.setScaleX(mScaleView, scale);
        ViewHelper.setScaleY(mScaleView, scale);
        mScaleView.getLayoutParams().height = (int) (scale * height);
        mScaleView.requestLayout();
    }

    private void recovery() {
        LogUtils.d("recovery::" + isRecovering);
        if (isRecovering) {
            return;
        }

        if (mTotalDy > 0) {

            doRefresh();

            isRecovering = true;
            mTotalDy = 0;
            if (isAnimate) {
                ValueAnimator anim = ValueAnimator.ofFloat(scale, 1f).setDuration(200);
                anim.addUpdateListener(
                        animation -> {
                            float value = (float) animation.getAnimatedValue();
                            ViewCompat.setScaleX(mScaleView, value);
                            ViewCompat.setScaleY(mScaleView, value);
                            mScaleView.getLayoutParams().height = (int) (value * height);
                            mScaleView.requestLayout();
                        }
                );
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isRecovering = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                anim.start();
            } else {
                ViewCompat.setScaleX(mScaleView, 1f);
                ViewCompat.setScaleY(mScaleView, 1f);
                mScaleView.getLayoutParams().height = height;
                mScaleView.requestLayout();
                isRecovering = false;
            }
        }
    }

    private void doRefresh() {
        if (onRefreshChangeListener != null && isRefreshing && !isDoRefresh) {
            isDoRefresh = true;
            onRefreshChangeListener.doRefresh();
        }
    }

    public void stopRefreshing() {
        isRefreshing = false;
        isDoRefresh = false;
    }

    public void startRefreshing() {
        isRefreshing = true;
        isDoRefresh = true;
    }

    public interface onRefreshChangeListener {
        void onRefreshShow();

        void doRefresh();

        void stopRefresh();

        void alphaChange(float point, int titleColor, int bgColor, int titleIconColor);
    }

    public void setOnRefreshChangeListener(onRefreshChangeListener onRefreshChangeListener) {
        this.onRefreshChangeListener = onRefreshChangeListener;
    }


    onRefreshChangeListener onRefreshChangeListener;
}

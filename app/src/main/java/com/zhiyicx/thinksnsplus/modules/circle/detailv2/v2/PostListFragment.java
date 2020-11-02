package com.zhiyicx.thinksnsplus.modules.circle.detailv2.v2;

import android.os.Bundle;
import android.view.View;

import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.BaseCircleDetailFragment;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailFragment;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListBaseItem;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/17:19
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PostListFragment extends BaseCircleDetailFragment {

    private OnEventListener mOnEventListener;
    private boolean mIsLoadedNetData;

    public static PostListFragment newInstance(BaseCircleRepository.CircleMinePostType circleMinePostType,
                                               Long id) {
        PostListFragment circleDetailFragment = new PostListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(CircleDetailFragment.CIRCLE_ID, id);
        bundle.putSerializable(CIRCLE_TYPE, circleMinePostType);
        circleDetailFragment.setArguments(bundle);
        return circleDetailFragment;
    }

    @Override
    protected boolean isNeedRequestNetDataWhenCacheDataNull() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mPresenter != null && !mIsLoadedNetData) {
            startRefrsh();
            mIsLoadedNetData = true;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (getUserVisibleHint() && mPresenter != null && !mIsLoadedNetData) {
            startRefrsh();
            mIsLoadedNetData = true;
        }
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<CirclePostListBean> data, boolean isLoadMore) {
        mIsLoadedNetData = true;
        super.onNetResponseSuccess(data, isLoadMore);
        if (mOnEventListener != null && !isLoadMore) {
            mOnEventListener.hideRefresh();
        }
        if (mOnEventListener != null) {
            mOnEventListener.onChildNetResponseSuccess(isLoadMore);
        }
        mRefreshlayout.setEnableRefresh(false);
    }

    @Override
    public void onResponseError(Throwable throwable, boolean isLoadMore) {
        super.onResponseError(throwable, isLoadMore);
        if (mOnEventListener != null) {
            mOnEventListener.onChildNetResponseFailed(isLoadMore);
        }
    }

    public void setOnEventListener(OnEventListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    @Override
    protected void showCommentView() {
        if (mOnEventListener != null) {
            mVShadow.setVisibility(View.GONE);
            mOnEventListener.showComment();
        }
    }

    @Override
    protected void setAdapter(MultiItemTypeAdapter adapter, CirclePostListBaseItem circlePostListBaseItem) {
        super.setAdapter(adapter, circlePostListBaseItem);
        circlePostListBaseItem.setShowPostExcelentTag(!BaseCircleRepository.CircleMinePostType.EXCELLENT.equals(mCircleMinePostType));
    }

    @Override
    protected void closeInputView() {

    }

    @Override
    public CircleInfo getCircleInfo() {
        if (mOnEventListener != null) {
            return mOnEventListener.getCurrentCircleInfo();
        }
        return super.getCircleInfo();
    }

    protected void requestNetData() {
        requestNetData(0L, false);
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    public interface OnEventListener {
        void showComment();

        void hideComment();

        void hideRefresh();

        void onChildNetResponseSuccess(boolean isLoadMore);

        void onChildNetResponseFailed(boolean isLoadMore);

        CircleInfo getCurrentCircleInfo();
    }
}

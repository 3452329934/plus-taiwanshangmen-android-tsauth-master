package com.zhiyicx.thinksnsplus.modules.search.typelist;

import android.os.Bundle;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.modules.circle.search.onlypost.SearchOnlyCirclePostFragment;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchSuceesListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/11:06
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchPostListFragment extends SearchOnlyCirclePostFragment {

    private ISearchSuceesListener mISearchSuceesListener;

    public void setISearchSuceesListener(ISearchSuceesListener isearchsuceeslistener) {
        mISearchSuceesListener = isearchsuceeslistener;
    }

    public static SearchPostListFragment newInstance() {

        Bundle args = new Bundle();

        SearchPostListFragment fragment = new SearchPostListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean showPostFrom() {
        return true;
    }

    @Override
    public BaseCircleRepository.CircleMinePostType getCircleMinePostType() {
        return BaseCircleRepository.CircleMinePostType.SEARCH;
    }

    @Override
    protected boolean searchContainerVisibility() {
        return false;
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<CirclePostListBean> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        if (mISearchSuceesListener != null) {
            mISearchSuceesListener.onSearchSucees(getSearchInput());
        }
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }
}

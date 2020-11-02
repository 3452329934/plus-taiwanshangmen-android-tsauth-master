package com.zhiyicx.thinksnsplus.modules.search.typelist;

import android.os.Bundle;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.modules.circle.search.SearchCircleFragment;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchSuceesListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/11:05
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchCircleListFragment extends SearchCircleFragment {

    private ISearchSuceesListener mISearchSuceesListener;

    public void setISearchSuceesListener(ISearchSuceesListener isearchsuceeslistener) {
        mISearchSuceesListener = isearchsuceeslistener;
    }

    public static SearchCircleListFragment newInstance() {

        Bundle args = new Bundle();

        SearchCircleListFragment fragment = new SearchCircleListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean useHistory() {
        return false;
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<CircleInfo> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        if (mISearchSuceesListener != null) {
            mISearchSuceesListener.onSearchSucees(getSearchInput());
        }
    }
}

package com.zhiyicx.thinksnsplus.modules.search.typelist;

import android.os.Bundle;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchSuceesListener;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.qa.QASearchListFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/11:07
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchQAListFragment extends QASearchListFragment {

    private ISearchSuceesListener mISearchSuceesListener;

    public void setISearchSuceesListener(ISearchSuceesListener isearchsuceeslistener) {
        mISearchSuceesListener = isearchsuceeslistener;
    }

    public static SearchQAListFragment newInstance() {

        Bundle args = new Bundle();

        SearchQAListFragment fragment = new SearchQAListFragment();
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
    public void onNetResponseSuccess(@NotNull List<QAListInfoBean> data, boolean isLoadMore) {
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

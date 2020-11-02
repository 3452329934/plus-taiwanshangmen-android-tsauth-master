package com.zhiyicx.thinksnsplus.modules.search.typelist;

import android.os.Bundle;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.qa.QATopicBean;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchSuceesListener;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.topic.QATopicSearchListFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/11:09
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchQATopicListFragment extends QATopicSearchListFragment {

    private ISearchSuceesListener mISearchSuceesListener;

    public void setISearchSuceesListener(ISearchSuceesListener isearchsuceeslistener) {
        mISearchSuceesListener = isearchsuceeslistener;
    }

    public static SearchQATopicListFragment newInstance() {

        Bundle args = new Bundle();

        SearchQATopicListFragment fragment = new SearchQATopicListFragment();
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
    public void onNetResponseSuccess(@NotNull List<QATopicBean> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        if (mISearchSuceesListener != null) {
            mISearchSuceesListener.onSearchSucees(getName());
        }
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }
}

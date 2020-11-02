package com.zhiyicx.thinksnsplus.modules.search.typelist;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.zhiyicx.common.utils.AndroidBug5497Workaround;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.DynamicFragment;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchListener;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchSuceesListener;
import com.zhiyicx.thinksnsplus.modules.search.container.SearchHistoryViewPagerContainerFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.zhiyicx.baseproject.config.ApiConfig.DYNAMIC_TYPE_NEW;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/11:03
 * @Email Jliuer@aliyun.com
 * @Description 动态
 */
public class SearchDynamicListFragment extends DynamicFragment implements ISearchListener {

    /**
     * 搜索关键字
     */
    private String keyWord;

    private ISearchSuceesListener mISearchSuceesListener;

    public void setISearchSuceesListener(ISearchSuceesListener isearchsuceeslistener) {
        mISearchSuceesListener = isearchsuceeslistener;
    }

    public static SearchDynamicListFragment newInstance(String keyword) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_DYNAMIC_TYPE, DYNAMIC_TYPE_NEW);
        args.putString(SearchHistoryViewPagerContainerFragment.KEY_WORD, keyword);
        SearchDynamicListFragment fragment = new SearchDynamicListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void deal5497Bug() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            AndroidBug5497Workaround.assistActivity(mActivity);
        }
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean setUseCenterLoading() {
        return false;
    }

    @Override
    protected void requestCacheData(Long maxId, boolean isLoadMore) {
        onCacheResponseSuccess(null, isLoadMore);
    }

    @Override
    public void onEditChanged(String str) {
        if (str.equals(keyWord)) {
            return;
        }
        keyWord = str;
        if (TextUtils.isEmpty(str)) {
            getArguments().putString(SearchHistoryViewPagerContainerFragment.KEY_WORD, str);
        }
        if (mRefreshlayout.getState().isOpening) {
            onRefresh(mRefreshlayout);
        } else {
            mRefreshlayout.autoRefresh();
        }
    }

    @Override
    public String getKeyWord() {
        if (TextUtils.isEmpty(keyWord)) {
            keyWord = getArguments().getString(SearchHistoryViewPagerContainerFragment.KEY_WORD);
        }
        return keyWord;
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<DynamicDetailBeanV2> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        if (mISearchSuceesListener != null) {
            mISearchSuceesListener.onSearchSucees(getKeyWord());
        }
    }
}

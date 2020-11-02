package com.zhiyicx.thinksnsplus.modules.search.typelist;

import android.os.Bundle;
import android.view.View;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchListener;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchSuceesListener;
import com.zhiyicx.thinksnsplus.modules.topic.search.DaggerSearchTopicComponent;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicPresenter;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicPresenterModule;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/11:14
 * @Email Jliuer@aliyun.com
 * @Description 话题
 */
public class SearchFeedTopicListFragment extends SearchTopicFragment implements ISearchListener {

    private ISearchSuceesListener mISearchSuceesListener;

    public void setISearchSuceesListener(ISearchSuceesListener isearchsuceeslistener) {
        mISearchSuceesListener = isearchsuceeslistener;
    }

    @Inject
    SearchTopicPresenter mSearchTopicPresenter;

    private String keyWord;

    public static SearchFeedTopicListFragment newInstance() {

        Bundle args = new Bundle();

        SearchFeedTopicListFragment fragment = new SearchFeedTopicListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        Observable.create(subscriber -> {
            DaggerSearchTopicComponent.builder()
                    .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .searchTopicPresenterModule(new SearchTopicPresenterModule(this))
                    .build().inject(this);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        initData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

    @Override
    public void onEditChanged(String str) {
        if (str.equals(keyWord)) {
            return;
        }
        keyWord = str;
        if (mRefreshlayout.getState().isOpening) {
            onRefresh(mRefreshlayout);
        } else {
            mRefreshlayout.autoRefresh();
        }
    }

    @Override
    public String getSearchKeyWords() {
        return keyWord;
    }

    @Override
    protected boolean searchContainerVisibility() {
        return false;
    }

    @Override
    protected boolean isRefreshEnable() {
        return true;
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<TopicListBean> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        if (mISearchSuceesListener != null) {
            mISearchSuceesListener.onSearchSucees(getSearchKeyWords());
        }
    }

    @Override
    protected void requestCacheData(Long maxId, boolean isLoadMore) {
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }
}

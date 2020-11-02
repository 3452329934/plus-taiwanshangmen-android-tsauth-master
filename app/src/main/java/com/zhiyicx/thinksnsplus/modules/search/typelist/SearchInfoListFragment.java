package com.zhiyicx.thinksnsplus.modules.search.typelist;

import android.os.Bundle;
import android.view.View;

import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.InfoListDataBean;
import com.zhiyicx.thinksnsplus.modules.information.infosearch.DaggerInfoSearchComponent;
import com.zhiyicx.thinksnsplus.modules.information.infosearch.InfoSearchPresenter;
import com.zhiyicx.thinksnsplus.modules.information.infosearch.InfoSearchPresenterMudule;
import com.zhiyicx.thinksnsplus.modules.information.infosearch.SearchFragment;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchListener;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchSuceesListener;

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
 * @Date 2018/08/06/11:03
 * @Email Jliuer@aliyun.com
 * @Description 资讯(文章)
 */
public class SearchInfoListFragment extends SearchFragment implements ISearchListener {

    @Inject
    InfoSearchPresenter mInfoSearchPresenter;

    private String keyWord;

    private ISearchSuceesListener mISearchSuceesListener;

    public void setISearchSuceesListener(ISearchSuceesListener isearchsuceeslistener) {
        mISearchSuceesListener = isearchsuceeslistener;
    }

    public static SearchInfoListFragment newInstance() {
        Bundle args = new Bundle();
        SearchInfoListFragment fragment = new SearchInfoListFragment();
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
            DaggerInfoSearchComponent.builder()
                    .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .infoSearchPresenterMudule(new InfoSearchPresenterMudule(this))
                    .build()
                    .inject(this);
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
    public String getKeyWords() {
        return keyWord;
    }

    @Override
    protected boolean searchContainerVisibility() {
        return false;
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<InfoListDataBean> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        if (mISearchSuceesListener != null) {
            mISearchSuceesListener.onSearchSucees(getKeyWords());
        }
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }
}

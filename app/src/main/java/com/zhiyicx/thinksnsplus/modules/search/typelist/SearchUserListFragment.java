package com.zhiyicx.thinksnsplus.modules.search.typelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.findsomeone.search.name.DaggerSearchSomeOneComponent;
import com.zhiyicx.thinksnsplus.modules.findsomeone.search.name.SearchSomeOneFragment;
import com.zhiyicx.thinksnsplus.modules.findsomeone.search.name.SearchSomeOnePresenter;
import com.zhiyicx.thinksnsplus.modules.findsomeone.search.name.SearchSomeOnePresenterModule;
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
 * @Date 2018/08/06/11:04
 * @Email Jliuer@aliyun.com
 * @Description 用户
 */
public class SearchUserListFragment extends SearchSomeOneFragment implements ISearchListener {

    @Inject
    SearchSomeOnePresenter mSearchSomeOnePresenter;

    private String keyWord;

    private ISearchSuceesListener mISearchSuceesListener;

    public void setISearchSuceesListener(ISearchSuceesListener ISearchSuceesListener) {
        mISearchSuceesListener = ISearchSuceesListener;
    }

    public static SearchUserListFragment newInstance() {

        Bundle args = new Bundle();

        SearchUserListFragment fragment = new SearchUserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean isRefreshEnable() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerSearchSomeOneComponent
                .builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .searchSomeOnePresenterModule(new SearchSomeOnePresenterModule(this))
                .build().inject(this);
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
    }

    @Override
    public void onEditChanged(String str) {
        if (str.equals(keyWord)) {
            return;
        }
        keyWord = str;
        searchUser();
    }

    @Override
    protected String getKeyword() {
        return keyWord;
    }

    @Override
    protected boolean searchContainerVisibility() {
        return false;
    }

    @Override
    protected boolean recommentVisibility() {
        return false;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onNetResponseSuccess(@NotNull List<UserInfoBean> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        if (mISearchSuceesListener != null) {
            mISearchSuceesListener.onSearchSucees(getKeyword());
        }
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }
}

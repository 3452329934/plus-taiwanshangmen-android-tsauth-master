package com.zhiyicx.thinksnsplus.modules.search.history;

import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.data.beans.SearchHistoryBean;
import com.zhiyicx.thinksnsplus.data.source.local.SearchHistoryBeanGreenDaoImpl;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/10:44
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchHistoryPresenter extends AppBasePresenter<SearchHistoryContract.View>
        implements SearchHistoryContract.Presenter {

    SearchHistoryBeanGreenDaoImpl mSearchHistoryBeanGreenDao;

    @Inject
    public SearchHistoryPresenter(SearchHistoryContract.View rootView,
                                  SearchHistoryBeanGreenDaoImpl searchHistoryBeanGreenDao) {
        super(rootView);
        mSearchHistoryBeanGreenDao = searchHistoryBeanGreenDao;
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {

    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(mSearchHistoryBeanGreenDao.getMultiDataFromCache(), isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<SearchHistoryBean> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public void deleteHistory() {
        mSearchHistoryBeanGreenDao.clearTable();
        mRootView.getListDatas().clear();
        mRootView.refreshData();
    }

    @Override
    public void deleteSearchHistory(String content) {
        mSearchHistoryBeanGreenDao.deleteSearchHistory(content);
    }
}

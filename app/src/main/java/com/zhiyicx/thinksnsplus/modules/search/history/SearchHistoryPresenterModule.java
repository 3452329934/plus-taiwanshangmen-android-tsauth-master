package com.zhiyicx.thinksnsplus.modules.search.history;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/10:44
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class SearchHistoryPresenterModule {
    SearchHistoryContract.View mView;

    public SearchHistoryPresenterModule(SearchHistoryContract.View view) {
        mView = view;
    }

    @Provides
    SearchHistoryContract.View provideSearchHistoryContractView(){
        return mView;
    }
}

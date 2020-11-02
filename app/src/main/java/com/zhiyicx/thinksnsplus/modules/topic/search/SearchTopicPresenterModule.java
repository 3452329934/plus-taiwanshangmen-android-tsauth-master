package com.zhiyicx.thinksnsplus.modules.topic.search;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/14:27
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class SearchTopicPresenterModule {
    SearchTopicContract.View mView;

    public SearchTopicPresenterModule(SearchTopicContract.View view) {
        mView = view;
    }

    @Provides
    SearchTopicContract.View provideSearchTopicContractView(){
        return mView;
    }
}

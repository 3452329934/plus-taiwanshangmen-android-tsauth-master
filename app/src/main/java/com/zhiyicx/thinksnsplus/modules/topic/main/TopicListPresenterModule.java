package com.zhiyicx.thinksnsplus.modules.topic.main;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/17:21
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class TopicListPresenterModule {
    TopicListContract.View mView;

    public TopicListPresenterModule(TopicListContract.View view) {
        mView = view;
    }

    @Provides
    TopicListContract.View provideTopicListContractView(){
        return mView;
    }
}

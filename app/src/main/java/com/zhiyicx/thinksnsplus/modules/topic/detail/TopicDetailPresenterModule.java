package com.zhiyicx.thinksnsplus.modules.topic.detail;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/9:46
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class TopicDetailPresenterModule {
    private TopicDetailContract.View mView;

    public TopicDetailPresenterModule(TopicDetailContract.View view) {
        mView = view;
    }

    @Provides
    TopicDetailContract.View provideTopicDetailContractView(){
        return mView;
    }
}

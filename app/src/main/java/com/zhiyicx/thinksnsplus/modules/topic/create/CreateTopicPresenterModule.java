package com.zhiyicx.thinksnsplus.modules.topic.create;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/10:34
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class CreateTopicPresenterModule {
    CreateTopicContract.View mView;

    CreateTopicPresenterModule(CreateTopicContract.View view) {
        mView = view;
    }

    @Provides
    CreateTopicContract.View provideCreateTopicCtractView(){
        return mView;
    }
}

package com.zhiyicx.thinksnsplus.modules.topic.detail.joined;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/31/9:10
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class JoinedUserPresenterModule {
    JoinedUserContract.View mView;

    public JoinedUserPresenterModule(JoinedUserContract.View view) {
        mView = view;
    }

    @Provides
    JoinedUserContract.View provideJoinedUserContractView() {
        return mView;
    }
}

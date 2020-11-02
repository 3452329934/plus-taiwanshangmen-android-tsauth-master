package com.zhiyicx.thinksnsplus.modules.circle.pre;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/9:44
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class PreCirclePresenterModule {

    PreCircleContract.View mView;

    public PreCirclePresenterModule(PreCircleContract.View view) {
        mView = view;
    }

    @Provides
    PreCircleContract.View provideView() {
        return mView;
    }
}

package com.zhiyicx.thinksnsplus.modules.aaaat;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/13/13:33
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class AtUserListPresenterModule {
    AtUserContract.View mView;

    public AtUserListPresenterModule(AtUserContract.View view) {
        mView = view;
    }

    @Provides
    AtUserContract.View provideView(){
        return mView;
    }
}

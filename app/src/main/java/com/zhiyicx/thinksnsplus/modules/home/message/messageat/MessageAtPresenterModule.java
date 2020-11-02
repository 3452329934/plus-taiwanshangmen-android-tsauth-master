package com.zhiyicx.thinksnsplus.modules.home.message.messageat;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/16/11:50
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class MessageAtPresenterModule {
    MessageAtContract.View mView;

    public MessageAtPresenterModule(MessageAtContract.View view) {
        mView = view;
    }

    @Provides
    MessageAtContract.View provideView() {
        return mView;
    }
}

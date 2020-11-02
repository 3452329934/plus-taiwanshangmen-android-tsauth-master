package com.zhiyicx.thinksnsplus.modules.chat.location.send;

import dagger.Module;
import dagger.Provides;

/**
 * @Author Jliuer
 * @Date 2018/06/12/15:33
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class SendLocationPresenterModule {
    private SendLocationContract.View mView;

    public SendLocationPresenterModule(SendLocationContract.View mView) {
        this.mView = mView;
    }

    @Provides
    public SendLocationContract.View provideSendLocationContractView() {
        return mView;
    }
}

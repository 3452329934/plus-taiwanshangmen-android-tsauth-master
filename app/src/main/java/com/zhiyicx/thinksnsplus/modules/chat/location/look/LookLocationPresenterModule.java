package com.zhiyicx.thinksnsplus.modules.chat.location.look;

import dagger.Module;
import dagger.Provides;

/**
 * @author Catherine
 * @describe
 * @date 2018/1/10
 * @contact email:648129313@qq.com
 */
@Module
public class LookLocationPresenterModule {

    private LookLocationContract.View mView;

    public LookLocationPresenterModule(LookLocationContract.View mView) {
        this.mView = mView;
    }

    @Provides
    public LookLocationContract.View provideSendLocationContractView() {
        return mView;
    }
}

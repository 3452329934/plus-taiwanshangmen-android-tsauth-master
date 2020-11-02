package com.zhiyicx.thinksnsplus.modules.chat.location.look;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * @author Catherine
 * @describe 发送定位和查看定位详情的页面
 * @date 2018/1/10
 * @contact email:648129313@qq.com
 */

public class LookLocationActivity extends TSActivity<LookLocationPresenter, LookLocationFragment>{

    @Override
    protected LookLocationFragment getFragment() {
        return new LookLocationFragment().instance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerLookLocationComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .lookLocationPresenterModule(new LookLocationPresenterModule(mContanierFragment))
                .build()
                .inject(this);
    }
}

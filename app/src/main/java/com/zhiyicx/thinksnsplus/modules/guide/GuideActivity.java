package com.zhiyicx.thinksnsplus.modules.guide;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.huawei.android.hms.agent.HMSAgent;
import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/1/13
 * @Contact master.jungle68@gmail.com
 */
public class GuideActivity extends TSActivity<GuidePresenter, GuideFragment> {

    @Override
    protected GuideFragment getFragment() {
        return new GuideFragment();
    }

    @Override
    protected void componentInject() {
        DaggerGuideComponent
                .builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .guidePresenterModule(new GuidePresenterModule(mContanierFragment))
                .build()
                .inject(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mContanierFragment.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DeviceUtils.openFullScreenModel(this);
        super.onCreate(savedInstanceState);
        String manufacturer = Build.MANUFACTURER;
        if ((!TextUtils.isEmpty(manufacturer) && "HUAWEI".equals(manufacturer.toUpperCase()))
                || !TextUtils.isEmpty(DeviceUtils.getEMUI())) {
            // 华为接口连接
            HMSAgent.connect(this, rst -> LogUtils.d("HMS connect end:" + rst));
        }

    }
}

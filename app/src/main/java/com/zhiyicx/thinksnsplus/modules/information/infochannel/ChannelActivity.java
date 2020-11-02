package com.zhiyicx.thinksnsplus.modules.information.infochannel;


import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;

import static com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsFragment.BUNDLE_INFO_TYPE;

/**
 * @Author Jliuer
 * @Date 2017/03/06
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChannelActivity extends TSActivity<InfoChannelPresenter, InfoChannelFragment> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected InfoChannelFragment getFragment() {
        return InfoChannelFragment.newInstance(getIntent().getBundleExtra(BUNDLE_INFO_TYPE));
    }

    @Override
    protected void componentInject() {
        DaggerInfoChannelComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .infoChannelPresenterModule(new InfoChannelPresenterModule(mContanierFragment))
                .build()
                .inject(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_top_enter, R.anim.slide_from_top_quit);
    }
}

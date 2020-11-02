package com.zhiyicx.thinksnsplus.modules.personal_center;

import android.content.Intent;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.common.utils.ActivityUtils;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.modules.home.HomeActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.ZhiyiVideoView;

import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;

public class PersonalCenterActivity extends TSActivity<PersonalCenterPresenter, PersonalCenterFragment> {

    @Override
    protected void componentInject() {
        DaggerPersonalCenterPresenterComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .shareModule(new ShareModule(this))
                .personalCenterPresenterModule(new PersonalCenterPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengSharePolicyImpl.onActivityResult(requestCode, resultCode, data, this);
        mContanierFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected PersonalCenterFragment getFragment() {
        return PersonalCenterFragment.initFragment(getIntent().getExtras());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmengSharePolicyImpl.onDestroy(this);
    }

    @Override
    public void onBackPressed() {
        if (mContanierFragment != null && mContanierFragment.backPressed()) {
            return;
        }
        if (JZVideoPlayer.backPress()) {
            return;
        }
        mContanierFragment.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (JZVideoPlayerManager.getCurrentJzvd() != null) {
            if (JZVideoPlayerManager.getCurrentJzvd().currentState == ZhiyiVideoView.CURRENT_STATE_PREPARING
                    || JZVideoPlayerManager.getCurrentJzvd().currentState == ZhiyiVideoView.CURRENT_STATE_PREPARING_CHANGING_URL) {
                ZhiyiVideoView.releaseAllVideos();
            }
        }
        ZhiyiVideoView.goOnPlayOnPause();
    }
}

package com.zhiyicx.thinksnsplus.modules.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.common.utils.ActivityUtils;
import com.zhiyicx.thinksnsplus.config.JpushMessageTypeConfig;
import com.zhiyicx.thinksnsplus.data.beans.JpushMessageBean;
import com.zhiyicx.thinksnsplus.modules.home.message.messageat.MessageAtActivity;
import com.zhiyicx.thinksnsplus.modules.home.message.messagecomment.MessageCommentActivity;
import com.zhiyicx.thinksnsplus.modules.home.message.messagelike.MessageLikeActivity;
import com.zhiyicx.thinksnsplus.modules.home.message.notifacationlist.NotificationActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.NetChangeReceiver;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.ZhiyiVideoView;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;


/**
 * @Describe
 * @Author Jungle68
 * @Date 2016/12/22
 * @Contact master.jungle68@gmail.com
 */

public class HomeActivity extends TSActivity {
    public static final String BUNDLE_JPUSH_MESSAGE = "jpush_message";
    private NetChangeReceiver mNetChangeReceiver;

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            JpushMessageBean jpushMessageBean = bundle.getParcelable(BUNDLE_JPUSH_MESSAGE);
            if (jpushMessageBean != null && jpushMessageBean.getType() != null) {
                switch (jpushMessageBean.getType()) {
                    case JpushMessageTypeConfig.JPUSH_MESSAGE_TYPE_SYSTEM:
                        startActivity(new Intent(this, NotificationActivity.class));
                        break;
                    case JpushMessageTypeConfig.JPUSH_MESSAGE_TYPE_LIKE:
                        startActivity(new Intent(this, MessageLikeActivity.class));
                        break;
                    case JpushMessageTypeConfig.JPUSH_MESSAGE_TYPE_COMMENT:
                        startActivity(new Intent(this, MessageCommentActivity.class));
                        break;
                    case JpushMessageTypeConfig.JPUSH_MESSAGE_TYPE_AT:
                        startActivity(new Intent(this, MessageAtActivity.class));
                        break;
                    default:
                        ((HomeContract.View) mContanierFragment).checkBottomItem(HomeFragment.PAGE_MESSAGE);
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNetChangeReceiver == null) {
            mNetChangeReceiver = new NetChangeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetChangeReceiver, filter);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmengSharePolicyImpl.onDestroy(this);
        if (mNetChangeReceiver != null) {
            unregisterReceiver(mNetChangeReceiver);
        }
    }

    @Override
    protected void componentInject() {
    }

    @Override
    protected Fragment getFragment() {
        return HomeFragment.newInstance(getIntent().getExtras());
    }

    @Override
    public void onBackPressed() {
        if (mContanierFragment instanceof HomeFragment) {
            if (((HomeFragment) (mContanierFragment)).getMainFragment().backPressed()) {
                return;
            }
        }
        if (JZVideoPlayer.backPress()) {
            return;
        }
        ActivityUtils.goHome(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengSharePolicyImpl.onActivityResult(requestCode, resultCode, data, this);
        mContanierFragment.onActivityResult(requestCode, resultCode, data);
    }

}

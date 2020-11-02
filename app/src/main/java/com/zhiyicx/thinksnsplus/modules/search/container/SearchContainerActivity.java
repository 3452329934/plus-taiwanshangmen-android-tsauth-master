package com.zhiyicx.thinksnsplus.modules.search.container;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterActivity;

import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/10:36
 * @Email Jliuer@aliyun.com
 * @Description 聚合搜索入口
 */
public class SearchContainerActivity extends TSActivity {

    @Override
    protected SearchContainerFragment getFragment() {
        return new SearchContainerFragment();
    }

    @Override
    protected void componentInject() {

    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        ((SearchContainerFragment) (mContanierFragment)).onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
        if (jzVideoPlayer != null) {
            if (JZUtils.scanForActivity(jzVideoPlayer.getContext()) instanceof PersonalCenterActivity) {
                jzVideoPlayer.onStateNormal();
            }
        }
    }
}

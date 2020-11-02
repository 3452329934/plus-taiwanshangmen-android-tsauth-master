package com.zhiyicx.thinksnsplus.modules.topic.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.thinksnsplus.base.AppApplication;

import cn.jzvd.JZVideoPlayer;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/9:48
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopicDetailActivity extends TSActivity<TopicDetailPresenter, TopicDetailFragment> {

    @Override
    protected TopicDetailFragment getFragment() {
        return TopicDetailFragment.newInstance(getIntent().getExtras());
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
    protected void componentInject() {
        DaggerTopicDetailComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .shareModule(new ShareModule(this))
                .topicDetailPresenterModule(new TopicDetailPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    public static void startTopicDetailActivity(Context context, Long to) {
        Intent intent = new Intent(context, TopicDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(TopicDetailFragment.ID, to);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startTopicDetailActivity(Context context, Long from, Long to) {
        Intent intent = new Intent(context, TopicDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(TopicDetailFragment.FROM, from);
        bundle.putLong(TopicDetailFragment.ID, to);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengSharePolicyImpl.onActivityResult(requestCode, resultCode, data, this);
        mContanierFragment.onActivityResult(requestCode, resultCode, data);
    }
}

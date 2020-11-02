package com.zhiyicx.thinksnsplus.modules.topic.detail.joined;

import android.content.Context;
import android.content.Intent;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/31/9:12
 * @Email Jliuer@aliyun.com
 * @Description 话题参与者列表
 */
public class JoinedUserActivity extends TSActivity<JoinedUserPresenter, JoinedUserListFragment> {

    @Override
    protected JoinedUserListFragment getFragment() {
        return JoinedUserListFragment.newInstance(getIntent().getLongExtra(JoinedUserListFragment.TOPICID, -1L));
    }

    @Override
    protected void componentInject() {
        DaggerJoinedUserComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .joinedUserPresenterModule(new JoinedUserPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    public static void startJoinedUserActivity(Context context, Long topicId) {
        Intent intent = new Intent(context, JoinedUserActivity.class);
        intent.putExtra(JoinedUserListFragment.TOPICID, topicId);
        context.startActivity(intent);
    }
}

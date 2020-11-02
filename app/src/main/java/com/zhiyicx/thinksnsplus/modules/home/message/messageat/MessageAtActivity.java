package com.zhiyicx.thinksnsplus.modules.home.message.messageat;

import android.content.Intent;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/16/11:51
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageAtActivity extends TSActivity<MessageAtPresenter, MessageAtFragment> {

    @Override
    protected MessageAtFragment getFragment() {
        return MessageAtFragment.newInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mContanierFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void componentInject() {
        DaggerMessageAtComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .messageAtPresenterModule(new MessageAtPresenterModule(mContanierFragment))
                .build().inject(this);
    }
}

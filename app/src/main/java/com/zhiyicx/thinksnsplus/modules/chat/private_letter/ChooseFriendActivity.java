package com.zhiyicx.thinksnsplus.modules.chat.private_letter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.modules.home.message.messagelist.DaggerMessageConversationComponent;
import com.zhiyicx.thinksnsplus.modules.home.message.messagelist.MessageConversationPresenterModule;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/07/14:46
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChooseFriendActivity extends TSActivity<ChooseFriendPresenter, ChooseFriendFragment> {

    @Override
    protected ChooseFriendFragment getFragment() {
        return ChooseFriendFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerMessageConversationComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .messageConversationPresenterModule(new MessageConversationPresenterModule(mContanierFragment))
                .build()
                .inject(this);
    }

    public static void startChooseFriendActivity(Context context, Letter letter) {
        Intent intent = new Intent(context, ChooseFriendActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ChooseFriendFragment.LETTER, letter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

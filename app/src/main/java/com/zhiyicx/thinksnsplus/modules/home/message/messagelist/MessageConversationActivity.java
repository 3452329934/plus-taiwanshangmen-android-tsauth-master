package com.zhiyicx.thinksnsplus.modules.home.message.messagelist;

import android.content.Intent;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.modules.home.message.MessageFragment;
import com.zhiyicx.thinksnsplus.modules.home.message.MessagePresenter;
import com.zhiyicx.thinksnsplus.modules.home.message.messageat.MessageAtFragment;
import com.zhiyicx.thinksnsplus.modules.home.message.messageat.MessageAtPresenter;
import com.zhiyicx.thinksnsplus.modules.home.message.messageat.MessageAtPresenterModule;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/16/11:51
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageConversationActivity extends TSActivity<MessageConversationPresenter, MessageConversationFragment> {

    @Override
    protected MessageConversationFragment getFragment() {
        String title = "聊天";
        return MessageConversationFragment.newInstance(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mContanierFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void componentInject() {

    }
}

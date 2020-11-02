package com.zhiyicx.thinksnsplus.modules.chat.private_letter;


import com.zhiyicx.baseproject.em.manager.eventbus.TSEMMultipleMessagesEvent;
import com.zhiyicx.baseproject.em.manager.eventbus.TSEMRefreshEvent;
import com.zhiyicx.thinksnsplus.modules.home.message.messagelist.MessageConversationContract;
import com.zhiyicx.thinksnsplus.modules.home.message.messagelist.MessageConversationPresenter;

import javax.inject.Inject;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/07/14:52
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChooseFriendPresenter extends MessageConversationPresenter {

    @Inject
    public ChooseFriendPresenter(MessageConversationContract.View rootView) {
        super(rootView);
    }

    @Override
    public void refreshConversationReadMessage() {
    }

    @Override
    public void onTSEMRefreshEventEventBus(TSEMRefreshEvent event) {
    }

    @Override
    public void onMessageReceived(TSEMMultipleMessagesEvent messagesEvent) {
    }

    @Override
    public void searchList(String key) {
        super.searchList(key);
    }
}

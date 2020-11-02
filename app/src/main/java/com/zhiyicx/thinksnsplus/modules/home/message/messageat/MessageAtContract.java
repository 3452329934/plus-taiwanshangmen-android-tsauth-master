package com.zhiyicx.thinksnsplus.modules.home.message.messageat;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.notify.AtMeaasgeBean;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/16/11:45
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface MessageAtContract {
    interface View extends ITSListView<AtMeaasgeBean,Presenter>{}
    interface Presenter extends ITSListPresenter<AtMeaasgeBean>{
        void sendComment(int currentPostion, long replyUserId, String text);
    }
}

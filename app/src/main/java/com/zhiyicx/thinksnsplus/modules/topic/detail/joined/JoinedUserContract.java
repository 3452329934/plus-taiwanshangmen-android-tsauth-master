package com.zhiyicx.thinksnsplus.modules.topic.detail.joined;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

import java.util.List;

import rx.Observable;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/31/9:06
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface JoinedUserContract {
    interface View extends ITSListView<UserInfoBean, Presenter> {
        Long getTopicId();

    }

    interface Presenter extends ITSListPresenter<UserInfoBean> {
        void getParticipants(Long topicId, Integer offset,boolean isLoadMore);
        void handleUserFollowState(int index,UserInfoBean userInfoBean);
    }

}

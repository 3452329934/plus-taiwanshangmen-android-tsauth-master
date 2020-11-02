package com.zhiyicx.thinksnsplus.modules.topic.main;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/17:20
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface TopicListContract {
    interface View extends ITSListView<TopicListBean, Presenter> {
        String getTopicListType();
    }

    interface Presenter extends ITSListPresenter<TopicListBean> {
        void handleTopicFollowState(TopicListBean topicDetailBean);
    }
}

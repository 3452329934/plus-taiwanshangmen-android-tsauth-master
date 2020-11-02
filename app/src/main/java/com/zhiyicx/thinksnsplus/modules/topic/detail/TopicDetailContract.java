package com.zhiyicx.thinksnsplus.modules.topic.detail;

import android.graphics.Bitmap;

import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.DynamicContract;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/9:38
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface TopicDetailContract {
    interface View extends DynamicContract.View<Presenter> {
        TopicDetailBean getCurrentTopic();
        Long getTopicId();
        void updateCurrentTopic(TopicDetailBean topic);
        void updateCount(int count,int followersCount);
        void topicHasBeDeleted();
    }

    interface Presenter extends DynamicContract.Presenter {
        void shareTopic(TopicDetailBean topic, Bitmap bitmap);
        void handleTopicFollowState(Long topicId, boolean isFollowed);
    }
}

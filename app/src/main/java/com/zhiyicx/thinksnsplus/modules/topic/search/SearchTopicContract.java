package com.zhiyicx.thinksnsplus.modules.topic.search;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/14:25
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface SearchTopicContract {
    interface View extends ITSListView<TopicListBean,Presenter>{
        String getSearchKeyWords();
        void setHotTopicList(List<TopicListBean> hotTopics);
    }
    interface Presenter extends ITSListPresenter<TopicListBean>{}
}

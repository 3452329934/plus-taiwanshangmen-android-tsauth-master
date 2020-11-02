package com.zhiyicx.thinksnsplus.modules.topic.create;

import com.zhiyicx.baseproject.base.IBaseTouristPresenter;
import com.zhiyicx.common.mvp.i.IBaseView;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/10:32
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface CreateTopicContract {
    interface View extends IBaseView<Presenter>{
        TopicDetailBean getModifyTopic();
        void setTopicId(Long id);
    }
    interface Presenter extends IBaseTouristPresenter{
        void createOrModifyTopic(String name, String desc, String image);
    }
}

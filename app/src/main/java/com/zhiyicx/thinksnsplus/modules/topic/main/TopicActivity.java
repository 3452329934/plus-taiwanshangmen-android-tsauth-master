package com.zhiyicx.thinksnsplus.modules.topic.main;

import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/17:10
 * @Email Jliuer@aliyun.com
 * @Description 话题入口
 */
public class TopicActivity extends TSActivity {

    @Override
    protected Fragment getFragment() {
        return new TopicListContainerFragment();
    }

    @Override
    protected void componentInject() {

    }
}

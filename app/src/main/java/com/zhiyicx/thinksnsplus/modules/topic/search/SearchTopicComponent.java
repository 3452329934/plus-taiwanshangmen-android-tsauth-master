package com.zhiyicx.thinksnsplus.modules.topic.search;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.base.AppComponent;
import com.zhiyicx.thinksnsplus.base.InjectComponent;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchFeedTopicListFragment;

import dagger.Component;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/14:30
 * @Email Jliuer@aliyun.com
 * @Description
 */
@FragmentScoped
@Component(dependencies = AppComponent.class, modules = {SearchTopicPresenterModule.class})
public interface SearchTopicComponent extends InjectComponent<SearchTopicActivity> {
    void inject(SearchFeedTopicListFragment searchFeedTopicListFragment);
}

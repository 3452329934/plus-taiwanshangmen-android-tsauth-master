package com.zhiyicx.thinksnsplus.modules.topic.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/14:29
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchTopicActivity extends TSActivity<SearchTopicPresenter, SearchTopicFragment> {

    @Override
    protected SearchTopicFragment getFragment() {
        return SearchTopicFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerSearchTopicComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .searchTopicPresenterModule(new SearchTopicPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    public static void startSearchTopicActivity(Activity context, boolean fromPublish) {
        Intent intent = new Intent(context, SearchTopicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(SearchTopicFragment.FROM, fromPublish);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, SearchTopicFragment.CHOOSE_TOPIC);
    }

    public static void startSearchTopicActivity(Context context) {
        Intent intent = new Intent(context, SearchTopicActivity.class);
        context.startActivity(intent);
    }
}

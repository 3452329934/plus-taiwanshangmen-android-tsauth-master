package com.zhiyicx.thinksnsplus.modules.topic.create;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/10:35
 * @Email Jliuer@aliyun.com
 * @Description create-feed-topic
 */
public class CreateTopicActivity extends TSActivity<CreateTopicPresenter, CreateTopicFragment> {
    @Override
    protected CreateTopicFragment getFragment() {
        return CreateTopicFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerCreateTopicComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .createTopicPresenterModule(new CreateTopicPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mContanierFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        mContanierFragment.onBackPressed();
    }

    public static void startCreateTopicActivity(Context context, TopicDetailBean topic) {
        Intent intent = new Intent(context, CreateTopicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SearchTopicFragment.TOPIC, topic);
        intent.putExtras(bundle);
        if (context instanceof Activity && topic != null) {
            ((Activity) context).startActivityForResult(intent, CreateTopicFragment.CHANGETOPIC);
        } else {
            context.startActivity(intent);
        }
    }
}

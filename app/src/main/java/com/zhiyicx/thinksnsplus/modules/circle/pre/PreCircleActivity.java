package com.zhiyicx.thinksnsplus.modules.circle.pre;

import android.content.Context;
import android.content.Intent;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailFragment;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/9:45
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PreCircleActivity extends TSActivity<PreCirclePresenter, PreCircleFragment> {

    @Override
    protected PreCircleFragment getFragment() {
        return PreCircleFragment.newInstance(getIntent().getLongExtra(CircleDetailFragment.CIRCLE_ID, 1));
    }

    @Override
    protected void componentInject() {
        DaggerPreCircleComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .preCirclePresenterModule(new PreCirclePresenterModule(mContanierFragment))
                .build().inject(this);
    }

    public static void startPreCircleActivity(Context context, Long circleId) {
        Intent intent = new Intent(context, PreCircleActivity.class);
        intent.putExtra(CircleDetailFragment.CIRCLE_ID, circleId);
        context.startActivity(intent);
    }
}

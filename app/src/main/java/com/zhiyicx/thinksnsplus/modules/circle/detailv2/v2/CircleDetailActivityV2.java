package com.zhiyicx.thinksnsplus.modules.circle.detailv2.v2;

import android.content.Context;
import android.content.Intent;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailFragment;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/16:43
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CircleDetailActivityV2 extends TSActivity<CircleDetailPresenterV2, CircleDetailFragmentV2> {
    @Override
    protected CircleDetailFragmentV2 getFragment() {
        return CircleDetailFragmentV2.newInstance(getIntent().getLongExtra(CircleDetailFragment.CIRCLE_ID, 1));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengSharePolicyImpl.onActivityResult(requestCode, resultCode, data, this);
        mContanierFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void componentInject() {
        DaggerCircleDetailComponentV2.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .circleDetailModuleV2(new CircleDetailModuleV2(mContanierFragment))
                .shareModule(new ShareModule(this))
                .build().inject(this);
    }

    public static void startCircleDetailActivity(Context context, Long circleId) {
        Intent intent = new Intent(context, CircleDetailActivityV2.class);
        intent.putExtra(CircleDetailFragment.CIRCLE_ID, circleId);
        context.startActivity(intent);
    }
}

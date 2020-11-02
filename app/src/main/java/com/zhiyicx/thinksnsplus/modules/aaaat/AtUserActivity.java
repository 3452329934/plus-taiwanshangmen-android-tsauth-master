package com.zhiyicx.thinksnsplus.modules.aaaat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/13/10:54
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AtUserActivity extends TSActivity<AtUserListPresenter, AtUserListFragment> {

    @Override
    protected AtUserListFragment getFragment() {
        return AtUserListFragment.newInstance();
    }

    @Override
    protected void componentInject() {
        DaggerAtUserListComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .atUserListPresenterModule(new AtUserListPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    public static void startAtUserActivity(Context context) {
        Intent intent = new Intent(context, AtUserActivity.class);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, AtUserListFragment.REQUES_USER);
        }
    }

    public static void startAtUserActivity(Fragment context) {
        Intent intent = new Intent(context.getContext(), AtUserActivity.class);
        context.startActivityForResult(intent, AtUserListFragment.REQUES_USER);
    }

    @Override
    public void onBackPressed() {
        mContanierFragment.onBackPressed();
    }
}

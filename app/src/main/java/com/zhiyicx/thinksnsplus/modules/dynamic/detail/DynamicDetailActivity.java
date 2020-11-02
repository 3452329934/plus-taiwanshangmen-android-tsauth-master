package com.zhiyicx.thinksnsplus.modules.dynamic.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.modules.home.message.messagecomment.MessageCommentAdapter;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.ZhiyiVideoView;

public class DynamicDetailActivity extends TSActivity<DynamicDetailPresenter, DynamicDetailFragment> {

    @Override
    protected DynamicDetailFragment getFragment() {
        return DynamicDetailFragment.initFragment(
                getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerDynamicDetailPresenterCompnent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .shareModule(new ShareModule(DynamicDetailActivity.this))
                .dynamicDetailPresenterModule(new DynamicDetailPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    @Override
    public void onBackPressed() {
        if (mContanierFragment != null && mContanierFragment.backPressed()) {
            return;
        }
        if (ZhiyiVideoView.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengSharePolicyImpl.onActivityResult(requestCode, resultCode, data, this);
        mContanierFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmengSharePolicyImpl.onDestroy(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mContanierFragment.replaceVideoIfNeed();
    }

    public static void startDynamicDetailActivity(Context context, Long id) {
        Intent intent = new Intent(context, DynamicDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(MessageCommentAdapter
                .BUNDLE_SOURCE_ID, id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

package com.zhiyicx.thinksnsplus.modules.q_a.detail.answer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AnswerInfoBean;

/**
 * @Author Jliuer
 * @Date 2017/03/07
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AnswerDetailsActivity extends TSActivity<AnswerDetailsPresenter, AnswerDetailsFragment> {

    @Override
    protected AnswerDetailsFragment getFragment() {
        return AnswerDetailsFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerAnswerDetailsComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .shareModule(new ShareModule(this))
                .answerDetailsPresenterMudule(new AnswerDetailsPresenterMudule(mContanierFragment))
                .build().inject(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengSharePolicyImpl.onActivityResult(requestCode, resultCode, data, this);
        mContanierFragment.onActivityResult(requestCode, resultCode, data);
    }

    public static void startAnswerDetailsActivity(Context context, Long id) {
        startAnswerDetailsActivity(context, new AnswerInfoBean(id));
    }

    public static void startAnswerDetailsActivity(Context context, AnswerInfoBean data) {
        Intent intent = new Intent(context, AnswerDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(AnswerDetailsFragment.BUNDLE_SOURCE_ID, data.getId());
        bundle.putSerializable(AnswerDetailsFragment.BUNDLE_ANSWER, data);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

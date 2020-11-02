package com.zhiyicx.thinksnsplus.modules.q_a.detail.question;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;

import static com.zhiyicx.thinksnsplus.modules.home.message.messagereview.adapter.BaseTopItem.BUNDLE_SOURCE_ID;

/**
 * @author Catherine
 * @describe 问题详情
 * @date 2017/8/15
 * @contact email:648129313@qq.com
 */

public class QuestionDetailActivity extends TSActivity<QuestionDetailPresenter, QuestionDetailFragment> {

    public static final String BUNDLE_QUESTION_BEAN = "bundle_question_bean";

    @Override
    protected QuestionDetailFragment getFragment() {
        return QuestionDetailFragment.
                instance(getIntent().getBundleExtra(BUNDLE_QUESTION_BEAN));
    }

    @Override
    protected void componentInject() {
        DaggerQuestionDetailComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .questionDetailPresenterModule(new QuestionDetailPresenterModule(mContanierFragment))
                .shareModule(new ShareModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengSharePolicyImpl.onActivityResult(requestCode, resultCode, data, this);
        mContanierFragment.onActivityResult(requestCode, resultCode, data);
    }

    public static void startQuestionDetailActivity(Context context, Long id) {
        startQuestionDetailActivity(context, new QAListInfoBean(id));
    }

    public static void startQuestionDetailActivity(Context context, QAListInfoBean data) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_SOURCE_ID,data.getId());
        bundle.putSerializable(BUNDLE_QUESTION_BEAN, data);
        intent.putExtra(BUNDLE_QUESTION_BEAN, bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmengSharePolicyImpl.onDestroy(this);
    }
}

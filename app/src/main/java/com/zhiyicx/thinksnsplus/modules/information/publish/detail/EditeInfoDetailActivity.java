package com.zhiyicx.thinksnsplus.modules.information.publish.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.InfoPublishBean;
import com.zhiyicx.thinksnsplus.modules.markdown_editor.BaseMarkdownActivity;

import static com.zhiyicx.thinksnsplus.modules.information.publish.detail.EditeInfoDetailFragment.INFO_REFUSE;

/**
 * @Author Jliuer
 * @Date 2018/01/18/9:48
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class EditeInfoDetailActivity extends BaseMarkdownActivity<EditeInfoDetailPresenter, EditeInfoDetailFragmentV2> {

    @Override
    protected EditeInfoDetailFragmentV2 getYourFragment() {
        return EditeInfoDetailFragmentV2.getInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerEditeInfoDetailComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .editeInfoDetailPresenterModule(new EditeInfoDetailPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    public static void startEditeInfoDetailActivity(Context context, InfoPublishBean infoPublishBean){
        Intent intent = new Intent(context, EditeInfoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(INFO_REFUSE, infoPublishBean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

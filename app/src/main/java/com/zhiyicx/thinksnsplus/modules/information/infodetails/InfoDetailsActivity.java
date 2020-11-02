package com.zhiyicx.thinksnsplus.modules.information.infodetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.modules.home.message.messagecomment.MessageCommentAdapter;

import static com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsFragment.BUNDLE_INFO;

/**
 * @Author Jliuer
 * @Date 2017/03/07
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class InfoDetailsActivity extends TSActivity<InfoDetailsPresenter, InfoDetailsFragment> {

    @Override
    protected InfoDetailsFragment getFragment() {
        Bundle bundle = getIntent().getBundleExtra(BUNDLE_INFO);
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        return InfoDetailsFragment.newInstance(bundle);
    }

    @Override
    protected void componentInject() {
        DaggerInfoDetailsComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .infoDetailsPresenterMudule(new InfoDetailsPresenterMudule(mContanierFragment))
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmengSharePolicyImpl.onDestroy(this);
    }

    public static void startInfoDetailsActivity(Context context, Long id) {
        Intent intent = new Intent(context, InfoDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(MessageCommentAdapter
                .BUNDLE_SOURCE_ID, id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

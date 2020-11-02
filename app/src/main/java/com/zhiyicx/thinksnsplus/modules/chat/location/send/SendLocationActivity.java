package com.zhiyicx.thinksnsplus.modules.chat.location.send;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;

/**
 * @Author Jliuer
 * @Date 2018/06/12/15:29
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SendLocationActivity extends TSActivity<SendLocationPresenter, SendLocationFragment> {

    @Override
    protected SendLocationFragment getFragment() {
        return SendLocationFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {

    }

    public static void startSendLocationActivity(Fragment from, Context context, String userName) {
        Intent intent = new Intent(context, SendLocationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SendLocationFragment.USER_NAME, userName);
        intent.putExtras(bundle);
        from.startActivityForResult(intent, SendLocationFragment.REQUST_CODE_AREA);
    }
}

package com.zhiyicx.thinksnsplus.modules.shortvideo.record;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;

/**
 * @Author Jliuer
 * @Date 2018/03/28/10:52
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class RecordActivity extends TSActivity<AppBasePresenter, RecordFragment> {

    public static void startRecordActivity(Context context, TopicListBean topicListBean) {
        Intent intent = new Intent(context, RecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SearchTopicFragment.TOPIC, topicListBean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyWindowFullScreen();
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onBackPressed() {
        mContanierFragment.onBackPressed();
    }

    @Override
    protected RecordFragment getFragment() {
        return RecordFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {

    }
}

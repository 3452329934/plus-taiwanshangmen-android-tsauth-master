package com.zhiyicx.thinksnsplus.modules.dynamic.send.dynamic_type;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.dynamic_type.V2.SelectDynamicTypeFragmentV2;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;

/**
 * @Describe 发布按钮选择页
 * @Author Jungle68
 * @Date 2017/3/30
 * @Contact master.jungle68@gmail.com
 */
public class SelectDynamicTypeActivity extends TSActivity<SelectDynamicTypePresenter, SelectDynamicTypeFragmentV2> {

    @Override
    protected SelectDynamicTypeFragmentV2 getFragment() {
        return SelectDynamicTypeFragmentV2.getInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerSelectDynamicTypeComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .selectDynamicTypePresenterModule(new SelectDynamicTypePresenterModule(mContanierFragment))
                .build()
                .inject(this);
    }

    @Override
    public void onBackPressed() {
        mContanierFragment.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DeviceUtils.openFullScreenModel(this);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static void startSelectDynamicTypeActivity(Context context, TopicListBean topic) {
        Intent intent = new Intent(context, SelectDynamicTypeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SearchTopicFragment.TOPIC, topic);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

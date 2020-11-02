package com.zhiyicx.thinksnsplus.modules.home.message.messagelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.modules.home.message.MessageAdapterV2;

/**
 * @author Catherine
 * @describe
 * @date 2017/12/28
 * @contact email:648129313@qq.com
 */
public class MessageConversationFragment
        extends BaseMessageConversationFragment<MessageConversationContract.Presenter<MessageItemBeanV2>, MessageItemBeanV2> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerMessageConversationComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .messageConversationPresenterModule(new MessageConversationPresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        MessageAdapterV2 commonAdapter = new MessageAdapterV2(getActivity(), mListDatas, mPresenter);
        commonAdapter.setOnSwipItemClickListener(this);
        commonAdapter.setOnUserInfoClickListener(this);
        commonAdapter.setOnConversationItemLongClickListener(this);
        return commonAdapter;
    }
}

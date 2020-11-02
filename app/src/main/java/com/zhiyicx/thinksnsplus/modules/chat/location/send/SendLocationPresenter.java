package com.zhiyicx.thinksnsplus.modules.chat.location.send;

import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.data.beans.ChatLocationBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2018/06/12/15:29
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SendLocationPresenter extends AppBasePresenter<SendLocationContract.View> implements SendLocationContract.Presenter {

    @Inject
    public SendLocationPresenter(SendLocationContract.View rootView) {
        super(rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {

    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {

    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<ChatLocationBean> data, boolean isLoadMore) {
        return false;
    }
}

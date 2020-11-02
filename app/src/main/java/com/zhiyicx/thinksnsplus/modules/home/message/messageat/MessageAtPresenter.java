package com.zhiyicx.thinksnsplus.modules.home.message.messageat;

import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.notify.AtMeaasgeBean;
import com.zhiyicx.thinksnsplus.data.beans.UserFollowerCountBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseDynamicRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.CommentRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.NotificationRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.i.INotificationRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/16/11:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageAtPresenter extends AppBasePresenter<MessageAtContract.View> implements MessageAtContract.Presenter {

    @Inject
    NotificationRepository mNotificationRepository;
    @Inject
    BaseDynamicRepository mBaseDynamicRepository;

    @Inject
    public MessageAtPresenter(MessageAtContract.View rootView) {
        super(rootView);
    }

    @Override
    public void sendComment(int currentPostion, long replyUserId, String commentContent) {
        AtMeaasgeBean currentCommentBean = mRootView.getListDatas().get(currentPostion);
        String path = CommentRepository.getCommentPath(currentCommentBean.getResourceable().getId()
                , currentCommentBean.getResourceable().getType(), 0);
        Subscription commentSub = mCommentRepository.sendCommentV2(commentContent, replyUserId, Long.parseLong(AppApplication
                .getmCurrentLoginAuth().getUser_id() + "" + System.currentTimeMillis()), path)
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R.string.comment_ing)))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<Object> data) {
                        mRootView.showSnackSuccessMessage(data.getMessage().get(0));
                        requestNetData(0L, false);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.comment_fail));
                    }
                });
        addSubscrebe(commentSub);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        if (!isLoadMore) {
            mNotificationRepository.makeNotificationReaded(INotificationRepository.AT)
                    .subscribe(o -> {
                    });
        }
        Subscription commentSub = mBaseDynamicRepository.getAtMessages(INotificationRepository.AT, mRootView.getPage())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<List<AtMeaasgeBean>>() {
                    @Override
                    protected void onSuccess(List<AtMeaasgeBean> data) {
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showMessage(message);
                        mRootView.onResponseError(null, isLoadMore);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
        addSubscrebe(commentSub);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(null, isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<AtMeaasgeBean> data, boolean isLoadMore) {
        return false;
    }
}

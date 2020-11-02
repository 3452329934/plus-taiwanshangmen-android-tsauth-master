package com.zhiyicx.thinksnsplus.modules.topic.detail.joined;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseTopicRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/31/9:10
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class JoinedUserPresenter extends AppBasePresenter<JoinedUserContract.View> implements JoinedUserContract.Presenter {

    @Inject
    UserInfoRepository mUserInfoRepository;
    @Inject
    BaseTopicRepository mTopicRepository;

    @Inject
    public JoinedUserPresenter(JoinedUserContract.View rootView) {
        super(rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        getParticipants(mRootView.getTopicId(), maxId.intValue(), isLoadMore);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(null, isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<UserInfoBean> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public void getParticipants(Long topicId, Integer offset, boolean isLoadMore) {
        Subscription subscription = mTopicRepository.getParticipants(topicId, TSListFragment.DEFAULT_PAGE_SIZE, offset)
                .flatMap((Func1<List<Integer>, Observable<List<UserInfoBean>>>) integers -> {
                    final List<Object> users = new ArrayList<>(integers);
                    return mUserInfoRepository.getUserInfo(users, true);
                }).subscribe(new BaseSubscribeForV2<List<UserInfoBean>>() {
                    @Override
                    protected void onSuccess(List<UserInfoBean> data) {
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void handleUserFollowState(int index, UserInfoBean userInfoBean) {
        mUserInfoRepository.handleFollow(userInfoBean);
        mRootView.refreshData();
    }
}

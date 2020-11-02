package com.zhiyicx.thinksnsplus.modules.aaaat;

import android.text.TextUtils;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/13/11:40
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AtUserListPresenter extends AppBasePresenter<AtUserContract.View> implements AtUserContract.Presenter {

    @Inject
    UserInfoRepository mUserInfoRepository;

    private Subscription searchSub;

    private List<UserInfoBean> mFollowUserList = new ArrayList<>();

    @Inject
    public AtUserListPresenter(AtUserContract.View rootView) {
        super(rootView);
    }

    @Override
    public void searchUser(String name, int offset, boolean isLoadMore) {
        if (searchSub != null && !searchSub.isUnsubscribed()) {
            searchSub.unsubscribe();
        }
        searchSub = mUserInfoRepository.searchUserInfo(null, name, offset, null, TSListFragment.DEFAULT_PAGE_SIZE)
                .subscribe(new BaseSubscribeForV2<List<UserInfoBean>>() {
                    @Override
                    protected void onSuccess(List<UserInfoBean> data) {
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.onResponseError(null, isLoadMore);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
        addSubscrebe(searchSub);
    }

    @Override
    public void getFollowListFromNet(long userId, int maxId, boolean isLoadMore) {
        if (mFollowUserList != null && !mFollowUserList.isEmpty() && !isLoadMore && !mRootView.refreshExtraData()) {
            mRootView.onNetResponseSuccess(mFollowUserList, false);
            return;
        }
        Subscription subscription = mUserInfoRepository.getUserFriendsList(maxId, null)
                .subscribe(new BaseSubscribeForV2<List<UserInfoBean>>() {
                    @Override
                    protected void onSuccess(List<UserInfoBean> data) {
                        if (mFollowUserList == null) {
                            mFollowUserList = new ArrayList<>();
                        }
                        if (!isLoadMore) {
                            mFollowUserList.clear();
                        }
                        mFollowUserList.addAll(data);
                        mRootView.refreshExtraData(true);
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        Throwable throwable = new Throwable(message);
                        mRootView.onResponseError(throwable, isLoadMore);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        LogUtils.e(throwable, throwable.getMessage());
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        String searchKeyWord = mRootView.getKeyWord();
        if (TextUtils.isEmpty(searchKeyWord)) {
            getFollowListFromNet(AppApplication.getMyUserIdWithdefault(), maxId.intValue(), isLoadMore);
        } else {
            searchUser(searchKeyWord, maxId.intValue(), isLoadMore);
        }
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(null, isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<UserInfoBean> data, boolean isLoadMore) {
        return false;
    }
}

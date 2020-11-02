package com.zhiyicx.thinksnsplus.data.source.repository;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.config.BackgroundTaskRequestMethodConfig;
import com.zhiyicx.thinksnsplus.data.beans.BackgroundRequestTaskBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.remote.TopicClient;
import com.zhiyicx.thinksnsplus.modules.topic.ITopicRepository;
import com.zhiyicx.thinksnsplus.modules.topic.main.TopicListFragment;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/17:31
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class BaseTopicRepository extends BaseDynamicRepository implements ITopicRepository {

    TopicClient mTopicClient;

    @Inject
    public BaseTopicRepository(ServiceManager serviceManager) {
        super(serviceManager);
        mTopicClient = serviceManager.getTopicClient();
    }

    @Override
    public Observable<List<TopicListBean>> getTopicListBean(String only, String query, String direction, Integer limit, Long index) {
        if (!TopicListFragment.TYPE_HOT.equals(only)) {
            only = null;
        }
        return mTopicClient.getTopicListBean(only, query, direction, limit, index)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<DynamicDetailBeanV2>> getTopicDynamicListBean(Long topicId, String direction, Integer limit, Long index, boolean isLoadMore) {
        return dealWithDynamicListV2(mTopicClient.getTopicDynamicListBeanV2(topicId, direction, limit, index),
                null, isLoadMore);
    }

    @Override
    public Observable<TopicDetailBean> getTopicDetailBean(Long topicId) {
        return mTopicClient.getTopicDetailBean(topicId)
                .flatMap((Func1<TopicDetailBean, Observable<TopicDetailBean>>) detailBean -> {
                    final List<Object> userIds = new ArrayList<>();
                    userIds.add(detailBean.getCreator_user_id());
                    if (detailBean.getParticipants() != null) {
                        userIds.addAll(detailBean.getParticipants());
                    }
                    return mUserInfoRepository.getUserInfo(userIds).flatMap((Func1<List<UserInfoBean>,
                            Observable<TopicDetailBean>>) userInfoBeans -> {
                        UserInfoBean creator = null;
                        for (UserInfoBean user : userInfoBeans) {
                            if (user.getUser_id().equals(detailBean.getCreator_user_id())) {
                                detailBean.setCreator_user(user);
                                creator = user;
                                break;
                            }
                        }
                        if (creator != null) {
                            // 创建者第一个
                            userInfoBeans.remove(creator);
                            userInfoBeans.add(0, creator);
                        }
                        detailBean.setUserList(userInfoBeans);
                        return Observable.just(detailBean);
                    });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BaseJsonV2<Integer>> createTopic(String name, String desc, String logo) {
        return mTopicClient.createTopic(name, desc, logo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> modifyTopic(Long topicId, String desc, String logo) {
        return mTopicClient.modifyTopic(topicId, desc, logo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void handleTopicFollowState(Long topicId, boolean isFollowed) {
        BackgroundRequestTaskBean requestTaskBean = new BackgroundRequestTaskBean();
        requestTaskBean.setMethodType(isFollowed ? BackgroundTaskRequestMethodConfig.DELETE_V2 : BackgroundTaskRequestMethodConfig.PUT);
        requestTaskBean.setPath(String.format(Locale.getDefault(), ApiConfig.APP_PATH_FOLLOW_TOPICS_FORMAT, topicId));
        BackgroundTaskManager.getInstance(mContext).
                addBackgroundRequestTask(requestTaskBean);
    }

    @Override
    public Observable<List<Integer>> getParticipants(Long topicId, Integer limit, Integer offset) {
        return mTopicClient.getParticipants(topicId, limit, offset).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

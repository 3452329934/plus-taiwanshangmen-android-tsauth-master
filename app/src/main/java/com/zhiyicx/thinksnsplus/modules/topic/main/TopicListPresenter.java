package com.zhiyicx.thinksnsplus.modules.topic.main;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.source.local.TopicListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseTopicRepository;
import com.zhiyicx.thinksnsplus.modules.topic.ITopicRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/17:21
 * @Email Jliuer@aliyun.com
 * @Description 话题首页
 */
public class TopicListPresenter extends AppBasePresenter<TopicListContract.View> implements TopicListContract.Presenter {

    BaseTopicRepository mTopicRepository;
    TopicListBeanGreenDaoImpl mTopicListBeanGreenDao;

    @Inject
    public TopicListPresenter(TopicListContract.View rootView,
                              BaseTopicRepository topicRepository,
                              TopicListBeanGreenDaoImpl topicListBeanGreenDao) {
        super(rootView);
        mTopicRepository = topicRepository;
        mTopicListBeanGreenDao = topicListBeanGreenDao;
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        Subscription subscription = mTopicRepository.getTopicListBean(mRootView.getTopicListType(), null, ITopicRepository.DESC, TSListFragment
                .DEFAULT_PAGE_SIZE, maxId)
                .flatMap((Func1<List<TopicListBean>, Observable<List<TopicListBean>>>) topicListBeans -> {
                    LogUtils.d("thread");
                    if (TopicListFragment.TYPE_HOT.equals(mRootView.getTopicListType())) {
                        for (TopicListBean listBean : topicListBeans) {
                            listBean.setIsHotTopic(true);
                        }
                    }
                    return Observable.just(topicListBeans);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<List<TopicListBean>>() {
                    @Override
                    protected void onSuccess(List<TopicListBean> data) {
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
        addSubscrebe(subscription);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(null, isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<TopicListBean> data, boolean isLoadMore) {
        mTopicListBeanGreenDao.saveMultiData(data);
        return false;
    }

    @Override
    public void handleTopicFollowState(TopicListBean topicDetailBean) {
        mTopicRepository.handleTopicFollowState(topicDetailBean.getId(), topicDetailBean.isHas_followed());
        topicDetailBean.setHas_followed(!topicDetailBean.isHas_followed());
    }
}

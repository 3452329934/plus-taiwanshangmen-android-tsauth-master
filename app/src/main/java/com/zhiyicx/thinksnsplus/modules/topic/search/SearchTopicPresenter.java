package com.zhiyicx.thinksnsplus.modules.topic.search;

import android.text.TextUtils;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.SharePreferenceUtils;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.source.local.TopicListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseTopicRepository;
import com.zhiyicx.thinksnsplus.modules.topic.ITopicRepository;
import com.zhiyicx.thinksnsplus.modules.topic.main.TopicListFragment;

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
 * @Date 2018/07/24/14:26
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchTopicPresenter extends AppBasePresenter<SearchTopicContract.View> implements SearchTopicContract.Presenter {

    BaseTopicRepository mTopicRepository;
    TopicListBeanGreenDaoImpl mTopicListBeanGreenDao;

    private Subscription searchSub;

    @Inject
    public SearchTopicPresenter(SearchTopicContract.View rootView, BaseTopicRepository baseTopicRepository,
                                TopicListBeanGreenDaoImpl topicListBeanGreenDao) {
        super(rootView);
        mTopicRepository = baseTopicRepository;
        mTopicListBeanGreenDao = topicListBeanGreenDao;
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {

        if (searchSub != null && !searchSub.isUnsubscribed()) {
            searchSub.unsubscribe();
        }
        final String hotKey = TextUtils.isEmpty(mRootView.getSearchKeyWords()) ? TopicListFragment.TYPE_HOT : null;
        searchSub = mTopicRepository.getTopicListBean(hotKey, mRootView.getSearchKeyWords(), ITopicRepository.DESC, TSListFragment.DEFAULT_PAGE_SIZE, maxId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<List<TopicListBean>>() {
                    @Override
                    protected void onSuccess(List<TopicListBean> data) {
                        if (!isLoadMore && !TextUtils.isEmpty(hotKey)) {
                            for (TopicListBean listBean : data) {
                                listBean.setIsHotTopic(true);
                            }
                            mTopicListBeanGreenDao.saveMultiData(data);
                            mRootView.setHotTopicList(data);
                        }
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
        addSubscrebe(searchSub);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        if (System.currentTimeMillis() - SharePreferenceUtils.getLong(mContext, SearchTopicFragment.TOPIC_HISTORY_INTERVAL)
                > SearchTopicFragment.TOPIC_HISTORY_INTERVAL_TIME) {
            // 离上一次超出 60s
            mRootView.onCacheResponseSuccess(null, isLoadMore);
            SharePreferenceUtils.saveLong(mContext, SearchTopicFragment.TOPIC_HISTORY_INTERVAL, System.currentTimeMillis());
        } else {
            mRootView.onCacheResponseSuccess(mTopicListBeanGreenDao.getHotTopic(), isLoadMore);
        }
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<TopicListBean> data, boolean isLoadMore) {
        return false;
    }
}

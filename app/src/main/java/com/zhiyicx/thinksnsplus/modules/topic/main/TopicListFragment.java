package com.zhiyicx.thinksnsplus.modules.topic.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.topic.detail.TopicDetailActivity;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/17:16
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopicListFragment extends TSListFragment<TopicListContract.Presenter, TopicListBean> implements TopicListContract.View {

    public static final String TYPE_NEW = "";
    public static final String TYPE_HOT = "hot";
    public static final String TYPE = "type";

    /**
     * 仅用于构造函数
     */
    @Inject
    TopicListPresenter mTopicListPresenter;

    private String mTopicListType;

    public static TopicListFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        TopicListFragment fragment = new TopicListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getTopicListType() {
        return mTopicListType;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        Observable.create(subscriber -> {
            // 在 super.initData();之前，因为initdata 会使用到 presenter
            DaggerTopicListComponent.builder()
                    .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .topicListPresenterModule(new TopicListPresenterModule(this))
                    .build().inject(this);
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        initData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

    @Override
    protected void initData() {
        mTopicListType = TYPE_HOT;
        if (getArguments() != null) {
            mTopicListType = getArguments().getString(TYPE, TYPE_HOT);
        }

        super.initData();
        mRvList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvList.setBackgroundColor(Color.WHITE);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
//        return new CommonAdapter<TopicListBean>(mActivity, R.layout.item_topic_main, mListDatas) {
//            @Override
//            protected void convert(ViewHolder holder, TopicListBean topicListBean, int position) {
//                holder.setText(R.id.tv_name, topicListBean.getName());
//                holder.itemView.setOnClickListener(v -> TopicDetailActivity.startTopicDetailActivity(mActivity, topicListBean.getId()));
//            }
//        };

        mRvList.setPadding(0, ConvertUtils.dp2px(getContext(), getItemDecorationSpacing()), 0, 0);

        return new CommonAdapter<TopicListBean>(mActivity, R.layout.item_topic_mainv2, mListDatas) {
            @Override
            protected void convert(ViewHolder holder, TopicListBean topicListBean, int position) {
                holder.setText(R.id.tv_name, topicListBean.getName());
                Glide.with(mActivity)
                        .load(topicListBean.getLogo() != null ? topicListBean.getLogo().getUrl() : "")
                        .error(R.mipmap.pic_cover)
                        .placeholder(R.mipmap.pic_cover)
                        .into(holder.getImageViwe(R.id.iv_image));
                holder.itemView.setOnClickListener(v -> {
                    if (mPresenter != null) {
                        if (!mPresenter.handleTouristControl()) {
                            TopicDetailActivity.startTopicDetailActivity(mActivity, topicListBean.getId());
                        }
                    }
                });
                holder.setText(R.id.tv_follow, getString(topicListBean.isHas_followed() ? R.string.followed : R.string.add_follow));
                holder.getView(R.id.tv_follow).setOnClickListener(v -> {
                    if (mPresenter != null) {
                        if (!mPresenter.handleTouristControl()) {
                            mPresenter.handleTopicFollowState(topicListBean);
                            holder.setText(R.id.tv_follow, getString(topicListBean.isHas_followed() ? R.string.followed : R.string.add_follow));
                        }
                    }
                });

            }
        };
    }

    @Override
    protected float getItemDecorationSpacing() {
        return 15f;
    }
}

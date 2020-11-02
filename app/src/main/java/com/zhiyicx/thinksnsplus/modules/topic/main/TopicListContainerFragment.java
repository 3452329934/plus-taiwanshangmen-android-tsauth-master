package com.zhiyicx.thinksnsplus.modules.topic.main;

import android.support.v4.app.Fragment;
import android.view.View;

import com.zhiyicx.baseproject.base.TSViewPagerFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.topic.create.CreateTopicActivity;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/17:11
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopicListContainerFragment extends TSViewPagerFragment<TopicListContract.Presenter>
        implements TopicListContract.View {

    @Inject
    TopicListPresenter mTopicListPresenter;

    @Override
    protected List<String> initTitles() {
        return Arrays.asList(getString(R.string.topic_type_hot), getString(R.string.topic_type_new));
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
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return true;
    }

    @Override
    protected void setRightLeftClick() {
        if (mPresenter != null) {
            if (!mPresenter.handleTouristControl()) {
                SearchTopicActivity.startSearchTopicActivity(mActivity);
            }
        }
    }

    @Override
    protected void setRightClick() {
        if (mPresenter != null) {
            if (!mPresenter.handleTouristControl()) {
                CreateTopicActivity.startCreateTopicActivity(mActivity, null);
            }
        }
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.topic);
    }

    @Override
    protected List<Fragment> initFragments() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList();
        }
        mFragmentList.add(TopicListFragment.newInstance(TopicListFragment.TYPE_HOT));
        mFragmentList.add(TopicListFragment.newInstance(TopicListFragment.TYPE_NEW));
        return mFragmentList;
    }

    @Override
    protected View getRightViewOfMusicWindow() {
        return mTsvToolbar.getRightTextView();
    }

    @Override
    protected void initData() {
        DaggerTopicListComponent.builder()
                .topicListPresenterModule(new TopicListPresenterModule(this))
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .build().inject(this);
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTsvToolbar.setRightImg(R.mipmap.ico_create_topic, R.color.white);
        mTsvToolbar.setLeftRightImg(R.mipmap.ico_search, R.color.white);

        mTsvToolbar.setRightClickListener(this, this::setRightClick);
        mTsvToolbar.setLeftClickListener(this, this::setLeftClick);
        mTsvToolbar.setLeftRightClickListener(this, this::setRightLeftClick);
    }

    @Override
    public String getTopicListType() {
        return null;
    }

    @Override
    public void onNetResponseSuccess(List<TopicListBean> data, boolean isLoadMore) {

    }

    @Override
    public void onCacheResponseSuccess(List<TopicListBean> data, boolean isLoadMore) {

    }

    @Override
    public void onResponseError(Throwable throwable, boolean isLoadMore) {

    }

    @Override
    public void hideRefreshState(boolean isLoadMore) {

    }

    @Override
    public void showStickyMessage(String msg) {

    }

    @Override
    public void showStickyHtmlMessage(@NotNull String html) {

    }

    @Override
    public void hideStickyMessage() {

    }

    @Override
    public int getPage() {
        return 0;
    }

    @Override
    public List<TopicListBean> getListDatas() {
        return null;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void refreshData(List<TopicListBean> datas) {

    }

    @Override
    public void refreshData(int index) {

    }

    @Override
    public void refreshRangeData(int start, int count) {

    }

    @Override
    public void startRefrsh() {

    }
}

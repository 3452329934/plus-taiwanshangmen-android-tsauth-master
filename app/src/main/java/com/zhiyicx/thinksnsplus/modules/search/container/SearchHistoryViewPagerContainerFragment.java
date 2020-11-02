package com.zhiyicx.thinksnsplus.modules.search.container;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.zhiyicx.baseproject.base.TSViewPagerAdapter;
import com.zhiyicx.baseproject.base.TSViewPagerFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.source.local.SearchHistoryBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchListener;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.ISearchSuceesListener;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchCircleListFragment;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchDynamicListFragment;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchFeedTopicListFragment;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchInfoListFragment;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchPostListFragment;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchQAListFragment;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchQATopicListFragment;
import com.zhiyicx.thinksnsplus.modules.search.typelist.SearchUserListFragment;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/10:57
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchHistoryViewPagerContainerFragment extends TSViewPagerFragment implements ISearchSuceesListener {

    private String mCurrentSearchContent = "";
    public static final String KEY_WORD = "keyword";

    SearchHistoryBeanGreenDaoImpl mSearchHistoryBeanGreenDao;

    public static SearchHistoryViewPagerContainerFragment newInstance(Bundle args) {
        SearchHistoryViewPagerContainerFragment fragment = new SearchHistoryViewPagerContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected void initView(View rootView) {
        if (getArguments() != null) {
            String keyword = getArguments().getString(KEY_WORD);
            if (!TextUtils.isEmpty(keyword)) {
                mCurrentSearchContent = keyword;
            }
        }
        mSearchHistoryBeanGreenDao = new SearchHistoryBeanGreenDaoImpl(getActivity().getApplication());
        super.initView(rootView);
    }

    @Override
    protected List<String> initTitles() {
        return Arrays.asList(getResources().getStringArray(R.array.global_search_type));
    }

    @Override
    protected List<Fragment> initFragments() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
            SearchDynamicListFragment dynamicListFragment = SearchDynamicListFragment.newInstance(mCurrentSearchContent);
            dynamicListFragment.setISearchSuceesListener(this);

            SearchInfoListFragment searchInfoListFragment = SearchInfoListFragment.newInstance();
            searchInfoListFragment.setISearchSuceesListener(this);

            SearchUserListFragment searchUserListFragment = SearchUserListFragment.newInstance();
            searchUserListFragment.setISearchSuceesListener(this);

            SearchCircleListFragment searchCircleListFragment = SearchCircleListFragment.newInstance();
            searchCircleListFragment.setISearchSuceesListener(this);

            SearchPostListFragment searchPostListFragment = SearchPostListFragment.newInstance();
            searchPostListFragment.setISearchSuceesListener(this);

            SearchQAListFragment searchQAListFragment = SearchQAListFragment.newInstance();
            searchQAListFragment.setISearchSuceesListener(this);

            SearchQATopicListFragment searchQATopicListFragment = SearchQATopicListFragment.newInstance();
            searchQATopicListFragment.setISearchSuceesListener(this);

            SearchFeedTopicListFragment searchFeedTopicListFragment = SearchFeedTopicListFragment.newInstance();
            searchFeedTopicListFragment.setISearchSuceesListener(this);


            mFragmentList.add(dynamicListFragment);
            mFragmentList.add(searchInfoListFragment);
            mFragmentList.add(searchUserListFragment);
            mFragmentList.add(searchCircleListFragment);
            mFragmentList.add(searchPostListFragment);
            mFragmentList.add(searchQAListFragment);
            mFragmentList.add(searchQATopicListFragment);
            mFragmentList.add(searchFeedTopicListFragment);
        }
        return mFragmentList;
    }

    @Override
    protected void initData() {
        mVpFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onSearhChanged(mCurrentSearchContent);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initViewPager(View rootView) {
        mTsvToolbar = rootView.findViewById(com.zhiyicx.baseproject.R.id.tsv_toolbar);
        mTsvToolbar.setLeftImg(0);
        mTsvToolbar.setTabSpacing(getResources().getDimensionPixelOffset(R.dimen.spacing_mid));
        mTsvToolbar.showDivider(true);
        mTsvToolbar.setXOffset(10);
        mTsvToolbar.setIndicatorMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        mVpFragment = rootView.findViewById(R.id.vp_fragment);
        tsViewPagerAdapter = new TSViewPagerAdapter(getChildFragmentManager());
        tsViewPagerAdapter.bindData(initFragments());
        mVpFragment.setAdapter(tsViewPagerAdapter);
        mTsvToolbar.setAdjustMode(false);
        mTsvToolbar.initTabView(mVpFragment, initTitles());
        mVpFragment.setOffscreenPageLimit(getOffsetPage());
    }

    /**
     * 搜索内容变化
     *
     * @param string
     */
    public void onSearhChanged(String string) {
        this.mCurrentSearchContent = string;
        if (tsViewPagerAdapter == null || mVpFragment == null || TextUtils.isEmpty(string)) {
            return;
        }
        try {
            ((ISearchListener) tsViewPagerAdapter.getItem(mVpFragment.getCurrentItem())).onEditChanged(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSearchSucees(String content) {
        if (mSearchHistoryBeanGreenDao != null && !TextUtils.isEmpty(content)) {
            mSearchHistoryBeanGreenDao.saveHistory(content);
        }
    }

    @Override
    protected int getOffsetPage() {
        // DEFAULT_OFFSET_PAGE，fragment被销毁之后，重新创建 emptyview 不可见
        return mFragmentList.size();
    }
}

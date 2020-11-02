package com.zhiyicx.thinksnsplus.modules.search.container;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.widget.edittext.DeleteEditText;
import com.zhiyicx.common.utils.ActivityUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.IHistoryCententClickListener;
import com.zhiyicx.thinksnsplus.modules.search.history.SearchHistoryFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/10:18
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchContainerFragment extends TSFragment implements IHistoryCententClickListener {

    @BindView(R.id.fragment_info_search_edittext)
    DeleteEditText mFragmentInfoSearchEdittext;

    @BindView(R.id.fragment_info_search_cancle)
    TextView mFragmentInfoSearchCancle;

    @BindView(R.id.fragment_info_search_container)
    RelativeLayout mFragmentInfoSearchContainer;

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    private boolean hasViewpager;

    private SearchHistoryViewPagerContainerFragment mSearchHistoryViewPagerContainerFragment;

    @Override
    protected View getRightViewOfMusicWindow() {
        return mFragmentInfoSearchCancle;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_search_contaner;
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
    protected void initView(View rootView) {
        SearchHistoryFragment historyFragment = new SearchHistoryFragment();
        historyFragment.setIHistoryCententClickListener(this);
        ActivityUtils.addFragmentToActivity(getActivity().getSupportFragmentManager()
                , historyFragment
                , R.id.fragment_container);

        initListener();
    }

    @Override
    public void onBackPressed() {
        mActivity.finish();
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.fragment_info_search_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_info_search_cancle:
                mActivity.finish();
                break;
            default:
        }
    }

    @Override
    public void onContentClick(String str) {
        doSearch(str);
    }

    private void initListener() {
        RxTextView.editorActionEvents(mFragmentInfoSearchEdittext).subscribe(textViewEditorActionEvent -> {
            if (textViewEditorActionEvent.actionId() == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(mFragmentInfoSearchEdittext.getText().toString())) {
                    doSearch(mFragmentInfoSearchEdittext.getText().toString());
                    DeviceUtils.hideSoftKeyboard(getContext(), mFragmentInfoSearchEdittext);
                }
            }
        });
    }

    private void doSearch(String str) {
        if (!hasViewpager) {
            hasViewpager = true;
            Bundle bundle = new Bundle();
            bundle.putString(SearchHistoryViewPagerContainerFragment.KEY_WORD, str);

            mSearchHistoryViewPagerContainerFragment = SearchHistoryViewPagerContainerFragment.newInstance(bundle);
            ActivityUtils.replaceFragmentToActivity(getActivity().getSupportFragmentManager()
                    , mSearchHistoryViewPagerContainerFragment
                    , R.id.fragment_container);
        }
        mSearchHistoryViewPagerContainerFragment.onSearhChanged(str);
    }
}

package com.zhiyicx.thinksnsplus.modules.topic.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.klinker.android.link_builder.Link;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.edittext.DeleteEditText;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.topic.detail.TopicDetailActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/14:28
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchTopicFragment extends TSListFragment<SearchTopicContract.Presenter, TopicListBean>
        implements SearchTopicContract.View {

    public static final String TOPIC_HISTORY_INTERVAL = "topic_history_interval";

    /**
     * 超过5分钟更新
     */
    public static final int TOPIC_HISTORY_INTERVAL_TIME = 60000;

    public static final int CHOOSE_TOPIC = 5000;
    public static final String TOPIC = "topic";
    public static final String FROM = "from";

    @BindView(R.id.fragment_search_back)
    ImageView mFragmentInfoSearchBack;
    @BindView(R.id.fragment_info_search_edittext)
    DeleteEditText mFragmentInfoSearchEdittext;
    @BindView(R.id.fragment_info_search_cancle)
    TextView mFragmentInfoSearchCancel;
    @BindView(R.id.tv_recommend_hint)
    TextView mTvRecommendHint;
    @BindView(R.id.fragment_info_search_container)
    RelativeLayout mFragmentInfoSearchContainer;
    @BindView(R.id.toolbar_container)
    AppBarLayout mToolbarContainer;
    @BindView(R.id.tv_toolbar_left)
    TextView mTvToolbarLeft;

    private boolean isFromPublish;
    private List<TopicListBean> mHotTopics;

    public static SearchTopicFragment newInstance(Bundle args) {
        SearchTopicFragment fragment = new SearchTopicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View getRightViewOfMusicWindow() {
        return mFragmentInfoSearchCancel;
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
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new CustomLinearDecoration(0, getResources().getDimensionPixelSize(R.dimen
                .divider_line), 0, 0, ContextCompat.getDrawable(getContext(), R.drawable
                .shape_recyclerview_grey_divider));
    }

    @Override
    protected void initData() {
        super.initData();
        mFragmentInfoSearchContainer.setVisibility(searchContainerVisibility() ? View.VISIBLE : View.GONE);
        mTvRecommendHint.setVisibility(searchContainerVisibility() ? View.VISIBLE : View.GONE);
        initListener();
        Bundle bundle = getArguments();
        if (bundle != null) {
            isFromPublish = bundle.getBoolean(FROM);
        }
    }

    @Override
    public void setHotTopicList(List<TopicListBean> hotTopics) {
        if (mHotTopics != null) {
            return;
        }
        mHotTopics = hotTopics;
    }

    @Override
    public String getSearchKeyWords() {
        return mFragmentInfoSearchEdittext.getText().toString();
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<TopicListBean> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        mRefreshlayout.setEnableRefresh(isRefreshEnable());
    }

    private void initListener() {
        RxView.clicks(mFragmentInfoSearchCancel)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> getActivity().finish());
        RxView.clicks(mTvToolbarLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> getActivity().finish());

        RxTextView.textChanges(mFragmentInfoSearchEdittext)
                .subscribe(charSequence -> {
                    if (charSequence.length() == 0 && mFragmentInfoSearchContainer.getVisibility() == View.VISIBLE) {
                        mTvRecommendHint.setVisibility(View.VISIBLE);
                        if (mHotTopics != null) {
                            onNetResponseSuccess(mHotTopics, false);
                        }
                    }
                    if (charSequence.length() != 0) {
                        mTvRecommendHint.setVisibility(View.GONE);
                        mPresenter.requestNetData(0L, false);
                    }
                });
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new CommonAdapter<TopicListBean>(mActivity, R.layout.item_search_topic, mListDatas) {
            @Override
            protected void convert(ViewHolder holder, TopicListBean topicListBean, int position) {
                holder.setIsRecyclable(false);
                String keyWord = getSearchKeyWords();
                String title = topicListBean.getName();
                holder.setText(R.id.tv_content, title);
                ConvertUtils.stringLinkConvert(holder.getTextView(R.id.tv_content), searchKeyWordLink(keyWord), false);
                holder.getTextView(R.id.tv_content).setOnClickListener(view -> {
                    if (isFromPublish) {
                        Intent data = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(TOPIC, topicListBean);
                        data.putExtras(bundle);
                        mActivity.setResult(Activity.RESULT_OK, data);
                        mActivity.finish();
                    } else {
                        TopicDetailActivity.startTopicDetailActivity(mActivity, topicListBean.getId());
                    }
                });
            }
        };
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return !TextUtils.isEmpty(getSearchKeyWords());
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_searchtopic;
    }

    protected boolean searchContainerVisibility() {
        return true;
    }

    private List<Link> searchKeyWordLink(String key) {
        List<Link> links = new ArrayList<>();
        if (TextUtils.isEmpty(key)) {
            return links;
        }
        Link link = new Link(key)
                .setTextColor(ContextCompat.getColor(mActivity, R.color.important_for_theme))
                .setUnderlined(false)
                .setTextColorOfHighlightedLink(ContextCompat.getColor(mActivity, R.color.important_for_content));
        links.add(link);
        return links;
    }
}

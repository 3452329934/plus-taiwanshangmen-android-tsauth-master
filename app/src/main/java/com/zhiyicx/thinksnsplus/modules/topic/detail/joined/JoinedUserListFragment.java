package com.zhiyicx.thinksnsplus.modules.topic.detail.joined;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/31/9:11
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class JoinedUserListFragment extends TSListFragment<JoinedUserContract.Presenter, UserInfoBean>
        implements JoinedUserContract.View {

    public static final String TOPICID = "topicid";

    private Long mTopicId;

    @Override
    protected String setCenterTitle() {
        return getString(R.string.topic_join_user);
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    public static JoinedUserListFragment newInstance(Long topicId) {
        Bundle args = new Bundle();
        args.putLong(TOPICID, topicId);
        JoinedUserListFragment fragment = new JoinedUserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTopicId = getArguments().getLong(TOPICID);
        }
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new JoinedUserListAdapter(getContext(), R.layout.item_find_some_list, mListDatas, mPresenter);
    }

    @Override
    public Long getTopicId() {
        return mTopicId;
    }

    @Override
    protected Long getMaxId(@NotNull List<UserInfoBean> data) {
        return (long) getOffset();
    }
}

package com.zhiyicx.thinksnsplus.widget.comment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/16:19
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopicNoPullRecyclerView extends CommentBaseRecycleView<TopicListBean> {

    public TopicNoPullRecyclerView(Context context) {
        super(context);
    }

    public TopicNoPullRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopicNoPullRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void convertData(com.zhy.adapter.recyclerview.base.ViewHolder holder, TopicListBean topicListBean, int position) {
        holder.setText(R.id.tv_content, topicListBean.getName());
    }

    @Override
    protected void init(@Nullable AttributeSet attrs, int defStyle) {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        setLayoutManager(layoutManager);
    }

    @Override
    protected void initData(List<TopicListBean> data) {
        mAdapter = new CommonAdapter<TopicListBean>(getContext(), R.layout.item_topic_feed_channel, data) {
            @Override
            protected void convert(com.zhy.adapter.recyclerview.base.ViewHolder holder, TopicListBean t, final int position) {
                convertData(holder, t, position);
            }
        };
        setAdapter(mAdapter);
    }
}

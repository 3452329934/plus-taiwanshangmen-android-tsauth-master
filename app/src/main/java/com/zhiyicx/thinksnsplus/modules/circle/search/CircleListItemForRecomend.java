package com.zhiyicx.thinksnsplus.modules.circle.search;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zhiyicx.baseproject.base.IBaseTouristPresenter;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.modules.circle.main.adapter.CircleListItem;
import com.zhy.adapter.recyclerview.base.ViewHolder;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/03/16:09
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CircleListItemForRecomend extends CircleListItem {

    public CircleListItemForRecomend(boolean isMineJoined, Context context, CircleItemItemEvent circleItemItemEvent) {
        super(isMineJoined, context, circleItemItemEvent);
    }

    public CircleListItemForRecomend(boolean isMineJoined, Activity context, CircleItemItemEvent circleItemItemEvent, IBaseTouristPresenter presenter) {
        super(isMineJoined, context, circleItemItemEvent, presenter);
    }

    protected boolean showRecommentd() {
        return true;
    }

    @Override
    public void convert(ViewHolder holder, CircleInfo circleInfo, CircleInfo lastT, int position, int itemCounts) {
        super.convert(holder, circleInfo, lastT, position, itemCounts);
        holder.setVisible(R.id.iv_recommend, showRecommentd() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_circle_list_recommend;
    }
}

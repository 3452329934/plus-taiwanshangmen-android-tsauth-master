package com.zhiyicx.thinksnsplus.modules.chat.private_letter.adapter;

import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.widget.button.CombinationButton;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendFragment;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.concurrent.TimeUnit;

import retrofit2.http.GET;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/07/16:44
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopChooseItem implements ItemViewDelegate<MessageItemBeanV2> {

    private OnItemClickLisener mOnItemClickLisener;

    public void setOnItemClickLisener(OnItemClickLisener onItemClickLisener) {
        mOnItemClickLisener = onItemClickLisener;
    }

    public TopChooseItem(OnItemClickLisener onItemClickLisener) {
        mOnItemClickLisener = onItemClickLisener;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_combinationbutton;
    }

    @Override
    public boolean isForViewType(MessageItemBeanV2 item, int position) {
        return item.getConversation() == null;
    }

    @Override
    public void convert(ViewHolder holder, MessageItemBeanV2 messageItemBean, MessageItemBeanV2 lastT, int position, int itemCounts) {
        boolean create = ChooseFriendFragment.CREATE_CONVERSITION.equals(messageItemBean.getEmKey());
        boolean choose = ChooseFriendFragment.CHOOSE_GROUP.equals(messageItemBean.getEmKey());
        CombinationButton button = holder.getView(R.id.tv_name);
        if (create) {
            // 创建
            button.setLeftText(button.getResources().getText(R.string.create_new_conversition));
        } else {
            button.setLeftText(button.getResources().getText(R.string.choose_group));
        }
        button.setTopLineVisible(position == 0 ? View.VISIBLE : View.GONE);

        RxView.clicks(holder.itemView)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (mOnItemClickLisener != null) {
                        mOnItemClickLisener.onItemClick(messageItemBean, position);
                    }
                });
    }
}

package com.zhiyicx.thinksnsplus.modules.chat.private_letter.adapter;

import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleTransform;
import com.zhiyicx.baseproject.widget.UserAvatarView;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.chat.call.TSEMHyphenate;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/07/16:41
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ConversitionItem implements ItemViewDelegate<MessageItemBeanV2> {

    private OnItemClickLisener mOnItemClickLisener;

    public void setOnItemClickLisener(OnItemClickLisener onItemClickLisener) {
        mOnItemClickLisener = onItemClickLisener;
    }

    public ConversitionItem(OnItemClickLisener onItemClickLisener) {
        mOnItemClickLisener = onItemClickLisener;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_choose_friend;
    }

    @Override
    public boolean isForViewType(MessageItemBeanV2 item, int position) {
        return item.getConversation() != null;
    }

    @Override
    public void convert(ViewHolder holder, MessageItemBeanV2 messageItemBean, MessageItemBeanV2 lastT, int position, int itemCounts) {
        UserAvatarView userAvatarView = holder.getView(R.id.uv_head);
        switch (messageItemBean.getConversation().getType()) {
            case Chat:
                // 私聊
                UserInfoBean singleChatUserinfo = messageItemBean.getUserInfo();
                if (singleChatUserinfo == null) {
                    ChatUserInfoBean chatUserInfoBean = TSEMHyphenate.getInstance().getChatUser(messageItemBean.getEmKey());
                    ImageUtils.loadUserHead(chatUserInfoBean, userAvatarView, false);
                    holder.setText(R.id.tv_name, chatUserInfoBean.getName());
                } else {
                    ImageUtils.loadUserHead(singleChatUserinfo, userAvatarView, false);
                    holder.setText(R.id.tv_name, singleChatUserinfo.getName());
                }
                userAvatarView.getIvVerify().setVisibility(View.VISIBLE);

                break;
            case GroupChat:
                // 群组
                ChatGroupBean chatGroupBean = messageItemBean.getChatGroupBean();
                EMGroup group = EMClient.getInstance().groupManager().getGroup(messageItemBean.getEmKey());
                userAvatarView.getIvVerify().setVisibility(View.GONE);
                Glide.with(userAvatarView.getContext())
                        .load(chatGroupBean == null || TextUtils.isEmpty(chatGroupBean.getGroup_face()) ? R.mipmap.ico_ts_assistant : chatGroupBean
                                .getGroup_face())
                        .error(R.mipmap.ico_ts_assistant)
                        .placeholder(R.mipmap.ico_ts_assistant)
                        .transform(new GlideCircleTransform(userAvatarView.getContext()))
                        .into(userAvatarView.getIvAvatar());
                // 群名称
                String groupName = userAvatarView.getContext().getString(R.string.chat_group_name_default, chatGroupBean == null ? group.getGroupName
                        () : chatGroupBean.getName(), chatGroupBean == null ? group.getMemberCount() : chatGroupBean.getAffiliations_count());
                holder.setText(R.id.tv_name, groupName);
                break;
            default:
                break;
        }
        RxView.clicks(holder.itemView)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (mOnItemClickLisener != null) {
                        mOnItemClickLisener.onItemClick(messageItemBean, position);
                    }
                });

    }
}

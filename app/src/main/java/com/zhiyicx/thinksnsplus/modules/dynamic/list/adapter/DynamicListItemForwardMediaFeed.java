package com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter;

import android.content.Context;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailActivity;
import com.zhiyicx.thinksnsplus.widget.popwindow.LetterPopWindow;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.data.beans.DynamicListAdvert.DEFAULT_ADVERT_FROM_TAG;


/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jliuer
 * @Date 18/08/21 14:53
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class DynamicListItemForwardMediaFeed extends DynamicListBaseItem {

    public DynamicListItemForwardMediaFeed(Context context) {
        super(context);
    }

    @Override
    public boolean isForViewType(DynamicDetailBeanV2 item, int position) {
        Letter letter = item.getMLetter();
        return item.getFeed_mark() != null &&
                item.getFeed_from() != DEFAULT_ADVERT_FROM_TAG &&
                (item.getImages() == null || item.getImages().isEmpty()) &&
                item.getVideo() == null &&
                letter != null &&
                !TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD.equals(letter.getDynamic_type()) &&
                TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(letter.getType());
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_dynamic_list_forward_media_feed;
    }

    @Override
    public void convert(ViewHolder holder, DynamicDetailBeanV2 dynamicBean, DynamicDetailBeanV2 lastT, int position, int itemCounts) {
        super.convert(holder, dynamicBean, lastT, position, itemCounts);
        TextView forwardTitleView = holder.getView(R.id.tv_forward_name);
        TextView forwardContentView = holder.getView(R.id.tv_forward_content);


        String sufix = "：";
        String name = dynamicBean.getMLetter().getName() + sufix;
        forwardTitleView.setText(name);


        RxView.clicks(forwardTitleView)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (mOnUserInfoClickListener != null) {
                        mOnUserInfoClickListener.onUserInfoClick(new UserInfoBean(dynamicBean.getMLetter().getName()));
                    }
                });

        RxView.clicks(holder.getView(R.id.ll_forward_container))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> DynamicDetailActivity.startDynamicDetailActivity(mContext,
                        Long.parseLong(dynamicBean.getMLetter().getId())));

        boolean isImage = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE.equals(dynamicBean.getMLetter()
                .getDynamic_type());
        forwardContentView.setCompoundDrawablesWithIntrinsicBounds(isImage ?
                        R.mipmap.ico_pic_highlight : R.mipmap.ico_video_highlight
                , 0, 0, 0);
        String content = isImage ? LetterPopWindow.PIC : LetterPopWindow.VIDEO;
        forwardContentView.setText(content);

    }
}

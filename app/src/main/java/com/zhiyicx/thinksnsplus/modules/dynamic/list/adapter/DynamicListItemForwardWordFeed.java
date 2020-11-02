package com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.widget.textview.SpanTextViewWithEllipsize;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailActivity;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.data.beans.DynamicListAdvert.DEFAULT_ADVERT_FROM_TAG;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jliuer
 * @Date 18/08/21 15:39
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class DynamicListItemForwardWordFeed extends DynamicListBaseItem {

    public DynamicListItemForwardWordFeed(Context context) {
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
                TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD.equals(letter.getDynamic_type()) &&
                TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(letter.getType());
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_dynamic_list_forward_word_feed;
    }

    @Override
    public void convert(ViewHolder holder, DynamicDetailBeanV2 dynamicBean, DynamicDetailBeanV2 lastT, int position, int itemCounts) {
        super.convert(holder, dynamicBean, lastT, position, itemCounts);
        SpanTextViewWithEllipsize forwardContentView = holder.getView(R.id.tv_forward_content);


        String sufix = "：";
        String name = dynamicBean.getMLetter().getName();
        name += TextUtils.isEmpty(name) ? "" : sufix;
        String content = name + dynamicBean.getMLetter().getContent();


        forwardContentView.setText(content);
        forwardContentView.post(() -> forwardContentView.updateForRecyclerView(forwardContentView.getText(),
                forwardContentView.getWidth()));

        List<Link> links = setLiknks(dynamicBean, content);

        Link link = new Link(name)
                .setTextColor(ContextCompat.getColor(mContext, R.color.important_for_content))
                .setUnderlined(false)
                .setOnClickListener((clickedText, linkMetadata) -> {
                    if (mOnUserInfoClickListener != null) {
                        mOnUserInfoClickListener.onUserInfoClick(new UserInfoBean(clickedText.replaceAll(sufix, "")));
                    }
                })
                .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color.normal_for_dynamic_list_content));
        links.add(link);

        ConvertUtils.stringLinkConvert(forwardContentView, links, false);
        RxView.clicks(forwardContentView)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (!dynamicBean.getMLetter().isDeleted()) {
                        DynamicDetailActivity.startDynamicDetailActivity(mContext,
                                Long.parseLong(dynamicBean.getMLetter().getId()));
                    }
                });

    }
}

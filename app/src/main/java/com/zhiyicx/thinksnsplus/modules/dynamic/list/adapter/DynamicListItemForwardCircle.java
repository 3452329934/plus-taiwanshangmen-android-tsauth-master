package com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.pre.PreCircleActivity;
import com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsActivity;
import com.zhy.adapter.recyclerview.base.ViewHolder;

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
public class DynamicListItemForwardCircle extends DynamicListBaseItem {

    public DynamicListItemForwardCircle(Context context) {
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
                TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE.equals(letter.getType());
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_dynamic_list_forward_circle;
    }

    @Override
    public void convert(ViewHolder holder, DynamicDetailBeanV2 dynamicBean, DynamicDetailBeanV2 lastT, int position, int itemCounts) {
        super.convert(holder, dynamicBean, lastT, position, itemCounts);
        holder.setText(R.id.tv_forward_name, dynamicBean.getMLetter().getName());
        TextView forwardContentView = holder.getView(R.id.tv_forward_content);
        forwardContentView.setText(dynamicBean.getMLetter().getContent());
        ImageView imageView = holder.getImageViwe(R.id.iv_forward_image);
        String path = dynamicBean.getMLetter().getImage();
        imageView.setVisibility(TextUtils.isEmpty(path) ? View.GONE : View.VISIBLE);

        holder.getView(R.id.rl_forward_circle).setBackgroundResource(R.color.white);
        RxView.clicks(holder.getView(R.id.tv_forward_container))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_POST_CIRCLE_TYPE.equals(dynamicBean.getMLetter().getCircle_type())) {
                        PreCircleActivity.startPreCircleActivity(mContext, Long.parseLong(dynamicBean.getMLetter().getId()));
                    } else {
                        CircleDetailActivity.startCircleDetailActivity(mContext, Long.parseLong(dynamicBean.getMLetter().getId()));
                    }
                });

        if (TextUtils.isEmpty(path)) {
            return;
        }
        Glide.with(mContext)
                .load(path)
                .placeholder(R.drawable.shape_default_image)
                .error(R.drawable.shape_default_image)
                .into(imageView);

    }
}

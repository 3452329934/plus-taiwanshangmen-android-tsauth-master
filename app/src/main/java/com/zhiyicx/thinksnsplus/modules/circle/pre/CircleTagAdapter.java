package com.zhiyicx.thinksnsplus.modules.circle.pre;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.UserTagBean;
import com.zhiyicx.thinksnsplus.modules.edit_userinfo.UserInfoTagsAdapter;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/09/08/17:35
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CircleTagAdapter extends UserInfoTagsAdapter {

    public CircleTagAdapter(List<UserTagBean> datas, Context context) {
        super(datas, context);
    }

    public CircleTagAdapter(List<UserTagBean> datas, Context context, boolean isCircleRadus) {
        super(datas, context, isCircleRadus);
    }

    @Override
    public View getView(FlowLayout parent, int position, UserTagBean qaTopicBean) {
        TextView tv = new TextView(mContext);
        tv.setPadding(10, 5, 10, 5);
        if (mIsCircleRadus) {
            tv.setBackgroundResource(R.drawable.shape_default_radus_circle_gray);
        } else {
            tv.setBackgroundResource(R.drawable.item_react_bg_gray);
        }
        tv.setText(qaTopicBean.getTagName());
        return tv;
    }
}

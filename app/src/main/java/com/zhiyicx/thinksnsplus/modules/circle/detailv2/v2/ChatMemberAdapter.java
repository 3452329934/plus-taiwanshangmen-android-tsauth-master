package com.zhiyicx.thinksnsplus.modules.circle.detailv2.v2;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zhiyicx.baseproject.widget.UserAvatarView;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * @author Catherine
 * @describe 选中的好友的adapter
 * @date 2018/1/12
 * @contact email:648129313@qq.com
 */

public class ChatMemberAdapter extends CommonAdapter<CircleMembers> {

    public ChatMemberAdapter(Context context, List<CircleMembers> datas) {
        super(context, R.layout.item_circle_detail_chat_member, datas);
    }

    @Override
    protected void convert(ViewHolder holder, CircleMembers circleMembers, int position) {
        UserAvatarView ivUserPortrait = holder.getView(R.id.iv_user_portrait);
        ImageView ivMore = holder.getView(R.id.iv_more);
        if (circleMembers.getUser() == null) {
            ivMore.setVisibility(View.VISIBLE);
        } else {
            ImageUtils.loadCircleUserHeadPic(circleMembers.getUser(), ivUserPortrait);

        }
    }
}

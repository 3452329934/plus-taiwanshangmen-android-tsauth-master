package com.zhiyicx.thinksnsplus.modules.chat.private_letter.newconversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.modules.chat.location.tofriends.ToFriendsActivity;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendFragment;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/10/9:51
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class NewConversationActivity extends ToFriendsActivity {

    @Override
    protected NewConversationFragment getFragment() {
        return NewConversationFragment.newInstance(getIntent().getExtras());
    }

    public static void startNewConversationActivity(Context context, Letter letter) {
        Intent intent = new Intent(context, NewConversationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ChooseFriendFragment.LETTER, letter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

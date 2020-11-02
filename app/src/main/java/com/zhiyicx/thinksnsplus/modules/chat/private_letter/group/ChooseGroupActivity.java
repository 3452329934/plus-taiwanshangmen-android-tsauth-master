package com.zhiyicx.thinksnsplus.modules.chat.private_letter.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendFragment;
import com.zhiyicx.thinksnsplus.modules.home.message.messagegroup.MessageGroupActivity;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/10/9:09
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChooseGroupActivity extends MessageGroupActivity {

    @Override
    protected ChooseGroupListFragment getFragment() {
        return ChooseGroupListFragment.newInstance(getIntent().getExtras());
    }

    public static void startChooseGroupActivity(Context context, Letter letter) {
        Intent intent = new Intent(context, ChooseGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ChooseFriendFragment.LETTER, letter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

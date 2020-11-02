package com.zhiyicx.thinksnsplus.jpush;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;

/**
 * ThinkSNS Plus
 * Copyright (c) 2019 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jungle68
 * @describe
 * @date 2019/1/30
 * @contact master.jungle68@gmail.com
 */
public class My2HuaWeiPushReceiver extends PushReceiver {
    @Override
    public void onEvent(Context context, Event event, Bundle bundle) {
        super.onEvent(context, event, bundle);
    }

    @Override
    public void onPushState(Context context, boolean b) {
        super.onPushState(context, b);
    }
}

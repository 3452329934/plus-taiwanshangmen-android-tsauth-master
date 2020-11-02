package com.zhiyicx.thinksnsplus.jpush;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.huawei.hms.support.api.push.PushReceiver;
import com.hyphenate.chat.EMClient;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.log.LogUtils;

import static com.zhiyicx.baseproject.em.manager.util.TSEMConstants.ML_HUAWEI_APP_ID;

/**
 * ThinkSNS Plus
 * Copyright (c) 2019 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jungle68
 * @describe
 * @date 2019/1/30
 * @contact master.jungle68@gmail.com
 */
public class MyHuaWeiPushReceiver extends PushReceiver {
    @Override
    public void onToken(Context context, String token, Bundle bundle) {
        super.onToken(context, token, bundle);
        String manufacturer = Build.MANUFACTURER;
        if ((!TextUtils.isEmpty(manufacturer) && "HUAWEI".equals(manufacturer.toUpperCase())) || !TextUtils.isEmpty(DeviceUtils.getEMUI())) {
            EMClient.getInstance().sendHMSPushTokenToServer(ML_HUAWEI_APP_ID, token);
        }
    }

    @Override
    public boolean onPushMsg(Context context, byte[] bytes, Bundle bundle) {
        LogUtils.d("---------------------");
        LogUtils.d(bundle);
        return super.onPushMsg(context, bytes, bundle);
    }
}

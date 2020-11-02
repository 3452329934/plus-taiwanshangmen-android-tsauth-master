package com.zhiyicx.thinksnsplus.service.empush;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hyphenate.chat.EMClient;
import com.zhiyicx.common.utils.log.LogUtils;

/**
 * ThinkSNS Plus
 * Copyright (c) 2019 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jungle68
 * @describe
 * @date 2019/1/29
 * @contact master.jungle68@gmail.com
 */
public class EMFCMTokenRefreshService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMTokenRefreshService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        LogUtils.i(TAG, "onTokenRefresh: " + token);
        // Important, send the fcm token to the server
        EMClient.getInstance().sendFCMTokenToServer(token);
    }
}
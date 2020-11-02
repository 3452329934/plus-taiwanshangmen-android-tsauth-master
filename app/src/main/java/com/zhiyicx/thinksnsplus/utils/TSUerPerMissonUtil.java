package com.zhiyicx.thinksnsplus.utils;

import android.content.Context;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.config.BackgroundTaskRequestMethodConfig;
import com.zhiyicx.thinksnsplus.data.beans.BackgroundRequestTaskBean;
import com.zhiyicx.thinksnsplus.i.IUserPermissons;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskHandler;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;

import java.util.HashMap;
import java.util.Locale;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/12/10/10:05
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TSUerPerMissonUtil implements IUserPermissons {

    public static TSUerPerMissonUtil getInstance() {
        return PermissonSingleInstance.sPerMissonUtil;
    }

    private static class PermissonSingleInstance {
        static TSUerPerMissonUtil sPerMissonUtil = new TSUerPerMissonUtil();
    }


    private TSUerPerMissonUtil() {
    }

    @Override
    public boolean canDeleteFeed() {
        return AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(DELETEFEED);
    }

    @Override
    public boolean canDeleteComment() {
        return AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(DELETECOMMENT);
    }

    @Override
    public boolean canDisableUser() {
        return AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(DISABLEDUSER);
    }

    @Override
    public boolean canManageQATopic() {
        return AppApplication.getmCurrentLoginAuth() != null && AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(QA_TOPIC_MANAGE);
    }

    @Override
    public boolean canDeleteInfo() {
        return AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(DELETENEWS);
    }

    @Override
    public boolean canDeleteQuestion() {
        return AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(MANAGEQUESTIONS);
    }

    @Override
    public boolean canDeleteAnswer() {
        return AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(MANAGEANSWERS);
    }

    @Override
    public boolean hasAnyPerMission(String perMisson) {
        return AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(perMisson);
    }

    @Override
    public void deleteComment(Context context, Long commentId) {
        BackgroundRequestTaskBean task = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig
                .DELETE_V2);
        task.setPath(String.format(Locale.getDefault(), ApiConfig.APP_PATH_DELETE_COMMENT, commentId));
        BackgroundTaskManager.getInstance(context).addBackgroundRequestTask(task);
    }

    @Override
    public void diableUser(Context context, Long userId) {
        diableUser(context, userId, null);
    }

    @Override
    public void diableUser(Context context, Long userId, BackgroundTaskHandler.OnNetResponseCallBack callBack) {
        BackgroundRequestTaskBean task = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig
                .PUT);
        task.setPath(String.format(Locale.getDefault(), ApiConfig.APP_PATH_DIABLEUSER_FORMAT, userId));
        task.setMax_retry_count(1);
        HashMap<String, Object> hashMap = new HashMap<>(1);
        hashMap.put(BackgroundTaskHandler.NET_CALLBACK, callBack);
        task.setParams(hashMap);
        BackgroundTaskManager.getInstance(context).addBackgroundRequestTask(task);
    }

    public static abstract class TaskListener implements BackgroundTaskHandler.OnNetResponseCallBack {

        @Override
        public void onException(Throwable throwable) {
            onFailure(throwable.getMessage(), 500);
        }
    }
}

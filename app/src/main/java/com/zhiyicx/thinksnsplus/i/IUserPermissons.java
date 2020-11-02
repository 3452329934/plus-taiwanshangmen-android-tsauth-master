package com.zhiyicx.thinksnsplus.i;

import android.content.Context;

import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskHandler;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/12/10/9:51
 * @Email Jliuer@aliyun.com
 * @Description 用户权限
 */
public interface IUserPermissons {

    /**
     * 删除用户权限
     */
    String DELETEUSER = "admin:user:delete";

    /**
     * 删除动态权限
     */
    String DELETEFEED = "[feed] Delete Feed";

    /**
     * 删除资讯权限
     */
    String DELETENEWS = "[News] Delete News Post";

    /**
     * 管理回答权限
     */
    String MANAGEANSWERS = "[Q&A] Manage Answers";

    /**
     * 管理问题权限
     */
    String MANAGEQUESTIONS = "[Q&A] Manage Questions";

    /**
     * 删除任何评论权限
     */
    String DELETECOMMENT = "[comment] Delete Comment";

    /**
     * 删除猫咪权限
     */
    String DELETECAT = "[feed] Delete Cat";

    /**
     * 禁用用户权限
     */
    String DISABLEDUSER = "[user] Disabled User";

    /**
     * 推荐至类别权限
     */
    String PUSHTOCATEGORY = "[meow] Push To Category";
    /**
     * 话题管理权限
     * https://github.com/zhiyicx/plus-domo/blob/master/docs/theme.md
     */
    String QA_TOPIC_MANAGE = "[meow] Manage Theme";

    boolean canDeleteFeed();

    boolean canDeleteInfo();

    boolean canDeleteQuestion();

    boolean canDeleteAnswer();

    boolean canDeleteComment();

    boolean canDisableUser();

    boolean canManageQATopic();

    boolean hasAnyPerMission(String perMisson);

    void deleteComment(Context context, Long commentId);

    void diableUser(Context context, Long userId);

    void diableUser(Context context, Long userId, BackgroundTaskHandler.OnNetResponseCallBack callBack);

}

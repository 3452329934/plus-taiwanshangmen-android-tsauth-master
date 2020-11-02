package com.zhiyicx.thinksnsplus.data.source.repository.i;

import com.zhiyicx.thinksnsplus.data.beans.notify.UserNotifyMsgBean;

import rx.Observable;

/**
 * @Describe 通知相关
 * @Author Jungle68
 * @Date 2017/12/25
 * @Contact master.jungle68@gmail.com
 */
public interface INotificationRepository {


    String SYSTEMMSG = "system";
    String AT = "at";
    String COMMENT = "comment";
    String LIKE = "like";
    String FOLLOW = "follow";


    /**
     * 打赏动态
     */
    String REWARD_FEEDS = "reward:feeds";

    /**
     * 打赏资讯
     */
    String REWARD_NEWS = "reward:news";

    /**
     * 打赏用户
     */
    String REWARD_USER = "reward";

    /**
     * 打赏帖子
     */
    String GROUP_POST_REWARD = "group:post-reward";
    /**
     * 用户认证（注意区分通过驳回）
     */
    String USER_CERTIFICATION = "user-certification";
    String CERTIFICATION_REJECTED = "rejected";
    String CERTIFICATION_PASSED = "passed";
    String CERTIFICATION_ADMIN = "admin";
    /**
     * 加圈申请
     */
    String GROUP_JOIN = "group:join";

    /**
     * 圈子转让
     */
    String GROUP_TRANSFORM = "group:transform";

    /**
     * 回答被采纳
     */
    String QA_ANSWER_ADOPTION = "qa:answer-adoption";
    String QA_ANSWER_ADOPTION_1 = "question:answer";

    /**
     * 问题邀请回答
     */
    String QA_INVITATION = "qa:invitation";

    /**
     * 回答被大赏
     */
    String QA_ANSWER_REWARD = "qa:reward";

    /**
     * 动态评论置顶审核
     */
    String PINNED_FEED_COMMENT = "pinned:feed/comment";
    /**动态评论被删除*/
    String DELETE_FEED_COMMENT = "delete:feed/comment";

    /**
     * 动态置顶审核
     */
    String PINNED_FEED = "pinned:feeds";

    /**
     * 资讯评论置顶审核
     */
    String PINNED_NEWS_COMMENT = "pinned:news/comment";

    /**
     * 帖子评论置顶审核
     */
    String GROUP_COMMENT_PINNED = "group:comment-pinned";
    /**
     * 申请你的帖子下的评论置顶
     */
    String GROUP_SEND_COMMENT_PINNED = "group:send-comment-pinned";

    /**
     * 你的帖子《%s》已被管理员置顶
     */
    String GROUP_PINNED_ADMIN = "group:pinned-admin";

    /**
     * 帖子置顶审核
     */
    String GROUP_POST_PINNED = "group:post-pinned";
    /**
     * 有人购买了你的什么
     */
    String PURCHASE = "purchase";

    /**
     * 申请的提现
     */
    String USER_CASH = "user-cash";
    /**
     * 积分提现
     */
    String USER_CURRENCY_CASH = "user-currency:cash";
    /**
     * 举报的内容平台已处理
     */
    String REPORT = "report";
    /**
     * 举报的二级分类
     */
    String REPORT_USER = "users";
    String REPORT_TOPIC = "feed_topics";
    String REPORT_COMMENT = "comments";
    String REPORT_QUESTION = "questions";
    String REPORT_FEED = "feeds";
    String REPORT_NEWS = "news";
    String REPORT_ANSWER = "answers";
    String REPORT_POSTS = "posts";
    String REPORT_GROUPS = "groups";
    String REPORT_GROUPS_POST = "group-posts";


    /**
     * 申请创建的专题通过
     */
    String QA_TOPIC_ACCEPT= "qa:question-topic:accept";
    /**
     * 申请创建的专题被驳回
     */
    String QA_TOPIC_REJECT= "qa:question-topic:reject";
    /**
     * 帖子有新的举报
     */
    String GROUP_REPORT_POST= "group:report-post";
    /**
     * xx在我圈子下举报了xx的评论
     */
    String REPORT_GROUPS_POST_COMMENT = "group:report-comment";
    /**
     *圈子有新的举报
     */
    String GROUP_REPORT= "group:report";

    /**
     * 系统管理员将你设置成%s的成员
     */
    String GROUP_MEMBER= "group:menbers";
    /**
     * 系统管理员将你设置成%s的%s
     */
    String GROUP_MENBER= "group:menbers";
    /**
     * 圈子成员退出圈子
     */
    String GROUP_EXIT= "group:exit";
    /**资讯投稿被驳回*/
    String NEW_REJECT= "news:reject";

    /**
     * 问题加精通过
     */
    String QA_EXCELLENT_ACCEPT= "qa:question-excellent:accept";

    /**
     * 问题加精被拒
     */
    String QA_EXCELLENT_REJECT= "qa:question-excellent:reject";



    /**
     * 获取通知列表
     * @param type
     * @param page
     * @return
     */
    Observable<UserNotifyMsgBean> getSystemMsg(String type, int page);

    /**
     * 标记所有通知阅读
     *
     * @return
     */
    Observable<Object> makeNotificationAllReaded();

    Observable<Object> makeNotificationReaded(String notificationType);
}

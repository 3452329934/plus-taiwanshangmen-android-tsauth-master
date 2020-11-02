package com.zhiyicx.thinksnsplus.modules.home.message;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;
import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.utils.ActivityHandler;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.imsdk.entity.AuthData;
import com.zhiyicx.imsdk.entity.Conversation;
import com.zhiyicx.imsdk.entity.Message;
import com.zhiyicx.imsdk.manage.ZBIMClient;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.config.JpushMessageTypeConfig;
import com.zhiyicx.thinksnsplus.data.beans.JpushMessageBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UnReadNotificaitonBean;
import com.zhiyicx.thinksnsplus.data.beans.UnreadCountBean;
import com.zhiyicx.thinksnsplus.data.beans.UserFollowerCountBean;
import com.zhiyicx.thinksnsplus.data.source.repository.MessageRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;
import com.zhiyicx.thinksnsplus.modules.chat.call.TSEMHyphenate;
import com.zhiyicx.thinksnsplus.modules.home.HomeActivity;
import com.zhiyicx.thinksnsplus.modules.home.message.container.MessageContainerFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/13
 * @Contact master.jungle68@gmail.com
 */
@FragmentScoped
public class MessagePresenter extends AppBasePresenter<MessageContract.View> implements MessageContract.Presenter {
    private static final int MAX_USER_NUMS_COMMENT = 2;
    private static final int MAX_USER_NUMS_DIGG = 3;

    @Inject
    MessageRepository mMessageRepository;

    @Inject
    UserInfoRepository mUserInfoRepository;

    /**
     * @ 我的消息
     */
    private MessageItemBean mItemBeanAtMessage;

    /**
     * 系统消息
     */
    private MessageItemBean mItemBeanSystemMessage;

    /**
     * 评论的
     */
    private MessageItemBean mItemBeanComment;
    /**
     * 点赞的
     */
    private MessageItemBean mItemBeanDigg;
    /**
     * 评论置顶的
     */
    private MessageItemBean mItemBeanReview;

    /**
     * 通知的小红点
     */
    private boolean mNotificaitonRedDotIsShow;

    /**
     * 用户未读消息
     */
    private UnReadNotificaitonBean mUnReadNotificaitonBean;
    private UserFollowerCountBean mUserFollowerCountBean;

    private Subscription mUnreadNotiSub;

    @Inject
    public MessagePresenter(MessageContract.View rootView) {
        super(rootView);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
    }

    /**
     * 没有加载更多，一次全部取出
     *
     * @param isLoadMore 加载状态
     */
    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        if (mAuthRepository.getAuthBean() == null) {
            mRootView.onCacheResponseSuccess(new ArrayList<>(), isLoadMore);
        } else {
            initHeaderItemData();
            // 处理本地通知数据
            mRootView.updateLikeItemData(mItemBeanDigg);
        }
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<MessageItemBean> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public MessageItemBean updateCommnetItemData() {
        return mItemBeanComment;
    }

    @Override
    public MessageItemBean updateLikeItemData() {
        return mItemBeanDigg;
    }

    @Override
    public MessageItemBean updateReviewItemData() {
        return mItemBeanReview;
    }

    @Override
    public MessageItemBean updateSystemMsgItemData() {
        return mItemBeanSystemMessage;
    }

    @Override
    public MessageItemBean updateAtMsgItemData() {
        return mItemBeanAtMessage;
    }

    /**
     * 刷新是否显示底部红点
     * 刷新当条item 的未读数
     */
    @Override
    public void refreshConversationReadMessage() {
        Subscription represhSu = Observable.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(s -> {
                    checkBottomMessageTip();
                    return mRootView.getListDatas();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> mRootView.refreshData(), Throwable::printStackTrace);
        addSubscrebe(represhSu);
    }

    /**
     * 删除对话信息
     */
    @Override
    public void deletConversation(int position) {
        // 改为环信的删除
        MessageItemBeanV2 messageItemBeanV2 = mRootView.getRealMessageList().get(position);
        Subscription subscription = Observable.just(messageItemBeanV2)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemBeanV2 -> {
                    LogUtils.d("Cathy", "deletConversation");
                    mRootView.getRealMessageList().remove(itemBeanV2);
                    mRootView.refreshData();
                    checkBottomMessageTip();
                    EMClient.getInstance().chatManager().deleteConversation(itemBeanV2.getEmKey(), true);
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getSingleConversation(int cid) {
        Subscription subscribe = mMessageRepository.getSingleConversation(cid)
                .observeOn(Schedulers.io())
                .map(data -> {
                    if (data == null || data.getConversation() == null) {
                        return false;
                    }
                    int size = mRootView.getListDatas().size();
                    for (int i = 0; i < size; i++) {
                        if (mRootView.getListDatas().get(i).getConversation().getCid() == cid) {
                            return false;
                        }
                    }
                    if (mRootView.getListDatas().size() == 0) {
                        mRootView.getListDatas().add(data);
                    } else {
                        // 置顶新消息
                        mRootView.getListDatas().add(0, data);
                    }
                    return true;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<Boolean>() {
                    @Override
                    protected void onSuccess(Boolean data) {
                        if (data) {
                            mRootView.refreshData();
                        }
                    }

                });
        addSubscrebe(subscribe);
    }

    /**
     * 检测未读消息数
     */
    @Override
    public void checkUnreadNotification() {
        mMessageRepository.ckeckUnreadNotification()
                .subscribe(new BaseSubscribeForV2<Void>() {
                    @Override
                    protected void onSuccess(Void data) {
                        LogUtils.i("addBtnAnimation notification", data);
                    }
                });
    }

    @Override
    public UnReadNotificaitonBean getUnreadNotiBean() {
        return mUnReadNotificaitonBean;
    }

    @Override
    public UserFollowerCountBean getUserFollowerCountBean() {
        return mUserFollowerCountBean;
    }

    /**
     * 未读数获取到
     *
     * @param unreadNumStr unread  notificaiton nums
     */
    @Subscriber(tag = EventBusTagConfig.EVENT_UNREAD_NOTIFICATION_LIMIT)
    private void onCheckUnreadNotifyRecieved(String unreadNumStr) {
        int unreadNum = 0;
        try {
            unreadNum = Integer.parseInt(unreadNumStr);
        } catch (Exception igonred) {
        }
        mNotificaitonRedDotIsShow = unreadNum > 0;
        checkBottomMessageTip();
    }


    @Subscriber(tag = EventBusTagConfig.EVENT_IM_SET_NOTIFICATION_TIP_VISABLE)
    private void updateNotificaitonReddot(boolean isHide) {
        mNotificaitonRedDotIsShow = false;
        checkBottomMessageTip();
    }

    /*******************************************
     * IM 相关
     *********************************************/

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGEACKRECEIVED)
    private void onMessageACKReceived(Message message) {
        if (!(ActivityHandler.getInstance().currentActivity() instanceof HomeActivity)) {
        }
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_AUTHSUCESSED)
    private void onAuthSuccessed(AuthData authData) {
//        mRootView.showSnackSuccessMessage("IM 聊天加载成功");
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONCONNECTED)
    private void onConnected(String content) {
        mRootView.hideStickyMessage();
//        getAllConversationV2(false);
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONDISCONNECT)
    private void onDisconnect(int code, String reason) {
        mRootView.showStickyMessage(reason);
        if (code == EMError.USER_REMOVED) {
            // 显示帐号已经被移除
        } else if (code == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            // 显示帐号在其他设备登录
        } else {
            if (NetUtils.hasNetwork(mContext)) {
                //连接不到聊天服务器
            } else {
                //当前网络不可用，请检查网络设置
            }
        }
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONERROR)
    private void onError(Exception error) {
        mRootView.showMessage(error.toString());
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGETIMEOUT)
    private void onMessaeTimeout(Message message) {

    }

    /**
     * 新对话创建回调
     */
    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONCONVERSATIONCRATED)
    private void onConversationCreated(MessageItemBeanV2 messageItemBean) {

        mRootView.getRealMessageList().add(0, messageItemBean);
        mRootView.refreshData();

    }


    /**
     * 推送相关
     */
    @Subscriber(tag = EventBusTagConfig.EVENT_JPUSH_RECIEVED_MESSAGE_UPDATE_MESSAGE_LIST)
    private void onJpushMessageRecieved(JpushMessageBean jpushMessageBean) {
        if (jpushMessageBean.getType() == null) {
            return;
        }
        switch (jpushMessageBean.getType()) {
            // 推送携带的消息  {"seq":36,"msg_type":0,"cid":1,"mid":338248648800337924,"type":"im",
            // "uid":20} IM 消息通过IM接口 同步，故不需要对 推送消息做处理
            case JpushMessageTypeConfig.JPUSH_MESSAGE_TYPE_IM:
//                handleIMPush(jpushMessageBean);
                break;
            case JpushMessageTypeConfig.JPUSH_MESSAGE_TYPE_FEED_CONTENT:

            default:
                // 服务器同步未读评论和点赞消息
                handleFlushMessage();
                checkUnreadNotification();
                break;


        }
    }

    /**
     * 处理聊天推送
     */
    private void handleIMPush(JpushMessageBean jpushMessageBean) {
        String extras = jpushMessageBean.getExtras();
        try {
            JSONObject jsonObject = new JSONObject(extras);
            Message message = new Message();
            message.setCid(jsonObject.getInt("cid"));
            message.setSeq(jsonObject.getInt("seq"));
            ZBIMClient.getInstance().syncAsc(message.getCid(), message.getSeq() - 1, message.getSeq() + 1, (int) System.currentTimeMillis());
            // 获取推送的信息
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理 获取用户收到的最新消息
     */
    @Override
    public void handleFlushMessage() {
        if (mUnreadNotiSub != null && !mUnreadNotiSub.isUnsubscribed()) {
            mUnreadNotiSub.unsubscribe();
        }

        mUnreadNotiSub = mMessageRepository.getUnreadNotificationData()
                .observeOn(Schedulers.io())
                .flatMap((Func1<UnReadNotificaitonBean, Observable<UnReadNotificaitonBean>>) unReadNotificaitonBean -> mUserInfoRepository.getUserAppendFollowerCount()
                        .map(userFollowerCountBean -> {
                            if (userFollowerCountBean != null && userFollowerCountBean.getUser() != null) {
                                /**
                                 * 设置未读数
                                 */
                                int commentCount = unReadNotificaitonBean.getComment() != null ? unReadNotificaitonBean.getComment().getBadge() : 0;
                                int likeCount = unReadNotificaitonBean.getLike() != null ? unReadNotificaitonBean.getLike().getBadge() : 0;
                                int systemCount = unReadNotificaitonBean.getSystem() != null ? unReadNotificaitonBean.getSystem().getBadge() : 0;
                                int atCount = unReadNotificaitonBean.getAt() != null ? unReadNotificaitonBean.getAt().getBadge() : 0;
                                int followCount = unReadNotificaitonBean.getFollow() != null ? unReadNotificaitonBean.getFollow().getBadge() : 0;
                                mItemBeanComment.setUnReadMessageNums(commentCount);
                                mItemBeanDigg.setUnReadMessageNums(likeCount);
                                mItemBeanSystemMessage.setUnReadMessageNums(systemCount);
                                mItemBeanAtMessage.setUnReadMessageNums(atCount);

                                userFollowerCountBean.getUser().setCommented(commentCount);
                                userFollowerCountBean.getUser().setLiked(likeCount);
                                userFollowerCountBean.getUser().setSystem(systemCount);
                                userFollowerCountBean.getUser().setAt(atCount);
                                userFollowerCountBean.getUser().setFollowing(followCount);
                                mItemBeanComment.setUnReadMessageNums(commentCount);
                                mItemBeanDigg.setUnReadMessageNums(likeCount);
                                mItemBeanSystemMessage.setUnReadMessageNums(systemCount);
                                mItemBeanAtMessage.setUnReadMessageNums(atCount);
                                EventBus.getDefault().post(userFollowerCountBean, EventBusTagConfig.EVENT_IM_SET_MINE_FANS_TIP_VISABLE);


                                int pinnedNums;
                                pinnedNums = userFollowerCountBean.getUser().getFeedCommentPinned()
                                        + userFollowerCountBean.getUser().getNewsCommentPinned()
                                        + userFollowerCountBean.getUser().getGroupJoinPinned()
                                        + userFollowerCountBean.getUser().getPostCommentPinned()
                                        + userFollowerCountBean.getUser().getPostPinned();
                                TSEMHyphenate.getInstance().setMsgUnreadCount(userFollowerCountBean.getUser().getFollowing()
                                        + userFollowerCountBean.getUser().getMutual() + commentCount + likeCount + systemCount + pinnedNums);
                                mItemBeanReview.setUnReadMessageNums(pinnedNums);
                                mUserFollowerCountBean = userFollowerCountBean;
                            }
                            return unReadNotificaitonBean;
                        }))
                .map(data -> {
                    mUnReadNotificaitonBean = data;
                    if (data.getAt() == null && data.getComment() == null && data.getLike() == null && data.getSystem() == null) {
                        return false;
                    }

                    /**
                     * 设置时间
                     */
                    mItemBeanComment.getConversation().setLast_message_time(data.getComment().getBadge() <= 0 ? 0 :
                            TimeUtils.utc2LocalLong(data.getComment().getLast_created_at()));
                    mItemBeanDigg.getConversation().setLast_message_time(data.getLike().getBadge() <= 0 ? 0 :
                            TimeUtils.utc2LocalLong(data.getLike().getLast_created_at()));

                    mItemBeanAtMessage.getConversation().setLast_message_time(mUnReadNotificaitonBean.getAt().getBadge() <= 0 ? 0 :
                            TimeUtils.utc2LocalLong(data.getAt().getLast_created_at()));

//                    String feedTime = data.getPinneds() != null && data.getPinneds().getFeeds() != null ? data.getPinneds().getFeeds().getTime() :
//                            null;
//                    String newTime = data.getPinneds() != null && data.getPinneds().getNews() != null ? data.getPinneds().getNews().getTime() : null;
//                    String groupPostsTime = data.getPinneds() != null && data.getPinneds().getGroupPosts() != null ? data.getPinneds().getGroupPosts()
//                            .getTime() : null;
//                    String groupCommentsTime = data.getPinneds() != null && data.getPinneds().getGroupComments() != null ? data.getPinneds()
//                            .getGroupComments().getTime
//                                    () : null;
//                    long lastTime = 0;
//                    lastTime = getLastTime(feedTime, lastTime);
//                    lastTime = getLastTime(newTime, lastTime);
//                    lastTime = getLastTime(groupPostsTime, lastTime);
//                    lastTime = getLastTime(groupCommentsTime, lastTime);
//
//                    mItemBeanReview.getConversation().setLast_message_time(lastTime);

                    /**
                     * 设置提示内容
                     * mContext.getString(R.string.has_no_body)
                     + mContext.getString(R.string.comment_me)
                     */
                    String atTip = getAtItemTipStr(data.getAt() == null ? null : data.getAt().getPreview_users_names(), MAX_USER_NUMS_COMMENT);
                    if (!TextUtils.isEmpty(atTip) && data.getAt().getBadge() > 0) {
                        if (data.getAt() != null && data.getAt().getPreview_users_names().size() > MAX_USER_NUMS_COMMENT) {
                            atTip += mContext.getString(R.string.at_me_more);
                        } else {
                            atTip += mContext.getString(R.string.at_me);
                        }
                    } else {
                        atTip = mContext.getString(R.string.has_no_body)
                                + mContext.getString(R.string.at_me);
                    }
                    mItemBeanAtMessage.getConversation().getLast_message().setTxt(
                            atTip);

                    String commentTip = getAtItemTipStr(data.getComment().getPreview_users_names(), MAX_USER_NUMS_COMMENT);
                    if (!TextUtils.isEmpty(commentTip) && data.getComment().getBadge() > 0) {
                        if (data.getComment() != null && data.getComment().getPreview_users_names().size() > MAX_USER_NUMS_COMMENT) {
                            commentTip += mContext.getString(R.string.comment_me_more);
                        } else {
                            commentTip += mContext.getString(R.string.comment_me);
                        }
                    } else {
                        commentTip = mContext.getString(R.string.has_no_body)
                                + mContext.getString(R.string.comment_me);
                    }
                    mItemBeanComment.getConversation().getLast_message().setTxt(
                            commentTip);

                    String diggTip = getAtItemTipStr(data.getLike().getPreview_users_names(), MAX_USER_NUMS_DIGG);
                    if (!TextUtils.isEmpty(diggTip) && data.getLike().getBadge() > 0) {
                        if (data.getLike() != null && data.getLike().getPreview_users_names().size() > MAX_USER_NUMS_DIGG) {
                            diggTip += mContext.getString(R.string.like_me_more);
                        } else {
                            diggTip += mContext.getString(R.string.like_me);
                        }
                    } else {
                        diggTip = mContext.getString(R.string.has_no_body)
                                + mContext.getString(R.string.like_me);
                    }
                    mItemBeanDigg.getConversation().getLast_message().setTxt(
                            diggTip);

                    String reviewTip;
                    if (mItemBeanReview.getUnReadMessageNums() > 0) {
                        reviewTip = mContext.getString(R.string.new_apply_data);
                    } else {
                        reviewTip = mContext.getString(R.string.no_apply_data);
                        mItemBeanReview.getConversation().setLast_message_time(0);

                    }
                    mItemBeanReview.getConversation().getLast_message().setTxt(
                            reviewTip);
                    if (data.getSystem() != null && data.getSystem().getBadge() > 0
                            && data.getSystem().getFirst() != null && data.getSystem().getFirst().getData() != null) {
                        mItemBeanSystemMessage.getConversation().getLast_message().setTxt(data.getSystem().getFirst().getData().getContent());
                        mItemBeanSystemMessage.getConversation().getLast_message().setCreate_time(TimeUtils.utc2LocalLong(data.getSystem()
                                .getFirst().getCreated_at()));
                        mItemBeanSystemMessage.getConversation().setLast_message_time(
                                TimeUtils.utc2LocalLong(data.getSystem().getFirst().getCreated_at()));
                    }else{
                        mItemBeanSystemMessage.getConversation().setLast_message_time(0);
                        mItemBeanSystemMessage.getConversation().getLast_message().setTxt(mContext.getString(R.string.system_notification_null));
                    }
                    // 更新我的消息提示
                    checkBottomMessageTip();
                    return true;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<Boolean>() {
                    @Override
                    protected void onSuccess(Boolean result) {
                        if (result) {
                            mRootView.updateLikeItemData(mItemBeanDigg);
                        }
                    }
                });
        addSubscrebe(mUnreadNotiSub);
    }

    private long getLastTime(String newTime, long lastTime) {
        if (newTime != null && TimeUtils.utc2LocalLong(newTime) > lastTime) {
            lastTime = TimeUtils.utc2LocalLong(newTime);
        }
        return lastTime;
    }

    /**
     * 获取用户文字显示  张三、李四评论了我
     */
    private String getItemTipStr(List<UnreadCountBean> commentsNoti, int maxNum) {
        if (commentsNoti == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String dot = mContext.getString(R.string.str_pingyin_dot);
        for (int i = 0; i < commentsNoti.size(); i++) {
            if (i < maxNum) {
                try {
                    if (stringBuilder.toString().contains(commentsNoti.get(i).getUser().getName())) {
                        maxNum++;
                    } else {
                        stringBuilder.append(commentsNoti.get(i).getUser().getName());
                        stringBuilder.append(dot);
                    }
                    // 服务器脏数据导致用户信息为空
                } catch (NullPointerException ignored) {
                }
            } else {
                break;
            }
        }
        String tip = stringBuilder.toString();
        if (tip.endsWith(dot)) {
            tip = tip.substring(0, tip.length() - 1);
        }
        return tip;
    }


    private String getAtItemTipStr(List<String> atsNoti, int maxNum) {
        if (atsNoti == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String dot = mContext.getString(R.string.str_pingyin_dot);
        for (int i = 0; i < atsNoti.size(); i++) {
            if (i < maxNum) {
                try {
                    if (stringBuilder.toString().contains(atsNoti.get(i))) {
                        maxNum++;
                    } else {
                        stringBuilder.append(atsNoti.get(i));
                        stringBuilder.append(dot);
                    }
                    // 服务器脏数据导致用户信息为空
                } catch (NullPointerException ignored) {
                }
            } else {
                break;
            }
        }
        String tip = stringBuilder.toString();
        if (tip.endsWith(dot)) {
            tip = tip.substring(0, tip.length() - 1);
        }
        return tip;
    }


    /**
     * 初始化 header 数据
     */
    private void initHeaderItemData() {

        mItemBeanAtMessage = new MessageItemBean();
        Conversation atMessage = new Conversation();
        Message atMsg = new Message();
        atMessage.setLast_message(atMsg);
        mItemBeanAtMessage.setConversation(atMessage);
        mItemBeanAtMessage.getConversation().getLast_message().setTxt(mContext.getString(R.string.has_no_body)
                + mContext.getString(R.string.at_me));

        mItemBeanSystemMessage = new MessageItemBean();
        Conversation systemMessage = new Conversation();
        Message systemMsg = new Message();
        systemMessage.setLast_message(systemMsg);
        mItemBeanSystemMessage.setConversation(systemMessage);
        mItemBeanSystemMessage.getConversation().getLast_message().setTxt(mContext.getString(R.string.system_notification_null));

        mItemBeanComment = new MessageItemBean();
        Conversation commentMessage = new Conversation();
        Message message = new Message();
        commentMessage.setLast_message(message);
        mItemBeanComment.setConversation(commentMessage);
        mItemBeanComment.getConversation().getLast_message().setTxt(mContext.getString(R.string.has_no_body)
                + mContext.getString(R.string.comment_me));

        mItemBeanDigg = new MessageItemBean();
        Conversation diggConveration = new Conversation();
        Message diggmessage = new Message();
        diggConveration.setLast_message(diggmessage);
        mItemBeanDigg.setConversation(diggConveration);
        mItemBeanDigg.getConversation().getLast_message().setTxt(mContext.getString(R.string.has_no_body)
                + mContext.getString(R.string.like_me));

        mItemBeanReview = new MessageItemBean();
        Conversation reviewConveration = new Conversation();
        Message reviewmessage = new Message();
        reviewConveration.setLast_message(reviewmessage);
        mItemBeanReview.setConversation(reviewConveration);
        mItemBeanReview.getConversation().getLast_message().setTxt(mContext.getString(R.string.no_apply_data));
    }


    /**
     * 检测底部小红点是否需要显示
     */
    private void checkBottomMessageTip() {
        Subscription subscribe = Observable.just(true)
                .subscribeOn(Schedulers.io())
                .map(aBoolean -> {
                    // 是否显示底部红点
                    boolean isShowMessgeTip;

                    boolean hasAtMsg = mItemBeanAtMessage != null && mItemBeanAtMessage.getUnReadMessageNums() != 0;
                    boolean hasSystemMsg = mItemBeanSystemMessage != null && mItemBeanSystemMessage.getUnReadMessageNums() != 0;
                    boolean hasDigMsg = mItemBeanDigg != null && mItemBeanDigg.getUnReadMessageNums() != 0;
                    boolean hasCommentMsg = mItemBeanComment != null && mItemBeanComment.getUnReadMessageNums() != 0;
                    boolean hasReviewMsg = mItemBeanReview != null && mItemBeanReview.getUnReadMessageNums() != 0;

                    isShowMessgeTip = hasAtMsg || hasSystemMsg || hasDigMsg || hasCommentMsg || hasReviewMsg;

                    mNotificaitonRedDotIsShow = TSEMHyphenate.getInstance().getUnreadMsgCount() > 0;

                    return isShowMessgeTip;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isShowMessgeTip -> {
                    Fragment containerFragment = mRootView.getCureenFragment().getParentFragment();
                    if (containerFragment instanceof MessageContainerFragment) {
                        ((MessageContainerFragment) containerFragment).setNewMessageNoticeState(isShowMessgeTip, 0);
                        ((MessageContainerFragment) containerFragment).setNewMessageNoticeState(mNotificaitonRedDotIsShow, 1);
                    }
                    boolean messageContainerRedDotIsShow = isShowMessgeTip || mNotificaitonRedDotIsShow;
                    EventBus.getDefault().post(messageContainerRedDotIsShow, EventBusTagConfig.EVENT_IM_SET_MESSAGE_TIP_VISABLE);

                }, Throwable::printStackTrace);
        addSubscrebe(subscribe);


    }
}
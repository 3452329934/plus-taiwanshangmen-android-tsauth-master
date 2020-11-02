package com.zhiyicx.thinksnsplus.modules.home.message.notifacationlist;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.notify.UserNotifyMsgBean;
import com.zhiyicx.thinksnsplus.data.source.repository.MessageRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.NotificationRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.i.INotificationRepository;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/31
 * @contact email:648129313@qq.com
 */
@FragmentScoped
public class NotificationPresenter extends AppBasePresenter<NotificationContract.View>
        implements NotificationContract.Presenter {


    @Inject
    MessageRepository mMessageRepository;
    @Inject
    NotificationRepository mNotificationRepository;

    private Subscription subscribe;

    @Inject
    public NotificationPresenter(NotificationContract.View rootView) {
        super(rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
        subscribe = mNotificationRepository.makeNotificationReaded(INotificationRepository.SYSTEMMSG)
                .flatMap(o -> {
                    readNotification();
                    return mNotificationRepository.getSystemMsg(INotificationRepository.SYSTEMMSG,maxId.intValue());
                })
                .subscribe(new BaseSubscribeForV2<UserNotifyMsgBean>() {
                    @Override
                    protected void onSuccess(UserNotifyMsgBean data) {
                        mRootView.onNetResponseSuccess(data.getData(), isLoadMore);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.onResponseError(throwable, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.onResponseError(null, isLoadMore);
                    }
                });
        addSubscrebe(subscribe);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(null, isLoadMore);

    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<UserNotifyMsgBean.DataBeanX> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public void readNotification() {

        EventBus.getDefault().post(true, EventBusTagConfig.EVENT_IM_SET_NOTIFICATION_TIP_VISABLE);

    }
}

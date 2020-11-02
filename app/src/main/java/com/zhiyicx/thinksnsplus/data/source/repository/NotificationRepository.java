package com.zhiyicx.thinksnsplus.data.source.repository;

import com.zhiyicx.thinksnsplus.data.beans.notify.UserNotifyMsgBean;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.remote.UserInfoClient;
import com.zhiyicx.thinksnsplus.data.source.repository.i.INotificationRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Catherine
 * @describe 通知相关
 * @date 2017/8/31
 * @contact email:648129313@qq.com
 */

public class NotificationRepository implements INotificationRepository {

    protected UserInfoClient mUserInfoClient;

    @Inject
    public NotificationRepository(ServiceManager serviceManager) {
        mUserInfoClient = serviceManager.getUserInfoClient();
    }

    @Override
    public Observable<UserNotifyMsgBean> getSystemMsg(String type, int page) {
        return mUserInfoClient.getNotificationList(type, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> makeNotificationAllReaded() {
        return null;
    }

    @Override
    public Observable<Object> makeNotificationReaded(String notificationType) {
        return mUserInfoClient.makeNotificationReaded(notificationType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

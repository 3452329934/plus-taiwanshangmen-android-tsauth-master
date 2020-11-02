package com.zhiyicx.thinksnsplus.modules.home.message.notifacationlist;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.notify.UserNotifyMsgBean;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/31
 * @contact email:648129313@qq.com
 */

public interface NotificationContract {

    interface View extends ITSListView<UserNotifyMsgBean.DataBeanX, Presenter> {

    }

    interface Presenter extends ITSListPresenter<UserNotifyMsgBean.DataBeanX> {
        void readNotification();
    }

}

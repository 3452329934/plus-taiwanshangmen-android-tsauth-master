package com.zhiyicx.thinksnsplus.modules.chat.location.send;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.ChatLocationBean;

/**
 * @Author Jliuer
 * @Date 2018/06/12/15:28
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface SendLocationContract {

    interface View extends ITSListView<ChatLocationBean, Presenter> {

    }

    interface Presenter extends ITSListPresenter<ChatLocationBean> {

    }

    interface Repository {

    }
}

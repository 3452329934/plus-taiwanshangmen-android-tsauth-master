package com.zhiyicx.thinksnsplus.modules.home.message.messagegroup;

import com.hyphenate.chat.EMGroup;
import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBeanForGroupList;

/**
 * @Author Jliuer
 * @Date 2018/05/03/15:41
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface MessageGroupContract {

    interface View extends ITSListView<ChatGroupBeanForGroupList,Presenter>{
        String getsearchKeyWord();
        void checkGroupExist(ChatGroupBeanForGroupList groupBean, EMGroup data);
    }

    interface Presenter extends ITSListPresenter<ChatGroupBeanForGroupList>{

        void checkGroupExist(ChatGroupBeanForGroupList groupBean);
    }
}

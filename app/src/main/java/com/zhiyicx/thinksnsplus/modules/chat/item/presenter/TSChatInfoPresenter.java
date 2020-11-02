package com.zhiyicx.thinksnsplus.modules.chat.item.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.presenter.EaseChatTextPresenter;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.thinksnsplus.modules.chat.item.ChatRowFeed;
import com.zhiyicx.thinksnsplus.modules.chat.item.ChatRowInfo;
import com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsActivity;

/**
 * @author Jliuer
 * @Date 18/02/06 10:08
 * @Email Jliuer@aliyun.com
 * @Description 动态私信
 */
public class TSChatInfoPresenter extends EaseChatTextPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        return new ChatRowInfo(cxt, message, position, adapter, userInfoBean);
    }
}

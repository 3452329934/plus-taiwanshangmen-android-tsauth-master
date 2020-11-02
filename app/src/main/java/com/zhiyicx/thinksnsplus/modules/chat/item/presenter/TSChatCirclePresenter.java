package com.zhiyicx.thinksnsplus.modules.chat.item.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.presenter.EaseChatTextPresenter;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.thinksnsplus.modules.chat.item.ChatRowCircle;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailActivity;

/**
 * @author Jliuer
 * @Date 18/02/06 10:08
 * @Email Jliuer@aliyun.com
 * @Description 圈子私信
 */
public class TSChatCirclePresenter extends EaseChatTextPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        return new ChatRowCircle(cxt, message, position, adapter, userInfoBean);
    }

    public TSChatCirclePresenter(OnCircleClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if (mOnClickListener != null) {
            Long id = Long.parseLong(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_ID, "-1"));
            mOnClickListener.onCircleClick(id);
        }
    }

    OnCircleClickListener mOnClickListener;

    public interface OnCircleClickListener {
        void onCircleClick(Long id);
    }
}

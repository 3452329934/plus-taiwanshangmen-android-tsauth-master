package com.zhiyicx.thinksnsplus.modules.chat.item.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.presenter.EaseChatTextPresenter;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.thinksnsplus.modules.chat.item.ChatRowPost;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailActivity;

/**
 * @author Jliuer
 * @Date 18/02/06 10:08
 * @Email Jliuer@aliyun.com
 * @Description 动态私信
 */
public class TSChatPostPresenter extends EaseChatTextPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        return new ChatRowPost(cxt, message, position, adapter, userInfoBean);
    }

    public TSChatPostPresenter(OnPostClickListener onPostClickListener) {
        mOnPostClickListener = onPostClickListener;
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if (mOnPostClickListener != null) {
            Long postId = Long.parseLong(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_ID, "-1"));
            Long circleId = Long.parseLong(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_POST_CIRCLE_ID, "-1"));
            mOnPostClickListener.onPostClick(circleId,postId);
        }
    }

    OnPostClickListener mOnPostClickListener;

    public interface OnPostClickListener {
        void onPostClick(Long circleId,Long postId);
    }
}

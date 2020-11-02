package com.zhiyicx.thinksnsplus.modules.chat.item.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.presenter.EaseChatTextPresenter;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.thinksnsplus.modules.chat.item.ChatRowFeed;
import com.zhiyicx.thinksnsplus.modules.chat.item.ChatRowQuestion;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity;

/**
 * @author Jliuer
 * @Date 18/02/06 10:08
 * @Email Jliuer@aliyun.com
 * @Description 问题私信
 */
public class TSChatQuestionPresenter extends EaseChatTextPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        return new ChatRowQuestion(cxt, message, position, adapter, userInfoBean);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        Long id = Long.parseLong(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_ID, "-1"));
        QuestionDetailActivity.startQuestionDetailActivity(getContext(),id);
    }
}

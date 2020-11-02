package com.zhiyicx.thinksnsplus.modules.chat.item;

import android.content.Context;
import android.text.Spannable;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.thinksnsplus.R;


/**
 * @author Jliuer
 * @Date 18/02/06 14:01
 * @Email Jliuer@aliyun.com
 * @Description 问题私信
 */
public class ChatRowQuestion extends ChatBaseRow {
    private TextView mTvChatContent;
    private TextView mTvQuestionTitle;

    public ChatRowQuestion(Context context, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        super(context, message, position, adapter, userInfoBean);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.SEND ?
                R.layout.item_chat_list_send_question : R.layout.item_chat_list_receive_question, this);
    }

    @Override
    protected void onFindViewById() {
        super.onFindViewById();
        mTvChatContent = (TextView) findViewById(R.id.tv_chat_content);
        mTvQuestionTitle = (TextView) findViewById(R.id.tv_question_title);
    }

    @Override
    protected void onSetUpView() {
        super.onSetUpView();
        try {
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            // 正文
            Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
            mTvChatContent.setText(span, TextView.BufferType.SPANNABLE);
            mTvQuestionTitle.setText(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

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
 * @Description 动态私信
 */
public class ChatRowFeed extends ChatBaseRow {
    private TextView mTvChatContent;
    private TextView mTvFeedAuthor;

    public ChatRowFeed(Context context, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        super(context, message, position, adapter, userInfoBean);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.SEND ?
                R.layout.item_chat_list_send_feed : R.layout.item_chat_list_receive_feed, this);
    }

    @Override
    protected void onFindViewById() {
        super.onFindViewById();
        mTvChatContent = (TextView) findViewById(R.id.tv_chat_content);
        mTvFeedAuthor = (TextView) findViewById(R.id.tv_feed_author);
    }

    @Override
    protected void onSetUpView() {
        super.onSetUpView();
        try {
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            // 正文
            Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
            mTvChatContent.setText(span, TextView.BufferType.SPANNABLE);
            String feedType = message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE, "");
            int image, video;

            if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE.equals(feedType)) {
                image = message.direct() == EMMessage.Direct.SEND ? R.mipmap.ico_pic_disabled : R.mipmap.ico_pic_highlight;
                mTvChatContent.setCompoundDrawablesWithIntrinsicBounds(image, 0, 0, 0);
            }
            if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_VIDEO.equals(feedType)) {
                video = message.direct() == EMMessage.Direct.SEND ? R.mipmap.ico_video_disabled : R.mipmap.ico_video_highlight;
                mTvChatContent.setCompoundDrawablesWithIntrinsicBounds(video, 0, 0, 0);
            }
            mTvFeedAuthor.setText(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

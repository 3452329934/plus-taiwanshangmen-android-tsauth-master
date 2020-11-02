package com.zhiyicx.thinksnsplus.modules.chat.item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.chat.call.TSEMHyphenate;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;


/**
 * @author Catherine
 * @describe 文字的item
 * @date 2018/1/2
 * @contact email:648129313@qq.com
 */

public class ChatRowText extends ChatBaseRow {
    private TextView mTvChatContent;
    private boolean adminMsg;

    public ChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        super(context, message, position, adapter, userInfoBean);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.SEND ?
                R.layout.item_chat_list_send_text : R.layout.item_chat_list_receive_text, this);
    }

    @Override
    protected void onFindViewById() {
        super.onFindViewById();
        mTvChatContent = (TextView) findViewById(R.id.tv_chat_content);
    }

    @Override
    protected void onSetUpView() {
        super.onSetUpView();
        try {
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            // 正文
            Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
            mTvChatContent.setText(span, TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) mTvChatContent.getText();
            URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
            for (int i = 0; i < spans.length; i++) {
                String url = spans[i].getURL();
                int index = spannable.toString().indexOf(url);
                int end = index + url.length();
                if (index == -1) {
                    if (url.contains("http://")) {
                        url = url.replace("http://", "");
                    } else if (url.contains("https://")) {
                        url = url.replace("https://", "");
                    } else if (url.contains("rtsp://")) {
                        url = url.replace("rtsp://", "");
                    }
                    index = spannable.toString().indexOf(url);
                    end = index + url.length();
                }
                if (index != -1) {
                    spannable.removeSpan(spans[i]);
                    int finalI = i;
                    spannable.setSpan(new ClickableSpan() {
                                          @Override
                                          public void onClick(@NonNull View widget) {
                                              CustomWEBActivity.startToWEBActivity(widget.getContext(), spans[finalI].getURL());
                                          }
                                      }, index
                            , end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

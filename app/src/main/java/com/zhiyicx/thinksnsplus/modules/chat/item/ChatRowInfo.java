package com.zhiyicx.thinksnsplus.modules.chat.item;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsActivity;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;


/**
 * @author Jliuer
 * @Date 18/02/06 14:01
 * @Email Jliuer@aliyun.com
 * @Description 动态私信
 */
public class ChatRowInfo extends ChatBaseRow {

    private TextView mTvChatContent;
    private TextView mTvInfoTittle;
    private ImageView mIvInfoImage;

    public ChatRowInfo(Context context, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        super(context, message, position, adapter, userInfoBean);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.SEND ?
                R.layout.item_chat_list_send_info : R.layout.item_chat_list_receive_info, this);
    }

    @Override
    protected void onFindViewById() {
        super.onFindViewById();
        mTvChatContent = findViewById(R.id.tv_chat_content);
        mTvInfoTittle = findViewById(R.id.tv_info_title);
        mIvInfoImage = findViewById(R.id.iv_info_image);
        setOnClickListener(view -> mTvChatContent.performClick());
        RxView.clicks(mTvChatContent)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    Long id = Long.parseLong(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_ID, "-1"));
                    InfoDetailsActivity.startInfoDetailsActivity(getContext(),id);
                });

    }

    @Override
    protected void onSetUpView() {
        super.onSetUpView();
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        // 正文
        Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());

        String url = message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_IMAGE, "");

        mTvInfoTittle.setText(span);
        mTvChatContent.setText(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_NAME, ""));

//        if (TextUtils.isEmpty(url)) {
//            mIvInfoImage.setVisibility(GONE);
//            return;
//        }

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.shape_default_image)
                .error(R.drawable.shape_default_image)
                .into(mIvInfoImage);

    }

}

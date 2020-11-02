package com.zhiyicx.thinksnsplus.modules.chat.item;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.RoundedCornersTransformation;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;


/**
 * @author Jliuer
 * @Date 18/02/06 14:01
 * @Email Jliuer@aliyun.com
 * @Description 动态私信
 */
public class ChatRowCircle extends ChatBaseRow {
    private TextView mTvChatContent;
    private TextView mTvCircleTittle;
    private ImageView mIvCircleImage;

    public ChatRowCircle(Context context, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        super(context, message, position, adapter, userInfoBean);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.SEND ?
                R.layout.item_chat_list_send_circle : R.layout.item_chat_list_receive_circle, this);
    }

    @Override
    protected void onFindViewById() {
        super.onFindViewById();
        mTvChatContent = findViewById(R.id.tv_chat_content);
        mTvCircleTittle = findViewById(R.id.tv_circle_title);
        mIvCircleImage = findViewById(R.id.iv_circle_image);
    }

    @Override
    protected void onSetUpView() {
        super.onSetUpView();
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        // 正文
        Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());

        String url = message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_IMAGE, "");
        mTvChatContent.setText(span);
        mTvCircleTittle.setText(message.getStringAttribute(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_NAME, ""));

//        if (TextUtils.isEmpty(url)) {
//            mIvCircleImage.setVisibility(GONE);
//            return;
//        }

        RoundedCornersTransformation cornersTransformation;
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            cornersTransformation = new RoundedCornersTransformation(AppApplication.getContext(),
                    context.getResources().getDimensionPixelOffset(R.dimen.ts_chat_item_iamge_raduis),
                    0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT);
        } else {
            cornersTransformation = new RoundedCornersTransformation(AppApplication.getContext(),
                    context.getResources().getDimensionPixelOffset(R.dimen.ts_chat_item_iamge_raduis),
                    0, RoundedCornersTransformation.CornerType.BOTTOM_RIGHT);
        }
        Glide.with(context)
                .load(url)
                .bitmapTransform(new CenterCrop(context), cornersTransformation)
                .placeholder(message.direct() == EMMessage.Direct.RECEIVE ? R.drawable.shape_circle_letter_other_default_image :
                        R.drawable.shape_circle_letter_my_default_image)
                .error(message.direct() == EMMessage.Direct.RECEIVE ? R.drawable.shape_circle_letter_other_default_image :
                        R.drawable.shape_circle_letter_my_default_image)
                .into(mIvCircleImage);

    }

}

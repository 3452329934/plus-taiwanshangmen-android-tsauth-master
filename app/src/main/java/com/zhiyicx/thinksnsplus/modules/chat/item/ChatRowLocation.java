package com.zhiyicx.thinksnsplus.modules.chat.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.RoundedCornersTransformation;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * @author Catherine
 * @describe 位置消息
 * @date 2018/1/9
 * @contact email:648129313@qq.com
 */

public class ChatRowLocation extends ChatBaseRow {

    private TextView mTvLocation;
    private TextView mTvLocation2;
    private ImageView mIvLocation;

    public static final String REMOTE = "remote";

    public ChatRowLocation(Context context, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        super(context, message, position, adapter, userInfoBean);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.item_chat_list_receive_location : R.layout.item_chat_list_send_location, this);
    }

    @Override
    protected void onFindViewById() {
        super.onFindViewById();
        mTvLocation = (TextView) findViewById(R.id.tv_location);
        mTvLocation2 = (TextView) findViewById(R.id.tv_location2);
        mIvLocation = (ImageView) findViewById(R.id.iv_location);
    }

    @Override
    protected void onSetUpView() {
        super.onSetUpView();
        String address;
        String addressDetail="";
        String url = "";
        if (message.getBody() instanceof EMImageMessageBody && message.getBooleanAttribute(TSEMConstants.BUNDLE_LOCATION_IMAGE, false)) {
            address = message.getStringAttribute(TSEMConstants.BUNDLE_LOCATION_TITLE, "");
            addressDetail = message.getStringAttribute(TSEMConstants.BUNDLE_LOCATION_ADDRESS, "");
            EMImageMessageBody imageMessageBody = (EMImageMessageBody) message.getBody();
            boolean isUseLocalImage = message.direct() == EMMessage.Direct.SEND && !TextUtils.isEmpty(imageMessageBody.getLocalUrl()) && FileUtils
                    .isFileExists
                            (imageMessageBody.getLocalUrl());
            url = isUseLocalImage ? imageMessageBody.getLocalUrl() : imageMessageBody.getRemoteUrl();
        } else {
            EMLocationMessageBody locBody = (EMLocationMessageBody) message.getBody();
            address = locBody.getAddress();
        }
        RoundedCornersTransformation cornersTransformation;
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            cornersTransformation = new RoundedCornersTransformation(AppApplication.getContext(),
                    context.getResources().getDimensionPixelOffset(R.dimen.ts_chat_item_iamge_raduis),
                    0, RoundedCornersTransformation.CornerType.OTHER_TOP_LEFT, bitmap -> {
                        String remote = message.getStringAttribute(REMOTE, "");
                        boolean hasLocalImage = !TextUtils.isEmpty(remote) && FileUtils.isFileExists(remote);
                        if (!hasLocalImage) {
                            String path = FileUtils.saveBitmapToFile(context, bitmap, System.currentTimeMillis() + "amap.jpg");
                            message.setAttribute(REMOTE, path);
                        }
                    });
        } else {
            cornersTransformation = new RoundedCornersTransformation(AppApplication.getContext(),
                    context.getResources().getDimensionPixelOffset(R.dimen.ts_chat_item_iamge_raduis),
                    0, RoundedCornersTransformation.CornerType.OTHER_TOP_RIGHT);
        }


        mTvLocation.setText(address);
        mTvLocation2.setText(addressDetail);
        Glide.with(context)
                .load(url)
                .bitmapTransform(new FitCenter(context), cornersTransformation)
                .placeholder(R.drawable.shape_default_image)
                .error(R.drawable.shape_default_image)
                .into(mIvLocation);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        super.onViewUpdate(msg);
    }

}

package com.zhiyicx.thinksnsplus.modules.chat.item.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.presenter.EaseChatLocationPresenter;
import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.common.utils.ActivityHandler;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.thinksnsplus.modules.chat.ChatActivity;
import com.zhiyicx.thinksnsplus.modules.chat.ChatFragment;
import com.zhiyicx.thinksnsplus.modules.chat.item.ChatRowLocation;
import com.zhiyicx.thinksnsplus.modules.chat.location.look.LookLocationActivity;

/**
 * @author Catherine
 * @describe
 * @date 2018/1/9
 * @contact email:648129313@qq.com
 */

public class TSChatLocationPresenter extends EaseChatLocationPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean userInfoBean) {
        return new ChatRowLocation(cxt, message, position, adapter, userInfoBean);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        double latitude, longitude;
        String address, title;
        String url = "";
        if (message.getBody() instanceof EMImageMessageBody && message.getBooleanAttribute(TSEMConstants.BUNDLE_LOCATION_IMAGE, false)) {
            latitude = Double.parseDouble(message.getStringAttribute(TSEMConstants.BUNDLE_LOCATION_LATITUDE, "-1.0"));
            longitude = Double.parseDouble(message.getStringAttribute(TSEMConstants.BUNDLE_LOCATION_LONGITUDE, "-1.0"));
            title = message.getStringAttribute(TSEMConstants.BUNDLE_LOCATION_TITLE, "");
            address = message.getStringAttribute(TSEMConstants.BUNDLE_LOCATION_ADDRESS, "");
            EMImageMessageBody imageMessageBody = (EMImageMessageBody) message.getBody();
            boolean isUseLocalImage = message.direct() == EMMessage.Direct.SEND && !TextUtils.isEmpty(imageMessageBody.getLocalUrl()) && FileUtils
                    .isFileExists
                            (imageMessageBody.getLocalUrl());
            String remote = message.getStringAttribute(ChatRowLocation.REMOTE, "");
            url = isUseLocalImage ? imageMessageBody.getLocalUrl() : remote;
        } else {
            EMLocationMessageBody locBody = (EMLocationMessageBody) message.getBody();
            latitude = locBody.getLatitude();
            longitude = locBody.getLongitude();
            address = locBody.getAddress();
            title = locBody.getAddress();
        }

        Intent intent = new Intent(getContext(), LookLocationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble(TSEMConstants.BUNDLE_LOCATION_LATITUDE, latitude);
        bundle.putDouble(TSEMConstants.BUNDLE_LOCATION_LONGITUDE, longitude);
        bundle.putString(TSEMConstants.BUNDLE_LOCATION_ADDRESS, address);
        bundle.putString(TSEMConstants.BUNDLE_LOCATION_TITLE, title);
        bundle.putString(TSEMConstants.BUNDLE_LOCATION_IMAGE, url);
        bundle.putString(TSEMConstants.BUNDLE_CHAT_ID, message.getTo());

        intent.putExtras(bundle);
        getContext().startActivity(intent);
        try {
            if (ActivityHandler.getInstance().currentActivity() instanceof ChatActivity) {
                ((ChatFragment) ((TSActivity) ActivityHandler.getInstance().currentActivity()).getContanierFragment())
                        .setNeedRefreshToLast(false);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

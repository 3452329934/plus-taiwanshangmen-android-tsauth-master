package com.zhiyicx.thinksnsplus.modules.chat.location.tofriends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.thinksnsplus.modules.chat.select.SelectFriendsActivity;

/**
 * @Author Jliuer
 * @Date 2018/06/14/10:41
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ToFriendsActivity extends SelectFriendsActivity {

    @Override
    protected ToFriendsFragment getFragment() {
        return ToFriendsFragment.instance(getIntent().getExtras());
    }

    public static void startToFriendActivity(Context context, EMMessage message) {
        Intent intent = new Intent(context, ToFriendsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EaseConstant.EXTRA_MESSAGE, message);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startToFriendActivity(Context context, String image, String latitude,
                                             String longitude, String address, String name, String chatId) {

        Intent intent = new Intent(context, ToFriendsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(TSEMConstants.BUNDLE_LOCATION_IMAGE, image);
        bundle.putString(TSEMConstants.BUNDLE_LOCATION_LATITUDE, latitude);
        bundle.putString(TSEMConstants.BUNDLE_LOCATION_LONGITUDE, longitude);
        bundle.putString(TSEMConstants.BUNDLE_LOCATION_ADDRESS, address);
        bundle.putString(TSEMConstants.BUNDLE_LOCATION_TITLE, name);
        bundle.putString(TSEMConstants.BUNDLE_CHAT_ID, chatId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}

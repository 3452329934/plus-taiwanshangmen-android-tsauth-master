package com.zhiyicx.thinksnsplus.modules.chat.location.tofriends;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.em.manager.util.TSEMessageUtils;
import com.zhiyicx.common.utils.ActivityHandler;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.chat.ChatActivity;
import com.zhiyicx.thinksnsplus.modules.chat.location.look.LookLocationActivity;
import com.zhiyicx.thinksnsplus.modules.chat.select.SelectFriendsFragment;
import com.zhiyicx.thinksnsplus.utils.DealPhotoUtils;

import java.util.List;

/**
 * @Author Jliuer
 * @Date 2018/06/14/10:42
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ToFriendsFragment extends SelectFriendsFragment {

    private String mCurrentChatId;

    public static ToFriendsFragment instance(Bundle bundle) {
        ToFriendsFragment friendsFragment = new ToFriendsFragment();
        friendsFragment.setArguments(bundle);
        return friendsFragment;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        if (getArguments() != null) {
            mCurrentChatId = getArguments().getString(TSEMConstants.BUNDLE_CHAT_ID);
        }
    }

    @Override
    protected void setRightClick() {
        if (mSelectedList.size() == 1) {
            super.setRightClick();
        } else {
            String image, latitude, longitude, address, name;
            image = latitude = longitude = address = name = "";
            if (getArguments() != null) {
                image = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_IMAGE);
                latitude = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_LATITUDE);
                longitude = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_LONGITUDE);
                address = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_ADDRESS);
                name = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_TITLE);
            }
            boolean isGif = DealPhotoUtils.checkPhotoIsGif(image);
            boolean isNeedOrinImage = isGif || DealPhotoUtils.checkPhotoIsLongImage(image);
            if (TextUtils.isEmpty(image)) {
                return;
            }
            for (UserInfoBean user : mSelectedList) {
                TSEMessageUtils.sendImageLocationMessage(image, latitude, longitude,
                        address, name, isNeedOrinImage, String.valueOf(user.getUser_id()));
            }
            showSnackSuccessMessage(getString(R.string.send_success));
            ActivityHandler.getInstance().removeActivity(LookLocationActivity.class);
        }
    }

    @Override
    protected void snackViewDismissWhenTimeOut(Prompt prompt) {
        mActivity.finish();
    }

    @Override
    protected void checkData() {
        mToolbarRight.setEnabled(mSelectedList.size() != 0);
        mIvSearchIcon.setVisibility(mSelectedList.size() > 0 ? View.GONE : View.VISIBLE);
        if (mSelectedList.size() > 0) {

            if (mChatGroupBean == null) {
                setRightText(String.format(getString(R.string.select_friends_send_location_right_title), mSelectedList.size()));
            } else {
                setRightText(String.format(getString(mIsDeleteMember ? R.string.chat_edit_group_remove_d : R.string.chat_edit_group_add_d),
                        mSelectedList.size()));
            }

            mToolbarRight.setTextColor(getColor(R.color.themeColor));
        } else {
            if (mChatGroupBean == null) {
                setRightText(getString(R.string.send));
            } else {
                setRightText(String.format(getString(mIsDeleteMember ? R.string.chat_edit_group_remove : R.string.chat_edit_group_add),
                        mSelectedList.size()));
            }
            mToolbarRight.setTextColor(getColor(R.color.normal_for_disable_button_text));
        }
        mSelectedFriendsAdapter.notifyDataSetChanged();
        if (!mSelectedList.isEmpty()) {
            mRvSelectResult.smoothScrollToPosition(mSelectedFriendsAdapter.getItemCount() - 1);
        }
    }

    @Override
    protected void checkUserIsSelected(List<UserInfoBean> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        UserInfoBean currentChatUser = null;
        for (UserInfoBean userInfoBean : list) {
            // 给已经选中的用户手动设置
            if (mSelectedList.size() > 0) {
                for (int i = 0; i < mSelectedList.size(); i++) {
                    if (userInfoBean.getUser_id().equals(mSelectedList.get(i).getUser_id())) {
                        userInfoBean.setIsSelected(1);
                        break;
                    }
                }
            }
            if (!TextUtils.isEmpty(mCurrentChatId)) {
                if (mCurrentChatId.equals(String.valueOf(userInfoBean.getUser_id()))) {
                    currentChatUser = userInfoBean;
                }
            }
            // 如果是添加群成员 那么要把已经有的成员处理为不可点击
            if (!mIsDeleteMember && mChatGroupBean != null && mChatGroupBean.getAffiliations().size() > 0) {
                for (int i = 0; i < mChatGroupBean.getAffiliations().size(); i++) {
                    if (userInfoBean.getUser_id().equals(mChatGroupBean.getAffiliations().get(i).getUser_id())) {
                        userInfoBean.setIsSelected(-1);
                        break;
                    }
                }
            }
        }
        if (currentChatUser != null) {
            list.remove(currentChatUser);
        }
    }

    @Override
    public void createConversionResult(List<ChatUserInfoBean> list, EMConversation.EMConversationType type, int chatType, String id) {
        if (type == EMConversation.EMConversationType.Chat) {
            EMClient.getInstance().chatManager().getConversation(id, type, true);
        } else {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(id);
            if (group == null) {
                showSnackErrorMessage(getString(R.string.create_fail));
                return;
            }
        }

        String image, latitude, longitude, address, name;
        image = latitude = longitude = address = name = "";
        if (getArguments() != null) {
            image = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_IMAGE);
            latitude = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_LATITUDE);
            longitude = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_LONGITUDE);
            address = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_ADDRESS);
            name = getArguments().getString(TSEMConstants.BUNDLE_LOCATION_TITLE);
        }

        ChatActivity.startChatActivityWithLocation(mActivity, id, chatType, image, latitude, longitude, address, name);
        getActivity().finish();
    }
}

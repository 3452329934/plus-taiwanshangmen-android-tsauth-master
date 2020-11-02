package com.zhiyicx.thinksnsplus.modules.chat.private_letter.newconversation;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hyphenate.chat.EMMessage;
import com.zhiyi.emoji.EmojiDialog;
import com.zhiyicx.baseproject.em.manager.util.TSEMessageUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.chat.location.tofriends.ToFriendsFragment;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendFragment;
import com.zhiyicx.thinksnsplus.widget.popwindow.LetterPopWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/10/9:52
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class NewConversationFragment extends ToFriendsFragment {

    private LetterPopWindow mLetterPopWindow;
    private EmojiDialog mEmojiDialog;

    public static NewConversationFragment newInstance(Bundle args) {
        NewConversationFragment fragment = new NewConversationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setRightClick() {
        Letter letter;
        if (getArguments() == null) {
            return;
        }
        letter = getArguments().getParcelable(ChooseFriendFragment.LETTER);
        List<String> conversations = new ArrayList<>();
        StringBuilder name = new StringBuilder();
        for (UserInfoBean user : mSelectedList) {
            conversations.add(String.valueOf(user.getUser_id()));
            name.append(user.getName());
            name.append(",");
        }
        name.deleteCharAt(name.length() - 1);
        initLetterPopWindow(name.toString(), letter, conversations);
    }

    private void initLetterPopWindow(String name, Letter letter, List<String> conversations) {
        mLetterPopWindow = LetterPopWindow.builder()
                .with(getActivity())
                .parentView(getView())
                .isFocus(true)
                .titleStr(name)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .letter(letter)
                .buildCenterPopWindowItem1ClickListener(new LetterPopWindow.CenterPopWindowItemClickListener() {
                    @Override
                    public void onRightClicked(Letter letter) {
                        for (String conversationID : conversations) {
                            TSEMessageUtils.sendLetterMessage(conversationID,
                                    letter.getName(), letter.getContent(), letter.getMessage(), letter.getImage(),
                                    letter.getType(), letter.getDynamic_type(), letter.getId(), EMMessage.ChatType.Chat, letter.getCircle_id());
                        }
                        mLetterPopWindow.dismiss();
                        mActivity.finish();
                    }

                    @Override
                    public void onLeftClicked() {
                        mLetterPopWindow.dismiss();
                    }

                    @Override
                    public void onEmojiClick() {
                        Fragment fragment = getChildFragmentManager().findFragmentByTag(EmojiDialog.Tag);

                        if (fragment == null) {
                            mEmojiDialog = new EmojiDialog();
                            DeviceUtils.hideSoftKeyboard(mActivity, mLetterPopWindow.getEditText());
                            mEmojiDialog.setEditText(mLetterPopWindow.getEditText());
                            mEmojiDialog.show(getChildFragmentManager(), EmojiDialog.Tag);
                        } else {
                            mLetterPopWindow.getEditText().performClick();
                        }
                    }
                }).build();
        mLetterPopWindow.setFocusable(true);
        mLetterPopWindow.setTouchable(true);
        mLetterPopWindow.setOnDismissListener(() -> {
            if (mEmojiDialog != null) {
                mEmojiDialog.dismiss();
            }
        });
        mLetterPopWindow.getEditText().setOnClickListener(view -> {
            if (mEmojiDialog != null) {
                mEmojiDialog.dismiss();
            }
            mLetterPopWindow.getEditText().requestFocus();
            DeviceUtils.showSoftKeyboard(mActivity, mLetterPopWindow.getEditText());
        });
        mLetterPopWindow.update();
        mLetterPopWindow.show();
    }
}

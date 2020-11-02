package com.zhiyicx.thinksnsplus.modules.chat.private_letter.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.zhiyi.emoji.EmojiDialog;
import com.zhiyicx.baseproject.em.manager.util.TSEMessageUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBeanForGroupList;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendFragment;
import com.zhiyicx.thinksnsplus.modules.home.message.messagegroup.MessageGroupListFragment;
import com.zhiyicx.thinksnsplus.widget.popwindow.LetterPopWindow;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/10/9:10
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChooseGroupListFragment extends MessageGroupListFragment {

    private LetterPopWindow mLetterPopWindow;
    private EmojiDialog mEmojiDialog;

    public static ChooseGroupListFragment newInstance(Bundle args) {
        ChooseGroupListFragment fragment = new ChooseGroupListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void checkGroupExist(ChatGroupBeanForGroupList groupBean, EMGroup data) {
        if (data != null) {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupBean.getId(), EMConversation.EMConversationType.GroupChat, true);
            Letter letter = null;
            if (getArguments() != null) {
                letter = getArguments().getParcelable(ChooseFriendFragment.LETTER);
            }
            if (conversation != null && letter != null) {
                initLetterPopWindow(groupBean.getName(), letter, conversation.conversationId());
            }
        }
    }

    private void initLetterPopWindow(String name, Letter letter, String conversationID) {
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
                        TSEMessageUtils.sendLetterMessage(conversationID,
                                letter.getName(), letter.getContent(), letter.getMessage(), letter.getImage(),
                                letter.getType(), letter.getDynamic_type(), letter.getId(), EMMessage.ChatType.GroupChat,letter.getCircle_id());
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

package com.zhiyicx.thinksnsplus.modules.chat.private_letter;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.zhiyi.emoji.EmojiDialog;
import com.zhiyicx.baseproject.em.manager.util.TSEMessageUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.chat.call.TSEMHyphenate;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.adapter.ConversitionItem;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.adapter.OnItemClickLisener;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.adapter.TopChooseItem;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.group.ChooseGroupActivity;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.newconversation.NewConversationActivity;
import com.zhiyicx.thinksnsplus.modules.home.message.messagelist.BaseMessageConversationFragment;
import com.zhiyicx.thinksnsplus.widget.popwindow.LetterPopWindow;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/07/14:40
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChooseFriendFragment extends BaseMessageConversationFragment<ChooseFriendPresenter, MessageItemBeanV2>
        implements OnItemClickLisener {

    public static final String CREATE_CONVERSITION = "-1";
    public static final String CHOOSE_GROUP = "-2";
    public static final String LETTER = "letter";

    private LetterPopWindow mLetterPopWindow;
    private Letter mLetter;
    private EmojiDialog mEmojiDialog;

    @Override
    protected String setCenterTitle() {
        return getString(R.string.choose_friend);
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    public static ChooseFriendFragment newInstance(Bundle args) {
        ChooseFriendFragment fragment = new ChooseFriendFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        if (getArguments() != null) {
            mLetter = getArguments().getParcelable(LETTER);
        }
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<MessageItemBeanV2> data, boolean isLoadMore) {
        if (!isLoadMore) {
            MessageItemBeanV2 createConversition = new MessageItemBeanV2(CREATE_CONVERSITION);
            MessageItemBeanV2 chooseGroup = new MessageItemBeanV2(CHOOSE_GROUP);
            if (!data.contains(chooseGroup)) {
                data.add(0, chooseGroup);
            }
            if (!data.contains(createConversition)) {
                data.add(0, createConversition);
            }
        }
        super.onNetResponseSuccess(data, isLoadMore);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter<>(mActivity, mListDatas);
        adapter.addItemViewDelegate(new ConversitionItem(this));
        adapter.addItemViewDelegate(new TopChooseItem(this));
        return adapter;
    }

    @Override
    public void onBlickClick() {

    }

    @Override
    public void onItemClick(MessageItemBeanV2 messageItemBean, int position) {
        if (messageItemBean.getConversation() == null) {
            boolean create = ChooseFriendFragment.CREATE_CONVERSITION.equals(messageItemBean.getEmKey());
            boolean choose = ChooseFriendFragment.CHOOSE_GROUP.equals(messageItemBean.getEmKey());
            if (choose) {
                ChooseGroupActivity.startChooseGroupActivity(mActivity, mLetter);
                mActivity.finish();
            } else if (create) {
                NewConversationActivity.startNewConversationActivity(mActivity, mLetter);
                mActivity.finish();
            }
        } else {
            initLetterPopWindow(messageItemBean);
        }
    }

    private void initLetterPopWindow(MessageItemBeanV2 messageItemBean) {
        if (mLetter == null) {
            return;
        }
        String name;
        EMMessage.ChatType chatType;
        switch (messageItemBean.getConversation().getType()) {
            case Chat:
                // 私聊
                chatType = EMMessage.ChatType.Chat;
                UserInfoBean singleChatUserinfo = messageItemBean.getUserInfo();
                ChatUserInfoBean chatUserInfoBean = null;
                if (singleChatUserinfo == null) {
                    chatUserInfoBean = TSEMHyphenate.getInstance().getChatUser(messageItemBean.getEmKey());
                }
                if (singleChatUserinfo == null) {
                    name = chatUserInfoBean.getName();
                } else {
                    name = singleChatUserinfo.getName();
                }
                break;
            case GroupChat:
                // 群组
                chatType = EMMessage.ChatType.GroupChat;
                ChatGroupBean chatGroupBean = messageItemBean.getChatGroupBean();
                EMGroup group = EMClient.getInstance().groupManager().getGroup(messageItemBean.getEmKey());
                name = getString(R.string.chat_group_name_default, chatGroupBean == null ? group.getGroupName
                        () : chatGroupBean.getName(), chatGroupBean == null ? group.getMemberCount() : chatGroupBean.getAffiliations_count());
                break;
            default:
                name = "";
                chatType = EMMessage.ChatType.Chat;
                break;
        }
        if (TextUtils.isEmpty(name)) {
            return;
        }
        mLetterPopWindow = LetterPopWindow.builder()
                .with(getActivity())
                .parentView(getView())
                .isFocus(true)
                .titleStr(name)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .letter(mLetter)
                .buildCenterPopWindowItem1ClickListener(new LetterPopWindow.CenterPopWindowItemClickListener() {
                    @Override
                    public void onRightClicked(Letter letter) {
                        TSEMessageUtils.sendLetterMessage(messageItemBean.getConversation().conversationId(),
                                letter.getName(), letter.getContent(), letter.getMessage(), letter.getImage(), letter.getType(),
                                letter.getDynamic_type(), letter.getId(), chatType, letter.getCircle_id());
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

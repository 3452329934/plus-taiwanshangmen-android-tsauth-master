package com.zhiyicx.thinksnsplus.modules.home.message.messageat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.notify.AtMeaasgeBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserActivity;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserListFragment;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/16/11:51
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageAtFragment extends TSListFragment<MessageAtContract.Presenter, AtMeaasgeBean>
        implements MessageAtContract.View, MultiItemTypeAdapter.OnItemClickListener{

    public static final String FEED = "feeds";
    public static final String INFO = "news";
    public static final String POST = "group-posts";
    public static final String QUESTION = "questions";
    public static final String ANSWER = "question-answers";
    public static final String MUSIC = "musics";
    public static final String MUSIC_SPECIAL = "music_special";

    public static final String COMMENTS = "comments";

    private long mReplyUserId;// 被评论者的 id ,评论动态 id = 0
    private int mCurrentPostion;// 当前点击的 item 位置
    private Subscription showComment;

    public static MessageAtFragment newInstance() {

        Bundle args = new Bundle();

        MessageAtFragment fragment = new MessageAtFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
    }

    @Override
    protected boolean setUseInputCommentView() {
        return true;
    }

    @Override
    protected boolean setUseShadowView() {
        return true;
    }

    @Override
    protected void onShadowViewClick() {
        closeInputView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AtUserListFragment.REQUES_USER) {
            // @ 选人返回
            if (data != null && data.getExtras() != null) {
                UserInfoBean userInfoBean = data.getExtras().getParcelable(AtUserListFragment.AT_USER);
                if (userInfoBean != null) {
                    mIlvComment.appendAt(userInfoBean.getName());
                }
            }
            showComment = Observable.timer(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> showCommentView());
        }
    }

    @Override
    protected Long getMaxId(@NotNull List<AtMeaasgeBean> data) {
        return (long) getPage() + 1;
    }

    @Override
    protected int getOffset() {
        return super.getOffset();
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.at_me_message);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        MessageAtAdapter adapter = new MessageAtAdapter(mActivity, R.layout
                .item_message_comment_list, mListDatas);
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    public void showCommentView() {
        // 评论
        mIlvComment.setVisibility(View.VISIBLE);
        mIlvComment.setSendButtonVisiable(true);
        mIlvComment.getFocus();
        mVShadow.setVisibility(View.VISIBLE);
        DeviceUtils.showSoftKeyboard(getActivity(), mIlvComment.getEtContent());
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        mReplyUserId = mListDatas.get(position).getUser_id();
        mCurrentPostion = position;
        showCommentView();
        String contentHint = getString(R.string.reply, mListDatas.get(position)
                .getUserInfoBean().getName());
        mIlvComment.setEtContentHint(contentHint);
    }

    @Override
    public void onSendClick(View v, String text) {
        DeviceUtils.hideSoftKeyboard(getContext(), v);
        mIlvComment.setVisibility(View.GONE);
        mVShadow.setVisibility(View.GONE);
        mPresenter.sendComment(mCurrentPostion, mReplyUserId, text);
    }

    @Override
    protected Integer getPagesize() {
        return 0;
    }

    @Override
    public void onAtTrigger() {
        AtUserActivity.startAtUserActivity(this);
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_list_with_input;
    }

    @Override
    public void onDestroyView() {
        if (showComment != null && !showComment.isUnsubscribed()) {
            showComment.unsubscribe();
        }
        super.onDestroyView();
    }

    protected void closeInputView() {
        if (mIlvComment.getVisibility() == View.VISIBLE) {
            mIlvComment.setVisibility(View.GONE);
            DeviceUtils.hideSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        }

        mVShadow.setVisibility(View.GONE);
    }
}

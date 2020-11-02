package com.zhiyicx.thinksnsplus.modules.topic.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhiyi.rxdownload3.core.Status;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.Toll;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.baseproject.share.Share;
import com.zhiyicx.baseproject.widget.button.CombinationButton;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.baseproject.widget.popwindow.PayPopWindow;
import com.zhiyicx.common.BuildConfig;
import com.zhiyicx.common.base.BaseActivity;
import com.zhiyicx.common.utils.AndroidBug5497Workaround;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.SkinUtils;
import com.zhiyicx.common.utils.TextViewUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.fordownload.TSListFragmentForDownload;
import com.zhiyicx.thinksnsplus.config.UserPermissions;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.report.ReportResourceBean;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserActivity;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserListFragment;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListBaseItem;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForAdvert;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForEightImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForFiveImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForFourImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForNineImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForOneImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForSevenImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForShorVideo;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForSixImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForThreeImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForTwoImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForZeroImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForwardAnswer;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForwardCircle;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForwardInfo;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForwardMediaFeed;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForwardPost;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForwardQuestion;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForwardWordFeed;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.dynamic_type.SelectDynamicTypeActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.tollcomment.DynamicCommentTollActivity;
import com.zhiyicx.thinksnsplus.modules.gallery.GalleryActivity;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.modules.rank.adapter.RankIndexUserAdapter;
import com.zhiyicx.thinksnsplus.modules.report.ReportActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportType;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.ZhiyiVideoView;
import com.zhiyicx.thinksnsplus.modules.topic.create.CreateTopicActivity;
import com.zhiyicx.thinksnsplus.modules.topic.create.CreateTopicFragment;
import com.zhiyicx.thinksnsplus.modules.topic.detail.joined.JoinedUserActivity;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;
import com.zhiyicx.thinksnsplus.modules.wallet.sticktop.StickTopFragment;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicListCommentView;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicListTopicView;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicNoPullRecycleView;
import com.zhiyicx.thinksnsplus.widget.coordinatorlayout.TopicDetailBehavior;
import com.zhiyicx.thinksnsplus.widget.popwindow.DownloadPopWindow;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zycx.shortvideo.view.AutoPlayScrollListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayerManager;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;
import static com.zhiyicx.thinksnsplus.data.beans.DynamicListAdvert.DEFAULT_ADVERT_FROM_TAG;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_DETAIL_DATA;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_DETAIL_DATA_POSITION;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_VIDEO_STATE;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.LOOK_COMMENT_MORE;
import static com.zhiyicx.thinksnsplus.modules.dynamic.list.DynamicFragment.ITEM_SPACING;
import static com.zhiyicx.thinksnsplus.modules.dynamic.tollcomment.DynamicCommentTollFragment.TOLL_DYNAMIC_COMMENT;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/9:22
 * @Email Jliuer@aliyun.com
 * @Description 话题详情页
 */
public class TopicDetailFragment extends TSListFragmentForDownload<TopicDetailContract.Presenter, DynamicDetailBeanV2>
        implements TopicDetailContract.View, DynamicNoPullRecycleView.OnCommentStateClickListener<DynamicCommentBean>,
        DynamicListCommentView.OnCommentClickListener,
        DynamicListCommentView.OnMoreCommentClickListener,
        DynamicListBaseItem.OnReSendClickListener, DynamicListBaseItem.OnMenuItemClickLisitener,
        DynamicListBaseItem.OnImageClickListener, OnUserInfoClickListener, DynamicListTopicView.OnTopicClickListener,
        MultiItemTypeAdapter.OnItemClickListener, TextViewUtils.OnSpanTextClickListener,
        ZhiyiVideoView.ShareInterface {

    public static final String ID = "topic_to";
    public static final String FROM = "topic_from";

    /**
     * 话题顶部图片
     */
    @BindView(R.id.iv_topic_bg)
    ImageView mIvTopicBg;

    @BindView(R.id.iv_background_cover_top_hint)
    ImageView mIvTopicBgShadow;

    /**
     * 话题名字
     */
    @BindView(R.id.tv_topic_name)
    TextView mTvTopicName;

    /**
     * 话题顶部名字
     */
    @BindView(R.id.tv_top_topic_name)
    TextView mTvTopTopicName;

    /**
     * 话题创建者
     */
    @BindView(R.id.tv_topic_creator)
    TextView mTvTopicCreator;

    /**
     * 话题详情刷新动画
     */
    @BindView(R.id.iv_refresh)
    ImageView mIvRefresh;

    /**
     * 返回
     */
    @BindView(R.id.iv_back)
    ImageView mIvBack;

    /**
     * 分享
     */
    @BindView(R.id.iv_share)
    ImageView mIvShare;

    /**
     * ...
     */
    @BindView(R.id.iv_more)
    ImageView mIvMore;

    /**
     * 上面toolbar
     */
    @BindView(R.id.toolbar)
    Toolbar mCircleTitleLayout;

    /**
     * 有封面时话题描述
     */
    @BindView(R.id.tv_topic_dec)
    TextView mTvTopicDec;

    /**
     * 没有封面时话题描述
     */
    @BindView(R.id.tv_topic_dec_nobg)
    TextView mTvTopicDecNoBg;

    /**
     * 话题关注数量&动态数量
     */
    @BindView(R.id.tv_count)
    TextView mTvCount;

    /**
     * 关注
     */
    @BindView(R.id.iv_follow)
    CheckBox mIvFollow;

    /**
     * 右下角发送
     */
    @BindView(R.id.btn_send_post)
    ImageView mBtnSendPost;

    @BindView(R.id.al_appbar)
    AppBarLayout mAppBarLayout;

    /**
     * 参与话题的人
     */
    @BindView(R.id.ll_topic_join_user)
    LinearLayout mLlTopicJoinUser;

    @BindView(R.id.tv_topic_join_user)
    CombinationButton mTvTopicJoinUser;

    @BindView(R.id.rv_topic_join_user)
    RecyclerView mRvTopicJoinUser;

    private TopicDetailBehavior myAppBarLayoutBehavoir;
    private TopicDetailBean mTopic;

    /**
     * 付费弹窗
     */
    private PayPopWindow mPayImagePopWindow;

    /**
     * 当前评论的动态位置
     */
    private int mCurrentPostion;

    /**
     * 被评论者的 id
     */
    private long mReplyToUserId;

    /**
     * 动态重发操作
     */
    private ActionPopupWindow mReSendDynamicPopWindow;

    /**
     * 评论重发操作
     */
    private ActionPopupWindow mReSendCommentPopWindow;

    /**
     * 删除操作
     */
    private ActionPopupWindow mDeletCommentPopWindow;

    /**
     * 又上角操作
     */
    private ActionPopupWindow mEditeTopicPopWindow;

    /**
     * 参与话题的人列表适配器
     */
    private UserListAdapter mUserListAdapter;

    /**
     * 记录话题封面，如果改变，则刷新
     */
    private String mCoverPath = "";

    private String mDynamicType = "topic_dynamic";

    /**
     * 显示评论输入
     */
    private Subscription showComment;

    /**
     * 标识置顶的那条评论
     */
    private DynamicCommentBean mDynamicCommentBean;


    public static TopicDetailFragment newInstance(Bundle args) {
        TopicDetailFragment fragment = new TopicDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean setUseInputCommentView() {
        return true;
    }

    @Override
    public String getKeyWord() {
        return null;
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected int setToolBarBackgroud() {
        return android.R.color.transparent;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected boolean setUseCenterLoading() {
        return true;
    }

    @Override
    protected int getstatusbarAndToolbarHeight() {
        return 0;
    }

    @Override
    protected float getItemDecorationSpacing() {
        return ITEM_SPACING;
    }

    @Override
    protected View getRightViewOfMusicWindow() {
        return mIvMore;
    }

    @Override
    protected void setLoadingViewHolderClick() {
        super.setLoadingViewHolderClick();
        mPresenter.requestNetData(0L, false);
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_topic;
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
    protected void initView(View rootView) {
        super.initView(rootView);
        initToolBar();


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            AndroidBug5497Workaround.assistActivity(mActivity);
        }

        // 自动播放 - 滑出屏幕暂停也在这里面
        mRvList.addOnScrollListener(new AutoPlayScrollListener() {
            @Override
            public int getPlayerViewId() {
                return R.id.videoplayer;
            }

            @Override
            public boolean canAutoPlay() {
                // NetUtils.isWifiConnected(getContext().getApplicationContext())
                // 暂时关闭滑动自动播放
                return false;
            }
        });

        ((BaseActivity) (Objects.requireNonNull(getActivity()))).registerFragmentDispatchTouchEventListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CreateTopicFragment.CHANGETOPIC && resultCode == Activity.RESULT_OK) {
            // 编辑话题
            if (data != null && data.getExtras() != null) {
                updateCurrentTopic(data.getExtras().getParcelable(SearchTopicFragment.TOPIC));
            }
        } else if (requestCode == AtUserListFragment.REQUES_USER) {
            // @ 选人返回
            if (data != null && data.getExtras() != null) {
                UserInfoBean userInfoBean = data.getExtras().getParcelable(AtUserListFragment.AT_USER);
                if (userInfoBean != null) {
                    mIlvComment.appendAt(userInfoBean.getName());
                }
            }
            showComment = Observable.timer(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> showBottomView(false));
        } else if (requestCode == StickTopFragment.PINNED) {
            if (mDynamicCommentBean != null) {
                mDynamicCommentBean.setPinned(true);
                refreshData();
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestNetData(DEFAULT_PAGE_MAX_ID, false);

        myAppBarLayoutBehavoir = (TopicDetailBehavior)
                ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();

        myAppBarLayoutBehavoir.setOnRefreshChangeListener(new TopicDetailBehavior.onRefreshChangeListener() {
            @Override
            public void onRefreshShow() {
                mIvRefresh.setVisibility(View.VISIBLE);
                ((AnimationDrawable) mIvRefresh.getDrawable()).start();
            }

            @Override
            public void doRefresh() {
                mPresenter.requestNetData(DEFAULT_PAGE_MAX_ID, false);
            }

            @Override
            public void stopRefresh() {
                ((AnimationDrawable) mIvRefresh.getDrawable()).stop();
                mIvRefresh.setVisibility(View.GONE);
            }

            @Override
            public void alphaChange(float point, int titleColor, int bgColor, int titleIconColor) {
                mCircleTitleLayout.setBackgroundColor(bgColor);
                mTvTopTopicName.setTextColor(titleColor);
                if (mTopic != null && TextUtils.isEmpty(mTopic.getLogoUrl()) && mTopic.getLogo() == null) {
                    return;
                }
                setToolBarLeftImage(R.mipmap.topbar_back_white, titleIconColor);
                setToolBarRightImage(R.mipmap.topbar_more_white, titleIconColor);
                setToolBarRightLeftImage(R.mipmap.music_ico_share, titleIconColor);
            }
        });

        mTvTopicJoinUser.setOnClickListener(view -> {
            if (mTvTopicJoinUser.getCombinedButtonImgRight().getVisibility() == View.VISIBLE) {
                JoinedUserActivity.startJoinedUserActivity(mActivity, mTopic.getId());
            }
        });

        setLoadViewHolderImag(R.mipmap.img_default_internet);
    }

    @Override
    protected Long getMaxId(@NotNull List<DynamicDetailBeanV2> data) {
        return (long) data.get(data.size() - 1).getIndex();
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<DynamicDetailBeanV2> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        myAppBarLayoutBehavoir.stopRefreshing();
        ((AnimationDrawable) mIvRefresh.getDrawable()).stop();
        mIvRefresh.setVisibility(View.INVISIBLE);
        closeLoadingView();
        if (mEmptyView != null) {
            mEmptyView.setPadding(0, 0, 0, 500);
        }
    }

    @Override
    public void onResponseError(Throwable throwable, boolean isLoadMore) {
        showLoadViewLoadError();
    }

    @Override
    public void topicHasBeDeleted() {
        setLoadViewHolderImag(R.mipmap.img_default_delete);
    }

    @Override
    public TopicDetailBean getCurrentTopic() {
        return mTopic;
    }

    @Override
    public Long getTopicId() {
        if (getArguments() != null) {
            return getArguments().getLong(ID);
        }
        if (mTopic != null) {
            return mTopic.getId();
        }
        return null;
    }

    @Override
    public void updateCurrentTopic(TopicDetailBean topic) {
        mTopic = topic;
        if (mTopic == null) {
            return;
        }
        if (mTopic.getCreator_user_id() == AppApplication.getMyUserIdWithdefault()) {
            mIvFollow.setVisibility(View.GONE);
        } else {
            if (mTopic.has_followed()) {
                mIvFollow.setText(R.string.followed);
                mIvFollow.setChecked(true);
            } else {
                mIvFollow.setText(R.string.add_follow);
                mIvFollow.setChecked(false);
            }
        }
        // 话题头像
        String cover = TextUtils.isEmpty(topic.getLogoUrl())
                ? topic.getLogo() != null ? topic.getLogo().getUrl() : "" : topic.getLogoUrl();
        boolean change = !mCoverPath.equals(cover);
        mCoverPath = cover;
        mTvTopicDec.setText(topic.getDesc());
        mTvTopicDecNoBg.setText(topic.getDesc());
        if (TextUtils.isEmpty(topic.getLogoUrl()) && topic.getLogo() == null) {
            // 没有封面
            mIvTopicBgShadow.setVisibility(View.GONE);
            mIvRefresh.setImageResource(R.drawable.frame_loading_grey);
            mTvTopicDec.setVisibility(View.GONE);
            mTvTopicDecNoBg.setVisibility(TextUtils.isEmpty(topic.getDesc()) ? View.GONE : View.VISIBLE);
            mTvTopicName.setTextColor(SkinUtils.getColor(R.color.important_for_content));
            mTvTopicCreator.setTextColor(SkinUtils.getColor(R.color.normal_for_disable_button_b3_text));
            mTvTopicName.setShadowLayer(0, 0, 0, Color.WHITE);
            mTvTopicCreator.setShadowLayer(0, 0, 0, Color.WHITE);
            setToolBarLeftImage(R.mipmap.topbar_back);
            setToolBarRightImage(R.mipmap.topbar_more_black);
            setToolBarRightLeftImage(R.mipmap.topbar_back_black);

        } else {
            mTvTopicDecNoBg.setVisibility(View.GONE);
            mIvTopicBgShadow.setVisibility(View.VISIBLE);
            mTvTopicDec.setVisibility(TextUtils.isEmpty(topic.getDesc()) ? View.GONE : View.VISIBLE);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mIvTopicBg.getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.create_topic_pic_hint_height);
            mIvTopicBg.setLayoutParams(layoutParams);
            mTvTopicName.setTextColor(SkinUtils.getColor(R.color.white));
            mTvTopicCreator.setTextColor(SkinUtils.getColor(R.color.white));
            mTvTopicName.setTextAppearance(mActivity, R.style.TextShadowStyle);
            mTvTopicCreator.setTextAppearance(mActivity, R.style.TextShadowStyle);
            if (change) {
                Glide.with(mActivity)
                        .load(mCoverPath)
                        .into(mIvTopicBg);
            }
        }

        mTvTopicName.setText(topic.getName());
        mTvTopTopicName.setText(topic.getName());
        mTvTopicCreator.setText(getString(R.string.topic_creator, topic.getCreator_user().getName()));

        updateCount(topic.getFeeds_count(), topic.getFollowers_count());

        if (mUserListAdapter == null) {
            if (topic.getUserList() == null || topic.getUserList().isEmpty()) {
                return;
            }
            mUserListAdapter = new UserListAdapter(mActivity, R.layout.item_topic_joined_user, topic.getUserList());
            GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 4);
            mRvTopicJoinUser.setAdapter(mUserListAdapter);
            mRvTopicJoinUser.setLayoutManager(layoutManager);
        } else {
            if (mUserListAdapter.getDatas().equals(topic.getUserList())) {
                return;
            }
            mUserListAdapter.dataChange(topic.getUserList());
            mUserListAdapter.notifyDataSetChanged();
        }
        mTvTopicJoinUser.getCombinedButtonImgRight().setVisibility(topic.getUserList().size() > 3 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateCount(int feedsCount, int followersCount) {
        mTvCount.setText(getString(R.string.topic_content_count, feedsCount, followersCount));
    }

    @Override
    protected MultiItemTypeAdapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter<>(getContext(), mListDatas);
        setAdapter(adapter, new DynamicListItemForZeroImage(getContext()));
        setAdapter(adapter, new DynamicListItemForOneImage(getContext()));
        setAdapter(adapter, new DynamicListItemForTwoImage(getContext()));
        setAdapter(adapter, new DynamicListItemForThreeImage(getContext()));
        setAdapter(adapter, new DynamicListItemForFourImage(getContext()));
        setAdapter(adapter, new DynamicListItemForFiveImage(getContext()));
        setAdapter(adapter, new DynamicListItemForSixImage(getContext()));
        setAdapter(adapter, new DynamicListItemForSevenImage(getContext()));
        setAdapter(adapter, new DynamicListItemForEightImage(getContext()));
        setAdapter(adapter, new DynamicListItemForNineImage(getContext()));
        setAdapter(adapter, new DynamicListItemForAdvert(getContext()));

        setAdapter(adapter, new DynamicListItemForwardWordFeed(mActivity));
        setAdapter(adapter, new DynamicListItemForwardMediaFeed(mActivity));
        setAdapter(adapter, new DynamicListItemForwardInfo(mActivity));
        setAdapter(adapter, new DynamicListItemForwardCircle(mActivity));
        setAdapter(adapter, new DynamicListItemForwardPost(mActivity));
        setAdapter(adapter, new DynamicListItemForwardQuestion(mActivity));
        setAdapter(adapter, new DynamicListItemForwardAnswer(mActivity));

        setAdapter(adapter, new DynamicListItemForShorVideo(getContext(), this) {
            @Override
            protected String videoFrom() {
                return mDynamicType;
            }
        });
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    protected void setAdapter(MultiItemTypeAdapter adapter, DynamicListBaseItem
            dynamicListBaseItem) {
        dynamicListBaseItem.setOnImageClickListener(this);
        dynamicListBaseItem.setOnSpanTextClickListener(this);
        dynamicListBaseItem.setOnUserInfoClickListener(this);
        dynamicListBaseItem.setOnMenuItemClickLisitener(this);
        dynamicListBaseItem.setOnReSendClickListener(this);
        dynamicListBaseItem.setOnMoreCommentClickListener(this);
        dynamicListBaseItem.setOnCommentClickListener(this);
        dynamicListBaseItem.setOnCommentStateClickListener(this);
        dynamicListBaseItem.setOnTopicClickListener(this);
        adapter.addItemViewDelegate(dynamicListBaseItem);
    }

    /**
     * 发送评论
     *
     * @param v
     * @param text
     */
    @Override
    public void onSendClick(View v, String text) {
        com.zhiyicx.imsdk.utils.common.DeviceUtils.hideSoftKeyboard(getContext(), v);
        mIlvComment.setVisibility(View.GONE);
        mVShadow.setVisibility(View.GONE);
        mPresenter.sendCommentV2(mCurrentPostion, mReplyToUserId, text);
        showBottomView(true);
    }

    @Override
    public void onAtTrigger() {
        AtUserActivity.startAtUserActivity(this);
    }

    /**
     * 付费文字点击
     *
     * @param position   动态位置
     * @param note       付费节点
     * @param amount     付费金额
     * @param view       文本view
     * @param canNotRead
     */
    @Override
    public void setSpanText(int position, int note, long amount, TextView view, boolean canNotRead) {
        position -= mHeaderAndFooterWrapper.getHeadersCount();
        initImageCenterPopWindow(position, position, amount,
                note, R.string.buy_pay_words_desc, false);
    }

    /**
     * 用户信息点击
     *
     * @param userInfoBean
     */
    @Override
    public void onUserInfoClick(UserInfoBean userInfoBean) {
        // 游客处理
        if (!TouristConfig.USER_INFO_CAN_LOOK && mPresenter.handleTouristControl()) {
            return;
        }
        // 广告
        if (userInfoBean.getUser_id() != null && userInfoBean.getUser_id().intValue() == -1) {
            return;
        }
        PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);
    }

    @Override
    public boolean onTopicClick(TopicListBean topicListBean) {
        if (topicListBean.getId().equals(getFromTopicId())) {
            mActivity.finish();
            return false;
        }
        return !topicListBean.getId().equals(mTopic.getId());
    }

    @Override
    public TopicListBean doNotShowThisTopic() {
        return new TopicListBean(getTopicId());
    }

    @Override
    public Long onTopicClickFrom() {
        return getTopicId();
    }

    /**
     * 图片点击
     *
     * @param holder
     * @param dynamicBean
     * @param position
     */
    @Override
    public void onImageClick(ViewHolder holder, DynamicDetailBeanV2 dynamicBean, int position) {
        if (!TouristConfig.DYNAMIC_BIG_PHOTO_CAN_LOOK && mPresenter.handleTouristControl()) {
            return;
        }
        // 广告
        if (dynamicBean.getFeed_from() == DEFAULT_ADVERT_FROM_TAG) {
            toAdvert(dynamicBean.getDeleted_at(), dynamicBean.getFeed_content());
            return;
        }
        int dynamicPosition = holder.getAdapterPosition() - mHeaderAndFooterWrapper
                .getHeadersCount();

        DynamicDetailBeanV2.ImagesBean img = dynamicBean.getImages().get(position);
        boolean canLook = !(img.isPaid() != null && !img.isPaid() && img.getType().equals(Toll
                .LOOK_TOLL_TYPE));
        if (!canLook) {
            initImageCenterPopWindow(dynamicPosition, position, dynamicBean
                            .getImages().get(position).getAmount(),
                    dynamicBean.getImages().get(position).getPaid_node(), R.string.buy_pay_desc,
                    true);
            return;
        }

        List<DynamicDetailBeanV2.ImagesBean> tasks = dynamicBean.getImages();
        List<ImageBean> imageBeanList = new ArrayList<>();
        ArrayList<AnimationRectBean> animationRectBeanArrayList
                = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (i >= 9) {
                continue;
            }
            DynamicDetailBeanV2.ImagesBean task = tasks.get(i);
            int id = UIUtils.getResourceByName("siv_" + i, "id", getContext());
            ImageView imageView = holder.getView(id);
            ImageBean imageBean = new ImageBean();
            imageBean.setImgUrl(task.getImgUrl());
            Toll toll = new Toll();
            toll.setPaid(task.isPaid());
            toll.setToll_money(task.getAmount());
            toll.setToll_type_string(task.getType());
            toll.setPaid_node(task.getPaid_node());
            imageBean.setToll(toll);
            imageBean.setDynamicPosition(dynamicPosition);
            imageBean.setFeed_id(dynamicBean.getId());
            imageBean.setWidth(task.getWidth());
            imageBean.setHeight(task.getHeight());
            imageBean.setListCacheUrl(task.getGlideUrl());
            imageBean.setStorage_id(task.getFile());
            imageBean.setImgMimeType(task.getImgMimeType());
            imageBeanList.add(imageBean);
            AnimationRectBean rect = AnimationRectBean.buildFromImageView(imageView);
            animationRectBeanArrayList.add(rect);
        }

        GalleryActivity.startToGallery(mActivity, position, imageBeanList, animationRectBeanArrayList);
    }

    /**
     * 点赞等菜单点击
     *
     * @param view
     * @param dataPosition
     * @param viewPosition
     */
    @Override
    public void onMenuItemClick(View view, int dataPosition, int viewPosition) {
        dataPosition -= mHeaderAndFooterWrapper.getHeadersCount();
        switch (viewPosition) {
            // 0 1 2 3 代表 view item 位置
            case 0:

                boolean isAdvert = mListDatas.get(dataPosition).getFeed_from() == DEFAULT_ADVERT_FROM_TAG;
                // 喜欢
                // 还未发送成功的动态列表不查看详情
                boolean isLoginAndDiggClick = !TouristConfig.DYNAMIC_CAN_DIGG && mPresenter.handleTouristControl();
                if (isAdvert || isLoginAndDiggClick || mListDatas.get(dataPosition).getId() == null || mListDatas.get
                        (dataPosition).getId() == 0) {
                    return;
                }
                handleLike(dataPosition);
                break;

            case 1:
                // 评论
                boolean isLogainAndCanComment = !TouristConfig.DYNAMIC_CAN_COMMENT && mPresenter.handleTouristControl();
                // 还未发送成功的动态列表不查看详情
                if (isLogainAndCanComment ||
                        mListDatas.get(dataPosition).getId() == null || mListDatas.get
                        (dataPosition).getId() == 0) {
                    return;
                }
                showBottomView(false);
                mIlvComment.setEtContentHint(getString(R.string.default_input_hint));
                mCurrentPostion = dataPosition;
                // 0 代表评论动态
                mReplyToUserId = 0;
                break;

            case 2:
                // 浏览
                onItemClick(null, null, dataPosition + mHeaderAndFooterWrapper.getHeadersCount());
                break;

            case 3:
                // 更多
                Bitmap shareBitMap = null;
                try {
                    ImageView imageView = layoutManager.findViewByPosition
                            (dataPosition + mHeaderAndFooterWrapper.getHeadersCount())
                            .findViewById(R.id.siv_0);
                    if (imageView == null) {
                        imageView = layoutManager.findViewByPosition
                                (dataPosition + mHeaderAndFooterWrapper.getHeadersCount())
                                .findViewById(R.id.thumb);
                    }
                    shareBitMap = ConvertUtils.drawable2BitmapWithWhiteBg(getContext(), imageView
                            .getDrawable(), R.mipmap.icon);
                } catch (Exception ignored) {
                }
                boolean isMine = AppApplication.getmCurrentLoginAuth() != null && mListDatas.get(dataPosition)
                        .getUser_id() == AppApplication.getMyUserIdWithdefault();
                initMyDynamicPopupWindow(mListDatas.get(dataPosition), dataPosition,
                        mListDatas.get(dataPosition).isHas_collect(), shareBitMap, isMine);

                break;
            default:
                onItemClick(null, null, dataPosition + mHeaderAndFooterWrapper.getHeadersCount());
        }
    }

    @Override
    public void onReSendClick(int position) {
        initReSendDynamicPopupWindow(position);
        mReSendDynamicPopWindow.show();
    }

    @Override
    public void share(int position) {
        position -= mHeaderAndFooterWrapper.getHeadersCount();
        if (mListDatas.get(position).getId() > 0) {
            Bitmap shareBitMap = getShareBitmap(position, R.id.thumb);
            mPresenter.shareDynamic(mListDatas.get(position), shareBitMap);
        }
        showBottomView(true);
    }

    @Override
    public void shareWihtType(int position, SHARE_MEDIA type) {
        position -= mHeaderAndFooterWrapper.getHeadersCount();
        if (mListDatas.get(position).getId() > 0) {
            mPresenter.shareDynamic(mListDatas.get(position), getShareBitmap(position, R.id.thumb),
                    type);
        }
    }

    @Override
    public void onCommentStateClick(DynamicCommentBean dynamicCommentBean, int position) {
        initReSendCommentPopupWindow(dynamicCommentBean, mListDatas.get(mPresenter
                .getCurrenPosiotnInDataList(dynamicCommentBean.getFeed_mark())).getId());
        mReSendCommentPopWindow.show();
    }

    @Override
    public void onMoreCommentClick(View view, DynamicDetailBeanV2 dynamicBean) {
        if (!TouristConfig.MORE_COMMENT_CAN_LOOK && mPresenter.handleTouristControl()) {
            return;
        }
        int dataPosition = mPresenter.getCurrenPosiotnInDataList(dynamicBean.getFeed_mark());
        DynamicDetailBeanV2 detailBeanV2 = mListDatas.get(dataPosition);
        boolean canNotLookWords = detailBeanV2.getPaid_node() != null &&
                !detailBeanV2.getPaid_node().isPaid()
                && detailBeanV2.getUser_id().intValue() != AppApplication.getMyUserIdWithdefault();
        if (canNotLookWords) {
            initImageCenterPopWindow(dataPosition, dataPosition,
                    detailBeanV2.getPaid_node().getAmount(),
                    detailBeanV2.getPaid_node().getNode(), R.string.buy_pay_words_desc, false);
            return;
        }
        int viewPosition = dataPosition + mHeaderAndFooterWrapper.getHeadersCount();
        goDynamicDetail(dataPosition, true, (ViewHolder) mRvList.findViewHolderForAdapterPosition(viewPosition));
    }

    @Override
    public void onCommentUserInfoClick(UserInfoBean userInfoBean) {
        onUserInfoClick(userInfoBean);
    }

    @Override
    public void onCommentContentClick(DynamicDetailBeanV2 dynamicBean, int position) {
        if (!TouristConfig.DYNAMIC_CAN_COMMENT && mPresenter.handleTouristControl()) {
            return;
        }
        mCurrentPostion = mPresenter.getCurrenPosiotnInDataList(dynamicBean.getFeed_mark());
        if (dynamicBean.getComments().get(position).getUser_id() == AppApplication
                .getMyUserIdWithdefault()) {
            initDeletCommentPopupWindow(dynamicBean, mCurrentPostion, position);
            mDeletCommentPopWindow.show();

        } else {
            showBottomView(false);
            mReplyToUserId = dynamicBean.getComments().get(position).getUser_id();
            String contentHint = getString(R.string.default_input_hint);
            if (dynamicBean.getComments().get(position).getUser_id() != AppApplication
                    .getMyUserIdWithdefault()) {
                contentHint = getString(R.string.reply, dynamicBean.getComments().get(position)
                        .getCommentUser().getName());
            }
            mIlvComment.setEtContentHint(contentHint);
        }
    }

    @Override
    public void onCommentContentLongClick(DynamicDetailBeanV2 dynamicBean, int position) {
        if (!TouristConfig.DYNAMIC_CAN_COMMENT && mPresenter.handleTouristControl()) {
            return;
        }
        mCurrentPostion = mPresenter.getCurrenPosiotnInDataList(dynamicBean.getFeed_mark());
        // 举报
        if (dynamicBean.getComments().get(position).getUser_id() != AppApplication
                .getMyUserIdWithdefault()) {
            ReportActivity.startReportActivity(mActivity, new ReportResourceBean(dynamicBean
                    .getComments().get
                            (position).getCommentUser(), dynamicBean.getComments().get
                    (position).getComment_id().toString(),
                    null, "", dynamicBean.getComments().get(position).getComment_content(),
                    ReportType.COMMENT));

        }
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        position -= mHeaderAndFooterWrapper.getHeadersCount();
        if (!TouristConfig.DYNAMIC_DETAIL_CAN_LOOK && mPresenter.handleTouristControl()) { // 游客处理
            return;
        }
        DynamicDetailBeanV2 detailBeanV2 = mListDatas.get(position);
        // 是广告
        if (detailBeanV2.getFeed_from() == DEFAULT_ADVERT_FROM_TAG) {
            toAdvert(detailBeanV2.getDeleted_at(), detailBeanV2.getFeed_content());
            return;
        }
        boolean canNotLookWords = detailBeanV2.getPaid_node() != null &&
                !detailBeanV2.getPaid_node().isPaid()
                && detailBeanV2.getUser_id().intValue() != AppApplication.getMyUserIdWithdefault();
        if (canNotLookWords) {
            initImageCenterPopWindow(position, position,
                    detailBeanV2.getPaid_node().getAmount(),
                    detailBeanV2.getPaid_node().getNode(), R.string.buy_pay_words_desc, false);
            return;
        }

        goDynamicDetail(position, false, (ViewHolder) holder);
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    protected void onEmptyViewClick() {
        myAppBarLayoutBehavoir.startRefreshing();
        mIvRefresh.setVisibility(View.VISIBLE);
        ((AnimationDrawable) mIvRefresh.getDrawable()).start();
        mPresenter.requestNetData(DEFAULT_PAGE_MAX_ID, false);
    }

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.iv_more, R.id.iv_follow, R.id.btn_send_post})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setLeftClick();
                break;
            case R.id.iv_share:
                mPresenter.shareTopic(mTopic, ConvertUtils.drawable2BitmapWithWhiteBg(mActivity, mIvTopicBg.getDrawable(), R.mipmap.icon));
                break;
            case R.id.iv_more:
                initEditeTopicPopWindow(mTopic);
                break;
            case R.id.iv_follow:
                mPresenter.handleTopicFollowState(mTopic.getId(), mTopic.has_followed());
                break;
            case R.id.btn_send_post:
                if (mTopic == null) {
                    return;
                }
                TopicListBean topicListBean = new TopicListBean();
                topicListBean.setId(mTopic.getId());
                topicListBean.setName(mTopic.getName());
                SelectDynamicTypeActivity.startSelectDynamicTypeActivity(mActivity, topicListBean);
                break;
            default:
        }
    }

    @Override
    public String getDynamicType() {
        return null;
    }

    @Override
    public void closeInputView() {
        if (mIlvComment.getVisibility() == View.VISIBLE) {
            mIlvComment.setVisibility(View.GONE);
            DeviceUtils.hideSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        }
        mVShadow.setVisibility(View.GONE);
    }

    @Override
    public void showNewDynamic(int position, boolean isForward) {
        if (position == -1) {
            // 添加一条新动态
            refreshData();
            // 回到顶部
            mRvList.scrollToPosition(0);
        } else {
            refreshData();
            if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
                ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
            }
        }
    }

    @Override
    protected void setToolBarLeftImage(int resImg, int color) {
        mIvBack.setImageDrawable(UIUtils.getCompoundDrawables(getContext(), resImg, color));
    }

    @Override
    protected void setToolBarRightImage(int resImg, int color) {
        mIvMore.setImageDrawable(UIUtils.getCompoundDrawables(getContext(), resImg, color));
    }

    @Override
    protected void setToolBarRightLeftImage(int resImg, int color) {
        mIvShare.setImageDrawable(UIUtils.getCompoundDrawables(getContext(), resImg, color));
    }

    @Override
    protected void setToolBarLeftImage(int resImg) {
        mIvBack.setImageDrawable(UIUtils.getCompoundDrawables(getContext(), resImg));
    }

    @Override
    protected void setToolBarRightImage(int resImg) {
        mIvMore.setImageDrawable(UIUtils.getCompoundDrawables(getContext(), resImg));
    }

    @Override
    protected void setToolBarRightLeftImage(int resImg) {
        mIvShare.setImageDrawable(UIUtils.getCompoundDrawables(getContext(), resImg));
    }

    private void initToolBar() {
        if (setUseStatusView()) {
            mCircleTitleLayout.setPadding(0, 0, 0, 0);
        }
    }

    private void toAdvert(String link, String title) {
        CustomWEBActivity.startToWEBActivity(getActivity(), link, title);
    }

    /**
     * @param dynamicPosition 动态位置
     * @param imagePosition   图片位置
     * @param amout           费用
     * @param note            支付节点
     * @param strRes          文字说明
     * @param isImage         是否是图片收费
     */
    private void initImageCenterPopWindow(final int dynamicPosition, final int imagePosition,
                                          long amout,
                                          final int note, int strRes, final boolean isImage) {

        mPayImagePopWindow = PayPopWindow.builder()
                .with(getActivity())
                .isWrap(true)
                .isFocus(true)
                .isOutsideTouch(true)
                .buildLinksColor1(R.color.themeColor)
                .buildLinksColor2(R.color.important_for_content)
                .contentView(R.layout.ppw_for_center)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .buildDescrStr(String.format(getString(strRes) + getString(R
                        .string.buy_pay_member), amout, mPresenter.getGoldName()))
                .buildLinksStr(getString(R.string.buy_pay_member))
                .buildTitleStr(getString(R.string.buy_pay))
                .buildItem1Str(getString(R.string.buy_pay_in))
                .buildItem2Str(getString(R.string.buy_pay_out))
                .buildMoneyStr(String.format(getString(R.string.buy_pay_integration), "" + amout))
                .buildCenterPopWindowItem1ClickListener(() -> {
                    mPresenter.payNote(dynamicPosition, imagePosition, note, isImage, null);
                    mPayImagePopWindow.hide();
                })
                .buildCenterPopWindowItem2ClickListener(() -> mPayImagePopWindow.hide())
                .buildCenterPopWindowLinkClickListener(new PayPopWindow
                        .CenterPopWindowLinkClickListener() {
                    @Override
                    public void onLongClick() {

                    }

                    @Override
                    public void onClicked() {

                    }
                })
                .build();
        mPayImagePopWindow.show();
    }

    @Override
    public void showBottomView(boolean isShow) {
        if (isShow) {
            mVShadow.setVisibility(View.GONE);
            mIlvComment.setVisibility(View.GONE);
            mIlvComment.clearFocus();
            mIlvComment.setSendButtonVisiable(false);
            DeviceUtils.hideSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        } else {
            mVShadow.setVisibility(View.VISIBLE);
            mIlvComment.setVisibility(View.VISIBLE);
            mIlvComment.getFocus();
            mIlvComment.setSendButtonVisiable(true);
            DeviceUtils.showSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        }
    }

    @Override
    public void hiddenChanged(boolean hidden) {

    }

    @Override
    public void showDeleteTipPopupWindow(DynamicDetailBeanV2 detailBeanV2) {
        showDeleteTipPopupWindow(getString(R.string.dynamic_list_delete_dynamic), ()
                -> {
            mPresenter.deleteDynamic(detailBeanV2, 0);
            showBottomView(true);
        }, true);
    }

    @Override
    public void onDestroyView() {
        dismissPop(mPayImagePopWindow);
        dismissPop(mDeletCommentPopWindow);
        dismissPop(mEditeTopicPopWindow);
        dismissPop(mReSendCommentPopWindow);
        dismissPop(mReSendDynamicPopWindow);
        if (showComment != null && !showComment.isUnsubscribed()) {
            showComment.unsubscribe();
        }
        super.onDestroyView();
    }

    /**
     * 喜欢
     *
     * @param dataPosition
     */
    private void handleLike(int dataPosition) {
        // 先更新界面，再后台处理
        mListDatas.get(dataPosition).setHas_digg(!mListDatas.get(dataPosition)
                .isHas_digg());
        mListDatas.get(dataPosition).setFeed_digg_count(mListDatas.get(dataPosition)
                .isHas_digg() ?
                mListDatas.get(dataPosition).getFeed_digg_count() + 1 : mListDatas.get
                (dataPosition).getFeed_digg_count() - 1);
        refreshData();
        mPresenter.handleLike(mListDatas.get(dataPosition).isHas_digg(),
                mListDatas.get(dataPosition).getId(), dataPosition);
    }

    /**
     * 初始化我的动态操作弹窗
     *
     * @param dynamicBean curent dynamic
     * @param position    curent dynamic postion
     * @param isMine      true ,dynamic is mine
     */
    private void initMyDynamicPopupWindow(final DynamicDetailBeanV2 dynamicBean, final int position,
                                          boolean isCollected, final Bitmap shareBitMap, boolean isMine) {
        List<UmengSharePolicyImpl.ShareBean> data;
        UmengSharePolicyImpl.ShareBean forward = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_forwarding, getString(R.string
                .share_forward), Share.FORWARD);
        UmengSharePolicyImpl.ShareBean sticktop = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_top, getString(R.string.share_sticktp),
                Share.STICKTOP);
        UmengSharePolicyImpl.ShareBean collect = new UmengSharePolicyImpl.ShareBean(isCollected ? R.mipmap.detail_share_clt_hl : R.mipmap
                .detail_share_clt, getString(isCollected ? R.string.dynamic_list_collected_dynamic : R
                .string.dynamic_list_collect_dynamic), Share.COLLECT);
        UmengSharePolicyImpl.ShareBean letter = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_sent, getString(R.string.share_letter),
                Share.LETTER);
        UmengSharePolicyImpl.ShareBean delete = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_det, getString(R.string.share_delete),
                Share.DELETE);
        UmengSharePolicyImpl.ShareBean report = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_report, getString(R.string
                .share_report), Share.REPORT);
        data = new ArrayList<>();

        boolean isAdvert = dynamicBean.getFeed_from() == DEFAULT_ADVERT_FROM_TAG;

        if (!isAdvert) {
            // 不是广告
            if (isMine) {
                Long feed_id = dynamicBean.getMaxId();
                boolean feedIdIsNull = feed_id == null || feed_id == 0;
                boolean isTop = dynamicBean.getTop() == DynamicDetailBeanV2.TOP_SUCCESS;
                if (!feedIdIsNull) {
                    data.add(forward);
                    data.add(letter);
                    data.add(collect);
                    if (!isTop) {
                        data.add(sticktop);
                    }
                }
                data.add(delete);

            } else {
                boolean hasDynamicDeletePermission = AppApplication.getmCurrentLoginAuth().getUserPermissionIds() != null &&
                        AppApplication.getmCurrentLoginAuth().getUserPermissionIds().contains(UserPermissions
                                .FEED_DELETE.value);
                data.add(forward);
                data.add(letter);
                data.add(hasDynamicDeletePermission ? delete : report);
                data.add(collect);
            }
        }

        mPresenter.shareDynamic(dynamicBean, shareBitMap, data);

    }

    /**
     * 初始化他人动态操作选择弹框
     *
     * @param dynamicBean curent dynamic
     * @param position    curent dynamic postion
     */
    private void initOtherDynamicPopupWindow(final DynamicDetailBeanV2 dynamicBean, final int
            position,
                                             boolean isCollected, final Bitmap shareBitmap) {

        boolean isAdvert = dynamicBean.getFeed_from() == DEFAULT_ADVERT_FROM_TAG;
        List<UmengSharePolicyImpl.ShareBean> data = null;
        if (!isAdvert) {
            UmengSharePolicyImpl.ShareBean forward = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_forwarding, getString(R.string.share_forward), Share.FORWARD);
            UmengSharePolicyImpl.ShareBean letter = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_sent, getString(R.string.share_letter), Share.LETTER);
            UmengSharePolicyImpl.ShareBean report = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_report, getString(R.string.share_report), Share.REPORT);
            UmengSharePolicyImpl.ShareBean collect = new UmengSharePolicyImpl.ShareBean(isCollected ? R.mipmap.detail_share_clt_hl : R.mipmap.detail_share_clt, getString(isCollected ? R.string.dynamic_list_collected_dynamic : R
                    .string.dynamic_list_collect_dynamic), Share.COLLECT);
            data = new ArrayList<>();
            data.add(forward);
            data.add(letter);
            data.add(report);
            data.add(collect);
        }
        mPresenter.shareDynamic(dynamicBean, shareBitmap, data);
    }

    /**
     * 初始化重发动态选择弹框
     */
    private void initReSendDynamicPopupWindow(final int position) {
        mReSendDynamicPopWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.resend))
                .item1Color(ContextCompat.getColor(getContext(), R.color.themeColor))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(() -> {
                    mReSendDynamicPopWindow.hide();
                    mListDatas.get(position).setState(DynamicDetailBeanV2.SEND_ING);
                    refreshData();
                    mPresenter.reSendDynamic(position);
                })
                .bottomClickListener(() -> mReSendDynamicPopWindow.hide())
                .build();
    }

    /**
     * 获取分享动态需要的图片
     *
     * @param position
     * @param id
     * @return
     */
    private Bitmap getShareBitmap(int position, int id) {
        Bitmap shareBitMap = null;
        try {
            ImageView imageView = layoutManager.findViewByPosition
                    (position + mHeaderAndFooterWrapper.getHeadersCount()).findViewById(id);
            shareBitMap = ConvertUtils.drawable2BitmapWithWhiteBg(getContext(), imageView
                    .getDrawable(), R.mipmap.icon);
        } catch (Exception e) {
        }
        return shareBitMap;
    }

    /**
     * 初始化重发评论选择弹框
     */
    private void initReSendCommentPopupWindow(final DynamicCommentBean commentBean, final long
            feed_id) {
        mReSendCommentPopWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.dynamic_list_resend_comment))
                .item1Color(ContextCompat.getColor(getContext(), R.color.themeColor))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(() -> {
                    mReSendCommentPopWindow.hide();
                    mPresenter.reSendComment(commentBean, feed_id);
                    showBottomView(true);
                })
                .bottomClickListener(() -> {
                    mReSendCommentPopWindow.hide();
                    showBottomView(true);
                })
                .build();
    }

    private void goDynamicDetail(int position, boolean isLookMoreComment, ViewHolder holder) {
        // 还未发送成功的动态列表不查看详情
        if (mListDatas.get(position).getId() == null || mListDatas.get(position).getId() <= 0) {
            return;
        }

        Intent intent = new Intent(getActivity(), DynamicDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DYNAMIC_DETAIL_DATA, mListDatas.get(position));
        bundle.putInt(DYNAMIC_DETAIL_DATA_POSITION, position);

        bundle.putBoolean(LOOK_COMMENT_MORE, isLookMoreComment);
        mPresenter.handleViewCount(mListDatas.get(position).getId(), position);

        if (isLookMoreComment) {
            ZhiyiVideoView.releaseAllVideos();
            intent.putExtras(bundle);
            startActivity(intent);
            return;
        }
        ZhiyiVideoView playView = null;
        try {
            playView = holder.getView(R.id.videoplayer);
        } catch (Exception ignore) {

        }

        if (playView != null && JZVideoPlayerManager.getFirstFloor() != null) {
            playView.mVideoFrom = mDynamicType;
            if (playView.currentState == ZhiyiVideoView.CURRENT_STATE_PLAYING) {
                playView.startButton.callOnClick();
            }
            bundle.putInt(DYNAMIC_VIDEO_STATE, playView.currentState);
            playView.textureViewContainer.removeView(JZMediaManager.textureView);
            playView.onStateNormal();
        }

        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 初始化评论删除选择弹框
     *
     * @param dynamicBean     curent dynamic
     * @param dynamicPositon  dynamic comment position
     * @param commentPosition current comment position
     */
    private void initDeletCommentPopupWindow(final DynamicDetailBeanV2 dynamicBean, final int
            dynamicPositon, final int commentPosition) {
        boolean sourceIsMine = AppApplication.getMyUserIdWithdefault() == dynamicBean.getUser_id();
        mDeletCommentPopWindow = ActionPopupWindow.builder()
                .item1Str(BuildConfig.USE_TOLL && dynamicBean.getState() == DynamicDetailBeanV2
                        .SEND_SUCCESS && !dynamicBean
                        .getComments().get(commentPosition).getPinned() && dynamicBean.getComments().get(commentPosition).getComment_id() != null ?
                        getString(sourceIsMine ? R.string.dynamic_list_stick_top_comment : R.string.dynamic_list_top_comment) : null)
                .item2Str(getString(R.string.dynamic_list_delete_comment))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(() -> {
                    mDeletCommentPopWindow.hide();
                    StickTopFragment.startSticTopActivity(this, StickTopFragment.TYPE_DYNAMIC, dynamicBean.getId(), dynamicBean
                            .getComments().get(commentPosition).getComment_id(), sourceIsMine);
                    showBottomView(true);
                })
                .item2ClickListener(() -> {
                    mDeletCommentPopWindow.hide();
                    showDeleteTipPopupWindow(getString(R.string.delete_comment), () -> {
                        mPresenter.deleteCommentV2(dynamicBean, dynamicPositon, dynamicBean
                                        .getComments().get(commentPosition).getComment_id() != null ? dynamicBean
                                        .getComments().get(commentPosition).getComment_id() : 0,
                                commentPosition);
                        showBottomView(true);
                    }, true);

                })
                .bottomClickListener(() -> {
                    mDeletCommentPopWindow.hide();
                    showBottomView(true);
                })
                .build();
    }

    private void initEditeTopicPopWindow(TopicDetailBean topic) {
        boolean isCreator = topic.getCreator_user_id() == AppApplication.getMyUserIdWithdefault();
        mEditeTopicPopWindow = ActionPopupWindow.builder()
                .with(mActivity)
                .item1Str(getString(isCreator ? R.string.edit : R.string.report))
                .item1ClickListener(() -> {
                    if (isCreator) {
                        CreateTopicActivity.startCreateTopicActivity(mActivity, topic);
                    } else {
                        String coverPath = TextUtils.isEmpty(topic.getLogoUrl())
                                ? topic.getLogo() != null ? topic.getLogo().getUrl() : "" : topic.getLogoUrl();
                        if (TextUtils.isEmpty(topic.getLogoUrl()) && topic.getLogo() == null) {
                            coverPath = null;
                        }
                        ReportActivity.startReportActivity(mActivity, new ReportResourceBean(topic.getCreator_user()
                                , topic.getId().toString(),
                                topic.getName(), coverPath, topic.getDesc(),
                                ReportType.TOPIC));
                    }
                    mEditeTopicPopWindow.dismiss();
                })
                .bottomStr(getString(R.string.cancel))
                .bottomClickListener(() -> mEditeTopicPopWindow.dismiss())
                .build();
        mEditeTopicPopWindow.show();
    }

    public Long getFromTopicId() {
        if (getArguments() != null) {
            return getArguments().getLong(FROM);
        }
        return null;
    }

    class UserListAdapter extends RankIndexUserAdapter {
        public UserListAdapter(Context context, int layoutId, List<UserInfoBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, UserInfoBean userInfoBean, int position) {
            super.convert(holder, userInfoBean, position);
            holder.itemView.setOnClickListener(view -> PersonalCenterFragment.startToPersonalCenter(mActivity, userInfoBean));
        }

        @Override
        protected boolean needResizeContainer() {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        mActivity.finish();
    }

}

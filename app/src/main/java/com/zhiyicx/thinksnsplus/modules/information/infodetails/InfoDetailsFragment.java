package com.zhiyicx.thinksnsplus.modules.information.infodetails;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.config.PayConfig;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.baseproject.share.Share;
import com.zhiyicx.baseproject.widget.DynamicDetailMenuView;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.BuildConfig;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.BaseWebLoad;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.InfoCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoListDataBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsCountBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.report.ReportResourceBean;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserActivity;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserListFragment;
import com.zhiyicx.thinksnsplus.modules.information.adapter.InfoDetailCommentEmptyItem;
import com.zhiyicx.thinksnsplus.modules.information.adapter.InfoDetailCommentItem;
import com.zhiyicx.thinksnsplus.modules.information.adapter.InfoDetailHeaderView;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.modules.report.ReportActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportType;
import com.zhiyicx.thinksnsplus.modules.wallet.reward.RewardType;
import com.zhiyicx.thinksnsplus.modules.wallet.sticktop.StickTopFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.utils.TSUerPerMissonUtil;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.zhiyicx.baseproject.widget.DynamicDetailMenuView.ITEM_POSITION_0;
import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.config.EventBusTagConfig.EVENT_UPDATE_LIST_DELETE;
import static com.zhiyicx.thinksnsplus.modules.home.message.messagecomment.MessageCommentAdapter.BUNDLE_SOURCE_ID;

/**
 * @Author Jliuer
 * @Date 2017/03/08
 * @Email Jliuer@aliyun.com
 * @Description 资讯详情
 */
public class InfoDetailsFragment extends TSListFragment<InfoDetailsConstract.Presenter,
        InfoCommentListBean> implements InfoDetailsConstract.View,
        BaseWebLoad.OnWebLoadListener, MultiItemTypeAdapter.OnItemClickListener,
        OnUserInfoClickListener, InfoDetailHeaderView.InfoHeaderEventListener {

    public static final String BUNDLE_INFO_TYPE = "info_type";
    public static final String BUNDLE_INFO = "info";

    @BindView(R.id.behavior_demo_coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.dd_dynamic_tool)
    DynamicDetailMenuView mDdDynamicTool;
    @BindView(R.id.tv_toolbar_center)
    TextView mTvToolbarCenter;
    @BindView(R.id.tv_toolbar_left)
    TextView mTvToolbarLeft;
    @BindView(R.id.tv_toolbar_right)
    TextView mTvToolbarRight;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ll_bottom_menu_container)
    ViewGroup mLLBottomMenuContainer;

    private InfoDetailHeaderView mInfoDetailHeader;

    private ActionPopupWindow mDeletCommentPopWindow;
    private ActionPopupWindow mDealInfoMationPopWindow;

    /**
     * 传入的资讯信息
     */
    private InfoListDataBean mInfoMation;

    private int mReplyUserId;// 被评论者的 id ,评论动态 id = 0

    /**
     * 打赏
     */
    private List<RewardsListBean> mRewardsListBeen = new ArrayList<>();
    private boolean mIsClose;
    private Subscription showComment;
    private InfoCommentListBean mInfoCommentListBean;

    public static InfoDetailsFragment newInstance(Bundle params) {
        InfoDetailsFragment fragment = new InfoDetailsFragment();
        fragment.setArguments(params);
        return fragment;
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
    protected boolean isNeedRefreshAnimation() {
        return false;
    }

    @Override
    protected int getstatusbarAndToolbarHeight() {
        return 0;
    }

    @Override
    protected float getItemDecorationSpacing() {
        return 0;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected MultiItemTypeAdapter getAdapter() {
        MultiItemTypeAdapter multiItemTypeAdapter = new MultiItemTypeAdapter<>(getActivity(),
                mListDatas);
        InfoDetailCommentItem infoDetailCommentItem = new InfoDetailCommentItem(new
                ItemOnCommentListener());
        infoDetailCommentItem.setOnUserInfoClickListener(this);
        multiItemTypeAdapter.addItemViewDelegate(infoDetailCommentItem);
        multiItemTypeAdapter.addItemViewDelegate(new InfoDetailCommentEmptyItem());
        multiItemTypeAdapter.setOnItemClickListener(this);
        return multiItemTypeAdapter;
    }

    @Override
    public void updateReWardsView(RewardsCountBean rewardsCountBean, List<RewardsListBean> datas) {
        RewardsCountBean rewardsCountBean1 = rewardsCountBean;
        this.mRewardsListBeen.clear();
        this.mRewardsListBeen.addAll(datas);
        if (rewardsCountBean1 != null && !TextUtils.isEmpty(rewardsCountBean1.getAmount())) {
            rewardsCountBean1.setAmount(PayConfig.realCurrency2GameCurrencyStr(Double.parseDouble(rewardsCountBean1.getAmount()), mPresenter
                    .getRatio()));
        }
        try {
            mSystemConfigBean = mPresenter.getSystemConfigBean();
            if (mSystemConfigBean.getSite().getReward().hasStatus()) {
                mInfoDetailHeader.updateReward(mInfoMation.getId(), mRewardsListBeen, rewardsCountBean1, RewardType.INFO, mPresenter.getGoldName());
            } else {
                mInfoDetailHeader.setReWardViewVisible(View.GONE);
            }
        } catch (NullPointerException e) {
            mInfoDetailHeader.setReWardViewVisible(GONE);
        }

    }

    @Override
    public void updateInfoHeader(InfoListDataBean infoDetailBean) {
        mCoordinatorLayout.setEnabled(true);
        this.mInfoMation = infoDetailBean;
        mInfoDetailHeader.setDetail(infoDetailBean);
        mInfoDetailHeader.getReWardView().setOnRewardsClickListener(() -> {
            boolean isMine = infoDetailBean.getUser_id() == AppApplication.getMyUserIdWithdefault();
            if (isMine) {
                showAuditTipPopupWindow(getString(R.string.can_not_reward_self));
            }
            return !isMine;
        });
        mInfoDetailHeader.updateDigList(infoDetailBean);
        mInfoDetailHeader.setRelateInfo(infoDetailBean);
        onNetResponseSuccess(infoDetailBean.getCommentList(), false);
    }

    /**
     * 更新关注状态
     * @param followFansBean
     */
    @Subscriber(tag = EventBusTagConfig.EVENT_FOLLOW_AND_CANCEL_FOLLOW)
    public void upDateFollowState(UserInfoBean followFansBean) {
        mInfoDetailHeader.updateUserFollow(followFansBean.getFollower());
    }

    @Override
    public void clickUserInfo(UserInfoBean userInfoBean) {
        PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);
    }

    @Override
    public void userFollowClick(boolean isChecked) {
        mPresenter.handleFollowUser(mInfoMation.getAuthorUserInfoBean());
    }

    @Override
    public void deleteInfo(boolean deleting, boolean success, String message) {
        if (deleting) {
//            showSnackLoadingMessage(getString(R.string.info_deleting));
        } else {
            if (success) {
                showSnackSuccessMessage(message);
                EventBus.getDefault().post(mInfoMation, EVENT_UPDATE_LIST_DELETE);
                mIsClose = true;
            } else {
                showSnackErrorMessage(message);
            }
        }
    }

    @Override
    public void upDateFollowFansState(boolean follower) {
        mInfoDetailHeader.updateUserFollow(follower);
        refreshData();
    }

    @Override
    protected void snackViewDismissWhenTimeOut(Prompt prompt) {
        super.snackViewDismissWhenTimeOut(prompt);
        if (getActivity() != null && Prompt.SUCCESS == prompt && mIsClose) {
            getActivity().finish();
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mIlvComment.setEtContentHint(getString(R.string.default_input_hint));
        mInfoMation = (InfoListDataBean) getArguments().getSerializable(BUNDLE_INFO);

        if (mInfoMation == null) {
            mInfoMation = new InfoListDataBean();
            Long ids = getArguments().getLong(BUNDLE_SOURCE_ID);
            mInfoMation.setId(ids);
        }

        mTvToolbarCenter.setVisibility(View.VISIBLE);
        mTvToolbarCenter.setText(getString(R.string.info_details));
        initHeaderView();
        initBottomToolStyle();
        initBottomToolListener();
        initListener();
        mInfoMation.setIs_collection_news(mPresenter.isCollected() ? 1 : 0);
        mInfoMation.setIs_digg_news(mPresenter.isDiged() ? 1 : 0);
        setDigg(mPresenter.isDiged());

        // 投稿中的资讯隐藏底部操作以及打赏
        mDdDynamicTool.setVisibility(mInfoMation.getAudit_status() == 0 ? View.VISIBLE : View.GONE);
        mInfoDetailHeader.setInfoReviewIng(mInfoMation.getAudit_status() == 0 ? View.VISIBLE : View.GONE);
        mRvList.setItemAnimator(null);
    }

    @Override
    public void loadAllError() {
        setLoadViewHolderImag(R.mipmap.img_default_internet);
        mTvToolbarRight.setVisibility(View.GONE);
        mTvToolbarCenter.setVisibility(View.GONE);
        showLoadViewLoadError();
    }

    @Override
    public void infoMationHasBeDeleted() {
        setLoadViewHolderImag(R.mipmap.img_default_delete);
        mTvToolbarRight.setVisibility(View.GONE);
        mTvToolbarCenter.setVisibility(View.GONE);
        showLoadViewLoadErrorDisableClick();
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_info_detail;
    }

    @Override
    protected boolean setUseCenterLoading() {
        return true;
    }

    @Override
    public Long getNewsId() {
        return (long) mInfoMation.getId();
    }

    @Override
    public int getInfoType() {
        return Integer.valueOf(getArguments().getString(BUNDLE_INFO_TYPE, "-100"));
    }

    @Override
    public InfoListDataBean getCurrentInfo() {
        return mInfoMation;
    }

    @Override
    public void setCollect(boolean isCollected) {
//        mDdDynamicTool.setItemIsChecked(isCollected, ITEM_POSITION_0);
    }

    @Override
    public void setDigg(boolean isDigged) {
        mDdDynamicTool.setItemIsChecked(isDigged, ITEM_POSITION_0);
        if (mInfoMation.getDigList() != null) {
            mInfoDetailHeader.updateDigList(mInfoMation);
        }
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<InfoCommentListBean> data, boolean isLoadMore) {
        if (!isLoadMore) {
            if (data.isEmpty()) { // 空白展位图
                InfoCommentListBean emptyData = new InfoCommentListBean();
                data.add(emptyData);
            }
        }
        super.onNetResponseSuccess(data, isLoadMore);
        closeLoadingView();
    }

    @Override
    public void onResponseError(Throwable throwable, boolean isLoadMore) {
        setLoadViewHolderImag(R.mipmap.img_default_internet);
        showLoadViewLoadError();
        hideRefreshState(isLoadMore);
    }

    @Override
    protected void setLoadingViewHolderClick() {
        super.setLoadingViewHolderClick();
        onEmptyViewClick();
    }

    @Override
    public void onSendClick(View v, String text) {
        DeviceUtils.hideSoftKeyboard(getContext(), v);
        mIlvComment.setVisibility(View.GONE);
        mVShadow.setVisibility(View.GONE);
        mPresenter.sendComment(mReplyUserId, text);
        mLLBottomMenuContainer.setVisibility(View.VISIBLE);
        scrollToCommentTop();
    }

    private void scrollToCommentTop() {
        mRvList.post(() -> ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(0, -mInfoDetailHeader.scrollCommentToTop()));
    }

    @Override
    public void refreshData() {
        super.refreshData();
        mInfoMation.setComment_count(TextUtils.isEmpty(mListDatas.get(0).getComment_content()) ? 0 : mListDatas.size());
        mInfoDetailHeader.updateCommentView(mInfoMation);
    }

    private void initHeaderView() {
        mInfoDetailHeader = new InfoDetailHeaderView(getContext(), mPresenter.getAdvert());
//        mInfoDetailHeader.setWebLoadListener(this);
        mInfoDetailHeader.setInfoHeaderEventListener(this);
        mHeaderAndFooterWrapper.addHeaderView(mInfoDetailHeader.getInfoDetailHeader());
        View mFooterView = new View(getContext());
        mFooterView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mHeaderAndFooterWrapper.addFootView(mFooterView);
        mRvList.setAdapter(mHeaderAndFooterWrapper);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }

    private void initBottomToolStyle() {
        mDdDynamicTool.setButtonText(new int[]{R.string.dynamic_like, R.string.comment,
                R.string.share, R.string.more});
        mDdDynamicTool.setImageNormalResourceIds(new int[]{
                R.mipmap.home_ico_good_normal, R.mipmap.home_ico_comment_normal,
                R.mipmap.detail_ico_share_normal, R.mipmap.home_ico_more});

        mDdDynamicTool.setImageCheckedResourceIds(new int[]{
                R.mipmap.home_ico_good_high, R.mipmap.home_ico_comment_normal,
                R.mipmap.detail_ico_share_normal, R.mipmap.home_ico_more});
        mDdDynamicTool.setData();
    }

    private void initBottomToolListener() {
        mDdDynamicTool.setItemOnClick((parent, v, position) -> {
            mDdDynamicTool.getTag(R.id.view_data);
            switch (position) {
                case DynamicDetailMenuView.ITEM_POSITION_0:// 点赞
                    mPresenter.handleLike(!mInfoMation.getHas_like(),
                            mInfoMation.getId() + "");
                    break;
                case DynamicDetailMenuView.ITEM_POSITION_1:// 评论
                    showCommentView();
                    String contentHint = getString(R.string.default_input_hint);
                    mIlvComment.setEtContentHint(contentHint);
                    mReplyUserId = 0;
                    break;
                case DynamicDetailMenuView.ITEM_POSITION_2:// 分享
                    Bitmap bitmap = FileUtils.readImgFromFile(getActivity(), "info_share.jpg");
                    UmengSharePolicyImpl.ShareBean forward = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_forwarding, getString(R
                            .string.share_forward), Share.FORWARD);
                    UmengSharePolicyImpl.ShareBean letter = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_sent, getString(R.string
                            .share_letter), Share.LETTER);

                    List<UmengSharePolicyImpl.ShareBean> data = new ArrayList<>();
                    data.add(forward);
                    data.add(letter);

                    mPresenter.shareInfo(bitmap, data);
                    break;
                case DynamicDetailMenuView.ITEM_POSITION_3:// 更多
                    initDealInfoMationPopupWindow(mInfoMation, mInfoMation.getHas_collect());
                    mDealInfoMationPopWindow.show();
                    break;
                default:
                    break;
            }
        });
    }

    private void initListener() {
        mCoordinatorLayout.setEnabled(false);
        RxView.clicks(mTvToolbarLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> getActivity().finish());
        RxView.clicks(mTvToolbarRight)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                });
        RxView.clicks(mVShadow)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    mIlvComment.setVisibility(View.GONE);
                    mIlvComment.clearFocus();
                    DeviceUtils.hideSoftKeyboard(getActivity(), mIlvComment.getEtContent());
                    mLLBottomMenuContainer.setVisibility(View.VISIBLE);
                    mVShadow.setVisibility(View.GONE);

                });


    }

    public void showCommentView() {
        mLLBottomMenuContainer.setVisibility(View.INVISIBLE);
        // 评论
        mIlvComment.setVisibility(View.VISIBLE);
        mIlvComment.setSendButtonVisiable(true);
        mIlvComment.getFocus();
        mVShadow.setVisibility(View.VISIBLE);
        DeviceUtils.showSoftKeyboard(getActivity(), mIlvComment.getEtContent());
    }

    /**
     * 初始化评论删除选择弹框
     */
    private void initDeleteCommentPopupWindow(final InfoCommentListBean data) {
        boolean sourceIsMine = AppApplication.getMyUserIdWithdefault() == getCurrentInfo().getUser_id();
        mDeletCommentPopWindow = ActionPopupWindow.builder()
                .item1Str(BuildConfig.USE_TOLL && data.getId() != -1L && !data.getPinned() ?
                        getString(sourceIsMine ? R.string.dynamic_list_stick_top_comment : R.string.dynamic_list_top_comment) : null)
                .item2Str(getString(R.string.dynamic_list_delete_comment))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(() -> {
                    // 跳转置顶页面
                    mDeletCommentPopWindow.hide();
                    mInfoCommentListBean = data;
                    StickTopFragment.startSticTopActivity(this, StickTopFragment.TYPE_INFO, mInfoMation.getId(), data.getId(), sourceIsMine);
                    mDeletCommentPopWindow.hide();
                })
                .item2ClickListener(() -> {
                    mDeletCommentPopWindow.hide();
                    showDeleteTipPopupWindow(getString(R.string.delete_comment), () -> {
                        mPresenter.deleteComment(data);
                    }, true);

                })
                .bottomClickListener(() -> mDeletCommentPopWindow.hide())
                .build();
    }

    private void initDealInfoMationPopupWindow(final InfoListDataBean infoMation, boolean isCollected) {
        boolean isMine = infoMation.getUser_id() == AppApplication.getmCurrentLoginAuth().getUser_id();
        boolean isManager = TSUerPerMissonUtil.getInstance().canDeleteInfo();
        mDealInfoMationPopWindow = ActionPopupWindow.builder()
                .item1Str(isMine ? getString(R.string.info_apply_for_top) : "")
                .item2Str((isMine || isManager) ? getString(R.string.info_delete) : getString(isCollected ? R.string.dynamic_list_uncollect_dynamic : R.string
                        .dynamic_list_collect_dynamic))
                // 是我的，或者是广告就没举报
                .item3Str((isMine || isManager)? "" : getString(R.string.report))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item3ClickListener(() -> {                    // 举报资讯
                    String img = "";
                    if (infoMation.getImage() != null) {
                        img = ImageUtils.imagePathConvertV2(infoMation.getImage().getId(), getResources()
                                        .getDimensionPixelOffset(R.dimen.report_resource_img), getResources()
                                        .getDimensionPixelOffset(R.dimen.report_resource_img),
                                ImageZipConfig.IMAGE_80_ZIP);
                    } else {
                        int id = RegexUtils.getImageIdFromMarkDown(MarkdownConfig.IMAGE_FORMAT, infoMation.getContent());
                        if (id > 0) {
                            img = ImageUtils.imagePathConvertV2(id, getResources()
                                            .getDimensionPixelOffset(R.dimen.report_resource_img), getResources()
                                            .getDimensionPixelOffset(R.dimen.report_resource_img),
                                    ImageZipConfig.IMAGE_80_ZIP);
                        }
                    }
                    UserInfoBean userInfoBean = new UserInfoBean();
                    userInfoBean.setUser_id(infoMation.getUser_id());
                    ReportActivity.startReportActivity(mActivity, new ReportResourceBean(userInfoBean, String.valueOf(infoMation.getId()),
                            infoMation.getTitle(), img, infoMation.getSubject(), ReportType.INFO));
                    mDealInfoMationPopWindow.hide();
                })
                .item2ClickListener(() -> {
                    // 收藏
                    // 如果是自己发布的，则不能收藏只能删除
                    if (isMine || isManager) {
                        showDeleteTipPopupWindow(getString(R.string.delete_info), () -> {
                            mPresenter.deleteInfo();
                        }, true);
                    } else {
                        mPresenter.handleCollect(!infoMation.getHas_collect(),
                                mInfoMation.getId() + "");
                    }
                    mDealInfoMationPopWindow.hide();
                })
                // 申请置顶
                .item1ClickListener(() -> {
                    if (infoMation.is_pinned()) {
                        showSnackErrorMessage(getString(R.string.info_alert_reapply_for_top));
                    } else {
                        StickTopFragment.startSticTopActivity(getActivity(), StickTopFragment.TYPE_INFO, infoMation.getId());
                    }
                    mDealInfoMationPopWindow.hide();
                })
                .bottomClickListener(() -> mDealInfoMationPopWindow.hide())
                .build();
    }

    @Override
    public void onLoadFinish() {
        closeLoadingView();
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        comment(position);
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        goReportComment(position);
        return true;
    }

    @Override
    public void onUserInfoClick(UserInfoBean userInfoBean) {
        PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);
    }


    class ItemOnCommentListener implements InfoDetailCommentItem.OnCommentItemListener {
        @Override
        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
            comment(position);
        }

        @Override
        public void onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
            goReportComment(position);
        }

        @Override
        public void onUserInfoClick(UserInfoBean userInfoBean) {
            PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);
        }
    }

    /**
     * 评论
     *
     * @param position
     */
    private void comment(int position) {
        position = position - mHeaderAndFooterWrapper.getHeadersCount();// 减去 header
        InfoCommentListBean infoCommentListBean = mListDatas.get(position);
        if (infoCommentListBean != null && !TextUtils.isEmpty(infoCommentListBean.getComment_content())) {
            if (infoCommentListBean.getUser_id() == AppApplication.getMyUserIdWithdefault()) {// 自己的评论
                if (mListDatas.get(position).getId() != -1) {
                    initDeleteCommentPopupWindow(infoCommentListBean);
                    mDeletCommentPopWindow.show();
                }
//                else {
//
//                    return;
//                }
            } else {
                mReplyUserId = (int) infoCommentListBean.getUser_id();
                showCommentView();
                String contentHint = getString(R.string.default_input_hint);
                if (infoCommentListBean.getReply_to_user_id() != mInfoMation.getId()) {
                    contentHint = getString(R.string.reply, infoCommentListBean.getFromUserInfoBean().getName());
                }
                mIlvComment.setEtContentHint(contentHint);
            }
        }
    }

    /**
     * 举报
     *
     * @param position
     */
    private void goReportComment(int position) {
        // 减去 header
        position = position - mHeaderAndFooterWrapper.getHeadersCount();
        // 举报
        if (mListDatas.get(position).getUser_id() != AppApplication.getMyUserIdWithdefault()) {
            ReportActivity.startReportActivity(mActivity, new ReportResourceBean(mListDatas.get(position).getFromUserInfoBean(), mListDatas.get
                    (position).getId().toString(),
                    null, "", mListDatas.get(position).getComment_content(), ReportType.COMMENT));

        } else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RewardType.INFO.id) {
                mPresenter.reqReWardsData(mInfoMation.getId().intValue());
            }
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
            if (requestCode == StickTopFragment.PINNED) {
                if (mInfoCommentListBean != null) {
                    mInfoCommentListBean.setPinned(true);
                    refreshData();
                }
            }
        }
    }

    @Override
    public void onAtTrigger() {
        AtUserActivity.startAtUserActivity(this);
    }

    @Override
    public void onPause() {
        mInfoDetailHeader.getContentWebView().onPause();
        mInfoDetailHeader.getContentWebView().pauseTimers();
        mInfoDetailHeader.getContentSubWebView().onPause();
        mInfoDetailHeader.getContentSubWebView().pauseTimers();
        super.onPause();

    }

    @Override
    public void onResume() {
        mInfoDetailHeader.getContentWebView().onResume();
        mInfoDetailHeader.getContentWebView().resumeTimers();
        mInfoDetailHeader.getContentSubWebView().onResume();
        mInfoDetailHeader.getContentSubWebView().resumeTimers();
        mPresenter.updatePublishUserFromLocal();

        super.onResume();

    }

    @Override
    public void onDestroyView() {
        mInfoDetailHeader.destroyedWeb();
        dismissPop(mDeletCommentPopWindow);
        dismissPop(mDealInfoMationPopWindow);
        if (showComment != null && !showComment.isUnsubscribed()) {
            showComment.unsubscribe();
        }
        super.onDestroyView();
    }
}

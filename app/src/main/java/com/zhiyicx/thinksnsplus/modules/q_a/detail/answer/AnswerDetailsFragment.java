package com.zhiyicx.thinksnsplus.modules.q_a.detail.answer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.widget.DynamicDetailMenuView;
import com.zhiyicx.baseproject.widget.EmptyView;
import com.zhiyicx.baseproject.widget.InputPasswordView;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.baseproject.widget.popwindow.PayPopWindow;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.BaseWebLoad;
import com.zhiyicx.thinksnsplus.data.beans.AnswerCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.AnswerInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsCountBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.report.ReportResourceBean;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserActivity;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserListFragment;
import com.zhiyicx.thinksnsplus.modules.password.findpassword.FindPasswordActivity;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.modules.q_a.answer.EditeAnswerDetailFragment;
import com.zhiyicx.thinksnsplus.modules.q_a.answer.PublishType;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.adapter.AnswerDetailCommentEmptyItem;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.adapter.AnswerDetailCommentItem;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.adapter.AnswerDetailHeaderView;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportType;
import com.zhiyicx.thinksnsplus.modules.wallet.reward.RewardType;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.utils.TSUerPerMissonUtil;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static android.app.Activity.RESULT_OK;
import static com.zhiyicx.baseproject.widget.DynamicDetailMenuView.ITEM_POSITION_0;
import static com.zhiyicx.baseproject.widget.DynamicDetailMenuView.ITEM_POSITION_4;
import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.config.EventBusTagConfig.EVENT_UPDATE_ANSWER_LIST_DELETE;
import static com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity.BUNDLE_QUESTION_BEAN;

/**
 * @Author Jliuer
 * @Date 2017/03/08
 * @Email Jliuer@aliyun.com
 * @Description 回答详情
 */
public class AnswerDetailsFragment extends TSListFragment<AnswerDetailsConstract.Presenter,
        AnswerCommentListBean> implements AnswerDetailsConstract.View,
        AnswerDetailHeaderView.AnswerHeaderEventListener,
        BaseWebLoad.OnWebLoadListener, MultiItemTypeAdapter
                .OnItemClickListener {

    public static final String BUNDLE_SOURCE_ID = "source_id";
    public static final String BUNDLE_ANSWER = "answer";

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
    @BindView(R.id.answer_empty_view)
    protected EmptyView mAnswerEmptyView;


    private AnswerDetailHeaderView mAnswerDetailHeaderView;

    private ActionPopupWindow mDeletCommentPopWindow;
    private ActionPopupWindow mDealInfoMationPopWindow;

    private AnswerInfoBean mAnswerInfoBean;

    private int mReplyUserId;// 被评论者的 id ,评论动态 id = 0

    /**
     * 打赏
     */
    private List<RewardsListBean> mRewardsListBeen = new ArrayList<>();
    private RewardsCountBean mRewardsCountBean;
    private Subscription showComment;

    private PayPopWindow mPayWatchPopWindow;
    private boolean isMine;
    public static AnswerDetailsFragment newInstance(Bundle params) {
        AnswerDetailsFragment fragment = new AnswerDetailsFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    protected boolean setUseInputCommentView() {
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
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean setUseCenterLoading() {
        return true;
    }

    @Override
    protected MultiItemTypeAdapter getAdapter() {
        MultiItemTypeAdapter multiItemTypeAdapter = new MultiItemTypeAdapter<>(getActivity(),
                mListDatas);
        AnswerDetailCommentItem answerDetailCommentItem = new AnswerDetailCommentItem(new
                ItemOnCommentListener());
        multiItemTypeAdapter.addItemViewDelegate(answerDetailCommentItem);
        multiItemTypeAdapter.addItemViewDelegate(new AnswerDetailCommentEmptyItem());
        multiItemTypeAdapter.setOnItemClickListener(this);
        return multiItemTypeAdapter;
    }

    @Override
    public void updateReWardsView(RewardsCountBean rewardsCountBean, List<RewardsListBean> datas) {
        this.mRewardsCountBean = rewardsCountBean;
        this.mRewardsListBeen.clear();
        this.mRewardsListBeen.addAll(datas);
        try {
            mSystemConfigBean = mPresenter.getSystemConfigBean();
            if (mSystemConfigBean.getSite().getReward().hasStatus()) {
                mAnswerDetailHeaderView.updateReward(mAnswerInfoBean.getId(), mRewardsListBeen,
                        mRewardsCountBean, RewardType.QA_ANSWER, mPresenter.getGoldName());
            } else {
                mAnswerDetailHeaderView.setReWardViewVisible(View.GONE);
            }
        } catch (NullPointerException e) {
            mAnswerDetailHeaderView.setReWardViewVisible(View.GONE);
        }

    }

    @Override
    protected float getItemDecorationSpacing() {
        return 0;
    }

    @Override
    public void updateAnswerHeader(AnswerInfoBean answerInfoBean, boolean isLoadMore) {
        if (answerInfoBean == null) {
            loadAllError();
            return;
        }

        // 需要围观的答案详情，body 字段为空
        if (TextUtils.isEmpty(answerInfoBean.getBody())) {
            initPayWindow(answerInfoBean.getId());
            return;
        }

        mTvToolbarCenter.setText(answerInfoBean.getQuestion().getSubject());
        mAnswerInfoBean = answerInfoBean;
        isMine =  mAnswerInfoBean.getUser_id() != null && mAnswerInfoBean.getUser_id() == AppApplication.getMyUserIdWithdefault();
        if (mDdDynamicTool != null) {
            mDdDynamicTool.showAnswerTool(isMine);
        }
        initBottomToolStyle();
        mCoordinatorLayout.setEnabled(true);
        mAnswerDetailHeaderView.setDetail(answerInfoBean);
        mAnswerDetailHeaderView.getReWardView().setOnRewardsClickListener(() -> {

            if (isMine) {
                showAuditTipPopupWindow(getString(R.string.can_not_reward_self));
            }
            return !isMine;
        });
        setDigg(answerInfoBean.getLiked());
        mAnswerDetailHeaderView.updateDigList(answerInfoBean);
        onNetResponseSuccess(answerInfoBean.getCommentList(), isLoadMore);
    }

    @Override
    public void deleteAnswer() {
        EventBus.getDefault().post(mAnswerInfoBean, EVENT_UPDATE_ANSWER_LIST_DELETE);
        getActivity().finish();
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mEmptyView = mAnswerEmptyView;
        mIlvComment.setEtContentHint(getString(R.string.default_input_hint));
        mAnswerInfoBean = (AnswerInfoBean) getArguments().getSerializable(BUNDLE_ANSWER);
        if (mAnswerInfoBean == null) {
            mAnswerInfoBean = new AnswerInfoBean();
            Long ids = getArguments().getLong(BUNDLE_SOURCE_ID);
            mAnswerInfoBean.setId(ids);
            mAnswerInfoBean.setQuestion_id(1L);
        }

        mTvToolbarCenter.setVisibility(View.VISIBLE);
        mTvToolbarCenter.setText(getString(R.string.qa_title_answer_detail));
        initHeaderView();
        initBottomToolStyle();
        initBottomToolListener();
        initListener();
    }

    @Override
    public void onSureClick(View v, String text, InputPasswordView.PayNote payNote) {
        mPresenter.payForOnlook(payNote.id, payNote.psd);
    }

    @Override
    public void onForgetPsdClick() {
        showInputPsdView(false);
        startActivity(new Intent(getActivity(), FindPasswordActivity.class));
        mActivity.finish();
    }

    @Override
    public void onCancle() {
        dismissSnackBar();
        mPresenter.canclePay();
        showInputPsdView(false);
        mActivity.finish();
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
    protected int getBodyLayoutId() {
        return R.layout.fragment_info_detail;
    }

    @Override
    public Long getAnswerId() {
        return mAnswerInfoBean.getId();
    }

    @Override
    public int getInfoType() {
        return 1;
    }

    @Override
    protected Long getMaxId(@NotNull List<AnswerCommentListBean> data) {
        return data.get(data.size() - 1).getId();
    }

    @Override
    public AnswerInfoBean getAnswerInfo() {
        return mAnswerInfoBean;
    }

    @Override
    public void setCollect(boolean isCollected) {

    }

    @Override
    public void setDigg(boolean isDigged) {
        mDdDynamicTool.setItemIsChecked(isDigged, ITEM_POSITION_0);
        if (mAnswerInfoBean.getLikes() != null) {
            mAnswerDetailHeaderView.updateDigList(mAnswerInfoBean);
        }
    }

    @Override
    public void setAdopt(boolean isAdopt) {
        mDdDynamicTool.setItemIsChecked(isAdopt, ITEM_POSITION_4);
        mDdDynamicTool.setButtonText(new int[]{R.string.dynamic_like, R.string.comment,
                R.string.share, R.string.more, isAdopt? R.string
                .qa_question_answer_adopt :
                R.string.qa_question_answer_adop});
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<AnswerCommentListBean> data, boolean isLoadMore) {
        if (!isLoadMore) {
            if (data.isEmpty()) { // 空白展位图
                AnswerCommentListBean emptyData = new AnswerCommentListBean();
                data.add(emptyData);
            }
        }
        super.onNetResponseSuccess(data, isLoadMore);
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

    @Override
    protected boolean setUseInputPsdView() {
        return true;
    }

    @Override
    protected boolean setUseShadowView() {
        return true;
    }

    @Override
    protected void onShadowViewClick() {
        showInputPsdView(false);
    }

    private void scrollToCommentTop() {
        mRvList.post(() -> ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(0, -mAnswerDetailHeaderView.scrollCommentToTop()));
    }

    @Override
    public void onAtTrigger() {
        AtUserActivity.startAtUserActivity(this);
    }

    @Override
    public void refreshData() {
        super.refreshData();
        mAnswerDetailHeaderView.updateCommentView(mAnswerInfoBean);
    }

    private void initHeaderView() {
        mAnswerDetailHeaderView = new AnswerDetailHeaderView(getContext(), null);
        mAnswerDetailHeaderView.setAnswerHeaderEventListener(this);
        mAnswerDetailHeaderView.setWebLoadListener(this);
        mHeaderAndFooterWrapper.addHeaderView(mAnswerDetailHeaderView.getAnswerDetailHeader());
        View mFooterView = new View(getContext());
        mFooterView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, 1));
        mHeaderAndFooterWrapper.addFootView(mFooterView);
        mRvList.setAdapter(mHeaderAndFooterWrapper);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }

    @Override
    public void clickUserInfo(UserInfoBean userInfoBean) {
        PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);
    }

    @Override
    public void userFollowClick(boolean isChecked) {
        mPresenter.handleFollowUser(mAnswerInfoBean.getUser());
    }

    @Override
    public void upDateFollowFansState(boolean isFollowed) {
        mAnswerDetailHeaderView.updateUserFollow(isFollowed);
    }

    @Override
    public void finish() {
        mActivity.finish();
    }

    private void initBottomToolStyle() {
        mDdDynamicTool.setButtonText(new int[]{R.string.dynamic_like, R.string.comment,
                R.string.share, R.string.more, mAnswerInfoBean.getQuestion() != null && mAnswerInfoBean.getQuestion().getHas_adoption() ? R.string
                .qa_question_answer_adopt :
                R.string.qa_question_answer_adop});
        mDdDynamicTool.setImageNormalResourceIds(new int[]{
                R.mipmap.home_ico_good_normal, R.mipmap.home_ico_comment_normal,
                R.mipmap.detail_ico_share_normal, R.mipmap.home_ico_more, R.mipmap.detail_ico_adopt_nomal});

        mDdDynamicTool.setImageCheckedResourceIds(new int[]{
                R.mipmap.home_ico_good_high, R.mipmap.home_ico_comment_normal,
                R.mipmap.detail_ico_share_normal, R.mipmap.home_ico_more, R.mipmap.detail_ico_adopt_high});
        mDdDynamicTool.setData();
        boolean isMineQuestion = mAnswerInfoBean.getQuestion() != null && mAnswerInfoBean.getQuestion().getUser_id() == AppApplication
                .getMyUserIdWithdefault();
        boolean isMineAnswer = mAnswerInfoBean.getUser_id() != null && mAnswerInfoBean.getUser_id() == AppApplication.getMyUserIdWithdefault();
        boolean isNeedAdoption = isMineQuestion || isMineAnswer;
        mDdDynamicTool.useEditView(isNeedAdoption);
        boolean questionAdoptioned = mAnswerInfoBean.getQuestion() != null && mAnswerInfoBean.getQuestion().getHas_adoption();
        mDdDynamicTool.setItemIsChecked(questionAdoptioned,
                DynamicDetailMenuView.ITEM_POSITION_4);
        boolean showAdoption = mAnswerInfoBean.getQuestion() != null
                &&mAnswerInfoBean.getQuestion().getUser_id()==AppApplication.getMyUserIdWithdefault()
                &&(!questionAdoptioned || mAnswerInfoBean.getAdoption() == 1);
        mDdDynamicTool.setItemPositionVisiable(DynamicDetailMenuView.ITEM_POSITION_4,
                showAdoption ? View.VISIBLE : View.GONE);

        mDdDynamicTool.showAnswerTool(isMine);
    }

    private void initBottomToolListener() {
        mDdDynamicTool.setItemOnClick((parent, v, position) -> {
            mDdDynamicTool.getTag(R.id.view_data);
            switch (position) {
                case DynamicDetailMenuView.ITEM_POSITION_0:// 点赞
                    mPresenter.handleLike(!mAnswerInfoBean.getLiked(),
                            mAnswerInfoBean.getId());
                    break;
                case DynamicDetailMenuView.ITEM_POSITION_1:// 评论
                    showCommentView();
                    mReplyUserId = 0;
                    break;
                case DynamicDetailMenuView.ITEM_POSITION_2:// 分享
                    mPresenter.shareAnswer(mAnswerDetailHeaderView.getSharBitmap());
                    break;
                case DynamicDetailMenuView.ITEM_POSITION_4:// 采纳
                    boolean answerIsMine = mAnswerInfoBean.getUser_id() ==
                            AppApplication.getmCurrentLoginAuth().getUser_id();// 自己的回答？

                    boolean isMineAdopted = mAnswerInfoBean.getAdoption() == 1;// 自己的答案被采纳？

                    boolean isAdopted = mAnswerInfoBean.getQuestion().getHas_adoption();// 问题被采纳？
                    if (isMineAdopted || answerIsMine || isAdopted) {
                        return;
                    }

                    mPresenter.adoptionAnswer(mAnswerInfoBean.getQuestion_id(), mAnswerInfoBean
                            .getId());
                    break;
                case DynamicDetailMenuView.ITEM_POSITION_3:// 更多
                    initDealAnswerPopupWindow(mAnswerInfoBean, mAnswerInfoBean.getCollected());
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
        RxView.clicks(mTvToolbarCenter)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BUNDLE_QUESTION_BEAN, mAnswerInfoBean.getQuestion());
                    intent.putExtra(BUNDLE_QUESTION_BEAN, bundle);
                    startActivity(intent);
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

    private void initPayWindow(Long id) {
        mPayWatchPopWindow = PayPopWindow.builder()
                .with(getActivity())
                .isWrap(true)
                .isFocus(true)
                .isOutsideTouch(true)
                .buildLinksColor1(R.color.themeColor)
                .buildLinksColor2(R.color.important_for_content)
                .contentView(R.layout.ppw_for_center)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .buildDescrStr(String.format(getString(R.string.qa_pay_for_watch_answer_hint) + getString(R
                                .string.buy_pay_member),
                        mPresenter.getSystemConfigBean().getQuestionConfig() != null ? mPresenter.getSystemConfigBean().getQuestionConfig()
                                .getOnlookers_amount() : 0
                        , mPresenter.getGoldName()))
                .buildLinksStr(getString(R.string.qa_pay_for_watch))
                .buildTitleStr(getString(R.string.qa_pay_for_watch))
                .buildItem1Str(getString(R.string.buy_pay_in_payment))
                .buildItem2Str(getString(R.string.buy_pay_out))
                .buildMoneyStr(getString(R.string.buy_pay_integration, "" + (mPresenter.getSystemConfigBean().getQuestionConfig() != null ? mPresenter
                        .getSystemConfigBean().getQuestionConfig()
                        .getOnlookers_amount() : 0)))
                .buildCenterPopWindowItem1ClickListener(() -> {
                    if (id == null) {
                        showSnackErrorMessage(getString(R.string.pay_fail));
                    } else {
                        if (mPresenter.usePayPassword()) {
                            mIlvPassword.setPayNote(new InputPasswordView.PayNote(null, id));
                            showInputPsdView(true);
                        } else {
                            mPresenter.payForOnlook(id, null);
                        }
                    }
                    mPayWatchPopWindow.hide();
                })
                .buildCenterPopWindowItem2ClickListener(() -> {
                    mPayWatchPopWindow.hide();
                    mActivity.finish();
                })
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
        mPayWatchPopWindow.show();

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
    private void initDeleteCommentPopupWindow(final AnswerCommentListBean data) {
        mDeletCommentPopWindow = ActionPopupWindow.builder()
                .item1Color(ContextCompat.getColor(getContext(), R.color.themeColor))
                .item2Str(getString(R.string.dynamic_list_delete_comment))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item2ClickListener(() -> {
                    mDeletCommentPopWindow.hide();
                    showDeleteTipPopupWindow(getString(R.string.delete_comment), () -> {
                        mPresenter.deleteComment(data);
                    }, true);

                })
                .bottomClickListener(() -> mDeletCommentPopWindow.hide())
                .build();
    }

    /**
     * 初始化他人动态操作选择弹框
     *
     * @param answerInfoBean curent answerInfoBean
     */
    private void initDealAnswerPopupWindow(final AnswerInfoBean answerInfoBean, boolean
            isCollected) {

        boolean questionIsMine = answerInfoBean.getQuestion().getUser_id() ==
                AppApplication.getmCurrentLoginAuth().getUser_id();// 自己的问题？

        boolean answerIsMine = answerInfoBean.getUser_id() ==
                AppApplication.getmCurrentLoginAuth().getUser_id();// 自己的回答？

        boolean isMineAdopted = answerInfoBean.getAdoption() == 1;// 自己的答案被采纳？

        boolean isAdopted = answerInfoBean.getQuestion().getHas_adoption();// 问题被采纳？

        boolean isInvited = answerInfoBean.getInvited() == 1;// 是否该回答是被邀请的人的回答。
       boolean isManager = TSUerPerMissonUtil.getInstance().canDeleteAnswer();
        mDealInfoMationPopWindow = ActionPopupWindow.builder()
                .item1Str(((answerIsMine && !isMineAdopted && !isInvited )||
                        isManager)? getString(R.string.info_delete) : "")
//                .item2Str(getString(isAdopted ? (isMineAdopted ? R.string.empty : R.string.empty)
//                        : questionIsMine ? R.string.qa_question_answer_adopting : R.string.empty))
                .item3Str(getString(isCollected ? R.string.dynamic_list_uncollect_dynamic : R
                        .string.dynamic_list_collect_dynamic))
                .item4Str(getString(answerIsMine && !isMineAdopted && !isInvited ? R.string.edit : R.string.empty))
                .item5Str(answerIsMine||isManager ? "" : getString(R.string.report))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(() -> {
                    // 删除
                    mDealInfoMationPopWindow.hide();
                    showDeleteTipPopupWindow(getString(R.string.delete), () -> {
                        mPresenter.deleteAnswer();
                    }, true);
                })
                .item2ClickListener(() -> {
                    // 采纳
                    mDealInfoMationPopWindow.hide();
                    if (isMineAdopted) {
                        return;
                    }
                    if (answerIsMine) {
                        showSnackErrorMessage(getString(R.string.qa_question_cannot_adopt_selef));
                        return;
                    }
                    mPresenter.adoptionAnswer(answerInfoBean.getQuestion_id(), answerInfoBean
                            .getId());

                })
                .item3ClickListener(() -> {
                    // 收藏
                    mPresenter.handleCollect(!answerInfoBean.getCollected(), answerInfoBean.getId
                            ());
                    mDealInfoMationPopWindow.hide();
                })
                .item4ClickListener(() -> {
                    // 编辑
                    mDealInfoMationPopWindow.hide();
                    EditeAnswerDetailFragment.startQActivity(getActivity(), PublishType
                                    .UPDATE_ANSWER, mAnswerInfoBean.getId(), mAnswerInfoBean.getBody(),
                            mAnswerInfoBean.getQuestion().getSubject(), mAnswerInfoBean.getAnonymity());
//                    showSnackWarningMessage(getString(R.string.fun_maintain));
                })
                .item5ClickListener(() -> {
                    // 举报帖子
                    String img = "";

                    int id = RegexUtils.getImageIdFromMarkDown(MarkdownConfig.IMAGE_FORMAT, mAnswerInfoBean.getBody());
                    if (id > 0) {
                        img = ImageUtils.imagePathConvertV2(id, getResources()
                                        .getDimensionPixelOffset(R.dimen.report_resource_img), getResources()
                                        .getDimensionPixelOffset(R.dimen.report_resource_img),
                                ImageZipConfig.IMAGE_80_ZIP);
                    }
                    // 预览的文字
                    String des = mAnswerInfoBean.getText_body();
                    if (TextUtils.isEmpty(des)) {
                        des = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, mAnswerInfoBean.getBody());
                    }
                    UserInfoBean reportUser;
                    // 匿名
                    if (mAnswerInfoBean.getAnonymity() == 1) {
                        reportUser = new UserInfoBean();
                        reportUser.setUser_id(-1L);
                        reportUser.setName(getString(R.string.qa_question_answer_anonymity_user));
                    } else {
                        reportUser = mAnswerInfoBean.getUser();
                    }

                    ReportActivity.startReportActivity(mActivity, new ReportResourceBean(reportUser, String.valueOf
                            (mAnswerInfoBean.getId()),
                            "", img, des, ReportType.QA_ANSWER));
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

    class ItemOnCommentListener implements AnswerDetailCommentItem.OnCommentItemListener {
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
        AnswerCommentListBean infoCommentListBean = mListDatas.get(position);
        if (infoCommentListBean != null && !TextUtils.isEmpty(infoCommentListBean.getBody())) {
            if (infoCommentListBean.getUser_id() == AppApplication.getmCurrentLoginAuth()
                    .getUser_id()) {// 自己的评论
//                if (mListDatas.get(position).getId() != -1) {
                initDeleteCommentPopupWindow(infoCommentListBean);
                mDeletCommentPopWindow.show();
//                } else {
//
//                    return;
//                }
            } else {
                mReplyUserId = infoCommentListBean.getUser_id().intValue();
                showCommentView();
                String contentHint = getString(R.string.default_input_hint);
                if (infoCommentListBean.getReply_user().longValue() != infoCommentListBean.getId().longValue()) {
                    contentHint = getString(R.string.reply, infoCommentListBean
                            .getFromUserInfoBean().getName());
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
        if (AppApplication.getMyUserIdWithdefault() != mListDatas.get(position).getUser_id()) {
            ReportActivity.startReportActivity(mActivity, new ReportResourceBean(mListDatas.get(position).getFromUserInfoBean(), mListDatas.get
                    (position).getId().toString(),
                    null, "", mListDatas.get(position).getBody(), ReportType.COMMENT));

        } else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RewardType.QA_ANSWER.id) {
                mPresenter.reqReWardsData(mAnswerInfoBean.getId().intValue());
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
        }

    }

    @Override
    public void onPause() {
        mAnswerDetailHeaderView.getContentWebView().onPause();
        mAnswerDetailHeaderView.getContentWebView().pauseTimers();
        super.onPause();

    }

    @Override
    public void onResume() {
        mAnswerDetailHeaderView.getContentWebView().onResume();
        mAnswerDetailHeaderView.getContentWebView().resumeTimers();
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        mAnswerDetailHeaderView.destroyedWeb();
        dismissPop(mDealInfoMationPopWindow);
        dismissPop(mDeletCommentPopWindow);
        dismissPop(mPayWatchPopWindow);
        if (showComment != null && !showComment.isUnsubscribed()) {
            showComment.unsubscribe();
        }
        super.onDestroyView();
    }


}

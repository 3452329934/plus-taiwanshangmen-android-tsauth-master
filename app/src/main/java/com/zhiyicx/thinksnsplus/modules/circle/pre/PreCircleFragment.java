package com.zhiyicx.thinksnsplus.modules.circle.pre;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.PayConfig;
import com.zhiyicx.baseproject.widget.InputPasswordView;
import com.zhiyicx.baseproject.widget.button.LoadingButton;
import com.zhiyicx.baseproject.widget.popwindow.PayPopWindow;
import com.zhiyicx.common.utils.AndroidBug5497Workaround;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListBaseItem;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForEightImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForFiveImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForFourImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForNineImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForOneImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForSevenImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForSixImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForThreeImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForTwoImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForZeroImage;
import com.zhiyicx.thinksnsplus.modules.password.findpassword.FindPasswordActivity;
import com.zhiyicx.thinksnsplus.widget.CirclePostEmptyItem;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailFragment.CIRCLE_ID;
import static com.zhiyicx.thinksnsplus.modules.dynamic.list.DynamicFragment.ITEM_SPACING;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/9:349
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PreCircleFragment extends TSListFragment<PreCircleContract.Presenter, CirclePostListBean>
        implements PreCircleContract.View, MultiItemTypeAdapter.OnItemClickListener,
        CirclePostListBaseItem.OnMenuItemClickLisitener {

    @BindView(R.id.iv_circle_head)
    ImageView mIvCircleHead;
    @BindView(R.id.tv_circle_name)
    TextView mTvCircleName;
    @BindView(R.id.tl_circle_tag)
    TagFlowLayout mTlCircleTag;
    @BindView(R.id.tv_member_count)
    TextView mTvMemberCount;
    @BindView(R.id.ll_member_container)
    LinearLayout mLlMemberContainer;
    @BindView(R.id.tv_posts_count)
    TextView mTvPostsCount;
    @BindView(R.id.ll_posts_container)
    LinearLayout mLlPostsContainer;
    @BindView(R.id.tv_essence_count)
    TextView mTvEssenceCount;
    @BindView(R.id.ll_essence_container)
    LinearLayout mLlEssenceContainer;
    @BindView(R.id.tv_introduction)
    TextView mTvIntroduction;
    @BindView(R.id.tv_circle_introduction)
    TextView mTvCircleIntroduction;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_circle_address)
    TextView mTvCircleAddress;
    @BindView(R.id.tv_notice)
    TextView mTvNotice;
    @BindView(R.id.tv_circle_notice)
    TextView mTvCircleNotice;
    @BindView(R.id.tv_pre_essence_posts)
    TextView mTvPreEssencePosts;
    @BindView(R.id.al_appbar)
    AppBarLayout mAlAppbar;
    @BindView(R.id.bt_join_circle)
    LoadingButton mLoadingButton;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_back)
    View mBackView;
    @BindView(R.id.v_line)
    View mLine;
    @BindView(R.id.tv_center_name)
    TextView mTopCenterName;

    protected PayPopWindow mPayPopWindow;
    private CircleInfo mCircleInfo;

    public static PreCircleFragment newInstance(Long circleId) {

        Bundle args = new Bundle();
        args.putLong(CIRCLE_ID, circleId);
        PreCircleFragment fragment = new PreCircleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean setUseInputPsdView() {
        return true;
    }

    @Override
    public void onSureClick(View v, String text, InputPasswordView.PayNote payNote) {
        mPresenter.dealCircleJoinOrExit(mCircleInfo, payNote.psd);
        mLoadingButton.setEnabled(false);
    }

    @Override
    public void onForgetPsdClick() {
        showInputPsdView(false);
        startActivity(new Intent(getActivity(), FindPasswordActivity.class));
    }

    @Override
    public void onCancle() {
        dismissSnackBar();
        mPresenter.canclePay();
        showInputPsdView(false);
    }

    @Override
    protected void initData() {
        super.initData();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            AndroidBug5497Workaround.assistActivity(getActivity());
        }
        initToolBar();
        RxView.clicks(mLoadingButton)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> initPayPopWindow(mActivity, mCircleInfo.getMoney(), mPresenter.getRatio(), mPresenter.getGoldName()
                        , R.string.buy_pay_circle_desc));

        mBackView.setOnClickListener(view -> setLeftClick());
        mAlAppbar.addOnOffsetChangedListener((appBarLayout, i) -> {
                    float point = (float) Math.abs(i) / (float) appBarLayout.getTotalScrollRange();
                    mTopCenterName.setAlpha(point);
                }
        );
    }

    @Override
    protected boolean setUseShadowView() {
        return true;
    }

    @Override
    protected void onShadowViewClick() {
        showInputPsdView(false);
    }

    private void initToolBar() {
        if (setUseStatusView()) {
            // toolBar 设置状态栏高度的 marginTop
            int height = getResources().getDimensionPixelSize(R.dimen.spacing_large);
            CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) mToolbar.getLayoutParams();
            layoutParams.setMargins(0, height, 0, 0);
            mToolbar.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void paySuccess() {
        super.paySuccess();
        mLoadingButton.setEnabled(false);
        mLoadingButton.setText(getString(R.string.bill_doing));
    }

    @Override
    public void payFailed(String msg) {
        super.payFailed(msg);
        mLoadingButton.setEnabled(true);
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean isLoadingMoreEnable() {
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
    protected int getBodyLayoutId() {
        return R.layout.fragment_pre_circle;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter<>(getContext(), mListDatas);
        setAdapter(adapter, new CirclePostListItemForZeroImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForOneImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForTwoImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForThreeImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForFourImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForFiveImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForSixImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForSevenImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForEightImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForNineImage(getContext()));
        CirclePostEmptyItem emptyItem = new CirclePostEmptyItem();
        adapter.addItemViewDelegate(emptyItem);
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<CirclePostListBean> data, boolean isLoadMore) {
        if (data.isEmpty()) {
            data.add(new CirclePostListBean());
        }
        super.onNetResponseSuccess(data, isLoadMore);
    }

    @Override
    public void onMenuItemClick(View view, int dataPosition, int viewPosition) {

    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    protected void setAdapter(MultiItemTypeAdapter adapter, CirclePostListBaseItem circlePostListBaseItem) {
        circlePostListBaseItem.setOnMenuItemClickLisitener(this);
        circlePostListBaseItem.setShowCommentList(false);
        circlePostListBaseItem.setShowPostExcelentTag(true);
        adapter.addItemViewDelegate(circlePostListBaseItem);
    }

    @Override
    protected void snackViewDismissWhenTimeOut(Prompt prompt) {
        super.snackViewDismissWhenTimeOut(prompt);
        if (prompt == Prompt.SUCCESS) {
            mActivity.finish();
        }
    }

    @Override
    public void showSnackErrorMessage(String message) {
        super.showSnackErrorMessage(message);
        mLoadingButton.setEnabled(true);
    }

    @Override
    public Long getCircleId() {
        return getArguments().getLong(CIRCLE_ID);
    }

    @Override
    public void updateHeaderInfo(CircleInfo circleInfo) {
        mCircleInfo = circleInfo;
        boolean isClosedCircle = CircleInfo.CirclePayMode.PAID.value.equals(circleInfo.getMode())
                || CircleInfo.CirclePayMode.PRIVATE.value.equals(circleInfo.getMode());
        boolean isJoined = circleInfo.getJoined() != null && circleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;
        if (!isClosedCircle || isJoined) {
            CircleDetailActivity.startCircleDetailActivity(mActivity, circleInfo.getId());
            mActivity.finish();
            return;
        }
        closeLoadingView();
        mRefreshlayout.setEnableRefresh(false);
        Glide.with(mActivity)
                .load(circleInfo.getAvatar() != null ? circleInfo.getAvatar().getUrl() : "")
                .error(R.drawable.shape_default_image)
                .placeholder(R.drawable.shape_default_image)
                .into(mIvCircleHead);

        mTvCircleName.setText(circleInfo.getName());
        mTopCenterName.setAlpha(0f);

        mTvMemberCount.setText(String.valueOf(circleInfo.getUsers_count()));
        mTvPostsCount.setText(ConvertUtils.numberConvert(circleInfo.getPosts_count()));
        mTvEssenceCount.setText(ConvertUtils.numberConvert(circleInfo.getExcellen_posts_count()));

        mTvCircleIntroduction.setText(circleInfo.getSummary());
        mTvCircleAddress.setText(circleInfo.getLocation());
        mTvCircleNotice.setText(circleInfo.getNotice());

        mTopCenterName.setText(circleInfo.getName());

        mTlCircleTag.setAdapter(new CircleTagAdapter(circleInfo.getTags(), mActivity, false));
        boolean hasSummary = !TextUtils.isEmpty(circleInfo.getSummary());
        boolean hasLocation = !TextUtils.isEmpty(circleInfo.getLocation());
        boolean hasNotice = !TextUtils.isEmpty(circleInfo.getNotice());
        if (!hasSummary) {
            mTvCircleIntroduction.setVisibility(View.GONE);
            mTvIntroduction.setVisibility(View.GONE);
        }
        if (!hasLocation) {
            mTvCircleAddress.setVisibility(View.GONE);
            mTvAddress.setVisibility(View.GONE);
        }
        if (!hasNotice) {
            mTvCircleNotice.setVisibility(View.GONE);
            mTvNotice.setVisibility(View.GONE);
        }
        if (!hasLocation && !hasNotice && !hasSummary) {
            mLine.setVisibility(View.GONE);
        }
        boolean isJoinedWateReview = circleInfo.getJoined() != null && circleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.REVIEWING.value;
        boolean isPaid = CircleInfo.CirclePayMode.PAID.value.equals(circleInfo.getMode());
        if (isPaid && !isJoinedWateReview) {
            mLoadingButton.setTextLeftDrawable(R.mipmap.ico_integral);
            mLoadingButton.setText(circleInfo.getMoney() + mPresenter.getGoldName() + getString(R.string.join_circle));
        }
        if (isJoinedWateReview) {
            mLoadingButton.setEnabled(false);
            mLoadingButton.setText(getString(R.string.bill_doing));
        }
    }

    /**
     * @param context
     * @param amout    金额
     * @param ratio    转换率
     * @param goldName 单位名称
     * @param strRes   描述文字
     */
    protected void initPayPopWindow(Activity context, long amout, int ratio, String goldName, int strRes) {
        if (amout == 0) {
            mPresenter.dealCircleJoinOrExit(mCircleInfo, null);
            mLoadingButton.setEnabled(false);
            return;
        }
        mPayPopWindow = PayPopWindow.builder()
                .with(context)
                .isWrap(true)
                .isFocus(true)
                .isOutsideTouch(true)
                .buildLinksColor1(R.color.themeColor)
                .buildLinksColor2(R.color.important_for_content)
                .contentView(R.layout.ppw_for_center)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .buildDescrStr(String.format(context.getString(strRes) + context.getString(R
                        .string.buy_pay_member), PayConfig.realCurrency2GameCurrencyStr(amout, ratio), goldName))
                .buildLinksStr(context.getString(R.string.buy_pay_member))
                .buildTitleStr(context.getString(R.string.buy_pay))
                .buildItem1Str(context.getString(R.string.buy_pay_in))
                .buildItem2Str(context.getString(R.string.buy_pay_out))
                .buildMoneyStr(String.format(context.getString(R.string.buy_pay_integration), PayConfig.realCurrency2GameCurrencyStr(amout, ratio)))
                .buildCenterPopWindowItem1ClickListener(() -> {
                    boolean isPaid = CircleInfo.CirclePayMode.PAID.value.equals(mCircleInfo.getMode());
                    if (mPresenter.usePayPassword() && isPaid) {
                        mIlvPassword.setPayNote(new InputPasswordView.PayNote());
                        showInputPsdView(true);
                    } else {
                        mPresenter.dealCircleJoinOrExit(mCircleInfo, null);
                        mLoadingButton.setEnabled(false);
                    }
                    mPayPopWindow.hide();
                })
                .buildCenterPopWindowItem2ClickListener(() -> mPayPopWindow.hide())
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
        mPayPopWindow.show();
    }
}

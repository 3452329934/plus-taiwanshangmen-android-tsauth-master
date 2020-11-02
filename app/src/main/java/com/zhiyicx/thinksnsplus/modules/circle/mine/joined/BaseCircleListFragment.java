package com.zhiyicx.thinksnsplus.modules.circle.mine.joined;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.InputPasswordView;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.UserCertificationInfo;
import com.zhiyicx.thinksnsplus.data.source.remote.CircleClient;
import com.zhiyicx.thinksnsplus.modules.certification.detail.CertificationDetailActivity;
import com.zhiyicx.thinksnsplus.modules.certification.input.CertificationInputActivity;
import com.zhiyicx.thinksnsplus.modules.circle.create.CreateCircleActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.main.adapter.BaseCircleItem;
import com.zhiyicx.thinksnsplus.modules.circle.main.adapter.CircleListItem;
import com.zhiyicx.thinksnsplus.modules.circle.pre.PreCircleActivity;
import com.zhiyicx.thinksnsplus.modules.password.findpassword.FindPasswordActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhiyicx.thinksnsplus.modules.certification.detail.CertificationDetailActivity.BUNDLE_DETAIL_DATA;
import static com.zhiyicx.thinksnsplus.modules.certification.detail.CertificationDetailActivity.BUNDLE_DETAIL_TYPE;
import static com.zhiyicx.thinksnsplus.modules.certification.input.CertificationInputActivity.BUNDLE_CERTIFICATION_TYPE;
import static com.zhiyicx.thinksnsplus.modules.certification.input.CertificationInputActivity.BUNDLE_TYPE;

/**
 * @Describe 圈子类别基类
 * @Author Jungle68
 * @Date 2017/12/6
 * @Contact master.jungle68@gmail.com
 */
public class BaseCircleListFragment extends TSListFragment<BaseCircleListContract.Presenter, CircleInfo>
        implements BaseCircleListContract.View, BaseCircleItem.CircleItemItemEvent {

    public static final String BUNDLE_IS_NEED_TOOLBAR = "isNeedToolBar";

    @Inject
    BaseCircleListPresenter mCircleListPresenter;

    protected CircleListItem mCircleListItem;

    protected boolean mIsNeedToolBar;
    protected CircleInfo mCircleInfo;
    private UserCertificationInfo mUserCertificationInfo;
    private ActionPopupWindow mCertificationAlertPopWindow;

    public static BaseCircleListFragment newInstance(boolean isNeedToolBar) {
        BaseCircleListFragment circleListFragment = new BaseCircleListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_IS_NEED_TOOLBAR, isNeedToolBar);
        circleListFragment.setArguments(bundle);
        return circleListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mIsNeedToolBar = getArguments().getBoolean(BUNDLE_IS_NEED_TOOLBAR);
        }
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected boolean showToolbar() {
        return mIsNeedToolBar;
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return mIsNeedToolBar && super.setUseStatusView();
    }

    @Override
    protected boolean setUseInputPsdView() {
        return true;
    }

    @Override
    public void onSureClick(View v, String text, InputPasswordView.PayNote payNote) {
        mPresenter.dealCircleJoinOrExit(payNote.id.intValue(), getCurrentCircleInfo(),payNote.psd);
    }

    protected CircleInfo getCurrentCircleInfo(){
        return mCircleInfo;
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
    protected boolean setUseShadowView() {
        return true;
    }

    @Override
    protected void onShadowViewClick() {
        showInputPsdView(false);
    }

    @Override
    protected int setLeftImg() {
        if (mIsNeedToolBar) {
            return super.setLeftImg();
        } else {
            return 0;
        }
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter<>(getContext(), mListDatas);
        mCircleListItem = new CircleListItem(isMineJoined(), mActivity, this, mPresenter);
        adapter.addItemViewDelegate(mCircleListItem);
        return adapter;
    }

    protected boolean isMineJoined() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        initDagger();
    }

    @Override
    protected void initData() {
        super.initData();
        if (mCircleListItem != null) {
            mCircleListItem.setPresenter(mPresenter);
        }
    }

    protected void initDagger() {
        Observable.create(subscriber -> {
            DaggerBaseCircleListComponent
                    .builder()
                    .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .baseCircleListPresenterModule(new BaseCircleListPresenterModule(BaseCircleListFragment.this))
                    .build()
                    .inject(BaseCircleListFragment.this);

            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        initData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }


    @Override
    public void toAllJoinedCircle(CircleInfo circleInfo) {

    }

    @Override
    public void toCircleDetail(CircleInfo circleInfo) {
        boolean isClosedCircle = CircleInfo.CirclePayMode.PAID.value.equals(circleInfo.getMode())
                || CircleInfo.CirclePayMode.PRIVATE.value.equals(circleInfo.getMode());
        boolean isJoined = circleInfo.getJoined() != null && circleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;


        if (isClosedCircle && !isJoined) {
            PreCircleActivity.startPreCircleActivity(mActivity, circleInfo.getId());
//            showSnackErrorMessage(getString(R.string.circle_blocked));
            return;
        }
        CircleDetailActivity.startCircleDetailActivity(mActivity, circleInfo.getId());
    }

    @Override
    public void changeRecommend() {

    }

    @Override
    public void setUserCertificationInfo(UserCertificationInfo userCertificationInfo) {
        mUserCertificationInfo = userCertificationInfo;
        mSystemConfigBean = mPresenter.getSystemConfigBean();
        if (mSystemConfigBean.getCircleGroup() != null && mSystemConfigBean.getCircleGroup()
                .isNeed_verified() && userCertificationInfo.getStatus() != UserCertificationInfo.CertifyStatusEnum.PASS.value) {
            showCerificationPopWindow();
        } else {
            CreateCircleActivity.startCreateActivity(mActivity);
        }
    }

    @Override
    public void dealCircleJoinOrExit(int position, CircleInfo circleInfo) {
        mCircleInfo = circleInfo;
        boolean isJoined = circleInfo.getJoined() != null && circleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;
        boolean isJoinedWateReview = circleInfo.getJoined() != null && circleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.REVIEWING.value;

        if (isJoinedWateReview) {
            showSnackErrorMessage(getString(R.string.reviewing_join_circle));
            return;
        }

        boolean isPaid = CircleInfo.CirclePayMode.PAID.value.equals(circleInfo.getMode());
        if (mPresenter.usePayPassword() && !isJoined && isPaid) {
            mIlvPassword.setPayNote(new InputPasswordView.PayNote(null, (long) position));
            showInputPsdView(true);
        } else {
            mPresenter.dealCircleJoinOrExit(position, circleInfo,null);
        }
    }

    @Override
    protected Long getMaxId(@NotNull List<CircleInfo> data) {
        return (long) mListDatas.size();
    }

    @Override
    public CircleClient.MineCircleType getMineCircleType() {
        return CircleClient.MineCircleType.JOIN;
    }

    @Override
    public String getSearchInput() {
        return "";
    }

    private void showCerificationPopWindow() {

        if (mCertificationAlertPopWindow == null) {
            mCertificationAlertPopWindow = ActionPopupWindow.builder()
                    .item1Str(getString(R.string.info_publish_hint))
                    .item2Str(getString(R.string.certification_personage))
                    .item3Str(getString(R.string.certification_company))
                    .desStr(getString(R.string.circle_publish_hint_certification))
                    .bottomStr(getString(R.string.cancel))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                    .with(getActivity())
                    .bottomClickListener(() -> mCertificationAlertPopWindow.hide())
                    .item2ClickListener(() -> {
                        // 个人认证
                        mCertificationAlertPopWindow.hide();
                        // 待审核
                        if (mUserCertificationInfo != null
                                && mUserCertificationInfo.getId() != 0
                                && mUserCertificationInfo.getStatus() != UserCertificationInfo.CertifyStatusEnum.REJECTED.value) {
                            Intent intentToDetail = new Intent(getActivity(), CertificationDetailActivity.class);
                            Bundle bundleData = new Bundle();
                            bundleData.putInt(BUNDLE_DETAIL_TYPE, 0);
                            bundleData.putParcelable(BUNDLE_DETAIL_DATA, mUserCertificationInfo);
                            intentToDetail.putExtra(BUNDLE_DETAIL_TYPE, bundleData);
                            startActivity(intentToDetail);
                        } else {
                            Intent intent = new Intent(getActivity(), CertificationInputActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(BUNDLE_TYPE, 0);
                            intent.putExtra(BUNDLE_CERTIFICATION_TYPE, bundle);
                            startActivity(intent);
                        }
                    })
                    .item3ClickListener(() -> {
                        // 企业认证
                        mCertificationAlertPopWindow.hide();
                        // 待审核
                        if (mUserCertificationInfo != null
                                && mUserCertificationInfo.getId() != 0
                                && mUserCertificationInfo.getStatus() != UserCertificationInfo.CertifyStatusEnum.REJECTED.value) {

                            Intent intentToDetail = new Intent(getActivity(), CertificationDetailActivity.class);
                            Bundle bundleData = new Bundle();
                            bundleData.putInt(BUNDLE_DETAIL_TYPE, 1);
                            bundleData.putParcelable(BUNDLE_DETAIL_DATA, mUserCertificationInfo);
                            intentToDetail.putExtra(BUNDLE_DETAIL_TYPE, bundleData);
                            startActivity(intentToDetail);
                        } else {
                            Intent intent = new Intent(getActivity(), CertificationInputActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(BUNDLE_TYPE, 1);
                            intent.putExtra(BUNDLE_CERTIFICATION_TYPE, bundle);
                            startActivity(intent);
                        }
                    })
                    .build();
        }
        mCertificationAlertPopWindow.show();

    }
}

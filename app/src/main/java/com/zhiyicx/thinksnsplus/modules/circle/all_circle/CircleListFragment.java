package com.zhiyicx.thinksnsplus.modules.circle.all_circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.utils.ExcutorUtil;
import com.zhiyicx.baseproject.widget.InputPasswordView;
import com.zhiyicx.common.BuildConfig;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.main.adapter.BaseCircleItem;
import com.zhiyicx.thinksnsplus.modules.circle.main.adapter.CircleListItem;
import com.zhiyicx.thinksnsplus.modules.circle.pre.PreCircleActivity;
import com.zhiyicx.thinksnsplus.modules.password.findpassword.FindPasswordActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Jliuer
 * @date 17/11/22 13:42
 * @email Jliuer@aliyun.com
 * @description
 */
public class CircleListFragment extends TSListFragment<CircleListContract.Presenter, CircleInfo>
        implements CircleListContract.View, BaseCircleItem.CircleItemItemEvent {

    public static final String CIRCLE_TYPE = "circle_type";

    @Inject
    CircleListPresenter mCircleListPresenter;
    private CircleListItem mCircleListItem;
    private CircleInfo mCircleInfo;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected int setLeftImg() {
        return 0;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_list_with_input;
    }

    @Override
    protected Long getMaxId(@NotNull List<CircleInfo> data) {
        return (long) mListDatas.size();
    }

    public static CircleListFragment newInstance(String type) {
        CircleListFragment circleListFragment = new CircleListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CIRCLE_TYPE, type);
        circleListFragment.setArguments(bundle);
        return circleListFragment;
    }

    @Override
    public long getCategoryId() {
        try {
            return Long.parseLong(getArguments().getString(CIRCLE_TYPE));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter<>(getContext(), mListDatas);
        mCircleListItem = new CircleListItem(false, mActivity, this, mPresenter);
        adapter.addItemViewDelegate(mCircleListItem);
        return adapter;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        Observable.create(subscriber -> {
            DaggerCircleListComponent.
                    builder()
                    .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .circleListPresenterModule(new CircleListPresenterModule(this))
                    .build().inject(this);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Subscriber<Object>() {
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
    protected boolean setUseInputPsdView() {
        return true;
    }

    @Override
    public void onSureClick(View v, String text, InputPasswordView.PayNote payNote) {
        mPresenter.dealCircleJoinOrExit(payNote.id.intValue(), mCircleInfo,payNote.psd);
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
    protected void initData() {
        mCircleListItem.setPresenter(mPresenter);
        super.initData();
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
//            showSnackErrorMessage(getString(R.string.circle_blocked));
            PreCircleActivity.startPreCircleActivity(mActivity, circleInfo.getId());
            return;
        }
        CircleDetailActivity.startCircleDetailActivity(mActivity, circleInfo.getId());
    }

    @Override
    public void changeRecommend() {

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
}

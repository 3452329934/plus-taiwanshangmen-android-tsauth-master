package com.zhiyicx.thinksnsplus.modules.circle.pre;

import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.ErrorCodeConfig;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.beans.circle.CircleZipBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/9:42
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PreCirclePresenter extends AppBasePresenter<PreCircleContract.View> implements PreCircleContract.Presenter {

    @Inject
    BaseCircleRepository mBaseCircleRepository;
    private Subscription subscribe;

    @Inject
    public PreCirclePresenter(PreCircleContract.View rootView) {
        super(rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        Subscription subscription = Observable.zip(mBaseCircleRepository.getCircleInfo(mRootView.getCircleId()),
                mBaseCircleRepository.getPrePostListFromCircle(mRootView.getCircleId()), CircleZipBean::new)
                .subscribe(new BaseSubscribeForV2<CircleZipBean>() {
                    @Override
                    protected void onSuccess(CircleZipBean data) {
                        mRootView.onNetResponseSuccess(data.getCirclePostListBeanList(), isLoadMore);
                        mRootView.updateHeaderInfo(data.getCircleInfo());
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(null, isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<CirclePostListBean> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public void dealCircleJoinOrExit(CircleInfo circleInfo,String psd) {

        if (handleTouristControl()) {
            return;
        }
        if (circleInfo.getAudit() != 1) {
            mRootView.showSnackErrorMessage(mContext.getString(R.string.reviewing_circle));
            return;
        }

        boolean isJoinedWateReview = circleInfo.getJoined() != null && circleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.REVIEWING.value;

        if (isJoinedWateReview) {
            mRootView.showSnackErrorMessage(mContext.getString(R.string.reviewing_join_circle));
            return;
        }

        boolean isJoined = circleInfo.getJoined() != null && circleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;

        boolean isPaid = CircleInfo.CirclePayMode.PAID.value.equals(circleInfo.getMode());

        Observable<BaseJsonV2<Object>> observable;
        if (isPaid && !isJoined) {
            observable = handleIntegrationBlance(circleInfo.getMoney())
                    .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R
                            .string.pay_alert_ing)))
                    .flatMap(o -> mBaseCircleRepository.dealCircleJoinOrExit(circleInfo,psd));
        } else {
            observable = mBaseCircleRepository.dealCircleJoinOrExit(circleInfo,null)
                    .doOnSubscribe(() -> {
                                mRootView.dismissSnackBar();
                                mRootView.showSnackLoadingMessage(mContext.getString(R.string.circle_dealing));
                            }
                    );

        }
        subscribe = observable
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<Object> data) {
                        mRootView.showSnackSuccessMessage(data.getMessage().get(0));
                        mRootView.paySuccess();
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        if (code == ErrorCodeConfig.REPEAT_OPERATION) {
                            mRootView.showSnackSuccessMessage(message);
                            mRootView.paySuccess();
                            return;
                        }
                        if (usePayPassword()){
                            mRootView.payFailed(message);
                            return;
                        }
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        if (isIntegrationBalanceCheck(throwable)) {
                            mRootView.paySuccess();
                            return;
                        }
                        if (usePayPassword()){
                            mRootView.payFailed(mContext.getString(R.string.bill_doing_fialed));
                            return;
                        }
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.bill_doing_fialed));
                    }
                });

        addSubscrebe(subscribe);
    }

    @Override
    public void canclePay() {
        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }
}

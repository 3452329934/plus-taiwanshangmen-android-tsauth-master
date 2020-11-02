package com.zhiyicx.thinksnsplus.modules.gallery;

import android.os.Bundle;

import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.WalletBean;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicDetailBeanV2GreenDaoImpl;

import org.simple.eventbus.EventBus;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;

import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_DETAIL_DATA;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_LIST_NEED_REFRESH;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_UPDATE_TOLL;

/**
 * @Author Jliuer
 * @Date 2017/06/29/9:56
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class GalleryPresenter extends AppBasePresenter<GalleryConstract.View> implements GalleryConstract.Presenter {

    DynamicDetailBeanV2GreenDaoImpl mDynamicDetailBeanV2GreenDao;
    private Subscription subscription;

    @Inject
    public GalleryPresenter(GalleryConstract.View rootView, DynamicDetailBeanV2GreenDaoImpl detailBeanV2GreenDao) {
        super(rootView);
        mDynamicDetailBeanV2GreenDao = detailBeanV2GreenDao;
    }

    @Override
    public void checkNote(int note) {

    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public void canclePay() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void payNote(final Long feedId, final int imagePosition, int note, boolean isSavePic, String psd) {

        DynamicDetailBeanV2 dynamicDetail = mDynamicDetailBeanV2GreenDao.getDynamicByFeedId(feedId);
        double amount = dynamicDetail.getImages().get(imagePosition).getAmount();

        subscription = handleIntegrationBlance((long) amount)
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R
                        .string.ts_pay_check_handle_doing)))
                .flatMap(o -> mCommentRepository.paykNote(note, psd))
                .flatMap(Observable::just)
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<String>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2 data) {
                        mRootView.hideCenterLoading();
                        mRootView.paySuccess();
                        UserInfoBean currentUser = mUserInfoBeanGreenDao.getUserInfoById(AppApplication.getMyUserIdWithdefault() + "");
                        currentUser.getCurrency().setSum(currentUser.getFormatCurrencyNum() - (long) amount);
                        mUserInfoBeanGreenDao.insertOrReplace(currentUser);
                        DynamicDetailBeanV2 dynamicDetailBeanV2 = mDynamicDetailBeanV2GreenDao.getDynamicByFeedId(feedId);
                        dynamicDetailBeanV2.getImages().get(imagePosition).setPaid(true);
                        mRootView.getCurrentImageBean().getToll().setPaid(true);
                        mRootView.reLoadImage(isSavePic);
                        mDynamicDetailBeanV2GreenDao.insertOrReplace(dynamicDetailBeanV2);
                        if (!isSavePic) {
                            mRootView.showSnackSuccessMessage(mContext.getString(R.string.transaction_success));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(DYNAMIC_UPDATE_TOLL, true);
                        bundle.putParcelable(DYNAMIC_DETAIL_DATA, dynamicDetailBeanV2);
                        bundle.putBoolean(DYNAMIC_LIST_NEED_REFRESH, true);
                        EventBus.getDefault().post(bundle, EventBusTagConfig.EVENT_UPDATE_DYNAMIC);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        if (usePayPassword()) {
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
                        if (usePayPassword()) {
                            mRootView.payFailed(mContext.getString(R.string.transaction_fail));
                            return;
                        }
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.transaction_fail));
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mRootView.hideCenterLoading();
                    }
                });
        addSubscrebe(subscription);
    }
}

package com.zhiyicx.thinksnsplus.modules.wallet.reward;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.WalletBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseRewardRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/05/22
 * @Contact master.jungle68@gmail.com
 */

public class RewardPresenter extends AppBasePresenter<RewardContract.View> implements RewardContract.Presenter {


    @Inject
    BaseRewardRepository mRewardRepository;
    private Subscription subscription;

    @Inject
    public RewardPresenter(RewardContract.View rootView) {
        super(rootView);
    }

    @Override
    public void reward(double rewardMoney, RewardType rewardType, long sourceId, String psd) {

        WalletBean walletBean = mWalletBeanGreenDao.getSingleDataByUserId(AppApplication.getmCurrentLoginAuth().getUser_id());
        switch (rewardType) {
            case INFO: // 咨询打赏
                hanldeRewardResult(mRewardRepository.rewardInfo(sourceId, rewardMoney, psd), walletBean, rewardMoney);
                break;
            case DYNAMIC: // 动态打赏
                hanldeRewardResult(mRewardRepository.rewardDynamic(sourceId, rewardMoney, psd), walletBean, rewardMoney);
                break;
            case USER: // 用户打赏
                hanldeRewardResult(mRewardRepository.rewardUser(sourceId, rewardMoney, psd), walletBean, rewardMoney);
                break;
            case QA_ANSWER: // 问答回答打赏
                hanldeRewardResult(mRewardRepository.rewardQA(sourceId, rewardMoney, psd), walletBean, rewardMoney);
                break;

            case POST: // 帖子打赏
                hanldeRewardResult(mRewardRepository.rewardPost(sourceId, rewardMoney, psd), walletBean, rewardMoney);
                break;

            default:
                mRootView.showSnackErrorMessage(mContext.getString(R.string.reward_type_error));

        }
    }

    private void hanldeRewardResult(Observable<Object> result, WalletBean walletBean, double rewardMoney) {
        subscription = handleIntegrationBlance((long) rewardMoney)
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R
                        .string.ts_pay_check_handle_doing)))
                .flatMap(o -> result).doAfterTerminate(() -> mRootView.setSureBtEnable(true))
                .subscribe(new BaseSubscribeForV2<Object>() {
                    @Override
                    protected void onSuccess(Object data) {
                        walletBean.setBalance(walletBean.getBalance() - rewardMoney);
                        mWalletBeanGreenDao.insertOrReplace(walletBean);
                        mRootView.paySuccess();
                        mRootView.showSnackSuccessMessage(mContext.getString(R.string.reward_success));
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        if (usePayPassword()) {
                            mRootView.payFailed(message);
                            return;
                        }
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        if (isIntegrationBalanceCheck(throwable)) {
                            mRootView.paySuccess();
                            return;
                        }
                        if (usePayPassword()) {
                            mRootView.payFailed(mContext.getString(R.string.reward_failed));
                            mRootView.dismissSnackBar();
                            return;
                        }
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.reward_failed));
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void canclePay() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}

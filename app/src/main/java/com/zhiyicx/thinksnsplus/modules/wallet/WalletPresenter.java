package com.zhiyicx.thinksnsplus.modules.wallet;

import com.zhiyicx.baseproject.config.PayConfig;
import com.zhiyicx.common.utils.SharePreferenceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.SharePreferenceTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BillRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/05/22
 * @Contact master.jungle68@gmail.com
 */
public class WalletPresenter extends AppBasePresenter<WalletContract.View> implements WalletContract.Presenter {
    public static final int DEFAULT_LOADING_SHOW_TIME = 1;

    /**
     * action tag
     */
    public static final int TAG_DEfault = 0; // do nothing
    public static final int TAG_RECHARGE = 1; // recharge
    public static final int TAG_WITHDRAW = 2; // withdraw
    public static final int TAG_SHOWRULE_POP = 3; // showComment rulepop
    public static final int TAG_SHOWRULE_JUMP = 4; // jump rule

    @Inject
    UserInfoRepository mUserInfoRepository;
    @Inject
    BillRepository mBillRepository;

    /**
     * 用户信息是否拿到了
     */
    private boolean mIsUsreInfoRequseted = false;


    @Inject
    public WalletPresenter(WalletContract.View rootView) {
        super(rootView);
    }

    @Override
    public void updateUserInfo() {
//        getWalletConfigFromServer(TAG_DEfault, false); // 默认主动获取一次
        Subscription timerSub = Observable.timer(DEFAULT_LOADING_SHOW_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (!mIsUsreInfoRequseted) {
                        mRootView.handleLoading(true);
                    }
                });

        Subscription userInfoSub = mUserInfoRepository.getCurrentLoginUserInfo()
                .doAfterTerminate(() -> {
                    mRootView.handleLoading(false);
                    mIsUsreInfoRequseted = true;
                })
                .subscribe(new BaseSubscribeForV2<UserInfoBean>() {
                    @Override
                    protected void onSuccess(UserInfoBean data) {
                        mUserInfoBeanGreenDao.insertOrReplace(data);
                        if (data.getWallet() != null) {
                            mWalletBeanGreenDao.insertOrReplace(data.getWallet());
                        }
                        mRootView.updateBalance(data.getWallet() != null ? PayConfig.realCurrency2GameCurrency(data.getWallet().getBalance(),
                                getRatio()) : 0);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showSnackWarningMessage(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.err_net_not_work));
                    }
                });
        addSubscrebe(timerSub);
        addSubscrebe(userInfoSub);
    }

    /**
     * @return check first look  need showComment  tip pop
     */
    @Override
    public boolean checkIsNeedTipPop() {
        boolean isNotFrist = SharePreferenceUtils.getBoolean(mContext, SharePreferenceTagConfig.SHAREPREFERENCE_TAG_IS_NOT_FIRST_LOOK_WALLET);
        if (!isNotFrist) {
            SharePreferenceUtils.saveBoolean(mContext, SharePreferenceTagConfig.SHAREPREFERENCE_TAG_IS_NOT_FIRST_LOOK_WALLET, true);
        }
        return !isNotFrist;
    }

    /**
     * check wallet config info, if walletconfig has cach used it or get it from server
     *
     * @param tag action tag
     */
    @Override
    public void checkWalletConfig(int tag, final boolean isNeedTip) {
        mRootView.walletConfigCallBack(getSystemConfigBean().getWallet(), tag);
    }

    @Override
    public String getTipPopRule() {
        return getSystemConfigBean().getWallet().getRule();

    }


}

package com.zhiyicx.thinksnsplus.modules.wallet.sticktop;


import android.text.TextUtils;

import com.zhiyicx.baseproject.widget.InputPasswordView;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.AuthBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.StickTopAverageBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicCommentBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.StickTopRepsotory;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;
import com.zhiyicx.thinksnsplus.modules.wallet.integration.mine.MineIntegrationActivity;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * @Author Jliuer
 * @Date 2017/05/23/12:02
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class StickTopPresenter extends AppBasePresenter<StickTopContract.View>
        implements StickTopContract.Presenter {

    @Inject
    UserInfoRepository mUserInfoRepository;
    @Inject
    StickTopRepsotory mStickTopRepsotory;
    @Inject
    DynamicCommentBeanGreenDaoImpl mDynamicCommentBeanGreenDao;

    private StickTopAverageBean mStickTopAverageBean;
    private Subscription subscription;

    @Inject
    public StickTopPresenter(StickTopContract.View rootView) {
        super(rootView);
    }

    /**
     * 内容置顶
     *
     * @param parentId
     */
    @Override
    public void stickTop(long parentId, String psd) {
        if (mRootView.getInputMoney() < 0 && mRootView.getInputMoney() != (int) mRootView.getInputMoney()) {
            mRootView.initStickTopInstructionsPop(mContext.getString(R.string.sticktop_instructions_detail));
            return;
        }
        if (mRootView.getTopDyas() <= 0) {
            mRootView.initStickTopInstructionsPop(mContext.getString(R.string.sticktop_instructions_day));
            return;
        }
        if (mRootView.insufficientBalance()) {
            mRootView.gotoRecharge();
            return;
        }
        if (parentId < 0) {
            return;
        }

        double amount = mRootView.getInputMoney() * mRootView.getTopDyas();

        subscription = mCommentRepository.getCurrentLoginUserInfo()
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R
                        .string.apply_doing)))
                .flatMap(userInfoBean -> {
                    mUserInfoBeanGreenDao.insertOrReplace(userInfoBean);
                    if (userInfoBean.getCurrency() != null) {
                        if (userInfoBean.getCurrency().getSum() < amount) {
                            mRootView.goTargetActivity(MineIntegrationActivity.class);
                            return Observable.error(new RuntimeException(""));
                        }
                    }
                    return mStickTopRepsotory.stickTop(mRootView.getType(), parentId, amount, mRootView.getTopDyas(), psd);
                }, throwable -> {
                    mRootView.showSnackErrorMessage(mContext.getString(R.string.transaction_fail));
                    return null;
                }, () -> null)
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Integer>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<Integer> data) {
                        mRootView.showSnackSuccessMessage(data.getMessage() != null && !TextUtils.isEmpty(data.getMessage().get(0)) ? data
                                .getMessage().get(0) : mContext
                                .getString(R.string.comment_top_success));
                        mRootView.topSuccess();
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        if (usePayPassword()) {
                            mRootView.onFailure(message, code);
                            mRootView.dismissSnackBar();
                        } else {
                            mRootView.showSnackErrorMessage(message);
                        }
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        if (usePayPassword()) {
                            mRootView.onFailure(throwable.getMessage(), -1);
                            mRootView.dismissSnackBar();
                        } else {
                            mRootView.showSnackErrorMessage(throwable.getMessage());
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    /**
     * 内容所属的评论置顶
     *
     * @param parentId
     * @param childId
     */
    @Override
    public void stickTop(long parentId, long childId, String psd) {
        if (mRootView.getInputMoney() < 0 && mRootView.getInputMoney() != (int) mRootView.getInputMoney()) {
            mRootView.initStickTopInstructionsPop(mContext.getString(R.string.sticktop_instructions_detail));
            return;
        }
        if (mRootView.getTopDyas() <= 0) {
            mRootView.initStickTopInstructionsPop(mContext.getString(R.string.sticktop_instructions_day));
            return;
        }
        if (mRootView.insufficientBalance()) {
            mRootView.gotoRecharge();
            return;
        }
        if (parentId < 0) {
            return;
        }
        subscription = mStickTopRepsotory.stickTop(mRootView.getType(), parentId, childId,
                mRootView.getInputMoney() * mRootView.getTopDyas(),
                mRootView.getTopDyas(), psd)
                .doOnSubscribe(() ->
                        mRootView.showSnackLoadingMessage(mContext.getString(R.string.apply_doing))
                )
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Integer>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<Integer> data) {
                        if (StickTopFragment.TYPE_DYNAMIC.equals(mRootView.getType())) {
                            DynamicCommentBean dynamicCommentBean = mDynamicCommentBeanGreenDao.getSingleDataFromCache(childId);
                            if (dynamicCommentBean != null) {
                                dynamicCommentBean.setPinned(true);
                                mDynamicCommentBeanGreenDao.insertOrReplace(dynamicCommentBean);
                            }
                        }
                        mRootView.showSnackSuccessMessage(data.getMessage() != null && !TextUtils.isEmpty(data.getMessage().get(0)) ? data
                                .getMessage().get(0) : mContext
                                .getString(R.string.comment_top_success));
                        mRootView.topSuccess();
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        if (usePayPassword()) {
                            mRootView.onFailure(message, code);
                            mRootView.dismissSnackBar();
                        } else {
                            mRootView.showSnackErrorMessage(message);
                        }
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        if (usePayPassword()) {
                            mRootView.onFailure(throwable.getMessage(), -1);
                            mRootView.dismissSnackBar();
                        } else {
                            mRootView.showSnackErrorMessage(throwable.getMessage());
                        }

                    }
                });

        addSubscrebe(subscription);
    }

    @Override
    public void stickTop(InputPasswordView.PayNote payNote) {
        if (payNote == null) {
            return;
        }
        if (payNote.parent_id == null) {
            return;
        }
        if (payNote.id != null && payNote.id > 0) {
            stickTop(payNote.parent_id, payNote.id, payNote.psd);
        } else {
            stickTop(payNote.parent_id, payNote.psd);
        }
    }

    @Override
    public void canclePay() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public StickTopAverageBean getStickTopAverageBean() {
        return mStickTopAverageBean;
    }

    @Override
    public long getBalance() {

        Subscription userInfoSub = mStickTopRepsotory.getInfoAndCommentTopAverageNum()
                .flatMap((Func1<StickTopAverageBean, Observable<UserInfoBean>>) stickTopAverageBean -> {
                    mStickTopAverageBean = stickTopAverageBean;
                    return mUserInfoRepository.getCurrentLoginUserInfo();
                })
                .subscribe(new BaseSubscribeForV2<UserInfoBean>() {
                    @Override
                    protected void onSuccess(UserInfoBean data) {
                        mUserInfoBeanGreenDao.insertOrReplace(data);
                        mRootView.updateBalance(data.getCurrency() != null ? data.getCurrency().getSum() : 0);
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

        addSubscrebe(userInfoSub);

        AuthBean authBean = AppApplication.getmCurrentLoginAuth();
        if (authBean != null) {
            UserInfoBean userInfoBean = mUserInfoBeanGreenDao.getSingleDataFromCache(authBean.getUser_id());
            if (userInfoBean == null || userInfoBean.getCurrency() == null) {
                return 0;
            }
            return userInfoBean.getCurrency().getSum();
        }
        return 0;
    }
}

package com.zhiyicx.thinksnsplus.modules.wallet.integration.mine

import com.zhiyicx.baseproject.base.SystemConfigBean
import com.zhiyicx.common.utils.SharePreferenceUtils
import com.zhiyicx.thinksnsplus.R
import com.zhiyicx.thinksnsplus.base.AppApplication
import com.zhiyicx.thinksnsplus.base.AppBasePresenter
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2
import com.zhiyicx.thinksnsplus.config.SharePreferenceTagConfig
import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBean
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean
import com.zhiyicx.baseproject.base.SystemConfigBean.IntegrationConfigBean
import com.zhiyicx.thinksnsplus.data.source.local.AllAdvertListBeanGreenDaoImpl
import com.zhiyicx.thinksnsplus.data.source.repository.BillRepository
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/05/22
 * @Contact master.jungle68@gmail.com
 */
class MineIntegrationPresenter @Inject
constructor(rootView: MineIntegrationContract.View) : AppBasePresenter<MineIntegrationContract.View>(rootView), MineIntegrationContract.Presenter {


    @Inject
    @JvmField
    var mUserInfoRepository: UserInfoRepository? = null
    @Inject
    @JvmField
    var mBillRepository: BillRepository? = null

    @Inject
    @JvmField
    var mAdvertListBeanGreenDao: AllAdvertListBeanGreenDaoImpl? = null

    /**
     * 用户信息是否拿到了
     */
    private var mIsUsreInfoRequseted = false



    override val tipPopRule: String
        get() = if (systemConfigBean.currency.rule == null) {
            mContext.resources.getString(R.string.integration_rule)
        } else systemConfigBean.currency.rule

    /**
     *
     * @return 广告信息
     */
    override val integrationAdvert: List<RealAdvertListBean>
        get() {
            val adverBean = mAdvertListBeanGreenDao!!.integrationAdvert
            return if (adverBean != null)
                adverBean.mRealAdvertListBeen
            else
                ArrayList()
        }

    override fun updateUserInfo() {
        val timerSub = Observable.timer(DEFAULT_LOADING_SHOW_TIME.toLong(), TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    if (!mIsUsreInfoRequseted) {
                        mRootView.handleLoading(true)
                    }
                }

        val userInfoSub = mUserInfoRepository!!.getLocalUserInfoBeforeNet(AppApplication.getMyUserIdWithdefault())
                .doAfterTerminate {
                    mRootView.handleLoading(false)
                    mIsUsreInfoRequseted = true
                }
                .subscribe(object : BaseSubscribeForV2<UserInfoBean>() {
                    override fun onSuccess(data: UserInfoBean) {
                        mUserInfoBeanGreenDao.insertOrReplace(data)
                        if (data.wallet != null) {
                            mWalletBeanGreenDao.insertOrReplace(data.wallet)
                        }
                        mRootView.updateBalance(data.formatCurrencyNum)
                    }

                    override fun onFailure(message: String, code: Int) {
                        mRootView.showSnackWarningMessage(message)
                    }

                    override fun onError(e: Throwable) {
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.err_net_not_work))
                    }
                })
        addSubscrebe(timerSub)
        addSubscrebe(userInfoSub)
    }

    /**
     * @return check first look  need showComment  tip pop
     */
    override fun checkIsNeedTipPop(): Boolean {
        val isNotFrist = SharePreferenceUtils.getBoolean(mContext, SharePreferenceTagConfig.SHAREPREFERENCE_TAG_IS_NOT_FIRST_LOOK_WALLET)
        if (!isNotFrist) {
            SharePreferenceUtils.saveBoolean(mContext, SharePreferenceTagConfig.SHAREPREFERENCE_TAG_IS_NOT_FIRST_LOOK_WALLET, true)
        }
        return !isNotFrist
    }

    /**
     * check wallet config info, if integrationconfig has cach used it or get it from server
     *
     * @param tag action tag
     */
    override fun checkIntegrationConfig(tag: Int, isNeedTip: Boolean) {
        mRootView.integrationConfigCallBack(systemConfigBean.currency.settings , tag)

    }

    companion object {
        val DEFAULT_LOADING_SHOW_TIME = 1
        /**
         * action tag
         */
        val TAG_DETAIL = 0 // detail
        val TAG_RECHARGE = 1 // recharge
        val TAG_WITHDRAW = 2 // withdraw
        val TAG_SHOWRULE_POP = 3 // showComment rulepop
        val TAG_SHOWRULE_JUMP = 4 // jump rule
    }
}

package com.zhiyicx.thinksnsplus.modules.wallet.integration.recharge

import com.alipay.sdk.app.PayTask
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.zhiyicx.baseproject.base.SystemConfigBean
import com.zhiyicx.baseproject.config.PayConfig
import com.zhiyicx.baseproject.config.UmengConfig
import com.zhiyicx.common.base.BaseJsonV2
import com.zhiyicx.rxerrorhandler.functions.RetryWithInterceptDelay
import com.zhiyicx.thinksnsplus.R
import com.zhiyicx.thinksnsplus.base.AppBasePresenter
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2
import com.zhiyicx.thinksnsplus.data.source.repository.BillRepository
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository

import javax.inject.Inject

import rx.Subscription

import com.zhiyicx.rxerrorhandler.functions.RetryWithInterceptDelay.RETRY_INTERVAL_TIME
import com.zhiyicx.rxerrorhandler.functions.RetryWithInterceptDelay.RETRY_MAX_COUNT
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig
import com.zhiyicx.thinksnsplus.data.beans.*
import com.zhiyicx.tspay.TSPayClient
import com.zhiyicx.tspay.TSPayClient.*
import org.simple.eventbus.Subscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/05/22
 * @Contact master.jungle68@gmail.com
 */
class IntegrationRechargePresenter @Inject
constructor(rootView: IntegrationRechargeContract.View) : AppBasePresenter<IntegrationRechargeContract.View>(rootView), IntegrationRechargeContract.Presenter {


    @Inject
    lateinit
    var mBillRepository: BillRepository
    @Inject
    lateinit
    var mUserInfoRepository: UserInfoRepository

    override fun useEventBus(): Boolean {
        return true
    }

    override fun getPayStr(channel: String, amount: Double) {
        if (mRootView.money != mRootView.money.toInt().toDouble() && mRootView.useInputMonye()) {
            mRootView.initmRechargeInstructionsPop()
            return
        }
        /**
         * 余额支付
         */
        when {
            CHANNEL_BALANCE == channel -> mBillRepository.balance2Integration(amount.toLong())
                    .flatMap { mUserInfoRepository.currentLoginUserInfo }
                    .doAfterTerminate { mRootView.configSureBtn(true) }
                    .subscribe(object : BaseSubscribeForV2<UserInfoBean>() {
                        override fun onSuccess(data: UserInfoBean) {
                            try {
                                mRootView.rechargeSuccess(amount)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        override fun onFailure(message: String, code: Int) {
                            super.onFailure(message, code)
                            try {
                                mRootView.showSnackErrorMessage(message)
                            } catch (ignored: Exception) {
                                ignored.printStackTrace()
                            }

                        }

                        override fun onException(throwable: Throwable) {
                            super.onException(throwable)
                            try {
                                mRootView.showSnackErrorMessage(mContext.resources.getString(R.string.err_net_not_work))
                            } catch (ignored: Exception) {
                                ignored.printStackTrace()
                            }

                        }

                    })
            channel == CHANNEL_ALIPAY_V2 ->
                /**
                 * 支付宝
                 */
                getAliPayStr(channel,amount)
            channel == CHANNEL_WXPAY_V2 -> // 微信
                getWXPayStr(channel,amount)
        }
    }


    override fun getAliPayStr(channel: String, amount: Double) {
        if (mRootView.money != mRootView.money.toInt().toDouble() && mRootView.useInputMonye()) {
            mRootView.initmRechargeInstructionsPop()
            return
        }
       val subscriber = mBillRepository.getIntegrationAliPayStr(channel, amount)
                .doAfterTerminate { mRootView.configSureBtn(true) }

                .doOnSubscribe {
                    mRootView.configSureBtn(false)
                    mRootView.showSnackLoadingMessage(mContext.getString(R.string.recharge_credentials_ing))
                }
                .flatMap { stringBaseJsonV2 ->
                    val orderInfo = stringBaseJsonV2.data
                    val alipay = PayTask(mRootView.getCurrentActivity())
                    Observable.just(alipay.payV2(orderInfo, true))
                }
                .flatMap { stringStringMap ->
                     if (TSPayClient.CHANNEL_ALIPAY_SUCCESS == stringStringMap["resultStatus"]) {
                        mBillRepository.aliPayIntegrationVerify(stringStringMap["memo"],
                                stringStringMap["result"], stringStringMap["resultStatus"])
                    } else Observable.error<BaseJsonV2<String>>(IllegalArgumentException(stringStringMap["memo"]))
                }
                .flatMap { mUserInfoRepository.currentLoginUserInfo }
                .subscribe(object : BaseSubscribeForV2<UserInfoBean>() {
                    override fun onSuccess(data: UserInfoBean) {
                        try {
                            mRootView.rechargeSuccess(amount)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onFailure(message: String, code: Int) {
                        super.onFailure(message, code)
                        try {
                            mRootView.showSnackErrorMessage(message)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                        }

                    }

                    override fun onException(throwable: Throwable) {
                        super.onException(throwable)
                        try {
                            mRootView.showSnackErrorMessage(throwable.message)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                        }

                    }

                })
        addSubscrebe(subscriber)
    }


    override fun getWXPayStr(channel: String, amount: Double) {
        if (mRootView.money != mRootView.money.toInt().toDouble() && mRootView.useInputMonye()) {
            mRootView.initmRechargeInstructionsPop()
            return
        }
        val subscribe = mBillRepository.getIntegrationWXPayStr(channel, amount)
                .doOnSubscribe {
                    mRootView.configSureBtn(false)
                    mRootView.showSnackLoadingMessage(mContext.getString(R.string.recharge_credentials_ing))
                }
                .doAfterTerminate { mRootView.configSureBtn(true) }
                .subscribe(object : BaseSubscribeForV2<BaseJsonV2<WXPayInfo>>() {
                    override fun onSuccess(data: BaseJsonV2<WXPayInfo>) {
                        val wxPayInfo = data.data
                        val api = WXAPIFactory.createWXAPI(mContext, UmengConfig.WEIXIN_APPID, false)
                        api.registerApp(UmengConfig.WEIXIN_APPID)
                        val request = PayReq()
                        request.appId = UmengConfig.WEIXIN_APPID
                        request.partnerId = wxPayInfo.partnerid
                        request.prepayId = wxPayInfo.prepayid
                        request.packageValue = wxPayInfo.packagestr
                        request.nonceStr = wxPayInfo.noncestr
                        request.timeStamp = wxPayInfo.timestamp
                        request.sign = wxPayInfo.sign
                        api.sendReq(request)
                    }

                    override fun onFailure(message: String, code: Int) {
                        super.onFailure(message, code)
                        try {
                            mRootView.showSnackErrorMessage(message)
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                        }
                    }

                    override fun onException(throwable: Throwable) {
                        super.onException(throwable)
                        try {
                            mRootView.showSnackErrorMessage(mContext.resources.getString(R.string.err_net_not_work))
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                        }
                    }
                })
        addSubscrebe(subscribe)
    }


    override fun rechargeSuccess(charge: String, amount: Double) {
        val subscribe = mBillRepository.integrationRechargeSuccess(charge)
                .subscribe(object : BaseSubscribeForV2<RechargeSuccessV2Bean>() {
                    override fun onSuccess(data: RechargeSuccessV2Bean) {
                        rechargeSuccessCallBack(data.id.toString() + "", amount)
                    }

                    override fun onFailure(message: String, code: Int) {
                        super.onFailure(message, code)
                        mRootView.showSnackErrorMessage(message)
                    }

                    override fun onException(throwable: Throwable) {
                        super.onException(throwable)
                        mRootView.showSnackSuccessMessage(mContext.resources.getString(R.string.err_net_not_work))
                    }
                })
        addSubscrebe(subscribe)
    }

    override fun rechargeSuccessCallBack(charge: String, amount: Double) {
        mUserInfoRepository.currentLoginUserInfo
                .subscribe(object : BaseSubscribeForV2<UserInfoBean>() {
                    override fun onSuccess(data: UserInfoBean) {}
                })
        mRootView.rechargeSuccess(amount)

    }


    @Subscriber(tag = EventBusTagConfig.EVENT_WX_PAY_RESULT)
    fun wxPayResult(wxPayResult: WXPayResult) {
        when {
            wxPayResult.code == 0 -> {
                mUserInfoRepository.currentLoginUserInfo.subscribe(object : BaseSubscribeForV2<UserInfoBean>(){
                    override fun onSuccess(data: UserInfoBean?) {
                        try {
                            mRootView.rechargeSuccess(PayConfig.realCurrencyYuan2Fen(mRootView.money))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
                mRootView.showSnackSuccessMessage(mContext.getString(R.string.recharge_success))

            }// 0 ,微信交易成功
            wxPayResult.code == -2 -> // -2 ,取消交易
                mRootView.showSnackSuccessMessage(mContext.getString(R.string.recharge_cancle))
            else -> mRootView.dismissSnackBar()
        }
    }
}

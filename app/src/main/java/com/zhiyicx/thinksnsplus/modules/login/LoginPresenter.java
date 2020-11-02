package com.zhiyicx.thinksnsplus.modules.login;

import android.database.sqlite.SQLiteFullException;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.utils.ActivityHandler;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.rxerrorhandler.functions.RetryWithInterceptDelay;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.BackgroundTaskRequestMethodConfig;
import com.zhiyicx.thinksnsplus.data.beans.AccountBean;
import com.zhiyicx.thinksnsplus.data.beans.AuthBean;
import com.zhiyicx.thinksnsplus.data.beans.BackgroundRequestTaskBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.AccountBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.BillRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.VertifyCodeRepository;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zhiyicx.rxerrorhandler.functions.RetryWithInterceptDelay.RETRY_INTERVAL_TIME;
import static com.zhiyicx.rxerrorhandler.functions.RetryWithInterceptDelay.RETRY_MAX_COUNT;
import static com.zhiyicx.thinksnsplus.config.ErrorCodeConfig.DATA_HAS_BE_DELETED;

/**
 * @author LiuChao
 * @describe
 * @date 2016/12/30
 * @contact email:450127106@qq.com
 */
@FragmentScoped
public class LoginPresenter extends AppBasePresenter<LoginContract.View> implements LoginContract.Presenter {

    public static final int S_TO_MS_SPACING = 1000; // s 和 ms 的比例
    public static final int SNS_TIME = 60 * S_TO_MS_SPACING; // 发送短信间隔时间，单位 ms

    private int mTimeOut = SNS_TIME;

    private static final String DEFAULPRESUFIX = "用户";

    private String userName = "";
    private boolean needNewUser = false;

    @Inject
    UserInfoRepository mUserInfoRepository;
    @Inject
    AccountBeanGreenDaoImpl mAccountBeanGreenDao;
    @Inject
    BaseCircleRepository mBaseCircleRepository;
    @Inject
    VertifyCodeRepository mVertifyCodeRepository;

    CountDownTimer timer = new CountDownTimer(mTimeOut, S_TO_MS_SPACING) {

        @Override
        public void onTick(long millisUntilFinished) {
            //显示倒数的秒速
            mRootView.setVertifyCodeBtText(millisUntilFinished / S_TO_MS_SPACING + mContext.getString(R.string.seconds));
        }

        @Override
        public void onFinish() {
            //恢复初始化状态
            mRootView.setVertifyCodeBtEnabled(true);
            mRootView.setVertifyCodeBtText(mContext.getString(R.string.send_vertify_code));
        }
    };

    @Inject
    public LoginPresenter(LoginContract.View rootView) {
        super(rootView);
    }

    @Override
    public void login(String phone, String password, String verifiableCode) {
        // 此处由于登陆方式有用户名和手机号还有邮箱 注册规则由服务器判断，所以我们不做判断处理
        // 验证码登录时需要判断 2018-10-19 14:04:12
        if (!TextUtils.isEmpty(verifiableCode) && !RegexUtils.isMobileExact(phone) && !RegexUtils.isEmail(phone)) {
            // 不符合手机号格式
            mRootView.showErrorTips(mContext.getString(R.string.phone_number_toast_hint));
            return;
        }

        if (TextUtils.isEmpty(verifiableCode) && checkPasswordLength(password)) {
            return;
        }
        if (!TextUtils.isEmpty(verifiableCode) && checkVertifyLength(verifiableCode)) {
            return;
        }
        mRootView.setLogining();
        Observable<AuthBean> observable;
        if (needNewUser) {
            observable = Observable.defer(() -> Observable.just(userName))
                    .flatMap((Func1<String, Observable<List<UserInfoBean>>>) s ->
                            mUserInfoRepository.getUserInfoByNames(s))
                    .flatMap((Func1<List<UserInfoBean>, Observable<AuthBean>>) userInfoBeans -> {
                        if (userInfoBeans == null || userInfoBeans.isEmpty()) {
                            return mUserInfoRepository
                                    .registerByPhone(phone, userName, verifiableCode, null)
                                    .observeOn(Schedulers.io())
                                    .flatMap((Func1<AuthBean, Observable<AuthBean>>) Observable::just);
                        } else {
                            userName = DEFAULPRESUFIX + RegexUtils.getRandomUserName(6, true);
                            throw new NullPointerException();
                        }
                    });
        } else {
            observable = mUserInfoRepository.loginV2(phone, password, verifiableCode);
        }
        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithInterceptDelay(100, RETRY_INTERVAL_TIME) {
                    @Override
                    protected boolean extraReTryCondition(Throwable throwable) {
                        return throwable instanceof NullPointerException;
                    }
                })
                .flatMap(authBean -> {
                    // 保存登录认证信息
                    mAuthRepository.saveAuthBean(authBean);
                    return mUserInfoRepository.getCurrentLoginUserInfo()
                            .map(userInfoBean -> {
                                authBean.setUser(userInfoBean);
                                authBean.setUser_id(userInfoBean.getUser_id());
                                return authBean;
                            });
                })
                // 获取用户权限信息
                .flatMap(authBean -> mUserInfoRepository.getCurrentLoginUserPermissions()
                        .map(userPermissons ->{
                            authBean.setUserPermissions(userPermissons);
                            return authBean;
                        }))
                .observeOn(Schedulers.io())
                .map(data -> {
                    mAuthRepository.clearAuthBean();
                    // 登录成功跳转
                    // 保存auth信息
                    mAuthRepository.saveAuthBean(data);
                    // IM 登录 需要 token ,所以需要先保存登录信息
                    handleIMLogin();
                    mUserInfoBeanGreenDao.insertOrReplace(data.getUser());
                    if (data.getUser().getWallet() != null) {
                        mWalletBeanGreenDao.insertOrReplace(data.getUser().getWallet());
                    }

//                    mBaseCircleRepository.saveCircleType(); // 保存圈子分组信息
                    mAccountBeanGreenDao.insertOrReplaceByName(mRootView.getAccountBean());
                    return true;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<Boolean>() {
                    @Override
                    protected void onSuccess(Boolean data) {
                        closeTimer();
                        mRootView.setLoginState(data);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        // 登录失败
                        mRootView.setLoginState(false);
                        mRootView.showErrorTips(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.showErrorTips(mContext.getString(R.string.err_net_not_work));
                        mRootView.setLoginState(false);
                    }
                });

        addSubscrebe(subscription);
    }

    @Override
    public void getVertifyCode(String phone) {
        if (checkPhone(phone)) {
            return;
        }
        mRootView.setVertifyCodeBtEnabled(false);
        mRootView.setVertifyCodeLoadin(true);
        ArrayList<String> phones = new ArrayList<>();
        phones.add(phone);
        userName = DEFAULPRESUFIX + RegexUtils.getRandomUserName(6, true);
        Subscription getVertifySub = mUserInfoRepository.getUsersByPhone(phones)
                .retryWhen(new RetryWithInterceptDelay(RETRY_MAX_COUNT, RETRY_INTERVAL_TIME) {
                    @Override
                    protected boolean extraReTryCondition(Throwable throwable) {
                        // 用户不存在 服务器返回404,
                        boolean is404 = false;
                        if (throwable instanceof retrofit2.HttpException) {
                            retrofit2.HttpException exception = (retrofit2.HttpException) throwable;
                            is404 = exception.code() == 404;
                        }
                        return !is404;
                    }
                }).onErrorReturn(throwable -> null).flatMap((Func1<List<UserInfoBean>, Observable<Object>>) userInfoBeans -> {
                    if (userInfoBeans == null || userInfoBeans.isEmpty()) {
                        needNewUser = true;
                        return mVertifyCodeRepository.getNonMemberVertifyCode(phone);
                    }
                    return mVertifyCodeRepository.getMemberVertifyCode(phone);
                }).subscribe(new BaseSubscribeForV2<Object>() {
                    @Override
                    protected void onSuccess(Object data) {
                        mRootView.hideLoading();//隐藏loading
                        timer.start();//开始倒计时
                        mRootView.setVertifyCodeLoadin(false);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showErrorTips(message);
                        mRootView.setVertifyCodeBtEnabled(true);
                        mRootView.setVertifyCodeLoadin(false);
                    }

                    @Override
                    protected void onException(Throwable e) {
                        mRootView.showErrorTips(mContext.getString(R.string.err_net_not_work));
                        mRootView.setVertifyCodeBtEnabled(true);
                        mRootView.setVertifyCodeLoadin(false);
                    }
                });

        // 代表检测成功
        mRootView.showErrorTips("");
        addSubscrebe(getVertifySub);
    }

    @Override
    public List<AccountBean> getAllAccountList() {
        try {
            return mAccountBeanGreenDao.getMultiDataFromCache();
        }catch (SQLiteFullException e){
            ToastUtils.showToast(R.string.phone_ram_app_exit);
            ActivityHandler.getInstance().AppExitWithSleep();
            return new ArrayList<>();
        }
    }

    /**
     * 三方登录或者注册
     *
     * @param provider
     * @param accessToken
     */
    @Override
    public void checkBindOrLogin(String provider, String accessToken) {

        Subscription subscribe = mUserInfoRepository.checkThridIsRegitser(provider, accessToken)
                .map(data -> {
                    mAuthRepository.clearAuthBean();
                    // 登录成功跳转
                    mAuthRepository.saveAuthBean(data);// 保存auth信息
                    // IM 登录 需要 token ,所以需要先保存登录信息
                    handleIMLogin();
                    mUserInfoBeanGreenDao.insertOrReplace(data.getUser());
                    if (data.getUser().getWallet() != null) {
                        mWalletBeanGreenDao.insertOrReplace(data.getUser().getWallet());
                    }
                    mAccountBeanGreenDao.insertOrReplaceByName(mRootView.getAccountBean());
                    return mAuthRepository.getAuthBean();
                })
                // 获取用户权限信息
                .flatMap(authBean -> mUserInfoRepository.getCurrentLoginUserPermissions()
                        .map(userPermissons ->{
                            authBean.setUserPermissions(userPermissons);
                            return true;
                        }))
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<Boolean>() {
                    @Override
                    protected void onSuccess(Boolean data) {
                        closeTimer();
                        mRootView.setLoginState(data);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        if (code == DATA_HAS_BE_DELETED) {
                            // 三方注册
                            mRootView.registerByThrid(provider, accessToken);
                        } else {
                            // 登录失败
                            mRootView.setLoginState(false);
                            mRootView.showErrorTips(message);
                        }
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.showErrorTips(mContext.getString(R.string.err_net_not_work));
                        mRootView.setLoginState(false);
                    }
                });
        addSubscrebe(subscribe);

    }

    @Override
    public void closeTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void initAdvert() {
        if (/*AppApplication.getMyUserIdWithdefault() < 0 || */!com.zhiyicx.common.BuildConfig.USE_ADVERT) {
            return;
        }
        BackgroundRequestTaskBean backgroundRequestTaskBean = new BackgroundRequestTaskBean();
        backgroundRequestTaskBean.setUser_id(AppApplication.getMyUserIdWithdefault());
        backgroundRequestTaskBean.setMethodType(BackgroundTaskRequestMethodConfig.GET_ADVERT_INFO);
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }

    /**
     * 检查密码是否是最小长度
     *
     * @param password
     * @return
     */
    private boolean checkPasswordLength(String password) {
        if (password.length() < mContext.getResources().getInteger(R.integer.password_min_length)) {
            mRootView.showErrorTips(mContext.getString(R.string.password_toast_hint));
            return true;
        }
        return false;
    }

    private void handleIMLogin() {
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig
                .GET_IM_INFO));
    }

    private boolean checkPhone(String phone) {
        if (!RegexUtils.isMobileExact(phone)) {
            mRootView.showErrorTips(mContext.getString(R.string.phone_number_toast_hint));
            return true;
        }
        return false;
    }

    private boolean checkVertifyLength(String vertifyCode) {
        if (vertifyCode.length() < mContext.getResources().getInteger(R.integer.vertiry_code_min_lenght)) {
            mRootView.showErrorTips(mContext.getString(R.string.vertify_code_input_hint));
            return true;
        }
        return false;
    }
}

package com.zhiyicx.thinksnsplus.modules.circle.mine.joined;

import android.os.Bundle;
import android.text.TextUtils;

import com.zhiyicx.baseproject.base.SystemConfigBean;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;
import com.zhiyicx.thinksnsplus.data.beans.UserCertificationInfo;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.VerifiedBean;
import com.zhiyicx.thinksnsplus.data.beans.circle.CircleSearchHistoryBean;
import com.zhiyicx.thinksnsplus.data.beans.qa.QASearchHistoryBean;
import com.zhiyicx.thinksnsplus.data.source.local.CircleInfoGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.CircleSearchBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.UserCertificationInfoGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.UserInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.remote.CircleClient;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;
import com.zhiyicx.thinksnsplus.modules.circle.main.CircleMainFragment;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;

import static com.zhiyicx.thinksnsplus.modules.q_a.search.list.qa.QASearchListPresenter.DEFAULT_FIRST_SHOW_HISTORY_SIZE;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/12/6
 * @Contact master.jungle68@gmail.com
 */
public class BaseCircleListPresenter extends AppBasePresenter<BaseCircleListContract.View>
        implements BaseCircleListContract.Presenter {

    CircleInfoGreenDaoImpl mCircleInfoGreenDao;
    CircleSearchBeanGreenDaoImpl mCircleSearchBeanGreenDao;
    BaseCircleRepository mBaseCircleRepository;
    UserInfoBeanGreenDaoImpl mUserInfoBeanGreenDao;
    UserCertificationInfoGreenDaoImpl mUserCertificationInfoDao;
    UserInfoRepository mCertificationDetailRepository;

    Subscription mSearchSub;
    private Subscription subscribe;

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Inject
    public BaseCircleListPresenter(BaseCircleListContract.View rootView
            , CircleInfoGreenDaoImpl circleInfoGreenDao,UserCertificationInfoGreenDaoImpl userCertificationInfoDao
            , CircleSearchBeanGreenDaoImpl circleSearchBeanGreenDao,UserInfoRepository userInfoRepository
            , BaseCircleRepository baseCircleRepository,UserInfoBeanGreenDaoImpl userInfoBeanGreenDao) {
        super(rootView);
        mCircleInfoGreenDao = circleInfoGreenDao;
        mCircleSearchBeanGreenDao = circleSearchBeanGreenDao;
        mUserInfoBeanGreenDao = userInfoBeanGreenDao;
        mCertificationDetailRepository = userInfoRepository;
        mUserCertificationInfoDao = userCertificationInfoDao;
        mBaseCircleRepository = baseCircleRepository;
    }

    @Override
    public void canclePay() {
        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }

    @Override
    public void checkCertification() {
        UserInfoBean userInfoBean = mUserInfoBeanGreenDao.getSingleDataFromCache(AppApplication.getMyUserIdWithdefault());
        UserCertificationInfo userCertificationInfo = mUserCertificationInfoDao.getInfoByUserId();
        if (getSystemConfigBean() != null && getSystemConfigBean().getCircleGroup() != null && userCertificationInfo != null &&
                userCertificationInfo.getStatus() == UserCertificationInfo.CertifyStatusEnum.PASS.value) {
            mRootView.setUserCertificationInfo(userCertificationInfo);
            return;
        }

        Subscription subscribe = Observable.zip(mSystemRepository.getBootstrappersInfo(), mCertificationDetailRepository.getCertificationInfo(),
                (systemConfigBean, userCertificationInfo1) -> {
                    Map data = new HashMap();
                    data.put("systemConfigBean", systemConfigBean);
                    data.put("userCertificationInfo", userCertificationInfo1);
                    return data;
                })
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage("信息加载中..."))
                .doAfterTerminate(() -> mRootView.dismissSnackBar())
                .subscribe(new BaseSubscribeForV2<Map>() {
                    @Override
                    protected void onSuccess(Map zipData) {
                        UserCertificationInfo data = (UserCertificationInfo) zipData.get("userCertificationInfo");
                        SystemConfigBean systemConfigBean = (SystemConfigBean) zipData.get("systemConfigBean");
                        mSystemRepository.saveComponentStatus(systemConfigBean, mContext);
                        mUserCertificationInfoDao.saveSingleData(data);
                        if (userInfoBean != null) {
                            if (userInfoBean.getVerified() != null) {
                                userInfoBean.getVerified().setStatus((int) data.getStatus());
                            } else {
                                VerifiedBean verifiedBean = new VerifiedBean();
                                verifiedBean.setStatus((int) data.getStatus());
                                userInfoBean.setVerified(verifiedBean);
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(EventBusTagConfig.EVENT_UPDATE_CERTIFICATION_SUCCESS, data);
                        EventBus.getDefault().post(bundle, EventBusTagConfig.EVENT_UPDATE_CERTIFICATION_SUCCESS);
                        mUserInfoBeanGreenDao.updateSingleData(userInfoBean);
                        mRootView.setUserCertificationInfo(data);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackSuccessMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.showSnackSuccessMessage(mContext.getString(R.string.err_net_not_work));
                    }
                });
        addSubscrebe(subscribe);
    }

    @Override
    public void getRecommendCircle(Integer limit, int offet, String type) {
        Subscription subscription=mBaseCircleRepository.getRecommendCircle(CircleMainFragment.DATALIMIT, 0, CircleClient.MineCircleType.RANDOM.value)
                .subscribe(new BaseSubscribeForV2<List<CircleInfo>>() {
                    @Override
                    protected void onSuccess(List<CircleInfo> data) {
                        if (data.isEmpty()) {
                            mRootView.hideRefreshState(false);
                            return;
                        }
                        mRootView.onNetResponseSuccess(data, false);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {

        switch (mRootView.getMineCircleType()) {
            case JOIN:
            case AUDIT:
            case ALLOW:
                Subscription subscribe = mBaseCircleRepository.getMyJoinedCircle(TSListFragment.DEFAULT_PAGE_SIZE
                        , maxId.intValue(), mRootView.getMineCircleType().value)
                        .subscribe(new BaseSubscribeForV2<List<CircleInfo>>() {

                            @Override
                            protected void onSuccess(List<CircleInfo> data) {
                                mRootView.onNetResponseSuccess(data, isLoadMore);
                            }

                            @Override
                            protected void onFailure(String message, int code) {
                                super.onFailure(message, code);
                                mRootView.showMessage(message);
                            }

                            @Override
                            protected void onException(Throwable throwable) {
                                super.onException(throwable);
                                mRootView.onResponseError(throwable, isLoadMore);
                            }
                        });
                addSubscrebe(subscribe);

                break;
            case SEARCH:
                if (mSearchSub != null && !mSearchSub.isUnsubscribed()) {
                    mSearchSub.unsubscribe();
                }
                final String searchContent = mRootView.getSearchInput();
                // 无搜索内容
                if (TextUtils.isEmpty(searchContent)) {
                    getRecommendCircle(TSListFragment.DEFAULT_PAGE_DB_SIZE, 0, CircleClient.MineCircleType.RANDOM.value);
//                    mRootView.hideRefreshState(isLoadMore);
                    return;
                }
                mSearchSub = mBaseCircleRepository.getAllCircle(TSListFragment.DEFAULT_PAGE_SIZE, maxId.intValue(),
                        searchContent, null,null)
                        .subscribe(new BaseSubscribeForV2<List<CircleInfo>>() {
                            @Override
                            protected void onSuccess(List<CircleInfo> data) {
                                // 历史记录存入数据库
                                saveSearhDatq(searchContent);
                                mRootView.onNetResponseSuccess(data, isLoadMore);
                            }

                            @Override
                            protected void onFailure(String message, int code) {
                                super.onFailure(message, code);
                                mRootView.showMessage(message);
                            }

                            @Override
                            protected void onException(Throwable throwable) {
                                mRootView.onResponseError(throwable, isLoadMore);
                            }
                        });
                addSubscrebe(mSearchSub);


                break;

            default:


        }


    }

    /**
     * 存搜索记录
     *
     * @param searchContent
     */
    private void saveSearhDatq(String searchContent) {
        CircleSearchHistoryBean cricleSearchHistoryBean = new CircleSearchHistoryBean(searchContent, CircleSearchHistoryBean.TYPE_CIRCLE);
        cricleSearchHistoryBean.setOutSideCircle(true);
        mCircleSearchBeanGreenDao.saveHistoryDataByType(cricleSearchHistoryBean, CircleSearchHistoryBean.TYPE_CIRCLE);
    }


    @Override
    public void dealCircleJoinOrExit(int position, CircleInfo circleInfo,String psd) {
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
        if (isPaid) {
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
                        if (isJoined) {
                            circleInfo.setJoined(null);
                            circleInfo.setUsers_count(circleInfo.getUsers_count() - 1);
                        } else {
                            // 如果是 封闭的或者 收费的 ，就不及时更新
                            if (CircleInfo.CirclePayMode.PRIVATE.value.equals(circleInfo.getMode())
                                    || CircleInfo.CirclePayMode.PAID.value.equals(circleInfo.getMode())) {
                                return;
                            }
                            CircleJoinedBean circleJoinedBean = new CircleJoinedBean(CircleMembers.MEMBER);
                            circleJoinedBean.setUser_id((int) AppApplication.getMyUserIdWithdefault());
                            circleJoinedBean.setUser(AppApplication.getmCurrentLoginAuth().getUser());
                            circleJoinedBean.setGroup_id(circleInfo.getId().intValue());
                            circleJoinedBean.setAudit(1);
                            circleInfo.setJoined(circleJoinedBean);
                            circleInfo.setUsers_count(circleInfo.getUsers_count() + 1);
                        }
                        mCircleInfoGreenDao.updateSingleData(circleInfo);
                        mRootView.refreshData(position);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        if (isIntegrationBalanceCheck(throwable)) {
                            return;
                        }
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.bill_doing_fialed));
                    }
                });

        addSubscrebe(subscribe);

    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(new ArrayList<>(), isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<CircleInfo> data, boolean isLoadMore) {
        mCircleInfoGreenDao.saveMultiData(data);
        return isLoadMore;
    }


    @Override
    public List<CircleSearchHistoryBean> getFirstShowHistory() {
        List<CircleSearchHistoryBean> historyBeans = mCircleSearchBeanGreenDao.getFristShowData(DEFAULT_FIRST_SHOW_HISTORY_SIZE, CircleSearchHistoryBean.TYPE_CIRCLE, true);
        if (historyBeans == null || historyBeans.isEmpty()) {
            getRecommendCircle(TSListFragment.DEFAULT_PAGE_DB_SIZE, 0, CircleClient.MineCircleType.RANDOM.value);
        }
        return historyBeans;
    }

    @Override
    public void cleaerAllSearchHistory() {
        mCircleSearchBeanGreenDao.clearAllQASearchHistory();
    }

    @Override
    public List<CircleSearchHistoryBean> getAllSearchHistory() {
        return mCircleSearchBeanGreenDao.getCircleSearchHistory();
    }

    @Override
    public void deleteSearchHistory(CircleSearchHistoryBean qaSearchHistoryBean) {
        mCircleSearchBeanGreenDao.deleteSingleCache(qaSearchHistoryBean);
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_UPDATE_CIRCLE)
    public void updateCircle(CircleInfo circleInfo) {
        int index = -1;
        for (CircleInfo circle : mRootView.getListDatas()) {
            if (circle.equals(circleInfo)) {
                index = mRootView.getListDatas().indexOf(circle);
            }
        }
        if (index != -1) {
            mRootView.getListDatas().set(index, circleInfo);
        }
        mRootView.refreshData(index);
        LogUtils.d(EventBusTagConfig.EVENT_UPDATE_CIRCLE);
    }

}

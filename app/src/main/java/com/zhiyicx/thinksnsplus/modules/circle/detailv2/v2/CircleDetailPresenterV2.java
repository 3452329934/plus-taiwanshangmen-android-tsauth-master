package com.zhiyicx.thinksnsplus.modules.circle.detailv2.v2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.baseproject.share.OnShareCallbackListener;
import com.zhiyicx.baseproject.share.Share;
import com.zhiyicx.baseproject.share.ShareContent;
import com.zhiyicx.baseproject.share.SharePolicy;
import com.zhiyicx.common.base.BaseFragment;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.source.local.CircleInfoGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailContract;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.utils.TSShareUtils;

import org.simple.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.PostTypeChoosePopAdapter.MyPostTypeEnum.LATEST_POST;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/16:39
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CircleDetailPresenterV2 extends AppBasePresenter<CircleDetailContract.ViewV2>
        implements CircleDetailContract.PresenterV2, OnShareCallbackListener {

    private static final int SHOW_MAX_CHAT_MEMBER = 4;

    @Inject
    SharePolicy mSharePolicy;
    @Inject
    BaseCircleRepository mBaseCircleRepository;
    @Inject
    CircleInfoGreenDaoImpl mCircleInfoGreenDao;

    private CircleInfo mShareCircleInfo;
    private Subscription subscribe;

    @Inject
    public CircleDetailPresenterV2(CircleDetailContract.ViewV2 rootView) {
        super(rootView);
    }

    @Override
    public void getCircleInfo() {
        Subscription subscription = Observable.zip(mBaseCircleRepository.getCircleMemberList(mRootView.getCircleId(), 0,
                SHOW_MAX_CHAT_MEMBER, CircleMembers.ALL, null),
                mBaseCircleRepository.getCircleInfo(mRootView.getCircleId()), (circleMembers, circleInfo) -> {
                    circleMembers.add(new CircleMembers());
                    circleInfo.setCircleMembers(circleMembers);
                    return circleInfo;
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<CircleInfo>() {
                    @Override
                    protected void onSuccess(CircleInfo data) {
                        mRootView.updateCircleInfo(data, false);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mRootView.dismissSnackBar();
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.loadAllError();
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.loadAllError();
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void shareCircle(CircleInfo circleinfo, Bitmap shareBitMap, List<UmengSharePolicyImpl.ShareBean> data) {
        mShareCircleInfo = circleinfo;
        ((UmengSharePolicyImpl) mSharePolicy).setOnShareCallbackListener(this);
        ShareContent shareContent = new ShareContent();
        shareContent.setTitle(circleinfo.getName());
        shareContent.setContent(TextUtils.isEmpty(circleinfo.getSummary()) ? mContext.getString(R.string
                .share_default, mContext.getString(R.string.app_name)) : circleinfo.getSummary());
        if (shareBitMap != null) {
            shareContent.setBitmap(shareBitMap);
        } else {
            shareContent.setBitmap(ConvertUtils.drawBg4Bitmap(Color.WHITE, BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon)));
        }
        shareContent.setUrl(TSShareUtils.convert2ShareUrl(String.format(ApiConfig.APP_PATH_SHARE_GROUP, circleinfo.getId(), LATEST_POST.value)));
        mSharePolicy.setShareContent(shareContent);
        mSharePolicy.showShare(((TSFragment) mRootView).getActivity(), data);
    }

    @Override
    public void canclePay() {
        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }

    @Override
    public void dealCircleJoinOrExit(CircleInfo circleInfo, String psd) {

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
                    .flatMap(o -> mBaseCircleRepository.dealCircleJoinOrExit(circleInfo, psd));
        } else {
            observable = mBaseCircleRepository.dealCircleJoinOrExit(circleInfo, null)
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
                        boolean isPrivateOrPaid = CircleInfo.CirclePayMode.PRIVATE.value.equals(circleInfo.getMode())
                                || CircleInfo.CirclePayMode.PAID.value.equals(circleInfo.getMode());
                        if (isJoined) {
                            circleInfo.setJoined(null);
                            circleInfo.setUsers_count(circleInfo.getUsers_count() - 1);
                            mRootView.updateCircleInfo(circleInfo, true);
                        } else {
                            mRootView.showSnackSuccessMessage(data.getMessage().get(0));
                            // 如果是 封闭的或者 收费的 ，就不及时更新
                            if (isPrivateOrPaid) {
                                return;
                            }
                            CircleJoinedBean circleJoinedBean = new CircleJoinedBean(CircleMembers.MEMBER);
                            circleJoinedBean.setUser_id((int) AppApplication.getMyUserIdWithdefault());
                            circleJoinedBean.setUser(AppApplication.getmCurrentLoginAuth().getUser());
                            circleJoinedBean.setGroup_id(circleInfo.getId().intValue());
                            circleJoinedBean.setAudit(1);
                            circleInfo.setJoined(circleJoinedBean);
                            circleInfo.setUsers_count(circleInfo.getUsers_count() + 1);
                            mRootView.updateCircleInfo(circleInfo, false);
                        }
                        if (isJoined) {
                            EventBus.getDefault().post(circleInfo, EventBusTagConfig.EVENT_UPDATE_CIRCLE);
                        }
                        mCircleInfoGreenDao.insertOrReplace(circleInfo);
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
    public void onStart(Share share) {
        Letter letter;
        switch (share) {
            case FORWARD:
                letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE);
                if (mShareCircleInfo != null) {
                    // 圈子
                    letter.setName(mShareCircleInfo.getName());
                    String content = TextUtils.isEmpty(mShareCircleInfo.getSummary()) ? mContext.getString(R.string
                            .share_default, mContext.getString(R.string.app_name)) : mShareCircleInfo.getSummary();
                    letter.setContent(content);
                    letter.setImage(mShareCircleInfo.getAvatar() != null ? mShareCircleInfo.getAvatar().getUrl() : "");
                    letter.setId(mShareCircleInfo.getId() + "");

                    SendDynamicDataBean sendWordsDynamicDataBean = new SendDynamicDataBean();
                    sendWordsDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
                    sendWordsDynamicDataBean.setDynamicType(SendDynamicDataBean.TEXT_ONLY_DYNAMIC);
                    SendDynamicActivity.startToSendDynamicActivity(((BaseFragment) mRootView).getActivity(), sendWordsDynamicDataBean, letter);
                }
                break;
            case LETTER:
                letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_CIRCLE);
                if (mShareCircleInfo != null) {
                    // 圈子
                    letter.setName(mShareCircleInfo.getName());
                    String content = TextUtils.isEmpty(mShareCircleInfo.getSummary()) ? mContext.getString(R.string
                            .share_default, mContext.getString(R.string.app_name)) : mShareCircleInfo.getSummary();
                    letter.setContent(content);
                    letter.setImage(mShareCircleInfo.getAvatar() != null ? mShareCircleInfo.getAvatar().getUrl() : "");
                    letter.setId(mShareCircleInfo.getId() + "");

                    ChooseFriendActivity.startChooseFriendActivity(((BaseFragment) mRootView).getActivity(), letter);
                }
                break;
            default:
        }
    }

    @Override
    public void onSuccess(Share share) {

    }

    @Override
    public void onError(Share share, Throwable throwable) {

    }

    @Override
    public void onCancel(Share share) {

    }
}

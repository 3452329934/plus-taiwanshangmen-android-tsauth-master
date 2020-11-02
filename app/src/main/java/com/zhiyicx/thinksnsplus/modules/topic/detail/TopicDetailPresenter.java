package com.zhiyicx.thinksnsplus.modules.topic.detail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.baseproject.share.Share;
import com.zhiyicx.baseproject.share.ShareContent;
import com.zhiyicx.baseproject.share.SharePolicy;
import com.zhiyicx.common.base.BaseFragment;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.ErrorCodeConfig;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.beans.report.ReportResourceBean;
import com.zhiyicx.thinksnsplus.data.source.local.AllAdvertListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicCommentBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicDetailBeanV2GreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.SendDynamicDataBeanV2GreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.TopDynamicBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseDynamicRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseTopicRepository;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.BaseDynamicPresenter;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportType;
import com.zhiyicx.thinksnsplus.modules.topic.ITopicRepository;
import com.zhiyicx.thinksnsplus.modules.wallet.sticktop.StickTopFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.utils.TSShareUtils;
import com.zhiyicx.thinksnsplus.widget.popwindow.LetterPopWindow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/9:45
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopicDetailPresenter extends BaseDynamicPresenter<TopicDetailContract.View, TopicDetailContract.Presenter> implements TopicDetailContract.Presenter {

    @Inject
    BaseTopicRepository mTopicRepository;

    @Inject
    SharePolicy mSharePolicy;

    @Inject
    TopicDetailPresenter(TopicDetailContract.View rootView
            , AllAdvertListBeanGreenDaoImpl allAdvertListBeanGreenDao
            , DynamicDetailBeanV2GreenDaoImpl dynamicDetailBeanV2GreenDao
            , DynamicCommentBeanGreenDaoImpl dynamicCommentBeanGreenDao
            , SendDynamicDataBeanV2GreenDaoImpl sendDynamicDataBeanV2GreenDao
            , TopDynamicBeanGreenDaoImpl topDynamicBeanGreenDao
            , BaseDynamicRepository baseDynamicRepository) {
        super(rootView, allAdvertListBeanGreenDao, dynamicDetailBeanV2GreenDao, dynamicCommentBeanGreenDao
                , sendDynamicDataBeanV2GreenDao, topDynamicBeanGreenDao, baseDynamicRepository);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {

        Observable<List<DynamicDetailBeanV2>> observable;
        Observable<List<DynamicDetailBeanV2>> observableTopicDynamicList;
        Observable<TopicDetailBean> observableTopicDetail;

        observableTopicDetail = mTopicRepository.getTopicDetailBean(mRootView.getTopicId());

        observableTopicDynamicList = mTopicRepository.getTopicDynamicListBean(mRootView.getTopicId(),
                ITopicRepository.DESC, TSListFragment.DEFAULT_PAGE_SIZE, maxId, isLoadMore);

        if (!isLoadMore) {
            observable = Observable.zip(observableTopicDetail, observableTopicDynamicList, (topicDetailBean, dynamicDetails) -> {
                Observable.just(topicDetailBean)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(topicDetailBean1 -> mRootView.updateCurrentTopic(topicDetailBean1));
                return dynamicDetails;
            });
        } else {
            observable = observableTopicDynamicList;
        }
        Subscription subscription = observable
                .observeOn(Schedulers.io())
                .map(listBaseJson -> {
                    List<DynamicDetailBeanV2> data;
                    // 更新数据库
                    insertOrUpdateDynamicDBV2(listBaseJson);
                    // 如果是刷新，并且获取到了数据，更新发布的动态 ,把发布的动态信息放到请求数据的前面
                    if (!isLoadMore) {
                        data = getDynamicBeenFromDBWithTopic(mRootView.getTopicId());
                        data.addAll(listBaseJson);
                    } else {
                        data = new ArrayList<>();
                        data.addAll(listBaseJson);
                    }
                    TopicListBean currentTopic = new TopicListBean(mRootView.getTopicId());
                    // 把自己发的评论加到评论列表的前面
                    for (int i = 0; i < listBaseJson.size(); i++) {
                        // 处理友好显示数据
                        listBaseJson.get(i).handleData();
                        // 不显示当前话题
                        if (listBaseJson.get(i).getTopics() != null) {
                            listBaseJson.get(i).getTopics().remove(currentTopic);
                        }
                        List<DynamicCommentBean> dynamicCommentBeen = mDynamicCommentBeanGreenDao.getMySendingComment(listBaseJson.get(i)
                                .getFeed_mark());
                        if (!dynamicCommentBeen.isEmpty()) {
                            dynamicCommentBeen.addAll(listBaseJson.get(i).getComments());
                            listBaseJson.get(i).getComments().clear();
                            listBaseJson.get(i).getComments().addAll(dynamicCommentBeen);
                        }
                    }
                    return data;
                })
                .observeOn(Schedulers.io())
                .flatMap((Func1<List<DynamicDetailBeanV2>, Observable<List<DynamicDetailBeanV2>>>) dynamicDetailBeanV2s -> {
                    for (DynamicDetailBeanV2 beanV2 : dynamicDetailBeanV2s) {
                        beanV2.handleData();
                    }
                    return Observable.just(dynamicDetailBeanV2s);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<List<DynamicDetailBeanV2>>() {
                    @Override
                    protected void onSuccess(List<DynamicDetailBeanV2> data) {
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        if (code == ErrorCodeConfig.DATA_HAS_BE_DELETED) {
                            mRootView.topicHasBeDeleted();
                        }
                        mRootView.onResponseError(null, isLoadMore);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(new ArrayList<>(), isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<DynamicDetailBeanV2> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public void shareTopic(TopicDetailBean topic, Bitmap shareBitMap) {
        ((UmengSharePolicyImpl) mSharePolicy).setOnShareCallbackListener(this);
        ShareContent shareContent = new ShareContent();
        shareContent.setTitle(topic.getName());
        shareContent.setContent(TextUtils.isEmpty(topic.getDesc()) ? mContext.getString(R.string
                .share_default, mContext.getString(R.string.app_name)) : topic.getDesc());
        if (shareBitMap != null) {
            shareContent.setBitmap(shareBitMap);
        } else {
            shareContent.setBitmap(ConvertUtils.drawBg4Bitmap(Color.WHITE, BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon)));
        }
//        shareContent.setUrl(TSShareUtils.convert2ShareUrl(String.format(ApiConfig.APP_PATH_SHARE_TOPIC, topic.getId(), "topic")));
        shareContent.setUrl(TSShareUtils.convert2ShareUrl(ApiConfig.APP_PATH_SHARE_DEFAULT));
        mSharePolicy.setShareContent(shareContent);
        mSharePolicy.showShare(((TSFragment) mRootView).getActivity());
    }

    @Override
    public void handleTopicFollowState(Long topicId, boolean isFollowed) {
        mTopicRepository.handleTopicFollowState(topicId, isFollowed);
        TopicDetailBean topic = mRootView.getCurrentTopic();
        topic.setHas_followed(!isFollowed);
        int followersCount = mRootView.getCurrentTopic().getFollowers_count() + (isFollowed ? (-1) : 1);
        topic.setFollowers_count(followersCount);
        mRootView.updateCurrentTopic(topic);
    }

    @Override
    public void handleSendDynamic(DynamicDetailBeanV2 dynamicBean) {
        Subscription subscribe = Observable.just(dynamicBean)
                .observeOn(Schedulers.computation())
                .map(dynamicDetailBeanV2 -> hasDynamicContanied(dynamicBean))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(position -> {
                    // 如果列表有当前数据
                    if (position[1] != -1) {
                        mRootView.showNewDynamic(position[1], dynamicBean.getMLetter() != null);
                    } else {
                        List<DynamicDetailBeanV2> temps = new ArrayList<>(mRootView.getListDatas());
                        temps.add(position[0], dynamicBean);
                        mRootView.getListDatas().clear();
                        mRootView.getListDatas().addAll(temps);
                        temps.clear();
                        mRootView.showNewDynamic(position[0], dynamicBean.getMLetter() != null);
                        mRootView.getCurrentTopic().setFeeds_count(mRootView.getCurrentTopic().getFeeds_count() + 1);
                        mRootView.updateCount(mRootView.getCurrentTopic().getFeeds_count(), mRootView.getCurrentTopic().getFollowers_count());
                    }
                }, Throwable::printStackTrace);
        addSubscrebe(subscribe);
    }


    @Override
    public void onStart(Share share) {
        super.onStart(share);
        String content = "";
        String dynamicType = "";
        boolean hasImage, hasVideo;
        Letter letter;
        switch (share) {
            case FORWARD:

                hasImage = mShareDynamic.getImages() != null && !mShareDynamic.getImages().isEmpty();
                hasVideo = mShareDynamic.getVideo() != null;
                if (!hasImage && !hasVideo) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD;
                    content = mShareDynamic.getFriendlyContent();
                }
                if (hasImage) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE;
                    content = LetterPopWindow.PIC;

                }
                if (hasVideo) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_VIDEO;
                    content = LetterPopWindow.VIDEO;
                }
                letter = new Letter(mShareDynamic.getUserInfoBean().getName(), content, TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC);
                letter.setId(mShareDynamic.getId() + "");
                letter.setDynamic_type(dynamicType);

                SendDynamicDataBean sendWordsDynamicDataBean = new SendDynamicDataBean();
                sendWordsDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
                sendWordsDynamicDataBean.setDynamicType(SendDynamicDataBean.TEXT_ONLY_DYNAMIC);
                SendDynamicActivity.startToSendDynamicActivity(((BaseFragment) mRootView).getActivity(), sendWordsDynamicDataBean, letter);
                break;
            case REPORT:
                if (handleTouristControl() || mShareDynamic == null) {
                    return;
                }
                String img = "";
                if (mShareDynamic.getImages() != null && !mShareDynamic.getImages().isEmpty()) {
                    img = ImageUtils.imagePathConvertV2(mShareDynamic.getImages().get(0)
                                    .getFile(), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.report_resource_img),
                            mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.report_resource_img),
                            100);
                }
                ReportResourceBean reportResourceBean = new ReportResourceBean(mShareDynamic
                        .getUserInfoBean(), String.valueOf(mShareDynamic
                        .getId()),
                        "", img, mShareDynamic.getFeed_content(), ReportType.DYNAMIC);
                reportResourceBean.setDesCanlook(mShareDynamic.getPaid_node() == null ||
                        mShareDynamic
                                .getPaid_node().isPaid());
                ReportActivity.startReportActivity(((BaseFragment) mRootView).getActivity(), reportResourceBean);
                mRootView.showBottomView(true);
                break;
            case COLLECT:
                if (!TouristConfig.DYNAMIC_CAN_COLLECT && handleTouristControl
                        ()) {
                    return;
                }
                handleCollect(mShareDynamic);
                mRootView.showBottomView(true);
                break;
            case LETTER:
                hasImage = mShareDynamic.getImages() != null && !mShareDynamic.getImages().isEmpty();
                hasVideo = mShareDynamic.getVideo() != null;
                if (!hasImage && !hasVideo) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD;
                    content = mShareDynamic.getFriendlyContent();
                }
                if (hasImage) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE;
                    content = LetterPopWindow.PIC;

                }
                if (hasVideo) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_VIDEO;
                    content = LetterPopWindow.VIDEO;
                }
                letter = new Letter(mShareDynamic.getUserInfoBean().getName(), content, TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC);
                letter.setId(mShareDynamic.getId() + "");
                letter.setDynamic_type(dynamicType);
                ChooseFriendActivity.startChooseFriendActivity(((BaseFragment) mRootView).getActivity(), letter);
                break;
            case DELETE:
                mRootView.showDeleteTipPopupWindow(mShareDynamic);
                break;
            case STICKTOP:
                StickTopFragment.startSticTopActivity(((BaseFragment) mRootView).getActivity(), StickTopFragment
                        .TYPE_DYNAMIC, mShareDynamic.getId());
                break;
            default:
        }
    }

    @Override
    public void deleteDynamic(DynamicDetailBeanV2 dynamicBean) {
        super.deleteDynamic(dynamicBean);
        if (dynamicBean != null) {
            mRootView.getCurrentTopic().setFeeds_count(mRootView.getCurrentTopic().getFeeds_count() - 1);
            mRootView.updateCount(mRootView.getCurrentTopic().getFeeds_count(), mRootView.getCurrentTopic().getFollowers_count());
        }
    }
}

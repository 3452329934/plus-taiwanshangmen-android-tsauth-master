package com.zhiyicx.thinksnsplus.modules.circle.detailv2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.baseproject.share.OnShareCallbackListener;
import com.zhiyicx.baseproject.share.Share;
import com.zhiyicx.baseproject.share.ShareContent;
import com.zhiyicx.baseproject.share.SharePolicy;
import com.zhiyicx.common.base.BaseFragment;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.circle.CircleSearchHistoryBean;
import com.zhiyicx.thinksnsplus.data.beans.circle.CircleZipBean;
import com.zhiyicx.thinksnsplus.data.beans.report.ReportResourceBean;
import com.zhiyicx.thinksnsplus.data.source.local.CircleInfoGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.CirclePostCommentBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.CirclePostListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.CircleSearchBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.PostTypeChoosePopAdapter;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailFragment;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportType;
import com.zhiyicx.thinksnsplus.modules.wallet.sticktop.StickTopFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.utils.TSShareUtils;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhiyicx.thinksnsplus.config.EventBusTagConfig.EVENT_UPDATE_CIRCLE_POST;
import static com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository.CircleMinePostType.PUBLISH;
import static com.zhiyicx.thinksnsplus.modules.q_a.search.list.qa.QASearchListPresenter.DEFAULT_FIRST_SHOW_HISTORY_SIZE;

/**
 * @author Jliuer
 * @Date 2017/11/22/14:36
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CircleDetailPresenter extends AppBasePresenter<CircleDetailContract.View>
        implements CircleDetailContract.Presenter, OnShareCallbackListener {

    CirclePostCommentBeanGreenDaoImpl mCirclePostCommentBeanGreenDao;
    CirclePostListBeanGreenDaoImpl mCirclePostListBeanGreenDao;
    public SharePolicy mSharePolicy;
    CircleSearchBeanGreenDaoImpl mCircleSearchBeanGreenDao;
    CircleInfoGreenDaoImpl mCircleInfoGreenDao;
    BaseCircleRepository mBaseCircleRepository;

    private Subscription mSearchSub;

    private CircleInfo mShareCircleInfo;
    private CirclePostListBean mShareCirclePostListBean;

    @Inject
    public CircleDetailPresenter(CircleDetailContract.View rootView
            , CirclePostCommentBeanGreenDaoImpl circlePostCommentBeanGreenDao
            , CirclePostListBeanGreenDaoImpl circlePostListBeanGreenDao
            , SharePolicy sharePolicy
            , CircleSearchBeanGreenDaoImpl circleSearchBeanGreenDao
            , CircleInfoGreenDaoImpl circleInfoGreenDao
            , BaseCircleRepository baseCircleRepository
    ) {
        super(rootView);
        mCirclePostCommentBeanGreenDao = circlePostCommentBeanGreenDao;
        mCirclePostListBeanGreenDao = circlePostListBeanGreenDao;
        mSharePolicy = sharePolicy;
        mCircleSearchBeanGreenDao = circleSearchBeanGreenDao;
        mCircleInfoGreenDao = circleInfoGreenDao;
        mBaseCircleRepository = baseCircleRepository;
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {

        Integer excellent = null;
        if (BaseCircleRepository.CircleMinePostType.EXCELLENT.equals(mRootView.getCircleMinePostType())) {
            excellent = 1;
        }
        // 需要头信息
        if (mRootView.isNeedHeaderInfo()) {
            if (!isLoadMore) {
                Subscription subscribe = Observable.zip(mBaseCircleRepository.getCircleInfo(mRootView.getCircleId()), mBaseCircleRepository
                                .getPostListFromCircle
                                        (mRootView.getCircleId(), maxId, mRootView.getType(), excellent),
                        CircleZipBean::new)
                        .map(circleZipBean -> {
                            List<CirclePostListBean> data = circleZipBean.getCirclePostListBeanList();
                            int pinnedCount = 0;
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(i).getPinned()) {
                                    pinnedCount++;
                                }
                                List<CirclePostCommentBean> circlePostCommentBeans = mCirclePostCommentBeanGreenDao.getMySendingComment(data.get(i)
                                        .getMaxId().intValue());
                                if (!circlePostCommentBeans.isEmpty()) {
                                    circlePostCommentBeans.addAll(data.get(i).getComments());
                                    data.get(i).getComments().clear();
                                    data.get(i).getComments().addAll(circlePostCommentBeans);
                                }
                            }
                            circleZipBean.setPinnedCount(pinnedCount);
                            return circleZipBean;
                        }).subscribe(new BaseSubscribeForV2<CircleZipBean>() {
                            @Override
                            protected void onSuccess(CircleZipBean data) {
                                mRootView.onNetResponseSuccess(data.getCirclePostListBeanList(), isLoadMore);
                                mRootView.allDataReady(data);
                                mCircleInfoGreenDao.insertOrReplace(data.getCircleInfo());
                            }

                            @Override
                            protected void onFailure(String message, int code) {
                                super.onFailure(message, code);
                                mRootView.onResponseError(null, isLoadMore);
                            }

                            @Override
                            protected void onException(Throwable throwable) {
                                super.onException(throwable);
                                mRootView.onResponseError(throwable, isLoadMore);
                            }
                        });
                addSubscrebe(subscribe);

            } else {
                Subscription subscribe = mBaseCircleRepository
                        .getPostListFromCircle(mRootView.getCircleId(), maxId, mRootView.getType(), excellent)
                        .map(data -> {
                            for (int i = 0; i < data.size(); i++) {
                                List<CirclePostCommentBean> circlePostCommentBeans = mCirclePostCommentBeanGreenDao.getMySendingComment(data.get(i)
                                        .getMaxId().intValue());
                                if (!circlePostCommentBeans.isEmpty()) {
                                    circlePostCommentBeans.addAll(data.get(i).getComments());
                                    data.get(i).getComments().clear();
                                    data.get(i).getComments().addAll(circlePostCommentBeans);
                                }
                            }
                            return data;
                        }).subscribe(new BaseSubscribeForV2<List<CirclePostListBean>>() {
                            @Override
                            protected void onSuccess(List<CirclePostListBean> data) {
                                mRootView.onNetResponseSuccess(data, isLoadMore);
                            }

                            @Override
                            protected void onFailure(String message, int code) {
                                super.onFailure(message, code);
                                mRootView.onResponseError(null, isLoadMore);
                            }

                            @Override
                            protected void onException(Throwable throwable) {
                                super.onException(throwable);
                                mRootView.onResponseError(throwable, isLoadMore);
                            }
                        });
                addSubscrebe(subscribe);
            }

        } else {
            switch (mRootView.getCircleMinePostType()) {
                case PUBLISH:
                case HAD_PINNED:
                case WAIT_PINNED_AUDIT:
                    Subscription subscribe = mBaseCircleRepository.getMinePostList(TSListFragment.DEFAULT_PAGE_SIZE, maxId.intValue(), mRootView
                            .getCircleMinePostType().value)
                            .subscribe(new BaseSubscribeForV2<List<CirclePostListBean>>() {
                                @Override
                                protected void onSuccess(List<CirclePostListBean> data) {
                                    mRootView.onNetResponseSuccess(data, isLoadMore);

                                }

                                @Override
                                protected void onFailure(String message, int code) {
                                    super.onFailure(message, code);
                                    mRootView.onResponseError(null, isLoadMore);
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
                        mRootView.hideRefreshState(isLoadMore);
                        return;
                    }
                    mSearchSub = mBaseCircleRepository.getAllePostList(TSListFragment.DEFAULT_PAGE_SIZE, maxId.intValue(), searchContent, mRootView
                            .getCircleId())
                            .subscribe(new BaseSubscribeForV2<List<CirclePostListBean>>() {
                                @Override
                                protected void onSuccess(List<CirclePostListBean> data) {
                                    // 历史记录存入数据库
                                    saveSearhDatq(searchContent);
                                    mRootView.onNetResponseSuccess(data, isLoadMore);
                                }

                                @Override
                                protected void onFailure(String message, int code) {
                                    super.onFailure(message, code);
                                    mRootView.onResponseError(null, isLoadMore);
                                }

                                @Override
                                protected void onException(Throwable throwable) {
                                    super.onException(throwable);
                                    mRootView.onResponseError(throwable, isLoadMore);
                                }
                            });
                    addSubscrebe(mSearchSub);

                    break;
                case COLLECT:
                    Subscription collectSubscribe = mBaseCircleRepository.getUserCollectPostList(TSListFragment.DEFAULT_PAGE_SIZE, maxId.intValue())
                            .subscribe(new BaseSubscribeForV2<List<CirclePostListBean>>() {
                                @Override
                                protected void onSuccess(List<CirclePostListBean> data) {
                                    mRootView.onNetResponseSuccess(data, isLoadMore);
                                }

                                @Override
                                protected void onFailure(String message, int code) {
                                    super.onFailure(message, code);
                                    mRootView.onResponseError(null, isLoadMore);
                                }

                                @Override
                                protected void onException(Throwable throwable) {
                                    super.onException(throwable);
                                    mRootView.onResponseError(throwable, isLoadMore);
                                }
                            });
                    addSubscrebe(collectSubscribe);
                    break;
                case LATEST_POST:
                case LATEST_REPLY:
                case EXCELLENT:
                    String type = PostTypeChoosePopAdapter.MyPostTypeEnum.LATEST_POST.value;
                    if (BaseCircleRepository.CircleMinePostType.LATEST_REPLY.equals(mRootView.getCircleMinePostType())) {
                        type = PostTypeChoosePopAdapter.MyPostTypeEnum.LATEST_COMMENT.value;
                    }
                    subscribe = mBaseCircleRepository
                            .getPostListFromCircle(mRootView.getCircleId(), maxId, type, excellent)
                            .map(data -> {
                                for (int i = 0; i < data.size(); i++) {
                                    List<CirclePostCommentBean> circlePostCommentBeans = mCirclePostCommentBeanGreenDao.getMySendingComment(data.get(i)
                                            .getMaxId().intValue());
                                    if (!circlePostCommentBeans.isEmpty()) {
                                        circlePostCommentBeans.addAll(data.get(i).getComments());
                                        data.get(i).getComments().clear();
                                        data.get(i).getComments().addAll(circlePostCommentBeans);
                                    }
                                }
                                return data;
                            }).subscribe(new BaseSubscribeForV2<List<CirclePostListBean>>() {
                                @Override
                                protected void onSuccess(List<CirclePostListBean> data) {
                                    mRootView.onNetResponseSuccess(data, isLoadMore);
                                }

                                @Override
                                protected void onFailure(String message, int code) {
                                    super.onFailure(message, code);
                                    mRootView.onResponseError(null, isLoadMore);
                                }

                                @Override
                                protected void onException(Throwable throwable) {
                                    super.onException(throwable);
                                    mRootView.onResponseError(throwable, isLoadMore);
                                }
                            });
                    addSubscrebe(subscribe);
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(new ArrayList<>(), isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<CirclePostListBean> data, boolean isLoadMore) {
        mCirclePostListBeanGreenDao.saveMultiData(data);
        return isLoadMore;
    }

    @Override
    public void reSendComment(CirclePostCommentBean creatComment, long feed_id) {
        creatComment.setState(CirclePostCommentBean.SEND_ING);
        creatComment.setId(-1L);
        mRootView.refreshData();
        mCirclePostCommentBeanGreenDao.insertOrReplace(creatComment);
        mBaseCircleRepository.sendPostComment(creatComment.getContent(), feed_id,
                creatComment.getReply_to_user_id(),
                creatComment.getComment_mark());
    }

    @Override
    public void deleteComment(CirclePostListBean circlePostListBean, int postPositon, Long commentId, int commentPosition) {
        mRootView.getListDatas().get(postPositon).setComments_count(circlePostListBean.getComments_count() - 1);
        mCirclePostListBeanGreenDao.insertOrReplace(mRootView.getListDatas().get(postPositon));

        if (!circlePostListBean.getComments().isEmpty()) {
            mCirclePostCommentBeanGreenDao.deleteSingleCache(circlePostListBean.getComments().get(commentPosition));
            mRootView.getListDatas().get(postPositon).getComments().remove(commentPosition);
        }

        mRootView.refreshData(postPositon);
        mBaseCircleRepository.deletePostComment(circlePostListBean.getId(), commentId);
    }

    @Override
    public void sendComment(int mCurrentPostion, long replyToUserId, String commentContent) {
        CirclePostCommentBean creatComment = new CirclePostCommentBean();
        creatComment.setState(CirclePostCommentBean.SEND_ING);
        creatComment.setContent(commentContent);
        creatComment.setId(-1L);
        String comment_mark = AppApplication.getmCurrentLoginAuth().getUser_id() + "" + System.currentTimeMillis();
        creatComment.setComment_mark(Long.parseLong(comment_mark));
        creatComment.setCircle_id((int) mRootView.getListDatas().get(mCurrentPostion).getGroup_id());
        creatComment.setPost_id(mRootView.getListDatas().get(mCurrentPostion).getId().intValue());
        creatComment.setReply_to_user_id(replyToUserId);
        //当回复帖子的时候
        if (replyToUserId == 0) {
            UserInfoBean userInfoBean = new UserInfoBean();
            userInfoBean.setUser_id(replyToUserId);
            creatComment.setReplyUser(userInfoBean);
        } else {
            creatComment.setReplyUser(mUserInfoBeanGreenDao.getSingleDataFromCache(replyToUserId));
        }
        creatComment.setUser_id(AppApplication.getmCurrentLoginAuth().getUser_id());
        creatComment.setCommentUser(mUserInfoBeanGreenDao.getSingleDataFromCache(AppApplication.getMyUserIdWithdefault()));
        creatComment.setCreated_at(TimeUtils.getCurrenZeroTimeStr());
        List<CirclePostCommentBean> commentBeanList = new ArrayList<>();
        commentBeanList.add(creatComment);
        commentBeanList.addAll(mRootView.getListDatas().get(mCurrentPostion).getComments());
        mRootView.getListDatas().get(mCurrentPostion).getComments().clear();
        mRootView.getListDatas().get(mCurrentPostion).getComments().addAll(commentBeanList);
        mRootView.getListDatas().get(mCurrentPostion).setComments_count(mRootView.getListDatas().get(mCurrentPostion).getComments_count() + 1);
        mRootView.refreshData();

        mCirclePostCommentBeanGreenDao.insertOrReplace(creatComment);
        mBaseCircleRepository.sendPostComment(commentContent,
                mRootView.getListDatas().get(mCurrentPostion).getId(),
                replyToUserId,
                creatComment.getComment_mark());
    }

    @Override
    public void deletePost(CirclePostListBean circlePostListBean, int position) {
        if (position == -1) {
            return;
        }
        mCirclePostListBeanGreenDao.deleteSingleCache(circlePostListBean);
        mRootView.getListDatas().remove(position);
//        if (mRootView.getListDatas().isEmpty()) {
//            mRootView.getListDatas().add(new CirclePostListBean());
//        }
        mRootView.refreshData();
        if (circlePostListBean.getId() != null && circlePostListBean.getId() != 0) {
            mBaseCircleRepository.deletePost(circlePostListBean.getGroup_id(), circlePostListBean.getId());
        }
    }

    /**
     * 分享帖子
     *
     * @param circlePostListBean
     * @param shareBitMap
     */
    @Override
    public void sharePost(CirclePostListBean circlePostListBean, Bitmap shareBitMap) {
        mShareCircleInfo = null;
        mShareCirclePostListBean = circlePostListBean;
        ((UmengSharePolicyImpl) mSharePolicy).setOnShareCallbackListener(this);
        ShareContent shareContent = new ShareContent();
        shareContent.setTitle(circlePostListBean.getTitle());
        shareContent.setContent(TextUtils.isEmpty(circlePostListBean.getSummary()) ? mContext.getString(R.string
                .share_default, mContext.getString(R.string.app_name)) : circlePostListBean.getSummary());
        if (shareBitMap != null) {
            shareContent.setBitmap(shareBitMap);
        } else {
            shareContent.setBitmap(ConvertUtils.drawBg4Bitmap(Color.WHITE, BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon)));
        }
        shareContent.setUrl(TSShareUtils.convert2ShareUrl(String.format(ApiConfig.APP_PATH_SHARE_GROUNP_DYNAMIC, circlePostListBean.getGroup_id(),
                circlePostListBean.getId())));
        mSharePolicy.setShareContent(shareContent);
        List<UmengSharePolicyImpl.ShareBean> data = new ArrayList<>();

        boolean isManager = false;
        CircleInfo circleInfo = circlePostListBean.getGroup();
        if (circleInfo.getJoined() != null) {
            isManager = CircleMembers.FOUNDER.equals(circleInfo.getJoined().getRole()) ||
                    CircleMembers.ADMINISTRATOR.equals(circleInfo.getJoined().getRole());
        }
        boolean isPinned = circlePostListBean.getPinned() || BaseCircleRepository.CircleMinePostType.HAD_PINNED == mRootView.getCircleMinePostType();
        boolean isCollected = circlePostListBean.hasCollected();
        boolean isExcellent = circlePostListBean.getExcellent_at() != null;
        boolean isSlef = circlePostListBean.getUser_id() == AppApplication.getMyUserIdWithdefault();

        UmengSharePolicyImpl.ShareBean forward = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_forwarding, mContext.getString(R.string.share_forward), Share.FORWARD);
        UmengSharePolicyImpl.ShareBean sticktop;
        UmengSharePolicyImpl.ShareBean collect = new UmengSharePolicyImpl.ShareBean(isCollected ? R.mipmap.detail_share_clt_hl : R.mipmap.detail_share_clt, mContext.getString(isCollected ? R.string.dynamic_list_collected_dynamic : R
                .string.dynamic_list_collect_dynamic), Share.COLLECT);

        UmengSharePolicyImpl.ShareBean excellent = new UmengSharePolicyImpl.ShareBean(isExcellent ? R.mipmap.ico_cancel : R.mipmap.ico_essence, mContext.getString(!isExcellent ? R.string.dynamic_list_excellent_post : R
                .string.dynamic_list_unexcellent_post), Share.EXCELLENT);

        UmengSharePolicyImpl.ShareBean letter = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_sent, mContext.getString(R.string.share_letter), Share.LETTER);
        UmengSharePolicyImpl.ShareBean delete = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_det, mContext.getString(R.string.share_delete), Share.DELETE);


        if (isManager) {
            sticktop = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_top, mContext.getString(isPinned ? R.string.post_undo_top : R.string.post_apply_top), Share.STICKTOP);
            data.add(forward);
            data.add(letter);
            data.add(excellent);
            data.add(sticktop);
            data.add(delete);
        } else {
            if (isSlef) {
                data.add(forward);
                data.add(letter);
                data.add(collect);
                if (!isPinned) {
                    sticktop = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_top, mContext.getString(R.string.share_sticktp), Share.STICKTOP);
                    data.add(sticktop);
                }
                data.add(delete);
            } else {
                UmengSharePolicyImpl.ShareBean report = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_report, mContext.getString(R.string.share_report), Share.REPORT);
                data.add(forward);
                data.add(letter);
                data.add(report);
                data.add(collect);
            }
        }
        mSharePolicy.showShare(((TSFragment) mRootView).getActivity(), data);
    }

    /**
     * 分享圈子
     *
     * @param circleinfo
     * @param shareBitMap
     */
    @Override
    public void shareCircle(CircleInfo circleinfo, Bitmap shareBitMap, List<UmengSharePolicyImpl.ShareBean> data) {
        mShareCircleInfo = circleinfo;
        mShareCirclePostListBean = null;
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
        shareContent.setUrl(TSShareUtils.convert2ShareUrl(String.format(ApiConfig.APP_PATH_SHARE_GROUP, circleinfo.getId(), mRootView.getType())));
        mSharePolicy.setShareContent(shareContent);
        mSharePolicy.showShare(((TSFragment) mRootView).getActivity(), data);
    }

    @Override
    public void onStart(Share share) {
        Letter letter;
        switch (share) {
            case FORWARD:
                letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE);
                if (mShareCircleInfo == null) {
                    if (mShareCirclePostListBean == null) {
                        return;
                    }
                    // 帖子
                    StringBuilder content = new StringBuilder(mShareCirclePostListBean.getFriendlyContent() + "");
                    if (mShareCirclePostListBean.getImages() != null && !mShareCirclePostListBean.getImages().isEmpty()) {
                        int id = mShareCirclePostListBean.getImages().get(0).getFile_id();
                        String image = ImageUtils.imagePathConvertV2(id, mContext.getResources()
                                        .getDimensionPixelOffset(R.dimen.chat_post_image_widht), mContext.getResources()
                                        .getDimensionPixelOffset(R.dimen.chat_post_image_widht),
                                ImageZipConfig.IMAGE_80_ZIP);
                        letter.setImage(image);
                        for (CirclePostListBean.ImagesBean imagesBean : mShareCirclePostListBean.getImages()) {
                            content.append(MarkdownConfig.IMAGE_REPLACE);
                        }
                    }
                    letter.setCircle_id(mShareCirclePostListBean.getGroup_id() + "");
                    letter.setType(TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST);
                    letter.setName(mShareCirclePostListBean.getTitle());
                    letter.setContent(content.toString());
                    letter.setId(mShareCirclePostListBean.getId() + "");

                } else {
                    // 圈子
                    letter.setName(mShareCircleInfo.getName());
                    String content = TextUtils.isEmpty(mShareCircleInfo.getSummary()) ? mContext.getString(R.string
                            .share_default, mContext.getString(R.string.app_name)) : mShareCircleInfo.getSummary();
                    letter.setContent(content);
                    letter.setImage(mShareCircleInfo.getAvatar() != null ? mShareCircleInfo.getAvatar().getUrl() : "");
                    letter.setId(mShareCircleInfo.getId() + "");
                }
                SendDynamicDataBean sendWordsDynamicDataBean = new SendDynamicDataBean();
                sendWordsDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
                sendWordsDynamicDataBean.setDynamicType(SendDynamicDataBean.TEXT_ONLY_DYNAMIC);
                SendDynamicActivity.startToSendDynamicActivity(((BaseFragment) mRootView).getActivity(), sendWordsDynamicDataBean, letter);
                break;
            case REPORT:
                if (handleTouristControl() || mShareCirclePostListBean == null) {
                    return;
                }
                String img = "";
                if (mShareCirclePostListBean.getImages() != null && !mShareCirclePostListBean.getImages().isEmpty()) {
                    img = ImageUtils.imagePathConvertV2(mShareCirclePostListBean.getImages().get(0).getFile_id(), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.report_resource_img), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.report_resource_img),
                            100);
                }
                // 预览的文字
                String des = mShareCirclePostListBean.getSummary();
                if (!TextUtils.isEmpty(des)) {
                    des = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, mShareCirclePostListBean.getSummary());
                }
                ReportActivity.startReportActivity(((BaseFragment) mRootView).getActivity(), new ReportResourceBean(mShareCirclePostListBean.getUser(), String.valueOf
                        (mShareCirclePostListBean.getId()),
                        mShareCirclePostListBean.getTitle(), img, des, ReportType.CIRCLE_POST));
                mRootView.showBottomView(true);
                break;
            case COLLECT:
                if (!TouristConfig.DYNAMIC_CAN_COLLECT && handleTouristControl
                        ()) {
                    return;
                }
                int index = mRootView.getListDatas().indexOf(mShareCirclePostListBean);
                mRootView.handleCollect(index);
                mRootView.showBottomView(true);
                break;
            case EXCELLENT:
                if (handleTouristControl()) {
                    return;
                }
                mRootView.handleExcellent(mRootView.getListDatas().indexOf(mShareCirclePostListBean));
                mRootView.showBottomView(true);
                break;
            case LETTER:
                letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_CIRCLE);
                if (mShareCircleInfo == null) {
                    if (mShareCirclePostListBean == null) {
                        return;
                    }
                    // 帖子
                    if (mShareCirclePostListBean.getImages() != null && !mShareCirclePostListBean.getImages().isEmpty()) {
                        int id = mShareCirclePostListBean.getImages().get(0).getFile_id();
                        String image = ImageUtils.imagePathConvertV2(id, mContext.getResources()
                                        .getDimensionPixelOffset(R.dimen.chat_post_image_widht), mContext.getResources()
                                        .getDimensionPixelOffset(R.dimen.chat_post_image_widht),
                                ImageZipConfig.IMAGE_80_ZIP);
                        letter.setImage(image);
                    }
                    letter.setCircle_id(mShareCirclePostListBean.getGroup_id() + "");
                    letter.setType(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_POST);
                    letter.setName(mShareCirclePostListBean.getTitle());
                    letter.setContent(mShareCirclePostListBean.getFriendlyContent());
                    letter.setId(mShareCirclePostListBean.getId() + "");

                } else {
                    // 圈子
                    letter.setName(mShareCircleInfo.getName());
                    String content = TextUtils.isEmpty(mShareCircleInfo.getSummary()) ? mContext.getString(R.string
                            .share_default, mContext.getString(R.string.app_name)) : mShareCircleInfo.getSummary();
                    letter.setContent(content);
                    letter.setImage(mShareCircleInfo.getAvatar() != null ? mShareCircleInfo.getAvatar().getUrl() : "");
                    letter.setId(mShareCircleInfo.getId() + "");

                }
                ChooseFriendActivity.startChooseFriendActivity(((BaseFragment) mRootView).getActivity(), letter);
                break;
            case DELETE:
                mRootView.showDeleteTipPopupWindow(mShareCirclePostListBean);
                mRootView.showBottomView(true);
                break;
            case STICKTOP:
                if (mShareCirclePostListBean == null) {
                    return;
                }
                boolean isManager = false;
                CircleInfo circleInfo = mShareCirclePostListBean.getGroup();
                if (circleInfo.getJoined() != null) {
                    isManager = CircleMembers.FOUNDER.equals(circleInfo.getJoined().getRole()) ||
                            CircleMembers.ADMINISTRATOR.equals(circleInfo.getJoined().getRole());
                }
                boolean isPinned = mShareCirclePostListBean.getPinned() || BaseCircleRepository.CircleMinePostType.HAD_PINNED == mRootView.getCircleMinePostType();

                if (isManager) {
                    int position = mRootView.getListDatas().indexOf(mShareCirclePostListBean);
                    if (isPinned) {
                        undoTopPost(mShareCirclePostListBean.getId(), position);
                    } else {
                        mRootView.managerStickTop(mShareCirclePostListBean.getId(), position);
                    }
                } else {
                    StickTopFragment.startSticTopActivity(((BaseFragment) mRootView).getActivity(), StickTopFragment
                            .TYPE_POST, mShareCirclePostListBean.getId());
                }
                mRootView.showBottomView(true);
                break;
            default:
        }
    }

    @Override
    public void handleLike(boolean isLiked, Long postId, int dataPosition) {
        if (postId == 0) {
            return;
        }
        mCirclePostListBeanGreenDao.insertOrReplace(mRootView.getListDatas().get(dataPosition));
        mBaseCircleRepository.dealLike(isLiked, postId);
    }

    @Override
    public void handleCollect(CirclePostListBean circlePostListBean) {
        // 修改数据-更新界面
        boolean isCollection = circlePostListBean.getCollected();
        // 通知服务器
        mBaseCircleRepository.dealCollect(isCollection, circlePostListBean.getId());
    }

    @Override
    public void handleExcellent(CirclePostListBean circlePostListBean) {
        // 通知服务器
        mBaseCircleRepository.dealExcellent(circlePostListBean.getId());
    }

    @Override
    public void handleViewCount(Long postId, int position) {
        if (postId == null || postId == 0) {
            return;
        }
        mRootView.getListDatas().get(position).setViews_count(mRootView.getListDatas().get(position).getViews_count() + 1);
        mCirclePostListBeanGreenDao.insertOrReplace(mRootView.getListDatas().get(position));
        mRootView.refreshData(position);
    }

    @Override
    public int getCurrenPosiotnInDataList(Long id) {
        int position = -1;
        int size = mRootView.getListDatas().size();
        for (int i = 0; i < size; i++) {
            if (id.intValue() == mRootView.getListDatas().get(i).getId()) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public void dealCircleJoinOrExit(CircleInfo circleInfo,String psd) {

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
                    .flatMap(o -> mBaseCircleRepository.dealCircleJoinOrExit(circleInfo,psd));
        } else {
            observable = mBaseCircleRepository.dealCircleJoinOrExit(circleInfo,null)
                    .doOnSubscribe(() -> {
                                mRootView.dismissSnackBar();
                                mRootView.showSnackLoadingMessage(mContext.getString(R.string.circle_dealing));
                            }
                    );

        }
        Subscription subscribe = observable
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


    @Subscriber(tag = EventBusTagConfig.EVENT_SEND_COMMENT_TO_CIRCLE_POST)
    public void handleSendComment(CirclePostCommentBean circlePostCommentBean) {
        Subscription subscription = Observable.just(circlePostCommentBean)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(circlePostCommentBean1 -> {
                    int size = mRootView.getListDatas().size();
                    int postPosition = -1;
                    for (int i = 0; i < size; i++) {
                        if (mRootView.getListDatas().get(i).getId().intValue() == circlePostCommentBean1.getPost_id()) {
                            postPosition = i;
                            break;
                        }
                    }
                    if (postPosition != -1) {// 如果列表有当前评论
                        int commentSize = mRootView.getListDatas().get(postPosition).getComments().size();
                        for (int i = 0; i < commentSize; i++) {
                            if (mRootView.getListDatas().get(postPosition).getComments().get(i).getPost_id() == circlePostCommentBean1.getPost_id()) {
                                mRootView.getListDatas().get(postPosition).getComments().get(i).setState(circlePostCommentBean1.getState());
                                mRootView.getListDatas().get(postPosition).getComments().get(i).setId(circlePostCommentBean1.getId());
                                mRootView.getListDatas().get(postPosition).getComments().get(i).setPost_id(circlePostCommentBean1.getPost_id());
                                break;
                            }
                        }
                    }
                    return postPosition;
                })
                .subscribe(integer -> {
                    if (integer != -1) {
                        mRootView.refreshData();
                    }

                }, throwable -> throwable.printStackTrace());

        addSubscrebe(subscription);

    }

    @Override
    public void onSuccess(Share share) {
        mRootView.showSnackSuccessMessage(mContext.getString(R.string.share_sccuess));
    }

    @Override
    public void onError(Share share, Throwable throwable) {
        mRootView.showSnackErrorMessage(mContext.getString(R.string.share_fail));
    }

    @Override
    public void onCancel(Share share) {
        mRootView.showSnackSuccessMessage(mContext.getString(R.string.share_cancel));
    }

    /**
     * 存搜索记录
     *
     * @param searchContent
     */
    private void saveSearhDatq(String searchContent) {
        CircleSearchHistoryBean cricleSearchHistoryBean = new CircleSearchHistoryBean(searchContent, CircleSearchHistoryBean.TYPE_CIRCLE_POST);
        cricleSearchHistoryBean.setOutSideCircle(mRootView.isOutsideSerach());
        if (!mRootView.isOutsideSerach()) {
            cricleSearchHistoryBean.setCircleId(mRootView.getCircleId());
        }
        mCircleSearchBeanGreenDao.saveHistoryDataByType(cricleSearchHistoryBean, CircleSearchHistoryBean.TYPE_CIRCLE_POST);
    }


    @Override
    public List<CircleSearchHistoryBean> getFirstShowHistory() {
        boolean isOutsideSerach = mRootView.isOutsideSerach();
        return mCircleSearchBeanGreenDao.getFristShowData(DEFAULT_FIRST_SHOW_HISTORY_SIZE,
                CircleSearchHistoryBean.TYPE_CIRCLE_POST, mRootView.isOutsideSerach(), mRootView.getCircleId());
    }

    @Override
    public void cleaerAllSearchHistory() {
        mCircleSearchBeanGreenDao.clearAllQASearchHistory();
    }

    @Override
    public List<CircleSearchHistoryBean> getAllSearchHistory() {
        return mCircleSearchBeanGreenDao.getCirclePostSearchHistory();
    }

    @Override
    public void deleteSearchHistory(CircleSearchHistoryBean qaSearchHistoryBean) {
        mCircleSearchBeanGreenDao.deleteSingleCache(qaSearchHistoryBean);
    }

    @Override
    public void stickTopPost(Long postId, int position, int day) {
        Subscription subscribe = mBaseCircleRepository.stickTopPost(postId, day)
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R.string.circle_dealing)))
                .subscribe(new BaseSubscribeForV2<BaseJsonV2>() {
                    @Override
                    protected void onSuccess(BaseJsonV2 data) {
                        mRootView.showSnackSuccessMessage(mContext.getString(R.string.post_top_success));
                        // 我发布的页面不需要置顶刷新和置顶标识
                        if (mRootView.isFromMine()) {
                            return;
                        }
                        CirclePostListBean old = mRootView.getListDatas().get(position);
                        CirclePostListBean currentPost = (CirclePostListBean) old.clone();
                        mRootView.getListDatas().remove(old);
                        if (currentPost != null) {
                            currentPost.setPinned(true);
                            mRootView.getListDatas().add(0, currentPost);
                            mRootView.scrollToTop();
                        }
                        if (mRootView.getCircleZipBean() != null) {
                            mRootView.getCircleZipBean().setPinnedCount(mRootView.getCircleZipBean().getPinnedCount() + 1);
                        }
                        mRootView.refreshData();
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.post_top_failed));
                    }
                });
        addSubscrebe(subscribe);
    }

    @Override
    public void undoTopPost(Long postId, int position) {
        Subscription subscribe = mBaseCircleRepository.undoTopPost(postId)
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R.string.circle_dealing)))
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<Object> data) {
                        mRootView.showSnackSuccessMessage(data.getMessage().get(0));
                        mRootView.getListDatas().remove(mRootView.getListDatas().get(position));
                        if (mRootView.getCircleZipBean() != null) {
                            mRootView.getCircleZipBean().setPinnedCount(mRootView.getCircleZipBean().getPinnedCount() - 1);
                        }
                        mRootView.refreshData();
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.showSnackErrorMessage(throwable.getMessage());
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 详情界面处理了数据
     * 处理更新数据
     *
     * @param data
     */
    @Subscriber(tag = EVENT_UPDATE_CIRCLE_POST)
    public void updatePost(Bundle data) {
        Subscription subscribe = Observable.just(data)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .map(bundle -> {
                    CirclePostListBean postListBean = bundle.getParcelable(CirclePostDetailFragment.POST_DATA);
                    boolean isNeedRefresh = bundle.getBoolean(CirclePostDetailFragment.POST_LIST_NEED_REFRESH) && postListBean != null;
                    if (isNeedRefresh) {
                        int position = mRootView.getListDatas().indexOf(postListBean);
                        if (position != -1) {
                            CirclePostListBean nowListData = mRootView.getListDatas().get(position);
                            if (postListBean.getPinned() && !nowListData.getPinned()) {
                                // 在详情页面置顶
                                CirclePostListBean pinned = (CirclePostListBean) nowListData.clone();
                                mRootView.getListDatas().add(0, pinned);
                                mRootView.scrollToTop();
                            } else if (!postListBean.getPinned() && nowListData.getPinned()) {
                                // 在详情页面撤销置顶
                                mRootView.getListDatas().remove(nowListData);
                            }
                            if (postListBean.getPinned() == nowListData.getPinned()) {
                                // 在详情页面没有置顶操作
                                mRootView.getListDatas().set(position, postListBean);
                            }
                        } else {
                            // 发帖更新到列表
                            mRootView.getListDatas().add(0, postListBean);
                        }
                    }
                    return isNeedRefresh;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isNeedRefresh -> {
                    if (isNeedRefresh) {
                        mRootView.refreshData();
                    }

                }, Throwable::printStackTrace);
        addSubscrebe(subscribe);
    }

    @Subscriber(tag = EventBusTagConfig.POST_LIST_DELETE_UPDATE)
    public void deletePost(CirclePostListBean postListBean) {
        deletePost(postListBean, mRootView.getListDatas().indexOf(postListBean));
        LogUtils.d(EventBusTagConfig.POST_LIST_DELETE_UPDATE);
    }

    @Override
    protected void unSubscribe() {
        super.unSubscribe();
        if (mSharePolicy != null) {
            ((UmengSharePolicyImpl) mSharePolicy).setOnShareCallbackListener(null);
            mSharePolicy = null;
        }
    }
}

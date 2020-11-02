package com.zhiyicx.thinksnsplus.data.source.repository;

import android.app.Application;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.gson.Gson;
import com.klinker.android.link_builder.Link;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.config.BackgroundTaskRequestMethodConfig;
import com.zhiyicx.thinksnsplus.data.beans.CommentedBean;
import com.zhiyicx.thinksnsplus.data.beans.DigedBean;
import com.zhiyicx.thinksnsplus.data.beans.MusicAlbumListBean;
import com.zhiyicx.thinksnsplus.data.beans.SimpleMusic;
import com.zhiyicx.thinksnsplus.data.beans.notify.AtMeaasgeBean;
import com.zhiyicx.thinksnsplus.data.beans.BackgroundRequestTaskBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.CollectGroupDyanmciListBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentToll;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDigListBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicListBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoListDataBean;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.SimpleAnswerBean;
import com.zhiyicx.thinksnsplus.data.beans.SimplePostBean;
import com.zhiyicx.thinksnsplus.data.beans.TopDynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.notify.UserNotifyMsgBean;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicCommentBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicDetailBeanV2GreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.TopDynamicBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.remote.CircleClient;
import com.zhiyicx.thinksnsplus.data.source.remote.DynamicClient;
import com.zhiyicx.thinksnsplus.data.source.remote.InfoMainClient;
import com.zhiyicx.thinksnsplus.data.source.remote.MusicClient;
import com.zhiyicx.thinksnsplus.data.source.remote.QAClient;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.remote.UserInfoClient;
import com.zhiyicx.thinksnsplus.modules.dynamic.IDynamicReppsitory;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.TimeStringSortClass;
import com.zhiyicx.thinksnsplus.modules.home.message.messageat.MessageAtFragment;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zhiyicx.baseproject.config.ApiConfig.DYNAMIC_TYPE_HOTS;
import static com.zhiyicx.baseproject.config.ApiConfig.DYNAMIC_TYPE_MY_COLLECTION;
import static com.zhiyicx.baseproject.config.ApiConfig.DYNAMIC_TYPE_USERS;
import static com.zhiyicx.thinksnsplus.data.beans.TopDynamicBean.TYPE_HOT;
import static com.zhiyicx.thinksnsplus.data.beans.TopDynamicBean.TYPE_NEW;


/**
 * @Describe 动态数据处理基类
 * @Author Jungle68
 * @Date 2017/1/
 * @Contact master.jungle68@gmail.com
 */

public class BaseDynamicRepository implements IDynamicReppsitory {


    public enum MyDynamicTypeEnum {
        ALL(null),
        PAID("paid"),
        PINNED("pinned");
        public String value;

        MyDynamicTypeEnum(String value) {
            this.value = value;
        }
    }


    protected DynamicClient mDynamicClient;
    protected UserInfoClient mUserInfoClient;
    protected InfoMainClient mInfoMainClient;
    protected QAClient mQAClient;
    protected CircleClient mCircleClient;
    protected MusicClient mMusicClient;

    @Inject
    protected UserInfoRepository mUserInfoRepository;
    @Inject
    protected Application mContext;

    @Inject
    DynamicCommentBeanGreenDaoImpl mDynamicCommentBeanGreenDao;

    @Inject
    DynamicDetailBeanV2GreenDaoImpl mDynamicDetailBeanV2GreenDao;

    @Inject
    TopDynamicBeanGreenDaoImpl mTopDynamicBeanGreenDao;

    @Inject
    public BaseDynamicRepository(ServiceManager serviceManager) {
        mDynamicClient = serviceManager.getDynamicClient();
        mUserInfoClient = serviceManager.getUserInfoClient();
        mInfoMainClient = serviceManager.getInfoMainClient();
        mQAClient = serviceManager.getQAClient();
        mCircleClient = serviceManager.getCircleClient();
        mMusicClient = serviceManager.getMusicClient();
    }

    @Override
    public Observable<BaseJsonV2<Object>> sendDynamicV2(SendDynamicDataBeanV2 dynamicDetailBean) {
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), gson.toJson(dynamicDetailBean));
        return mDynamicClient.sendDynamicV2(body);
    }


    @Override
    public Observable<List<DynamicDetailBeanV2>> getDynamicListV2(String type, Long after, String search, Long userId, final boolean isLoadMore,
                                                                  String chooseType, String id) {
        Observable<DynamicBeanV2> observable;
        // 收藏的动态地址和返回大不一样，真滴难受
        if (DYNAMIC_TYPE_MY_COLLECTION.equals(type)) {
            observable = mDynamicClient.getCollectDynamicListV2(after, userId, TSListFragment.DEFAULT_PAGE_SIZE)
                    .flatMap(detailBeanV2 -> {
                        DynamicBeanV2 data = new DynamicBeanV2();
                        data.setFeeds(detailBeanV2);
                        return Observable.just(data);
                    });
        } else {
            observable = mDynamicClient.getDynamicListV2(type, DYNAMIC_TYPE_HOTS.equals(type) ? null : after, search, userId, TSListFragment
                    .DEFAULT_PAGE_SIZE, chooseType, DYNAMIC_TYPE_HOTS.equals(type) ? after.intValue() : null, id);
        }
        return dealWithDynamicListV2(observable, type, search, chooseType, isLoadMore);
    }

    @Override
    public Observable<List<AtMeaasgeBean>> getAtMessages(int index, Integer limit, String direction) {
        return mUserInfoClient.getAtMessages(index, limit, direction)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Func1<List<AtMeaasgeBean>, Observable<List<AtMeaasgeBean>>>) atMeaasgeBeans -> {
                    StringBuilder feeds = new StringBuilder();
                    StringBuilder infos = new StringBuilder();
                    StringBuilder posts = new StringBuilder();
                    StringBuilder questions = new StringBuilder();
                    StringBuilder answers = new StringBuilder();
                    StringBuilder comments = new StringBuilder();

                    SparseIntArray feedSparseArray = new SparseIntArray();
                    SparseIntArray commentSparseArray = new SparseIntArray();

                    // 分离动态、评论(评论有多种类)
                    for (AtMeaasgeBean atMeaasgeBean : atMeaasgeBeans) {
                        if (atMeaasgeBean.getResourceable() != null) {
                            int index1 = atMeaasgeBeans.indexOf(atMeaasgeBean);
                            if (MessageAtFragment.FEED.equals(atMeaasgeBean.getResourceable().getType())) {
                                // feed
                                feedSparseArray.append(atMeaasgeBean.getResourceable().getId().intValue(), index1);
                                feeds.append(atMeaasgeBean.getResourceable().getId());
                                feeds.append(",");
                                atMeaasgeBean.setAt_type(MessageAtFragment.FEED);
                            } else if (MessageAtFragment.COMMENTS.equals(atMeaasgeBean.getResourceable().getType())) {
                                // comment
                                commentSparseArray.append(atMeaasgeBean.getResourceable().getId().intValue(), index1);
                                comments.append(atMeaasgeBean.getResourceable().getId());
                                atMeaasgeBean.setAt_type(MessageAtFragment.COMMENTS);
                                comments.append(",");
                            }
                        }
                    }

                    Observable<List<AtMeaasgeBean>> commentObservable;
                    if (TextUtils.isEmpty(comments.toString())) {
                        commentObservable = Observable.just(new ArrayList<>());
                    } else {
                        commentObservable = mUserInfoRepository.getAllComments(null, 0, null, null,
                                null, null, null, null,
                                comments.toString());
                    }
                    Long[] maxId = new Long[]{1L};
                    if (!atMeaasgeBeans.isEmpty()) {
                        AtMeaasgeBean lastMsg = atMeaasgeBeans.get(atMeaasgeBeans.size() - 1);
                        maxId[0] = lastMsg.getId();
                    }

                    // 获取评论
                    return commentObservable
                            .flatMap((Func1<List<AtMeaasgeBean>, Observable<List<AtMeaasgeBean>>>) flat -> {
                                for (AtMeaasgeBean comment : flat) {
                                    int index1 = commentSparseArray.get(comment.getId().intValue());
                                    comment.setAt_type(atMeaasgeBeans.get(index1).getResourceable().getType());
                                    atMeaasgeBeans.set(index1, comment);

                                    // 解析评论内容
                                    if (comment.getResourceable() != null) {
                                        switch (comment.getResourceable().getType()) {
                                            case MessageAtFragment.FEED:
                                                // feed
                                                feeds.append(comment.getResourceable().getId());
                                                feeds.append(",");
                                                break;
                                            case MessageAtFragment.INFO:
                                                // info
                                                infos.append(comment.getResourceable().getId());
                                                infos.append(",");
                                                break;
                                            case MessageAtFragment.POST:
                                                // post
                                                posts.append(comment.getResourceable().getId());
                                                posts.append(",");
                                                break;
                                            case MessageAtFragment.QUESTION:
                                                // question
                                                questions.append(comment.getResourceable().getId());
                                                questions.append(",");
                                                break;
                                            case MessageAtFragment.ANSWER:
                                                // answer
                                                answers.append(comment.getResourceable().getId());
                                                answers.append(",");
                                                break;
                                            default:
                                        }
                                    }
                                }

                                // 获取上级资源
                                // 动态
                                Observable<List<DynamicDetailBeanV2>> feedObservable = getDynamicListV2(null, null, null, null,
                                        false, null, feeds.toString());
                                if (TextUtils.isEmpty(feeds.toString())) {
                                    feedObservable = Observable.just(new ArrayList<>());
                                }

                                // 资讯
                                Observable<List<InfoListDataBean>> infoObservable = mInfoMainClient.getInfoList(null
                                        , null, null, null, null, infos.toString());
                                if (TextUtils.isEmpty(infos.toString())) {
                                    infoObservable = Observable.just(new ArrayList<>());
                                }

                                // 问题
                                Observable<List<QAListInfoBean>> questionObservable = mQAClient.getQAQustion(null
                                        , null, null, null, questions.toString());
                                if (TextUtils.isEmpty(questions.toString())) {
                                    questionObservable = Observable.just(new ArrayList<>());
                                }

                                // 问题
                                Observable<List<SimpleAnswerBean>> anserObservable = mQAClient.getSimpleAnswerList(answers.toString());
                                if (TextUtils.isEmpty(answers.toString())) {
                                    anserObservable = Observable.just(new ArrayList<>());
                                }

                                // 帖子
                                Observable<List<SimplePostBean>> postObservable = mCircleClient.getPostListByIds(posts.toString());
                                if (TextUtils.isEmpty(posts.toString())) {
                                    postObservable = Observable.just(new ArrayList<>());
                                }


                                return Observable.zip(feedObservable, infoObservable, questionObservable, postObservable, anserObservable,
                                        (dynamicDetailBeanV2s, infoListDataBeans, qaListInfoBeans, simplePostBeans, simpleAnswerBeans) -> {

                                            LongSparseArray<DynamicDetailBeanV2> feedsArray = new LongSparseArray<>();
                                            LongSparseArray<InfoListDataBean> infosArray = new LongSparseArray<>();
                                            LongSparseArray<QAListInfoBean> questionsArray = new LongSparseArray<>();
                                            LongSparseArray<SimplePostBean> postsArray = new LongSparseArray<>();
                                            LongSparseArray<SimpleAnswerBean> answersArray = new LongSparseArray<>();

                                            for (DynamicDetailBeanV2 detailBeanV2 : dynamicDetailBeanV2s) {
                                                // 在这里设置为已付费，避免生成付费假数据
                                                detailBeanV2.setPaid_node(null);
                                                detailBeanV2.handleData();
                                                feedsArray.append(detailBeanV2.getId(), detailBeanV2);
                                            }
                                            for (InfoListDataBean listDataBean : infoListDataBeans) {
                                                infosArray.append(listDataBean.getId(), listDataBean);
                                            }
                                            for (QAListInfoBean qaListInfoBean : qaListInfoBeans) {
                                                questionsArray.append(qaListInfoBean.getId(), qaListInfoBean);
                                            }
                                            for (SimplePostBean postBean : simplePostBeans) {
                                                postsArray.append(postBean.getId(), postBean);
                                            }

                                            for (SimpleAnswerBean answerBean : simpleAnswerBeans) {
                                                answersArray.append(answerBean.getId(), answerBean);
                                            }
                                            List<AtMeaasgeBean> deletedData = new ArrayList<>();

                                            for (AtMeaasgeBean meaasgeBean : atMeaasgeBeans) {
                                                meaasgeBean.setMaxId(maxId[0]);
                                                Long key = meaasgeBean.getResourceable().getId();
                                                DynamicDetailBeanV2 detailBeanV2 = feedsArray.get(key);
                                                InfoListDataBean infoListDataBean = infosArray.get(key);
                                                QAListInfoBean qaListInfoBean = questionsArray.get(key);
                                                SimplePostBean simplePostBean = postsArray.get(key);
                                                SimpleAnswerBean simpleAnswerBean = answersArray.get(key);
                                                if (meaasgeBean.getResourceable() != null) {
                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (detailBeanV2 == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.rank_dynamic)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.rank_dynamic)));
                                                            continue;
                                                        }
                                                        meaasgeBean.setHasVideo(detailBeanV2.getVideo() != null);
                                                        String atType = meaasgeBean.getAt_type();
                                                        if (!MessageAtFragment.COMMENTS.equals(atType)) {
                                                            meaasgeBean.setUser_id(detailBeanV2.getUser_id());
                                                        }
                                                        meaasgeBean.setContent(detailBeanV2.getFeed_content());
                                                        meaasgeBean.setBody_content(detailBeanV2.getFriendlyContent());

                                                        boolean hasImage, hasVideo;
                                                        hasImage = detailBeanV2.getImages() != null && !detailBeanV2.getImages().isEmpty();
                                                        hasVideo = detailBeanV2.getVideo() != null;

                                                        int image = hasImage
                                                                ? detailBeanV2.getImages().get(0).getFile() : 0;
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                        if (hasVideo) {
                                                            meaasgeBean.setBody_image(String.format(ApiConfig.APP_DOMAIN + ApiConfig.FILE_PATH,
                                                                    detailBeanV2.getVideo().getCover_id()));
                                                        }
                                                    }
                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (infoListDataBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.collect_info)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.collect_info)));
                                                            continue;
                                                        }
                                                        String content = TextUtils.isEmpty(infoListDataBean.getSubject()) ? infoListDataBean.getContent()
                                                                : infoListDataBean.getSubject();
                                                        content = RegexUtils.replaceAllLines(content);
                                                        content = RegexUtils.getReplaceAll(content, MarkdownConfig.HTML_FORMAT, "");
                                                        content = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, content);
                                                        meaasgeBean.setContent(infoListDataBean.getTitle());
                                                        meaasgeBean.setExtra_content(content);
                                                        meaasgeBean.setBody_content(infoListDataBean.getTitle());
                                                        int image = infoListDataBean.getImage() != null
                                                                ? infoListDataBean.getImage().getId() : 0;
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                    }

                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (simplePostBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.post)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.post)));
                                                            continue;
                                                        }

                                                        meaasgeBean.setContent(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, TextUtils.isEmpty(simplePostBean.getSummary()) ?
                                                                simplePostBean.getTitle() : simplePostBean.getSummary()));
                                                        meaasgeBean.setBody_content(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, TextUtils.isEmpty(simplePostBean.getSummary()) ?
                                                                simplePostBean.getTitle() : simplePostBean.getSummary()));
                                                        int image = simplePostBean.getImage();
                                                        meaasgeBean.setParent_id(simplePostBean.getGroup_id());
                                                        meaasgeBean.setExtra_content(simplePostBean.getTitle());
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                    }

                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (simpleAnswerBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.draft_type_answers)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.draft_type_answers)));
                                                            continue;
                                                        }

                                                        meaasgeBean.setContent(simpleAnswerBean.getBody());
                                                        meaasgeBean.setBody_content(simpleAnswerBean.getBody());
                                                        int image = RegexUtils.getImageId(simpleAnswerBean.getBody());
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                    }

                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (qaListInfoBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.question)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.question)));
                                                            continue;
                                                        }

                                                        meaasgeBean.setExtra_content(qaListInfoBean.getBody());
                                                        meaasgeBean.setContent(qaListInfoBean.getSubject());
                                                        meaasgeBean.setBody_content(qaListInfoBean.getSubject());
                                                        int image = RegexUtils.getImageId(qaListInfoBean.getBody());
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                    }
                                                }
                                                if (TextUtils.isEmpty(meaasgeBean.getAt_type())) {
                                                    deletedData.add(meaasgeBean);
                                                }
                                            }
                                            atMeaasgeBeans.removeAll(deletedData);
                                            return atMeaasgeBeans;
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(Schedulers.io())
                                        .flatMap((Func1<List<AtMeaasgeBean>, Observable<List<AtMeaasgeBean>>>) atMeaasgeBeans1 -> {
                                            List<Object> userids = new ArrayList<>();

                                            for (AtMeaasgeBean atMeaasgeBean : atMeaasgeBeans1) {
                                                userids.add(atMeaasgeBean.getUser_id());
                                            }
                                            return mUserInfoRepository.getUserInfo(userids)
                                                    .flatMap((Func1<List<UserInfoBean>, Observable<List<AtMeaasgeBean>>>) userInfoBeans -> {
                                                        SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                                        for (UserInfoBean userInfoBean : userInfoBeans) {
                                                            userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(),
                                                                    userInfoBean);
                                                        }
                                                        for (AtMeaasgeBean atMeaasgeBean : atMeaasgeBeans1) {
                                                            atMeaasgeBean.setUserInfoBean(userInfoBeanSparseArray.get(atMeaasgeBean
                                                                    .getUser_id().intValue()));
                                                        }
                                                        return Observable.just(atMeaasgeBeans1);
                                                    });
                                        });
                            });
                });
    }

    @Override
    public Observable<List<AtMeaasgeBean>> getAtMessages(String type, Integer page) {
        return mUserInfoClient.getNotificationList(type, page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Func1<UserNotifyMsgBean, Observable<List<AtMeaasgeBean>>>) userNotifyMsgBean -> {
                    List<AtMeaasgeBean> result = new ArrayList<>();
                    if (userNotifyMsgBean.getData() != null) {
                        for (UserNotifyMsgBean.DataBeanX dataBeanX : userNotifyMsgBean.getData()) {
                            if (dataBeanX.getData() == null || dataBeanX.getData().getResource() == null) {
                                continue;
                            }
                            AtMeaasgeBean atMeaasgeBean = new AtMeaasgeBean();
                            atMeaasgeBean.setUser_id(dataBeanX.getData().getSender().getId());
                            atMeaasgeBean.setCreated_at(dataBeanX.getCreated_at());
                            atMeaasgeBean.setResourceable(dataBeanX.getData().getResource());
                            atMeaasgeBean.setId((long) dataBeanX.getId().hashCode());
                            result.add(atMeaasgeBean);
                        }
                    }
                    return Observable.just(result);
                })
                .flatMap((Func1<List<AtMeaasgeBean>, Observable<List<AtMeaasgeBean>>>) atMeaasgeBeans -> {
                    StringBuilder feeds = new StringBuilder();
                    StringBuilder infos = new StringBuilder();
                    StringBuilder posts = new StringBuilder();
                    StringBuilder questions = new StringBuilder();
                    StringBuilder answers = new StringBuilder();
                    StringBuilder comments = new StringBuilder();

                    List<Long> music = new ArrayList<>();
                    List<Long> music_specials = new ArrayList<>();

                    SparseIntArray feedSparseArray = new SparseIntArray();
                    SparseIntArray commentSparseArray = new SparseIntArray();

                    // 分离动态、评论(评论有多种类)
                    for (AtMeaasgeBean atMeaasgeBean : atMeaasgeBeans) {
                        if (atMeaasgeBean.getResourceable() != null) {
                            int index1 = atMeaasgeBeans.indexOf(atMeaasgeBean);
                            if (MessageAtFragment.FEED.equals(atMeaasgeBean.getResourceable().getType())) {
                                // feed
                                feedSparseArray.append(atMeaasgeBean.getResourceable().getId().intValue(), index1);
                                feeds.append(atMeaasgeBean.getResourceable().getId());
                                feeds.append(",");
                                atMeaasgeBean.setAt_type(MessageAtFragment.FEED);
                            } else if (MessageAtFragment.COMMENTS.equals(atMeaasgeBean.getResourceable().getType())) {
                                // comment
                                commentSparseArray.append(atMeaasgeBean.getResourceable().getId().intValue(), index1);
                                comments.append(atMeaasgeBean.getResourceable().getId());
                                atMeaasgeBean.setAt_type(MessageAtFragment.COMMENTS);
                                comments.append(",");
                            }
                        }
                    }

                    Observable<List<AtMeaasgeBean>> commentObservable;
                    if (TextUtils.isEmpty(comments.toString())) {
                        commentObservable = Observable.just(new ArrayList<>());
                    } else {
                        commentObservable = mUserInfoRepository.getAllComments(null, 0, null, null,
                                null, null, null, null,
                                comments.toString());
                    }
                    Long[] maxId = new Long[]{1L};
                    if (!atMeaasgeBeans.isEmpty()) {
                        AtMeaasgeBean lastMsg = atMeaasgeBeans.get(atMeaasgeBeans.size() - 1);
                        maxId[0] = lastMsg.getId();
                    }

                    // 获取评论
                    return commentObservable
                            .flatMap((Func1<List<AtMeaasgeBean>, Observable<List<AtMeaasgeBean>>>) flat -> {
                                for (AtMeaasgeBean comment : flat) {
                                    int index1 = commentSparseArray.get(comment.getId().intValue());
                                    comment.setAt_type(atMeaasgeBeans.get(index1).getResourceable().getType());
                                    atMeaasgeBeans.set(index1, comment);

                                    // 解析评论内容
                                    if (comment.getResourceable() != null) {
                                        switch (comment.getResourceable().getType()) {
                                            case MessageAtFragment.FEED:
                                                // feed
                                                feeds.append(comment.getResourceable().getId());
                                                feeds.append(",");
                                                break;
                                            case MessageAtFragment.INFO:
                                                // info
                                                infos.append(comment.getResourceable().getId());
                                                infos.append(",");
                                                break;
                                            case MessageAtFragment.POST:
                                                // post
                                                posts.append(comment.getResourceable().getId());
                                                posts.append(",");
                                                break;
                                            case MessageAtFragment.QUESTION:
                                                // question
                                                questions.append(comment.getResourceable().getId());
                                                questions.append(",");
                                                break;
                                            case MessageAtFragment.ANSWER:
                                                // answer
                                                answers.append(comment.getResourceable().getId());
                                                answers.append(",");
                                                break;
                                            case ApiConfig.APP_LIKE_MUSIC:
                                                // musics
                                                music.add(comment.getResourceable().getId());
                                                break;
                                            case ApiConfig.APP_LIKE_MUSIC_SPECIALS:
                                                // music_specials
                                                music_specials.add(comment.getResourceable().getId());
                                                break;
                                            default:
                                        }
                                    }
                                }

                                // 获取上级资源
                                // 动态
                                Observable<List<DynamicDetailBeanV2>> feedObservable = getDynamicListV2(null, null, null, null,
                                        false, null, feeds.toString());
                                if (TextUtils.isEmpty(feeds.toString())) {
                                    feedObservable = Observable.just(new ArrayList<>());
                                }

                                // 资讯
                                Observable<List<InfoListDataBean>> infoObservable = mInfoMainClient.getInfoList(null
                                        , null, null, null, null, infos.toString());
                                if (TextUtils.isEmpty(infos.toString())) {
                                    infoObservable = Observable.just(new ArrayList<>());
                                }

                                // 问题
                                Observable<List<QAListInfoBean>> questionObservable = mQAClient.getQAQustion(null
                                        , null, null, null, questions.toString());
                                if (TextUtils.isEmpty(questions.toString())) {
                                    questionObservable = Observable.just(new ArrayList<>());
                                }

                                // 问题
                                Observable<List<SimpleAnswerBean>> anserObservable = mQAClient.getSimpleAnswerList(answers.toString());
                                if (TextUtils.isEmpty(answers.toString())) {
                                    anserObservable = Observable.just(new ArrayList<>());
                                }

                                // 帖子
                                Observable<List<SimplePostBean>> postObservable = mCircleClient.getPostListByIds(posts.toString());
                                if (TextUtils.isEmpty(posts.toString())) {
                                    postObservable = Observable.just(new ArrayList<>());
                                }

                                // 音乐单曲
                                Observable<List<SimpleMusic>> musicObservable = mMusicClient.getSimpleMusicList(null, music, null);
                                if (music.isEmpty()) {
                                    musicObservable = Observable.just(new ArrayList<>());
                                }

                                // 音乐专辑
                                Observable<List<MusicAlbumListBean>> albumObservable = mMusicClient.getMusicList(null, null, music_specials);
                                if (music_specials.isEmpty()) {
                                    albumObservable = Observable.just(new ArrayList<>());
                                }

                                return Observable.zip(feedObservable, infoObservable, questionObservable, postObservable, anserObservable, musicObservable, albumObservable,
                                        (dynamicDetailBeanV2s, infoListDataBeans, qaListInfoBeans, simplePostBeans, simpleAnswerBeans, musics, albumBeans) -> {

                                            LongSparseArray<DynamicDetailBeanV2> feedsArray = new LongSparseArray<>();
                                            LongSparseArray<InfoListDataBean> infosArray = new LongSparseArray<>();
                                            LongSparseArray<QAListInfoBean> questionsArray = new LongSparseArray<>();
                                            LongSparseArray<SimplePostBean> postsArray = new LongSparseArray<>();
                                            LongSparseArray<SimpleAnswerBean> answersArray = new LongSparseArray<>();
                                            LongSparseArray<MusicAlbumListBean> musicAlbumArray = new LongSparseArray<>();
                                            LongSparseArray<SimpleMusic> simpleMusicArray = new LongSparseArray<>();

                                            for (DynamicDetailBeanV2 detailBeanV2 : dynamicDetailBeanV2s) {
                                                // 在这里设置为已付费，避免生成付费假数据
                                                detailBeanV2.setPaid_node(null);
                                                detailBeanV2.handleData();
                                                feedsArray.append(detailBeanV2.getId(), detailBeanV2);
                                            }
                                            for (InfoListDataBean listDataBean : infoListDataBeans) {
                                                infosArray.append(listDataBean.getId(), listDataBean);
                                            }
                                            for (QAListInfoBean qaListInfoBean : qaListInfoBeans) {
                                                questionsArray.append(qaListInfoBean.getId(), qaListInfoBean);
                                            }
                                            for (SimplePostBean postBean : simplePostBeans) {
                                                postsArray.append(postBean.getId(), postBean);
                                            }

                                            for (SimpleAnswerBean answerBean : simpleAnswerBeans) {
                                                answersArray.append(answerBean.getId(), answerBean);
                                            }
                                            for (MusicAlbumListBean albumListBean : albumBeans) {
                                                musicAlbumArray.append(albumListBean.getId(), albumListBean);
                                            }

                                            for (SimpleMusic music1 : musics) {
                                                simpleMusicArray.append(music1.getId(), music1);
                                            }

                                            List<AtMeaasgeBean> deletedData = new ArrayList<>();

                                            for (AtMeaasgeBean meaasgeBean : atMeaasgeBeans) {
                                                meaasgeBean.setMaxId(maxId[0]);
                                                Long key = meaasgeBean.getResourceable().getId();
                                                DynamicDetailBeanV2 detailBeanV2 = feedsArray.get(key);
                                                InfoListDataBean infoListDataBean = infosArray.get(key);
                                                QAListInfoBean qaListInfoBean = questionsArray.get(key);
                                                SimplePostBean simplePostBean = postsArray.get(key);
                                                SimpleAnswerBean simpleAnswerBean = answersArray.get(key);
                                                MusicAlbumListBean albumListBean = musicAlbumArray.get(key);
                                                SimpleMusic simpleMusic = simpleMusicArray.get(key);
                                                if (meaasgeBean.getResourceable() != null) {
                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (detailBeanV2 == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.rank_dynamic)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.rank_dynamic)));
                                                            continue;
                                                        }
                                                        meaasgeBean.setHasVideo(detailBeanV2.getVideo() != null);
                                                        String atType = meaasgeBean.getAt_type();
                                                        if (!MessageAtFragment.COMMENTS.equals(atType)) {
                                                            meaasgeBean.setUser_id(detailBeanV2.getUser_id());
                                                        }
                                                        meaasgeBean.setContent(detailBeanV2.getFeed_content());
                                                        meaasgeBean.setBody_content(detailBeanV2.getFriendlyContent());

                                                        boolean hasImage, hasVideo;
                                                        hasImage = detailBeanV2.getImages() != null && !detailBeanV2.getImages().isEmpty();
                                                        hasVideo = detailBeanV2.getVideo() != null;

                                                        int image = hasImage
                                                                ? detailBeanV2.getImages().get(0).getFile() : 0;
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                        if (hasVideo) {
                                                            meaasgeBean.setBody_image(String.format(ApiConfig.APP_DOMAIN + ApiConfig.FILE_PATH,
                                                                    detailBeanV2.getVideo().getCover_id()));
                                                        }
                                                    }
                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (infoListDataBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.collect_info)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.collect_info)));
                                                            continue;
                                                        }
                                                        String content = TextUtils.isEmpty(infoListDataBean.getSubject()) ? infoListDataBean.getContent()
                                                                : infoListDataBean.getSubject();
                                                        content = RegexUtils.replaceAllLines(content);
                                                        content = RegexUtils.getReplaceAll(content, MarkdownConfig.HTML_FORMAT, "");
                                                        content = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, content);
                                                        meaasgeBean.setContent(infoListDataBean.getTitle());
                                                        meaasgeBean.setExtra_content(content);
                                                        meaasgeBean.setBody_content(infoListDataBean.getTitle());
                                                        int image = infoListDataBean.getImage() != null
                                                                ? infoListDataBean.getImage().getId() : 0;
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                    }

                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (simplePostBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.post)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.post)));
                                                            continue;
                                                        }

                                                        meaasgeBean.setContent(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, TextUtils.isEmpty(simplePostBean.getSummary()) ?
                                                                simplePostBean.getTitle() : simplePostBean.getSummary()));
                                                        meaasgeBean.setBody_content(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, TextUtils.isEmpty(simplePostBean.getSummary()) ?
                                                                simplePostBean.getTitle() : simplePostBean.getSummary()));
                                                        int image = simplePostBean.getImage();
                                                        meaasgeBean.setParent_id(simplePostBean.getGroup_id());
                                                        meaasgeBean.setExtra_content(simplePostBean.getTitle());
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                    }

                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (simpleAnswerBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.draft_type_answers)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.draft_type_answers)));
                                                            continue;
                                                        }

                                                        meaasgeBean.setContent(simpleAnswerBean.getBody());
                                                        meaasgeBean.setBody_content(simpleAnswerBean.getBody());
                                                        int image = RegexUtils.getImageId(simpleAnswerBean.getBody());
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                    }

                                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION.equals(meaasgeBean.getResourceable().getType())) {

                                                        if (qaListInfoBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.question)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.question)));
                                                            continue;
                                                        }

                                                        meaasgeBean.setExtra_content(qaListInfoBean.getBody());
                                                        meaasgeBean.setContent(qaListInfoBean.getSubject());
                                                        meaasgeBean.setBody_content(qaListInfoBean.getSubject());
                                                        int image = RegexUtils.getImageId(qaListInfoBean.getBody());
                                                        meaasgeBean.setBody_image(image > 0 ? ImageUtils.imagePathConvertV2(image, 120,
                                                                120, 100) : "");
                                                    }

                                                    if (ApiConfig.APP_LIKE_MUSIC_SPECIALS.equals(meaasgeBean.getResourceable().getType())) {
                                                        if (albumListBean == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.music_album)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.music_album)));
                                                            continue;
                                                        }
                                                        meaasgeBean.setContent(albumListBean.getTitle());
                                                        meaasgeBean.setBody_content(albumListBean.getTitle());
                                                    }
                                                    if (ApiConfig.APP_LIKE_MUSIC.equals(meaasgeBean.getResourceable().getType())) {
                                                        if (simpleMusic == null) {
                                                            deletedData.add(meaasgeBean);
                                                            meaasgeBean.getResourceable().setDeleted(true);
                                                            meaasgeBean.setContent(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.single_music)));
                                                            meaasgeBean.setBody_content(mContext.getString(R.string.resource_deleted_format, mContext.getString(R.string.single_music)));
                                                            continue;
                                                        }
                                                        meaasgeBean.setContent(simpleMusic.getTitle());
                                                        meaasgeBean.setBody_content(simpleMusic.getTitle());
                                                    }
                                                }
//                                                if (TextUtils.isEmpty(meaasgeBean.getAt_type())) {
//                                                    deletedData.add(meaasgeBean);
//                                                }
                                            }
                                            atMeaasgeBeans.removeAll(deletedData);
                                            return atMeaasgeBeans;
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(Schedulers.io())
                                        .flatMap((Func1<List<AtMeaasgeBean>, Observable<List<AtMeaasgeBean>>>) atMeaasgeBeans1 -> {
                                            List<Object> userids = new ArrayList<>();

                                            for (AtMeaasgeBean atMeaasgeBean : atMeaasgeBeans1) {
                                                userids.add(atMeaasgeBean.getUser_id());
                                            }
                                            return mUserInfoRepository.getUserInfo(userids)
                                                    .flatMap((Func1<List<UserInfoBean>, Observable<List<AtMeaasgeBean>>>) userInfoBeans -> {
                                                        SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                                        for (UserInfoBean userInfoBean : userInfoBeans) {
                                                            userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(),
                                                                    userInfoBean);
                                                        }
                                                        for (AtMeaasgeBean atMeaasgeBean : atMeaasgeBeans1) {
                                                            atMeaasgeBean.setUserInfoBean(userInfoBeanSparseArray.get(atMeaasgeBean
                                                                    .getUser_id().intValue()));
                                                        }
                                                        return Observable.just(atMeaasgeBeans1);
                                                    });
                                        });
                            });
                });
    }

    @Override
    public Observable<List<CommentedBean>> getCommentMessages(String type, Integer page) {
        return mUserInfoClient.getNotificationList(type, page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Func1<UserNotifyMsgBean, Observable<List<CommentedBean>>>) userNotifyMsgBean -> {
                    List<CommentedBean> result = new ArrayList<>();

                    StringBuilder feeds = new StringBuilder();
                    StringBuilder infos = new StringBuilder();
                    StringBuilder posts = new StringBuilder();
                    StringBuilder questions = new StringBuilder();
                    StringBuilder answers = new StringBuilder();
                    List<Long> music = new ArrayList<>();
                    List<Long> music_specials = new ArrayList<>();

                    if (userNotifyMsgBean.getData() != null) {
                        for (UserNotifyMsgBean.DataBeanX dataBeanX : userNotifyMsgBean.getData()) {
                            if (dataBeanX.getData() == null || dataBeanX.getData().getResource() == null) {
                                continue;
                            }
                            CommentedBean commentedBean = new CommentedBean();
                            commentedBean.setChannel(dataBeanX.getData().getResource().getType());
                            commentedBean.setTarget_id(dataBeanX.getData().getResource().getId());
                            commentedBean.setReply_user(dataBeanX.getData().hasReply() ? AppApplication.getMyUserIdWithdefault() : 0);
                            commentedBean.setComment_content(dataBeanX.getData().getContent());
                            commentedBean.setCreated_at(dataBeanX.getCreated_at());
                            commentedBean.setUpdated_at(dataBeanX.getCreated_at());

                            commentedBean.setUser_id(dataBeanX.getData().getSender().getId());

                            result.add(commentedBean);

                            switch (dataBeanX.getData().getResource().getType()) {
                                case MessageAtFragment.FEED:
                                    // feed
                                    feeds.append(dataBeanX.getData().getResource().getId());
                                    feeds.append(",");
                                    break;
                                case MessageAtFragment.INFO:
                                    // info
                                    infos.append(dataBeanX.getData().getResource().getId());
                                    infos.append(",");
                                    break;
                                case MessageAtFragment.POST:
                                    // post
                                    posts.append(dataBeanX.getData().getResource().getId());
                                    posts.append(",");
                                    break;
                                case MessageAtFragment.QUESTION:
                                    // question
                                    questions.append(dataBeanX.getData().getResource().getId());
                                    questions.append(",");
                                    break;
                                case MessageAtFragment.ANSWER:
                                    // answer
                                    answers.append(dataBeanX.getData().getResource().getId());
                                    answers.append(",");
                                    break;
                                case ApiConfig.APP_LIKE_MUSIC:
                                    // musics
                                    music.add(dataBeanX.getData().getResource().getId());
                                    break;
                                case ApiConfig.APP_LIKE_MUSIC_SPECIALS:
                                    // music_specials
                                    music_specials.add(dataBeanX.getData().getResource().getId());
                                    break;
                                default:
                            }
                        }
                    }


                    // 获取上级资源
                    // 动态
                    Observable<List<DynamicDetailBeanV2>> feedObservable = getDynamicListV2(null, null, null, null,
                            false, null, feeds.toString());
                    if (TextUtils.isEmpty(feeds.toString())) {
                        feedObservable = Observable.just(new ArrayList<>());
                    }

                    // 资讯
                    Observable<List<InfoListDataBean>> infoObservable = mInfoMainClient.getInfoList(null
                            , null, null, null, null, infos.toString());
                    if (TextUtils.isEmpty(infos.toString())) {
                        infoObservable = Observable.just(new ArrayList<>());
                    }

                    // 问题
                    Observable<List<QAListInfoBean>> questionObservable = mQAClient.getQAQustion(null
                            , null, null, null, questions.toString());
                    if (TextUtils.isEmpty(questions.toString())) {
                        questionObservable = Observable.just(new ArrayList<>());
                    }

                    // 问题
                    Observable<List<SimpleAnswerBean>> anserObservable = mQAClient.getSimpleAnswerList(answers.toString());
                    if (TextUtils.isEmpty(answers.toString())) {
                        anserObservable = Observable.just(new ArrayList<>());
                    }

                    // 帖子
                    Observable<List<SimplePostBean>> postObservable = mCircleClient.getPostListByIds(posts.toString());
                    if (TextUtils.isEmpty(posts.toString())) {
                        postObservable = Observable.just(new ArrayList<>());
                    }

                    // 音乐单曲
                    Observable<List<SimpleMusic>> musicObservable = mMusicClient.getSimpleMusicList(null, music, null);
                    if (music.isEmpty()) {
                        musicObservable = Observable.just(new ArrayList<>());
                    }

                    // 音乐专辑
                    Observable<List<MusicAlbumListBean>> albumObservable = mMusicClient.getMusicList(null, null, music_specials);
                    if (music_specials.isEmpty()) {
                        albumObservable = Observable.just(new ArrayList<>());
                    }

                    return Observable.zip(feedObservable, infoObservable, questionObservable, postObservable, anserObservable, albumObservable, musicObservable,
                            (dynamicDetailBeanV2s, infoListDataBeans, qaListInfoBeans, simplePostBeans, simpleAnswerBeans, albumBeans, musics) -> {

                                LongSparseArray<DynamicDetailBeanV2> feedsArray = new LongSparseArray<>();
                                LongSparseArray<InfoListDataBean> infosArray = new LongSparseArray<>();
                                LongSparseArray<QAListInfoBean> questionsArray = new LongSparseArray<>();
                                LongSparseArray<SimplePostBean> postsArray = new LongSparseArray<>();
                                LongSparseArray<SimpleAnswerBean> answersArray = new LongSparseArray<>();
                                LongSparseArray<MusicAlbumListBean> musicAlbumArray = new LongSparseArray<>();
                                LongSparseArray<SimpleMusic> simpleMusicArray = new LongSparseArray<>();

                                for (DynamicDetailBeanV2 detailBeanV2 : dynamicDetailBeanV2s) {
                                    // 在这里设置为已付费，避免生成付费假数据
                                    detailBeanV2.setPaid_node(null);
                                    detailBeanV2.handleData();
                                    feedsArray.append(detailBeanV2.getId(), detailBeanV2);
                                }
                                for (InfoListDataBean listDataBean : infoListDataBeans) {
                                    infosArray.append(listDataBean.getId(), listDataBean);
                                }
                                for (QAListInfoBean qaListInfoBean : qaListInfoBeans) {
                                    questionsArray.append(qaListInfoBean.getId(), qaListInfoBean);
                                }
                                for (SimplePostBean postBean : simplePostBeans) {
                                    postsArray.append(postBean.getId(), postBean);
                                }

                                for (SimpleAnswerBean answerBean : simpleAnswerBeans) {
                                    answersArray.append(answerBean.getId(), answerBean);
                                }

                                for (MusicAlbumListBean albumListBean : albumBeans) {
                                    musicAlbumArray.append(albumListBean.getId(), albumListBean);
                                }

                                for (SimpleMusic music1 : musics) {
                                    simpleMusicArray.append(music1.getId(), music1);
                                }

                                for (CommentedBean commentedBean : result) {
                                    Long key = commentedBean.getTarget_id();
                                    DynamicDetailBeanV2 detailBeanV2 = feedsArray.get(key);
                                    InfoListDataBean infoListDataBean = infosArray.get(key);
                                    QAListInfoBean qaListInfoBean = questionsArray.get(key);
                                    SimplePostBean simplePostBean = postsArray.get(key);
                                    SimpleAnswerBean simpleAnswerBean = answersArray.get(key);
                                    MusicAlbumListBean albumListBean = musicAlbumArray.get(key);
                                    SimpleMusic simpleMusic = simpleMusicArray.get(key);

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(commentedBean.getChannel())) {
                                        commentedBean.setCommentable(detailBeanV2);
                                    }
                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO.equals(commentedBean.getChannel())) {
                                        commentedBean.setCommentable(infoListDataBean);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST.equals(commentedBean.getChannel())) {
                                        commentedBean.setCommentable(simplePostBean);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS.equals(commentedBean.getChannel())) {
                                        commentedBean.setCommentable(simpleAnswerBean);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION.equals(commentedBean.getChannel())) {
                                        commentedBean.setCommentable(qaListInfoBean);
                                    }

                                    if (ApiConfig.APP_LIKE_MUSIC_SPECIALS.equals(commentedBean.getChannel())) {
                                        commentedBean.setCommentable(albumListBean);
                                    }
                                    if (ApiConfig.APP_LIKE_MUSIC.equals(commentedBean.getChannel())) {
                                        commentedBean.setCommentable(simpleMusic);
                                    }
                                    commentedBean.initDelet();
                                }
                                return result;
                            })
                            .flatMap((Func1<List<CommentedBean>, Observable<List<CommentedBean>>>) commentedBeanList -> {
                                List<Object> userids = new ArrayList<>();

                                for (CommentedBean commentedBean : commentedBeanList) {
                                    userids.add(commentedBean.getUser_id());
//                                    userids.add(commentedBean.getTarget_user());
                                    userids.add(commentedBean.getReply_user());
                                }
                                return mUserInfoRepository.getUserInfo(userids)
                                        .map(userinfobeans -> {
                                            if (!userinfobeans.isEmpty()) {
                                                // 获取用户信息，并设置动态所有者的用户信息，已以评论和被评论者的用户信息
                                                SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                                for (UserInfoBean userInfoBean : userinfobeans) {
                                                    userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                                }
                                                for (CommentedBean commentedBean : commentedBeanList) {
                                                    commentedBean.setCommentUserInfo(userInfoBeanSparseArray.get(commentedBean.getUser_id().intValue()));
                                                    if (commentedBean.getReply_user() == null || commentedBean.getReply_user() == 0) {
                                                        // 用于占位
                                                        UserInfoBean userinfo = new UserInfoBean();
                                                        userinfo.setUser_id(0L);
                                                        commentedBean.setReplyUserInfo(userinfo);
                                                    } else {
                                                        commentedBean.setReplyUserInfo(userInfoBeanSparseArray.get(commentedBean.getReply_user()
                                                                .intValue()));
                                                    }
                                                }
                                            }
                                            return commentedBeanList;
                                        });
                            });
                });
    }

    @Override
    public Observable<List<DigedBean>> getLikeMessages(String type, Integer page) {
        return mUserInfoClient.getNotificationList(type, page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Func1<UserNotifyMsgBean, Observable<List<DigedBean>>>) userNotifyMsgBean -> {
                    List<DigedBean> result = new ArrayList<>();

                    StringBuilder feeds = new StringBuilder();
                    StringBuilder infos = new StringBuilder();
                    StringBuilder posts = new StringBuilder();
                    StringBuilder questions = new StringBuilder();
                    StringBuilder answers = new StringBuilder();

                    if (userNotifyMsgBean.getData() != null) {
                        for (UserNotifyMsgBean.DataBeanX dataBeanX : userNotifyMsgBean.getData()) {
                            if (dataBeanX.getData() == null || dataBeanX.getData().getResource() == null) {
                                continue;
                            }
                            DigedBean digedBean = new DigedBean();
                            digedBean.setLikeable_type(dataBeanX.getData().getResource().getType());
                            digedBean.setCreated_at(dataBeanX.getCreated_at());
                            digedBean.setUpdated_at(dataBeanX.getCreated_at());
                            digedBean.setLikeable_id(dataBeanX.getData().getResource().getId());
                            digedBean.setUser_id(dataBeanX.getData().getSender().getId());

                            result.add(digedBean);

                            switch (dataBeanX.getData().getResource().getType()) {
                                case MessageAtFragment.FEED:
                                    // feed
                                    feeds.append(dataBeanX.getData().getResource().getId());
                                    feeds.append(",");
                                    break;
                                case MessageAtFragment.INFO:
                                    // info
                                    infos.append(dataBeanX.getData().getResource().getId());
                                    infos.append(",");
                                    break;
                                case MessageAtFragment.POST:
                                    // post
                                    posts.append(dataBeanX.getData().getResource().getId());
                                    posts.append(",");
                                    break;
                                case MessageAtFragment.QUESTION:
                                    // question
                                    questions.append(dataBeanX.getData().getResource().getId());
                                    questions.append(",");
                                    break;
                                case MessageAtFragment.ANSWER:
                                    // answer
                                    answers.append(dataBeanX.getData().getResource().getId());
                                    answers.append(",");
                                    break;
                                default:
                            }
                        }
                    }

                    // 获取上级资源
                    // 动态
                    Observable<List<DynamicDetailBeanV2>> feedObservable = getDynamicListV2(null, null, null, null,
                            false, null, feeds.toString());
                    if (TextUtils.isEmpty(feeds.toString())) {
                        feedObservable = Observable.just(new ArrayList<>());
                    }

                    // 资讯
                    Observable<List<InfoListDataBean>> infoObservable = mInfoMainClient.getInfoList(null
                            , null, null, null, null, infos.toString());
                    if (TextUtils.isEmpty(infos.toString())) {
                        infoObservable = Observable.just(new ArrayList<>());
                    }

                    // 问题
                    Observable<List<QAListInfoBean>> questionObservable = mQAClient.getQAQustion(null
                            , null, null, null, questions.toString());
                    if (TextUtils.isEmpty(questions.toString())) {
                        questionObservable = Observable.just(new ArrayList<>());
                    }

                    // 问题
                    Observable<List<SimpleAnswerBean>> anserObservable = mQAClient.getSimpleAnswerList(answers.toString());
                    if (TextUtils.isEmpty(answers.toString())) {
                        anserObservable = Observable.just(new ArrayList<>());
                    }

                    // 帖子
                    Observable<List<SimplePostBean>> postObservable = mCircleClient.getPostListByIds(posts.toString());
                    if (TextUtils.isEmpty(posts.toString())) {
                        postObservable = Observable.just(new ArrayList<>());
                    }

                    return Observable.zip(feedObservable, infoObservable, questionObservable, postObservable, anserObservable,
                            (dynamicDetailBeanV2s, infoListDataBeans, qaListInfoBeans, simplePostBeans, simpleAnswerBeans) -> {

                                LongSparseArray<DynamicDetailBeanV2> feedsArray = new LongSparseArray<>();
                                LongSparseArray<InfoListDataBean> infosArray = new LongSparseArray<>();
                                LongSparseArray<QAListInfoBean> questionsArray = new LongSparseArray<>();
                                LongSparseArray<SimplePostBean> postsArray = new LongSparseArray<>();
                                LongSparseArray<SimpleAnswerBean> answersArray = new LongSparseArray<>();

                                for (DynamicDetailBeanV2 detailBeanV2 : dynamicDetailBeanV2s) {
                                    // 在这里设置为已付费，避免生成付费假数据
                                    detailBeanV2.setPaid_node(null);
                                    detailBeanV2.handleData();
                                    feedsArray.append(detailBeanV2.getId(), detailBeanV2);
                                }
                                for (InfoListDataBean listDataBean : infoListDataBeans) {
                                    infosArray.append(listDataBean.getId(), listDataBean);
                                }
                                for (QAListInfoBean qaListInfoBean : qaListInfoBeans) {
                                    questionsArray.append(qaListInfoBean.getId(), qaListInfoBean);
                                }
                                for (SimplePostBean postBean : simplePostBeans) {
                                    postsArray.append(postBean.getId(), postBean);
                                }

                                for (SimpleAnswerBean answerBean : simpleAnswerBeans) {
                                    answersArray.append(answerBean.getId(), answerBean);
                                }

                                for (DigedBean digedBean : result) {
                                    Long key = digedBean.getLikeable_id();
                                    if (key == null) {
                                        continue;
                                    }
                                    DynamicDetailBeanV2 detailBeanV2 = feedsArray.get(key);
                                    InfoListDataBean infoListDataBean = infosArray.get(key);
                                    QAListInfoBean qaListInfoBean = questionsArray.get(key);
                                    SimplePostBean simplePostBean = postsArray.get(key);
                                    SimpleAnswerBean simpleAnswerBean = answersArray.get(key);

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(digedBean.getLikeable_type())) {
                                        digedBean.setLikeable(detailBeanV2);
                                    }
                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO.equals(digedBean.getLikeable_type())) {
                                        digedBean.setLikeable(infoListDataBean);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST.equals(digedBean.getLikeable_type())) {
                                        digedBean.setLikeable(simplePostBean);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS.equals(digedBean.getLikeable_type())) {
                                        digedBean.setLikeable(simpleAnswerBean);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION.equals(digedBean.getLikeable_type())) {
                                        digedBean.setLikeable(qaListInfoBean);
                                    }
                                    digedBean.initDelet();
                                }
                                return result;
                            })
                            .flatMap((Func1<List<DigedBean>, Observable<List<DigedBean>>>) digedBeans -> {
                                List<Object> userids = new ArrayList<>();

                                for (DigedBean digedBean : digedBeans) {
                                    userids.add(digedBean.getUser_id());
                                }
                                return mUserInfoRepository.getUserInfo(userids)
                                        .map(userinfobeans -> {
                                            if (!userinfobeans.isEmpty()) {
                                                // 获取用户信息，并设置动态所有者的用户信息，已以评论和被评论者的用户信息
                                                SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                                for (UserInfoBean userInfoBean : userinfobeans) {
                                                    userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                                }
                                                for (DigedBean digedBean : digedBeans) {
                                                    digedBean.setDigUserInfo(userInfoBeanSparseArray.get(digedBean.getUser_id().intValue()));
                                                }
                                            }
                                            return digedBeans;
                                        });
                            });
                });
    }

    /**
     * 处理喜欢操作
     *
     * @param feed_id
     * @return
     */
    @Override
    public void handleLike(boolean isLiked, final Long feed_id) {

        Observable.just(isLiked)
                .observeOn(Schedulers.io())
                .subscribe(aBoolean -> {
                    BackgroundRequestTaskBean backgroundRequestTaskBean;
                    HashMap<String, Object> params = new HashMap<>();
                    // 后台处理
                    if (aBoolean) {
                        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig
                                .POST_V2, params);
                        backgroundRequestTaskBean.setPath(String.format(ApiConfig
                                .APP_PATH_DYNAMIC_CLICK_LIKE_FORMAT_V2, feed_id));
                    } else {
                        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig
                                .DELETE_V2, params);
                        backgroundRequestTaskBean.setPath(String.format(ApiConfig
                                .APP_PATH_DYNAMIC_CANCEL_CLICK_LIKE_FORMAT_V2, feed_id));
                    }

                    BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
                }, throwable -> throwable.printStackTrace());
    }


    /**
     * 删除动态
     *
     * @param feed_id
     */
    @Override
    public void deleteDynamic(Long feed_id) {
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        params.put("feed_id", feed_id);
        // 后台处理
        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.DELETE, params);
        backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_DELETE_DYNAMIC, feed_id));
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }

    @Override
    public void deleteCommentV2(Long feed_id, Long comment_id) {
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        params.put("feed_id", feed_id);
        params.put("comment_id", comment_id);
        // 后台处理
        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.DELETE, params);
        backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_DYNAMIC_DELETE_COMMENT_V2, feed_id,
                comment_id));
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }


    @Override
    public void sendCommentV2(String commentContent, Long feed_id, Long reply_to_user_id, Long comment_mark) {
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        params.put("body", commentContent);
        params.put("comment_mark", comment_mark);
        if (reply_to_user_id > 0) {
            params.put("reply_user", reply_to_user_id);
        }

        // 后台处理
        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig
                .SEND_DYNAMIC_COMMENT, params);
        backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_DYNAMIC_SEND_COMMENT_V2, feed_id));
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }

    @Override
    public Observable<DynamicCommentToll> setDynamicCommentToll(Long feed_id, int amout) {
        return mDynamicClient.setDynamicCommentToll(feed_id, amout)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public void updateOrInsertDynamicV2(List<DynamicDetailBeanV2> dynamicBeens, final String type) {
        Observable.just(dynamicBeens)
                .observeOn(Schedulers.io())
                .subscribe(datas -> {
                    // 清除旧数据
                    if (!TextUtils.isEmpty(type)) {
                        mDynamicDetailBeanV2GreenDao.deleteDynamicByType(type);
                    }
                    List<DynamicCommentBean> dynamicCommentBeen = new ArrayList<>();
                    List<DynamicDetailBeanV2> result = new ArrayList<>();
                    int size = datas.size();
                    for (int i = 0; i < size; i++) {
                        // 处理关注和热门数据
                        if (datas.get(i).getFeed_mark() != null && datas.get(i).getFeed_mark() != 0) {
                            dealLocalTypeDataV2(datas.get(i));
                            if (datas.get(i).getComments() != null) {
                                dynamicCommentBeen.addAll(datas.get(i).getComments());
                            }
                            result.add(datas.get(i));
                        }
                    }

                    mDynamicDetailBeanV2GreenDao.insertOrReplace(result);
                    mDynamicCommentBeanGreenDao.insertOrReplace(dynamicCommentBeen);
                }, throwable -> throwable.printStackTrace());
    }


    private void dealLocalTypeDataV2(DynamicDetailBeanV2 dynamicBeanTmp) {
        DynamicDetailBeanV2 localDynamicBean = mDynamicDetailBeanV2GreenDao.getDynamicByFeedMark(dynamicBeanTmp
                .getFeed_mark());
        if (localDynamicBean != null) {
            if ((dynamicBeanTmp.getHot_creat_time() == null || dynamicBeanTmp.getHot_creat_time() == 0) &&
                    localDynamicBean.getHot_creat_time() != null && localDynamicBean.getHot_creat_time() != 0) {
                dynamicBeanTmp.setHot_creat_time(localDynamicBean.getHot_creat_time());
            }
            if (localDynamicBean.getIsFollowed()) {
                dynamicBeanTmp.setIsFollowed(localDynamicBean.getIsFollowed());
            }
        }
    }

    @Override
    public Observable<List<DynamicDigListBean>> getDynamicDigListV2(Long feedId, Long
            maxId) {
        return mDynamicClient.getDynamicDigListV2(feedId, maxId, TSListFragment.DEFAULT_PAGE_SIZE)
                .observeOn(Schedulers.io())
                .flatMap(dynamicDigListBeanList -> {
                    // 获取点赞的用户id列表
                    if (!dynamicDigListBeanList.isEmpty()) {
                        List<Object> userids = new ArrayList<>();
                        for (int i = 0; i < dynamicDigListBeanList.size(); i++) {
                            DynamicDigListBean dynamicDigListBean = dynamicDigListBeanList.get(i);
                            userids.add(dynamicDigListBean.getUser_id());
                            userids.add(dynamicDigListBean.getTarget_user());
                        }
                        // 通过用户id列表请求用户信息和用户关注状态
                        return mUserInfoRepository.getUserInfo(userids)
                                .map(listBaseJson -> {
                                    SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                    for (UserInfoBean userInfoBean : listBaseJson) {
                                        userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(),
                                                userInfoBean);
                                    }
                                    for (DynamicDigListBean dynamicDigListBean : dynamicDigListBeanList) {
                                        dynamicDigListBean.setDiggUserInfo(userInfoBeanSparseArray.get
                                                (dynamicDigListBean.getUser_id().intValue()));
                                        dynamicDigListBean.setTargetUserInfo(userInfoBeanSparseArray.get
                                                (dynamicDigListBean.getTarget_user().intValue()));
                                    }
                                    return dynamicDigListBeanList;
                                });
                    } else {
                        // 返回期待以外的数据，比如状态为false，或者数据为空，发射空数据
                        return Observable.just(dynamicDigListBeanList);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * V2
     *
     * @param feedMark dyanmic feed mark
     * @param feedId   dyanmic detail id
     * @param after    max_id
     * @return
     */
    @Override
    public Observable<List<DynamicCommentBean>> getDynamicCommentListV2(
            final Long feedMark, Long feedId, Long after) {
        return mDynamicClient.getDynamicCommentListV2(feedId, after, TSListFragment.DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(listBaseJson -> {

                    final List<Object> userIds = new ArrayList<>();
                    if (listBaseJson.getComments() != null && listBaseJson.getComments().size() > 1) {
                        Collections.sort(listBaseJson.getComments(), new TimeStringSortClass());
                    }
                    for (DynamicCommentBean dynamicCommentBean : listBaseJson.getPinneds()) {
                        dynamicCommentBean.setPinned(true);
                        for (DynamicCommentBean commentBean : listBaseJson.getComments()) {
                            if (dynamicCommentBean.getComment_id().equals(commentBean.getComment_id())) {
                                listBaseJson.getComments().remove(commentBean);
                                break;
                            }
                        }
                    }

                    listBaseJson.getPinneds().addAll(listBaseJson.getComments());
                    for (DynamicCommentBean dynamicCommentBean : listBaseJson.getPinneds()) {
                        userIds.add(dynamicCommentBean.getUser_id());
                        userIds.add(dynamicCommentBean.getReply_to_user_id());
                        dynamicCommentBean.setFeed_mark(feedMark);
                    }

                    if (userIds.isEmpty()) {
                        return Observable.just(listBaseJson.getPinneds());
                    }
                    return mUserInfoRepository.getUserInfo(userIds)
                            .map(userinfobeans -> {
                                SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                for (UserInfoBean userInfoBean : userinfobeans) {
                                    userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                }
                                for (int i = 0; i < listBaseJson.getPinneds().size(); i++) {
                                    listBaseJson.getPinneds().get(i).setCommentUser(userInfoBeanSparseArray.get(
                                            (int) listBaseJson.getPinneds().get(i).getUser_id()));
                                    if (listBaseJson.getPinneds().get(i).getReply_to_user_id() == 0) {
                                        // 如果
                                        // reply_user_id = 0 回复动态
                                        UserInfoBean userInfoBean = new UserInfoBean();
                                        userInfoBean.setUser_id(0L);
                                        listBaseJson.getPinneds().get(i).setReplyUser(userInfoBean);
                                    } else {
                                        listBaseJson.getPinneds().get(i).setReplyUser(userInfoBeanSparseArray.get
                                                ((int) listBaseJson.getPinneds().get(i).getReply_to_user_id()));
                                    }
                                }
                                return listBaseJson.getPinneds();
                            });

                })
                .observeOn(AndroidSchedulers.mainThread())
                ;
    }


    /**
     * 获取动态详情 V2
     *
     * @param feed_id 动态id
     * @return
     */
    @Override
    public Observable<DynamicDetailBeanV2> getDynamicDetailBeanV2(Long feed_id) {
        return dealWithDynamicListV2(mDynamicClient.getDynamicDetailBeanV2(feed_id)
                .observeOn(Schedulers.io())
                .flatMap((Func1<DynamicDetailBeanV2, Observable<DynamicBeanV2>>) detail -> {
                    DynamicBeanV2 dynamicBeanV2 = new DynamicBeanV2();
                    List<DynamicDetailBeanV2> datas = new ArrayList<>();
                    datas.add(detail);
                    detail.setUserInfoBean(null);
                    dynamicBeanV2.setFeeds(datas);
                    return Observable.just(dynamicBeanV2);
                }), null, null, null, false)
                .flatMap((Func1<List<DynamicDetailBeanV2>,
                        Observable<DynamicDetailBeanV2>>) dynamicDetailBeanV2s -> {
                    if (dynamicDetailBeanV2s == null) {
                        return null;
                    }
                    DynamicDetailBeanV2 result = dynamicDetailBeanV2s.get(0);
                    if (result != null) {
                        result.setPaid_node(null);
                        result.handleData();
                    }
                    return Observable.just(result);
                });
    }


    protected Observable<List<GroupDynamicListBean>> dealWithGroupDynamicList(Observable<List<GroupDynamicListBean>>
                                                                                      observable,
                                                                              final String type, final boolean
                                                                                      isLoadMore) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(groupDynamicList -> {
                    final List<Object> user_ids = new ArrayList<>();
                    for (GroupDynamicListBean groupDynamicListBean : groupDynamicList) {
                        user_ids.add(groupDynamicListBean.getUser_id());
                        if (groupDynamicListBean.getNew_comments() == null || groupDynamicListBean
                                .getNew_comments().isEmpty()) {
                            continue;
                        }
                        for (GroupDynamicCommentListBean commentListBean : groupDynamicListBean.getNew_comments()) {
                            user_ids.add(commentListBean.getUser_id());
                            user_ids.add(commentListBean.getReply_to_user_id());
                        }
                    }
                    if (user_ids.isEmpty()) {
                        return Observable.just(groupDynamicList);
                    }
                    return mUserInfoRepository.getUserInfo(user_ids)
                            .map(userinfobeans -> {
                                SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                for (UserInfoBean userInfoBean : userinfobeans) {
                                    userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                }
                                for (GroupDynamicListBean dynamicBean : groupDynamicList) {
                                    dynamicBean.setUserInfoBean(userInfoBeanSparseArray.get(dynamicBean
                                            .getUser_id().intValue()));
                                    if (dynamicBean.getNew_comments() == null || dynamicBean.getNew_comments()
                                            .isEmpty()) {
                                        continue;
                                    }
                                    for (int i = 0; i < dynamicBean.getNew_comments().size(); i++) {
                                        UserInfoBean tmpUserinfo = userInfoBeanSparseArray.get((int) dynamicBean
                                                .getNew_comments().get(i).getUser_id());
                                        if (tmpUserinfo != null) {
                                            dynamicBean.getNew_comments().get(i).setCommentUser(tmpUserinfo);
                                        }
                                        if (dynamicBean.getNew_comments().get(i).getReply_to_user_id() == 0) {
                                            // 如果 reply_user_id = 0 回复动态
                                            UserInfoBean userInfoBean = new UserInfoBean();
                                            userInfoBean.setUser_id(0L);
                                            dynamicBean.getNew_comments().get(i).setReplyUser(userInfoBean);
                                        } else {
                                            if (userInfoBeanSparseArray.get((int) dynamicBean.getNew_comments()
                                                    .get(i).getReply_to_user_id()) != null) {
                                                dynamicBean.getNew_comments().get(i).setReplyUser
                                                        (userInfoBeanSparseArray.get((int) dynamicBean
                                                                .getNew_comments().get(i).getReply_to_user_id()));
                                            }
                                        }
                                    }

                                }
                                return groupDynamicList;
                            });
                });

    }

    protected Observable<List<GroupDynamicListBean>> dealWithGroupCollectDynamicList
            (Observable<List<CollectGroupDyanmciListBean>> observable,
             final String type, final boolean
                     isLoadMore) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(groupDynamicList -> {
                    final List<Object> user_ids = new ArrayList<>();
                    List<GroupDynamicListBean> result = new ArrayList<>();
                    for (CollectGroupDyanmciListBean groupDynamicListBean : groupDynamicList) {
                        user_ids.add(groupDynamicListBean.getPost().getUser_id());
                        groupDynamicListBean.getPost().setId((long) groupDynamicListBean.getPost_id());
                        result.add(groupDynamicListBean.getPost());
                    }
                    if (user_ids.isEmpty()) {
                        return Observable.just(result);
                    }
                    return mUserInfoRepository.getUserInfo(user_ids)
                            .map(userinfobeans -> {
                                SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                for (UserInfoBean userInfoBean : userinfobeans) {
                                    userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                }
                                for (CollectGroupDyanmciListBean dynamicBean : groupDynamicList) {
                                    dynamicBean.getPost().setId((long) dynamicBean.getPost_id());
                                    dynamicBean.getPost().setUserInfoBean(userInfoBeanSparseArray.get(dynamicBean
                                            .getPost().getUser_id().intValue()));
                                    if (dynamicBean.getPost().getNew_comments() == null || dynamicBean.getPost()
                                            .getNew_comments().isEmpty()) {
                                        continue;
                                    }
                                    for (int i = 0; i < dynamicBean.getPost().getNew_comments().size(); i++) {
                                        UserInfoBean tmpUserinfo = userInfoBeanSparseArray.get((int) dynamicBean
                                                .getPost().getNew_comments().get(i).getUser_id());
                                        if (tmpUserinfo != null) {
                                            dynamicBean.getPost().getNew_comments().get(i).setCommentUser
                                                    (tmpUserinfo);
                                        }
                                        if (dynamicBean.getPost().getNew_comments().get(i).getReply_to_user_id()
                                                == 0) { // 如果 reply_user_id = 0 回复动态
                                            UserInfoBean userInfoBean = new UserInfoBean();
                                            userInfoBean.setUser_id(0L);
                                            dynamicBean.getPost().getNew_comments().get(i).setReplyUser
                                                    (userInfoBean);
                                        } else {
                                            if (userInfoBeanSparseArray.get((int) dynamicBean.getPost()
                                                    .getNew_comments().get(i).getReply_to_user_id()) != null) {
                                                dynamicBean.getPost().getNew_comments().get(i).setReplyUser
                                                        (userInfoBeanSparseArray.get((int) dynamicBean.getPost()
                                                                .getNew_comments().get(i).getReply_to_user_id()));
                                            }
                                        }
                                    }

                                }
                                return result;
                            });
                });

    }

    protected Observable<DynamicDetailBeanV2> dealWithDynamic(Observable<DynamicDetailBeanV2> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(dynamicBean -> {
                    final List<Object> user_ids = new ArrayList<>();
                    user_ids.add(dynamicBean.getUser_id());
                    return getDynamicCommentListV2(dynamicBean.getFeed_mark(), dynamicBean.getId(), 0L)
                            .flatMap(dynamicCommentBeen -> {
                                for (DynamicCommentBean dynamicCommentBean : dynamicCommentBeen) {
                                    user_ids.add(dynamicCommentBean.getUser_id());
                                    user_ids.add(dynamicCommentBean.getReply_to_user_id());
                                    // 评论中增加
                                    dynamicCommentBean.setFeed_mark(dynamicBean.getFeed_mark());
                                    // feed_mark \和用户标记
                                    dynamicCommentBean.setFeed_user_id(dynamicBean.getUser_id());
                                }
                                mDynamicCommentBeanGreenDao.deleteCacheByFeedMark(dynamicBean.getFeed_mark())
                                ;// 删除本条动态的本地评论
                                dynamicBean.setComments(dynamicCommentBeen);
                                return Observable.just(dynamicBean);
                            }).flatMap(dynamicDetailBeanV2 -> {
                                if (user_ids.isEmpty()) {
                                    return Observable.just(dynamicBean);
                                }
                                return mUserInfoRepository.getUserInfo(user_ids)
                                        .map(userinfobeans -> {
                                            SparseArray<UserInfoBean> userInfoBeanSparseArray = new
                                                    SparseArray<>();
                                            for (UserInfoBean userInfoBean : userinfobeans) {
                                                userInfoBeanSparseArray.put(userInfoBean.getUser_id()
                                                        .intValue(), userInfoBean);
                                            }
                                            dynamicBean.setUserInfoBean(userInfoBeanSparseArray.get
                                                    (dynamicBean.getUser_id().intValue()));

                                            dynamicBean.handleData();
                                            for (int i = 0; i < dynamicBean.getComments().size(); i++) {
                                                dynamicBean.getComments().get(i).setCommentUser
                                                        (userInfoBeanSparseArray.get((int) dynamicBean
                                                                .getComments().get(i).getUser_id()));
                                                if (dynamicBean.getComments().get(i).getReply_to_user_id() ==
                                                        0) {
                                                    // 如果 reply_user_id = 0 回复动态
                                                    UserInfoBean userInfoBean = new UserInfoBean();
                                                    userInfoBean.setUser_id(0L);
                                                    dynamicBean.getComments().get(i).setReplyUser(userInfoBean);
                                                } else {
                                                    dynamicBean.getComments().get(i).setReplyUser
                                                            (userInfoBeanSparseArray.get((int) dynamicBean
                                                                    .getComments().get(i).getReply_to_user_id
                                                                            ()));
                                                }
                                            }
                                            return dynamicBean;
                                        });
                            });
                })
                .observeOn(AndroidSchedulers.mainThread())
                ;
    }

    /**
     * 动态数据处理，包括置顶数据过滤，用户信息获取
     *
     * @param observable
     * @param type       动态类型
     * @param chooseType 动态筛选(个人中心)
     * @param isLoadMore
     * @return
     */
    protected Observable<List<DynamicDetailBeanV2>> dealWithDynamicListV2
    (Observable<DynamicBeanV2> observable, final String type, String search, final String chooseType, final boolean isLoadMore) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(dynamicBeanV2 -> {
                    List<DynamicDetailBeanV2> topData = dynamicBeanV2.getPinned();
                    if (topData != null && !topData.isEmpty()) {
                        for (DynamicDetailBeanV2 data : topData) {
                            data.setTop(DynamicDetailBeanV2.TOP_SUCCESS);
                            if (dynamicBeanV2.getFeeds() != null) {
                                // 删除置顶重复的，只处理刷新
                                for (DynamicDetailBeanV2 dynamicDetailBeanV2 : dynamicBeanV2.getFeeds()) {
                                    if (data.getId().equals(dynamicDetailBeanV2.getId())) {
                                        dynamicBeanV2.getFeeds().remove(dynamicDetailBeanV2);
                                        break;
                                    }
                                }
                            }
                        }
                        boolean needTopData = TextUtils.isEmpty(search) && !ApiConfig.DYNAMIC_TYPE_FOLLOWS.equals(type) && !(ApiConfig
                                .DYNAMIC_TYPE_USERS.equals(type) && !MyDynamicTypeEnum.PINNED.value.equals(chooseType));
                        if (needTopData) {
                            dynamicBeanV2.getFeeds().addAll(0, topData);
                        }
                    }
                    return dynamicBeanV2.getFeeds();
                })
                .flatMap(listBaseJson -> {
                    if (listBaseJson.isEmpty()) {
                        return Observable.just(listBaseJson);
                    }
                    final List<Object> userIds = new ArrayList<>();
                    // 如果是热门，需要初始化时间
                    if (!isLoadMore && ApiConfig.DYNAMIC_TYPE_HOTS.equals(type)) {
                        for (int i = listBaseJson.size() - 1; i >= 0; i--) {
                            listBaseJson.get(i).setHot_creat_time(System.currentTimeMillis());
                        }
                    }
                    List<DynamicDetailBeanV2> topData = new ArrayList<>();


                    for (DynamicDetailBeanV2 dynamicBean : listBaseJson) {
                        if (dynamicBean.getUserInfoBean() == null) {
                            userIds.add(dynamicBean.getUser_id());
                        }
                        //如果是关注，需要初始化标记
                        if (ApiConfig.DYNAMIC_TYPE_FOLLOWS.equals(type)) {
                            dynamicBean.setFollowed(true);
                        }
                        // 提前设置 maxId 用于分页
                        dynamicBean.setMaxId(dynamicBean.getId());
                        if (dynamicBean.getComments() != null) {
                            mDynamicCommentBeanGreenDao.insertOrReplace(dynamicBean.getComments());
                            for (DynamicCommentBean dynamicCommentBean : dynamicBean.getComments()) {
                                if (dynamicCommentBean.getCommentUser() == null) {
                                    userIds.add(dynamicCommentBean.getUser_id());
                                }
                                if (dynamicCommentBean.getReplyUser() == null) {
                                    if (dynamicCommentBean.getReply_to_user_id() == 0) {
                                        UserInfoBean userInfoBean = new UserInfoBean();
                                        userInfoBean.setUser_id(0L);
                                        dynamicCommentBean.setReplyUser(userInfoBean);
                                    } else {
                                        userIds.add(dynamicCommentBean.getReply_to_user_id());
                                    }
                                }
                                // 评论中增加 feed_mark \和用户标记
                                dynamicCommentBean.setFeed_mark(dynamicBean.getFeed_mark());
                                dynamicCommentBean.setFeed_user_id(dynamicBean.getUser_id());
                            }
                            // 删除本条动态的本地评论
                            mDynamicCommentBeanGreenDao.deleteCacheByFeedMark(dynamicBean.getFeed_mark());
                        }
                        if (dynamicBean.getTop() == DynamicDetailBeanV2.TOP_SUCCESS) {
                            topData.add(dynamicBean);
                        }

                    }
                    // 置顶只有 热门、最新
                    if (!ApiConfig.DYNAMIC_TYPE_FOLLOWS.equals(type)) {
                        TopDynamicBean topDynamicBean = new TopDynamicBean();
                        topDynamicBean.setType(ApiConfig.DYNAMIC_TYPE_NEW.equals(type) ? TYPE_NEW :
                                TYPE_HOT);
                        topDynamicBean.setTopDynamics(topData);
                        mTopDynamicBeanGreenDao.insertOrReplace(topDynamicBean);
                    }

                    if (userIds.isEmpty()) {
                        return Observable.just(listBaseJson);
                    }

                    return mUserInfoRepository.getUserInfo(userIds)
                            .map(userinfobeans -> {
                                SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                for (UserInfoBean userInfoBean : userinfobeans) {
                                    userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                }

                                for (DynamicDetailBeanV2 dynamicBean : listBaseJson) {

                                    if (dynamicBean.getUserInfoBean() == null) {
                                        dynamicBean.setUserInfoBean(userInfoBeanSparseArray.get(dynamicBean
                                                .getUser_id().intValue()));
                                    }
                                    dynamicBean.handleData();
                                    if (dynamicBean.getComments() != null) {
                                        for (int i = 0; i < dynamicBean.getComments().size(); i++) {
                                            if (dynamicBean.getComments().get(i).getCommentUser() == null && userInfoBeanSparseArray.get((int) dynamicBean
                                                    .getComments().get(i)
                                                    .getUser_id()) != null) {
                                                dynamicBean.getComments().get(i).setCommentUser
                                                        (userInfoBeanSparseArray.get((int) dynamicBean.getComments()
                                                                .get(i).getUser_id()));
                                            }
                                            // 如果 reply_user_id = 0 回复动态
                                            if (dynamicBean.getComments().get(i).getReply_to_user_id() == 0) {
                                                UserInfoBean userInfoBean = new UserInfoBean();
                                                userInfoBean.setUser_id(0L);
                                                dynamicBean.getComments().get(i).setReplyUser(userInfoBean);
                                            } else {
                                                if (dynamicBean.getComments().get(i).getReplyUser() == null) {
                                                    dynamicBean.getComments().get(i).setReplyUser(userInfoBeanSparseArray
                                                            .get((int) dynamicBean.getComments().get(i)
                                                                    .getReply_to_user_id()));
                                                }
                                            }
                                        }
                                    }
                                }
                                return listBaseJson;
                            });
                })
                .flatMap((Func1<List<DynamicDetailBeanV2>, Observable<List<DynamicDetailBeanV2>>>) datas -> {

                    StringBuilder feeds = new StringBuilder();
                    StringBuilder infos = new StringBuilder();
                    StringBuilder circles = new StringBuilder();
                    StringBuilder posts = new StringBuilder();
                    StringBuilder questions = new StringBuilder();
                    StringBuilder answers = new StringBuilder();

                    for (DynamicDetailBeanV2 dynamicBean : datas) {
                        if (TextUtils.isEmpty(dynamicBean.getRepostable_type())) {
                            continue;
                        }
                        switch (dynamicBean.getRepostable_type()) {
                            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC:
                                // feed
                                feeds.append(dynamicBean.getRepostable_id());
                                feeds.append(",");
                                break;
                            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO:
                                // info
                                infos.append(dynamicBean.getRepostable_id());
                                infos.append(",");
                                break;
                            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE:
                                // circle
                                circles.append(dynamicBean.getRepostable_id());
                                circles.append(",");
                                break;
                            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST:
                                // post
                                posts.append(dynamicBean.getRepostable_id());
                                posts.append(",");
                                break;
                            case MessageAtFragment.QUESTION:
                                // question
                                questions.append(dynamicBean.getRepostable_id());
                                questions.append(",");
                                break;
                            case MessageAtFragment.ANSWER:
                                // answer
                                answers.append(dynamicBean.getRepostable_id());
                                answers.append(",");
                                break;
                            default:
                        }
                    }

                    // 获取上级资源
                    // 动态
                    Observable<List<DynamicDetailBeanV2>> feedObservable = getDynamicListV2(null, null, null, null,
                            false, null, feeds.toString())
                            .observeOn(Schedulers.io());
                    if (TextUtils.isEmpty(feeds.toString())) {
                        feedObservable = Observable.just(new ArrayList<>());
                    }

                    // 资讯
                    Observable<List<InfoListDataBean>> infoObservable = mInfoMainClient.getInfoList(null
                            , null, null, null, null, infos.toString()).observeOn(Schedulers.io());
                    if (TextUtils.isEmpty(infos.toString())) {
                        infoObservable = Observable.just(new ArrayList<>());
                    }

                    // 问题
                    Observable<List<QAListInfoBean>> questionObservable = mQAClient.getQAQustion(null
                            , null, null, null, questions.toString()).observeOn(Schedulers.io());
                    if (TextUtils.isEmpty(questions.toString())) {
                        questionObservable = Observable.just(new ArrayList<>());
                    }

                    // 回答
                    Observable<List<SimpleAnswerBean>> anserObservable = mQAClient.getSimpleAnswerList(answers.toString()).observeOn(Schedulers.io());
                    if (TextUtils.isEmpty(answers.toString())) {
                        anserObservable = Observable.just(new ArrayList<>());
                    }

                    // 圈子
                    Observable<List<CircleInfo>> circleObservable = mCircleClient.getAllCircle(null, null,
                            null, null, circles.toString()).observeOn(Schedulers.io());

                    if (TextUtils.isEmpty(circles.toString())) {
                        circleObservable = Observable.just(new ArrayList<>());
                    }

                    // 帖子
                    Observable<List<SimplePostBean>> postObservable = mCircleClient.getPostListByIds(posts.toString()).observeOn(Schedulers.io());
                    if (TextUtils.isEmpty(posts.toString())) {
                        postObservable = Observable.just(new ArrayList<>());
                    }

                    return Observable.zip(feedObservable, infoObservable, questionObservable, anserObservable,
                            circleObservable, postObservable,
                            (dynamicDetailBeanV2s, infoListDataBeans, qaListInfoBeans,
                             simpleAnswerBeans, circleInfos, simplePostBeans) -> {

                                LongSparseArray<DynamicDetailBeanV2> feedsArray = new LongSparseArray<>();
                                LongSparseArray<InfoListDataBean> infosArray = new LongSparseArray<>();
                                LongSparseArray<QAListInfoBean> questionsArray = new LongSparseArray<>();
                                LongSparseArray<SimplePostBean> postsArray = new LongSparseArray<>();
                                LongSparseArray<CircleInfo> circlesArray = new LongSparseArray<>();
                                LongSparseArray<SimpleAnswerBean> answersArray = new LongSparseArray<>();

                                for (DynamicDetailBeanV2 detailBeanV2 : dynamicDetailBeanV2s) {
                                    // 设置为已付费，避免生成填充数据
                                    detailBeanV2.setPaid_node(null);
                                    detailBeanV2.handleData();
                                    feedsArray.append(detailBeanV2.getId(), detailBeanV2);
                                }
                                for (InfoListDataBean listDataBean : infoListDataBeans) {
                                    infosArray.append(listDataBean.getId(), listDataBean);
                                }
                                for (QAListInfoBean qaListInfoBean : qaListInfoBeans) {
                                    questionsArray.append(qaListInfoBean.getId(), qaListInfoBean);
                                }
                                circles.delete(0, circles.length());
                                for (SimplePostBean postBean : simplePostBeans) {
                                    circles.append(postBean.getGroup_id());
                                    circles.append(",");
                                    postsArray.append(postBean.getId(), postBean);
                                }
                                for (CircleInfo circleInfo : circleInfos) {
                                    circlesArray.append(circleInfo.getId(), circleInfo);
                                }
                                for (SimpleAnswerBean answerBean : simpleAnswerBeans) {
                                    answersArray.append(answerBean.getId(), answerBean);
                                }

                                for (DynamicDetailBeanV2 detailBeanV2 : datas) {

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.
                                            equals(detailBeanV2.getRepostable_type())) {
                                        DynamicDetailBeanV2 feed = feedsArray.get(detailBeanV2.getRepostable_id().intValue());
                                        if (feed == null) {
                                            detailBeanV2.setMLetter(new Letter("",
                                                    mContext.getString(R.string.review_dynamic_deleted),
                                                    TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC, true));
                                            continue;
                                        }
                                        Letter letter = new Letter(feed.getUserInfoBean().getName(), feed.getFriendlyContent(),
                                                TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC);

                                        boolean hasImage, hasVideo;
                                        hasImage = feed.getImages() != null && !feed.getImages().isEmpty();
                                        hasVideo = feed.getVideo() != null;
                                        String dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD;
                                        if (!hasImage && !hasVideo) {
                                            dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD;
                                        }
                                        if (hasImage) {
                                            String image = ImageUtils.imagePathConvertV2(feed.getImages().get(0).getFile(), mContext.getResources()
                                                            .getDimensionPixelOffset(R.dimen.chat_info_image_widht), mContext.getResources()
                                                            .getDimensionPixelOffset(R.dimen.chat_info_image_height),
                                                    ImageZipConfig.IMAGE_80_ZIP);
                                            letter.setImage(image);
                                            dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE;
                                        }
                                        if (hasVideo) {
                                            String image = String.format(ApiConfig.APP_DOMAIN + ApiConfig.FILE_PATH,
                                                    feed.getVideo().getCover_id());
                                            letter.setImage(image);
                                            dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_VIDEO;
                                        }
                                        letter.setId(String.valueOf(detailBeanV2.getRepostable_id()));
                                        letter.setDynamic_type(dynamicType);
                                        detailBeanV2.setMLetter(letter);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO.
                                            equals(detailBeanV2.getRepostable_type())) {
                                        InfoListDataBean info = infosArray.get(detailBeanV2.getRepostable_id().intValue());
                                        if (info == null) {
                                            detailBeanV2.setMLetter(new Letter("",
                                                    mContext.getString(R.string.review_dynamic_deleted),
                                                    TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC, true));
                                            continue;
                                        }
                                        int id;
                                        if (info.getImage() != null) {
                                            id = info.getImage().getId();
                                        } else {
                                            id = RegexUtils.getImageIdFromMarkDown(MarkdownConfig.IMAGE_FORMAT, info.getContent());
                                        }
                                        String image = ImageUtils.imagePathConvertV2(id, mContext.getResources()
                                                        .getDimensionPixelOffset(R.dimen.chat_info_image_widht), mContext.getResources()
                                                        .getDimensionPixelOffset(R.dimen.chat_info_image_height),
                                                ImageZipConfig.IMAGE_80_ZIP);
                                        String content = TextUtils.isEmpty(info.getSubject()) ? info.getContent()
                                                : info.getSubject();
                                        content = RegexUtils.replaceAllLines(content);
                                        content = RegexUtils.getReplaceAll(content, MarkdownConfig.HTML_FORMAT, "");
                                        content = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, content);
                                        Letter letter = new Letter(info.getTitle(), content, TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO);
                                        letter.setImage(image);
                                        letter.setId(detailBeanV2.getRepostable_id() + "");
                                        detailBeanV2.setMLetter(letter);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE.
                                            equals(detailBeanV2.getRepostable_type())) {
                                        CircleInfo circle = circlesArray.get(detailBeanV2.getRepostable_id().intValue());
                                        if (circle == null) {
                                            detailBeanV2.setMLetter(new Letter("",
                                                    mContext.getString(R.string.review_dynamic_deleted),
                                                    TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC, true));
                                            continue;
                                        }
                                        boolean isClosedCircle = CircleInfo.CirclePayMode.PAID.value.equals(circle.getMode())
                                                || CircleInfo.CirclePayMode.PRIVATE.value.equals(circle.getMode());
                                        boolean isJoined = circle.getJoined() != null && circle.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;

                                        Letter letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE);
                                        if (isClosedCircle && !isJoined) {
                                            letter.setCircle_type(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_POST_CIRCLE_TYPE);
                                        }
                                        letter.setName(circle.getName());
                                        String content = TextUtils.isEmpty(circle.getSummary()) ? mContext.getString(R.string
                                                .share_default, mContext.getString(R.string.app_name)) : circle.getSummary();
                                        letter.setContent(content);

                                        letter.setImage(circle.getAvatar() != null ? circle.getAvatar().getUrl() : "");
                                        letter.setId(detailBeanV2.getRepostable_id() + "");
                                        detailBeanV2.setMLetter(letter);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST.
                                            equals(detailBeanV2.getRepostable_type())) {
                                        SimplePostBean post = postsArray.get(detailBeanV2.getRepostable_id().intValue());
                                        if (post == null) {
                                            detailBeanV2.setMLetter(new Letter("",
                                                    mContext.getString(R.string.review_dynamic_deleted),
                                                    TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC, true));
                                            continue;
                                        }
                                        Letter letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST);

                                        if (post.getImage() > 0) {
                                            String image = ImageUtils.imagePathConvertV2(post.getImage(), mContext.getResources()
                                                            .getDimensionPixelOffset(R.dimen.chat_post_image_widht), mContext.getResources()
                                                            .getDimensionPixelOffset(R.dimen.chat_post_image_widht),
                                                    ImageZipConfig.IMAGE_80_ZIP);
                                            letter.setImage(image);
                                        }
                                        letter.setCircle_id(post.getGroup_id() + "");
                                        letter.setName(post.getTitle());
                                        String content = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, post.getSummary());
                                        content = content.replaceAll(MarkdownConfig.NETSITE_FORMAT, Link.DEFAULT_NET_SITE);
                                        letter.setContent(content);
                                        letter.setId(detailBeanV2.getRepostable_id() + "");
                                        detailBeanV2.setMLetter(letter);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION.
                                            equals(detailBeanV2.getRepostable_type())) {
                                        QAListInfoBean question = questionsArray.get(detailBeanV2.getRepostable_id().intValue());
                                        if (question == null) {
                                            detailBeanV2.setMLetter(new Letter("",
                                                    mContext.getString(R.string.review_dynamic_deleted),
                                                    TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC, true));
                                            continue;
                                        }
                                        Letter letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION);
                                        letter.setName(question.getSubject());
                                        letter.setContent(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT,
                                                RegexUtils.replaceAllLines(question.getBody())));
                                        letter.setId(detailBeanV2.getRepostable_id() + "");
                                        detailBeanV2.setMLetter(letter);
                                    }

                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS.
                                            equals(detailBeanV2.getRepostable_type())) {
                                        SimpleAnswerBean answer = answersArray.get(detailBeanV2.getRepostable_id().intValue());
                                        if (answer == null) {
                                            detailBeanV2.setMLetter(new Letter("",
                                                    mContext.getString(R.string.review_dynamic_deleted),
                                                    TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC, true));
                                            continue;
                                        }
                                        Letter letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS);
                                        letter.setName(answer.getQuestion().getSubject());
                                        letter.setContent(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT,
                                                RegexUtils.replaceAllLines(answer.getBody())));
                                        letter.setId(detailBeanV2.getRepostable_id() + "");
                                        detailBeanV2.setMLetter(letter);
                                    }
                                }
                                return datas;
                            })
                            .observeOn(Schedulers.io())
                            .flatMap((Func1<List<DynamicDetailBeanV2>, Observable<List<DynamicDetailBeanV2>>>) dynamicDetailBeanV2s -> {
                                // 那帖子所属的圈子信息
                                Observable<List<CircleInfo>> postCircleObservable = mCircleClient.getAllCircle(null, null,
                                        null, null, circles.toString());

                                if (TextUtils.isEmpty(circles.toString())) {
                                    postCircleObservable = Observable.just(new ArrayList<>());
                                }
                                return postCircleObservable.flatMap((Func1<List<CircleInfo>, Observable<List<DynamicDetailBeanV2>>>) circleInfos -> {
                                    SparseArray<CircleInfo> circleInfoSparseArray = new SparseArray<>();
                                    for (CircleInfo circleInfo : circleInfos) {
                                        circleInfoSparseArray.put(circleInfo.getId().intValue(), circleInfo);
                                    }
                                    for (DynamicDetailBeanV2 detailBeanV2 : datas) {
                                        boolean isPost = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST.
                                                equals(detailBeanV2.getRepostable_type());
                                        if (!isPost) {
                                            continue;
                                        }
                                        if (detailBeanV2.getMLetter() == null) {
                                            continue;
                                        }

                                        String circleId = detailBeanV2.getMLetter().getCircle_id();
                                        if (TextUtils.isEmpty(circleId)) {
                                            continue;
                                        }
                                        int groupId = Integer.parseInt(circleId);
                                        CircleInfo circle = circleInfoSparseArray.get(groupId);
                                        if (circle == null) {
                                            continue;
                                        }
                                        boolean isClosedCircle = CircleInfo.CirclePayMode.PAID.value.equals(circle.getMode())
                                                || CircleInfo.CirclePayMode.PRIVATE.value.equals(circle.getMode());
                                        boolean isJoined = circle.getJoined() != null && circle.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;

                                        if (isClosedCircle && !isJoined) {
                                            detailBeanV2.getMLetter().setCircle_type(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_POST_CIRCLE_TYPE);
                                        }
                                    }
                                    return Observable.just(datas);
                                });
                            })
                            .subscribeOn(Schedulers.io());
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<List<DynamicDetailBeanV2>> dealWithDynamicListV2
            (Observable<List<DynamicDetailBeanV2>> observable, final String type, final boolean isLoadMore) {

        return dealWithDynamicListV2(observable.flatMap((Func1<List<DynamicDetailBeanV2>, Observable<DynamicBeanV2>>) dynamicDetailBeanV2s -> {
            DynamicBeanV2 dynamicBeanV2 = new DynamicBeanV2();
            dynamicBeanV2.setFeeds(dynamicDetailBeanV2s);
            return Observable.just(dynamicBeanV2);
        }), null, null, null, isLoadMore);
    }

    /**
     * 动态置顶
     *
     * @param feed_id
     * @param amount
     * @param day
     * @return
     */
    @Override
    public Observable<BaseJsonV2<Integer>> stickTop(long feed_id, double amount, int day, String psd) {
        return mDynamicClient.stickTopDynamic(feed_id, (int) amount, day, psd)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BaseJsonV2<Integer>> commentStickTop(long feedId, long commentId, double amount, int day, String psd) {
        return mDynamicClient.stickTopDynamicComment(feedId, commentId, (int) amount, day, psd)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DynamicCommentToll> tollDynamicComment(Long feedId, int amount) {
        return mDynamicClient.setDynamicCommentToll(feedId, amount)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<DynamicDetailBeanV2>> getDynamicListForSomeone(Long userId, Long maxId, String chooseType) {
        return getDynamicListV2(DYNAMIC_TYPE_USERS, maxId, null, userId, false, chooseType, null);
    }
}

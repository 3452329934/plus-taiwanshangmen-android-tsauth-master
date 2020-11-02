package com.zhiyicx.thinksnsplus.modules.q_a.detail.question;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;

import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.base.SystemConfigBean;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.baseproject.share.OnShareCallbackListener;
import com.zhiyicx.baseproject.share.Share;
import com.zhiyicx.baseproject.share.ShareContent;
import com.zhiyicx.baseproject.share.SharePolicy;
import com.zhiyicx.common.base.BaseFragment;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.AnswerInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.QAPublishBean;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.AnswerInfoListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.QAListInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseQARepository;
import com.zhiyicx.thinksnsplus.data.source.repository.SystemRepository;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.utils.TSShareUtils;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_SHARE_QA_QUESTION_DETAIL;
import static com.zhiyicx.thinksnsplus.config.EventBusTagConfig.EVENT_UPDATE_QUESTION_DELETE;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/15
 * @contact email:648129313@qq.com
 */
@FragmentScoped
public class QuestionDetailPresenter extends AppBasePresenter<QuestionDetailContract.View>
        implements QuestionDetailContract.Presenter, OnShareCallbackListener {

    @Inject
    public SharePolicy mSharePolicy;
    @Inject
    AnswerInfoListBeanGreenDaoImpl mAnswerInfoListBeanGreenDao;
    @Inject
    QAListInfoBeanGreenDaoImpl mQAListInfoBeanGreenDao;
    @Inject
    SystemRepository mSystemRepository;
    @Inject
    BaseQARepository mBaseQARepository;

    private SystemConfigBean mSystemConfigBean;
    private Subscription subscription;


    @Inject
    public QuestionDetailPresenter(QuestionDetailContract.View rootView) {
        super(rootView);
    }

    @Override
    public SystemConfigBean getSystemConfig() {
        if (mSystemConfigBean == null) {
            mSystemConfigBean = mSystemRepository.getBootstrappersInfoFromLocal();
        }
        return mSystemConfigBean;
    }

    @Override
    public void saveQuestion(QAPublishBean qaPublishBean) {
        mBaseQARepository.saveQuestion(qaPublishBean);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        getQuestionDetail(mRootView.getCurrentQuestion().getId() + "", maxId, isLoadMore);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(new ArrayList<>(), isLoadMore);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<AnswerInfoBean> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public void getQuestionDetail(String questionId, Long maxId, boolean isLoadMore) {
        Subscription subscription = Observable.zip(mBaseQARepository.getQuestionDetail(questionId),
                mBaseQARepository.getAnswerList(questionId, mRootView.getCurrentOrderType(), maxId.intValue()),
                (qaListInfoBean, answerInfoBeanList) -> {
                    qaListInfoBean.setAnswerInfoBeanList(dealAnswerList(maxId, qaListInfoBean, answerInfoBeanList));
                    mQAListInfoBeanGreenDao.insertOrReplace(qaListInfoBean);
                    return qaListInfoBean;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<QAListInfoBean>() {
                    @Override
                    protected void onSuccess(QAListInfoBean data) {
                        mRootView.setQuestionDetail(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage(message);
                        mRootView.onResponseError(null, isLoadMore);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.onResponseError(throwable, isLoadMore);

                    }


                });
        addSubscrebe(subscription);
    }

    private List<AnswerInfoBean> dealAnswerList(long maxId, QAListInfoBean qaListInfoBean, List<AnswerInfoBean> list) {
        List<AnswerInfoBean> totalList = new ArrayList<>();
        if (qaListInfoBean.getInvitation_answers() != null && maxId == 0L) {
            totalList.addAll(qaListInfoBean.getInvitation_answers());
        }
        if (qaListInfoBean.getAdoption_answers() != null && maxId == 0L) {
            totalList.addAll(qaListInfoBean.getAdoption_answers());
        }
        totalList.addAll(list);
        return totalList;
    }

    @Override
    public void handleFollowState(String questionId, boolean isFollowed) {
        mRootView.getCurrentQuestion().setWatched(isFollowed);
        if (isFollowed) {
            mRootView.getCurrentQuestion().setWatchers_count(mRootView.getCurrentQuestion().getWatchers_count() + 1);
        } else {
            mRootView.getCurrentQuestion().setWatchers_count(mRootView.getCurrentQuestion().getWatchers_count() - 1);
        }
        mRootView.updateFollowState();
        mBaseQARepository.handleQuestionFollowState(questionId, isFollowed);
    }

    @Override
    public void shareQuestion(Bitmap bitmap) {
        ((UmengSharePolicyImpl) mSharePolicy).setOnShareCallbackListener(this);
        ShareContent shareContent = new ShareContent();
        shareContent.setTitle(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, mRootView.getCurrentQuestion().getSubject()));
        shareContent.setUrl(TSShareUtils.convert2ShareUrl(String.format(Locale.getDefault(), APP_PATH_SHARE_QA_QUESTION_DETAIL, mRootView
                .getCurrentQuestion().getId
                        ())));
        shareContent.setContent(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, mRootView.getCurrentQuestion().getBody()));

        if (bitmap == null) {
            shareContent.setBitmap(ConvertUtils.drawBg4Bitmap(Color.WHITE, BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon)));
        } else {
            shareContent.setBitmap(bitmap);
        }
        mSharePolicy.setShareContent(shareContent);

        UmengSharePolicyImpl.ShareBean forward = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_forwarding, mContext.getString(R.string
                .share_forward), Share.FORWARD);
        UmengSharePolicyImpl.ShareBean letter = new UmengSharePolicyImpl.ShareBean(R.mipmap.detail_share_sent, mContext.getString(R.string
                .share_letter), Share.LETTER);

        List<UmengSharePolicyImpl.ShareBean> data = new ArrayList<>();
        data.add(forward);
        data.add(letter);

        mSharePolicy.showShare(((TSFragment) mRootView).getActivity(), data);
    }


    @Override
    public void adoptionAnswer(QAListInfoBean qaListInfoBean, AnswerInfoBean answerInfoBean) {
        Subscription subscription = mBaseQARepository.adoptionAnswer(qaListInfoBean.getId(), answerInfoBean.getId())
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<Object> data) {
                        answerInfoBean.setAdoption(1);
                        qaListInfoBean.setHas_adoption(true);
                        List<AnswerInfoBean> answers = new ArrayList<>();
                        answers.add(answerInfoBean);
                        qaListInfoBean.setAdoption_answers(answers);
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
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.err_net_not_work));
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void deleteQuestion(Long question_id) {
        mRootView.handleLoading(true, false, mContext.getString(R.string.info_deleting));
        Subscription subscription = mBaseQARepository.deleteQuestion(question_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {

                    @Override
                    protected void onSuccess(BaseJsonV2<Object> data) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(EVENT_UPDATE_QUESTION_DELETE, mRootView.getCurrentQuestion());
                        EventBus.getDefault().post(bundle, EVENT_UPDATE_QUESTION_DELETE);
                        mRootView.showSnackSuccessMessage(mContext.getString(R.string.qa_question_delete_success));
                        Subscription subscribe = Observable.timer(500, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(aLong -> mRootView.deleteSuccess(), Throwable::printStackTrace);
                        addSubscrebe(subscribe);

                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        showErrorTip(throwable);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void applyForExcellent(Long question_id, String psd) {
        subscription = handleIntegrationBlance((long) getSystemConfig().getQuestionConfig().getApply_amount())
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R
                        .string.apply_doing)))
                .flatMap(o -> mBaseQARepository.applyForExcellent(question_id, psd))
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<Object> data) {
                        mRootView.paySuccess();
                        mRootView.handleLoading(false, true, mContext.getString(R.string.apply_for_success));
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.payFailed(message);
                        mRootView.handleLoading(false, false, message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        if (isIntegrationBalanceCheck(throwable)) {
                            mRootView.paySuccess();
                            return;
                        }
                        mRootView.payFailed(throwable.getMessage());
                        mRootView.handleLoading(false, false, throwable.getMessage());
                    }
                });


        addSubscrebe(subscription);
    }

    @Override
    public void handleAnswerLike(boolean isLiked, long answer_id, AnswerInfoBean answerInfoBean) {
        answerInfoBean.setLiked(isLiked);
        mAnswerInfoListBeanGreenDao.insertOrReplace(answerInfoBean);
        mBaseQARepository.handleAnswerLike(isLiked, answer_id);
    }

    @Override
    public void payForOnlook(long answerId, int position, String psd) {

        subscription = handleIntegrationBlance((long) getSystemConfig().getQuestionConfig().getOnlookers_amount())
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R
                        .string.pay_alert_ing)))
                .flatMap(o -> mBaseQARepository.payForOnlook(answerId, psd))
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<AnswerInfoBean>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<AnswerInfoBean> data) {
                        mRootView.paySuccess();
                        List<AnswerInfoBean> invitationAanswers = new ArrayList<>();
                        invitationAanswers.add(data.getData());
                        mRootView.getCurrentQuestion().setInvitation_answers(invitationAanswers);
                        mRootView.getListDatas().set(position, data.getData());
                        mRootView.refreshData(position);
                        EventBus.getDefault().post(data.getData(), EventBusTagConfig.EVENT_ONLOOK_ANSWER);
                        mRootView.showSnackMessage(mContext.getString(R.string.pay_alert_success), Prompt.DONE);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        if (usePayPassword()) {
                            mRootView.payFailed(message);
                            mRootView.dismissSnackBar();
                            return;
                        }
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        if (isIntegrationBalanceCheck(throwable)) {
                            mRootView.paySuccess();
                            return;
                        }
                        if (usePayPassword()) {
                            mRootView.payFailed(mContext.getString(R.string.pay_alert_failed));
                            mRootView.dismissSnackBar();
                            return;
                        }
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.pay_alert_failed));
                    }
                });
        addSubscrebe(subscription);

    }

    @Override
    public void canclePay() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onStart(Share share) {
        Letter letter;
        switch (share) {
            case FORWARD:
                letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION);
                letter.setName(mRootView.getCurrentQuestion().getSubject());
                String content = RegexUtils.replaceAllLines(mRootView.getCurrentQuestion().getBody());
                content = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, content);
                letter.setContent(content);
                letter.setId(mRootView.getCurrentQuestion().getId() + "");
                SendDynamicDataBean sendWordsDynamicDataBean = new SendDynamicDataBean();
                sendWordsDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
                sendWordsDynamicDataBean.setDynamicType(SendDynamicDataBean.TEXT_ONLY_DYNAMIC);
                SendDynamicActivity.startToSendDynamicActivity(((BaseFragment) mRootView).getActivity(), sendWordsDynamicDataBean, letter);
                break;
            case LETTER:
                letter = new Letter(TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_QUESTION);
                letter.setName(mRootView.getCurrentQuestion().getSubject());
                letter.setContent(RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, RegexUtils.replaceAllLines(mRootView.getCurrentQuestion()
                        .getBody())));
                letter.setId(mRootView.getCurrentQuestion().getId() + "");
                ChooseFriendActivity.startChooseFriendActivity(((BaseFragment) mRootView).getActivity(), letter);
                break;
            default:
        }
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

    @Subscriber(tag = EventBusTagConfig.EVENT_UPDATE_ANSWER_LIST_LIKE)
    public void updateLike(Bundle bundle) {
        if (bundle != null) {
            AnswerInfoBean answerInfoBean = (AnswerInfoBean) bundle.
                    getSerializable(EventBusTagConfig.EVENT_UPDATE_ANSWER_LIST_LIKE);
            if (answerInfoBean != null) {
                for (AnswerInfoBean answerInfoBean1 : mRootView.getListDatas()) {
                    if (answerInfoBean.getId().equals(answerInfoBean1.getId())) {
                        mRootView.getListDatas().set(mRootView.getListDatas().indexOf(answerInfoBean1), answerInfoBean);
                        mRootView.refreshData();
                        break;
                    }
                }
            }
        }
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_PUBLISH_ANSWER)
    public void publishAnswer(AnswerInfoBean data) {
        if (data != null) {
            List<AnswerInfoBean> datas = mRootView.getListDatas();
            if (datas != null && datas.size() > 0
                    && datas.get(0).getUser() == null
                    && TextUtils.isEmpty(datas.get(0).getBody())
                    && datas.get(0).getInvited() != 1) {// 占位
                mRootView.getListDatas().remove(0);
            }
            mRootView.getListDatas().add(data);
            mRootView.refreshData();
            mRootView.getCurrentQuestion().setAnswers_count(mRootView.getCurrentQuestion().getAnswers_count() + 1);
            mRootView.getCurrentQuestion().setMy_answer(data);
            mRootView.updateAnswerCount();
        }
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_UPDATE_ANSWER_OR_QUESTION)
    public void updateData(Long tag) {
        requestNetData(tag, false);
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_UPDATE_ANSWER_LIST_DELETE)
    public void deleteAnswer(AnswerInfoBean answerInfoBean) {
        if (mRootView.getListDatas().remove(answerInfoBean)) {
            if (answerInfoBean.getUser_id() == AppApplication.getMyUserIdWithdefault()) {
                mRootView.getCurrentQuestion().setMy_answer(null);
            }
            if (mRootView.getListDatas().isEmpty()) {
                AnswerInfoBean emptyData = new AnswerInfoBean();
                mRootView.getListDatas().add(emptyData);
            }
            mRootView.refreshData();
            mRootView.getCurrentQuestion().setAnswers_count(mRootView.getCurrentQuestion().getAnswers_count() - 1);
            mRootView.updateAnswerCount();
        }
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_ADOPT_ANSWER)
    public void adoptAnswer(final AnswerInfoBean answerInfoBean) {
        if (mRootView.getListDatas() != null) {
            for (AnswerInfoBean infoBean : mRootView.getListDatas()){
                if (infoBean.equals(answerInfoBean)){
                    infoBean.setAdoption(answerInfoBean.getAdoption());
                    mRootView.getCurrentQuestion().setAdoption_answers(Arrays.asList(answerInfoBean));
                    mRootView.refreshData();
                    break;
                }
            }
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public QAListInfoBean getCurrentQuestion() {
        return mRootView.getCurrentQuestion();
    }
}

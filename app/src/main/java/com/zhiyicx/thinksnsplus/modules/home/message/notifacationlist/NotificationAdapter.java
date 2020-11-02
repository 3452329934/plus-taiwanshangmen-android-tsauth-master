package com.zhiyicx.thinksnsplus.modules.home.message.notifacationlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.common.config.ConstantConfig;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.notify.UserNotifyMsgBean;
import com.zhiyicx.thinksnsplus.data.source.repository.i.INotificationRepository;
import com.zhiyicx.thinksnsplus.modules.certification.detail.CertificationDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.v2.CircleDetailActivityV2;
import com.zhiyicx.thinksnsplus.modules.circle.pre.PreCircleActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailActivity;
import com.zhiyicx.thinksnsplus.modules.home.message.messagereview.MessageReviewActivity;
import com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterActivity;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.answer.AnswerDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity;
import com.zhiyicx.thinksnsplus.modules.topic.detail.TopicDetailActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.meta.When;

import static com.zhiyicx.thinksnsplus.modules.q_a.detail.topic.TopicDetailActivity.BUNDLE_TOPIC_BEAN;

/**
 * @author Catherine
 * @describe 先随便展示一下吧
 * @date 2017/8/31
 * @contact email:648129313@qq.com
 */

public class NotificationAdapter extends CommonAdapter<UserNotifyMsgBean.DataBeanX> {

    public NotificationAdapter(Context context, List<UserNotifyMsgBean.DataBeanX> datas) {
        super(context, R.layout.item_notification, datas);
    }

    @Override
    protected void convert(ViewHolder holder, UserNotifyMsgBean.DataBeanX tspNotificationBean, int position) {
        holder.setText(R.id.tv_notification_content, tspNotificationBean.getData().getContent());
        holder.setText(R.id.tv_time, TimeUtils.getTimeFriendlyNormal(tspNotificationBean.getCreated_at()));

        RxView.clicks(holder.itemView)
                .throttleFirst(ConstantConfig.JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .filter(aVoid -> tspNotificationBean.getData() != null)
                .subscribe(aVoid -> {
                    switch (tspNotificationBean.getData().getType()) {
                        case INotificationRepository.REWARD_FEEDS:
                            DynamicDetailActivity.startDynamicDetailActivity(mContext, tspNotificationBean.getData().getFeed_id());
                            break;
                        case INotificationRepository.REWARD_USER:
                            PersonalCenterFragment.startToPersonalCenter(mContext,
                                    new UserInfoBean(tspNotificationBean.getData().getSender().getName()));
                            break;
                        case INotificationRepository.USER_CERTIFICATION:
                            Intent certify = new Intent(mContext, CertificationDetailActivity.class);
                            mContext.startActivity(certify);
                            break;
                        case INotificationRepository.QA_ANSWER_REWARD:
                        case INotificationRepository.QA_ANSWER_ADOPTION:
                            AnswerDetailsActivity.startAnswerDetailsActivity(mContext, tspNotificationBean.getData().getAnswer().getId());
                            break;
                        case INotificationRepository.QA_INVITATION:
                            QuestionDetailActivity.startQuestionDetailActivity(mContext, tspNotificationBean.getData().getQuestion().getId());
                            break;
                        case INotificationRepository.PINNED_FEED_COMMENT:
                            if (INotificationRepository.CERTIFICATION_REJECTED.equals(tspNotificationBean.getData().getState()) ||
                                    INotificationRepository.CERTIFICATION_PASSED.equals(tspNotificationBean.getData().getState())) {
                                DynamicDetailActivity.startDynamicDetailActivity(mContext, tspNotificationBean.getData().getFeed().getId());
                            } else {
                                MessageReviewActivity.startMessageReviewActivity(mContext, 0);
                            }
                            break;
                        case INotificationRepository.PINNED_NEWS_COMMENT:
                            if (INotificationRepository.CERTIFICATION_REJECTED.equals(tspNotificationBean.getData().getState()) ||
                                    INotificationRepository.CERTIFICATION_PASSED.equals(tspNotificationBean.getData().getState())
                            ) {
                                InfoDetailsActivity.startInfoDetailsActivity(mContext, tspNotificationBean.getData().getNews().getId());
                            } else {
                                MessageReviewActivity.startMessageReviewActivity(mContext, 1);
                            }
                            break;
                        case INotificationRepository.GROUP_COMMENT_PINNED:
                        case INotificationRepository.GROUP_SEND_COMMENT_PINNED:
                            if (INotificationRepository.CERTIFICATION_REJECTED.equals(tspNotificationBean.getData().getState()) ||
                                    INotificationRepository.CERTIFICATION_PASSED.equals(tspNotificationBean.getData().getState())) {
                                CirclePostDetailActivity.startCirclePostDetailActivity(mContext, tspNotificationBean.getData().getGroup_id(),
                                        tspNotificationBean.getData().getPost().getId());
                            } else {
                                MessageReviewActivity.startMessageReviewActivity(mContext, 2);
                            }
                            break;
                        case INotificationRepository.GROUP_POST_PINNED:
                            if (INotificationRepository.CERTIFICATION_REJECTED.equals(tspNotificationBean.getData().getState()) ||
                                    INotificationRepository.CERTIFICATION_PASSED.equals(tspNotificationBean.getData().getState())) {
                                CirclePostDetailActivity.startCirclePostDetailActivity(mContext, tspNotificationBean.getData().getGroup_id(),
                                        tspNotificationBean.getData().getPost().getId());
                            } else {
                                MessageReviewActivity.startMessageReviewActivity(mContext, 3);
                            }
                            break;
                        case INotificationRepository.GROUP_JOIN:
                            if (INotificationRepository.CERTIFICATION_REJECTED.equals(tspNotificationBean.getData().getState())) {
                                PreCircleActivity.startPreCircleActivity(mContext, tspNotificationBean.getData().getGroup().getId());

                            } else if (INotificationRepository.CERTIFICATION_PASSED.equals(tspNotificationBean.getData().getState())) {
                                CircleDetailActivityV2.startCircleDetailActivity(mContext, tspNotificationBean.getData().getGroup().getId());
                            } else {
                                MessageReviewActivity.startMessageReviewActivity(mContext, 4);
                            }
                            break;
                        case INotificationRepository.GROUP_TRANSFORM:
                            /*圈子转让*/
                            CircleDetailActivityV2.startCircleDetailActivity(mContext, tspNotificationBean.getData().getGroup().getId());
                            break;
                        case INotificationRepository.REWARD_NEWS:
                            InfoDetailsActivity.startInfoDetailsActivity(mContext, tspNotificationBean.getData().getNews().getId());
                            break;
                        /** 被打赏了帖子*/
                        case INotificationRepository.GROUP_POST_REWARD:
                            CirclePostDetailActivity.startCirclePostDetailActivity(mContext, tspNotificationBean.getData().getGroup_id(),
                                    tspNotificationBean.getData().getPost().getId());
                            break;

                        case INotificationRepository.PINNED_FEED:
                            /*动态置顶*/
                            DynamicDetailActivity.startDynamicDetailActivity(mContext, tspNotificationBean.getData().getFeed().getId());
                            break;
                        /*帖子置顶*/
                        case INotificationRepository.GROUP_PINNED_ADMIN:
                            CirclePostDetailActivity.startCirclePostDetailActivity(mContext, tspNotificationBean.getData().getGroup_id(), tspNotificationBean.getData().getPost().getId());
                            break;
                        /**举报*/
                        case INotificationRepository.REPORT:
                            long sourceId = tspNotificationBean.getData().getResource().getId();
                            switch (tspNotificationBean.getData().getResource().getType()) {
                                case INotificationRepository.REPORT_USER:
                                    PersonalCenterFragment.startToPersonalCenter(mContext, new UserInfoBean(sourceId));
                                    break;
                                case INotificationRepository.REPORT_TOPIC:
                                    TopicDetailActivity.startTopicDetailActivity(mContext, sourceId);
                                    break;
                                case INotificationRepository.REPORT_COMMENT:
                                    break;
                                case INotificationRepository.REPORT_QUESTION:
                                    QuestionDetailActivity.startQuestionDetailActivity(mContext, sourceId);
                                    break;
                                case INotificationRepository.REPORT_FEED:
                                    DynamicDetailActivity.startDynamicDetailActivity(mContext, sourceId);
                                    break;
                                case INotificationRepository.REPORT_NEWS:
                                    InfoDetailsActivity.startInfoDetailsActivity(mContext, sourceId);
                                    break;
                                case INotificationRepository.REPORT_POSTS:
                                    CirclePostDetailActivity.startCirclePostDetailActivity(mContext, 0, sourceId);
                                    break;
                                case INotificationRepository.REPORT_GROUPS:
                                    CircleDetailActivityV2.startCircleDetailActivity(mContext, sourceId);
                                    break;
                                case INotificationRepository.REPORT_ANSWER:
                                    AnswerDetailsActivity.startAnswerDetailsActivity(mContext, sourceId);
                                    break;
                                case INotificationRepository.REPORT_GROUPS_POST:
                                    CircleDetailActivityV2.startCircleDetailActivity(mContext, sourceId);
                                    break;
                            }
                            break;
                        case INotificationRepository.QA_TOPIC_ACCEPT:
                            /** 问答专题被通过*/
                            Intent intent = new Intent(mContext, com.zhiyicx.thinksnsplus.modules.q_a.detail.topic.TopicDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(BUNDLE_TOPIC_BEAN, tspNotificationBean.getData().getTopic());
                            intent.putExtra(BUNDLE_TOPIC_BEAN, bundle);
                            mContext.startActivity(intent);
                            break;
                        case INotificationRepository.GROUP_MEMBER:
                            CircleDetailActivityV2.startCircleDetailActivity(mContext, tspNotificationBean.getData().getGroup().getId());
                            break;
                        case INotificationRepository.GROUP_EXIT:
                            CircleDetailActivityV2.startCircleDetailActivity(mContext, tspNotificationBean.getData().getGroup().getId());
                            break;
                        /*申请问题加精*/
                        case INotificationRepository.QA_EXCELLENT_ACCEPT:
                        case INotificationRepository.QA_EXCELLENT_REJECT:
                            QuestionDetailActivity.startQuestionDetailActivity(mContext, tspNotificationBean.getData().getQa().getQuestion_id());
                            break;
                        case INotificationRepository.GROUP_REPORT_POST:
                        case INotificationRepository.REPORT_GROUPS_POST_COMMENT:
                            CirclePostDetailActivity.startCirclePostDetailActivity(mContext, tspNotificationBean.getData().getGroup().getId(), tspNotificationBean.getData().getPost().getId());
                            break;
                        case INotificationRepository.DELETE_FEED_COMMENT:
                            // 动态评论被删
                            DynamicDetailActivity.startDynamicDetailActivity(mContext, tspNotificationBean.getData().getFeed().getId());
                            break;
                        default:
                    }
                });
    }
}

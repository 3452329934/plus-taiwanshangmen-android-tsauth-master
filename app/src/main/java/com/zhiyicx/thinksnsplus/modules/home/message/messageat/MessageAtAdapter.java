package com.zhiyicx.thinksnsplus.modules.home.message.messageat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkMetadata;
import com.klinker.android.link_builder.NetUrlHandleBean;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.imageloader.glide.GlideImageConfig;
import com.zhiyicx.baseproject.widget.textview.SpanTextViewWithEllipsize;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.TextViewUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.utils.imageloader.core.ImageLoader;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.SimpleMusic;
import com.zhiyicx.thinksnsplus.data.beans.notify.AtMeaasgeBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailActivity;
import com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.music_fm.music_comment.MusicCommentActivity;
import com.zhiyicx.thinksnsplus.modules.music_fm.music_comment.MusicCommentFragment;
import com.zhiyicx.thinksnsplus.modules.music_fm.music_comment.MusicCommentHeader;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.answer.AnswerDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.modules.music_fm.music_comment.MusicCommentFragment.CURRENT_COMMENT;
import static com.zhiyicx.thinksnsplus.modules.music_fm.music_comment.MusicCommentFragment.CURRENT_COMMENT_TYPE;
import static com.zhiyicx.thinksnsplus.modules.music_fm.music_comment.MusicCommentFragment.CURRENT_COMMENT_TYPE_MUSIC;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/4/13
 * @Contact master.jungle68@gmail.com
 */

public class MessageAtAdapter extends CommonAdapter<AtMeaasgeBean> {
    public static final String BUNDLE_SOURCE_ID = "source_id";

    private ImageLoader mImageLoader;

    public MessageAtAdapter(Context context, int layoutId, List<AtMeaasgeBean> datas) {
        super(context, layoutId, datas);
        mImageLoader = AppApplication.AppComponentHolder.getAppComponent().imageLoader();
    }

    @Override
    protected void convert(final ViewHolder holder, final AtMeaasgeBean commentedBean, final int position) {

        ImageUtils.loadCircleUserHeadPic(commentedBean.getUserInfoBean(), holder.getView(R.id.iv_headpic));

        holder.setVisible(R.id.tv_reply, View.VISIBLE);
        holder.setVisible(R.id.tv_review, View.GONE);

        boolean hasImage = !TextUtils.isEmpty(commentedBean.getBody_image()) || commentedBean.isHasVideo();
        holder.setVisible(R.id.fl_image_container, hasImage ? View.VISIBLE : View.GONE);

        if (!TextUtils.isEmpty(commentedBean.getBody_image())) {
            mImageLoader.loadImage(getContext(), GlideImageConfig.builder()
                    .url(commentedBean.getBody_image())
                    .imagerView(holder.getView(R.id.iv_detail_image))
                    .build());

        }

        if (commentedBean.isHasVideo()) {
            holder.setVisible(R.id.iv_video_icon, View.VISIBLE);
        } else {
            holder.setVisible(R.id.iv_video_icon, View.GONE);
        }

        holder.getView(R.id.fl_detial).setVisibility(View.VISIBLE);
        holder.setText(R.id.tv_name, commentedBean.getUserInfoBean().getName());
        TextView contentView = holder.getView(R.id.tv_deatil);
        TextView extraContentView = holder.getView(R.id.tv_extra_deatil);
        contentView.setText(commentedBean.getBody_content());
        contentView.setMaxLines(TextUtils.isEmpty(commentedBean.getExtra_content()) ? 2 : 1);
        extraContentView.setVisibility(TextUtils.isEmpty(commentedBean.getExtra_content()) ? View.GONE : View.VISIBLE);
        extraContentView.setText(commentedBean.getExtra_content());

        SpanTextViewWithEllipsize spanTextViewWithEllipsize = holder.getView(R.id.tv_content);
        spanTextViewWithEllipsize.setNeedLookMore(true);
        spanTextViewWithEllipsize.setMaxLines(3);
        TextViewUtils.addLinkMovementMethod(spanTextViewWithEllipsize);
        TextViewUtils.dealTextViewClickEvent(spanTextViewWithEllipsize);
        spanTextViewWithEllipsize.setOnToucheSpanClickListener(v -> {
            spanTextViewWithEllipsize.setNeedLookMore(false);
            spanTextViewWithEllipsize.setMaxLines(100);
            spanTextViewWithEllipsize.setText(commentedBean.getTitle());
            List<Link> links = new ArrayList<>();
            links.add(getAtLink());
            ConvertUtils.stringLinkConvert(spanTextViewWithEllipsize, links, false);
        });
        spanTextViewWithEllipsize.setText(commentedBean.getTitle());
        spanTextViewWithEllipsize.setVisibility(TextUtils.isEmpty(commentedBean.getTitle()) ? View.GONE : View.VISIBLE);
        spanTextViewWithEllipsize.post(() -> spanTextViewWithEllipsize.updateForRecyclerView(spanTextViewWithEllipsize.getText(), spanTextViewWithEllipsize.getWidth()));
        List<Link> links = setLiknks(commentedBean.getBody_content(), commentedBean.getContent());
        if (!links.isEmpty()) {
            ConvertUtils.stringLinkConvert(spanTextViewWithEllipsize, links, false);
            ConvertUtils.stringLinkConvert(holder.getView(R.id.tv_deatil), links, false);
        }


        holder.setText(R.id.tv_time, TimeUtils.getTimeFriendlyNormal(commentedBean.getCreated_at()));
        // 响应事件
        RxView.clicks(holder.getView(R.id.tv_name))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> toUserCenter(commentedBean.getUserInfoBean()));
        RxView.clicks(holder.getView(R.id.iv_headpic))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> toUserCenter(commentedBean.getUserInfoBean()));

        RxView.clicks(holder.getView(R.id.fl_detial))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> toDetail(commentedBean));
        RxView.clicks(holder.getView(R.id.tv_deatil))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> toDetail(commentedBean));
        RxView.clicks(spanTextViewWithEllipsize)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(holder.getConvertView(), holder, position);
                    }
                });

        RxView.clicks(holder.getView(R.id.tv_reply))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(holder.getConvertView(), holder, position);
                    }
                });
    }

    /**
     * 前往用户个人中心
     */
    private void toUserCenter(UserInfoBean userInfoBean) {
        PersonalCenterFragment.startToPersonalCenter(mContext, userInfoBean);
    }


    /**
     * 根据不同的type 进入不同的 详情
     *
     * @param commentedBean
     */
    private void toDetail(AtMeaasgeBean commentedBean) {
        if (commentedBean.getResourceable().isDeleted()) {
            return;
        }
        Long id = commentedBean.getResourceable().getId();
        switch (commentedBean.getResourceable().getType()) {
            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC:
                DynamicDetailActivity.startDynamicDetailActivity(mContext, id);
                break;
            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO:
                InfoDetailsActivity.startInfoDetailsActivity(mContext, id);
                break;
            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE:
                CircleDetailActivity.startCircleDetailActivity(mContext, id);
                break;
            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST:
                CirclePostDetailActivity.startCirclePostDetailActivity(mContext,
                        commentedBean.getParent_id(), id);
                break;
            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION:
                QuestionDetailActivity.startQuestionDetailActivity(mContext, id);
                break;
            case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS:
                AnswerDetailsActivity.startAnswerDetailsActivity(mContext, id);
                break;
            case MessageAtFragment.MUSIC:
                Intent intent = new Intent(mContext, MusicCommentActivity.class);
               Bundle bundle = new Bundle();
               bundle.putString(CURRENT_COMMENT_TYPE, CURRENT_COMMENT_TYPE_MUSIC);
                MusicCommentHeader.HeaderInfo musicHeaderInfo = new MusicCommentHeader.HeaderInfo();
                try {
                    musicHeaderInfo.setId(id.intValue());
                    musicHeaderInfo.setTitle(commentedBean.getContent());
                    bundle.putSerializable(CURRENT_COMMENT, musicHeaderInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtra(CURRENT_COMMENT, bundle);
                mContext.startActivity(intent);
                break;
            case MessageAtFragment.MUSIC_SPECIAL:
                break;
            default:
        }
    }

    /**
     * 网页链接
     *
     * @param content
     * @return
     */
    protected List<Link> setLiknks(String friedlyContent, String content) {
        List<Link> links = new ArrayList<>();
        if (!TextUtils.isEmpty(friedlyContent) && friedlyContent.contains(Link.DEFAULT_NET_SITE)) {
            Link commentNameLink = new Link(Link.DEFAULT_NET_SITE)
                    .setTextColor(ContextCompat.getColor(mContext, R.color
                            .themeColor))
                    .setLinkMetadata(LinkMetadata.builder()
                            .putSerializableObj(LinkMetadata.METADATA_KEY_COTENT, new NetUrlHandleBean(content))
                            .putSerializableObj(LinkMetadata.METADATA_KEY_TYPE, LinkMetadata.SpanType.NET_SITE)
                            .build())
                    .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color
                            .general_for_hint))
                    .setHighlightAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                    .setOnClickListener((clickedText, linkMetadata) -> CustomWEBActivity.startToWEBActivity(mContext,clickedText))
                    .setOnLongClickListener((clickedText, linkMetadata) -> {

                    })
                    .setUnderlined(false);
            links.add(commentNameLink);
        }

        links.add(getAtLink());

        return links;
    }

    protected Link getAtLink() {
        return new Link(Pattern.compile(MarkdownConfig.AT_FORMAT))
                .setTextColor(ContextCompat.getColor(mContext, R.color.important_for_theme))
                .setUnderlined(false)
                .setOnClickListener((clickedText, linkMetadata) -> toUserCenter(new UserInfoBean(RegexUtils.replaceAllAt(clickedText))))
                .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color.important_for_content));
    }
}

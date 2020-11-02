package com.zhiyicx.thinksnsplus.modules.dynamic.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkMetadata;
import com.klinker.android.link_builder.NetUrlHandleBean;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.Toll;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.baseproject.widget.textview.SpanTextViewWithEllipsize;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.FastBlur;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.SkinUtils;
import com.zhiyicx.common.utils.TextViewUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDigListBean;
import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsCountBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.pre.PreCircleActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.dig_list.DigListActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.dig_list.DigListFragment;
import com.zhiyicx.thinksnsplus.modules.gallery.GalleryActivity;
import com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.answer.AnswerDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.ZhiyiVideoView;
import com.zhiyicx.thinksnsplus.modules.wallet.reward.RewardType;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.widget.DynamicHorizontalStackIconView;
import com.zhiyicx.thinksnsplus.widget.ReWardView;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicListTopicView;
import com.zhiyicx.thinksnsplus.widget.popwindow.LetterPopWindow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;

import static android.support.v4.view.ViewCompat.LAYER_TYPE_NONE;
import static cn.jzvd.JZVideoPlayer.URL_KEY_DEFAULT;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Describe 动态详情头部信息
 * @Author Jungle68
 * @Date 2017/3/10
 * @Contact master.jungle68@gmail.com
 */

public class DynamicDetailHeader {
    private static final int DEFAULT_PART_TOTAL = 100;

    private LinearLayout mPhotoContainer;
    private TextView mContent;
    private TextView mTitle;
    private View mDynamicDetailHeader;
    private FrameLayout mFlcommentcountcontainer;
    private FrameLayout mFlForwardContainer;
    private ReWardView mReWardView;
    private DynamicListTopicView mDynamicListTopicView;
    private Context mContext;
    private int screenWidth;
    private int screenHeight;
    private int picWidth;
    private Bitmap sharBitmap;
    private OnClickLisenter mOnClickLisenter;
    private TextViewUtils.OnSpanTextClickListener mOnSpanTextClickListener;

    /**
     * Gif 是否直接播放
     */
    private boolean mIsGifPlay = false;
    private boolean isListToDetail = false;
    private List<View> mGifViews;

    View getDynamicDetailHeader() {
        return mDynamicDetailHeader;
    }

    DynamicDetailHeader(Context context, List<RealAdvertListBean> adverts) {
        this.mContext = context;
        mGifViews = new ArrayList<>();
        mDynamicDetailHeader = LayoutInflater.from(context).inflate(R.layout
                .view_header_dynamic_detial, null);
        mDynamicDetailHeader.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout
                .LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        mTitle = mDynamicDetailHeader.findViewById(R.id.tv_dynamic_title);
        mContent = mDynamicDetailHeader.findViewById(R.id.tv_dynamic_content);
        initAdvert(context, adverts);
        mFlcommentcountcontainer = mDynamicDetailHeader.findViewById(R.id
                .fl_comment_count_container);
        mFlForwardContainer = mDynamicDetailHeader.findViewById(R.id
                .fl_forward_container);
        mPhotoContainer = mDynamicDetailHeader.findViewById(R.id
                .ll_dynamic_photos_container);
        picWidth = UIUtils.getWindowWidth(context);
        screenWidth = UIUtils.getWindowWidth(context);
        screenHeight = UIUtils.getWindowHeight(context) - DeviceUtils.getStatuBarHeight(mContext)
                - mContext.getResources().getDimensionPixelOffset(com.zhiyicx.baseproject.R.dimen
                .toolbar_height)
                - mContent.getResources().getDimensionPixelOffset(com.zhiyicx.baseproject.R.dimen
                .divider_line);
//        picWidth = UIUtils.getWindowWidth(context) - context.getResources().getDimensionPixelSize
//                (R.dimen.spacing_normal) * 2;
        picWidth = screenWidth;
        mReWardView = mDynamicDetailHeader.findViewById(R.id.v_reward);
        mDynamicListTopicView = mDynamicDetailHeader.findViewById(R.id.dltv_topic);
    }

    private void initAdvert(Context context, List<RealAdvertListBean> adverts) {
        DynamicDetailAdvertHeader dynamicDetailAdvertHeader = new DynamicDetailAdvertHeader
                (context, mDynamicDetailHeader
                        .findViewById(R.id.ll_advert));
        if (!com.zhiyicx.common.BuildConfig.USE_ADVERT || adverts.isEmpty()) {
            dynamicDetailAdvertHeader.hideAdvert();
            return;
        }
        dynamicDetailAdvertHeader.setTitle(context.getString(R.string.advert));
        dynamicDetailAdvertHeader.setAdverts(adverts);
        dynamicDetailAdvertHeader.setOnItemClickListener((v, position1, url) ->
                toAdvert(adverts.get(position1).getAdvertFormat().getImage().getLink(), adverts
                        .get(position1).getTitle())
        );
    }

    private void toAdvert(String link, String title) {
        CustomWEBActivity.startToWEBActivity(mContext, link, title);
    }

    /**
     * 设置头部动态信息
     *
     * @param dynamicBean
     */
    public void setDynamicDetial(DynamicDetailBeanV2 dynamicBean, int state, ZhiyiVideoView
            .ShareInterface shareInterface) {

        setForwardData(dynamicBean);


        mDynamicListTopicView.setData(dynamicBean);
        String titleText = "";
        if (TextUtils.isEmpty(titleText)) {
            mTitle.setVisibility(View.GONE);
        } else {
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(titleText);
        }
        String contentText = dynamicBean.getFeed_content();
        if (TextUtils.isEmpty(contentText)) {
            mContent.setVisibility(View.GONE);
        } else {
//            dealTollWords(dynamicBean, contentText);// 处理文字收费
            mContent.setVisibility(View.VISIBLE);
            dealLinkWords(dynamicBean, contentText, mContent);
        }

        final Context context = mTitle.getContext();
        // 设置图片
        List<DynamicDetailBeanV2.ImagesBean> photoList = dynamicBean.getImages();
        boolean hasImage = photoList != null && !photoList.isEmpty();
        boolean hasVideo = dynamicBean.getVideo() != null;
        if (!hasImage && !hasVideo) {
            mPhotoContainer.setVisibility(View.GONE);
        } else {
            mPhotoContainer.setVisibility(View.VISIBLE);
            DynamicDetailBeanV2.Video video = dynamicBean.getVideo();
            if (hasVideo) {
                ZhiyiVideoView videoView = new ZhiyiVideoView(mContext);
                videoView.setShareInterface(shareInterface);
                mPhotoContainer.addView(videoView);

                int width = picWidth;
                int height = (video.getHeight() * picWidth / video.getWidth());
                if (height > screenHeight) {
                    height = screenHeight;
                    width = (screenHeight / video.getHeight()) * video.getWidth();
                }
                videoView.getLayoutParams().height = height;
                videoView.getLayoutParams().width = width;

                String videoUrl = String.format(ApiConfig.APP_DOMAIN + ApiConfig.FILE_PATH,
                        dynamicBean.getVideo().getVideo_id());


                LogUtils.d(JZMediaManager.getCurrentDataSource());
                if (JZVideoPlayerManager.getFirstFloor() != null
                        && !JZVideoPlayerManager.getCurrentJzvd().equals(videoView)) {

                    LinkedHashMap<String, Object> map = (LinkedHashMap) JZVideoPlayerManager.getFirstFloor().dataSourceObjects[0];
                    if (map != null) {
                        isListToDetail = videoUrl.equals(map.get(URL_KEY_DEFAULT).toString());
                    }

                    if (isListToDetail) {
                        videoView.setUp(videoUrl, JZVideoPlayerStandard.SCREEN_WINDOW_LIST);
                        videoView.positionInList = 0;

                        videoView.setState(state);
                        videoView.positionInList = JZVideoPlayerManager.getFirstFloor().positionInList;
                        videoView.addTextureView();
                        if (JZVideoPlayerManager.getFirstFloor() instanceof ZhiyiVideoView) {
                            ZhiyiVideoView firstFloor = (ZhiyiVideoView) JZVideoPlayerManager.getFirstFloor();
                            videoView.mVideoFrom = firstFloor.mVideoFrom;
                        }
                        JZVideoPlayerManager.setFirstFloor(videoView);
                        videoView.startProgressTimer();
                        if (state == ZhiyiVideoView.CURRENT_STATE_PAUSE) {
                            videoView.startButton.callOnClick();
                        }
                    } else {
                        videoView.setUp(videoUrl, JZVideoPlayerStandard.SCREEN_WINDOW_LIST);
                        videoView.positionInList = 0;
                    }

                } else {
                    videoView.setUp(videoUrl, JZVideoPlayerStandard.SCREEN_WINDOW_LIST);
                    videoView.positionInList = 0;
                }

                Glide.with(mContext)
                        .load(video.getGlideUrl())
                        .placeholder(R.drawable.shape_default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.shape_default_image)
                        .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, GlideUrl model,
                                                       Target<GlideDrawable> target, boolean
                                                               isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, GlideUrl
                                    model, Target<GlideDrawable> target, boolean
                                                                   isFromMemoryCache, boolean
                                                                   isFirstResource) {
                                sharBitmap = ConvertUtils.drawable2BitmapWithWhiteBg(mContext, resource, R.mipmap.icon);
                                Bitmap bitmap = FastBlur.blurBitmapForShortVideo(ConvertUtils.drawable2Bitmap(resource), resource
                                        .getIntrinsicWidth(), resource
                                        .getIntrinsicHeight());
                                videoView.setBackground(new BitmapDrawable(mContext.getResources
                                        (), bitmap));
                                return false;
                            }
                        })
                        .into(videoView.thumbImageView);


            } else {
                mGifViews.clear();
                for (int i = 0; i < photoList.size(); i++) {
                    showContentImage(context, photoList, i, i == photoList.size() - 1, mPhotoContainer);
                }
                setImageClickListener(photoList, dynamicBean);
            }

        }
    }

    private void setForwardData(DynamicDetailBeanV2 dynamicBean) {
        if (dynamicBean.getRepostable_id() != null && dynamicBean.getMLetter() != null) {
            int forwardContentId;
            String content;
            String name;
            String image;

            name = dynamicBean.getMLetter().getName();
            content = dynamicBean.getMLetter().getContent();
            image = dynamicBean.getMLetter().getImage();

            boolean isImage = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE.equals(dynamicBean.getMLetter()
                    .getDynamic_type());
            boolean isWord = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD.equals(dynamicBean.getMLetter()
                    .getDynamic_type());
            boolean isCircle = false;
            boolean isPost = false;
            switch (dynamicBean.getMLetter().getType()) {
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC:
                    String sufix = "：";
                    name = dynamicBean.getMLetter().getName();
                    name += TextUtils.isEmpty(name) ? "" : sufix;
                    if (!isWord) {
                        // 图片、视频
                        forwardContentId = R.layout.detail_forward_for_media_feed;
                        content = dynamicBean.getMLetter().getContent();
                    } else {
                        forwardContentId = R.layout.detail_forward_for_word_feed;
                        content = name + dynamicBean.getMLetter().getContent();
                    }
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO:
                    forwardContentId = R.layout.forward_for_info;
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE:
                    forwardContentId = R.layout.forward_for_circle;
                    isCircle = true;
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST:
                    forwardContentId = R.layout.detail_forward_for_post;
                    isPost = true;
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION:
                    forwardContentId = R.layout.forward_for_question;
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS:
                    forwardContentId = R.layout.forward_for_answer;
                    break;
                default:
                    forwardContentId = R.layout.detail_forward_for_word_feed;
            }


            mFlForwardContainer.setVisibility(View.VISIBLE);
            mFlForwardContainer.removeAllViews();
            mFlForwardContainer.addView(LayoutInflater.from(mContext).inflate(forwardContentId, null));


            try {
                // 这里面的代码顺序不能改动，影响try catch
                if (isCircle) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFlForwardContainer.getLayoutParams();
                    int margin = mContext.getResources().getDimensionPixelOffset(R.dimen.spacing_normal);

                    params.setMargins(margin, margin, margin, margin);
                    mFlForwardContainer.setBackgroundResource(R.color.general_for_bg_light_f7);
                    mFlForwardContainer.setPadding(0, 0, 0, 0);
                    mContent.setPadding(mContent.getPaddingLeft(), mContent.getPaddingTop(), mContent.getPaddingRight(), margin);
                    mFlForwardContainer.findViewById(R.id.rl_forward_circle).setBackgroundResource(R.color.white);
                }
                if (isPost) {
                    mFlForwardContainer.findViewById(R.id.ll_forward_container).setPadding(ConvertUtils.dp2px(mContext, 15),
                            ConvertUtils.dp2px(mContext, 10), ConvertUtils.dp2px(mContext, 15), ConvertUtils.dp2px(mContext, 15));
                }

                RxView.clicks(mFlForwardContainer)
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> {
                            if (dynamicBean.getMLetter().isDeleted()) {
                                return;
                            }
                            Long id = dynamicBean.getRepostable_id();
                            switch (dynamicBean.getRepostable_type()) {
                                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC:
                                    DynamicDetailActivity.startDynamicDetailActivity(mContext, id);
                                    break;
                                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO:
                                    InfoDetailsActivity.startInfoDetailsActivity(mContext, id);
                                    break;
                                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE:
                                    if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_POST_CIRCLE_TYPE.equals(dynamicBean.getMLetter().getCircle_type())) {
                                        PreCircleActivity.startPreCircleActivity(mContext, Long.parseLong(dynamicBean.getMLetter().getId()));
                                    } else {
                                        CircleDetailActivity.startCircleDetailActivity(mContext, Long.parseLong(dynamicBean.getMLetter().getId()));
                                    }
                                    break;
                                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST:
                                    boolean isPre = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_POST_CIRCLE_TYPE.equals(dynamicBean.getMLetter().getCircle_type());
                                    CirclePostDetailActivity.startCirclePostDetailActivity(mContext,
                                            Long.parseLong(dynamicBean.getMLetter().getCircle_id()), id, isPre);
                                    break;
                                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION:
                                    QuestionDetailActivity.startQuestionDetailActivity(mContext, id);
                                    break;
                                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS:
                                    AnswerDetailsActivity.startAnswerDetailsActivity(mContext, id);
                                    break;
                                default:
                            }
                        });

                TextView forwardContentView = mFlForwardContainer.findViewById(R.id.tv_forward_content);
                RxView.clicks(forwardContentView)
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> mFlForwardContainer.performClick());
                if (forwardContentView instanceof SpanTextViewWithEllipsize) {
                    ((SpanTextViewWithEllipsize) forwardContentView).setTotalWidth(0.91f);
                    forwardContentView.post(() -> ((SpanTextViewWithEllipsize) forwardContentView)
                            .updateForRecyclerView(forwardContentView.getText(), forwardContentView.getWidth()));
                }

                if (!isWord && TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(dynamicBean.getRepostable_type())) {
                    forwardContentView.setCompoundDrawablesWithIntrinsicBounds(isImage ?
                                    R.mipmap.ico_pic_highlight : R.mipmap.ico_video_highlight
                            , 0, 0, 0);
                    content = isImage ? LetterPopWindow.PIC : LetterPopWindow.VIDEO;
                }
                dealLinkWords(dynamicBean, content, forwardContentView);

                TextView forwardTitleView = mFlForwardContainer.findViewById(R.id.tv_forward_name);
                RxView.clicks(forwardTitleView)
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> mFlForwardContainer.performClick());
                forwardTitleView.setText(name);

                ImageView imageView = mFlForwardContainer.findViewById(R.id.iv_forward_image);
                if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST.equals(dynamicBean.getRepostable_type())) {
                    if (imageView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                        ((LinearLayout.LayoutParams) imageView.getLayoutParams()).leftMargin = 0;
                    }
                    imageView.getLayoutParams().width = mContent.getResources().getDimensionPixelOffset(R.dimen.detail_forward_post_image_width);
                    imageView.getLayoutParams().height = mContent.getResources().getDimensionPixelOffset(R.dimen.detail_forward_post_image_height);
                }
                imageView.setVisibility(TextUtils.isEmpty(image) ? View.GONE : View.VISIBLE);
                if (!TextUtils.isEmpty(image)) {
                    Glide.with(mContext)
                            .load(image)
                            .placeholder(R.drawable.shape_default_image)
                            .error(R.drawable.shape_default_image)
                            .into(imageView);
                }

            } catch (Exception ignore) {
                LogUtils.d(ignore);
            }

        }
    }

    /**
     * 文字网页链接点击处理
     *
     * @param dynamicBean
     * @param content
     */
    private void dealLinkWords(DynamicDetailBeanV2 dynamicBean, String content, TextView textView) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        content = content.replaceAll(MarkdownConfig.NETSITE_FORMAT, Link.DEFAULT_NET_SITE);
        textView.setText(content);
        ConvertUtils.stringLinkConvert(textView, setLiknks(dynamicBean, textView.getText()
                .toString()), false);
    }

    /**
     * 处理详情文字收费
     *
     * @param dynamicBean
     * @param contentText
     */
    private void dealTollWords(DynamicDetailBeanV2 dynamicBean, String contentText) {
        if (contentText.length() == 50 && dynamicBean.getPaid_node() != null && !dynamicBean
                .getPaid_node().isPaid()) {
            contentText += mContext.getString(R.string.words_holder);
        }

        TextViewUtils textViewUtils = TextViewUtils.newInstance(mContent, contentText)
                .spanTextColor(SkinUtils.getColor(R
                        .color.normal_for_assist_text))
                .position(50, contentText.length())
                .maxLines(mContext.getResources().getInteger(R.integer
                        .dynamic_list_content_show_lines))
                .onSpanTextClickListener(mOnSpanTextClickListener)
                .disPlayText(true);

        if (dynamicBean.getPaid_node() != null) {// 有文字收费
            textViewUtils.note(dynamicBean.getPaid_node().getNode())
                    .amount(dynamicBean.getPaid_node().getAmount())
                    .disPlayText(dynamicBean.getPaid_node().isPaid());
        }
        textViewUtils.build();
    }

    Bitmap getSharBitmap() {
        ImageView imageView;
        try {
            if (mPhotoContainer.getChildAt(0) instanceof ZhiyiVideoView) {
                ZhiyiVideoView videoView = (ZhiyiVideoView) mPhotoContainer.getChildAt(0);
                imageView = videoView.thumbImageView;
            } else {
                imageView = mPhotoContainer.getChildAt(0).findViewById
                        (R.id.dynamic_content_img);
            }
            if (imageView != null) {
                sharBitmap = ConvertUtils.drawable2BitmapWithWhiteBg(mContext, imageView
                        .getDrawable(), R.mipmap.icon);
            }
        } catch (Exception e) {

        }
        if (sharBitmap == null) {
            sharBitmap = ConvertUtils.drawBg4Bitmap(ContextCompat.getColor(mContext, R.color.white), BitmapFactory.decodeResource(mContent
                    .getResources(), R.mipmap.icon).copy(Bitmap.Config.RGB_565, true));
        }
        return sharBitmap;
    }

    /**
     * 更新动态的图片加载
     *
     * @param dynamicBean
     */
    void updateImage(DynamicDetailBeanV2 dynamicBean) {
        if (dynamicBean.getVideo() != null) {
            return;
        }
        List<DynamicDetailBeanV2.ImagesBean> photoList = dynamicBean.getImages();
        mPhotoContainer.removeAllViews();
        for (int i = 0; i < photoList.size(); i++) {
            showContentImage(mContext, photoList, i, i == photoList.size() - 1, mPhotoContainer);
        }

        setImageClickListener(photoList, dynamicBean);
    }

    /**
     * 更新喜欢的人
     *
     * @param dynamicBean
     */
    void updateHeaderViewData(final DynamicDetailBeanV2 dynamicBean) {

        DynamicHorizontalStackIconView dynamicHorizontalStackIconView =
                mDynamicDetailHeader.findViewById(R.id
                        .detail_dig_view);

        dynamicHorizontalStackIconView.setDigCount(dynamicBean.getFeed_digg_count());
        dynamicHorizontalStackIconView.setPublishTime(dynamicBean.getCreated_at());
        dynamicHorizontalStackIconView.setViewerCount(dynamicBean.getFeed_view_count());
        // 设置点赞头像
        List<DynamicDigListBean> userInfoList = dynamicBean.getDigUserInfoList();
        dynamicHorizontalStackIconView.setDigUserHeadIcon(userInfoList);

        // 设置跳转到点赞列表
        dynamicHorizontalStackIconView.setDigContainerClickListener(digContainer -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(DigListFragment.DIG_LIST_DATA, dynamicBean);
            Intent intent = new Intent(mDynamicDetailHeader.getContext(), DigListActivity
                    .class);
            intent.putExtras(bundle);
            mDynamicDetailHeader.getContext().startActivity(intent);
        });
        if (dynamicBean.getFeed_comment_count() <= 0) {
            mFlcommentcountcontainer.setVisibility(View.GONE);
        } else {
            ((TextView) mDynamicDetailHeader.findViewById(R.id.tv_comment_count)).setText
                    (mDynamicDetailHeader.getResources().getString(R.string
                            .dynamic_comment_count, ConvertUtils.numberConvert(dynamicBean
                            .getFeed_comment_count())));
            mFlcommentcountcontainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新打赏内容
     *
     * @param sourceId         source id  for this reward
     * @param data             reward's users
     * @param rewardsCountBean all reward data
     * @param rewardType       reward type
     */
    public void updateReward(long sourceId, List<RewardsListBean> data, RewardsCountBean
            rewardsCountBean,
                             RewardType rewardType, String moneyName) {
        mReWardView.initData(sourceId, data, rewardsCountBean, rewardType, moneyName);
    }

    /**
     * 显示内容中的 图片
     *
     * @param context
     * @param photoList
     * @param position
     * @param lastImg
     * @param photoContainer
     */
    private void showContentImage(Context context, List<DynamicDetailBeanV2.ImagesBean>
            photoList, final int position,
                                  boolean lastImg, LinearLayout photoContainer) {
        DynamicDetailBeanV2.ImagesBean imageBean = photoList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.view_dynamic_detail_photos, null);
        FilterImageView imageView = view.findViewById(R.id.dynamic_content_img);
        imageView.setLayerType(LAYER_TYPE_NONE, null);
        // 隐藏最后一张图的下间距
        if (lastImg) {
            view.findViewById(R.id.img_divider).setVisibility(View.GONE);
        }

        int height = (imageBean.getHeight() * picWidth / imageBean.getWidth());
        // 提前设置图片控件的大小，使得占位图显示
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(picWidth, height);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setLayoutParams(layoutParams);

        if (TextUtils.isEmpty(imageBean.getImgUrl())) {
            int part = (picWidth / imageBean.getWidth()) * DEFAULT_PART_TOTAL;
            if (part > DEFAULT_PART_TOTAL) {
                part = DEFAULT_PART_TOTAL;
            }
            boolean canLook = !(imageBean.isPaid() != null && !imageBean.isPaid()
                    && imageBean.getType().equals(Toll.LOOK_TOLL_TYPE));
            boolean isGif = ImageUtils.imageIsGif(imageBean.getImgMimeType());
            boolean isImageOutOfBounds = ImageUtils.isWithOrHeightOutOfBounds(picWidth, height);
            boolean isLongImage = ImageUtils.isLongImage(height, picWidth);
            boolean isNeedOrigin = isGif || isImageOutOfBounds || isLongImage;

            imageView.setIshowGifTag(isGif);
            if (isGif) {
                mGifViews.add(imageView);
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if (ImageUtils.imageIsGif(imageBean.getImgMimeType()) && mIsGifPlay) {
                GifRequestBuilder requestBuilder = Glide.with(mContext)
                        .load(ImageUtils.imagePathConvertV2(canLook, imageBean.getFile(), canLook
                                        ? picWidth : 0,
                                canLook ? height : 0, isNeedOrigin ? ImageZipConfig.IMAGE_100_ZIP : part, AppApplication.getTOKEN()))
                        .asGif()
                        .placeholder(R.drawable.shape_default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.shape_default_image);
                if (!canLook) {// 切换展位图防止闪屏
                    requestBuilder.placeholder(R.drawable.shape_default_image);
                }
                requestBuilder.into(imageView);
            } else {
                DrawableRequestBuilder<GlideUrl> requestBuilder = Glide.with(mContext)
                        .load(ImageUtils.imagePathConvertV2(canLook, imageBean.getFile(), canLook
                                        ? picWidth : 0,
                                canLook ? height : 0, isNeedOrigin ? ImageZipConfig.IMAGE_100_ZIP : part, AppApplication.getTOKEN()))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .dontAnimate()
                        .placeholder(R.drawable.shape_default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.shape_default_error_image);
                if (!canLook) {
                    // 切换展位图防止闪屏
                    requestBuilder.placeholder(R.drawable.shape_default_image);
                }
                requestBuilder.into(new GlideDrawableImageViewTarget(imageView, 0));
            }
        } else {
            Glide.with(mContext)
                    .load(imageBean.getImgUrl())
                    .override(picWidth, height)
                    .placeholder(R.drawable.shape_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.shape_default_image)
                    .into(imageView);
        }
        photoContainer.addView(view);
    }

    /**
     * 设置图片点击事件
     */
    private void setImageClickListener(final List<DynamicDetailBeanV2.ImagesBean> photoList,
                                       final DynamicDetailBeanV2 dynamicBean) {
        final ArrayList<AnimationRectBean> animationRectBeanArrayList
                = new ArrayList<>();
        final List<ImageBean> imageBeans = new ArrayList<>();
        for (int i = 0; i < mPhotoContainer.getChildCount(); i++) {
            final View photoView = mPhotoContainer.getChildAt(i);
            int imagePosition = i;
            ImageView imageView = photoView.findViewById(R.id.dynamic_content_img);
            imageView.setOnClickListener(v -> {
                animationRectBeanArrayList.clear();
                DynamicDetailBeanV2.ImagesBean img = photoList.get(imagePosition);
                Boolean canLook = !(img.isPaid() != null && !img.isPaid() && img.getType().equals
                        (Toll.LOOK_TOLL_TYPE));
                if (!canLook) {
                    mOnClickLisenter.onImageClick(imagePosition, img.getAmount(), photoList
                            .get(imagePosition).getPaid_node());
                    return;
                }
                imageBeans.clear();
                for (int i1 = 0; i1 < mPhotoContainer.getChildCount(); i1++) {
                    View photoView1 = mPhotoContainer.getChildAt(i1);
                    ImageView imageView1 = photoView1.findViewById(R.id
                            .dynamic_content_img);
                    DynamicDetailBeanV2.ImagesBean task = photoList.get(i1);
                    ImageBean imageBean = new ImageBean();
                    imageBean.setImgUrl(task.getImgUrl());// 本地地址，也许有
                    Toll toll = new Toll(); // 收费信息
                    toll.setPaid(task.isPaid());// 是否已經付費
                    toll.setToll_money(task.getAmount());// 付费金额
                    toll.setToll_type_string(task.getType());// 付费类型
                    toll.setPaid_node(task.getPaid_node());// 付费节点
                    imageBean.setToll(toll);
                    imageBean.setFeed_id(dynamicBean.getId());// 动态id
                    imageBean.setListCacheUrl(task.getGlideUrl());
                    imageBean.setWidth(task.getWidth());// 图片宽高
                    imageBean.setHeight(task.getHeight());
                    imageBean.setStorage_id(task.getFile());// 图片附件id
                    imageBean.setImgMimeType(task.getImgMimeType());
                    imageBeans.add(imageBean);
                    AnimationRectBean rect = AnimationRectBean.buildFromImageView(imageView1);//
                    // 动画矩形
                    animationRectBeanArrayList.add(rect);
                }

                GalleryActivity.startToGallery(mContext, mPhotoContainer.indexOfChild
                        (photoView), imageBeans, animationRectBeanArrayList);
            });
        }
    }

    public OnClickLisenter getOnClickLisenter() {
        return mOnClickLisenter;
    }

    void setOnClickLisenter(OnClickLisenter onClickLisenter) {
        mOnClickLisenter = onClickLisenter;
    }

    void setReWardViewVisible(int visible) {
        mReWardView.setVisibility(visible);
    }

    protected List<Link> setLiknks(final DynamicDetailBeanV2 dynamicDetailBeanV2, String content) {
        List<Link> links = new ArrayList<>();
        if (content.contains(Link.DEFAULT_NET_SITE)) {
            Link commentNameLink = new Link(Link.DEFAULT_NET_SITE)
                    .setTextColor(ContextCompat.getColor(mContext, R.color
                            .themeColor))
                    .setLinkMetadata(LinkMetadata.builder()
                            .putSerializableObj(LinkMetadata.METADATA_KEY_COTENT, new
                                    NetUrlHandleBean(dynamicDetailBeanV2.getFeed_content()))
                            .putSerializableObj(LinkMetadata.METADATA_KEY_TYPE, LinkMetadata
                                    .SpanType.NET_SITE)
                            .build())
                    .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color
                            .general_for_hint))
                    .setHighlightAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                    .setOnClickListener((clickedText, linkMetadata) -> CustomWEBActivity.startToWEBActivity(mContext, clickedText))
                    .setOnLongClickListener((clickedText, linkMetadata) -> {

                    })
                    .setUnderlined(false);
            links.add(commentNameLink);
        }

        Link link = new Link(Pattern.compile(MarkdownConfig.AT_FORMAT))
                .setTextColor(ContextCompat.getColor(mContext, R.color.important_for_theme))
                .setUnderlined(false)
                .setOnClickListener((clickedText, linkMetadata) -> {
                    if (mOnClickLisenter != null) {
                        mOnClickLisenter.onUserClick(new UserInfoBean(RegexUtils.replaceAllAt(clickedText)));
                    }
                })
                .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color.important_for_content));
        links.add(link);

        return links;
    }

    int scrollCommentToTop() {
        return mFlcommentcountcontainer.getTop();
    }

    public ReWardView getReWardView() {
        return mReWardView;
    }

    public List<View> getGifViews() {
        return mGifViews;
    }

    public interface OnClickLisenter {
        void onImageClick(int iamgePosition, long amount, int note);

        void onUserClick(UserInfoBean userInfoBean);
    }

    public boolean isListToDetail() {
        return isListToDetail;
    }
}

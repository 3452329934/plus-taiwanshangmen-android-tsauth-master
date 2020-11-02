package com.zhiyicx.thinksnsplus.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.klinker.android.link_builder.Link;
import com.zhiyicx.baseproject.impl.photoselector.Toll;
import com.zycx.shortvideo.utils.BitmapUtils;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;
import com.zhiyicx.thinksnsplus.modules.gallery.GalleryActivity;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.utils.MarkDownRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.tiagohm.markdownview.MarkdownView;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.zhiyicx.baseproject.config.ApiConfig.API_VERSION_2;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_DOMAIN;
import static com.zhiyicx.common.config.MarkdownConfig.IMAGE_FORMAT;

/**
 * @author Catherine
 * @describe 问题的正文 如果有图的情况下，显示一张图和一排正文 展开全部；没图的情况下 显示三排内容，展开全部。 展开之后，正常图文混排，不可收起。
 * @date 2017/8/16
 * @contact email:648129313@qq.com
 */

public class QuestionDetailContent extends FrameLayout {

    private Context mContext;

    private LinearLayout mLlContentPreview;
    private ImageView mItemInfoImage;
    private ImageView mIvSolid;
    private ExpandableTextView mTvQuestionContent;
    private MarkdownView mMdvQuestionContent;
    private ArrayList<AnimationRectBean> animationRectBeanArrayList;
    private Bitmap mShareBitmap;
    private boolean isEmpty;

    public QuestionDetailContent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public QuestionDetailContent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public QuestionDetailContent(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        animationRectBeanArrayList = new ArrayList<>();
        LayoutInflater.from(context).inflate(R.layout.view_question_detail_content, this);
        mLlContentPreview = (LinearLayout) findViewById(R.id.ll_content_preview);
        mItemInfoImage = (ImageView) findViewById(R.id.item_info_imag);
        mIvSolid = (ImageView) findViewById(R.id.iv_solid);
        mTvQuestionContent = (ExpandableTextView) findViewById(R.id.tv_question_content);
        mMdvQuestionContent = (MarkdownView) findViewById(R.id.mdv_question_content);
    }

    /**
     * 设置正文的内容
     *
     * @param questionDetail bean
     */
    public void setQuestionDetail(QAListInfoBean questionDetail) {
        mTvQuestionContent.setStateShrink();
        String content = questionDetail.getBody();

        List<ImageBean> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(IMAGE_FORMAT);
        Matcher matcher = pattern.matcher(content);

        String sharImage = "";
        while (matcher.find()) {
            String imageMarkDown = matcher.group(0);
            String imageId = matcher.group(1);

            String imgPath = APP_DOMAIN + "api/" + API_VERSION_2 + "/files/" + imageId/* + "?q=80"*/;

            if (TextUtils.isEmpty(sharImage)) {
                sharImage = imgPath;
            }

            String imageTag = imageMarkDown.replaceAll("\\d+", imgPath).replace("@", "");
            // 先手动加上换行吧 =_，=
            imageTag = "    \n" + imageTag + "    \n";
            content = content.replace(imageMarkDown, imageTag);

            // 处理图片列表
            ImageBean imageBean = new ImageBean();
            imageBean.setFeed_id(-1L);// -1,标识来自网页内容
            imageBean.setStorage_id(Integer.parseInt(imageId));
            imageBean.setImgUrl(imgPath);
            list.add(imageBean);
            AnimationRectBean rect = AnimationRectBean.buildFromImageView(mIvSolid);// 动画矩形
            animationRectBeanArrayList.add(rect);
        }

        if (!TextUtils.isEmpty(sharImage)) {
            Observable.just(sharImage)
                    .observeOn(Schedulers.io())
                    .subscribe(url -> {
                        try {
                            File cacheFile = Glide.with(mContext)
                                    .load(url)
                                    .downloadOnly(200, 200)
                                    .get();
                            mShareBitmap = BitmapUtils.getBitmapFromFileDescriptor(cacheFile, 200, 200, true);
                        } catch (Exception e) {
                            LogUtils.d(e.getMessage());
                        }
                    });

        }
        // 暂时隐藏 展开更多，直接是展开状态 2018-7-5 17:15:00， by tym；
        // dealContent(content, list);

        Matcher matcherA = Pattern.compile(MarkdownConfig.FILTER_A_FORMAT).matcher(content);
        while (matcherA.find()) {
            content = matcherA.replaceFirst(matcherA.group(1));
            matcherA = Pattern.compile(MarkdownConfig.FILTER_A_FORMAT).matcher(content);
        }
        dealMDContent(content, list);
    }

    private void dealContent(String content, List<ImageBean> list) {
        String preContent = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, content); // 预览的文字
        preContent = preContent.replaceAll(MarkdownConfig.NORMAL_FORMAT, "");// 去掉所有 非(中文，英文字母和数字)内容
        if (mTvQuestionContent.getCurrState() != ExpandableTextView.STATE_EXPAND) {
            mMdvQuestionContent.setVisibility(GONE);
            mLlContentPreview.setVisibility(VISIBLE);
        }
        mTvQuestionContent.setExpandListener(new ExpandableTextView.OnExpandListener() {
            @Override
            public void onExpand(ExpandableTextView view) {
                // 展开后 隐藏内容，显示图文混排内容
                mLlContentPreview.setVisibility(GONE);
                mMdvQuestionContent.setVisibility(VISIBLE);
            }

            @Override
            public void onShrink(ExpandableTextView view) {

            }
        });

        if (list.size() > 0) {
            // 如果有图片 那么显示封面
            mItemInfoImage.setVisibility(VISIBLE);
            int w = DeviceUtils.getScreenWidth(mContext);
            int h = mContext.getResources().getDimensionPixelOffset(R.dimen.qa_info_iamge_height);
            String url = ImageUtils.imagePathConvertV2(list.get(0).getStorage_id(), w, h, ImageZipConfig.IMAGE_80_ZIP);
            Glide.with(mContext).load(url)
                    .asBitmap()
                    .override(w, h)
                    .placeholder(R.drawable.shape_default_image)
                    .error(R.drawable.shape_default_image)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mItemInfoImage.setImageBitmap(resource);
                            mShareBitmap = resource;
                        }
                    });
            mTvQuestionContent.setMaxLinesOnShrink(1);
            mTvQuestionContent.setText(preContent);
            dealPreContent(mTvQuestionContent);
        } else {
            mItemInfoImage.setVisibility(GONE);
            mTvQuestionContent.setText(preContent);
            // 此时如果正文不超过3排，那么，直接显示markdown内容就行了。
            int lineCount = mTvQuestionContent.getTextLineCount();
            mTvQuestionContent.setVisibility(lineCount < 4 ? GONE : VISIBLE);
            mMdvQuestionContent.setVisibility(lineCount < 4 ? VISIBLE : GONE);
        }
    }

    /**
     * 处理图文混排
     *
     * @param content 正文
     * @param list    图片列表
     */
    private void dealMDContent(String content, List<ImageBean> list) {
        mMdvQuestionContent.addStyleSheet(MarkDownRule.generateStandardStyle());
        mMdvQuestionContent.loadMarkdown(content);
        mMdvQuestionContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CustomWEBActivity.startToWEBActivity(mContext, url);
                return true;
            }
        });
        mMdvQuestionContent.setOnElementListener(new MarkdownView.OnElementListener() {
            @Override
            public void onButtonTap(String s) {

            }

            @Override
            public void onCodeTap(String s, String s1) {

            }

            @Override
            public void onHeadingTap(int i, String s) {

            }

            @Override
            public void onImageTap(String s, int width, int height) {
                LogUtils.d("Cathy", "onImageTap // " + s + " width :" + width + " height : " + height);
                int position = 0;
                if (isEmpty) {
                    // 适配原生 md 格式图片
                    list.clear();
                    animationRectBeanArrayList.clear();
                }
                isEmpty = list.isEmpty();
                if (isEmpty) {
                    ImageBean imageBean = new ImageBean();
                    imageBean.setImgUrl(s);
                    imageBean.setFeed_id(-1L);// -1,标识来自网页内容
                    Toll toll = new Toll();
                    toll.setPaid(true);
                    toll.setToll_money(0);
                    toll.setToll_type_string("");
                    toll.setPaid_node(0);
                    imageBean.setToll(toll);
                    list.add(imageBean);
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getImgUrl().equals(s)) {
                        position = i;
                    }
                }
                GalleryActivity.startToGallery(mContext, position, list, null);
            }

            @Override
            public void onLinkTap(String s, String s1) {
//                CustomWEBActivity.startToOutWEBActivity(mContext, s1);
            }

            @Override
            public void onKeystrokeTap(String s) {

            }

            @Override
            public void onMarkTap(String s) {

            }
        });
    }

    /**
     * 处理只有一排并且完全显示了内容的情况
     */
    private void dealPreContent(TextView textView) {
        // 处理一排的情况
        String content = textView.getText() + "";
        if (!content.contains(mContext.getString(R.string.qa_topic_q_detail_header_open_all))) {
            if (TextUtils.isEmpty(content)) {
                textView.setText(mContext.getString(R.string.qa_topic_q_detail_header_open_all));
            } else if (textView.getLineCount() == 1) {
                textView.append(mContext.getString(R.string.qa_topic_q_detail_header_open_all));
            }
            ConvertUtils.stringLinkConvert(textView, setLinks());
        }
    }

    private List<Link> setLinks() {
        List<Link> links = new ArrayList<>();
        Link followCountLink = new Link(mContext.getString(R.string.qa_topic_q_detail_header_open_all)).setTextColor(ContextCompat.getColor(getContext(), R.color
                .qa_topic_q_detail_header_show_all))
                .setTextColorOfHighlightedLink(ContextCompat.getColor(getContext(), R.color
                        .general_for_hint))
                .setHighlightAlpha(.8f)
                .setUnderlined(false);
        links.add(followCountLink);
        return links;
    }

    public Bitmap getShareBitmap() {
        if (mShareBitmap == null) {
            mShareBitmap = ConvertUtils.drawBg4Bitmap(ContextCompat.getColor(mContext, R.color.white), BitmapFactory.decodeResource(mContext
                    .getResources(), R.mipmap.icon).copy(Bitmap.Config.RGB_565, true));
        }
        return mShareBitmap;
    }

    public MarkdownView getMdvQuestionContent() {
        return mMdvQuestionContent;
    }
}

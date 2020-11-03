package com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkMetadata;
import com.klinker.android.link_builder.NetUrlHandleBean;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideStokeTransform;
import com.zhiyicx.baseproject.widget.DynamicListMenuView;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.baseproject.widget.textview.SpanTextViewWithEllipsize;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.DrawableProvider;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.SkinUtils;
import com.zhiyicx.common.utils.TextViewUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.gif.GifControl;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicListCommentView;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicListTopicView;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicNoPullRecycleView;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.data.beans.DynamicListAdvert.DEFAULT_ADVERT_FROM_TAG;

/**
 * @Describe 动态列表适配器基类
 * requirement document :{@see https://github
 * .com/zhiyicx/thinksns-plus-document/blob/master/document/%E5%8A%A8%E6%80%81%E6%A8%A1%E5%9D%97.md}
 * design document  {@see https://github
 * .com/zhiyicx/thinksns-plus-document/blob/master/document/TS%2B%E8%A7%86%E8%A7%89%E8%A7%84%E8
 * %8C%83%202.0/TS%2B%E8%A7%86%E8%A7%89%E8%A7%84%E8%8C%83%202.0.pdf}
 * @Author Jungle68
 * @Date 2017/1/6
 * @Contact master.jungle68@gmail.com
 */

public class DynamicListBaseItem implements ItemViewDelegate<DynamicDetailBeanV2> {
    protected final String TAG = this.getClass().getSimpleName();
    private static final int CURREN_CLOUMS = 0;
    public static final int DEFALT_IMAGE_HEIGHT = 280;
    public static final int DEFALT_IMAGE_WITH = 360;
    protected final int mHightPixels; // 屏幕高度
    protected final int mDiverwith; // 分割先的宽高
    protected int mImageContainerWith; // 图片容器最大宽度
    protected int mImageMaxHeight; // 单张图片最大高度
    protected Context mContext;

    protected boolean showToolMenu = true;// 是否显示工具栏:默认显示
    protected boolean showTopicTags = true;// 是否显示话题标签:默认显示
    protected boolean showCommentList = true;// 是否显示评论内容:默认显示
    protected boolean showReSendBtn = true;// 是否显示重发按钮
    protected long start;
    protected int mDrawableList;
    protected FilterImageView mFilterImageView;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    /**
     * 图片点击监听
     */
    protected OnImageClickListener mOnImageClickListener;

    public void setOnUserInfoClickListener(OnUserInfoClickListener onUserInfoClickListener) {
        mOnUserInfoClickListener = onUserInfoClickListener;
    }

    /**
     * 用户信息点击监听
     */
    protected OnUserInfoClickListener mOnUserInfoClickListener;

    protected TextViewUtils.OnSpanTextClickListener mOnSpanTextClickListener;

    public void setOnMenuItemClickLisitener(OnMenuItemClickLisitener onMenuItemClickLisitener) {
        mOnMenuItemClickLisitener = onMenuItemClickLisitener;
    }

    /**
     * 工具栏被点击
     */
    protected OnMenuItemClickLisitener mOnMenuItemClickLisitener;


    public void setOnReSendClickListener(OnReSendClickListener onReSendClickListener) {
        mOnReSendClickListener = onReSendClickListener;
    }

    protected OnReSendClickListener mOnReSendClickListener;

    public void setOnCommentClickListener(DynamicListCommentView.OnCommentClickListener
                                                  onCommentClickListener) {
        mOnCommentClickListener = onCommentClickListener;
    }

    protected DynamicListCommentView.OnCommentClickListener mOnCommentClickListener;

    private DynamicListTopicView.OnTopicClickListener mOnTopicClickListener;

    protected DynamicListCommentView.OnMoreCommentClickListener mOnMoreCommentClickListener;

    public void setOnTopicClickListener(DynamicListTopicView.OnTopicClickListener onTopicClickListener) {
        mOnTopicClickListener = onTopicClickListener;
    }

    public void setOnCommentStateClickListener(DynamicNoPullRecycleView.OnCommentStateClickListener<DynamicCommentBean>
                                                       onCommentStateClickListener) {
        mOnCommentStateClickListener = onCommentStateClickListener;
    }

    protected DynamicNoPullRecycleView.OnCommentStateClickListener<DynamicCommentBean> mOnCommentStateClickListener;

    public void setOnMoreCommentClickListener(DynamicListCommentView.OnMoreCommentClickListener
                                                      onMoreCommentClickListener) {
        mOnMoreCommentClickListener = onMoreCommentClickListener;
    }

    public DynamicListBaseItem(Context context) {
        mContext = context;
        mHightPixels = DeviceUtils.getScreenHeight(context);
        int margin = 2 * context.getResources().getDimensionPixelSize(R.dimen
                .dynamic_list_image_marginright);
        mDiverwith = context.getResources().getDimensionPixelSize(R.dimen.spacing_small);
        mImageContainerWith = DeviceUtils.getScreenWidth(context) - margin;
        mImageContainerWith = mImageContainerWith > context.getResources().getDimensionPixelSize(R.dimen
                .dynamic_one_image_max_with) ? context.getResources().getDimensionPixelSize(R.dimen
                .dynamic_one_image_max_with) : mImageContainerWith;
        // 最大高度是最大宽度的4/3 保持 宽高比 3：4
        mImageMaxHeight = mImageContainerWith * 4 / 3;

    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_dynamic_list_zero_image;
    }

    @Override
    public boolean isForViewType(DynamicDetailBeanV2 item, int position) {
        // 当本地和服务器都没有图片的时候，使用
        return item.getFeed_mark() != null && item.getFeed_from() != DEFAULT_ADVERT_FROM_TAG && (item.getImages() != null && item.getImages().size
                () == getImageCounts()) && item.getVideo() == null;
    }

    /**
     * 获取图片数量
     *
     * @return
     */
    protected int getImageCounts() {
        return CURREN_CLOUMS;
    }

    /**
     * @param holder
     * @param dynamicBean
     * @param lastT       android:descendantFocusability
     * @param position
     */
    @Override
    public void convert(ViewHolder holder, DynamicDetailBeanV2 dynamicBean, DynamicDetailBeanV2
            lastT, final int position, int itemCounts) {
        start = System.currentTimeMillis();
        mDrawableList = 0;
        try {
            // 防止个人中心没后头像错误
            try {
                ImageUtils.loadCircleUserHeadPic(dynamicBean.getUserInfoBean(), holder.getView(R.id.iv_headpic));
                setUserInfoClick(holder.getView(R.id.iv_headpic), dynamicBean);

            } catch (Exception ignored) {
            }
            holder.setText(R.id.tv_name, dynamicBean.getUserInfoBean().getName());
            holder.getTextView(R.id.tv_name).setTextSize(18);

            setUserInfoClick(holder.getView(R.id.tv_name), dynamicBean);

            holder.setText(R.id.tv_time, dynamicBean.getFriendlyTime());
            holder.setVisible(R.id.tv_title, View.GONE);
            SpanTextViewWithEllipsize contentView = holder.getView(R.id.tv_content);
            // 置顶标识 ,防止没有置顶布局错误
            try {
                // 待审核 也隐藏
                TextView topFlagView = holder.getView(R.id.tv_top_flag);
                topFlagView.setVisibility(dynamicBean.getTop() == DynamicDetailBeanV2.TOP_SUCCESS ?
                        View.VISIBLE : View.GONE);
                topFlagView.setText(mContext.getString(dynamicBean.getTop() ==
                        DynamicDetailBeanV2.TOP_REVIEW ?
                        R.string.review_ing : R.string.dynamic_top_flag));
            } catch (Exception ignored) {
            }

            String content = dynamicBean.getFriendlyContent();
            boolean hasContent = !TextUtils.isEmpty(content);
            contentView.setVisibility(hasContent ? View.VISIBLE : View.GONE);
            try {
                View iamgeContainer = holder.getView(R.id.nrv_image);
                RelativeLayout.LayoutParams iamgeParam = (RelativeLayout.LayoutParams) iamgeContainer.getLayoutParams();
                int marginTop = hasContent ? 0 : mContext.getResources().getDimensionPixelOffset(R.dimen.spacing_mid_small);
                int margingLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.spacing_normal);
                int margingRight = mContext.getResources().getDimensionPixelOffset(R.dimen.dynamic_list_image_marginright);
                iamgeParam.setMargins(margingLeft, marginTop, margingRight, 0);
            } catch (Exception ignore) {
            }
            if (hasContent) {
                boolean canLookWords = dynamicBean.getPaid_node() == null || dynamicBean
                        .getPaid_node().isPaid();

                int startPosition = dynamicBean.getStartPosition();
                contentView.setCanLookWords(canLookWords);
                if (canLookWords) {
                    TextViewUtils.newInstance(contentView, content)
                            .spanTextColor(SkinUtils.getColor(R
                                    .color.normal_for_assist_text))
                            .position(startPosition, content.length())
                            .dataPosition(holder.getAdapterPosition())
                            .maxLines(contentView.getResources().getInteger(R.integer
                                    .dynamic_list_content_show_lines))
                            .onSpanTextClickListener(mOnSpanTextClickListener)
                            .onTextSpanComplete(() -> ConvertUtils.stringLinkConvert(contentView, setLiknks(dynamicBean, contentView.getText()
                                    .toString()), false))
                            .disPlayText(true)
                            .build();
                } else {
                    TextViewUtils.newInstance(contentView, content)
                            .spanTextColor(SkinUtils.getColor(R
                                    .color.normal_for_assist_text))
                            .position(startPosition, content.length())
                            .dataPosition(holder.getAdapterPosition())
                            .maxLines(contentView.getResources().getInteger(R.integer
                                    .dynamic_list_content_show_lines))
                            .onSpanTextClickListener(mOnSpanTextClickListener)
                            .note(dynamicBean.getPaid_node().getNode())
                            .amount(dynamicBean.getPaid_node().getAmount())
                            .onTextSpanComplete(() -> ConvertUtils.stringLinkConvert(contentView, setLiknks(dynamicBean, contentView.getText()
                                    .toString()), false))
                            .disPlayText(false)
                            .build();
                }
                contentView.setVisibility(View.VISIBLE);
            } else {
//                contentView.setVisibility(TextUtils.isEmpty(dynamicBean.getFeed_content()) ? View.GONE : View.VISIBLE);
//                contentView.setText(dynamicBean.getFeed_content());
            }

            contentView.setOnClickListener(v -> holder.getConvertView().performClick());
            holder.setVisible(R.id.dlmv_menu, showToolMenu ? View.VISIBLE : View.GONE);
            // 分割线跟随工具栏显示隐藏
            holder.setVisible(R.id.v_line, showToolMenu ? View.VISIBLE : View.GONE);
            // user_id = -1 广告
            if (showToolMenu && dynamicBean.getUser_id() > 0) {
                // 显示工具栏
                DynamicListMenuView dynamicListMenuView = holder.getView(R.id.dlmv_menu);
                dynamicListMenuView.setMoreButtonRightPadding(ConvertUtils.dp2px(mContext,15));
                dynamicListMenuView.setImageNormalResourceIds(getToolImages());
                dynamicListMenuView.setItemTextAndStatus(ConvertUtils.numberConvert(dynamicBean
                        .getFeed_digg_count()), dynamicBean.isHas_digg(), 0);
                dynamicListMenuView.setItemTextAndStatus(ConvertUtils.numberConvert(dynamicBean
                        .getFeed_comment_count()), false, 1);
                // 浏览量没有 0
                dynamicListMenuView.setItemTextAndStatus(ConvertUtils.numberConvert(dynamicBean
                                .getFeed_view_count() == 0 ? 1 : dynamicBean.getFeed_view_count()),
                        false, 2);
//                dynamicListMenuView.setItemTextAndStatus("",false, 3);
                // 控制更多按钮的显示隐藏
                dynamicListMenuView.setItemPositionVisiable(0, getVisibleOne());
                dynamicListMenuView.setItemPositionVisiable(1, getVisibleTwo());
                dynamicListMenuView.setItemPositionVisiable(2, getVisibleThree());
                dynamicListMenuView.setItemPositionVisiable(3, getVisibleFour());
//                dynamicListMenuView.setItemPositionVisiable(4, getVisibleFive());
                // 设置工具栏的点击事件
                dynamicListMenuView.setItemOnClick((parent, v, menuPostion) -> {
                    if (mOnMenuItemClickLisitener != null) {
                        mOnMenuItemClickLisitener.onMenuItemClick(v, position, menuPostion);
                    }
                });
            }

            holder.setVisible(R.id.fl_tip, showReSendBtn ? View.VISIBLE : View.GONE);
            if (showReSendBtn) {
                // 设置动态发送状态
                if (dynamicBean.getState() == DynamicBean.SEND_ERROR) {
                    holder.setVisible(R.id.fl_tip, View.VISIBLE);
                    holder.setText(R.id.tv_hint_text, TextUtils.isEmpty(dynamicBean.getSendFailMessage()) ?
                            holder.getConvertView().getResources
                                    ().getString(R.string.send_fail) : dynamicBean.getSendFailMessage());
                } else {
                    holder.setVisible(R.id.fl_tip, View.GONE);
                }
                RxView.clicks(holder.getView(R.id.fl_tip))
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> {
                            if (mOnReSendClickListener != null) {
                                mOnReSendClickListener.onReSendClick(position);
                            }
                        });
            }

            // 设置评论内容
            DynamicListCommentView comment = holder.getView(R.id.dcv_comment);
            DynamicListTopicView topics = holder.getView(R.id.dltv_topic);
            boolean showTopic = showTopicTags && (dynamicBean.getTopics() != null && !dynamicBean.getTopics().isEmpty());
            if (!showTopic) {
                topics.setVisibility(View.GONE);
            } else {
                topics.setVisibility(View.VISIBLE);
                topics.setOnTopicClickListener(mOnTopicClickListener);
                if (mOnTopicClickListener != null) {
                    topics.setData(dynamicBean, mOnTopicClickListener.doNotShowThisTopic());
                } else {
                    topics.setData(dynamicBean, null);
                }

            }
            if (!showCommentList || dynamicBean.getComments() == null || dynamicBean.getComments().isEmpty()) {
                comment.setVisibility(View.GONE);
            } else {
                comment.setVisibility(View.VISIBLE);
                comment.setData(dynamicBean);
                comment.setOnCommentClickListener(mOnCommentClickListener);
                comment.setOnMoreCommentClickListener(mOnMoreCommentClickListener);
                comment.setOnCommentStateClickListener(mOnCommentStateClickListener);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户信息被点击
     *
     * @param view
     * @param dynamicBean
     */
    private void setUserInfoClick(View view, final DynamicDetailBeanV2 dynamicBean) {
        RxView.clicks(view)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (mOnUserInfoClickListener != null) {
                        mOnUserInfoClickListener.onUserInfoClick(dynamicBean.getUserInfoBean());
                    }
                });
    }


    /**
     * 设置 imageview 点击事件，以及显示
     *
     * @param view        the target
     * @param dynamicBean item data
     * @param positon     image item position
     * @param part        this part percent of imageContainer
     */
    protected void initImageView(final ViewHolder holder, FilterImageView view,
                                 final DynamicDetailBeanV2 dynamicBean, final int positon, int part) {
        if (dynamicBean.getImages() != null && dynamicBean.getImages().size() > 0) {

            if (mFilterImageView == null) {
                mFilterImageView = view;
            }
            DynamicDetailBeanV2.ImagesBean imageBean = dynamicBean.getImages().get(positon);
            view.setLoaded(false);
            if (TextUtils.isEmpty(imageBean.getImgUrl())) {
                // 是否是 gif
                boolean isGif = ImageUtils.imageIsGif(imageBean.getImgMimeType());
                view.setIshowGifTag(isGif);
                if (isGif) {
                    holder.addGifView(view);
                }
                // 是否是长图
                view.showLongImageTag(imageBean.hasLongImage());
                Glide.with(view.getContext())
                        .load(imageBean.getGlideUrl())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .dontAnimate()
                        .transform(new GlideStokeTransform(mContext, 1, Color.LTGRAY))
                        .placeholder(R.drawable.shape_default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.shape_default_image)
                        .into(new GlideDrawableImageViewTarget(view, 0));
            } else {
                BitmapFactory.Options option = DrawableProvider.getPicsWHByFile(imageBean.getImgUrl());
                boolean isGif = ImageUtils.imageIsGif(option.outMimeType);
                view.setIshowGifTag(isGif);
                if (isGif) {
                    holder.addGifView(view);
                }
                view.showLongImageTag(isLongImage(option.outHeight, option.outWidth));

                Glide.with(view.getContext())
                        .load(imageBean.getImgUrl())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .dontAnimate()
                        .transform(new GlideStokeTransform(mContext, 1, Color.LTGRAY))
                        .override(imageBean.getCurrentWith(), imageBean.getCurrentWith())
                        .placeholder(R.drawable.shape_default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.shape_default_image)
                        .into(new GlideDrawableImageViewTarget(view, 0));
            }
        }

        RxView.clicks(view)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onImageClick(holder, dynamicBean, positon);
                    }
                });
    }

    /**
     * 获取当前item 的宽
     *
     * @param part
     * @return
     */
    protected int getCurrenItemWith(int part) {
        try {
            return (mImageContainerWith - (getCurrenCloums() - 1) * mDiverwith) / getCurrenCloums() * part;
        } catch (Exception e) {
            LogUtils.d("获取当前 item 的宽 = 0");
        }
        return 0;
    }

    protected int getCurrenCloums() {
        return CURREN_CLOUMS;
    }

    /**
     * 是否是长图
     *
     * @param imageHeight 需要判断的图片的高
     * @param imageWith   需要判断的图片的宽
     * @return
     */
    public boolean isLongImage(int imageHeight, int imageWith) {
//        float a = (float) imageHeight * mHightPixels / ((float) imageWith * mHightPixels);
//
//        return a > 3 || a < .3f;
        return ImageUtils.isLongImage(imageHeight, imageWith);
    }

    /**
     * image interface
     */
    public interface OnImageClickListener {
        void onImageClick(ViewHolder holder, DynamicDetailBeanV2 dynamicBean, int position);
    }

    /**
     * like interface
     */
    public interface OnMenuItemClickLisitener {
        void onMenuItemClick(View view, int dataPosition, int viewPosition);
    }

    /**
     * resend interface
     */
    public interface OnReSendClickListener {
        void onReSendClick(int position);
    }

    public DynamicListBaseItem setShowToolMenu(boolean showToolMenu) {
        this.showToolMenu = showToolMenu;
        return this;
    }

    public DynamicListBaseItem setShowCommentList(boolean showCommentList) {
        this.showCommentList = showCommentList;
        return this;
    }

    public DynamicListBaseItem setShowReSendBtn(boolean showReSendBtn) {
        this.showReSendBtn = showReSendBtn;
        return this;
    }

    public void setOnSpanTextClickListener(TextViewUtils.OnSpanTextClickListener
                                                   onSpanTextClickListener) {
        mOnSpanTextClickListener = onSpanTextClickListener;
    }

    protected int[] getToolImages() {
        return null;
    }

    /**
     * 工具栏第几个是否显示
     *
     * @return
     */
    protected int getVisibleOne() {
        return View.VISIBLE;
    }

    protected int getVisibleTwo() {
        return View.VISIBLE;
    }

    protected int getVisibleThree() {
        return View.VISIBLE;
    }

    protected int getVisibleFour() {
        return View.VISIBLE;
    }

//    protected int getVisibleFive() {
//        return View.VISIBLE;
//    }
    /**
     * 网页链接
     *
     * @param dynamicDetailBeanV2
     * @param content
     * @return
     */
    protected List<Link> setLiknks(final DynamicDetailBeanV2 dynamicDetailBeanV2, String content) {
        List<Link> links = new ArrayList<>();
        if (content.contains(Link.DEFAULT_NET_SITE)) {
            Link commentNameLink = new Link(Link.DEFAULT_NET_SITE)
                    .setTextColor(ContextCompat.getColor(mContext, R.color
                            .themeColor))
                    .setLinkMetadata(LinkMetadata.builder()
                            .putSerializableObj(LinkMetadata.METADATA_KEY_COTENT, new NetUrlHandleBean(dynamicDetailBeanV2.getFeed_content()))
                            .putSerializableObj(LinkMetadata.METADATA_KEY_TYPE, LinkMetadata.SpanType.NET_SITE)
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
                    if (mOnUserInfoClickListener != null) {
                        mOnUserInfoClickListener.onUserInfoClick(new UserInfoBean(RegexUtils.replaceAllAt(clickedText)));
                    }
                })
                .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color.important_for_content));
        links.add(link);
        return links;
    }

}


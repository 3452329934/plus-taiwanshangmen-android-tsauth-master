package com.zhiyicx.thinksnsplus.widget.popwindow;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.common.utils.ColorPhrase;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.Letter;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018/08/08 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jliuer
 * @Date 18/08/08 14:36
 * @Email Jliuer@aliyun.com
 * @Description 转发内容
 */
public class LetterPopWindow extends CustomPopupWindow {

    public static final String PIC = "查看图片";
    public static final String VIDEO = "查看视频";
    private CenterPopWindowItemClickListener mCenterPopWindowItemClickListener;

    private String itemRight;
    private String itemLeft;
    private String titleStr;
    private Letter letter;

    private int titleColor;
    private int nameColor;
    private int contentColor;
    private int etContentColor;
    private int itemRightColor;
    private int itemLeftColor;
    private EditText editText;

    public static CBuilder builder() {
        return new CBuilder();
    }

    protected LetterPopWindow(CBuilder builder) {
        super(builder);
        this.itemLeft = builder.itemLeft;
        this.itemRight = builder.itemRight;
        this.titleStr = builder.titleStr;
        this.letter = builder.letter;
        this.titleColor = builder.titleColor;
        this.nameColor = builder.nameColor;
        this.contentColor = builder.contentColor;
        this.itemLeftColor = builder.itemColorLeft;
        this.itemRightColor = builder.itemRightColor;
        this.etContentColor = builder.etContentColor;
        this.mCenterPopWindowItemClickListener = builder.mCenterPopWindowItemClickListener;
        initView();
    }

    private void initView() {
        FrameLayout frameLayout = mContentView.findViewById(R.id.fl_content);
        frameLayout.removeAllViews();
        int contentId;
        if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC.equals(letter.getType())) {
            // 动态
            contentId = R.layout.letter_for_word_feed;
        } else if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_INFO.equals(letter.getType())) {
            // 资讯
            contentId = R.layout.letter_for_info;
        } else if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_CIRCLE.equals(letter.getType())) {
            // 圈子
            contentId = R.layout.letter_for_circle;
        } else {
            // 帖子
            contentId = R.layout.letter_for_post;
        }
        frameLayout.addView(LayoutInflater.from(mActivity).inflate(contentId, null));
        if (contentId != R.layout.letter_for_word_feed) {
            initImageView(letter.getImage(), R.id.iv_image);
        }
        if (contentId != R.layout.letter_for_info) {
            initTextView(letter.getContent(), contentColor, R.id.tv_content);
        }

        initTextView(letter.getName(), nameColor, R.id.tv_name);
        initTextView(titleStr, titleColor, R.id.tv_title);
        initEditextView(etContentColor, R.id.et_content);
        initBottomLeftView(itemLeft, itemLeftColor, R.id.ppw_center_item_left, mCenterPopWindowItemClickListener);
        initBottomRightView(itemRight, itemRightColor, R.id.ppw_center_item, mCenterPopWindowItemClickListener);
        View emojiIco = mContentView.findViewById(R.id.iv_emoji);
        emojiIco.setOnClickListener(view -> {
            if (mCenterPopWindowItemClickListener != null) {
                mCenterPopWindowItemClickListener.onEmojiClick();
            }
        });
    }

    private void initEditextView(int colorId, int resId) {
        editText = mContentView.findViewById(resId);
        if (colorId != 0) {
            editText.setTextColor(ContextCompat.getColor(mActivity, colorId));
        }
    }

    private void initBottomRightView(String text, int colorId, int resId, final CenterPopWindowItemClickListener listener) {
        TextView textView = mContentView.findViewById(resId);
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        if (colorId != 0) {
            textView.setTextColor(ContextCompat.getColor(mActivity, colorId));
        }
        textView.setOnClickListener(view -> {
            if (listener != null) {
                letter.setMessage(getMessage());
                listener.onRightClicked(letter);
            }
        });
    }

    private void initBottomLeftView(String text, int colorId, int resId, final CenterPopWindowItemClickListener listener) {
        TextView textView = mContentView.findViewById(resId);
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
        if (colorId != 0) {
            textView.setTextColor(ContextCompat.getColor(mActivity, colorId));
        }
        textView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onLeftClicked();
            }
        });
    }

    protected void initTextView(String text, int colorId, int resId) {
        if (!TextUtils.isEmpty(text)) {
            TextView textView = mContentView.findViewById(resId);
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
            if (R.id.tv_content == resId && TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC.equals(letter.getType())) {
                if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE.equals(letter.getDynamic_type())) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ico_pic_disabled, 0, 0, 0);
                }
                if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_VIDEO.equals(letter.getDynamic_type())) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ico_video_disabled, 0, 0, 0);
                }
            }
            if (R.id.tv_title == resId) {
                String title = textView.getContext().getResources().getString(R.string.letter_send_to, text);
                CharSequence titleString;
                if (!TextUtils.isEmpty(text)) {
                    titleString = ColorPhrase.from(title).withSeparator("<>")
                            .innerColor(ContextCompat.getColor(mActivity, R.color.important_for_theme))
                            .outerColor(ContextCompat.getColor(mActivity, R.color.important_for_content))
                            .format();
                } else {
                    titleString = title;
                }
                textView.setText(titleString);
            }

            if (colorId != 0) {
                textView.setTextColor(ContextCompat.getColor(mActivity, colorId));
            }
        }
    }

    private void initImageView(String path, int resId) {
        ImageView imageView = mContentView.findViewById(resId);

        imageView.setVisibility(TextUtils.isEmpty(path) ? View.GONE : View.VISIBLE);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Glide.with(mActivity)
                .load(path)
                .placeholder(R.drawable.shape_default_image)
                .error(R.drawable.shape_default_image)
                .into(imageView);
    }

    public String getMessage() {
        return editText.getText().toString();
    }

    public EditText getEditText() {
        return editText;
    }

    public static final class CBuilder extends Builder {

        private CenterPopWindowItemClickListener mCenterPopWindowItemClickListener;
        private String itemRight;
        private String itemLeft;
        private Letter letter;
        private String titleStr;

        private int titleColor;
        private int nameColor;
        private int contentColor;
        private int etContentColor;
        private int itemRightColor;
        private int itemColorLeft;

        public CBuilder buildCenterPopWindowItem1ClickListener(CenterPopWindowItemClickListener mCenterPopWindowItem1ClickListener) {
            this.mCenterPopWindowItemClickListener = mCenterPopWindowItem1ClickListener;
            return this;
        }

        @Override
        public CBuilder backgroundAlpha(float alpha) {
            super.backgroundAlpha(alpha);
            return this;
        }

        @Override
        public CBuilder width(int width) {
            super.width(width);
            return this;
        }

        @Override
        public CBuilder height(int height) {
            super.height(height);
            return this;
        }

        public CBuilder nameColor(int nameColor) {
            this.nameColor = nameColor;
            return this;
        }

        public CBuilder contentColor(int contentColor) {
            this.contentColor = contentColor;
            return this;
        }

        public CBuilder etContentColor(int etContentColor) {
            this.etContentColor = etContentColor;
            return this;
        }

        public CBuilder itemRightColor(int itemRightColor) {
            this.itemRightColor = itemRightColor;
            return this;
        }

        public CBuilder titleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public CBuilder letter(Letter letter) {
            this.letter = letter;
            return this;
        }

        public CBuilder itemRight(String itemRightStr) {
            this.itemRight = itemRightStr;
            return this;
        }

        public CBuilder titleStr(String titleStr) {
            this.titleStr = titleStr;
            return this;
        }

        public CBuilder itemLeft(String itemLeft) {
            this.itemLeft = itemLeft;
            return this;
        }

        public CBuilder itemColorLeft(int itemColorLeft) {
            this.itemColorLeft = itemColorLeft;
            return this;
        }


        @Override
        public CBuilder with(Activity activity) {
            super.with(activity);
            return this;
        }

        @Override
        public CBuilder isOutsideTouch(boolean isOutsideTouch) {
            super.isOutsideTouch(isOutsideTouch);
            return this;
        }

        @Override
        public CBuilder isFocus(boolean isFocus) {
            super.isFocus(isFocus);
            return this;
        }

        @Override
        public CBuilder backgroundDrawable(Drawable backgroundDrawable) {
            super.backgroundDrawable(backgroundDrawable);
            return this;
        }

        @Override
        public CBuilder animationStyle(int animationStyle) {
            super.animationStyle(animationStyle);
            return this;
        }

        @Override
        public CBuilder parentView(View parentView) {
            super.parentView(parentView);
            return this;
        }

        @Override
        public LetterPopWindow build() {
            contentViewId = R.layout.ppw_for_letter;
            isWrap = true;
            return new LetterPopWindow(this);
        }
    }

    public interface CenterPopWindowItemClickListener {
        void onRightClicked(Letter letter);

        void onLeftClicked();

        void onEmojiClick();
    }

}

package com.zhiyicx.thinksnsplus.widget.popwindow;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.ProtrolBean;
import com.zhiyicx.thinksnsplus.modules.register.rule.UserRuleActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jungle68
 * @describe 支付页的提醒弹窗
 * @date 2019-08-28
 * @contact master.jungle68@gmail.com
 */
public class ProtrolPopWindow extends CustomPopupWindow {

    private CBuilder mBuilder;

    public static CBuilder builder() {
        return new CBuilder();
    }

    public CBuilder getmBuilder() {
        return mBuilder;
    }

    protected ProtrolPopWindow(CBuilder builder) {
        super(builder);
        initView(builder);
    }

    private void initView(CBuilder builder) {
        mBuilder = builder;
        initBottomLeftView(builder.leftStr, 0, R.id.ppw_center_item_left, builder.mCenterPopWindowItemClickListener);
        initBottomRightView(builder.rightStr, 0, R.id.ppw_center_item_right, builder.mCenterPopWindowItemClickListener);

        TextView tvContnt = mContentView.findViewById(R.id.ppw_center_description);
        tvContnt.setText(mActivity.getApplicationContext().getString(R.string.protrol_tip_content, mActivity.getString(R.string.app_name)));

        TextView ruleTip = mContentView.findViewById(R.id.ppw_center_attention);
        ConvertUtils.stringLinkConvert(ruleTip, setLiknks(ruleTip.getText().toString()), false);

    }

    private List<Link> setLiknks(String content) {

        List<Link> links = new ArrayList<>();
        if (content.contains(mActivity.getApplicationContext().getString(R.string.user_protrol))) {
            Link commentNameLink = new Link(mActivity.getApplicationContext().getString(R.string.user_protrol))
                    .setTextColor(ContextCompat.getColor(mActivity.getApplicationContext(), R.color
                            .textcolor_net_link))
                    .setHighlightAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                    .setOnClickListener((clickedText, linkMetadata) -> {
                        UserRuleActivity.startUserRuleActivity(mActivity, ProtrolBean.TYPE_USER_AGREEMENT);
                    })
                    .setUnderlined(false);
            links.add(commentNameLink);
        }
        if (content.contains(mActivity.getApplicationContext().getString(R.string.privite_protrol))) {
            Link commentNameLink = new Link(mActivity.getApplicationContext().getString(R.string.privite_protrol))
                    .setTextColor(ContextCompat.getColor(mActivity.getApplicationContext(), R.color
                            .textcolor_net_link))
                    .setHighlightAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                    .setOnClickListener((clickedText, linkMetadata) -> {
                        UserRuleActivity.startUserRuleActivity(mActivity, ProtrolBean.TYPE_PRIVACY_AGREEMENT);
                    })
                    .setUnderlined(false);
            links.add(commentNameLink);
        }
        return links;
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
                listener.onRightClicked();
            }
        });
    }

    public void updateLeftText() {
        if (mBuilder != null && !TextUtils.isEmpty(mBuilder.leftStr)) {
            initBottomLeftView(mBuilder.leftStr, 0, R.id.ppw_center_item_left, mBuilder.mCenterPopWindowItemClickListener);
        }
    }

    public void updateRightText() {
        if (mBuilder != null && !TextUtils.isEmpty(mBuilder.rightStr)) {
            initBottomRightView(mBuilder.rightStr, 0, R.id.ppw_center_item_right, mBuilder.mCenterPopWindowItemClickListener);
        }
    }
    public void updateTitleText() {
        TextView textView = mContentView.findViewById(R.id.ppw_center_titl);
        if (mBuilder != null && !TextUtils.isEmpty(mBuilder.title)) {
            textView.setText(mBuilder.title);
        }
    }

    public void updateContentText() {
        TextView textView = mContentView.findViewById(R.id.ppw_center_description);
        if (mBuilder != null && !TextUtils.isEmpty(mBuilder.content)) {
            textView.setText(mBuilder.content);
        }
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
            if (colorId != 0) {
                textView.setTextColor(ContextCompat.getColor(mActivity, colorId));
            }
        }
    }

    public static final class CBuilder extends Builder {

        private CenterPopWindowItemClickListener mCenterPopWindowItemClickListener;


        private String title;
        private String content;
        private String tip;
        private String leftStr;
        private String rightStr;


        public CBuilder buildCenterPopWindowItem1ClickListener(CenterPopWindowItemClickListener l) {
            this.mCenterPopWindowItemClickListener = l;
            return this;
        }

        public CBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CBuilder content(String content) {
            this.content = content;
            return this;
        }

        public CBuilder tip(String tip) {
            this.tip = tip;
            return this;
        }

        public CBuilder leftStr(String leftStr) {
            this.leftStr = leftStr;
            return this;
        }

        public CBuilder rightStr(String rightStr) {
            this.rightStr = rightStr;
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
        public ProtrolPopWindow build() {
            contentViewId = R.layout.ppw_for_user_protrol;
            isWrap = true;
            return new ProtrolPopWindow(this);
        }
    }

    public interface CenterPopWindowItemClickListener {
        void onRightClicked();

        void onLeftClicked();
    }

}

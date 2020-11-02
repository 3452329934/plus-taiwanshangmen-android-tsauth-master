package com.zhiyicx.thinksnsplus.widget.popwindow;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyi.rxdownload3.core.Failed;
import com.zhiyi.rxdownload3.core.Status;
import com.zhiyi.rxdownload3.core.Succeed;
import com.zhiyicx.baseproject.widget.imageview.ShadowDrawable;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.SkinUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jliuer
 * @Date 18/11/07 16:43
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class DownloadPopWindow extends CustomPopupWindow {

    private ProgressBar mProgressBar;
    private TextView mDownlaod;
    private TextView mCancle;
    private TextView mProgress;
    private TextView mProgressWhite;
    private Rect mProgressRect;
    private Rect mClipRect;

    private OnclickListener mOnclickListener;

    public static CBuilder builder() {
        return new CBuilder();
    }

    protected DownloadPopWindow(CBuilder builder) {
        super(builder);
        this.mOnclickListener = builder.mOnclickListener;
        initView();
    }

    private void initView() {
        ShadowDrawable.setShadowDrawable(mContentView, SkinUtils.getColor(com.zhiyicx.baseproject.R.color.white),
                ConvertUtils.dp2px(mActivity, 10), Color.parseColor("#11030303"),
                ConvertUtils.dp2px(mActivity, 10), 0, 0);

        mClipRect = new Rect();
        mProgressBar = mContentView.findViewById(R.id.progress_bar);
        mDownlaod = mContentView.findViewById(R.id.tv_download);
        mCancle = mContentView.findViewById(R.id.tv_cancle);
        mProgress = mContentView.findViewById(R.id.tv_progress);
        mProgressWhite = mContentView.findViewById(R.id.tv_progress_white);
        mCancle.setText(R.string.downlaod_cancle);
        mProgressRect = new Rect();

        RxView.clicks(mCancle)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .filter(aVoid -> mOnclickListener != null)
                .subscribe(aVoid -> mOnclickListener.onCacnleClick());

        RxView.clicks(mDownlaod)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .filter(aVoid -> mOnclickListener != null)
                .subscribe(aVoid -> mOnclickListener.onReDownloadClick());
    }

    public static final class CBuilder extends Builder {

        private OnclickListener mOnclickListener;

        public CBuilder buildOnclickListener(OnclickListener listener) {
            mOnclickListener = listener;
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
        public DownloadPopWindow build() {
            contentViewId = R.layout.ppw_for_donwload;
            isWrap = true;
            return new DownloadPopWindow(this);
        }
    }

    public void updateStatus(Status status) {
        if (status instanceof Failed) {
            mDownlaod.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mProgressWhite.setVisibility(View.GONE);
            mDownlaod.setEnabled(true);
            mDownlaod.setText(R.string.downlaod_retry);
        } else if (status instanceof Succeed) {
            LogUtils.d("Succeed");
            mDownlaod.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mProgressWhite.setVisibility(View.GONE);
            mDownlaod.setEnabled(false);
            mDownlaod.setText(R.string.downlaod_complete);
            mCancle.setText(R.string.video_downlaod_complete);

            Observable.timer(2000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> hide());

        } else {
            mDownlaod.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
            mProgressWhite.setVisibility(View.VISIBLE);
        }
        LogUtils.d(status.percent());
        mProgressBar.setProgress((int) status.getDownloadSize());
        mProgressBar.setMax((int) status.getTotalSize());
        mProgress.setText(status.percent());
        mCancle.setText(R.string.downlaod_cancle);
        mProgressWhite.setText(status.percent());
        if (mProgressRect.width() == 0) {
            mProgressBar.getLocalVisibleRect(mProgressRect);
        }
        if (mProgressBar.getMax() > 0) {
            float d = (float) mProgressBar.getProgress() / (float) mProgressBar.getMax();
            mClipRect.set(mProgressRect.left + (int) (mProgressRect.right * d), mProgressRect.top, mProgressRect.right, mProgressRect.bottom);
            mProgress.setClipBounds(mClipRect);
        }

    }

    public interface OnclickListener {
        void onCacnleClick();

        void onReDownloadClick();
    }

}

package com.zhiyicx.thinksnsplus.base.fordownload;

import android.view.MotionEvent;
import android.view.View;

import com.zhiyi.rxdownload3.core.Status;
import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.base.BaseActivity;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.widget.popwindow.DownloadPopWindow;

import java.util.Objects;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/11/08/14:47
 * @Email Jliuer@aliyun.com
 * @Description
 */
public abstract class TSListFragmentForDownload<P extends ITSListPresenterForDownload<T>, T extends BaseListBean>
        extends TSListFragment<P, T> implements ITSListViewForDownload<T, P>, BaseActivity.FragmentDispatchTouchEventListener {

    protected DownloadPopWindow mDownloadPopWindow;

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        ((BaseActivity) (Objects.requireNonNull(getActivity()))).registerFragmentDispatchTouchEventListener(this);
    }

    @Override
    public void updateDownloadStatus(Status status, String url) {
        boolean needCreateWindow = mDownloadPopWindow == null;
        if (needCreateWindow) {
            initDownloadPopwindow(url);
        }
        if (mDownloadPopWindow != null && !mDownloadPopWindow.isShowing()) {
            mDownloadPopWindow.show();
        }
        mDownloadPopWindow.updateStatus(status);
    }

    @Override
    public boolean backPressed() {
        boolean isShow = mDownloadPopWindow != null && mDownloadPopWindow.isShowing();
        if (isShow) {
            mDownloadPopWindow.dismiss();
        }
        return isShow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mDownloadPopWindow != null && mDownloadPopWindow.isShowing();
    }

    protected void initDownloadPopwindow(String url) {
        if (mDownloadPopWindow != null && mDownloadPopWindow.isShowing()) {
            mDownloadPopWindow.dismiss();
        }
        mDownloadPopWindow = DownloadPopWindow.builder()
                .with(mActivity)
                .isFocus(false)
                .isOutsideTouch(false)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALL_ALPHA)
                .buildOnclickListener(new DownloadPopWindow.OnclickListener() {
                    @Override
                    public void onCacnleClick() {
                        mPresenter.cancelDownload(url);
                        mDownloadPopWindow.hide();
                    }

                    @Override
                    public void onReDownloadClick() {
                        mPresenter.downloadFile(url);
                    }
                })
                .build();
        mDownloadPopWindow.show();
        mDownloadPopWindow.setOnDismissListener(() -> mPresenter.cancelDownload(url));
    }

    @Override
    public void onDestroyView() {
        dismissPop(mDownloadPopWindow);
        super.onDestroyView();
    }
}

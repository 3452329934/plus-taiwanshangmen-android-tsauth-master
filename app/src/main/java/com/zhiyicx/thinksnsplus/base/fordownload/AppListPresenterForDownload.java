package com.zhiyicx.thinksnsplus.base.fordownload;

import com.zhiyi.rxdownload3.RxDownload;
import com.zhiyi.rxdownload3.core.Failed;
import com.zhiyi.rxdownload3.core.LocalMissionBox;
import com.zhiyi.rxdownload3.core.Status;
import com.zhiyi.rxdownload3.helper.UtilsKt;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.SharePreferenceUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;

import cn.jzvd.JZUtils;
import io.reactivex.Flowable;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/11/08/15:04
 * @Email Jliuer@aliyun.com
 * @Description
 */
public abstract class AppListPresenterForDownload<V extends ITSListViewForDownload> extends AppBasePresenter<V>
        implements IPresenterForDownload {

    private Disposable disposable;

    private ActionPopupWindow mWarnPopupWindow;

    public AppListPresenterForDownload(V rootView) {
        super(rootView);
    }

    @Override
    public void downloadFile(String url) {
        if (!JZUtils.isWifiConnected(mContext) && !SharePreferenceUtils.getBoolean(mContext, ALLOW_GPRS)) {
            initWarningDialog(url);
        } else {
            download(url);
        }
    }

    protected void download(String url) {
        disposable = RxDownload.INSTANCE.create(url, true)
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(status -> mRootView.updateDownloadStatus(status, url), throwable ->
                        mRootView.updateDownloadStatus(new Failed(new Status(), throwable), url));



    }

    @Override
    public void cancelDownload(String url) {
        UtilsKt.dispose(disposable);
        RxDownload.INSTANCE.stop(url).subscribe();
    }

    @Override
    public void onDestroy() {
        UtilsKt.dispose(disposable);
        if (mWarnPopupWindow != null && mWarnPopupWindow.isShowing()) {
            mWarnPopupWindow.hide();
        }
        super.onDestroy();
    }

    protected void initWarningDialog(String url) {
        mWarnPopupWindow = ActionPopupWindow.builder()
                .item1Str(mContext.getString(R.string.info_publish_hint))
                .desStr(mContext.getString(R.string.tips_not_wifi))
                .item2Str(mContext.getString(R.string.keepon))
                .bottomStr(mContext.getString(R.string.giveup))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(((TSFragment) mRootView).getActivity())
                .item2ClickListener(() -> {
                    mWarnPopupWindow.dismiss();
                    SharePreferenceUtils.saveBoolean(mContext, ALLOW_GPRS, true);
                    download(url);
                })
                .bottomClickListener(() -> mWarnPopupWindow.dismiss())
                .build();
        mWarnPopupWindow.show();
    }
}

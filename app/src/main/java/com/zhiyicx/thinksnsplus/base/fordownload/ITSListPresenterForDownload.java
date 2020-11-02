package com.zhiyicx.thinksnsplus.base.fordownload;

import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.baseproject.base.ITSListPresenter;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/11/08/14:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface ITSListPresenterForDownload<T extends BaseListBean> extends ITSListPresenter<T> {
    void downloadFile(String url);

    void cancelDownload(String url);
}

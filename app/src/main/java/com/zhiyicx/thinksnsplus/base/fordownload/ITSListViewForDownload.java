package com.zhiyicx.thinksnsplus.base.fordownload;

import com.zhiyi.rxdownload3.core.Status;
import com.zhiyicx.baseproject.base.ITSListView;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/11/08/14:50
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface ITSListViewForDownload<T, P> extends ITSListView<T, P> {
    void updateDownloadStatus(Status status, String url);
    boolean backPressed();
}

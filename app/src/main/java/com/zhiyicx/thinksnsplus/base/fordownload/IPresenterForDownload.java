package com.zhiyicx.thinksnsplus.base.fordownload;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/11/08/15:34
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface IPresenterForDownload {

    String ALLOW_GPRS = "ALLOW_GPRS";

    void downloadFile(String url);

    void cancelDownload(String url);
}

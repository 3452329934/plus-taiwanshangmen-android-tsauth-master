package com.zhiyicx.thinksnsplus.data.beans;

/**
 * ThinkSNS Plus
 * Copyright (c) 2019 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jungle68
 * @describe
 * @date 2019-11-28
 * @contact master.jungle68@gmail.com
 */
public class ProtrolBean {

    /**
     * type	string	必须，暂时支持{user_agreement: 用户协议, privacy_agreement: 隐私政策}
     */
    public static final String TYPE_USER_AGREEMENT = "user";
    public static final String TYPE_PRIVACY_AGREEMENT = "privacy";
    public static final String TYPE_VIP_PROTROL = "vip_protrol";

    private String content;
    private String url;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

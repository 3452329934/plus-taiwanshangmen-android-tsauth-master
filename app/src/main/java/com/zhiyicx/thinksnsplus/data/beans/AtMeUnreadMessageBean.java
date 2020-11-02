package com.zhiyicx.thinksnsplus.data.beans;

import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/14/18:18
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AtMeUnreadMessageBean {
    private List<String> users;
    private String latest_at;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getLatest_at() {
        return latest_at;
    }

    public void setLatest_at(String latest_at) {
        this.latest_at = latest_at;
    }
}

package com.zhiyicx.thinksnsplus.config;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jungle68
 * @describe 动态权限类型，每一个值代表一个权限
 * @date 2018/11/27
 * @contact master.jungle68@gmail.com
 */
public enum UserPermissions {

    /**
     * 删除动态
     */
    FEED_DELETE("[feed] Delete Feed");

    public String value;

    UserPermissions(String value) {
        this.value = value;
    }


}

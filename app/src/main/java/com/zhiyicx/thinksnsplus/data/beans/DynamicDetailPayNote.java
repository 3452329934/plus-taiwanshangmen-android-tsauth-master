package com.zhiyicx.thinksnsplus.data.beans;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/09/13/17:05
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class DynamicDetailPayNote{
    private String message;
    private int paid_node;
    private int amount;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPaid_node() {
        return paid_node;
    }

    public void setPaid_node(int paid_node) {
        this.paid_node = paid_node;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

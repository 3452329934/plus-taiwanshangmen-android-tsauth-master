package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2019 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author icepring
 * @Date 2019/02/26/10:46
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class UnreadNotifyMsgBean implements Parcelable, Serializable {
    private static final long serialVersionUID = 4063310623089572613L;
    private int badge;
    private String last_created_at;
    private List<String> preview_users_names;

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public String getLast_created_at() {
        return last_created_at;
    }

    public void setLast_created_at(String last_created_at) {
        this.last_created_at = last_created_at;
    }

    public List<String> getPreview_users_names() {
        return preview_users_names;
    }

    public void setPreview_users_names(List<String> preview_users_names) {
        this.preview_users_names = preview_users_names;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.badge);
        dest.writeString(this.last_created_at);
        dest.writeStringList(this.preview_users_names);
    }

    public UnreadNotifyMsgBean() {
    }

    protected UnreadNotifyMsgBean(Parcel in) {
        this.badge = in.readInt();
        this.last_created_at = in.readString();
        this.preview_users_names = in.createStringArrayList();
    }

    public static final Creator<UnreadNotifyMsgBean> CREATOR = new Creator<UnreadNotifyMsgBean>() {
        @Override
        public UnreadNotifyMsgBean createFromParcel(Parcel source) {
            return new UnreadNotifyMsgBean(source);
        }

        @Override
        public UnreadNotifyMsgBean[] newArray(int size) {
            return new UnreadNotifyMsgBean[size];
        }
    };
}

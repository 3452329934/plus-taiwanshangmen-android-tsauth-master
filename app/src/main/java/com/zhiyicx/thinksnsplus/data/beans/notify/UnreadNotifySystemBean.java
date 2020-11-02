package com.zhiyicx.thinksnsplus.data.beans.notify;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * ThinkSNS Plus
 * Copyright (c) 2019 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author icepring
 * @Date 2019/02/26/10:46
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class UnreadNotifySystemBean implements Parcelable, Serializable{
    private static final long serialVersionUID = -997902714202410581L;
    private int badge;
    private FirstBean first;

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public FirstBean getFirst() {
        return first;
    }

    public void setFirst(FirstBean first) {
        this.first = first;
    }

    public static class FirstBean implements Parcelable, Serializable{
        private static final long serialVersionUID = 7085004449772047020L;
        private String id;
        private String read_at;
        private String created_at;
        private DataBean data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRead_at() {
            return read_at;
        }

        public void setRead_at(String read_at) {
            this.read_at = read_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.read_at);
            dest.writeString(this.created_at);
            dest.writeParcelable(this.data, flags);
        }

        public FirstBean() {
        }

        protected FirstBean(Parcel in) {
            this.id = in.readString();
            this.read_at = in.readString();
            this.created_at = in.readString();
            this.data = in.readParcelable(DataBean.class.getClassLoader());
        }

        public static final Creator<FirstBean> CREATOR = new Creator<FirstBean>() {
            @Override
            public FirstBean createFromParcel(Parcel source) {
                return new FirstBean(source);
            }

            @Override
            public FirstBean[] newArray(int size) {
                return new FirstBean[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.badge);
        dest.writeParcelable(this.first, flags);
    }

    public UnreadNotifySystemBean() {
    }

    protected UnreadNotifySystemBean(Parcel in) {
        this.badge = in.readInt();
        this.first = in.readParcelable(FirstBean.class.getClassLoader());
    }

    public static final Creator<UnreadNotifySystemBean> CREATOR = new Creator<UnreadNotifySystemBean>() {
        @Override
        public UnreadNotifySystemBean createFromParcel(Parcel source) {
            return new UnreadNotifySystemBean(source);
        }

        @Override
        public UnreadNotifySystemBean[] newArray(int size) {
            return new UnreadNotifySystemBean[size];
        }
    };
}

package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;

import com.zhiyicx.baseproject.base.BaseListBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Unique;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/10:38
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Entity
public class SearchHistoryBean extends BaseListBean {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_NORMAL = 1;
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String content;
    @OrderBy("create_at DESC")
    private long create_at;
    private int type = TYPE_NORMAL;

    public SearchHistoryBean(String content) {
        this.content = content;
        this.create_at = System.currentTimeMillis();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.id);
        dest.writeString(this.content);
        dest.writeLong(this.create_at);
        dest.writeInt(this.type);
    }

    protected SearchHistoryBean(Parcel in) {
        super(in);
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.content = in.readString();
        this.create_at = in.readLong();
        this.type = in.readInt();
    }


    @Generated(hash = 639683378)
    public SearchHistoryBean(Long id, String content, long create_at, int type) {
        this.id = id;
        this.content = content;
        this.create_at = create_at;
        this.type = type;
    }


    @Generated(hash = 1570282321)
    public SearchHistoryBean() {
    }

    public static final Creator<SearchHistoryBean> CREATOR = new Creator<SearchHistoryBean>() {
        @Override
        public SearchHistoryBean createFromParcel(Parcel source) {
            return new SearchHistoryBean(source);
        }

        @Override
        public SearchHistoryBean[] newArray(int size) {
            return new SearchHistoryBean[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SearchHistoryBean that = (SearchHistoryBean) o;

        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getContent() {
        return this.content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public long getCreate_at() {
        return this.create_at;
    }


    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }


    public int getType() {
        return this.type;
    }


    public void setType(int type) {
        this.type = type;
    }
}

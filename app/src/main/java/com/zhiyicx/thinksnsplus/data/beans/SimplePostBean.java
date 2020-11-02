package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;

import com.zhiyicx.baseproject.base.BaseListBean;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/21/15:55
 * @Email Jliuer@aliyun.com
 * @Description 批量获取帖子简单数据
 */
public class SimplePostBean extends BaseListBean {

    /**
     * id : 1
     * group_id : 1
     * title : 帖子标题
     * summary : 帖子内容
     * image : 1
     */

    private Long id;
    private Long group_id;
    private String title;
    private String summary;
    private int image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.id);
        dest.writeValue(this.group_id);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeInt(this.image);
    }

    public SimplePostBean() {
    }

    protected SimplePostBean(Parcel in) {
        super(in);
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.group_id = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.summary = in.readString();
        this.image = in.readInt();
    }

    public static final Creator<SimplePostBean> CREATOR = new Creator<SimplePostBean>() {
        @Override
        public SimplePostBean createFromParcel(Parcel source) {
            return new SimplePostBean(source);
        }

        @Override
        public SimplePostBean[] newArray(int size) {
            return new SimplePostBean[size];
        }
    };
}

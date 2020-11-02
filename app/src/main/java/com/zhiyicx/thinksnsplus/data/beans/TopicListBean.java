package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.thinksnsplus.data.source.local.data_convert.AvatarConvert;
import com.zhiyicx.thinksnsplus.data.source.local.data_convert.BaseConvert;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/17:17
 * @Email Jliuer@aliyun.com
 * @Description 话题列表
 */
@Entity
public class TopicListBean extends BaseListBean implements Serializable, Parcelable {
    private static final long serialVersionUID = -7644468875941863599L;
    /**
     * id : 1 话题 ID, 主要用于查询定位。
     * name : Plus 话题名称
     * logo : 2 基于 File With 的话题 Logo
     * created_at : 2018-07-23T15:04:23Z Zulu 格式，话题创建时间
     */

    @Id
    private Long id;
    private String name;
    @Convert(columnType = String.class,converter = AvatarConvert.class)
    private Avatar logo;
    private boolean isHotTopic;
    private String created_at;
    @Convert(converter = PivotConvert.class, columnType = String.class)
    private Pivot pivot;
    private boolean has_followed;

    @Override
    public Long getMaxId() {
        return id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Avatar getLogo() {
        return logo;
    }

    public void setLogo(Avatar logo) {
        this.logo = logo;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isHotTopic() {
        return isHotTopic;
    }

    public void setHotTopic(boolean hotTopic) {
        isHotTopic = hotTopic;
    }

    public boolean isHas_followed() {
        return has_followed;
    }

    public void setHas_followed(boolean has_followed) {
        this.has_followed = has_followed;
    }

    public static class Pivot implements Parcelable, Serializable {
        private static final long serialVersionUID = -76444688759418699L;

        Long feed_id;
        Long topic_id;

        public Long getFeed_id() {
            return feed_id;
        }

        public void setFeed_id(Long feed_id) {
            this.feed_id = feed_id;
        }

        public Long getTopic_id() {
            return topic_id;
        }

        public void setTopic_id(Long topic_id) {
            this.topic_id = topic_id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.feed_id);
            dest.writeValue(this.topic_id);
        }

        public Pivot() {
        }

        protected Pivot(Parcel in) {
            this.feed_id = (Long) in.readValue(Long.class.getClassLoader());
            this.topic_id = (Long) in.readValue(Long.class.getClassLoader());
        }

        public static final Creator<Pivot> CREATOR = new Creator<Pivot>() {
            @Override
            public Pivot createFromParcel(Parcel source) {
                return new Pivot(source);
            }

            @Override
            public Pivot[] newArray(int size) {
                return new Pivot[size];
            }
        };
    }

    static class PivotConvert extends BaseConvert<Pivot> {
    }

    public TopicListBean() {
    }

    public TopicListBean(String name) {
        this.name = name;
    }

    public TopicListBean(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TopicListBean that = (TopicListBean) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public boolean getIsHotTopic() {
        return this.isHotTopic;
    }

    public void setIsHotTopic(boolean isHotTopic) {
        this.isHotTopic = isHotTopic;
    }

    public Pivot getPivot() {
        return this.pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }


    @Generated(hash = 821098927)
    public TopicListBean(Long id, String name, Avatar logo, boolean isHotTopic,
            String created_at, Pivot pivot, boolean has_followed) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.isHotTopic = isHotTopic;
        this.created_at = created_at;
        this.pivot = pivot;
        this.has_followed = has_followed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.logo, flags);
        dest.writeByte(this.isHotTopic ? (byte) 1 : (byte) 0);
        dest.writeString(this.created_at);
        dest.writeParcelable(this.pivot, flags);
        dest.writeByte(this.has_followed ? (byte) 1 : (byte) 0);
    }

    public boolean getHas_followed() {
        return this.has_followed;
    }

    protected TopicListBean(Parcel in) {
        super(in);
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.logo = in.readParcelable(Avatar.class.getClassLoader());
        this.isHotTopic = in.readByte() != 0;
        this.created_at = in.readString();
        this.pivot = in.readParcelable(Pivot.class.getClassLoader());
        this.has_followed = in.readByte() != 0;
    }

    public static final Creator<TopicListBean> CREATOR = new Creator<TopicListBean>() {
        @Override
        public TopicListBean createFromParcel(Parcel source) {
            return new TopicListBean(source);
        }

        @Override
        public TopicListBean[] newArray(int size) {
            return new TopicListBean[size];
        }
    };
}

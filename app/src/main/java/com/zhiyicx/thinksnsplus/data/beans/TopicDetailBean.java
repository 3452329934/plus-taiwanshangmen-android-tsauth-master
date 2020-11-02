package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/26/9:31
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopicDetailBean implements Parcelable {


    /**
     * id : 1 话题 ID
     * name : Plus 话题名称
     * logo : 1 话题 Logo 对应的 FileWith ID
     * desc : 啊哈哈 话题描述
     * creator_user_id : 1 话题创建者 User ID
     * feeds_count : 2 话题下有都少动态
     * followers_count : 1 有多少人关注了这个话题
     * has_followed : true 当前 Authorization 用户是否关注了话题
     */

    private Long id;
    private String name;
    private Avatar logo;

    // 本地图片
    private String logoUrl;
    private String desc;
    private Long creator_user_id;

    // 创建者
    private UserInfoBean creator_user;
    private List<UserInfoBean> userList;
    private int feeds_count;
    private int followers_count;
    private boolean has_followed;

    // 最多三个，参与者需求，按照参与时间倒序！
    private List<Integer> participants;

    public List<UserInfoBean> getUserList() {
        if (userList == null) {
            userList = new ArrayList<>();
        }
        return userList;
    }

    public void setUserList(List<UserInfoBean> userList) {
        this.userList = userList;
    }

    public List<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Integer> participants) {
        this.participants = participants;
    }

    public UserInfoBean getCreator_user() {
        if (creator_user == null) {
            return new UserInfoBean("未知用户");
        }
        return creator_user;
    }

    public void setCreator_user(UserInfoBean creator_user) {
        this.creator_user = creator_user;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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

    public String getDesc() {
        if (TextUtils.isEmpty(desc)) {
            return "";
        }
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getCreator_user_id() {
        return creator_user_id;
    }

    public void setCreator_user_id(Long creator_user_id) {
        this.creator_user_id = creator_user_id;
    }

    public int getFeeds_count() {
        return feeds_count;
    }

    public void setFeeds_count(int feeds_count) {
        this.feeds_count = feeds_count;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public boolean has_followed() {
        return has_followed;
    }

    public void setHas_followed(boolean has_followed) {
        this.has_followed = has_followed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.logo, flags);
        dest.writeString(this.logoUrl);
        dest.writeString(this.desc);
        dest.writeValue(this.creator_user_id);
        dest.writeParcelable(this.creator_user, flags);
        dest.writeTypedList(this.userList);
        dest.writeInt(this.feeds_count);
        dest.writeInt(this.followers_count);
        dest.writeByte(this.has_followed ? (byte) 1 : (byte) 0);
        dest.writeList(this.participants);
    }

    public TopicDetailBean() {
    }

    protected TopicDetailBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.logo = in.readParcelable(Avatar.class.getClassLoader());
        this.logoUrl = in.readString();
        this.desc = in.readString();
        this.creator_user_id = (Long) in.readValue(Long.class.getClassLoader());
        this.creator_user = in.readParcelable(UserInfoBean.class.getClassLoader());
        this.userList = in.createTypedArrayList(UserInfoBean.CREATOR);
        this.feeds_count = in.readInt();
        this.followers_count = in.readInt();
        this.has_followed = in.readByte() != 0;
        this.participants = new ArrayList<Integer>();
        in.readList(this.participants, Integer.class.getClassLoader());
    }

    public static final Creator<TopicDetailBean> CREATOR = new Creator<TopicDetailBean>() {
        @Override
        public TopicDetailBean createFromParcel(Parcel source) {
            return new TopicDetailBean(source);
        }

        @Override
        public TopicDetailBean[] newArray(int size) {
            return new TopicDetailBean[size];
        }
    };
}

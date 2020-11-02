package com.zhiyicx.thinksnsplus.data.beans.notify;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/16/11:46
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AtMeaasgeBean extends BaseListBean{

    /**
     * id : 1
     * user_id : 1
     * resourceable : {"type":"feeds","id":1}
     * created_at : 2018-08-13T08:06:54Z
     */

    /**
     * 消息 ID
     * 评论 ID
     */
    private Long id;

    /**
     * 上级资源id
     */
    private Long parent_id;

    /**
     * 消息接收人ID（当前用户ID）
     * 评论作者用户 ID
     */
    private Long user_id;

    /**
     * 接收评论用户 ID
     */
    private Long target_user;

    /**
     * 被回复的用户 ID
     */
    private Long reply_user;
    private ResourceableBean resourceable;
    @SerializedName(value = "body")
    private String title;
    private String body_image;

    /**
     * at的数据类型
     */
    private String at_type;
    /**
     * 处理过的文本
     */
    private String body_content;
    /**
     * 没处理过的文本
     */
    private String content;
    /**
     * 附加内容，比如标题等
     */
    private String extra_content;
    private String created_at;
    private UserInfoBean mUserInfoBean;
    private boolean hasVideo;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserInfoBean getUserInfoBean() {
        return mUserInfoBean;
    }

    public void setUserInfoBean(UserInfoBean userInfoBean) {
        mUserInfoBean = userInfoBean;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getTarget_user() {
        return target_user;
    }

    public void setTarget_user(Long target_user) {
        this.target_user = target_user;
    }

    public Long getReply_user() {
        return reply_user;
    }

    public void setReply_user(Long reply_user) {
        this.reply_user = reply_user;
    }

    public ResourceableBean getResourceable() {
        return resourceable;
    }

    public void setResourceable(ResourceableBean resourceable) {
        this.resourceable = resourceable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody_image() {
        return body_image;
    }

    public void setBody_image(String body_image) {
        this.body_image = body_image;
    }

    public String getBody_content() {
        return body_content;
    }

    public void setBody_content(String body_content) {
        this.body_content = body_content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public String getAt_type() {
        return at_type;
    }

    public void setAt_type(String at_type) {
        this.at_type = at_type;
    }

    public String getExtra_content() {
        return extra_content;
    }

    public void setExtra_content(String extra_content) {
        this.extra_content = extra_content;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.id);
        dest.writeValue(this.parent_id);
        dest.writeValue(this.user_id);
        dest.writeValue(this.target_user);
        dest.writeValue(this.reply_user);
        dest.writeParcelable(this.resourceable, flags);
        dest.writeString(this.title);
        dest.writeString(this.body_image);
        dest.writeString(this.at_type);
        dest.writeString(this.body_content);
        dest.writeString(this.content);
        dest.writeString(this.extra_content);
        dest.writeString(this.created_at);
        dest.writeParcelable(this.mUserInfoBean, flags);
        dest.writeByte(this.hasVideo ? (byte) 1 : (byte) 0);
    }

    public AtMeaasgeBean() {
    }

    protected AtMeaasgeBean(Parcel in) {
        super(in);
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.parent_id = (Long) in.readValue(Long.class.getClassLoader());
        this.user_id = (Long) in.readValue(Long.class.getClassLoader());
        this.target_user = (Long) in.readValue(Long.class.getClassLoader());
        this.reply_user = (Long) in.readValue(Long.class.getClassLoader());
        this.resourceable = in.readParcelable(ResourceableBean.class.getClassLoader());
        this.title = in.readString();
        this.body_image = in.readString();
        this.at_type = in.readString();
        this.body_content = in.readString();
        this.content = in.readString();
        this.extra_content = in.readString();
        this.created_at = in.readString();
        this.mUserInfoBean = in.readParcelable(UserInfoBean.class.getClassLoader());
        this.hasVideo = in.readByte() != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AtMeaasgeBean that = (AtMeaasgeBean) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static final Creator<AtMeaasgeBean> CREATOR = new Creator<AtMeaasgeBean>() {
        @Override
        public AtMeaasgeBean createFromParcel(Parcel source) {
            return new AtMeaasgeBean(source);
        }

        @Override
        public AtMeaasgeBean[] newArray(int size) {
            return new AtMeaasgeBean[size];
        }
    };
}

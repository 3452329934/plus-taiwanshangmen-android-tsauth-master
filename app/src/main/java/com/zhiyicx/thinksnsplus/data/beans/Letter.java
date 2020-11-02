package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;

import java.io.Serializable;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/21/10:20
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class Letter implements Parcelable, Serializable {

    private static final long serialVersionUID = -3290312084954682536L;
    private String name;
    private String content;
    private String message;
    private String image;
    private String type;
    private String id;
    private String dynamic_type;
    private String circle_id;
    private String circle_type;
    private boolean isDeleted;

    public Letter(String type) {
        this.type = type;
    }

    public Letter(String name, String content, String type) {
        this.name = name;
        this.content = content;
        this.type = type;
    }

    public Letter(String name, String content, String type, boolean isDeleted) {
        // 专用于被删除的资源
        this.name = name;
        this.content = content;
        this.type = type;
        this.isDeleted = isDeleted;
        this.dynamic_type = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD;
    }

    public Letter(String name, String content, String message, String type) {
        this.name = name;
        this.content = content;
        this.message = message;
        this.type = type;
    }

    public Letter(String name, String content, String message, String image, String type) {
        this.name = name;
        this.content = content;
        this.message = message;
        this.image = image;
        this.type = type;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(String circle_id) {
        this.circle_id = circle_id;
    }

    public String getDynamic_type() {
        return dynamic_type;
    }

    public void setDynamic_type(String dynamic_type) {
        this.dynamic_type = dynamic_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCircle_type() {
        return circle_type;
    }

    public void setCircle_type(String circle_type) {
        this.circle_type = circle_type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.content);
        dest.writeString(this.message);
        dest.writeString(this.image);
        dest.writeString(this.type);
        dest.writeString(this.id);
        dest.writeString(this.dynamic_type);
        dest.writeString(this.circle_id);
        dest.writeString(this.circle_type);
        dest.writeByte(this.isDeleted ? (byte) 1 : (byte) 0);
    }

    protected Letter(Parcel in) {
        this.name = in.readString();
        this.content = in.readString();
        this.message = in.readString();
        this.image = in.readString();
        this.type = in.readString();
        this.id = in.readString();
        this.dynamic_type = in.readString();
        this.circle_id = in.readString();
        this.circle_type = in.readString();
        this.isDeleted = in.readByte() != 0;
    }

    public static final Creator<Letter> CREATOR = new Creator<Letter>() {
        @Override
        public Letter createFromParcel(Parcel source) {
            return new Letter(source);
        }

        @Override
        public Letter[] newArray(int size) {
            return new Letter[size];
        }
    };
}

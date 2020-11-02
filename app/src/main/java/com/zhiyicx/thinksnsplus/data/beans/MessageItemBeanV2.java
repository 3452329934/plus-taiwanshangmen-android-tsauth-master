package com.zhiyicx.thinksnsplus.data.beans;


import android.os.Parcel;

import com.hyphenate.chat.EMConversation;
import com.zhiyicx.baseproject.base.BaseListBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Catherine
 * @describe 基于环信的bean
 * @date 2017/12/12
 * @contact email:648129313@qq.com
 */

public class MessageItemBeanV2 extends BaseListBean implements Serializable {

    private static final long serialVersionUID = 4454853132357124036L;

    private UserInfoBean userInfo;
    private EMConversation conversation;
    private String emKey;
    private EMConversation.EMConversationType type;
    private List<UserInfoBean> list;
    private ChatGroupBean chatGroupBean;

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public EMConversation getConversation() {
        return conversation;
    }

    public void setConversation(EMConversation conversation) {
        this.conversation = conversation;
    }

    public String getEmKey() {
        return emKey;
    }

    public void setEmKey(String emKey) {
        this.emKey = emKey;
    }

    public EMConversation.EMConversationType getType() {
        return type;
    }

    public void setType(EMConversation.EMConversationType type) {
        this.type = type;
    }

    public List<UserInfoBean> getList() {
        return list;
    }

    public void setList(List<UserInfoBean> list) {
        this.list = list;
    }

    public ChatGroupBean getChatGroupBean() {
        return chatGroupBean;
    }

    public void setChatGroupBean(ChatGroupBean chatGroupBean) {
        this.chatGroupBean = chatGroupBean;
    }


    @Override
    public String toString() {
        return "MessageItemBeanV2{" +
                "userInfo=" + userInfo +
                ", conversation=" + conversation +
                ", emKey='" + emKey + '\'' +
                ", type=" + type +
                ", list=" + list +
                ", chatGroupBean=" + chatGroupBean +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeString(this.emKey);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeTypedList(this.list);
        dest.writeParcelable(this.chatGroupBean, flags);
    }

    public MessageItemBeanV2(String emKey) {
        this.emKey = emKey;
    }

    public MessageItemBeanV2() {
    }

    protected MessageItemBeanV2(Parcel in) {
        super(in);
        this.userInfo = in.readParcelable(UserInfoBean.class.getClassLoader());
        this.conversation = in.readParcelable(EMConversation.class.getClassLoader());
        this.emKey = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : EMConversation.EMConversationType.values()[tmpType];
        this.list = in.createTypedArrayList(UserInfoBean.CREATOR);
        this.chatGroupBean = in.readParcelable(ChatGroupBean.class.getClassLoader());
    }

    public static final Creator<MessageItemBeanV2> CREATOR = new Creator<MessageItemBeanV2>() {
        @Override
        public MessageItemBeanV2 createFromParcel(Parcel source) {
            return new MessageItemBeanV2(source);
        }

        @Override
        public MessageItemBeanV2[] newArray(int size) {
            return new MessageItemBeanV2[size];
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

        MessageItemBeanV2 that = (MessageItemBeanV2) o;

        if (conversation != null ? !conversation.equals(that.conversation) : that.conversation != null) {
            return false;
        }
        return emKey != null ? emKey.equals(that.emKey) : that.emKey == null;
    }

    @Override
    public int hashCode() {
        int result = conversation != null ? conversation.hashCode() : 0;
        result = 31 * result + (emKey != null ? emKey.hashCode() : 0);
        return result;
    }
}

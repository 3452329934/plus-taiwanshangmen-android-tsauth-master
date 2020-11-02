package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.thinksnsplus.data.beans.notify.UnreadNotifySystemBean;

import java.io.Serializable;

/**
 * @Describe 为读书统计总模型  {@see https://slimkit.github.io/plus-docs/v2/core/users/unread}
 * @Author Jungle68
 * @Date 2017/10/23
 * @Contact master.jungle68@gmail.com
 */
public class UnReadNotificaitonBean implements Parcelable , Serializable {
    private static final long serialVersionUID = 2015917468354886163L;
    private UnreadNotifyMsgBean comment;
    private UnreadNotifyMsgBean like;
    private UnreadNotifyMsgBean at;
    private UnreadNotifyMsgBean follow;
    private UnreadNotifySystemBean system;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.comment, flags);
        dest.writeParcelable(this.like, flags);
        dest.writeParcelable(this.at, flags);
        dest.writeParcelable(this.follow, flags);
        dest.writeParcelable(this.system, flags);
    }

    public UnReadNotificaitonBean() {
    }

    protected UnReadNotificaitonBean(Parcel in) {
        this.comment = in.readParcelable(UnreadNotifyMsgBean.class.getClassLoader());
        this.like = in.readParcelable(UnreadNotifyMsgBean.class.getClassLoader());
        this.at = in.readParcelable(UnreadNotifyMsgBean.class.getClassLoader());
        this.follow = in.readParcelable(UnreadNotifyMsgBean.class.getClassLoader());
        this.system = in.readParcelable(UnreadNotifySystemBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<UnReadNotificaitonBean> CREATOR = new Parcelable.Creator<UnReadNotificaitonBean>() {
        @Override
        public UnReadNotificaitonBean createFromParcel(Parcel source) {
            return new UnReadNotificaitonBean(source);
        }

        @Override
        public UnReadNotificaitonBean[] newArray(int size) {
            return new UnReadNotificaitonBean[size];
        }
    };

    public UnreadNotifyMsgBean getComment() {
        return comment;
    }

    public void setComment(UnreadNotifyMsgBean comment) {
        this.comment = comment;
    }

    public UnreadNotifyMsgBean getLike() {
        return like;
    }

    public void setLike(UnreadNotifyMsgBean like) {
        this.like = like;
    }

    public UnreadNotifyMsgBean getAt() {
        return at;
    }

    public void setAt(UnreadNotifyMsgBean at) {
        this.at = at;
    }

    public UnreadNotifyMsgBean getFollow() {
        return follow;
    }

    public void setFollow(UnreadNotifyMsgBean follow) {
        this.follow = follow;
    }

    public UnreadNotifySystemBean getSystem() {
        return system;
    }

    public void setSystem(UnreadNotifySystemBean system) {
        this.system = system;
    }
}

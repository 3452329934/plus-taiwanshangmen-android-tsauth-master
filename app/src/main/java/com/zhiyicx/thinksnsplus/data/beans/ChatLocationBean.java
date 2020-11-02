package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;

import com.amap.api.services.core.LatLonPoint;
import com.zhiyicx.baseproject.base.BaseListBean;

import java.util.Objects;

/**
 * @Author Jliuer
 * @Date 2018/06/12/15:22
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChatLocationBean extends BaseListBean {
    private String title;
    private String name;
    private LatLonPoint mLatLonPoint;
    private String adress;
    private String snippet;
    private String city;
    private boolean hasCheck;

    public boolean hasCheck() {
        return hasCheck;
    }

    public void setHasCheck(boolean hasCheck) {
        this.hasCheck = hasCheck;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLonPoint getLatLonPoint() {
        return mLatLonPoint;
    }

    public void setLatLonPoint(LatLonPoint latLonPoint) {
        mLatLonPoint = latLonPoint;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.name);
        dest.writeParcelable(this.mLatLonPoint, flags);
        dest.writeString(this.adress);
        dest.writeString(this.snippet);
        dest.writeString(this.city);
        dest.writeByte(this.hasCheck ? (byte) 1 : (byte) 0);
    }

    public ChatLocationBean() {
    }

    protected ChatLocationBean(Parcel in) {
        super(in);
        this.title = in.readString();
        this.name = in.readString();
        this.mLatLonPoint = in.readParcelable(LatLonPoint.class.getClassLoader());
        this.adress = in.readString();
        this.snippet = in.readString();
        this.city = in.readString();
        this.hasCheck = in.readByte() != 0;
    }

    public static final Creator<ChatLocationBean> CREATOR = new Creator<ChatLocationBean>() {
        @Override
        public ChatLocationBean createFromParcel(Parcel source) {
            return new ChatLocationBean(source);
        }

        @Override
        public ChatLocationBean[] newArray(int size) {
            return new ChatLocationBean[size];
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

        ChatLocationBean that = (ChatLocationBean) o;
        if (title == null || name == null) {
            return false;
        }
        return title.equals(that.title) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        if (title == null || name == null) {
            return -1;
        }
        int result = title.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}

package com.zhiyicx.thinksnsplus.data.beans.circle;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/12/7
 * @Contact master.jungle68@gmail.com
 */
@Entity
public class CircleSearchHistoryBean implements Parcelable {
    public static final int TYPE_DEFAULT = 0; // 操作按钮，查看全部，展开等
    public static final int TYPE_CIRCLE = 1;
    public static final int TYPE_CIRCLE_POST = 2;
    @Id(autoincrement = true)
    private Long id;
    private Long circleId;// 圈内搜索 记录 圈子 id
    private String content;
    private long create_time;
    private boolean isOutSideCircle;
    private int type = TYPE_DEFAULT;


    public CircleSearchHistoryBean(String content, int type) {
        this.content = content;
        this.type = type;
        this.create_time = System.currentTimeMillis();
    }

    public CircleSearchHistoryBean(String content, int type, boolean isOutSideCircle) {
        this.content = content;
        this.type = type;
        this.isOutSideCircle = isOutSideCircle;
        this.create_time = System.currentTimeMillis();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.circleId);
        dest.writeString(this.content);
        dest.writeLong(this.create_time);
        dest.writeByte(this.isOutSideCircle ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
    }

    protected CircleSearchHistoryBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.circleId = (Long) in.readValue(Long.class.getClassLoader());
        this.content = in.readString();
        this.create_time = in.readLong();
        this.isOutSideCircle = in.readByte() != 0;
        this.type = in.readInt();
    }

    @Generated(hash = 1012122069)
    public CircleSearchHistoryBean(Long id, Long circleId, String content, long create_time, boolean isOutSideCircle, int type) {
        this.id = id;
        this.circleId = circleId;
        this.content = content;
        this.create_time = create_time;
        this.isOutSideCircle = isOutSideCircle;
        this.type = type;
    }

    @Generated(hash = 1365564893)
    public CircleSearchHistoryBean() {
    }

    public static final Parcelable.Creator<CircleSearchHistoryBean> CREATOR = new Parcelable.Creator<CircleSearchHistoryBean>() {
        @Override
        public CircleSearchHistoryBean createFromParcel(Parcel source) {
            return new CircleSearchHistoryBean(source);
        }

        @Override
        public CircleSearchHistoryBean[] newArray(int size) {
            return new CircleSearchHistoryBean[size];
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

        CircleSearchHistoryBean that = (CircleSearchHistoryBean) o;

        if (type != that.type) {
            return false;
        }
        if (circleId != null ? !circleId.equals(that.circleId) : that.circleId != null) {
            return false;
        }
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        int result = circleId != null ? circleId.hashCode() : 0;
        result = 31 * result + content.hashCode();
        result = 31 * result + type;
        return result;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCircleId() {
        return this.circleId;
    }

    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public boolean getIsOutSideCircle() {
        return this.isOutSideCircle;
    }

    public void setOutSideCircle(boolean isOutSideCircle) {
        this.isOutSideCircle = isOutSideCircle;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setIsOutSideCircle(boolean isOutSideCircle) {
        this.isOutSideCircle = isOutSideCircle;
    }
}

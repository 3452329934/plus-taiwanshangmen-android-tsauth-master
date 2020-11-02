package com.zhiyicx.thinksnsplus.data.beans.notify;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResourceableBean implements Parcelable , Serializable {
    private static final long serialVersionUID = -7552331799366951401L;
    /**
         * type : feeds
         * id : 1
         */

        private String type;
        private boolean isDeleted;
        private Long id;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public boolean isDeleted() {
            return isDeleted;
        }

        public void setDeleted(boolean deleted) {
            isDeleted = deleted;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.type);
            dest.writeByte(this.isDeleted ? (byte) 1 : (byte) 0);
            dest.writeValue(this.id);
        }

        public ResourceableBean() {
        }

        protected ResourceableBean(Parcel in) {
            this.type = in.readString();
            this.isDeleted = in.readByte() != 0;
            this.id = (Long) in.readValue(Long.class.getClassLoader());
        }

        public static final Creator<ResourceableBean> CREATOR = new Creator<ResourceableBean>() {
            @Override
            public ResourceableBean createFromParcel(Parcel source) {
                return new ResourceableBean(source);
            }

            @Override
            public ResourceableBean[] newArray(int size) {
                return new ResourceableBean[size];
            }
        };
    }
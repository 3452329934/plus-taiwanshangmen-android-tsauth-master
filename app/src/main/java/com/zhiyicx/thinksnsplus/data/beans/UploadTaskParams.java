package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.common.utils.FileUtils;

import java.io.File;
import java.io.Serializable;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/09/06/15:02
 * @Email Jliuer@aliyun.com
 * @Description 创建上传任务参数
 */
public class UploadTaskParams implements Parcelable, Serializable {

    private static final long serialVersionUID = -3687971688776849616L;

    private String filename;
    private String hash;
    private Long size;
    private String mime_type;
    private Storage storage;

    private String file;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public static class Storage implements Parcelable, Serializable {
        private static final long serialVersionUID = 3541989811429514065L;
        private String channel;

        public static final String CHANNEL_PUBLIC = "public";

        public Storage(String channel) {
            this.channel = channel;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.channel);
        }

        public Storage() {
        }

        protected Storage(Parcel in) {
            this.channel = in.readString();
        }

        public static final Creator<Storage> CREATOR = new Creator<Storage>() {
            @Override
            public Storage createFromParcel(Parcel source) {
                return new Storage(source);
            }

            @Override
            public Storage[] newArray(int size) {
                return new Storage[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filename);
        dest.writeString(this.hash);
        dest.writeValue(this.size);
        dest.writeString(this.mime_type);
        dest.writeParcelable(this.storage, flags);
        dest.writeString(this.file);
    }

    public UploadTaskParams() {
    }

    public UploadTaskParams(String filePath, String channel) {
        File f = new File(filePath);
        hash = FileUtils.getFileMD5ToString(f);
        filename = f.getName();
        size = FileUtils.getFileLength(f);
        mime_type = FileUtils.getMimeTypeByFile(f);
        file = filePath;
        storage = new Storage(channel);
    }

    protected UploadTaskParams(Parcel in) {
        this.filename = in.readString();
        this.hash = in.readString();
        this.size = (Long) in.readValue(Long.class.getClassLoader());
        this.mime_type = in.readString();
        this.storage = in.readParcelable(Storage.class.getClassLoader());
        this.file = in.readString();
    }

    public static final Creator<UploadTaskParams> CREATOR = new Creator<UploadTaskParams>() {
        @Override
        public UploadTaskParams createFromParcel(Parcel source) {
            return new UploadTaskParams(source);
        }

        @Override
        public UploadTaskParams[] newArray(int size) {
            return new UploadTaskParams[size];
        }
    };
}

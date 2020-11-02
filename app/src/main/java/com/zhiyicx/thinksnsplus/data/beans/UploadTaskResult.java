package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/09/06/15:02
 * @Email Jliuer@aliyun.com
 * @Description 创建上传任务返回数据
 */
public class UploadTaskResult implements Parcelable, Serializable {

    private static final long serialVersionUID = -8709260479427860541L;
    /**
     * uri : // 上传文件的请求地址
     * method : PUT  // 上传文件的请求方式
     * headers : {"Content-Disposition":"attachment;filename=demo.png","Content-Md5":"UC57soIyuC7SPBgHe47MNA==","Content-Length":802930,"Content-Type":"image/png"}
     * form : null  // 上传时候的表单，如果是 NULL 则表示整个 Body 是二进制文件流，如果是对象，则构造 `application/form-data` 表单对象
     * file_key : null  // 如果存在 `form` 表单信息，文件流所使用的 key 名称
     * node : // 文件上传完成后所使用的文件节点
     */

    private String uri;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> form;
    private String file_key;
    private String node;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getFile_key() {
        return file_key;
    }

    public void setFile_key(String file_key) {
        this.file_key = file_key;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getForm() {
        return form;
    }

    public void setForm(Map<String, String> form) {
        this.form = form;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uri);
        dest.writeString(this.method);
        dest.writeInt(this.headers.size());
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeInt(this.form.size());
        for (Map.Entry<String, String> entry : this.form.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeString(this.file_key);
        dest.writeString(this.node);
    }

    public UploadTaskResult() {
    }

    public UploadTaskResult(String node) {
        this.node = node;
    }

    protected UploadTaskResult(Parcel in) {
        this.uri = in.readString();
        this.method = in.readString();
        int headersSize = in.readInt();
        this.headers = new HashMap<String, String>(headersSize);
        for (int i = 0; i < headersSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.headers.put(key, value);
        }
        int formSize = in.readInt();
        this.form = new HashMap<String, String>(formSize);
        for (int i = 0; i < formSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.form.put(key, value);
        }
        this.file_key = in.readString();
        this.node = in.readString();
    }

    public static final Creator<UploadTaskResult> CREATOR = new Creator<UploadTaskResult>() {
        @Override
        public UploadTaskResult createFromParcel(Parcel source) {
            return new UploadTaskResult(source);
        }

        @Override
        public UploadTaskResult[] newArray(int size) {
            return new UploadTaskResult[size];
        }
    };
}

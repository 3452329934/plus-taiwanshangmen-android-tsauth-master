package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.thinksnsplus.data.source.local.data_convert.BaseConvert;
import com.zhiyicx.thinksnsplus.data.source.local.data_convert.IntegerListConvert;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/01/14:41
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Entity
public class InfoDraftBean extends BaseDraftBean implements Parcelable, Serializable {

    private static final long serialVersionUID = -703234318472876036L;

    @Convert(converter = InfoDraftConvert.class, columnType = String.class)
    private InfoPublishBean mInfoPublishBean;

    @Id
    private Long mark;
    private String html;
    private String title;
    private String content;
    private String create_at;
    private String updated_at;

    /**
     * 记录上传成功的照片 键值对：时间戳(唯一) -- 图片地址
     */
    @Convert(converter = HashMapConvert.class, columnType = String.class)
    private HashMap<Long, String> mInsertedImages;

    /**
     * 记录上传失败的照片 同上
     */
    @Convert(converter = HashMapConvert.class, columnType = String.class)
    private HashMap<Long, String> mFailedImages;

    /**
     * 上传图片成功后返回的id
     */
    @Convert(columnType = String.class, converter = IntegerListConvert.class)
    private List<Integer> mImages;

    public InfoPublishBean getInfoPublishBean() {
        return mInfoPublishBean;
    }

    public void setInfoPublishBean(InfoPublishBean infoPublishBean) {
        mInfoPublishBean = infoPublishBean;
    }

    static class InfoDraftConvert extends BaseConvert<InfoPublishBean> {
    }

    static class HashMapConvert extends BaseConvert<HashMap<Long, String>> {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mInfoPublishBean, flags);
        dest.writeValue(this.mark);
        dest.writeString(this.html);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.create_at);
        dest.writeString(this.updated_at);
        dest.writeSerializable(this.mInsertedImages);
        dest.writeSerializable(this.mFailedImages);
        dest.writeList(this.mImages);
    }

    public InfoPublishBean getMInfoPublishBean() {
        return this.mInfoPublishBean;
    }

    public void setMInfoPublishBean(InfoPublishBean mInfoPublishBean) {
        this.mInfoPublishBean = mInfoPublishBean;
    }

    public Long getMark() {
        return this.mark;
    }

    public void setMark(Long mark) {
        this.mark = mark;
    }

    public String getHtml() {
        return this.html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_at() {
        return this.create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public HashMap<Long, String> getMInsertedImages() {
        return this.mInsertedImages;
    }

    public void setInsertedImages(HashMap<Long, String> mInsertedImages) {
        this.mInsertedImages = mInsertedImages;
    }

    public HashMap<Long, String> getMFailedImages() {
        return this.mFailedImages;
    }

    public void setFailedImages(HashMap<Long, String> mFailedImages) {
        this.mFailedImages = mFailedImages;
    }

    public List<Integer> getMImages() {
        return this.mImages;
    }

    public void setImages(List<Integer> mImages) {
        this.mImages = mImages;
    }

    public void setMInsertedImages(HashMap<Long, String> mInsertedImages) {
        this.mInsertedImages = mInsertedImages;
    }

    public void setMFailedImages(HashMap<Long, String> mFailedImages) {
        this.mFailedImages = mFailedImages;
    }

    public void setMImages(List<Integer> mImages) {
        this.mImages = mImages;
    }

    public InfoDraftBean() {
    }



    protected InfoDraftBean(Parcel in) {
        super(in);
        this.mInfoPublishBean = in.readParcelable(InfoPublishBean.class.getClassLoader());
        this.mark = (Long) in.readValue(Long.class.getClassLoader());
        this.html = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.create_at = in.readString();
        this.updated_at = in.readString();
        this.mInsertedImages = (HashMap<Long, String>) in.readSerializable();
        this.mFailedImages = (HashMap<Long, String>) in.readSerializable();
        this.mImages = new ArrayList<Integer>();
        in.readList(this.mImages, Integer.class.getClassLoader());
    }

    @Generated(hash = 1003948624)
    public InfoDraftBean(InfoPublishBean mInfoPublishBean, Long mark, String html,
            String title, String content, String create_at, String updated_at,
            HashMap<Long, String> mInsertedImages, HashMap<Long, String> mFailedImages,
            List<Integer> mImages) {
        this.mInfoPublishBean = mInfoPublishBean;
        this.mark = mark;
        this.html = html;
        this.title = title;
        this.content = content;
        this.create_at = create_at;
        this.updated_at = updated_at;
        this.mInsertedImages = mInsertedImages;
        this.mFailedImages = mFailedImages;
        this.mImages = mImages;
    }

    public static final Creator<InfoDraftBean> CREATOR = new Creator<InfoDraftBean>() {
        @Override
        public InfoDraftBean createFromParcel(Parcel source) {
            return new InfoDraftBean(source);
        }

        @Override
        public InfoDraftBean[] newArray(int size) {
            return new InfoDraftBean[size];
        }
    };
}

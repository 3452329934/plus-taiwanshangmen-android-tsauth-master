package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.baseproject.base.BaseListBean;

import java.io.Serializable;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/23/10:54
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SimpleAnswerBean extends BaseListBean implements Serializable{


    private static final long serialVersionUID = -6973400168261172570L;
    /**
     * id : 1
     * body : haha

     * question : {"id":1,"subject":"问题1"}
     */

    private Long id;
    private String body;
    private QuestionBean question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public QuestionBean getQuestion() {
        return question;
    }

    public void setQuestion(QuestionBean question) {
        this.question = question;
    }

    public static class QuestionBean implements Parcelable,Serializable{
        private static final long serialVersionUID = 1023519301857126998L;
        /**
         * id : 1
         * subject : 问题1
         */

        private Long id;
        private String subject;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.id);
            dest.writeString(this.subject);
        }

        public QuestionBean() {
        }

        protected QuestionBean(Parcel in) {
            this.id = (Long) in.readValue(Long.class.getClassLoader());
            this.subject = in.readString();
        }

        public static final Creator<QuestionBean> CREATOR = new Creator<QuestionBean>() {
            @Override
            public QuestionBean createFromParcel(Parcel source) {
                return new QuestionBean(source);
            }

            @Override
            public QuestionBean[] newArray(int size) {
                return new QuestionBean[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.id);
        dest.writeString(this.body);
        dest.writeParcelable(this.question, flags);
    }

    public SimpleAnswerBean() {
    }

    protected SimpleAnswerBean(Parcel in) {
        super(in);
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.body = in.readString();
        this.question = in.readParcelable(QuestionBean.class.getClassLoader());
    }

    public static final Creator<SimpleAnswerBean> CREATOR = new Creator<SimpleAnswerBean>() {
        @Override
        public SimpleAnswerBean createFromParcel(Parcel source) {
            return new SimpleAnswerBean(source);
        }

        @Override
        public SimpleAnswerBean[] newArray(int size) {
            return new SimpleAnswerBean[size];
        }
    };
}

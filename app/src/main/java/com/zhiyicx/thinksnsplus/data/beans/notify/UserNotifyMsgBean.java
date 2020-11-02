package com.zhiyicx.thinksnsplus.data.beans.notify;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.baseproject.base.BaseListBean;

import java.io.Serializable;
import java.util.List;

/**
 * ThinkSNS Plus
 * Copyright (c) 2019 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author icepring
 * @Date 2019/02/26/9:36
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class UserNotifyMsgBean implements Serializable, Parcelable {

    private static final long serialVersionUID = 2500175062119317065L;
    /**
     * data : [{"id":"65287e1d-ee47-48c4-ba4f-a84aeda91432","created_at":"2019-02-26T01:28:09Z","read_at":null,"data":{"type":"reward","sender":{"id":16,"name":"用户9527"},"amount":1,"unit":"settings.currency_name"}}]
     * links : {"first":"http://test-plus.zhibocloud.cn/api/v2/user/notifications?type=system&page=1","last":"http://test-plus.zhibocloud.cn/api/v2/user/notifications?type=system&page=1","prev":null,"next":null}
     * meta : {"current_page":1,"from":1,"last_page":1,"path":"http://test-plus.zhibocloud.cn/api/v2/user/notifications","per_page":15,"to":1,"total":1}
     */

    private LinksBean links;
    private MetaBean meta;
    private List<DataBeanX> data;

    public LinksBean getLinks() {
        return links;
    }

    public void setLinks(LinksBean links) {
        this.links = links;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public List<DataBeanX> getData() {
        return data;
    }

    public void setData(List<DataBeanX> data) {
        this.data = data;
    }

    public static class LinksBean implements Parcelable, Serializable {
        private static final long serialVersionUID = 7513030107043496247L;
        /**
         * first : http://test-plus.zhibocloud.cn/api/v2/user/notifications?type=system&page=1
         * last : http://test-plus.zhibocloud.cn/api/v2/user/notifications?type=system&page=1
         * prev : null
         * next : null
         */

        private String first;
        private String last;
        private String prev;
        private String next;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getPrev() {
            return prev;
        }

        public void setPrev(String prev) {
            this.prev = prev;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.first);
            dest.writeString(this.last);
            dest.writeString(this.prev);
            dest.writeString(this.next);
        }

        public LinksBean() {
        }

        protected LinksBean(Parcel in) {
            this.first = in.readString();
            this.last = in.readString();
            this.prev = in.readString();
            this.next = in.readString();
        }

        public static final Creator<LinksBean> CREATOR = new Creator<LinksBean>() {
            @Override
            public LinksBean createFromParcel(Parcel source) {
                return new LinksBean(source);
            }

            @Override
            public LinksBean[] newArray(int size) {
                return new LinksBean[size];
            }
        };
    }

    public static class MetaBean implements Parcelable, Serializable {
        private static final long serialVersionUID = -4665243932196689861L;
        /**
         * current_page : 1
         * from : 1
         * last_page : 1
         * path : http://test-plus.zhibocloud.cn/api/v2/user/notifications
         * per_page : 15
         * to : 1
         * total : 1
         */

        private int current_page;
        private int from;
        private int last_page;
        private String path;
        private int per_page;
        private int to;
        private int total;

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getLast_page() {
            return last_page;
        }

        public void setLast_page(int last_page) {
            this.last_page = last_page;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.current_page);
            dest.writeInt(this.from);
            dest.writeInt(this.last_page);
            dest.writeString(this.path);
            dest.writeInt(this.per_page);
            dest.writeInt(this.to);
            dest.writeInt(this.total);
        }

        public MetaBean() {
        }

        protected MetaBean(Parcel in) {
            this.current_page = in.readInt();
            this.from = in.readInt();
            this.last_page = in.readInt();
            this.path = in.readString();
            this.per_page = in.readInt();
            this.to = in.readInt();
            this.total = in.readInt();
        }

        public static final Creator<MetaBean> CREATOR = new Creator<MetaBean>() {
            @Override
            public MetaBean createFromParcel(Parcel source) {
                return new MetaBean(source);
            }

            @Override
            public MetaBean[] newArray(int size) {
                return new MetaBean[size];
            }
        };
    }

    public static class DataBeanX extends BaseListBean implements Parcelable, Serializable {
        private static final long serialVersionUID = 2239008016969584783L;
        /**
         * id : 65287e1d-ee47-48c4-ba4f-a84aeda91432
         * created_at : 2019-02-26T01:28:09Z
         * read_at : null
         * data : {"type":"reward","sender":{"id":16,"name":"用户9527"},"amount":1,"unit":"settings.currency_name"}
         */

        private String id;
        private String created_at;
        private String read_at;
        private DataBean data;

        /**
         * at的数据类型
         */
        private String at_type;

        public String getAt_type() {
            return at_type;
        }

        public void setAt_type(String at_type) {
            this.at_type = at_type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getRead_at() {
            return read_at;
        }

        public void setRead_at(String read_at) {
            this.read_at = read_at;
        }

        public DataBean getData() {
            return data;
        }



        public void setData(DataBean data) {
            this.data = data;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.created_at);
            dest.writeString(this.read_at);
            dest.writeParcelable(this.data, flags);
        }

        public DataBeanX() {
        }

        protected DataBeanX(Parcel in) {
            this.id = in.readString();
            this.created_at = in.readString();
            this.read_at = in.readString();
            this.data = in.readParcelable(DataBean.class.getClassLoader());
        }

        public static final Creator<DataBeanX> CREATOR = new Creator<DataBeanX>() {
            @Override
            public DataBeanX createFromParcel(Parcel source) {
                return new DataBeanX(source);
            }

            @Override
            public DataBeanX[] newArray(int size) {
                return new DataBeanX[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.links, flags);
        dest.writeParcelable(this.meta, flags);
        dest.writeTypedList(this.data);
    }

    public UserNotifyMsgBean() {
    }

    protected UserNotifyMsgBean(Parcel in) {
        this.links = in.readParcelable(LinksBean.class.getClassLoader());
        this.meta = in.readParcelable(MetaBean.class.getClassLoader());
        this.data = in.createTypedArrayList(DataBeanX.CREATOR);
    }

    public static final Creator<UserNotifyMsgBean> CREATOR = new Creator<UserNotifyMsgBean>() {
        @Override
        public UserNotifyMsgBean createFromParcel(Parcel source) {
            return new UserNotifyMsgBean(source);
        }

        @Override
        public UserNotifyMsgBean[] newArray(int size) {
            return new UserNotifyMsgBean[size];
        }
    };
}

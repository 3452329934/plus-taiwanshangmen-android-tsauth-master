package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jungle68
 * @describe
 * @date 2018/4/16
 * @contact master.jungle68@gmail.com
 */
public class UserFollowerCountBean implements Parcelable{

    /**
     * user : {"following":1}
     */

    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    /**
     * 消息通知，统计最新未读数
     */
    public static class UserBean implements Parcelable{
        public static final String MESSAGE_TYPE_FOLLOWING = "following";
        public static final String MESSAGE_TYPE_MUTUAL = "mutual";
        public static final String MESSAGE_TYPE_LIKED = "liked";
        public static final String MESSAGE_TYPE_COMMENTED = "commented";
        public static final String MESSAGE_TYPE_AT = "at";
        public static final String MESSAGE_TYPE_SYSTEM = "system";
        public static final String MESSAGE_TYPE_NEWS_COMMENT_PINNED = "news-comment-pinned";
        public static final String MESSAGE_TYPE_FEED_COMMENT_PINNED = "feed-comment-pinned";
        public static final String MESSAGE_TYPE_POST_PINNED = "post-pinned";
        public static final String MESSAGE_TYPE_POST_COMMENT_PINNED = "post-comment-pinned";
        public static final String MESSAGE_TYPE_GROUP_JOIN_PINNED = "group-join-pinned";
        public static final String MESSAGE_TYPE_GROUP_POST_PINNED = "group-post-pinned";

        /**
         * following : 1 最新粉丝数
         * liked : 1   最新点赞数
         * commented : 1 最新评论数
         * system : 1  最新系统消息数
         * news-comment-pinned : 1  最新资讯评论置顶数
         * feed-comment-pinned：1   最新动态评论置顶数
         * mutual：1   最新好友数
         */

        private int following;
        private int liked;
        private int commented;
        private int system;
        @SerializedName("news-comment-pinned")
        private int newsCommentPinned;
        @SerializedName("feed-comment-pinned")
        private int feedCommentPinned;
        @SerializedName("post-pinned")
        private int postPinned;
        @SerializedName("post-comment-pinned")
        private int postCommentPinned;
        @SerializedName("group-join-pinned")
        private int groupJoinPinned;
        private int at;

        private int mutual;

        public int getAt() {
            return at;
        }

        public void setAt(int at) {
            this.at = at;
        }

        public int getMutual() {
            return mutual;
        }

        public void setMutual(int mutual) {
            this.mutual = mutual;
        }

        public int getFollowing() {
            return following;
        }

        public int getLiked() {
            return liked;
        }

        public void setLiked(int liked) {
            this.liked = liked;
        }

        public int getCommented() {
            return commented;
        }

        public void setCommented(int commented) {
            this.commented = commented;
        }

        public int getSystem() {
            return system;
        }

        public void setSystem(int system) {
            this.system = system;
        }

        public int getNewsCommentPinned() {
            return newsCommentPinned;
        }

        public void setNewsCommentPinned(int newsCommentPinned) {
            this.newsCommentPinned = newsCommentPinned;
        }

        public int getFeedCommentPinned() {
            return feedCommentPinned;
        }

        public void setFeedCommentPinned(int feedCommentPinned) {
            this.feedCommentPinned = feedCommentPinned;
        }

        public void setFollowing(int following) {
            this.following = following;
        }

        public int getPostPinned() {
            return postPinned;
        }

        public void setPostPinned(int postPinned) {
            this.postPinned = postPinned;
        }

        public int getPostCommentPinned() {
            return postCommentPinned;
        }

        public void setPostCommentPinned(int postCommentPinned) {
            this.postCommentPinned = postCommentPinned;
        }

        public int getGroupJoinPinned() {
            return groupJoinPinned;
        }

        public void setGroupJoinPinned(int groupJoinPinned) {
            this.groupJoinPinned = groupJoinPinned;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.following);
            dest.writeInt(this.liked);
            dest.writeInt(this.commented);
            dest.writeInt(this.system);
            dest.writeInt(this.newsCommentPinned);
            dest.writeInt(this.feedCommentPinned);
            dest.writeInt(this.postPinned);
            dest.writeInt(this.postCommentPinned);
            dest.writeInt(this.groupJoinPinned);
            dest.writeInt(this.at);
            dest.writeInt(this.mutual);
        }

        public UserBean() {
        }

        protected UserBean(Parcel in) {
            this.following = in.readInt();
            this.liked = in.readInt();
            this.commented = in.readInt();
            this.system = in.readInt();
            this.newsCommentPinned = in.readInt();
            this.feedCommentPinned = in.readInt();
            this.postPinned = in.readInt();
            this.postCommentPinned = in.readInt();
            this.groupJoinPinned = in.readInt();
            this.at = in.readInt();
            this.mutual = in.readInt();
        }

        public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
            @Override
            public UserBean createFromParcel(Parcel source) {
                return new UserBean(source);
            }

            @Override
            public UserBean[] newArray(int size) {
                return new UserBean[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
    }

    public UserFollowerCountBean() {
    }

    protected UserFollowerCountBean(Parcel in) {
        this.user = in.readParcelable(UserBean.class.getClassLoader());
    }

    public static final Creator<UserFollowerCountBean> CREATOR = new Creator<UserFollowerCountBean>() {
        @Override
        public UserFollowerCountBean createFromParcel(Parcel source) {
            return new UserFollowerCountBean(source);
        }

        @Override
        public UserFollowerCountBean[] newArray(int size) {
            return new UserFollowerCountBean[size];
        }
    };
}

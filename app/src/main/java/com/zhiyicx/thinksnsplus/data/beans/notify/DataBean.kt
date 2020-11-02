package com.zhiyicx.thinksnsplus.data.beans.notify

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.zhiyicx.baseproject.config.ApiConfig.APP_LIKE_FEED
import com.zhiyicx.thinksnsplus.data.beans.AnswerInfoBean
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean
import com.zhiyicx.thinksnsplus.data.beans.qa.QATopicBean
import com.zhiyicx.thinksnsplus.data.source.repository.i.INotificationRepository
import java.io.Serializable

class DataBean() : Parcelable, Serializable {
    /**
     * type : reward
     * sender : {"id":16,"name":"用户9527"}
     * amount : 1
     * unit : settings.currency_name
     */

    var type: String? = null
    @SerializedName(alternate = arrayOf("contents", "message", "subject", "report"), value = "content")
    private var content: String? = null
    var state: String? = null
    var sender: SenderBean? = null
    var group: SenderBean? = null
    var user: SenderBean? = null
    var question: SenderBean? = null
    var answer: SenderBean? = null
    var news: SenderBean? = null
    val comment: SenderBean? = null
    val post: SenderBean? = null
    val feed: SenderBean? = null
    /*这个有点坑，这个是申请问题精选返回的字段*/
    @SerializedName(alternate = arrayOf("application"), value = "qa")
    var qa: AnswerInfoBean? = null
    @SerializedName(alternate = arrayOf("topic_application"), value = "topic")
    var topic: QATopicBean? = null
    var amount: Int = 0
    var feed_id: Long? = null
    val group_id: Long? = null
    var unit: String? = null
    private var hasReply: Boolean = false

    @SerializedName(value = "commentable", alternate = arrayOf("resource"))
    var resource: ResourceableBean? = null

    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
        content = parcel.readString()
        state = parcel.readString()
        sender = parcel.readParcelable(SenderBean::class.java.classLoader)
        group = parcel.readParcelable(SenderBean::class.java.classLoader)
        user = parcel.readParcelable(SenderBean::class.java.classLoader)
        question = parcel.readParcelable(SenderBean::class.java.classLoader)
        answer = parcel.readParcelable(SenderBean::class.java.classLoader)
        news = parcel.readParcelable(SenderBean::class.java.classLoader)
        amount = parcel.readInt()
        feed_id = parcel.readValue(Long::class.java.classLoader) as? Long
        unit = parcel.readString()
        hasReply = parcel.readByte() != 0.toByte()
        resource = parcel.readParcelable(ResourceableBean::class.java.classLoader)
    }

    fun getContent(): String? {
        var realContent: String? = ""
        if (!TextUtils.isEmpty(type)) {
            realContent = when (type) {
                /**
                 * 关注
                 */
                INotificationRepository.FOLLOW -> sender?.name + "关注了你"
                /** 动态打赏*/
                INotificationRepository.REWARD_FEEDS -> sender?.name + "打赏了你的动态"
                /**帖子打赏*/
                INotificationRepository.GROUP_POST_REWARD -> sender?.name + "打赏了你的帖子「" + post?.name + "」"
                /**资讯打赏*/
                INotificationRepository.REWARD_NEWS -> "你的资讯《" + news?.name + "》被" + sender?.name + "打赏了" + amount + unit
                /*用户打赏*/
                INotificationRepository.REWARD_USER -> sender?.name + "打赏了你"
                /*回答打赏*/
                INotificationRepository.QA_ANSWER_REWARD -> sender?.name + "打赏了你的回答"
                /*动态评论置顶*/
                INotificationRepository.PINNED_FEED_COMMENT -> if (INotificationRepository.CERTIFICATION_REJECTED == state) {
                    "你的动态评论「" + comment?.name + "」的置顶请求被拒绝"
                } else {
                    "你的动态评论「" + comment?.name + "」的置顶请求已通过"
                }
                /** 动态置顶*/
                INotificationRepository.PINNED_FEED -> if (state == INotificationRepository.CERTIFICATION_REJECTED) {
                    "你申请的动态置顶未通过"
                } else if (state == INotificationRepository.CERTIFICATION_PASSED) {
                    "你申请的动态置顶已通过"
                } else {
                    "你的动态被管理员设置为置顶"
                }
                /** 资讯评论置顶*/
                INotificationRepository.PINNED_NEWS_COMMENT -> if (INotificationRepository.CERTIFICATION_REJECTED == state) {
                    "你对资讯《" + news?.name + "》的评论「" + comment?.name + "」的置顶请求被拒绝"
                } else {
                    "你对资讯《" + news?.name + "》的评论「" + comment?.name + "」的置顶请求已通过"
                }
                /** 圈子评论置顶*/
                INotificationRepository.GROUP_COMMENT_PINNED -> if (INotificationRepository.CERTIFICATION_REJECTED == state) {
                    "你的评论置顶请求被拒绝"
                } else {
                    "你的评论置顶请求已通过"
                }
                INotificationRepository.GROUP_SEND_COMMENT_PINNED -> "用户在你的帖子「" + post?.name + "」下申请评论置顶"
                /** 帖子置顶*/
                INotificationRepository.GROUP_POST_PINNED -> if (!isRejected()) {
                    "你的帖子「" + post?.name + "」的置顶请求已通过"
                } else {
                    "你的帖子「" + post?.name + "」的置顶请求被拒绝"
                }
                /*帖子被管理员置顶*/
                INotificationRepository.GROUP_PINNED_ADMIN -> content
                /** 加入圈子申请*/
                INotificationRepository.GROUP_JOIN -> if (INotificationRepository.CERTIFICATION_REJECTED == state) {
                    "你被拒绝加入「" + group?.name + "」圈子"
                } else if (INotificationRepository.CERTIFICATION_PASSED == state) {
                    "你被同意加入「" + group?.name + "」圈子"
                } else {
                    user?.name + "请求加入「" + group?.name + "」圈子"
                }
                /**圈子转让*/
                INotificationRepository.GROUP_TRANSFORM -> user?.name + "将圈子「" + group?.name + " 」转让给你"
                /**回答采纳*/
                INotificationRepository.QA_ANSWER_ADOPTION, INotificationRepository.QA_ANSWER_ADOPTION_1 -> "你提交的问题回答被采纳"
                /** 邀请回答*/
                INotificationRepository.QA_INVITATION -> sender?.name + "邀请你回答问题「" + question?.name + "」"
                /**认证审核*/
                INotificationRepository.USER_CERTIFICATION -> if (INotificationRepository.CERTIFICATION_REJECTED == state) {
                    "你申请的身份认证已被驳回，驳回理由：" + content
                } else {
                    "你申请的身份认证已通过"
                }
                /**提现申请*/
                INotificationRepository.USER_CASH -> if (INotificationRepository.CERTIFICATION_REJECTED == state) "申请提现失败" else {
                    "申请提现成功"
                }
                /*积分提现*/
                INotificationRepository.USER_CURRENCY_CASH -> if (!isRejected()) "你申请的积分提现已通过" else {
                    "你申请的积分提现已被驳回，驳回理由：" + content
                }
                /*举报*/
                INotificationRepository.REPORT -> "你举报的" +
                        when (resource?.type) {
                            INotificationRepository.REPORT_USER -> if (isRejected()) "用户「$content 」被驳回" else "用户「$content 」平台已处理"
                            INotificationRepository.REPORT_TOPIC -> if (isRejected()) "话题「$content 」被驳回" else "话题「$content 」平台已处理"
                            INotificationRepository.REPORT_COMMENT -> if (isRejected()) "评论「$content 」被驳回" else "评论「$content 」平台已处理"
                            INotificationRepository.REPORT_QUESTION -> if (isRejected()) "问题「$content 」被驳回" else "问题「$content 」平台已处理"
                            INotificationRepository.REPORT_FEED -> if (isRejected()) "动态「$content 」被驳回" else "动态「$content 」平台已处理"
                            INotificationRepository.REPORT_NEWS -> if (isRejected()) "资讯「$content 」被驳回" else "资讯「$content 」平台已处理"
                            INotificationRepository.REPORT_POSTS -> if (isRejected()) "帖子「$content 」被驳回" else "帖子「$content 」平台已处理"
                            INotificationRepository.REPORT_GROUPS -> if (isRejected()) "圈子「$content 」被驳回" else "圈子「$content 」平台已处理"
                            INotificationRepository.REPORT_ANSWER -> if (isRejected()) "回答「$content 」被驳回" else "回答「$content 」平台已处理"
                            INotificationRepository.REPORT_GROUPS_POST -> if (isRejected()) "「$content 」被驳回" else "「$content 」平台已处理"
                            else -> if (isRejected()) "「$content 」被驳回" else "「$content 」平台已处理"
                        }
                /*创建的专题*/
                INotificationRepository.QA_TOPIC_ACCEPT -> "你申请创建的专题「" + topic?.name + " 」已通过"
                INotificationRepository.QA_TOPIC_REJECT -> "你申请创建的专题「" + topic?.name + " 」已被驳回"
                /*帖子有新的举报*/
                INotificationRepository.GROUP_REPORT_POST -> "${sender?.name}举报了你的圈子「${group?.name} 」下的帖子「${post?.name} 」"
                INotificationRepository.REPORT_GROUPS_POST_COMMENT -> "${sender?.name}举报了你的圈子「${group?.name} 」下的帖子「${post?.name} 」下的评论「${post?.name} 」"
                /*圈子举报*/
                INotificationRepository.GROUP_REPORT -> if (!isRejected()) "你举报的圈子内容「$content 」平台已处理" else "你举报的圈子内容「$content 」被驳回"
                INotificationRepository.GROUP_MEMBER -> content
                /*圈子成员退出圈子*/
                INotificationRepository.GROUP_EXIT -> user?.name+"退出了圈子「"+group?.name + "」"
                INotificationRepository.NEW_REJECT -> content
                INotificationRepository.QA_EXCELLENT_ACCEPT -> "你的申请的问题「${qa?.question?.subject} 」加精已通过"
                INotificationRepository.QA_EXCELLENT_REJECT ->  "你的申请的问题「${qa?.question?.subject} 」加精已被驳回"
                INotificationRepository.DELETE_FEED_COMMENT -> "你的动态评论「" + comment?.name + "」被管理员删除"
                else -> if (null != resource) {
                    when (resource?.type) {
                        APP_LIKE_FEED -> if (hasReply) {
                            sender?.name + "回复你：" + content
                        } else {
                            sender?.name + "评论了你的动态：" + content
                        }
                        else -> {content
                        }
                    }//                            case APP_LIKE_GROUP_POST:
                    //                            case APP_LIKE_MUSIC:
                    //                            case APP_LIKE_MUSIC_SPECIALS:
                    //                            case APP_LIKE_NEWS:
                    //                            case ApiConfig.APP_QUESTIONS:
                    //                            case ApiConfig.APP_QUESTIONS_ANSWER:
                }else{
                    content
                }

            }
        }
        return if (TextUtils.isEmpty(realContent)) content else realContent
    }

    fun isRejected(): Boolean = INotificationRepository.CERTIFICATION_REJECTED == state;

    fun hasReply(): Boolean {
        return hasReply
    }


    fun setHasReply(hasReply: Boolean) {
        this.hasReply = hasReply
    }

    fun setContent(content: String) {
        this.content = content
    }

    class SenderBean() : Serializable, Parcelable {


        /**
         * id : 16
         * name : 用户9527
         */

        var id: Long? = null
        @SerializedName(alternate = arrayOf("subject", "contents", "body", "title"), value = "name")
        var name: String? = null
        var text_body: String? = null
        @SerializedName(alternate = arrayOf("user"), value = "mUserInfoBean")
        var userInfoBean: UserInfoBean? = null

        constructor(parcel: Parcel) : this() {
            id = parcel.readValue(Long::class.java.classLoader) as? Long
            name = parcel.readString()
            text_body = parcel.readString()
            userInfoBean = parcel.readParcelable(UserInfoBean::class.java.classLoader)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(id)
            parcel.writeString(name)
            parcel.writeString(text_body)
            parcel.writeParcelable(userInfoBean, flags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SenderBean> {
            override fun createFromParcel(parcel: Parcel): SenderBean {
                return SenderBean(parcel)
            }

            override fun newArray(size: Int): Array<SenderBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(content)
        parcel.writeString(state)
        parcel.writeParcelable(sender, flags)
        parcel.writeParcelable(group, flags)
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(question, flags)
        parcel.writeParcelable(answer, flags)
        parcel.writeParcelable(news, flags)
        parcel.writeInt(amount)
        parcel.writeValue(feed_id)
        parcel.writeString(unit)
        parcel.writeByte(if (hasReply) 1 else 0)
        parcel.writeParcelable(resource, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataBean> {
        override fun createFromParcel(parcel: Parcel): DataBean {
            return DataBean(parcel)
        }

        override fun newArray(size: Int): Array<DataBean?> {
            return arrayOfNulls(size)
        }
    }
}
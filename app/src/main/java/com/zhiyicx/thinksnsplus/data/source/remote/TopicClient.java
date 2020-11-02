package com.zhiyicx.thinksnsplus.data.source.remote;

import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.ReportResultBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_TOPICDETAIL;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_TOPIC_DYNAMIC;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_TOPIC_PARTICIPANTS;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_MODIFY_TOPICS;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_REPORT_TOPIC;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_TOPICS;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/17:31
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface TopicClient {

    /**
     * 获取话题列表
     *
     * @param query     搜索关键词，允许任何字符串。
     * @param direction 用于基于数据 id 字段的排序方向设置，允许 asc 或者 desc，默认 desc
     * @param limit     本次请求请求的数据条数，默认 15 条，允许的范围 1 - 100。
     * @param index     数据查询定位值，来源于数据 id 字段。
     * @param only      可选，该字段只有一个固定值 hot，当出现 only 值的时候其他参数全部失效，转而 API 只返回热门话题数据。
     * @return
     */
    @GET(APP_PATH_TOPICS)
    Observable<List<TopicListBean>> getTopicListBean(@Query("only") String only, @Query("q") String query, @Query("direction") String direction,
                                                     @Query("limit") Integer limit, @Query("index") Long index);

    @GET(APP_PATH_GET_TOPIC_DYNAMIC)
    Observable<DynamicBeanV2> getTopicDynamicListBean(@Path("topic_id") Long topicId, @Query("direction") String direction,
                                                      @Query("limit") Integer limit, @Query("index") Long index);

    @GET(APP_PATH_GET_TOPIC_DYNAMIC)
    Observable<List<DynamicDetailBeanV2>> getTopicDynamicListBeanV2(@Path("topic_id") Long topicId, @Query("direction") String direction,
                                                                    @Query("limit") Integer limit, @Query("index") Long index);

    @FormUrlEncoded
    @POST(APP_PATH_TOPICS)
    Observable<BaseJsonV2<Integer>> createTopic(@Field("name") String name, @Field("desc") String desc, @Field("logo") String logo);

    @GET(APP_PATH_GET_TOPICDETAIL)
    Observable<TopicDetailBean> getTopicDetailBean(@Path("topic_id") Long topicId);

    @FormUrlEncoded
    @PATCH(APP_PATH_MODIFY_TOPICS)
    Observable<Object> modifyTopic(@Path("topic_id") Long topicId, @Field("desc") String desc, @Field("logo") String logo);

    @GET(APP_PATH_GET_TOPIC_PARTICIPANTS)
    Observable<List<Integer>> getParticipants(@Path("topic_id") Long topicId, @Query("limit") Integer limit, @Query("offset") Integer offset);

    @PUT(APP_PATH_REPORT_TOPIC)
    Observable<ReportResultBean> reportTopic(@Path("topic_id") String topicId, @Body Map<String,String> message);
}

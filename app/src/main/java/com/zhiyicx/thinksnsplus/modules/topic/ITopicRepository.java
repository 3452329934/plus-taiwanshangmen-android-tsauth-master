package com.zhiyicx.thinksnsplus.modules.topic;

import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.IDynamicReppsitory;

import java.util.List;

import retrofit2.http.Path;
import rx.Observable;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/23/18:10
 * @Email Jliuer@aliyun.com
 * @Description feed-topic net request
 */
public interface ITopicRepository extends IDynamicReppsitory{
    String ASC = "asc";
    String DESC = "desc";

    /**
     * 获取话题列表
     *
     * @param query     搜索关键词，允许任何字符串。
     * @param direction 用于基于数据 id 字段的排序方向设置，允许 asc 或者 desc，默认 desc
     * @param limit     本次请求请求的数据条数，默认 15 条，允许的范围 1 - 100。
     * @param index     数据查询定位值，来源于数据 id 字段。
     * @param only     可选，该字段只有一个固定值 hot，当出现 only 值的时候其他参数全部失效，转而 API 只返回热门话题数据。
     * @return
     */
    Observable<List<TopicListBean>> getTopicListBean(String only,String query, String direction, Integer limit, Long index);

    /**
     * 获取话题下的动态列表
     *
     * @param topicId 话题id
     * @param direction 用于基于数据 index 字段的排序方向设置，允许 asc 或者 desc，默认 desc
     * @param limit 本次请求请求的数据条数，默认 15 条，允许的范围 1 - 100。
     * @param index 数据查询定位值，来源于数据 index 字段。
     * @param isLoadMore
     * @return
     */
    Observable<List<DynamicDetailBeanV2>> getTopicDynamicListBean(Long topicId, String direction, Integer limit, Long index,boolean isLoadMore);

    /**
     * 获取一个话题详情 (如果携带 Authorization 则会返回关注状态，否则不会返回！)
     * @param topicId 话题id
     * @return
     */
    Observable<TopicDetailBean> getTopicDetailBean(Long topicId);

    /**
     * 创建话题
     * @param name 必须，话题名称最长 100 个字。
     * @param desc 可选，话题描述最长 500 个字。
     * @param logo 可选，话题 Logo 调用文件上传接口返回的 FileWith ID 标识。
     * @return
     */
    Observable<BaseJsonV2<Integer>> createTopic(String name, String desc, String logo);

    /**
     * 编辑一个话题
     * @param topicId 话题ID
     * @param desc 话题描述
     * @param logo 话题 Logo 调用文件上传接口返回的 FileWith ID 标识。
     */
    Observable<Object> modifyTopic(Long topicId,String desc, String logo);

    /**
     * 关注/取消关注一个话题
     * @param topicId 话题 id
     * @param isFollowed true-已关注，
     */
    void handleTopicFollowState(Long topicId, boolean isFollowed);

    /**
     * 获取话题下的参与者列表
     * @param topicId 话题id
     * @param limit
     * @param offset
     * @return
     */
    Observable<List<Integer>> getParticipants(Long topicId,Integer limit,Integer offset);
}

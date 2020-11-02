package com.zhiyicx.thinksnsplus.data.source.remote;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_DELETE_USER_FRIENDS_LIST;

/**
 * @author LiuChao
 * @describe
 * @date 2017/2/16
 * @contact email:450127106@qq.com
 */

public interface FollowFansClient {
    /**
     * 获取用户粉丝列表
     */
    @GET(ApiConfig.APP_PATH_FANS_LIST)
    Observable<List<UserInfoBean>> getUserFansList(@Path("user_id") long user_id, @Query("offset") long max_id, @Query("limit") Integer limitCount);

    /**
     * 获取用户关注列表
     */
    @GET(ApiConfig.APP_PATH_FOLLOW_LIST)
    Observable<List<UserInfoBean>> getUserFollowsList(@Path("user_id") long user_id, @Query("offset") long max_id, @Query("limit") Integer limitCount);

    /**
     * 获取用户的好友列表
     *
     * @param offset     offset
     * @param limitCount 每页数据
     * @return Observable
     */
    @GET(APP_PATH_DELETE_USER_FRIENDS_LIST)
    Observable<List<UserInfoBean>> getUserFriendsList(@Query("offset") long offset, @Query("limit") Integer limitCount, @Query("keyword") String keyword);

}

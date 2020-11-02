package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2Dao;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/06/22/17:47
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class DynamicDetailBeanV2GreenDaoImpl extends CommonCacheImpl<DynamicDetailBeanV2> {

    @Inject
    public DynamicDetailBeanV2GreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(DynamicDetailBeanV2 singleData) {
        return getWDaoSession().getDynamicDetailBeanV2Dao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<DynamicDetailBeanV2> multiData) {
        getWDaoSession().getDynamicDetailBeanV2Dao().insertOrReplaceInTx(multiData);
    }

    public long getCounts() {
        return getRDaoSession().getDynamicDetailBeanV2Dao().count();
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public DynamicDetailBeanV2 getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getDynamicDetailBeanV2Dao().load(primaryKey);
    }

    @Override
    public List<DynamicDetailBeanV2> getMultiDataFromCache() {
        return getRDaoSession().getDynamicDetailBeanV2Dao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getDynamicDetailBeanV2Dao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getDynamicDetailBeanV2Dao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(DynamicDetailBeanV2 dta) {
        getWDaoSession().getDynamicDetailBeanV2Dao().delete(dta);
    }

    @Override
    public void updateSingleData(DynamicDetailBeanV2 newData) {
        getWDaoSession().getDynamicDetailBeanV2Dao().delete(newData);
    }

    public void deleteDynamicByFeedId(Long feed_id) {
        DynamicDetailBeanV2 data = getWDaoSession().getDynamicDetailBeanV2Dao().queryBuilder()
                .where(DynamicDetailBeanV2Dao.Properties.Id.eq(feed_id)).unique();
        if (data != null)
            getWDaoSession().getDynamicDetailBeanV2Dao().delete(data);
    }

    @Override
    public long insertOrReplace(DynamicDetailBeanV2 newData) {
        return getWDaoSession().getDynamicDetailBeanV2Dao().insertOrReplace(newData);
    }

    public void insertOrReplace(List<DynamicDetailBeanV2> newData) {
        if (newData == null) {
            return;
        }
        getWDaoSession().getDynamicDetailBeanV2Dao().insertOrReplaceInTx(newData);
    }

    public void deleteDynamicByType(String type) {
        List<DynamicDetailBeanV2> datas = null;
        switch (type) {
            case ApiConfig.DYNAMIC_TYPE_FOLLOWS:
                datas = getWDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where " + " T." + DynamicDetailBeanV2Dao.Properties.IsFollowed.columnName + " = 1 "); // 0 false 1 true
                for (DynamicDetailBeanV2 data : datas) {
                    data.setIsFollowed(false);
                }

                break;
            case ApiConfig.DYNAMIC_TYPE_HOTS:
                datas = getWDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where " + " T." + DynamicDetailBeanV2Dao.Properties.Hot_creat_time.columnName + " < " + System.currentTimeMillis()); // 0 false 1 true
                for (DynamicDetailBeanV2 data : datas) {
                    data.setHot_creat_time(0L);
                }

                break;
            case ApiConfig.DYNAMIC_TYPE_NEW:
                if (AppApplication.getmCurrentLoginAuth() != null) {
                    datas = getWDaoSession().getDynamicDetailBeanV2Dao().queryBuilder()
                            .where(DynamicDetailBeanV2Dao.Properties.Id.isNotNull(), DynamicDetailBeanV2Dao.Properties.User_id.notEq(AppApplication.getMyUserIdWithdefault()))
                            .whereOr(DynamicDetailBeanV2Dao.Properties.Hot_creat_time.isNull(), DynamicDetailBeanV2Dao.Properties.Hot_creat_time.eq(0), DynamicDetailBeanV2Dao.Properties.IsFollowed.eq(false))
                            .list();
                    getWDaoSession().getDynamicDetailBeanV2Dao().deleteInTx(datas);
                }
                return;
            case ApiConfig.DYNAMIC_TYPE_MY_COLLECTION:
                List<DynamicDetailBeanV2> beanV2List;
                beanV2List = getWDaoSession().getDynamicDetailBeanV2Dao().queryBuilder().where(DynamicDetailBeanV2Dao.Properties.Has_collect.eq(1)).list();

                for (DynamicDetailBeanV2 dynamicToolBean : beanV2List) {
                    dynamicToolBean.setHas_collect(false);
                }
                getWDaoSession().getDynamicDetailBeanV2Dao().insertOrReplaceInTx(beanV2List);
                break;
            default:
        }
        if (datas != null) {
            getWDaoSession().getDynamicDetailBeanV2Dao().insertOrReplaceInTx(datas);
        }
    }

    public DynamicDetailBeanV2 getDynamicByFeedMark(Long feed_mark) {
        List<DynamicDetailBeanV2> datas = getRDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where " + " T." + DynamicDetailBeanV2Dao.Properties.Feed_mark.columnName + " = ? "// feedId倒序
                , String.valueOf(feed_mark));
        if (!datas.isEmpty()) {
            return datas.get(0);
        }
        return null;
    }

    public DynamicDetailBeanV2 getDynamicByFeedId(Long feed_id) {
        List<DynamicDetailBeanV2> datas = getRDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where " + " T." + DynamicDetailBeanV2Dao.Properties.Id.columnName + " = ? "// feedId倒序
                , String.valueOf(feed_id));
        if (!datas.isEmpty()) {
            return datas.get(0);
        }
        return null;
    }

    /**
     * 获取关注的动态列表
     */
    public List<DynamicDetailBeanV2> getFollowedDynamicList(Long feed_id) {
        if (feed_id == null || feed_id == 0) {
            feed_id = System.currentTimeMillis();
        }
        return getRDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where " + " T." + DynamicDetailBeanV2Dao.Properties.IsFollowed.columnName + " = 1 and " // 0 false 1 true
                        + " T." + DynamicDetailBeanV2Dao.Properties.Id.columnName + " < ?   ORDER BY "
                        + " T." + DynamicDetailBeanV2Dao.Properties.Id.columnName + " DESC LIMIT " + TSListFragment.DEFAULT_PAGE_DB_SIZE// feedId倒序
                , String.valueOf(feed_id));
    }

    /**
     * 获取热门的动态列表
     *
     * @return
     */
    public List<DynamicDetailBeanV2> getHotDynamicList(Long id) {
        if (id == 0) {
            id = System.currentTimeMillis();
        }
        return getRDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where "
                        + " T." + DynamicDetailBeanV2Dao.Properties.Hot.columnName + " < ?  ORDER BY "
                        + " T." + DynamicDetailBeanV2Dao.Properties.Hot.columnName + " DESC LIMIT " + TSListFragment.DEFAULT_PAGE_DB_SIZE// 创建时间倒序
                , String.valueOf(id));
    }

    /**
     * 获取最新的动态列表
     */
    public List<DynamicDetailBeanV2> getNewestDynamicList(Long feed_id) {
        if (feed_id == null || feed_id == 0) {
            feed_id = System.currentTimeMillis();
        }
        return getRDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where " + " T." + DynamicDetailBeanV2Dao.Properties.Id.columnName + " < ?  ORDER BY "
                        + " T." + DynamicDetailBeanV2Dao.Properties.Id.columnName + " DESC LIMIT " + TSListFragment.DEFAULT_PAGE_DB_SIZE// feedId倒序
                , String.valueOf(feed_id));
    }

    /**
     * 获取我收藏的动态
     *
     * @return
     */
    public List<DynamicDetailBeanV2> getMyCollectDynamic() {
        return getRDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where "
                        + " T." + DynamicDetailBeanV2Dao.Properties.Has_collect.columnName + " = ? "
                        + " ORDER BY  T." + DynamicDetailBeanV2Dao.Properties.Id.columnName + " ASC LIMIT " + TSListFragment.DEFAULT_PAGE_DB_SIZE//
                // 按照Feed_id倒序：越新的动态，Feed_id越大
                , "1");
    }

    /**
     * 获取我正在或者发送失败的动态
     */
    public List<DynamicDetailBeanV2> getMySendingUnSuccessDynamic(Long userId) {
        List<DynamicDetailBeanV2> result = getRDaoSession().getDynamicDetailBeanV2Dao().queryRaw(" where " + " T." + DynamicDetailBeanV2Dao.Properties.User_id.columnName + " = ? and " + " T." + DynamicDetailBeanV2Dao.Properties.State.columnName + " != " + DynamicDetailBeanV2.SEND_SUCCESS + "  ORDER BY "
                        + " T." + DynamicDetailBeanV2Dao.Properties.Id.columnName + " DESC "// feedId倒序
                , String.valueOf(userId));
        for (DynamicDetailBeanV2 data : result) {
            data.handleData();
        }
        return result;
    }

    /**
     * 获取我正在或者发送失败的动态
     */
    public List<DynamicDetailBeanV2> getMySendingUnSuccessDynamicWithTopic(Long userId, Long topic) {
        List<DynamicDetailBeanV2> cache = getMySendingUnSuccessDynamic(userId);
        List<DynamicDetailBeanV2> result = new ArrayList<>();
        for (DynamicDetailBeanV2 data : cache) {
            List<TopicListBean> topics = data.getTopics();
            boolean continueData = topics == null || topics.isEmpty() || !topics.contains(new TopicListBean(topic));
            if (continueData) {
                continue;
            }
            data.handleData();
            result.add(data);
        }
        return result;
    }


}

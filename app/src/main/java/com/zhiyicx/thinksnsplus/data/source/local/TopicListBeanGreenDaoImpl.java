package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/27/9:47
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopicListBeanGreenDaoImpl extends CommonCacheImpl<TopicListBean> {

    @Inject
    public TopicListBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(TopicListBean singleData) {
        return getWDaoSession().getTopicListBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<TopicListBean> multiData) {
        getWDaoSession().getTopicListBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public TopicListBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getTopicListBeanDao().load(primaryKey);
    }

    @Override
    public List<TopicListBean> getMultiDataFromCache() {
        return getRDaoSession().getTopicListBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getTopicListBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getTopicListBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(TopicListBean dta) {
        getWDaoSession().getTopicListBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(TopicListBean newData) {
        getWDaoSession().getTopicListBeanDao().insertOrReplace(newData);
    }

    @Override
    public long insertOrReplace(TopicListBean newData) {
        return getWDaoSession().getTopicListBeanDao().insertOrReplace(newData);
    }

    public List<TopicListBean> getHotTopic() {
        return getRDaoSession().getTopicListBeanDao().queryBuilder().where(TopicListBeanDao.Properties.IsHotTopic.eq(true)).list();
    }
}

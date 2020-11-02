package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.QATopicBeanDao;
import com.zhiyicx.thinksnsplus.data.beans.qa.QATopicBean;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/15
 * @contact email:648129313@qq.com
 */

public class QATopicBeanGreenDaoImpl extends CommonCacheImpl<QATopicBean> {

    @Inject
    public QATopicBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(QATopicBean singleData) {
        return getWDaoSession().getQATopicBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<QATopicBean> multiData) {
        getWDaoSession().getQATopicBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public QATopicBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getQATopicBeanDao().load(primaryKey);
    }

    @Override
    public List<QATopicBean> getMultiDataFromCache() {
        return getRDaoSession().getQATopicBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getQATopicBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getQATopicBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(QATopicBean dta) {
        getWDaoSession().getQATopicBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(QATopicBean newData) {
        getWDaoSession().getQATopicBeanDao().update(newData);
    }

    @Override
    public long insertOrReplace(QATopicBean newData) {
        return getWDaoSession().getQATopicBeanDao().insertOrReplace(newData);
    }

    /**
     * 获取关注的话题列表
     */
    public List<QATopicBean> getUserFollowTopic() {
        try {
            return getRDaoSession().getQATopicBeanDao().queryBuilder().where(QATopicBeanDao.Properties.Has_follow.eq(true)).build().list();
        } catch (Exception e) {
            return null;
        }
    }
}

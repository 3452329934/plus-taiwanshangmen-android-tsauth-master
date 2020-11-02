package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBeanV2Dao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/07/10/17:27
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SendDynamicDataBeanV2GreenDaoImpl extends CommonCacheImpl<SendDynamicDataBeanV2> {

    @Inject
    public SendDynamicDataBeanV2GreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(SendDynamicDataBeanV2 singleData) {
        return getWDaoSession().getSendDynamicDataBeanV2Dao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<SendDynamicDataBeanV2> multiData) {
        getWDaoSession().getSendDynamicDataBeanV2Dao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public SendDynamicDataBeanV2 getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getSendDynamicDataBeanV2Dao().load(primaryKey);
    }

    @Override
    public List<SendDynamicDataBeanV2> getMultiDataFromCache() {
        return getRDaoSession().getSendDynamicDataBeanV2Dao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getSendDynamicDataBeanV2Dao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getSendDynamicDataBeanV2Dao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(SendDynamicDataBeanV2 dta) {
        getWDaoSession().getSendDynamicDataBeanV2Dao().delete(dta);
    }

    @Override
    public void updateSingleData(SendDynamicDataBeanV2 newData) {
        getWDaoSession().getSendDynamicDataBeanV2Dao().insertOrReplace(newData);
    }

    @Override
    public long insertOrReplace(SendDynamicDataBeanV2 newData) {
        return getWDaoSession().getSendDynamicDataBeanV2Dao().insertOrReplace(newData);
    }

    public SendDynamicDataBeanV2 getSendDynamicDataBeanV2ByFeedMark(String feed_mark) {
        return getRDaoSession().getSendDynamicDataBeanV2Dao().queryBuilder().where(SendDynamicDataBeanV2Dao.Properties.Feed_mark.eq(feed_mark)).unique();
    }

    public void delteSendDynamicDataBeanV2ByFeedMark(String feed_mark){
        getWDaoSession().getSendDynamicDataBeanV2Dao().delete(getWDaoSession().getSendDynamicDataBeanV2Dao().queryBuilder().
                where(SendDynamicDataBeanV2Dao.Properties.Feed_mark.eq(feed_mark)).unique());
    }
}

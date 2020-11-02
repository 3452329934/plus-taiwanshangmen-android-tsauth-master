package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.TopDynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.TopDynamicBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/07/12/17:46
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopDynamicBeanGreenDaoImpl extends CommonCacheImpl<TopDynamicBean> {

    @Inject
    public TopDynamicBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(TopDynamicBean singleData) {
        return 0;
    }

    @Override
    public void saveMultiData(List<TopDynamicBean> multiData) {
        getWDaoSession().getTopDynamicBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public TopDynamicBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getTopDynamicBeanDao().load(primaryKey);
    }

    @Override
    public List<TopDynamicBean> getMultiDataFromCache() {
        return getRDaoSession().getTopDynamicBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getTopDynamicBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getTopDynamicBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(TopDynamicBean dta) {
        getWDaoSession().getTopDynamicBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(TopDynamicBean newData) {
        insertOrReplace(newData);
    }

    @Override
    public long insertOrReplace(TopDynamicBean newData) {
        return getWDaoSession().getTopDynamicBeanDao().insertOrReplace(newData);
    }

    public List<DynamicDetailBeanV2> getTopDynamicByType(Long type) {
        if (getSingleDataFromCache(type) == null) {
            return null;
        }
        return getSingleDataFromCache(type).getTopDynamics();
    }

}

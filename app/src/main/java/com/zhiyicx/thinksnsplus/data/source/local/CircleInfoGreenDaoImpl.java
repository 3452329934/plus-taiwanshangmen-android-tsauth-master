package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfoDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Jliuer
 * @Date 2017/11/30/9:50
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CircleInfoGreenDaoImpl extends CommonCacheImpl<CircleInfo> {


    @Inject
    public CircleInfoGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(CircleInfo singleData) {
        return getWDaoSession().getCircleInfoDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<CircleInfo> multiData) {
        getWDaoSession().getCircleInfoDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public CircleInfo getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getCircleInfoDao().load(primaryKey);
    }

    @Override
    public List<CircleInfo> getMultiDataFromCache() {
        return getRDaoSession().getCircleInfoDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getCircleInfoDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getCircleInfoDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(CircleInfo dta) {
        getWDaoSession().getCircleInfoDao().delete(dta);
    }

    @Override
    public void updateSingleData(CircleInfo newData) {
        saveSingleData(newData);
    }

    @Override
    public long insertOrReplace(CircleInfo newData) {
        return saveSingleData(newData);
    }

    public List<CircleInfo> getCircleListByCategory(long categoryId) {
        return getRDaoSession().getCircleInfoDao().queryBuilder()
                .where(CircleInfoDao.Properties.Category_id.eq(categoryId))
                .orderDesc(CircleInfoDao.Properties.Created_at)
                .build()
                .list();
    }
}

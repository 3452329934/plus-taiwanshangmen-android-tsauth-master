package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBean;
import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/08/01/11:37
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class RealAdvertListBeanGreenDaoImpl extends CommonCacheImpl<RealAdvertListBean> {

    @Inject
    public RealAdvertListBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(RealAdvertListBean singleData) {
        return getWDaoSession().getRealAdvertListBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<RealAdvertListBean> multiData) {
        getWDaoSession().getRealAdvertListBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public RealAdvertListBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getRealAdvertListBeanDao().load(primaryKey);
    }

    @Override
    public List<RealAdvertListBean> getMultiDataFromCache() {
        return getRDaoSession().getRealAdvertListBeanDao().loadAll();
    }

    public List<RealAdvertListBean> getAdvertById(long id) {
        return getRDaoSession().getRealAdvertListBeanDao().queryBuilder().where(RealAdvertListBeanDao.Properties
                .Space_id.eq(id))
                .orderDesc(RealAdvertListBeanDao.Properties.Sort)
                .build().list();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getRealAdvertListBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getRealAdvertListBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(RealAdvertListBean dta) {
        getWDaoSession().getRealAdvertListBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(RealAdvertListBean newData) {
        getWDaoSession().getRealAdvertListBeanDao().insertOrReplace(newData);
    }

    @Override
    public long insertOrReplace(RealAdvertListBean newData) {
        return getWDaoSession().getRealAdvertListBeanDao().insertOrReplace(newData);
    }
}

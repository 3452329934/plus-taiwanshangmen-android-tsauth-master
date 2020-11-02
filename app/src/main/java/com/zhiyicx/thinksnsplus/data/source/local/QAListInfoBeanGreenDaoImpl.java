package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.QAListInfoBeanDao;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/18
 * @contact email:648129313@qq.com
 */

public class QAListInfoBeanGreenDaoImpl extends CommonCacheImpl<QAListInfoBean>{

    @Inject
    public QAListInfoBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(QAListInfoBean singleData) {
        return getWDaoSession().getQAListInfoBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<QAListInfoBean> multiData) {
        getWDaoSession().getQAListInfoBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public QAListInfoBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getQAListInfoBeanDao().load(primaryKey);
    }

    @Override
    public List<QAListInfoBean> getMultiDataFromCache() {
        return getRDaoSession().getQAListInfoBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getQAListInfoBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getQAListInfoBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(QAListInfoBean dta) {
        getWDaoSession().getQAListInfoBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(QAListInfoBean newData) {
        getWDaoSession().getQAListInfoBeanDao().update(newData);
    }

    @Override
    public long insertOrReplace(QAListInfoBean newData) {
        return getWDaoSession().getQAListInfoBeanDao().insertOrReplace(newData);
    }
}

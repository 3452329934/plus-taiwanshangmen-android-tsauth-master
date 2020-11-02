package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.InfoRecommendBeanDao;
import com.zhiyicx.thinksnsplus.data.beans.InfoRecommendBean;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/04/14/16:14
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class InfoRecommendBeanGreenDaoImpl extends CommonCacheImpl<InfoRecommendBean> {

    @Inject
    public InfoRecommendBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(InfoRecommendBean singleData) {
        return getWDaoSession().getInfoRecommendBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<InfoRecommendBean> multiData) {
        getWDaoSession().getInfoRecommendBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public InfoRecommendBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getInfoRecommendBeanDao().load(primaryKey);
    }

    @Override
    public List<InfoRecommendBean> getMultiDataFromCache() {
        return getRDaoSession().getInfoRecommendBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getInfoRecommendBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getInfoRecommendBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(InfoRecommendBean dta) {
        getWDaoSession().getInfoRecommendBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(InfoRecommendBean newData) {
        getWDaoSession().getInfoRecommendBeanDao().insertOrReplace(newData);
    }

    @Override
    public long insertOrReplace(InfoRecommendBean newData) {
        return getWDaoSession().getInfoRecommendBeanDao().insertOrReplace(newData);
    }
}

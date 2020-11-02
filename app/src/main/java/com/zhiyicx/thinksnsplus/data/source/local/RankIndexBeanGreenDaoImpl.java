package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.RankIndexBean;
import com.zhiyicx.thinksnsplus.data.beans.RankIndexBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;
import com.zhiyicx.thinksnsplus.modules.rank.main.container.RankTypeConfig;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/24
 * @contact email:648129313@qq.com
 */

public class RankIndexBeanGreenDaoImpl extends CommonCacheImpl<RankIndexBean>{

    @Inject
    public RankIndexBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(RankIndexBean singleData) {
        return getWDaoSession().getRankIndexBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<RankIndexBean> multiData) {
        getWDaoSession().getRankIndexBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public RankIndexBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getRankIndexBeanDao().load(primaryKey);
    }

    @Override
    public List<RankIndexBean> getMultiDataFromCache() {
        return getRDaoSession().getRankIndexBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getRankIndexBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getRankIndexBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(RankIndexBean dta) {
        getWDaoSession().getRankIndexBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(RankIndexBean newData) {
        getWDaoSession().getRankIndexBeanDao().update(newData);
    }

    @Override
    public long insertOrReplace(RankIndexBean newData) {
        return getWDaoSession().getRankIndexBeanDao().insertOrReplace(newData);
    }

    public List<RankIndexBean> getIndexRankList(String type){
        List<RankIndexBean> list = new ArrayList<>();
        list.addAll(getRDaoSession().getRankIndexBeanDao().queryBuilder().where(RankIndexBeanDao.Properties.Category.eq(type)).build().list());
        return list;
    }
}

package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.RechargeSuccessBean;
import com.zhiyicx.thinksnsplus.data.beans.RechargeSuccessBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/06/09/9:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class RechargeSuccessBeanGreenDaoImpl extends CommonCacheImpl<RechargeSuccessBean> {

    @Inject
    public RechargeSuccessBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(RechargeSuccessBean singleData) {
        return getWDaoSession().getRechargeSuccessBeanDao().insert(singleData);
    }

    @Override
    public void saveMultiData(List<RechargeSuccessBean> multiData) {
        getWDaoSession().getRechargeSuccessBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public RechargeSuccessBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getRechargeSuccessBeanDao().load(primaryKey);
    }

    @Override
    public List<RechargeSuccessBean> getMultiDataFromCache() {
        return getRDaoSession().getRechargeSuccessBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getRechargeSuccessBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getRechargeSuccessBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(RechargeSuccessBean dta) {
        getWDaoSession().getRechargeSuccessBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(RechargeSuccessBean newData) {
        getWDaoSession().getRechargeSuccessBeanDao().update(newData);
    }

    @Override
    public long insertOrReplace(RechargeSuccessBean newData) {
        return getWDaoSession().getRechargeSuccessBeanDao().insertOrReplace(newData);
    }

    public List<RechargeSuccessBean> selectBillByAction(int action) {
        return getRDaoSession().getRechargeSuccessBeanDao().queryBuilder()
                .where(RechargeSuccessBeanDao.Properties.Action.eq(action))
                .orderDesc(RechargeSuccessBeanDao.Properties.Created_at)
                .list();
    }
}

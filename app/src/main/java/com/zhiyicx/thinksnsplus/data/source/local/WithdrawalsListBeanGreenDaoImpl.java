package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.WithdrawalsListBean;
import com.zhiyicx.thinksnsplus.data.beans.WithdrawalsListBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/06/02/12:00
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class WithdrawalsListBeanGreenDaoImpl extends CommonCacheImpl<WithdrawalsListBean> {

    @Inject
    public WithdrawalsListBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(WithdrawalsListBean singleData) {
        return getWDaoSession().getWithdrawalsListBeanDao().insert(singleData);
    }

    @Override
    public void saveMultiData(List<WithdrawalsListBean> multiData) {
        getWDaoSession().getWithdrawalsListBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public WithdrawalsListBean getSingleDataFromCache(Long primaryKey) {
        return null;
    }

    @Override
    public List<WithdrawalsListBean> getMultiDataFromCache() {
        return getRDaoSession().getWithdrawalsListBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getWithdrawalsListBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getWithdrawalsListBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(WithdrawalsListBean dta) {
        getWDaoSession().getWithdrawalsListBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(WithdrawalsListBean newData) {
        getRDaoSession().getWithdrawalsListBeanDao().update(newData);
    }

    @Override
    public long insertOrReplace(WithdrawalsListBean newData) {
        return getRDaoSession().getWithdrawalsListBeanDao().insertOrReplace(newData);
    }

    public void insertOrReplaceMultiData(List<WithdrawalsListBean> multiData) {
        getRDaoSession().getWithdrawalsListBeanDao().insertOrReplaceInTx(multiData);
    }
}

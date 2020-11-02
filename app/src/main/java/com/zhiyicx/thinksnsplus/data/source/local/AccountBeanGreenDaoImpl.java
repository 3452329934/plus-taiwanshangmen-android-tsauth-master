package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.AccountBean;
import com.zhiyicx.thinksnsplus.data.beans.AccountBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/7/26
 * @contact email:648129313@qq.com
 */

public class AccountBeanGreenDaoImpl extends CommonCacheImpl<AccountBean>{

    @Inject
    public AccountBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(AccountBean singleData) {
        return getWDaoSession().getAccountBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<AccountBean> multiData) {
        getWDaoSession().getAccountBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public AccountBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getAccountBeanDao().load(primaryKey);
    }

    @Override
    public List<AccountBean> getMultiDataFromCache() {
        return getRDaoSession().getAccountBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getAccountBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getAccountBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(AccountBean dta) {
        getWDaoSession().getAccountBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(AccountBean newData) {
        getWDaoSession().getAccountBeanDao().update(newData);
    }

    @Override
    public long insertOrReplace(AccountBean newData) {
        return getWDaoSession().getAccountBeanDao().insertOrReplace(newData);
    }

    public void insertOrReplaceByName(AccountBean newData) {
        if(newData==null||newData.getAccountName()==null){
            return;
        }
        List<AccountBean> list = getRDaoSession().getAccountBeanDao().queryBuilder()
                .where(AccountBeanDao.Properties.AccountName.eq(newData.getAccountName())).list();
        if (list == null || list.size() == 0){
            getWDaoSession().getAccountBeanDao().insertOrReplace(newData);
        }
    }
}

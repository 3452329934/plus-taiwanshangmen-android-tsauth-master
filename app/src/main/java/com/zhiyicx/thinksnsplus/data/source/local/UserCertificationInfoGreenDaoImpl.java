package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.UserCertificationInfo;
import com.zhiyicx.thinksnsplus.data.beans.UserCertificationInfoDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/4
 * @contact email:648129313@qq.com
 */

public class UserCertificationInfoGreenDaoImpl extends CommonCacheImpl<UserCertificationInfo>{

    @Inject
    public UserCertificationInfoGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(UserCertificationInfo singleData) {
        return getWDaoSession().getUserCertificationInfoDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<UserCertificationInfo> multiData) {
        getWDaoSession().getUserCertificationInfoDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public UserCertificationInfo getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getUserCertificationInfoDao().load(primaryKey);
    }

    @Override
    public List<UserCertificationInfo> getMultiDataFromCache() {
        return getRDaoSession().getUserCertificationInfoDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getUserCertificationInfoDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getUserCertificationInfoDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(UserCertificationInfo dta) {
        getWDaoSession().getUserCertificationInfoDao().delete(dta);
    }

    @Override
    public void updateSingleData(UserCertificationInfo newData) {
        getWDaoSession().getUserCertificationInfoDao().update(newData);
    }

    @Override
    public long insertOrReplace(UserCertificationInfo newData) {
        return getWDaoSession().getUserCertificationInfoDao().insertOrReplace(newData);
    }

    public UserCertificationInfo getInfoByUserId(){
        UserCertificationInfo info = null;
        List<UserCertificationInfo> list =
                getRDaoSession().getUserCertificationInfoDao().queryBuilder().
                        where(UserCertificationInfoDao.Properties.User_id.eq(AppApplication.getmCurrentLoginAuth().getUser_id())).list();
        if (list != null && list.size() > 0){
            info = list.get(0);
        }
        return info;
    }
}

package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.GroupInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupInfoBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe 圈子
 * @date 2017/7/17
 * @contact email:648129313@qq.com
 */

public class GroupInfoBeanGreenDaoImpl extends CommonCacheImpl<GroupInfoBean> {

    @Inject
    public GroupInfoBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(GroupInfoBean singleData) {
        return getWDaoSession().getGroupInfoBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<GroupInfoBean> multiData) {
        getWDaoSession().getGroupInfoBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public GroupInfoBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getGroupInfoBeanDao().load(primaryKey);
    }

    @Override
    public List<GroupInfoBean> getMultiDataFromCache() {
        List<GroupInfoBean> result = getRDaoSession().getGroupInfoBeanDao().loadAll();
        Collections.sort(result,(o1, o2) -> (int) (o2.getId() - o1.getId()));
        return result;
    }

    @Override
    public void clearTable() {
        getWDaoSession().getGroupInfoBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getGroupInfoBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(GroupInfoBean dta) {
        if (dta != null) {
            getWDaoSession().getGroupInfoBeanDao().delete(dta);
        }
    }

    @Override
    public void updateSingleData(GroupInfoBean newData) {
        if (newData != null) {
            getWDaoSession().getGroupInfoBeanDao().update(newData);
        }
    }

    @Override
    public long insertOrReplace(GroupInfoBean newData) {
        if (newData != null) {
            return getWDaoSession().getGroupInfoBeanDao().insertOrReplace(newData);
        }
        return 0;
    }

    /**
     * 获取用户加入的圈子
     */
    public List<GroupInfoBean> getUserJoinedGroup() {
        List<GroupInfoBean> list = null;
        list = getRDaoSession().getGroupInfoBeanDao().queryBuilder()
                .where(GroupInfoBeanDao.Properties.Is_member.eq(1))
                .orderDesc(GroupInfoBeanDao.Properties.Id)
                .list();
        return list;
    }
}

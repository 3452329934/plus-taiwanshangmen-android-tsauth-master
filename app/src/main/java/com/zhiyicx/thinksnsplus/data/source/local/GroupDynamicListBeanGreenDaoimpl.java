package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2Dao;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicListBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicListBeanDao;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/07/18/17:41
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class GroupDynamicListBeanGreenDaoimpl extends CommonCacheImpl<GroupDynamicListBean> {

    @Inject
    public GroupDynamicListBeanGreenDaoimpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(GroupDynamicListBean singleData) {
        return getWDaoSession().getGroupDynamicListBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<GroupDynamicListBean> multiData) {
        getWDaoSession().getGroupDynamicListBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public GroupDynamicListBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getGroupDynamicListBeanDao().load(primaryKey);
    }

    @Override
    public List<GroupDynamicListBean> getMultiDataFromCache() {
        return getRDaoSession().getGroupDynamicListBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getGroupDynamicListBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getGroupDynamicListBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(GroupDynamicListBean dta) {
        getWDaoSession().getGroupDynamicListBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(GroupDynamicListBean newData) {
        getWDaoSession().getGroupDynamicListBeanDao().insertOrReplace(newData);
    }

    @Override
    public long insertOrReplace(GroupDynamicListBean newData) {
        return getWDaoSession().getGroupDynamicListBeanDao().insertOrReplace(newData);
    }

    public List<GroupDynamicListBean> getMySendingUnSuccessDynamic(long user_id) {
        return getRDaoSession().getGroupDynamicListBeanDao().queryDeep(" where " + " T." + GroupDynamicListBeanDao.Properties.User_id.columnName + " = ? and " + " T." +
                        GroupDynamicListBeanDao.Properties.State.columnName + " != " + GroupDynamicListBean.SEND_SUCCESS + "  ORDER BY "
                        + " T." + GroupDynamicListBeanDao.Properties.Id.columnName + " DESC "
                , String.valueOf(user_id));
    }

    public UserInfoBean getComment(){

        return null;
    }

    public GroupDynamicListBean getDynamicByFeedMark(Long feed_mark) {
        List<GroupDynamicListBean> datas = getRDaoSession().getGroupDynamicListBeanDao().queryDeep(" where " + " T." + DynamicDetailBeanV2Dao.Properties.Feed_mark.columnName + " = ? "// feedId倒序
                , String.valueOf(feed_mark));
        if (!datas.isEmpty()) {
            return datas.get(0);
        }
        return null;
    }
}

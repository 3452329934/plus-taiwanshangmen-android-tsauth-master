package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.CommonMetadataBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;
import com.zhiyicx.thinksnsplus.comment.CommonMetadataBean;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/04/26/18:27
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CommonMetadataBeanGreenDaoImpl extends CommonCacheImpl<CommonMetadataBean> {

    @Inject
    public CommonMetadataBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(CommonMetadataBean singleData) {
        return getRDaoSession().getCommonMetadataBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<CommonMetadataBean> multiData) {
        getWDaoSession().getCommonMetadataBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public CommonMetadataBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getCommonMetadataBeanDao().load(primaryKey);
    }

    @Override
    public List<CommonMetadataBean> getMultiDataFromCache() {
        return getRDaoSession().getCommonMetadataBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getCommonMetadataBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getRDaoSession().getCommonMetadataBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(CommonMetadataBean dta) {
        List<CommonMetadataBean> readyToDelete = getWDaoSession().getCommonMetadataBeanDao().queryBuilder()
                .where(CommonMetadataBeanDao.Properties.Comment_mark.eq(dta.getComment_mark()))
                .build()
                .list();
        if (!readyToDelete.isEmpty()) {
            getRDaoSession().getCommonMetadataBeanDao().delete(readyToDelete.get(0));
        }
    }

    @Override
    public void updateSingleData(CommonMetadataBean newData) {
        List<CommonMetadataBean> readyToDelete = getWDaoSession().getCommonMetadataBeanDao().queryBuilder()
                .where(CommonMetadataBeanDao.Properties.Comment_mark.eq(newData.getComment_mark()))
                .build()
                .list();
        if (!readyToDelete.isEmpty()) {
            getRDaoSession().getCommonMetadataBeanDao().insertOrReplace(readyToDelete.get(0));
        }
    }

    @Override
    public long insertOrReplace(CommonMetadataBean newData) {
        return getWDaoSession().getCommonMetadataBeanDao().insertOrReplace(newData);
    }

    public List<CommonMetadataBean> getCommonMetadataListByTypeAndID(int type, int sourceId) {
        return getRDaoSession().getCommonMetadataBeanDao().queryBuilder()
                .where(CommonMetadataBeanDao.Properties.Comment_type.eq(type),
                        CommonMetadataBeanDao.Properties.Source_id.eq(sourceId))
                .build()
                .list();
    }

    public CommonMetadataBean getCommonMetadataListByCommentMark(long comment_mark) {
        List<CommonMetadataBean> dta=getRDaoSession().getCommonMetadataBeanDao().queryBuilder()
                .where(CommonMetadataBeanDao.Properties.Comment_mark.eq(comment_mark))
                .build()
                .list();
        if (dta.isEmpty()){
            return null;
        }
        return dta.get(0);
    }

}

package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.InfoCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoCommentListBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * @Author Jliuer
 * @Date 2017/03/27
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class InfoCommentListBeanDaoImpl extends CommonCacheImpl<InfoCommentListBean> {


    @Inject
    public InfoCommentListBeanDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(InfoCommentListBean singleData) {
        return getWDaoSession().getInfoCommentListBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<InfoCommentListBean> multiData) {
        getWDaoSession().getInfoCommentListBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public InfoCommentListBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getInfoCommentListBeanDao().load(primaryKey);
    }

    @Override
    public List<InfoCommentListBean> getMultiDataFromCache() {
        return getRDaoSession().getInfoCommentListBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getInfoCommentListBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getInfoCommentListBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(InfoCommentListBean dta) {
        getWDaoSession().getInfoCommentListBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(InfoCommentListBean newData) {
        getWDaoSession().getInfoCommentListBeanDao().updateInTx(newData);
    }

    @Override
    public long insertOrReplace(InfoCommentListBean newData) {
        return getWDaoSession().getInfoCommentListBeanDao().insertOrReplace(newData);
    }

    public List<InfoCommentListBean> getMySendingComment(Long info_id) {
        if (AppApplication.getmCurrentLoginAuth() == null) {
            return new ArrayList<>();
        }
        return getRDaoSession().getInfoCommentListBeanDao().queryBuilder()
                .where(InfoCommentListBeanDao.Properties.User_id.eq
                                (AppApplication.getmCurrentLoginAuth().getUser_id()),
                        InfoCommentListBeanDao.Properties.Id.eq(-1), InfoCommentListBeanDao
                                .Properties.Info_id.eq(info_id),
                        InfoCommentListBeanDao.Properties.State.eq(InfoCommentListBean.SEND_ING))
                .orderDesc(InfoCommentListBeanDao.Properties.Id)
                .list();
    }

    /**
     * 通过 CommentMark 获取评论内容
     *
     * @return
     */
    public InfoCommentListBean getCommentByCommentMark(Long commentMark) {
        List<InfoCommentListBean> result = getRDaoSession().getInfoCommentListBeanDao().queryBuilder()
                .where(InfoCommentListBeanDao.Properties.Comment_mark.eq(commentMark))
                .build()
                .list();
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }
}

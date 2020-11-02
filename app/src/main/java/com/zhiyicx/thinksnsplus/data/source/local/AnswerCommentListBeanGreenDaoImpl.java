package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.AnswerCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.AnswerCommentListBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/08/16/10:40
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AnswerCommentListBeanGreenDaoImpl extends CommonCacheImpl<AnswerCommentListBean> {

    @Inject
    public AnswerCommentListBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(AnswerCommentListBean singleData) {
        return getWDaoSession().getAnswerCommentListBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<AnswerCommentListBean> multiData) {
        getWDaoSession().getAnswerCommentListBeanDao().insertOrReplaceInTx();
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public AnswerCommentListBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getAnswerCommentListBeanDao().load(primaryKey);
    }

    @Override
    public List<AnswerCommentListBean> getMultiDataFromCache() {
        return getRDaoSession().getAnswerCommentListBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getAnswerCommentListBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getAnswerCommentListBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(AnswerCommentListBean dta) {
        getWDaoSession().getAnswerCommentListBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(AnswerCommentListBean newData) {
        getWDaoSession().getAnswerCommentListBeanDao().delete(newData);
    }

    @Override
    public long insertOrReplace(AnswerCommentListBean newData) {
        return getWDaoSession().getAnswerCommentListBeanDao().insertOrReplace(newData);
    }

    public AnswerCommentListBean getCommentByCommentMark(long comment_mark) {
        List<AnswerCommentListBean> result = getRDaoSession().getAnswerCommentListBeanDao().queryBuilder()
                .where(AnswerCommentListBeanDao.Properties.Comment_mark.eq(comment_mark))
                .list();
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }
}

package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.QuestionCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.QuestionCommentBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/18
 * @contact email:648129313@qq.com
 */

public class QuestionCommentBeanGreenDaoImpl extends CommonCacheImpl<QuestionCommentBean>{

    @Inject
    public QuestionCommentBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(QuestionCommentBean singleData) {
        return getWDaoSession().getQuestionCommentBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<QuestionCommentBean> multiData) {
        getWDaoSession().getQuestionCommentBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public QuestionCommentBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getQuestionCommentBeanDao().load(primaryKey);
    }

    @Override
    public List<QuestionCommentBean> getMultiDataFromCache() {
        return getRDaoSession().getQuestionCommentBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getQuestionCommentBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getQuestionCommentBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(QuestionCommentBean dta) {
        getWDaoSession().getQuestionCommentBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(QuestionCommentBean newData) {
        getWDaoSession().getQuestionCommentBeanDao().update(newData);
    }

    @Override
    public long insertOrReplace(QuestionCommentBean newData) {
        return getWDaoSession().getQuestionCommentBeanDao().insertOrReplace(newData);
    }

    public QuestionCommentBean getCommentByCommentMark(long comment_mark) {
        List<QuestionCommentBean> result = getRDaoSession().getQuestionCommentBeanDao().queryBuilder()
                .where(QuestionCommentBeanDao.Properties.Comment_mark.eq(comment_mark))
                .list();
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }
}

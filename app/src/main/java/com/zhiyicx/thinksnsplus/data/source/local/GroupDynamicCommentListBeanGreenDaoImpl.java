package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicCommentListBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * @Author Jliuer
 * @Date 2017/07/18/16:57
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class GroupDynamicCommentListBeanGreenDaoImpl extends CommonCacheImpl<GroupDynamicCommentListBean> {

    @Inject
    public GroupDynamicCommentListBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(GroupDynamicCommentListBean singleData) {
        return getWDaoSession().getGroupDynamicCommentListBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<GroupDynamicCommentListBean> multiData) {
        getWDaoSession().getGroupDynamicCommentListBeanDao().insertOrReplaceInTx(multiData);
    }

    public void deleteCacheByFeedMark(Long feed_id){
        Observable.from(getLocalComments(feed_id))
                .subscribeOn(Schedulers.io())
                .filter(dynamicCommentBean -> dynamicCommentBean.getId() != null && dynamicCommentBean.getId() != 0)
                .subscribe(new Observer<GroupDynamicCommentListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GroupDynamicCommentListBean groupDynamicCommentListBean) {
                        deleteSingleCache(groupDynamicCommentListBean);
                    }
                });
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public GroupDynamicCommentListBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getGroupDynamicCommentListBeanDao().load(primaryKey);
    }

    @Override
    public List<GroupDynamicCommentListBean> getMultiDataFromCache() {
        return getRDaoSession().getGroupDynamicCommentListBeanDao().loadAll();
    }

    public List<GroupDynamicCommentListBean> getLocalComments(Long feed_id) {

        return getRDaoSession().getGroupDynamicCommentListBeanDao().queryBuilder()
                .where(GroupDynamicCommentListBeanDao.Properties.Feed_id.eq(feed_id)).list();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getGroupDynamicCommentListBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getGroupDynamicCommentListBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(GroupDynamicCommentListBean dta) {
        getWDaoSession().getGroupDynamicCommentListBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(GroupDynamicCommentListBean newData) {
        getWDaoSession().getGroupDynamicCommentListBeanDao().delete(newData);
    }

    @Override
    public long insertOrReplace(GroupDynamicCommentListBean newData) {
        return getWDaoSession().getGroupDynamicCommentListBeanDao().insertOrReplace(newData);
    }

    public void insertOrReplace(List<GroupDynamicCommentListBean> newData) {
        if (newData != null && !newData.isEmpty()){
            getWDaoSession().getGroupDynamicCommentListBeanDao().insertOrReplaceInTx(newData);
        }
    }

    public List<GroupDynamicCommentListBean> getMySendingComment(long feed_id) {
        if (AppApplication.getmCurrentLoginAuth() == null) {
            return new ArrayList<>();
        }
        return getRDaoSession().getGroupDynamicCommentListBeanDao().queryBuilder()
                .where(GroupDynamicCommentListBeanDao.Properties.User_id.eq(AppApplication.getmCurrentLoginAuth().getUser_id()),
                        GroupDynamicCommentListBeanDao.Properties.Id.isNull())
                .orderDesc(GroupDynamicCommentListBeanDao.Properties.Created_at)
                .list();
    }

    public List<GroupDynamicCommentListBean> getGroupCommentsByFeedId(long feed_id) {
        if (AppApplication.getmCurrentLoginAuth() == null) {
            return new ArrayList<>();
        }
        return getRDaoSession().getGroupDynamicCommentListBeanDao().queryBuilder()
                .where(GroupDynamicCommentListBeanDao.Properties.User_id.eq(AppApplication.getmCurrentLoginAuth().getUser_id()),
                        GroupDynamicCommentListBeanDao.Properties.Feed_id.eq(feed_id))
                .orderDesc(GroupDynamicCommentListBeanDao.Properties.Created_at)
                .list();
    }

    public GroupDynamicCommentListBean getGroupCommentsByCommentMark(long comment_mark) {
        List<GroupDynamicCommentListBean> result = getRDaoSession().getGroupDynamicCommentListBeanDao().queryBuilder()
                .where(GroupDynamicCommentListBeanDao.Properties.User_id.eq(AppApplication.getmCurrentLoginAuth().getUser_id()),
                        GroupDynamicCommentListBeanDao.Properties.Comment_mark.eq(comment_mark))
                .orderDesc(GroupDynamicCommentListBeanDao.Properties.Created_at)
                .list();

        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }
}

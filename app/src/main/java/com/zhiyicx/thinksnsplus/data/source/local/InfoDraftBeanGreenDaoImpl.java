package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.BaseDraftBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoDraftBean;
import com.zhiyicx.thinksnsplus.data.beans.PostDraftBean;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/01/15:29
 * @Email Jliuer@aliyun.com
 * @Description 资讯草稿
 */
public class InfoDraftBeanGreenDaoImpl extends CommonCacheImpl<InfoDraftBean> {

    @Inject
    public InfoDraftBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(InfoDraftBean singleData) {
        return getWDaoSession().getInfoDraftBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<InfoDraftBean> multiData) {
        getWDaoSession().getInfoDraftBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public InfoDraftBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getInfoDraftBeanDao().load(primaryKey);
    }

    @Override
    public List<InfoDraftBean> getMultiDataFromCache() {
        return getRDaoSession().getInfoDraftBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getInfoDraftBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getInfoDraftBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(InfoDraftBean dta) {
        getWDaoSession().getInfoDraftBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(InfoDraftBean newData) {
        getWDaoSession().getInfoDraftBeanDao().insertOrReplace(newData);
    }

    @Override
    public long insertOrReplace(InfoDraftBean newData) {
        return getWDaoSession().getInfoDraftBeanDao().insertOrReplace(newData);
    }

    public List<BaseDraftBean> getMultiBasetDraftDataFromCache() {
        List<InfoDraftBean> realData = getMultiDataFromCache();
        List<BaseDraftBean> needData = new ArrayList<>();
        if (!realData.isEmpty()) {
            needData.addAll(realData);
        }
        Collections.reverse(needData);
        return needData;
    }
}

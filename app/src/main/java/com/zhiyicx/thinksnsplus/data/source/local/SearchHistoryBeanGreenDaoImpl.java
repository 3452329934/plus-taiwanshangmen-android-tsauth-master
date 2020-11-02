package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.SearchHistoryBean;
import com.zhiyicx.thinksnsplus.data.beans.SearchHistoryBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/07/10:52
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchHistoryBeanGreenDaoImpl extends CommonCacheImpl<SearchHistoryBean> {

    @Inject
    public SearchHistoryBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(SearchHistoryBean singleData) {
        return getWDaoSession().getSearchHistoryBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<SearchHistoryBean> multiData) {
        getWDaoSession().getSearchHistoryBeanDao().insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public SearchHistoryBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getSearchHistoryBeanDao().load(primaryKey);
    }

    @Override
    public List<SearchHistoryBean> getMultiDataFromCache() {
        List<SearchHistoryBean> result = getWDaoSession().getSearchHistoryBeanDao().loadAll();
        if (result != null && !result.isEmpty()) {
            Collections.reverse(result);
        }
        return result;
    }

    @Override
    public void clearTable() {
        getWDaoSession().getSearchHistoryBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getSearchHistoryBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(SearchHistoryBean dta) {
        getWDaoSession().getSearchHistoryBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(SearchHistoryBean newData) {
        getWDaoSession().getSearchHistoryBeanDao().insertOrReplace(newData);
    }

    @Override
    public long insertOrReplace(SearchHistoryBean newData) {
        return getWDaoSession().getSearchHistoryBeanDao().insertOrReplace(newData);
    }

    public void saveHistory(String content) {
        SearchHistoryBean cache = getHistotyByContent(content);
        if (cache == null) {
            SearchHistoryBean newData = new SearchHistoryBean(content);
            insertOrReplace(newData);
        } else {
            cache.setCreate_at(System.currentTimeMillis());
        }
    }

    public void deleteSearchHistory(String content) {
        SearchHistoryBean cache = getHistotyByContent(content);
        if (cache != null) {
            deleteSingleCache(cache);
        }
    }

    public SearchHistoryBean getHistotyByContent(String content) {
        return getRDaoSession().getSearchHistoryBeanDao().queryBuilder()
                .where(SearchHistoryBeanDao.Properties.Content.eq(content))
                .unique();
    }
}

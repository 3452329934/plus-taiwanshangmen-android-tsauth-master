package com.zhiyicx.thinksnsplus.data.beans;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.zhiyicx.thinksnsplus.data.beans.SearchHistoryBean;
import com.zhiyicx.thinksnsplus.data.beans.SearchHistoryBeanDao;

public class SearchHistoryBeanTest extends AbstractDaoTestLongPk<SearchHistoryBeanDao, SearchHistoryBean> {

    public SearchHistoryBeanTest() {
        super(SearchHistoryBeanDao.class);
    }

    @Override
    protected SearchHistoryBean createEntity(Long key) {
        SearchHistoryBean entity = new SearchHistoryBean();
        entity.setMark(key);
        entity.setType();
        return entity;
    }

}

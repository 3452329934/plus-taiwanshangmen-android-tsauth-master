package com.zhiyicx.thinksnsplus.data.beans;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBeanDao;

public class TopicListBeanTest extends AbstractDaoTestLongPk<TopicListBeanDao, TopicListBean> {

    public TopicListBeanTest() {
        super(TopicListBeanDao.class);
    }

    @Override
    protected TopicListBean createEntity(Long key) {
        TopicListBean entity = new TopicListBean();
        entity.setId(key);
        entity.setLogo();
        return entity;
    }

}

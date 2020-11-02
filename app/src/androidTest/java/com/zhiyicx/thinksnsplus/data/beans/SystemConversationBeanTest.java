package com.zhiyicx.thinksnsplus.data.beans;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.zhiyicx.thinksnsplus.data.beans.SystemConversationBean;
import com.zhiyicx.thinksnsplus.data.beans.SystemConversationBeanDao;

public class SystemConversationBeanTest extends AbstractDaoTestLongPk<SystemConversationBeanDao, SystemConversationBean> {

    public SystemConversationBeanTest() {
        super(SystemConversationBeanDao.class);
    }

    @Override
    protected SystemConversationBean createEntity(Long key) {
        SystemConversationBean entity = new SystemConversationBean();
        entity.set_id(key);
        return entity;
    }

}

package com.zhiyicx.thinksnsplus.data.beans;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.zhiyicx.thinksnsplus.data.beans.InfoDraftBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoDraftBeanDao;

public class InfoDraftBeanTest extends AbstractDaoTestLongPk<InfoDraftBeanDao, InfoDraftBean> {

    public InfoDraftBeanTest() {
        super(InfoDraftBeanDao.class);
    }

    @Override
    protected InfoDraftBean createEntity(Long key) {
        InfoDraftBean entity = new InfoDraftBean();
        entity.setMark(key);
        return entity;
    }

}

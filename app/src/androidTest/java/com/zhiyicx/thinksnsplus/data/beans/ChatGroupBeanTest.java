package com.zhiyicx.thinksnsplus.data.beans;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBeanDao;

public class ChatGroupBeanTest extends AbstractDaoTestLongPk<ChatGroupBeanDao, ChatGroupBean> {

    public ChatGroupBeanTest() {
        super(ChatGroupBeanDao.class);
    }

    @Override
    protected ChatGroupBean createEntity(Long key) {
        ChatGroupBean entity = new ChatGroupBean();
        entity.setKey(key);
        entity.setMembersonly(false);
        entity.setAllowinvites(false);
        entity.setMaxusers(1);
        entity.setOwner(1);
        entity.setAffiliations_count(1);
        entity.setIsPublic(false);
        return entity;
    }

}

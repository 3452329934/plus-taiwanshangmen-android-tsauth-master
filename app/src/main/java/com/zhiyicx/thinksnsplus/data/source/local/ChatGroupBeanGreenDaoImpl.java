package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBeanDao;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBeanForGroupList;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2018/02/03/14:54
 * @Email Jliuer@aliyun.com
 * @Description 群信息管理
 */
public class ChatGroupBeanGreenDaoImpl extends CommonCacheImpl<ChatGroupBean> {

    @Inject
    public ChatGroupBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(ChatGroupBean singleData) {
        return getWDaoSession().getChatGroupBeanDao().insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<ChatGroupBean> multiData) {
        if (multiData == null || multiData.isEmpty()) {
            return;
        }
        getWDaoSession().getChatGroupBeanDao().insertOrReplaceInTx(multiData);
    }

    public void saveMultiDataForGroupList(List<ChatGroupBeanForGroupList> multiData) {
        if (multiData == null || multiData.isEmpty()) {
            return;
        }
        List<ChatGroupBean> news = new ArrayList<>();

        for (ChatGroupBeanForGroupList groupList : multiData) {
            ChatGroupBean groupBean = new ChatGroupBean(groupList);
            news.add(groupBean);
        }
        saveMultiData(news);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public ChatGroupBean getSingleDataFromCache(Long primaryKey) {
        return getRDaoSession().getChatGroupBeanDao().load(primaryKey);
    }

    @Override
    public List<ChatGroupBean> getMultiDataFromCache() {
        return getRDaoSession().getChatGroupBeanDao().loadAll();
    }

    @Override
    public void clearTable() {
        getWDaoSession().getChatGroupBeanDao().deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        getWDaoSession().getChatGroupBeanDao().deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(ChatGroupBean dta) {
        getWDaoSession().getChatGroupBeanDao().delete(dta);
    }

    @Override
    public void updateSingleData(ChatGroupBean newData) {
        saveSingleData(newData);
    }

    @Override
    public long insertOrReplace(ChatGroupBean newData) {
        return saveSingleData(newData);
    }

    public ChatGroupBean getChatGroupBeanById(String id) {
        getRDaoSession().getChatGroupBeanDao().detachAll();
        return getRDaoSession().getChatGroupBeanDao().queryBuilder().where(ChatGroupBeanDao.Properties.Id.eq(id)).build().unique();
    }

    public void deleteChatGroupBeanById(String id) {
        deleteSingleCache(getChatGroupBeanById(id));
    }

    public String getChatGroupName(String id) {
        getRDaoSession().getChatGroupBeanDao().detachAll();
        ChatGroupBean chatGroupBean = getRDaoSession().getChatGroupBeanDao().queryBuilder().where(ChatGroupBeanDao.Properties.Id.eq(id)).build().unique();
        if (chatGroupBean == null) {
            return AppApplication.getContext().getResources().getString(R.string.default_delete_user_name);
        }
        return chatGroupBean.getName() + "(" + chatGroupBean.getAffiliations_count() + ")";
    }

    public boolean updateChatGroupMemberCount(String id, int count, boolean add) {
        ChatGroupBean chatGroupBean = getRDaoSession().getChatGroupBeanDao().queryBuilder().where(ChatGroupBeanDao.Properties.Id.eq(id)).build().unique();
        if (chatGroupBean == null) {
            return false;
        }
        int resultCount;
        if (add) {
            resultCount = chatGroupBean.getAffiliations_count() + count;
        } else {
            resultCount = chatGroupBean.getAffiliations_count() - count;
        }
        resultCount = resultCount > 0 ? resultCount : 0;
        chatGroupBean.setAffiliations_count(resultCount);
        saveSingleData(chatGroupBean);
        return true;
    }

    public boolean updateGroupName(String id, String name) {
        ChatGroupBean chatGroupBean = getChatGroupBeanById(id);
        if (chatGroupBean == null) {
            return false;
        }
        chatGroupBean.setName(name);
        saveSingleData(chatGroupBean);
        return true;
    }

    public boolean updateGroupHeadImage(String id, String head) {
        ChatGroupBean chatGroupBean = getChatGroupBeanById(id);
        if (chatGroupBean == null) {
            return false;
        }
        chatGroupBean.setGroup_face(head);
        saveSingleData(chatGroupBean);
        return true;
    }

    public boolean updateGroupInfo(String id, String groupName, String groupIntro, int isPublic,
                                   int maxUser, boolean isMemberOnly, int isAllowInvites, String newOwner) {
        boolean isOldDataChanged;
        ChatGroupBean chatGroupBean = getChatGroupBeanById(id);
        isOldDataChanged = chatGroupBean == null;
        if (isOldDataChanged) {
            chatGroupBean = new ChatGroupBean();
            chatGroupBean.setId(id);
        }
        chatGroupBean.setName(groupName);
        try {
            chatGroupBean.setOwner(Long.parseLong(newOwner));
        } catch (NumberFormatException ignore) {
        }
        chatGroupBean.setDescription(groupIntro);
        chatGroupBean.setIsPublic(isPublic != 0);
        chatGroupBean.setAllowinvites(isAllowInvites != 0);
        chatGroupBean.setMaxusers(maxUser);
        chatGroupBean.setMembersonly(isMemberOnly);
        saveSingleData(chatGroupBean);
        return isOldDataChanged;
    }

    public List<ChatGroupBean> getChatGroupBeanByIds(List<String> ids) {
        List<ChatGroupBean> result = new ArrayList<>();
        if (ids.size() <= 0) {
            return result;
        }
        for (String id : ids) {
            ChatGroupBean chatGroupBean = getChatGroupBeanById(id);
            if (chatGroupBean != null) {
                result.add(chatGroupBean);
            }
        }
        return result;
    }
}

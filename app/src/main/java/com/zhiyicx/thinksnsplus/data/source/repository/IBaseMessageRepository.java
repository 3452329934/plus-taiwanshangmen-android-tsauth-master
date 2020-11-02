package com.zhiyicx.thinksnsplus.data.source.repository;

import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBeanForGroupList;
import com.zhiyicx.thinksnsplus.data.beans.ChatItemBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

import java.util.List;

import rx.Observable;

/**
 * @author Catherine
 * @describe
 * @date 2017/12/29
 * @contact email:648129313@qq.com
 */

public interface IBaseMessageRepository {
    /**
     * 获取对话列表信息
     *
     * @param user_id 用户 id
     */
    Observable<List<MessageItemBeanV2>> getConversationList(int user_id);

    Observable<List<MessageItemBeanV2>> completeEmConversation(List<MessageItemBeanV2> list);

    /**
     * 获取多组群信息
     *
     * @param ids 群 id , 以 ， 分割
     * @return
     */
    Observable<List<ChatGroupBean>> getGroupInfo(String ids);

    /**
     * 获取多组群信息，只有群头像
     *
     * @param ids 群 id , 以 ， 分割
     * @return
     */
    Observable<List<ChatGroupBeanForGroupList>> getGroupInfoOnlyGroupFace(String ids);

    /**
     * 完善用户信息 ，在 聊天详情界面
     *
     * @param id
     * @return
     */
    Observable<UserInfoBean> getUserInfo(String id);

    /**
     * 删除本地群聊信息
     *
     * @param id
     */
    void deleteLocalChatGoup(String id);

    void saveChatGoup(List<ChatGroupBean> groupBeans);
    void saveChatGoupForGroupList(List<ChatGroupBeanForGroupList> groupBeans);

    Observable<List<ChatItemBean>> completeUserInfo(List<ChatItemBean> list);
}

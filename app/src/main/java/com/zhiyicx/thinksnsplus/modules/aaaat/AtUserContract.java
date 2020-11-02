package com.zhiyicx.thinksnsplus.modules.aaaat;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/13/11:38
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface AtUserContract {
    interface View extends ITSListView<UserInfoBean, Presenter> {
        // 一下两个方法，用于 at 界面，当搜索框清空时，关注的人这个列表，是否请求网络数据
        boolean refreshExtraData();
        void refreshExtraData(boolean refresh);
        String getKeyWord();
    }
    interface Presenter extends ITSListPresenter<UserInfoBean> {
        void searchUser(String name,int offset, boolean isLoadMore);
        void getFollowListFromNet(final long userId, int maxId, boolean isLoadMore);
    }
}

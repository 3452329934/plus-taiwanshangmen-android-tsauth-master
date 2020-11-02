package com.zhiyicx.thinksnsplus.modules.search.history;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.SearchHistoryBean;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/10:42
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface SearchHistoryContract {
    interface View extends ITSListView<SearchHistoryBean, Presenter> {
    }

    interface Presenter extends ITSListPresenter<SearchHistoryBean> {
        void deleteHistory();

        void deleteSearchHistory(String content);
    }
}

package com.zhiyicx.thinksnsplus.modules.circle.pre;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/9:35
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface PreCircleContract {
    interface View extends ITSListView<CirclePostListBean, Presenter> {
        Long getCircleId();

        void updateHeaderInfo(CircleInfo circleInfo);
    }

    interface Presenter extends ITSListPresenter<CirclePostListBean> {
        void dealCircleJoinOrExit(CircleInfo circleInfo,String psd);
        void canclePay();
    }
}

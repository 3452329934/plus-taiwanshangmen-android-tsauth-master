package com.zhiyicx.thinksnsplus.modules.circle.detailv2.v2;

import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailContract;

import dagger.Module;
import dagger.Provides;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/29/16:42
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class CircleDetailModuleV2 {
    CircleDetailContract.ViewV2 mViewV2;

    public CircleDetailModuleV2(CircleDetailContract.ViewV2 viewV2) {
        mViewV2 = viewV2;
    }

    @Provides
    CircleDetailContract.ViewV2 provideView() {
        return mViewV2;
    }
}

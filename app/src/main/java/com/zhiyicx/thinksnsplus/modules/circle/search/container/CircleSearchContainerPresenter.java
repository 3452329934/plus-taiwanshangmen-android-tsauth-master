package com.zhiyicx.thinksnsplus.modules.circle.search.container;

import com.zhiyicx.thinksnsplus.base.AppBasePresenter;

import javax.inject.Inject;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/8/7
 * @Contact master.jungle68@gmail.com
 */

public class CircleSearchContainerPresenter extends AppBasePresenter<CircleSearchContainerContract.View>
        implements CircleSearchContainerContract.Presenter {

    @Inject
    public CircleSearchContainerPresenter(CircleSearchContainerContract.View rootView) {
        super(rootView);
    }

}

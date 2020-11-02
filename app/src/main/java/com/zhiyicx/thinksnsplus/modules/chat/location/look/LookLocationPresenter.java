package com.zhiyicx.thinksnsplus.modules.chat.location.look;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2018/1/10
 * @contact email:648129313@qq.com
 */

public class LookLocationPresenter extends AppBasePresenter<LookLocationContract.View>
        implements LookLocationContract.Presenter{

    @Inject
    public LookLocationPresenter(LookLocationContract.View rootView) {
        super(rootView);
    }
}

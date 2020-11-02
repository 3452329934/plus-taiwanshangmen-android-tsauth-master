package com.zhiyicx.thinksnsplus.modules.chat.location.send;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.base.AppComponent;
import com.zhiyicx.thinksnsplus.base.InjectComponent;

import dagger.Component;

/**
 * @Author Jliuer
 * @Date 2018/06/12/15:33
 * @Email Jliuer@aliyun.com
 * @Description
 */
@FragmentScoped
@Component(dependencies = AppComponent.class,modules = SendLocationPresenterModule.class)
public interface SendLocationComponent extends InjectComponent<SendLocationActivity> {
}

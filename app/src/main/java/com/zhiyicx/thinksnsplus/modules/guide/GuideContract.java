package com.zhiyicx.thinksnsplus.modules.guide;

import android.app.Activity;
import android.content.Context;

import com.zhiyicx.common.mvp.i.IBasePresenter;
import com.zhiyicx.common.mvp.i.IBaseView;
import com.zhiyicx.thinksnsplus.data.beans.AllAdverListBean;
import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBean;
import com.zhiyicx.baseproject.base.SystemConfigBean;

import java.util.List;

import rx.Observable;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/10
 * @Contact master.jungle68@gmail.com
 */

public interface GuideContract {
    /**
     * 对于经常使用的关于 UI 的方法可以定义到 BaseView 中,如显示隐藏进度条,和显示文字消息
     */
    interface View extends IBaseView<Presenter> {
        /**
         * 跳转
         *
         * @param tClass
         */
        void startActivity(Class tClass);

        /**
         *
         * @return 当前 Activity
         */
        Activity getCurrentActivity();

        /**
         * 返回广告数据
         * @param bootAdverts 广告数据
         */
        void initAdvertData(List<RealAdvertListBean> bootAdverts);
    }


    interface Presenter extends IBasePresenter {

        void checkLogin();

        SystemConfigBean getAdvert();

        void getLaunchAdverts();
        /**
         * 获取引导页广告数据
         */
        void getBootAdvert();

        void initConfig();

    }

}

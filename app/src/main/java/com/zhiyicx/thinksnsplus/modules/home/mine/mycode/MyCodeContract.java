package com.zhiyicx.thinksnsplus.modules.home.mine.mycode;

import android.graphics.Bitmap;

import com.zhiyicx.baseproject.base.IBaseTouristPresenter;
import com.zhiyicx.baseproject.widget.EmptyView;
import com.zhiyicx.common.mvp.i.IBaseView;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

/**
 * @author Catherine
 * @describe
 * @date 2017/12/8
 * @contact email:648129313@qq.com
 */

public interface MyCodeContract {

    interface View extends IBaseView<Presenter> {
        /**
         * 设置生成的二维码图片,添加了logo的二维码
         *
         * @param codePic 生成的图片结果
         */
        void setMyCode(Bitmap codePic);

        /**
         * 设置生成的二维码图片，还未添加logo的二维码
         *
         * @param codePic 生成的图片结果
         */
        void setCodeInfo(Bitmap codePic);

        /**
         * 设置用户信息
         *
         * @param userInfo 用户信息
         */
        void setUserInfo(UserInfoBean userInfo);

        EmptyView getEmptyView();
    }

    interface Presenter extends IBaseTouristPresenter {
        /**
         * 生成用户二维码图片
         */
        void createUserCodePic();

        /**
         * 获取用户信息
         */
        void getUserInfo();

        /**
         * 分享二维码
         */
        void shareMyQrCode(Bitmap bitmap);

        /**
         * 给生成的二维码添加图片
         *
         * @param codeTemp 二维码
         * @param logo     用户图片
         */
        void addLogoToCode(Bitmap codeTemp, Bitmap logo);
    }

}

package com.zhiyicx.thinksnsplus.modules.home.message.messagereview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.UserFollowerCountBean;

/**
 * @Author Jliuer
 * @Date 2017/7/5/21:25
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageReviewActivity extends TSActivity<MessageReviewPresenter, MessageReviewFragment> {

    @Override
    protected void componentInject() {
        DaggerMessageReviewComponent
                .builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .messageReviewPresenterModule(new MessageReviewPresenterModule(mContanierFragment))
                .build()
                .inject(this);
    }

    @Override
    protected MessageReviewFragment getFragment() {
        return MessageReviewFragment.newInstance(getIntent().getExtras());
    }


    public static void startMessageReviewActivity(Context context, int index) {
        Bundle bundle = new Bundle();
        Intent group = new Intent(context, MessageReviewActivity.class);
        UserFollowerCountBean countBean = new UserFollowerCountBean();
        UserFollowerCountBean.UserBean userBean = new UserFollowerCountBean.UserBean();
        switch (index) {
            case 0:
                userBean.setFeedCommentPinned(1);
                break;
            case 1:
                userBean.setNewsCommentPinned(1);
                break;
            case 2:
                userBean.setPostCommentPinned(1);
                break;
            case 3:
                userBean.setPostPinned(1);
                break;
            case 4:
                userBean.setGroupJoinPinned(1);
                break;
            default:
                userBean.setFeedCommentPinned(1);
        }
        countBean.setUser(userBean);
        bundle.putParcelable(MessageReviewFragment.BUNDLE_PINNED_DATA, countBean);
        group.putExtras(bundle);
        context.startActivity(group);
    }

}

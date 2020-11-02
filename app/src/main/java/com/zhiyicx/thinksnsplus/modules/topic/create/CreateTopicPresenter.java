package com.zhiyicx.thinksnsplus.modules.topic.create;

import android.text.TextUtils;

import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.UploadTaskResult;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseTopicRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UpLoadRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

import static com.zhiyicx.thinksnsplus.data.beans.UploadTaskParams.Storage.CHANNEL_PUBLIC;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/10:33
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CreateTopicPresenter extends AppBasePresenter<CreateTopicContract.View> implements CreateTopicContract.Presenter {

    @Inject
    UpLoadRepository mUpLoadRepository;

    @Inject
    BaseTopicRepository mTopicRepository;

    @Inject
    public CreateTopicPresenter(CreateTopicContract.View rootView) {
        super(rootView);
    }

    @Override
    public void createOrModifyTopic(String name, String desc, String image) {
        boolean isModify = mRootView.getModifyTopic() != null;
        if (TextUtils.isEmpty(image)) {
            if (isModify) {
                // 修改话题信息
                mTopicRepository.modifyTopic(mRootView.getModifyTopic().getId(), desc, null)
                        .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R.string.modify_topic_doing)))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscribeForV2<Object>() {
                            @Override
                            protected void onSuccess(Object data) {
                                mRootView.showSnackSuccessMessage(mContext.getString(R.string.modify_topic_success));
                            }

                            @Override
                            protected void onFailure(String message, int code) {
                                mRootView.showSnackErrorMessage(message);
                            }

                            @Override
                            protected void onException(Throwable throwable) {
                                mRootView.showSnackErrorMessage(throwable.getMessage());
                            }
                        });
            } else {
                mTopicRepository.createTopic(name, desc, null)
                        .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R.string.create_topic_doing)))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscribeForV2<BaseJsonV2<Integer>>() {
                            @Override
                            protected void onSuccess(BaseJsonV2<Integer> data) {
                                mRootView.setTopicId((long) data.getId());
                                if (data.getStatus()) {
                                    mRootView.showSnackMessage(mContext.getString(R.string.create_topic_success_wait), Prompt.DONE);
                                } else {
                                    mRootView.showSnackSuccessMessage(mContext.getString(R.string.create_topic_success));
                                }
                            }

                            @Override
                            protected void onFailure(String message, int code) {
                                mRootView.showSnackErrorMessage(message);
                            }

                            @Override
                            protected void onException(Throwable throwable) {
                                mRootView.showSnackErrorMessage(throwable.getMessage());
                            }
                        });
            }
        } else {
            mUpLoadRepository.doUpLoadImageTaskWithCompress(mContext, image, CHANNEL_PUBLIC, null)
                    .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(isModify ?
                            R.string.modify_topic_doing : R.string.create_topic_doing)))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .flatMap((Func1<UploadTaskResult, Observable<BaseJsonV2<Integer>>>) result -> {
                        if (mRootView.getModifyTopic() != null) {
                            return mTopicRepository.modifyTopic(mRootView.getModifyTopic().getId(),
                                    desc, result.getNode()).flatMap((Func1<Object, Observable<BaseJsonV2<Integer>>>) o -> {
                                BaseJsonV2<Integer> data = new BaseJsonV2<>();
                                data.setData(mRootView.getModifyTopic().getId().intValue());
                                return Observable.just(data);
                            });
                        }
                        return mTopicRepository.createTopic(name, desc, result.getNode());
                    })
                    .subscribe(new BaseSubscribeForV2<BaseJsonV2<Integer>>() {
                        @Override
                        protected void onSuccess(BaseJsonV2<Integer> data) {
                            mRootView.setTopicId((long) data.getId());
                            if (data.getStatus()) {
                                mRootView.showSnackMessage(mContext.getString(R.string.create_topic_success_wait), Prompt.DONE);
                            } else {
                                mRootView.showSnackSuccessMessage(mContext.getString(isModify ? R.string.modify_topic_success : R.string.create_topic_success));
                            }
                        }

                        @Override
                        protected void onFailure(String message, int code) {
                            mRootView.showSnackErrorMessage(message);
                        }

                        @Override
                        protected void onException(Throwable throwable) {
                            mRootView.showSnackErrorMessage(throwable.getMessage());
                        }
                    });
        }
    }
}

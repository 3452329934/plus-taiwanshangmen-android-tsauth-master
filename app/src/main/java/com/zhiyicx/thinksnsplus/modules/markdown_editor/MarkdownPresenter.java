package com.zhiyicx.thinksnsplus.modules.markdown_editor;

import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.net.listener.ProgressRequestBody;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.data.beans.BaseDraftBean;
import com.zhiyicx.thinksnsplus.data.source.repository.UpLoadRepository;
import com.zhiyicx.thinksnsplus.service.backgroundtask.ImageCompress;
import com.zycx.luban.CompressionPredicate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Author Jliuer
 * @Date 2017/11/17/17:36
 * @Email Jliuer@aliyun.com
 * @Description
 */
public abstract class MarkdownPresenter<View extends MarkdownContract.View> extends AppBasePresenter<View> implements MarkdownContract.Presenter {

    @Inject
    UpLoadRepository mUpLoadRepository;

    public MarkdownPresenter(View rootView) {
        super(rootView);
    }

    /**
     * 上传图片，进度监听
     *
     * @param filePath
     * @param tagId
     */
    @Override
    public void uploadPic(String filePath, long tagId) {

        Subscription subscribe =Observable.just(filePath)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    try {
                        return ImageCompress
                                .with(mContext)
                                .ignoreBy(200)
                                .filter(path -> !path.toLowerCase().endsWith(".gif"))
                                .setRenameListener(path -> {
                                    try {
                                        MessageDigest md = MessageDigest.getInstance("MD5");
                                        md.update(path.getBytes());
                                        return new BigInteger(1, md.digest()).toString(32);
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                    return "";
                                }).get(s).getAbsolutePath();
                    }catch (Exception e){
                        throw new IllegalArgumentException(e);
                    }
                }).flatMap((Func1<String, Observable<BaseJson<Integer>>>) filePath1 -> mUpLoadRepository.upLoadFileWithProgress(filePath1, "", true, 0, 0, new ProgressRequestBody.ProgressRequestListener() {
                    @Override
                    public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                        float progress = 0f;
                        if (contentLength > 0) {
                            progress = (float) ((double) bytesWritten / (double) contentLength) * 100;
                        }
                        if (progress == 100) {
                            return;
                        }
                        mRootView.onUploading(tagId, filePath1, (int) progress, -1);
                    }
                })).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<Integer>() {
                    @Override
                    protected void onSuccess(Integer data) {
                        mRootView.onUploading(tagId, filePath, 100, data);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage("图片上传失败");
                        mRootView.onFailed(filePath, tagId);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.showSnackErrorMessage("图片上传失败");
                        mRootView.onFailed(filePath, tagId);
                    }
                });
//        Subscription subscribe = mUpLoadRepository.upLoadFileWithProgress(filePath, "", true, 0, 0, (bytesWritten,
//                                                                                                     contentLength, done) ->
//        {
//
//            float progress = 0f;
//            if (contentLength > 0) {
//                progress = (float) ((double) bytesWritten / (double) contentLength) * 100;
//            }
//            if (progress == 100) {
//                return;
//            }
//            mRootView.onUploading(tagId, filePath, (int) progress, -1);
//
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseSubscribe<Integer>() {
//                    @Override
//                    protected void onSuccess(Integer data) {
//                        mRootView.onUploading(tagId, filePath, 100, data);
//                    }
//
//                    @Override
//                    protected void onFailure(String message, int code) {
//                        super.onFailure(message, code);
//                        mRootView.showSnackErrorMessage("图片上传失败");
//                        mRootView.onFailed(filePath, tagId);
//                    }
//
//                    @Override
//                    protected void onException(Throwable throwable) {
//                        super.onException(throwable);
//                        mRootView.showSnackErrorMessage("图片上传失败");
//                        mRootView.onFailed(filePath, tagId);
//                    }
//                });
        addSubscrebe(subscribe);

    }

    /**
     * 保存草稿
     * @param postDraftBean
     */
    @Override
    public void saveDraft(BaseDraftBean postDraftBean) {

    }

}

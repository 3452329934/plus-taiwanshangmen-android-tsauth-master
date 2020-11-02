package com.zhiyicx.thinksnsplus.data.source.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.common.net.UpLoadFile;
import com.zhiyicx.common.net.listener.ProgressRequestBody;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.rxerrorhandler.functions.RetryWithInterceptDelay;
import com.zhiyicx.thinksnsplus.data.beans.UpdateUserInfoTaskParams;
import com.zhiyicx.thinksnsplus.data.beans.UploadTaskParams;
import com.zhiyicx.thinksnsplus.data.beans.UploadTaskResult;
import com.zhiyicx.thinksnsplus.data.source.remote.CommonClient;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.remote.UserInfoClient;
import com.zhiyicx.thinksnsplus.data.source.repository.i.IUploadRepository;
import com.zhiyicx.thinksnsplus.service.backgroundtask.ImageCompress;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zhiyicx.thinksnsplus.data.beans.UploadTaskParams.Storage.CHANNEL_PUBLIC;

/**
 * @author LiuChao
 * @describe 上传文件的实现类, 通过dagger注入
 * @date 2017/1/22
 * @contact email:450127106@qq.com
 */

public class UpLoadRepository implements IUploadRepository {
    private CommonClient mCommonClient;
    private UserInfoClient mUserInfoClient;

    // 这个用于服务器校检 hash
    private static final int RETRY_MAX_COUNT = 2; // 最大重试次
    private static final int RETRY_INTERVAL_TIME = 2; // 循环间隔时间 单位 s

    @Inject
    public UpLoadRepository(ServiceManager serviceManager) {
        mCommonClient = serviceManager.getCommonClient();
        mUserInfoClient = serviceManager.getUserInfoClient();
    }

    @Override
    public Observable<BaseJson<Integer>> upLoadImageWithCompress(Context context, String filePath, int photoWidth, int photoHeight) {
        return Observable.just(filePath)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    try {
                        return ImageCompress
                                .with(context)
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
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                }).flatMap((Func1<String, Observable<BaseJson<Integer>>>) path -> upLoadSingleFileV2(path, "", true, photoWidth, photoHeight));
    }

    @Override
    public Observable<UploadTaskResult> doUpLoadImageTaskWithCompress(Context context, String filePath, String channel,
                                                                      ProgressRequestBody.ProgressRequestListener listener) {
        return Observable.just(filePath)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    try {
                        return ImageCompress
                                .with(context)
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
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                }).map(s -> new UploadTaskParams(s, channel))
                .flatMap((Func1<UploadTaskParams, Observable<UploadTaskResult>>) params -> doUploadTask(params, listener)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
    }


    /**
     * @author Jliuer
     * @Date 18/04/04 15:49
     * @Email Jliuer@aliyun.com
     * @Description 上传单个文件
     */
    @Override
    public Observable<BaseJson<Integer>> upLoadSingleFileV2(final String filePath, String mimeType, boolean isPic, int photoWidth, int photoHeight) {
        File file = new File(filePath);
        // 封装上传文件的参数
        final HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("hash", FileUtils.getFileMD5ToString(file));
        LogUtils.d("filePath::" + filePath);
        LogUtils.d("upLoadSingleFileV2::" + paramMap.get("hash"));
        paramMap.put("origin_filename", file.getName());
        if (photoWidth != 0 && photoHeight != 0) {
            paramMap.put("width", photoWidth + "");
            paramMap.put("height", photoHeight + "");
        }
        // 如果是图片就处理图片
        if (isPic) {
            paramMap.put("mime_type", mimeType);
        } else {
            paramMap.put("mime_type", FileUtils.getMimeTypeByFile(file));
        }
        return checkStorageHash(paramMap.get("hash"))
                .retryWhen(new RetryWithInterceptDelay(RETRY_MAX_COUNT, RETRY_INTERVAL_TIME) {
                    @Override
                    protected boolean extraReTryCondition(Throwable throwable) {
                        // 文件不存在 服务器返回404.
                        return !throwable.toString().contains("404");
                    }
                })
                .onErrorReturn(throwable -> {
                    BaseJsonV2 baseJson = new BaseJsonV2();
                    baseJson.setId(-1);
                    return baseJson;
                })
                .flatMap(baseJson -> {
                    if (baseJson.getId() != -1) {
                        BaseJson<Integer> success = new BaseJson<>();
                        success.setData(baseJson.getId());
                        success.setStatus(true);
                        return Observable.just(success);
                    } else {
                        // 封装图片File
                        HashMap<String, String> fileMap = new HashMap<>();
                        fileMap.put("file", filePath);
                        Map<String, Object> params = new HashMap<>(paramMap);
                        return mCommonClient.upLoadFileByPostV2(UpLoadFile.upLoadFileAndParams(fileMap, params))
                                .flatMap(uploadFileResultV2 -> {
                                    BaseJson<Integer> success = new BaseJson<>();
                                    success.setData(uploadFileResultV2.getId());
                                    baseJson.setId(1);
                                    success.setStatus(true);
                                    return Observable.just(success);
                                }, Observable::error, () -> null);

                    }
                });
    }

    @Override
    public Observable<UploadTaskResult> doUploadTask(UploadTaskParams taskParams,
                                                     ProgressRequestBody.ProgressRequestListener listener) {
        return mCommonClient.createUploadTask(taskParams)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Func1<UploadTaskResult, Observable<UploadTaskResult>>) uploadTaskResult -> {
                    HashMap<String, Object> paramMap = new HashMap<>();
                    HashMap<String, String> fileMap = new HashMap<>();
                    fileMap.put("file", taskParams.getFile());
                    if (uploadTaskResult.getForm() != null && uploadTaskResult.getForm() != null) {
                        paramMap.putAll(uploadTaskResult.getForm());
                    }
                    // file_key
                    if (!TextUtils.isEmpty(uploadTaskResult.getFile_key())) {
                        paramMap.put("file_key", uploadTaskResult.getFile_key());
                    }

                    if (paramMap.size() == 0) {
                        paramMap = null;
                    }

                    Map<String, RequestBody> part = UpLoadFile.upLoadPartFileAndProgress(fileMap, paramMap, listener);
                    Map<String, String> header = new HashMap<>();
                    if (uploadTaskResult.getHeaders() != null) {
                        header.putAll(uploadTaskResult.getHeaders());
                    }
//                    header.put("Accept", "application/json");
                    String url = uploadTaskResult.getUri();
//                    try {
//                        url = URLDecoder.decode(url, "UTF-8");
//                    } catch (Exception ignore) {
//                    }
//                    LogUtils.d("url:::" + url);
                    Observable<UploadTaskResult> result;
                    ProgressRequestBody body;
                    try {
                        body = UpLoadFile.upLoadFileAndProgress(taskParams.getFile(), null);
                    } catch (Exception e) {
                        return Observable.just(new UploadTaskResult(taskParams.getFile()));
                    }
                    switch (uploadTaskResult.getMethod()) {
                        case "PUT":
                            result = mCommonClient.doPutUploadTask(url, header, body);
                            break;
                        case "POST":
                            result = mCommonClient.doPostUploadTask(url, header, body);
                            break;
                        default:
                            result = Observable.just(new UploadTaskResult(taskParams.getFile()));
                    }
                    return result;
                });
    }

    /**
     * @param position 记录上传成功的个数
     * @author Jliuer
     * @Date 18/04/04 15:50
     * @Email Jliuer@aliyun.com
     * @Description 上传单个文件
     */
    @Override
    public Observable<BaseJson<Integer>> upLoadSingleFileV2(final String filePath, String mimeType, boolean isPic,
                                                            int photoWidth, int photoHeight, int[] position) {
        File file = new File(filePath);
        // 封装上传文件的参数
        final HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("hash", FileUtils.getFileMD5ToString(file));
        LogUtils.d("filePath::" + filePath);
        LogUtils.d("upLoadSingleFileV2::" + paramMap.get("hash"));
        paramMap.put("origin_filename", file.getName());
        paramMap.put("width", photoWidth + "");
        paramMap.put("height", photoHeight + "");
        // 如果是图片就处理图片
        if (isPic && !TextUtils.isEmpty(mimeType)) {
            paramMap.put("mime_type", mimeType);
        } else {
            mimeType = FileUtils.getMimeTypeByFile(new File(filePath));
            paramMap.put("mime_type", mimeType);
        }
        return checkStorageHash(paramMap.get("hash"))
                .retryWhen(new RetryWithInterceptDelay(RETRY_MAX_COUNT, RETRY_INTERVAL_TIME) {
                    @Override
                    protected boolean extraReTryCondition(Throwable throwable) {
                        // 文件不存在 服务器返回404,则 进行 重传文件，不再校验hash
                        // 文件超出大小 服务器返回422,则 抛出，不再校验hash
                        boolean is404 = false;
                        boolean is422 = false;
                        if (throwable instanceof HttpException) {
                            HttpException exception = (HttpException) throwable;
                            is404 = exception.code() == 404;
                            is422 = exception.code() == 422;
                        }
                        return !is404 && !is422;
                    }
                })
                .onErrorReturn(throwable -> {
                    BaseJsonV2 baseJson = new BaseJsonV2();
                    baseJson.setId(-1);
                    return baseJson;
                })
                .flatMap(baseJson -> {
                    if (baseJson.getId() != -1) {
                        BaseJson<Integer> success = new BaseJson<>();
                        success.setData(baseJson.getId());
                        success.setStatus(true);
                        return Observable.just(success);
                    } else {
                        // 封装图片File
                        HashMap<String, String> fileMap = new HashMap<>();
                        fileMap.put("file", filePath);
                        Map<String, Object> params = new HashMap<>(paramMap);
                        return mCommonClient.upLoadFileByPostV2(UpLoadFile.upLoadFileAndParams(fileMap, params))
                                .flatMap(uploadFileResultV2 -> {
                                    BaseJson<Integer> success = new BaseJson<>();
                                    success.setData(uploadFileResultV2.getId());
                                    baseJson.setId(1);
                                    success.setStatus(true);
                                    return Observable.just(success);
                                }, Observable::error, () -> null);

                    }
                });
    }

    /**
     * @author Jliuer
     * @Date 18/04/04 15:51
     * @Email Jliuer@aliyun.com
     * @Description 上传单个文件，带进度监听
     */
    public Observable<BaseJson<Integer>> upLoadFileWithProgress(final String filePath, String mimeType,
                                                                boolean isPic, int photoWidth, int photoHeight,
                                                                ProgressRequestBody.ProgressRequestListener listener) {
        File file = new File(filePath);
        // 封装上传文件的参数
        final HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("hash", FileUtils.getFileMD5ToString(file));
        paramMap.put("origin_filename", file.getName());
        // 如果是图片就处理图片
        if (isPic) {
            paramMap.put("mime_type", mimeType);
            if (photoWidth != 0 && photoHeight != 0) {
                paramMap.put("width", photoWidth + "");// 如果是图片就选择宽高
                paramMap.put("height", photoHeight + "");// 如果是图片就选择宽高
            }
        } else {
            paramMap.put("mime_type", FileUtils.getMimeTypeByFile(file));
        }
        return checkStorageHash(paramMap.get("hash"))
                .retryWhen(new RetryWithInterceptDelay(RETRY_MAX_COUNT, RETRY_INTERVAL_TIME) {
                    @Override
                    protected boolean extraReTryCondition(Throwable throwable) {
                        return !throwable.toString().contains("404"); // 文件不存在 服务器返回404.
                    }
                })
                .onErrorReturn(throwable -> {
                    BaseJsonV2 baseJson = new BaseJsonV2();
                    baseJson.setId(-1);
                    return baseJson;
                })
                .flatMap(baseJson -> {
                    if (baseJson.getId() != -1) {
                        BaseJson<Integer> success = new BaseJson<>();
                        success.setData(baseJson.getId());
                        success.setStatus(true);
                        return Observable.just(success);
                    } else {
                        // 封装图片File
                        HashMap<String, String> fileMap = new HashMap<>();
                        fileMap.put("file", filePath);
                        return mCommonClient.upLoadFileByPostV2(UpLoadFile.upLoadFileAndProgress(fileMap, listener))
                                .flatMap(uploadFileResultV2 -> {
                                    BaseJson<Integer> success = new BaseJson<>();
                                    success.setData(uploadFileResultV2.getId());
                                    baseJson.setId(1);
                                    success.setStatus(true);
                                    return Observable.just(success);
                                }, Observable::error, () -> null);

                    }
                });
    }

    @Override
    public Observable<BaseJsonV2> checkStorageHash(String hash) {
        return mCommonClient.checkStorageHash(hash);
    }

    /**
     * 更新用户头像
     *
     * @param filePath
     * @return
     */
    @Override
    public Observable<Object> uploadAvatar(String filePath) {
        return mUserInfoClient.updateAvatar(getMultipartBody(filePath, "avatar"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<UploadTaskResult> uploadUserAvatar(String filePath) {
        UploadTaskParams params = new UploadTaskParams(filePath, CHANNEL_PUBLIC);
        return doUploadTask(params, null)
                .flatMap((Func1<UploadTaskResult, Observable<UploadTaskResult>>) uploadTaskResult -> {
                    UpdateUserInfoTaskParams userInfoTaskParams = new UpdateUserInfoTaskParams();
                    userInfoTaskParams.setAvatar(uploadTaskResult.getNode());
                    return mUserInfoClient.updateUserInfo(userInfoTaskParams)
                            .flatMap((Func1<Object, Observable<UploadTaskResult>>) o -> Observable.just(uploadTaskResult));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> uploadBg(String filePath) {
        return mUserInfoClient.updateBg(getMultipartBody(filePath, "image"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<UploadTaskResult> uploadUserBg(String filePath) {
        UploadTaskParams params = new UploadTaskParams(filePath, CHANNEL_PUBLIC);
        return doUploadTask(params, null)
                .flatMap((Func1<UploadTaskResult, Observable<UploadTaskResult>>) uploadTaskResult -> {
                    UpdateUserInfoTaskParams userInfoTaskParams = new UpdateUserInfoTaskParams();
                    userInfoTaskParams.setBg(uploadTaskResult.getNode());
                    return mUserInfoClient.updateUserInfo(userInfoTaskParams)
                            .flatMap((Func1<Object, Observable<UploadTaskResult>>) o -> Observable.just(uploadTaskResult));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private MultipartBody getMultipartBody(String filePath, String key) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //filePath 图片地址
        File file = new File(filePath);
        String mimeType = FileUtils.getMimeTypeByFile(file);
        RequestBody imageBody = RequestBody.create(
                MediaType.parse(TextUtils.isEmpty(mimeType) ? "multipart/form-data" : mimeType), file);
        //imgfile 后台接收图片流的参数名
        builder.addFormDataPart(key, file.getName(), imageBody);
        //设置类型
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

}

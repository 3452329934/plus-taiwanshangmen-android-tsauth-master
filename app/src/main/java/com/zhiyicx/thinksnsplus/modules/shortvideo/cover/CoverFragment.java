package com.zhiyicx.thinksnsplus.modules.shortvideo.cover;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyi.videotrimmerlibrary.utils.VideoUtils;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.base.EmptySubscribe;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.VideoCoverView;
import com.zhiyicx.thinksnsplus.modules.shortvideo.record.RecordActivity;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;
import com.zycx.shortvideo.filter.helper.MagicFilterType;
import com.zycx.shortvideo.media.MediaPlayerWrapper;
import com.zycx.shortvideo.media.VideoInfo;
import com.zycx.shortvideo.mediacodec.VideoClipper;
import com.zycx.shortvideo.recodrender.ParamsManager;
import com.zycx.shortvideo.recordcore.VideoListManager;
import com.zycx.shortvideo.utils.FileUtils;
import com.zycx.shortvideo.utils.TrimVideoUtil;
import com.zycx.shortvideo.view.VideoPreviewView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.observables.AsyncOnSubscribe;
import rx.schedulers.Schedulers;

import static cn.jzvd.JZVideoPlayer.onAudioFocusChangeListener;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/04/07
 * @Email Jliuer@aliyun.com
 * @Description 选择封面 && 动态发布页预览
 */
public class CoverFragment extends TSFragment implements MediaPlayerWrapper.IMediaCallback, VideoCoverView.OnScrollDistanceListener {
    public static final String PATH = "path";
    public static final String PREVIEW = "preview";
    public static final String FILTER = "filter";
    public static final String BACKTORECORD = "backtorecord";
    public static final int REQUEST_COVER_CODE = 1000;
    public static final int REQUEST_DELETE_CODE = 2000;

    @BindView(R.id.videoView)
    VideoPreviewView mVideoView;
    @BindView(R.id.tv_toolbar_right)
    TextView mToolbarRight;
    @BindView(R.id.tv_toolbar_left)
    TextView mToolbarLeft;
    @BindView(R.id.tv_toolbar_center)
    TextView mToolbarCenter;
    @BindView(R.id.rl_toolbar)
    RelativeLayout mToolBar;
    @BindView(R.id.vc_cover_container)
    VideoCoverView mVideoCoverView;


    private ProgressDialog mProgressDialog;
    private boolean isPre;
    private boolean hasFilter;
    private boolean mResumed;
    private boolean mBack2Record;

    private Subscription mSubscription;

    private VideoInfo mVideoInfo;
    private ArrayList<String> mSrcList;
    private Bitmap mCoverBitmap;

    private ActionPopupWindow mCanclePopupWindow;

    public static CoverFragment newInstance(Bundle bundle) {
        CoverFragment fragment = new CoverFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View getRightViewOfMusicWindow() {
        return mToolbarRight;
    }

    @Override
    protected int setToolBarBackgroud() {
        return R.color.black_deep;
    }

    @Override
    public void onBackPressed() {
        mToolbarLeft.performClick();
    }

    @Override
    protected boolean setUseSatusbar() {
        return DeviceUtils.getHeight(mActivity) == 0;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int setLeftImg() {
        return R.mipmap.topbar_back_white;
    }

    @Override
    protected void initView(View rootView) {
        mToolbarCenter.setText(R.string.clip_cover);
        mToolbarLeft.setText(R.string.cancel);
        mToolbarRight.setText(R.string.complete);
        mToolbarRight.setEnabled(false);
        initListener();
    }


    private void initListener() {
        RxView.clicks(mToolbarRight)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                    if (isPre) {
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    } else {
                        getVideoCover();
                    }
                });

        RxView.clicks(mToolbarLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                    if (mSubscription != null && !mSubscription.isUnsubscribed()) {
                        mSubscription.unsubscribe();
                    }
                    if (isPre) {
                        mActivity.setResult(Activity.RESULT_CANCELED);
                        mActivity.finish();
                    } else if (!hasFilter) {
                        if (mBack2Record) {
                            TopicListBean topic = null;
                            if (getArguments() != null) {
                                topic = getArguments().getParcelable(SearchTopicFragment.TOPIC);
                            }
                            RecordActivity.startRecordActivity(mActivity, topic);
                        }
                        mActivity.finish();
                    }

                });

        mVideoView.setIMediaCallback(this);

    }

    private void getVideoCover() {
        if (mCoverBitmap == null) {
            return;
        }
        buildDialog(getString(R.string.dealing));
        String cover = com.zhiyicx.common.utils.FileUtils.saveBitmapToFile(mActivity,
                mCoverBitmap,
                System.currentTimeMillis() + ParamsManager.VideoCover);

        if (hasFilter) {
            mProgressDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString(PATH, cover);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        } else {
            combineVideo(cover);
        }

    }

    @Override
    protected void initData() {
        mSrcList = getArguments().getStringArrayList(PATH);
        if (mSrcList == null || mSrcList.isEmpty()) {
            throw new IllegalArgumentException("video path can not be null");
        }
        mVideoInfo = new VideoInfo();
        isPre = getArguments().getBoolean(PREVIEW);
        hasFilter = getArguments().getBoolean(FILTER);
        mBack2Record = getArguments().getBoolean(BACKTORECORD);
        mVideoView.setVideoPath(mSrcList);

        if (isPre) {
            mVideoCoverView.setVisibility(View.GONE);
            mToolbarCenter.setText(R.string.preview);
            mToolbarLeft.setText("");
            mToolbarLeft.setCompoundDrawables(UIUtils.getCompoundDrawables(getContext(), setLeftImg()), null, null, null);
            mToolbarRight.setText(R.string.delete);
            mToolbarRight.setTextColor(ContextCompat.getColorStateList(getContext(), com.zhiyicx.baseproject.R.color.selector_text_color));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mResumed && isPre) {
            mVideoView.start();
        }
        mResumed = true;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_cover;
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    public void onDestroyView() {
        mVideoView.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        if (mCoverBitmap != null && !mCoverBitmap.isRecycled()) {
            mCoverBitmap.recycle();
        }

        super.onDestroyView();
    }

    @Override
    public void changeTo(long millisecond) {
        mVideoView.seekTo((int) millisecond);
        mVideoView.takePic((bitmap, integer) -> mActivity.runOnUiThread(() -> {
            mCoverBitmap = bitmap;
            mVideoCoverView.setImageBitmap(mCoverBitmap);
        }));
    }

    @Override
    public void onVideoPrepare() {
        //获取音频焦点
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context
                .AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager
                .STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        mToolbarRight.setEnabled(true);
        if (isPre) {
            mVideoView.start();
        } else {
            mVideoView.seekTo(1);
            getCoverImageList();
            mVideoCoverView.setOnScrollDistanceListener(this);
        }
    }

    @Override
    public void onVideoStart() {

    }

    @Override
    public void onVideoPause() {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isPre) {
            mVideoView.seekTo(0);
            mVideoView.start();
        }
    }

    @Override
    public void onVideoChanged(VideoInfo info) {
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mActivity, "", msg);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }


    private void combineVideo(String cover) {
        mSubscription = Observable
                .create(AsyncOnSubscribe.createStateless(
                        (Action2<Long, Observer<Observable<? extends String>>>) (requested, observer) -> {
                            final String path = FileUtils.getPath(ParamsManager.AlbumPath, System.currentTimeMillis() + ParamsManager.CombineVideo);
                            VideoUtils.INSTANCE.appendMp4List(VideoListManager.getInstance().getSubVideoPathList(), path);
                            observer.onNext(Observable.just(path));
                            observer.onCompleted();
                        }))
                .flatMap((Func1<String, Observable<SendDynamicDataBean>>) dst -> TrimVideoUtil.getVideoOneFrame(dst)
                        .flatMap((Func1<Bitmap, Observable<SendDynamicDataBean>>) bitmap -> {

                            VideoListManager.getInstance().removeAllSubVideo();
                            VideoInfo mVideoInfo = new VideoInfo();

                            mVideoInfo.setPath(dst);
                            mVideoInfo.setCover(cover);
                            mVideoInfo.setCreateTime(System.currentTimeMillis() + "");

                            mVideoInfo.setWidth(mVideoView.getVideoWidth());
                            mVideoInfo.setHeight(mVideoView.getVideoHeight());

                            if (mVideoView.getVideoInfo().get(0).getRotation() == 90) {
                                mVideoInfo.setWidth(mVideoView.getVideoHeight());
                                mVideoInfo.setHeight(mVideoView.getVideoWidth());
                            }
                            mVideoInfo.setDuration(VideoListManager.getInstance().getDuration());

                            SendDynamicDataBean sendDynamicDataBean = new SendDynamicDataBean();
                            sendDynamicDataBean.setDynamicBelong(SendDynamicDataBean
                                    .NORMAL_DYNAMIC);
                            List<ImageBean> pic = new ArrayList<>();
                            ImageBean imageBean = new ImageBean();
                            imageBean.setImgUrl(mVideoInfo.getCover());
                            pic.add(imageBean);
                            sendDynamicDataBean.setDynamicPrePhotos(pic);
                            sendDynamicDataBean.setDynamicType(SendDynamicDataBean
                                    .VIDEO_TEXT_DYNAMIC);
                            sendDynamicDataBean.setVideoInfo(mVideoInfo);
                            return Observable.just(sendDynamicDataBean);
                        }))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> {
                    mToolbarRight.setEnabled(false);
                    buildDialog(getString(R.string.dealing));
                })
                .doAfterTerminate(() -> mProgressDialog.dismiss())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sendDynamicDataBean -> {
                    TopicListBean topic = null;
                    if (getArguments() != null) {
                        topic = getArguments().getParcelable(SearchTopicFragment.TOPIC);
                    }
                    SendDynamicActivity.startToSendDynamicActivity(getContext(),
                            sendDynamicDataBean, topic);
                    mActivity.finish();
                });
    }

    private void clipVideo(String path, String cover) {

        mSubscription = Observable.just(path)
                .subscribe(new BaseSubscribeForV2<String>() {
                    @Override
                    protected void onSuccess(String data) {
                        VideoClipper clipper = new VideoClipper();
                        clipper.showBeauty();
                        clipper.setInputVideoPath(mActivity, path);
                        String mOutputPath = FileUtils.getPath(ParamsManager.SaveVideo, System.currentTimeMillis() + ParamsManager.ClipVideo);
                        clipper.setFilterType(MagicFilterType.NONE);
                        clipper.setOutputVideoPath(mOutputPath);
                        try {
                            clipper.clipVideo(0, mVideoView.getVideoDuration() * 1000);
                        } catch (IOException e) {
                            Observable.error(new IOException());
                        }
                        clipper.setOnVideoCutFinishListener(new VideoClipper.OnVideoCutFinishListener() {
                            @Override
                            public void onFinish() {
                                mSubscription = Observable.empty().subscribe(new EmptySubscribe<Object>() {
                                    @Override
                                    public void onCompleted() {
                                        FileUtils.updateMediaStore(mActivity, mOutputPath, (s, uri) -> {

                                            mVideoInfo.setPath(mOutputPath);
                                            mVideoInfo.setCover(cover);
                                            mVideoInfo.setCreateTime(System.currentTimeMillis() + "");
                                            mVideoInfo.setWidth(mVideoView.getVideoWidth());
                                            mVideoInfo.setHeight(mVideoView.getVideoHeight());
                                            mVideoInfo.setDuration(mVideoView.getVideoDuration());

                                            // 封面

                                            SendDynamicDataBean sendDynamicDataBean = new SendDynamicDataBean();
                                            sendDynamicDataBean.setDynamicBelong(SendDynamicDataBean
                                                    .NORMAL_DYNAMIC);
                                            List<ImageBean> pic = new ArrayList<>();
                                            ImageBean imageBean = new ImageBean();
                                            imageBean.setImgUrl(mVideoInfo.getCover());
                                            pic.add(imageBean);
                                            sendDynamicDataBean.setDynamicPrePhotos(pic);
                                            sendDynamicDataBean.setDynamicType(SendDynamicDataBean
                                                    .VIDEO_TEXT_DYNAMIC);
                                            sendDynamicDataBean.setVideoInfo(mVideoInfo);
                                            SendDynamicActivity.startToSendDynamicActivity(getContext(),
                                                    sendDynamicDataBean);

                                            mProgressDialog.dismiss();
                                            mActivity.finish();
                                        });
                                    }


                                });
                            }

                            @Override
                            public void onFailed() {

                            }
                        });
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void getCoverImageList() {

        List<Observable<ArrayList<VideoCoverView.Video>>> observableList = new ArrayList<>();
        for (String uri : mSrcList) {
            final ArrayList<VideoCoverView.Video> list = new ArrayList<>();
            Observable<ArrayList<VideoCoverView.Video>> observable = Observable.just(uri)
                    .subscribeOn(Schedulers.io())
                    .flatMap((Func1<String, Observable<ArrayList<VideoCoverView.Video>>>) uri1 -> {

                        MediaMetadataRetriever mediaMetadataRetriever =
                                new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(uri1);

                        long videoLengthInMs = Long.parseLong
                                (mediaMetadataRetriever.extractMetadata
                                        (MediaMetadataRetriever
                                                .METADATA_KEY_DURATION)) * 1000;

                        mCoverBitmap = mediaMetadataRetriever
                                .getFrameAtTime(1,
                                        MediaMetadataRetriever
                                                .OPTION_CLOSEST_SYNC);

                        mediaMetadataRetriever.release();
                        long numThumbs = videoLengthInMs < 1000000
                                ? 1 : (videoLengthInMs / 1000000);
                        numThumbs += numThumbs;
                        final long interval = videoLengthInMs / numThumbs;

                        for (long i = 0; i < numThumbs; ++i) {
                            VideoCoverView.Video video = new VideoCoverView.Video(uri1, i * interval);
                            list.add(video);
                        }
                        return Observable.just(list);
                    });
            observableList.add(observable);
        }

        Observable.combineLatest(observableList, args -> {
            ArrayList<VideoCoverView.Video> result = new ArrayList<>();
            for (Object obj : args) {
                if (obj instanceof ArrayList) {
                    result.addAll((ArrayList<VideoCoverView.Video>) obj);
                }
            }
            return result;
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmaps -> mVideoCoverView.addImages(bitmaps));
    }
}

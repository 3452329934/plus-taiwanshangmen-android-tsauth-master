package com.zhiyicx.thinksnsplus.modules.shortvideo.clipe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyi.videotrimmerlibrary.VideoTrimmerView;
import com.zhiyi.videotrimmerlibrary.callback.OnTrimVideoListener;
import com.zhiyi.videotrimmerlibrary.controls.RecyclerViewControl;
import com.zhiyi.videotrimmerlibrary.controls.RegulatorControl;
import com.zhiyi.videotrimmerlibrary.utils.VideoUtils;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.common.utils.ActivityHandler;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.cover.CoverActivity;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;
import com.zycx.shortvideo.interfaces.TrimVideoListener;
import com.zycx.shortvideo.media.VideoInfo;
import com.zycx.shortvideo.recodrender.ParamsManager;
import com.zycx.shortvideo.recordcore.VideoListManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.schedulers.Schedulers;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/03/28/18:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TrimmerFragment extends TSFragment implements TrimVideoListener {

    public static final String PATH = "path";
    public static final String VIDEO = "video";

    @BindView(R.id.trimmer_view)
    VideoTrimmerView mVideoTrimmerView;
    @BindView(R.id.tv_toolbar_right)
    TextView mToolbarRight;
    @BindView(R.id.tv_toolbar_left)
    TextView mToolbarLeft;
    @BindView(R.id.tv_toolbar_center)
    TextView mToolbarCenter;
    @BindView(R.id.rl_toolbar)
    RelativeLayout mToolBar;
    private VideoInfo mVideoInfo;

    private ProgressDialog mProgressDialog;

    /**
     * 跳过获取封面
     */
    private boolean skipCover = true;


    public static TrimmerFragment newInstance(Bundle bundle) {
        TrimmerFragment fragment = new TrimmerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
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
    protected void initView(View rootView) {
        mVideoInfo = getArguments().getParcelable(VIDEO);
        if (skipCover && mVideoInfo == null) {
            mVideoInfo = new VideoInfo();
        }
        setUpView();
    }

    private void setUpView() {
        FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) mToolBar.getLayoutParams());
        params.height = ConvertUtils.dp2px(getContext(), 60);
        mToolBar.setPadding(mToolBar.getPaddingLeft(), getResources().getDimensionPixelOffset(R.dimen.spacing_normal), mToolBar.getPaddingRight(),
                mToolBar.getPaddingBottom());

        String path = getArguments().getString(PATH);
        if (TextUtils.isEmpty(path)) {
            path = mVideoInfo.getPath();
        }
        mVideoTrimmerView.getTrimmerSeekBar().setBitmap(R.mipmap.pic_left, R.mipmap.pic_right);
        mVideoTrimmerView.setVideoPath(path).handle();

        mToolbarCenter.setText(R.string.clip_speed);
        mToolbarRight.setText(R.string.complete);
        mToolbarLeft.setText(R.string.cancel);
        mToolbarRight.setEnabled(false);
        ColorStateList csl = getResources().getColorStateList(R.color.selector_clip_video_color);
        mToolbarRight.setTextColor(csl);
        initListener();
    }

    private void initListener() {
        RxView.clicks(mToolbarRight)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .observeOn(Schedulers.io())
                .subscribe(aVoid -> {
                    mActivity.runOnUiThread(() -> mToolbarRight.setEnabled(false));

                    boolean startPosChanged = mVideoTrimmerView.getTrimmerPos()[0] != 0;
                    // 2s 作为偏差值，误差为 1s; 大于一分钟，或者偏差了2s需要裁剪
                    boolean isNeedTirm = mVideoTrimmerView.getVideoView().getDuration() > 60 * 1000 || mVideoTrimmerView.getVideoView().getDuration
                            () - mVideoTrimmerView.getTrimmerPos()[1] > 2000;

                    if (startPosChanged || isNeedTirm) {
                        mActivity.runOnUiThread(this::onStartTrim);
                        VideoUtils.INSTANCE.startTrim(
                                RegulatorControl.Companion.getInstance().getConfigVo().getVideoPath()
                                , com.zycx.shortvideo.utils.FileUtils.getPath(
                                        ParamsManager.VideoPath
                                        , System.currentTimeMillis() + ParamsManager.CompressVideo)
                                , mVideoTrimmerView.getTrimmerPos()[0], mVideoTrimmerView.getTrimmerPos()[1],
                                new OnTrimVideoListener() {
                                    @Override
                                    public void getResult(@NotNull Uri uri) {
                                        mActivity.runOnUiThread(() -> onFinishTrim(uri.getPath()));
                                    }

                                    @Override
                                    public void cancelAction() {

                                    }
                                });
                    } else {
                        // 不裁剪
                        mActivity.runOnUiThread(() -> onFinishTrim(RegulatorControl.Companion.getInstance().getConfigVo().getVideoPath()));
                    }

                });

        RxView.clicks(mToolbarLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mActivity.finish()
                );

        mVideoTrimmerView.setOnBottomVideoThumbCompletedListener(() -> {
            if (mToolbarRight != null) {
                mToolbarRight.setEnabled(true);
            }
        });
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_time;
    }

    @Override
    public void onStartTrim() {
        try {
            buildDialog(getResources().getString(R.string.trimming)).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinishTrim(String url) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(url);
        if (!skipCover) {
            VideoListManager.getInstance().addSubVideo(url,
                    (int) RegulatorControl.Companion.getInstance().getConfigVo().getDurationL());
        }
        TopicListBean topic = null;
        if (getArguments() != null) {
            topic = getArguments().getParcelable(SearchTopicFragment.TOPIC);
        }

        if (skipCover) {
            mVideoInfo.setPath(url);
            mVideoInfo.setCreateTime(System.currentTimeMillis() + "");

            byte[] bytes = RecyclerViewControl.Companion.getInstance().getMThumbAdapter().getMDatas().get(0)
                    .getBitmapByte();
            String cover = FileUtils.saveBitmapToFile(mActivity, ConvertUtils.bytes2Bitmap(bytes),
                    System.currentTimeMillis() + ParamsManager.VideoCover);
            mVideoInfo.setCover(cover);
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
            ActivityHandler.getInstance().removeActivity(TrimmerActivity.class);
            ActivityHandler.getInstance().removeActivity(SendDynamicActivity.class);

            SendDynamicActivity.startToSendDynamicActivity(getContext(),
                    sendDynamicDataBean, topic);
        } else {
            CoverActivity.startCoverActivity(mActivity, arrayList, false,
                    false, false, topic);
        }
    }

    @Override
    public void onCancel() {
        mActivity.finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mActivity == null) {
            return null;
        }
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mActivity, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }

    public void onNewIntent(Intent intent) {
        setArguments(intent.getExtras());
        setUpView();
    }

    @Override
    public void onDestroyView() {
        mVideoTrimmerView.release();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onDestroyView();
    }

}

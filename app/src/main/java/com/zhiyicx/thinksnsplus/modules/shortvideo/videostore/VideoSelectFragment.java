package com.zhiyicx.thinksnsplus.modules.shortvideo.videostore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;
import com.zycx.shortvideo.media.VideoInfo;
import com.zycx.shortvideo.utils.FileUtils;
import com.zycx.shortvideo.utils.TrimVideoUtil;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.recycleviewdecoration.TGridDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.adapter.VideoGridViewAdapter;
import com.zhiyicx.thinksnsplus.modules.shortvideo.clipe.TrimmerActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.cover.CoverActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.record.RecordActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;

/**
 * @Author Jliuer
 * @Date 2018/03/28/14:14
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoSelectFragment extends TSListFragment {
    private static final int DEFAUT_CLOUMS = 4;

    public static final int RECHOSE_VIDEO = 9999;

    /**
     * 来自动态发布页面，重新选择视频
     */
    public static final String IS_RELOAD = "is_reload";

    private ActionPopupWindow mPopWindow;

    // 分批次加载预留
    private List<VideoInfo> copyData;

    public static VideoSelectFragment newInstance(Bundle bundle) {
        VideoSelectFragment videoSelectFragment = new VideoSelectFragment();
        videoSelectFragment.setArguments(bundle);
        return videoSelectFragment;
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return false;
    }

    @Override
    protected boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected void onEmptyViewClick() {

    }

    @Override
    protected boolean setUseCenterLoading() {
        return true;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.video_select);
    }

    @Override
    protected void initData() {
        super.initData();
        mRvList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvList.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.spacing_small), -getResources().getDimensionPixelOffset(R.dimen
                .spacing_small), getResources().getDimensionPixelOffset(R.dimen.spacing_small));
        mRvList.setBackgroundColor(Color.WHITE);
        /*
         * 用户拍摄的占位数据
         */
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setPath(null);

        TrimVideoUtil.getAllVideoFiles(mActivity, (videoInfos, integer) -> mActivity.runOnUiThread(() -> {
            if (!tag) {
                mListDatas.add(videoInfo);
            }
            mListDatas.addAll(videoInfos);
            tag = true;
            refreshData();
            closeLoadingView();
        }));
    }

    boolean tag = false;

    @Override
    protected RecyclerView.Adapter getAdapter() {
        VideoGridViewAdapter adapter = new VideoGridViewAdapter(mActivity, R.layout.item_select_video, mListDatas);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                VideoInfo videoInfo = (VideoInfo) mListDatas.get(position);

                if (TextUtils.isEmpty(videoInfo.getPath())) {
                    // 自己去拍
                    TopicListBean topic = null;
                    if (getArguments() != null) {
                        topic = getArguments().getParcelable(SearchTopicFragment.TOPIC);
                    }
                    RecordActivity.startRecordActivity(mActivity,topic);
                    mActivity.finish();
                } else {
                    // 选择列表
                    ImageView coverView = (ImageView) view.findViewById(R.id.iv_cover);
                    if (coverView != null) {
                        initReSendDynamicPopupWindow(videoInfo, null);
                    }

                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return adapter;
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new TGridDecoration(getResources().getDimensionPixelOffset(R.dimen.spacing_small), getResources().getDimensionPixelOffset(R.dimen
                .spacing_small), true);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(mActivity, DEFAUT_CLOUMS);
    }

    /**
     * 初始化重发动态选择弹框
     */
    private void initReSendDynamicPopupWindow(VideoInfo videoInfo, Bitmap cover) {
//        boolean canDirectupload = videoInfo.getDuration() < 300000 && FileUtils.getFileMSize(videoInfo.getPath()) < 200;
        boolean canDirectupload = videoInfo.getDuration() < 300000;
        final TopicListBean topic =getArguments() != null? getArguments().getParcelable(SearchTopicFragment.TOPIC):null;
        mPopWindow = ActionPopupWindow.builder()
                .item1Str(!canDirectupload ? "" : getString(R.string.direct_upload))
                .item2Str(getString(R.string.edite_upload))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(() -> {
                    mPopWindow.hide();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    SendDynamicDataBean sendDynamicDataBean = new SendDynamicDataBean();
                    sendDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
                    videoInfo.setNeedCompressVideo(true);
                    videoInfo.setNeedGetCoverFromVideo(true);
                    List<ImageBean> pic = new ArrayList<>();
                    ImageBean imageBean = new ImageBean();
                    imageBean.setImgUrl(videoInfo.getPath());
                    pic.add(imageBean);
                    sendDynamicDataBean.setDynamicPrePhotos(pic);
                    sendDynamicDataBean.setDynamicType(SendDynamicDataBean.VIDEO_TEXT_DYNAMIC);
                    sendDynamicDataBean.setVideoInfo(videoInfo);

                    if (getArguments() != null) {
                        boolean isReload = getArguments().getBoolean(IS_RELOAD);
                        if (isReload) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(SendDynamicActivity.SEND_DYNAMIC_DATA, sendDynamicDataBean);
                            intent.putExtras(bundle);
                            mActivity.setResult(Activity.RESULT_OK, intent);
                            mActivity.finish();
                            return;
                        }
                    }
                    SendDynamicActivity.startToSendDynamicActivity(getContext(), sendDynamicDataBean,topic);
                    mActivity.finish();
                })
                .item2ClickListener(() -> {
                    mPopWindow.hide();
                    if (videoInfo.getDuration() <= 4000) {
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(videoInfo.getPath());
                        CoverActivity.startCoverActivity(mActivity, arrayList, false, false,
                                false,topic);
                    } else {
                        TrimmerActivity.startTrimmerActivity(mActivity, videoInfo.getPath(),topic);
                    }
                })
                .bottomClickListener(() -> mPopWindow.hide())
                .build();
        mPopWindow.show();
    }

    @Override
    public void onDestroyView() {
        dismissPop(mPopWindow);
        super.onDestroyView();
    }
}

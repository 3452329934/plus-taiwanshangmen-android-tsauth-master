package com.zycx.shortvideo.recordcore.multimedia;

import java.util.List;

/**
 * @author Jliuer
 * @Date 18/04/28 9:35
 * @Email Jliuer@aliyun.com
 * @Description 多段视频合并管理器
 */
public final class VideoCombineManager {

    private static final String TAG = "VideoCombineManage";

    private static VideoCombineManager mInstance;


    public static VideoCombineManager getInstance() {
        if (mInstance == null) {
            mInstance = new VideoCombineManager();
        }
        return mInstance;
    }

    /**
     * 初始化媒体合并器
     *
     * @param videoPath
     * @param destPath
     */
    public void startVideoCombiner(final List<String> videoPath, final String destPath,
                                   final VideoCombiner.VideoCombineListener listener) {
        VideoCombiner videoCombiner = new VideoCombiner(videoPath, destPath, listener);
        videoCombiner.combineVideo();
    }
}

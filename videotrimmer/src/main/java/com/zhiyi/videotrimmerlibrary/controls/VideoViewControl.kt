package com.zhiyi.videotrimmerlibrary.controls

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.text.TextUtils
import android.util.Log
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.VideoView
import com.zhiyi.videotrimmerlibrary.MediaHandleManager
import com.zhiyicx.common.utils.log.LogUtils
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit

/**
 * ThinkSNS Plus
 * Copyright (c) 18/12/24 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jliuer
 * @Date 18/12/24 16:37
 * @Email Jliuer@aliyun.com
 * @Description 视频控制
 */
class VideoViewControl private constructor(seekBar: SeekBar, videoView: VideoView) : MediaPlayer.OnCompletionListener {

    private var mVideoView: VideoView = videoView
    private var mSeekBar: SeekBar = seekBar
    private var mStartPosition = 0
    private var mEndPosition = 0
    private var mSubscriber: Subscription? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: VideoViewControl? = null

        fun getInstance(seekBar: SeekBar, videoView: VideoView): VideoViewControl {
            if (mInstance == null) {
                synchronized(VideoViewControl::class) {
                    if (mInstance == null) {
                        mInstance = VideoViewControl(seekBar, videoView)
                    }
                }
            }
            return mInstance!!
        }

        fun getInstance(): VideoViewControl {
            if (mInstance == null) {
                throw IllegalArgumentException("VideoViewControl getInstance ::: must call method getInstance(videoView: VideoView) first !!!")
            }
            return mInstance!!
        }
    }

    fun initial() {
        if (TextUtils.isEmpty(MediaHandleManager.getInstance().getConfigVo().videoPath)) throw IllegalArgumentException("VideoViewControl getInstance ::: videoPath cannot be null or empty ")
        mVideoView.setVideoPath(MediaHandleManager.getInstance().getConfigVo().videoPath)
        mVideoView.requestFocus()

        val seek = Observable.interval(100L, TimeUnit.MILLISECONDS)
                .filter { mSeekBar.max > 0 }
        mVideoView.setOnCompletionListener(this)
        mVideoView.setOnPreparedListener {
            mVideoView.postDelayed({
                mVideoView.start()
                mSubscriber = seek
                        .subscribe({
                            mSeekBar.progress = mVideoView.currentPosition - mStartPosition
                            // mVideoView.currentPosition 这个值有偏差
                            // 小米 5s 实测，5277ms 的视频，在5077ms 这里就已经回调了 onCompletion 方法
                            if (mSeekBar.progress + 500 >= mSeekBar.max) {
                                updatePos(mStartPosition.toLong())
                            }
                        }, { t -> t.printStackTrace() })
            }, 10)
        }
        mSeekBar.isEnabled = false

    }

    override fun onCompletion(mp: MediaPlayer?) {
        LogUtils.d("onCompletion:" + mVideoView.currentPosition.toString())

    }

    fun updatePos(pos: Long) {
        try {
            mVideoView.seekTo(pos.toInt())
        } catch (ignored: IllegalStateException) {
            ignored.printStackTrace()
        }
        if (!mVideoView.isPlaying) {
            mVideoView.start()
        }
    }

    fun updateVideoTime(left: Long, right: Long) {
        mStartPosition = left.toInt()
        mEndPosition = right.toInt()

        mSeekBar.progress = mStartPosition
        mSeekBar.max = mEndPosition - mStartPosition

        val params: RelativeLayout.LayoutParams = mSeekBar.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = TrimmerSeekBarControl.getInstance().getLeftPosX().toInt()
//        params.rightMargin = TrimmerSeekBarControl.getInstance().mTrimmerSeekBar.imeasureWidth - TrimmerSeekBarControl.getInstance().getRightPosX().toInt()
        var seekWidth = TrimmerSeekBarControl.getInstance().getRightPosX().toInt() - TrimmerSeekBarControl.getInstance().getLeftPosX().toInt()
        val real = if (seekWidth <= 0) {
            RelativeLayout.LayoutParams.MATCH_PARENT
        } else {
            seekWidth
        }
        params.width = real
        mSeekBar.layoutParams = params
    }

    fun release() {
        mInstance = null
        mSubscriber?.unsubscribe()
        mSubscriber = null
        mVideoView.seekTo(0)
        mVideoView.pause()
        mVideoView.suspend()
        mVideoView.setOnCompletionListener(null)
        mVideoView.setOnPreparedListener(null)
    }
}
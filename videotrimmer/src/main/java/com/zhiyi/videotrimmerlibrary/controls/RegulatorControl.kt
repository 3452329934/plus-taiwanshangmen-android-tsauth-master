package com.zhiyi.videotrimmerlibrary.controls

import android.annotation.SuppressLint
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.zhiyi.videotrimmerlibrary.MediaHandleManager
import com.zhiyi.videotrimmerlibrary.VideoTrimmerView
import com.zhiyi.videotrimmerlibrary.callback.EndScrollActionListener
import com.zhiyi.videotrimmerlibrary.callback.EndTouchActionListener
import com.zhiyi.videotrimmerlibrary.callback.IConfig
import com.zhiyi.videotrimmerlibrary.callback.UpdatePosListener
import com.zhiyi.videotrimmerlibrary.vo.ConfigVo
import java.text.SimpleDateFormat

class RegulatorControl private constructor(leftPos: TextView, rightPos: TextView) : EndScrollActionListener, EndTouchActionListener, UpdatePosListener {


    private val leftPosTv = leftPos

    private val rightPosTv = rightPos

    var timeStart = 0L
    var timeEnd = 0L


    private var mOnScelcetedContentChangedListener: OnScelcetedContentChangedListener? = null

    interface OnScelcetedContentChangedListener {
        fun onScelcetedContentChanged(left: Long?, right: Long?)
    }

    fun setOnScelcetedContentChangedListener(onScelcetedContentChangedListener: OnScelcetedContentChangedListener) {
        mOnScelcetedContentChangedListener = onScelcetedContentChangedListener
    }


    override fun updatePos() {
        if (MediaHandleManager.getInstance().getConfigVo().isShowPosTextViews)
            updatePosTextViewsMargin()
    }

    private fun updatePosTextViewsMargin() {
        updateLeftMargin()
        updateRightMargin()
        updatePosTextViewsContent()

    }

    private fun updateLeftMargin() {
        val layoutParams = leftPosTv.layoutParams as RelativeLayout.LayoutParams
        layoutParams.setMargins(TrimmerSeekBarControl.getInstance().getLeftPosX().toInt(), layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin)
        leftPosTv.layoutParams = layoutParams
    }

    private fun updateRightMargin() {
        val layoutParams = rightPosTv.layoutParams as RelativeLayout.LayoutParams
        val rightMaring = TrimmerSeekBarControl.getInstance().mTrimmerSeekBar.imeasureWidth - TrimmerSeekBarControl.getInstance().getRightPosX().toInt()
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, rightMaring, layoutParams.bottomMargin)
        rightPosTv.layoutParams = layoutParams

    }

    override fun updateRegionIndex() {
        updateVideoViewThumb()
    }

    override fun updateByScroll() {
        updateVideoViewThumb()
        updatePosTextViewsContent()
    }

    private fun updateVideoViewThumb() {
        VideoViewControl.getInstance().updatePos(getLeftThumbPos(TrimmerSeekBarControl.getInstance().leftIndex))
    }

    private fun updatePosTextViewsContent() {
        val left = getLeftThumbPos(TrimmerSeekBarControl.getInstance().leftIndex)
        val right = getLeftThumbPos(TrimmerSeekBarControl.getInstance().rightIndex)
        setPosTextViews(left, right)
        mOnScelcetedContentChangedListener?.onScelcetedContentChanged(left, right)
    }

    private fun getLeftThumbPos(regionIndex: Int): Long {
        val perThumbWidth = TrimmerSeekBarControl.getInstance().mTrimmerSeekBar.perThumbWidth

        var realIndex = RecyclerViewControl.getInstance().firstItemPosition + regionIndex
        val totalThumbSize = RecyclerViewControl.getInstance().mThumbAdapter.mDatas.size
        if (realIndex >= totalThumbSize)
            realIndex = totalThumbSize - 1
        if (realIndex < 0) {
            return 0
        }
        val thumbVo = RecyclerViewControl.getInstance().mThumbAdapter.mDatas[realIndex]


        val xoffset = TrimmerSeekBarControl.getInstance().mTrimmerSeekBar.leftPosX.toInt()
        val remainder = xoffset % perThumbWidth
        val positionL = thumbVo.radixPosition
        val offset = positionL * remainder / perThumbWidth
        return thumbVo.positionL / 1000 + offset
    }

    private fun getRightThumbPos(regionIndex: Int): Long {
        val perThumbWidth = TrimmerSeekBarControl.getInstance().mTrimmerSeekBar.perThumbWidth

        var realIndex = RecyclerViewControl.getInstance().firstItemPosition + regionIndex
        val totalThumbSize = RecyclerViewControl.getInstance().mThumbAdapter.mDatas.size
        if (realIndex >= totalThumbSize)
            realIndex = totalThumbSize - 1
        if (realIndex < 0) {
            return 0
        }
        val thumbVo = RecyclerViewControl.getInstance().mThumbAdapter.mDatas[realIndex]


        val xoffset = TrimmerSeekBarControl.getInstance().mTrimmerSeekBar.rightPosX.toInt()
        val remainder = xoffset % perThumbWidth
        val positionL = thumbVo.radixPosition
        var offset = positionL * remainder / perThumbWidth
        if (xoffset == TrimmerSeekBarControl.getInstance().mTrimmerSeekBar.imeasureWidth) {
            offset = positionL
        }
        val result = thumbVo.positionL / 1000 + offset
        return result
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: RegulatorControl? = null

        fun getInstance(videoTrimmerView: VideoTrimmerView): RegulatorControl {
            if (mInstance == null) {
                synchronized(VideoViewControl::class) {
                    if (mInstance == null) {
                        mInstance = RegulatorControl(videoTrimmerView.getLeftPosTextView(), videoTrimmerView.getRightPosTextView())
                        RecyclerViewControl.getInstance(videoTrimmerView.getRecyclerView(), mInstance!!)
                        TrimmerSeekBarControl.getInstance(videoTrimmerView.getTrimmerSeekBar(), mInstance!!, mInstance!!)
                        VideoViewControl.getInstance(videoTrimmerView.getSeekBar(), videoTrimmerView.getVideoView())
                    }
                }
            }
            return mInstance!!
        }

        fun getInstance(): RegulatorControl {
            if (mInstance == null) {
                throw IllegalArgumentException("RegulatorControl getInstance ::: must call method getInstance(videoView: VideoView, recyclerView: RecyclerView) first !!!")
            }
            return mInstance!!
        }
    }

    fun setVideoPath(videoPath: String): RegulatorControl {
        MediaHandleManager.getInstance().setVideoPath(videoPath)
        return this
    }

    fun getConfigVo(): ConfigVo = MediaHandleManager.getInstance().getConfigVo()

    fun initialThumbItemWH(wh: Array<Int>): RegulatorControl {
        MediaHandleManager.getInstance().setThumbItemWH(wh)
        return this
    }

    fun handle() {
        setPosTextViews(0, MediaHandleManager.getInstance().getConfigVo().getVisiableEndPos())
        VideoViewControl.getInstance().initial()
        MediaHandleManager.getInstance().getFrameThumb(RecyclerViewControl.getInstance())
    }

    @SuppressLint("SimpleDateFormat")
    private fun setPosTextViews(leftPos: Long, rightPos: Long) {
        val realLeftPos = Math.rint(leftPos.toDouble() / 1000).toLong() * 1000
        val realRightPos = Math.rint(rightPos.toDouble() / 1000).toLong() * 1000
        timeStart = realLeftPos
        timeEnd = realRightPos
        leftPosTv.text = SimpleDateFormat("m:ss").format(realLeftPos)
        rightPosTv.text = SimpleDateFormat("m:ss").format(realRightPos)
        VideoViewControl.getInstance().updateVideoTime(leftPos, rightPos)
    }

    fun release() {
        mInstance = null
        try {
            TrimmerSeekBarControl.getInstance().release()
            RecyclerViewControl.getInstance().release()
            VideoViewControl.getInstance().release()
            MediaHandleManager.getInstance().release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setIConfig(icg: IConfig) {
        MediaHandleManager.getInstance().setIConfig(icg)
        TrimmerSeekBarControl.getInstance().postInvalidateByConfig()
        updatePosTextViews(icg)
    }

    private fun updatePosTextViews(icg: IConfig) {
        if (!icg.isShowTrimmerTextViews()) {
            leftPosTv.visibility = View.GONE
            rightPosTv.visibility = View.GONE
            (leftPosTv.parent as View).visibility = View.GONE
        } else {
            leftPosTv.visibility = View.VISIBLE
            rightPosTv.visibility = View.VISIBLE
            (leftPosTv.parent as View).visibility = View.VISIBLE
        }
    }

    fun getTrimmerPos(): LongArray = longArrayOf(timeStart, timeEnd)
}
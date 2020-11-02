package com.zhiyi.videotrimmerlibrary.controls

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE
import com.zhiyi.videotrimmerlibrary.ThumbAdapter
import com.zhiyi.videotrimmerlibrary.callback.GetFrameListener
import com.zhiyi.videotrimmerlibrary.callback.EndScrollActionListener
import com.zhiyi.videotrimmerlibrary.vo.ThumbVo

class RecyclerViewControl private constructor(recyclerView: RecyclerView, updateTrimmerViewsListener: EndScrollActionListener) : GetFrameListener, RecyclerView.OnScrollListener() {

    private var mUpdateTrimmerViewsListener: EndScrollActionListener? = updateTrimmerViewsListener

    private val mRecyclerView = recyclerView

    val mThumbAdapter: ThumbAdapter = mRecyclerView.adapter as ThumbAdapter



    private var mOnBottomVideoThumbLoadCompletedListener: OnBottomVideoThumbLoadCompletedListener? = null

    interface OnBottomVideoThumbLoadCompletedListener {
        fun onBottomVideoThumbLoadCompleted()
    }

    fun setOnBottomVideoThumbLoadCompletedListener(l: OnBottomVideoThumbLoadCompletedListener) {
        mOnBottomVideoThumbLoadCompletedListener = l
    }

    var firstItemPosition: Int = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mInstance: RecyclerViewControl? = null
        fun getInstance(recyclerView: RecyclerView, updateTrimmerViewsListener: EndScrollActionListener): RecyclerViewControl {
            if (mInstance == null) {
                synchronized(RecyclerViewControl::class) {
                    if (mInstance == null) {
                        recyclerView.adapter = ThumbAdapter()
                        mInstance = RecyclerViewControl(recyclerView, updateTrimmerViewsListener)
                        recyclerView.addOnScrollListener(mInstance!!)
                    }
                }
            }
            return mInstance!!
        }

        fun getInstance(): RecyclerViewControl {
            if (mInstance == null) {
                throw IllegalArgumentException("VideoViewControl getInstance ::: must call method getInstance(videoView: VideoView, videoPath: String, maxWidth: Int, maxHeight: Int) first !!!")
            }
            return mInstance!!
        }
    }

    override fun update(thumbs: ArrayList<ThumbVo>) {
        mThumbAdapter.addDatas(thumbs)
    }

    override fun updateCompleted() {
        mOnBottomVideoThumbLoadCompletedListener?.onBottomVideoThumbLoadCompleted()
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == SCROLL_STATE_IDLE) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            firstItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            mUpdateTrimmerViewsListener?.updateByScroll()
        }

    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        firstItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
//        mUpdateTrimmerViewsListener?.updateByScroll()
    }

    fun release() {
        mInstance = null
        mUpdateTrimmerViewsListener = null
    }
}
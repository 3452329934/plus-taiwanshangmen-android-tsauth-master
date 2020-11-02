package com.zhiyi.videotrimmerlibrary.callback

import com.zhiyi.videotrimmerlibrary.vo.ThumbVo

/**
* Created by cjh on 2017/8/31.
*/
interface GetFrameListener {

    fun update(thumbs: ArrayList<ThumbVo>)

    fun updateCompleted()

}
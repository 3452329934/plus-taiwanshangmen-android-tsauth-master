package com.zhiyi.videotrimmerlibrary.callback

import android.net.Uri

interface TrimVideoListener {

    fun onStartTrim(start: String, start1: Long, trimmers: String, trimmerl: Long)

    fun onProgress(progress: Int)

    fun onFinishTrim(uri: Uri)

    fun onCancel()

}
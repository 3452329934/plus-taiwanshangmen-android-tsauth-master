package com.zhiyi.videotrimmerlibrary.callback

import android.net.Uri

interface OnTrimVideoListener {

    fun getResult(uri: Uri)

    fun cancelAction()
}

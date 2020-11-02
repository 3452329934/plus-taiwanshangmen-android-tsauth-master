package com.zhiyi.videotrimmerlibrary.callback


interface IConfig {

    fun getTrimmerTime(): Long

    fun getVisiableThumbCount(): Int

    fun getThumbListUpdateCount(): Int

    fun getTrimmerSeekBarShaowColor(): String

    fun getTrimmerSeekBarTrimmerStrokeColor(): String

    fun getTrimmerSeekBarTrimmerStrokeWidth(): Int

    fun getTrimmerOffsetValue(): Int

    fun getMinTrimmerThumbCount(): Int
    fun getMinTrimmerThumbTime(): Int

    fun isShowTrimmerTextViews(): Boolean

    fun getLeftCursorBitmap(): Int
    fun getRightCursorBitmap(): Int
}
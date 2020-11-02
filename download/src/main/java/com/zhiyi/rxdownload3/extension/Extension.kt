package com.zhiyi.rxdownload3.extension

import io.reactivex.Maybe
import com.zhiyi.rxdownload3.core.RealMission


interface Extension {
    fun init(mission: RealMission)

    fun action(): Maybe<Any>
}
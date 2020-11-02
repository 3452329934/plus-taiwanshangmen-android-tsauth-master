package com.zhiyi.rxdownload3.notification

import android.app.Notification
import android.content.Context
import com.zhiyi.rxdownload3.core.RealMission
import com.zhiyi.rxdownload3.core.Status


interface NotificationFactory {
    fun build(context: Context, mission: RealMission, status: Status): Notification?
}


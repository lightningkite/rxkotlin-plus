package com.lightningkite.rx.viewgenerators.fcm

interface ForegroundNotificationHandler {
    fun handleNotificationInForeground(map: Map<String, String>): ForegroundNotificationHandlerResult {
        return ForegroundNotificationHandlerResult.ShowNotification
    }
}

enum class ForegroundNotificationHandlerResult {
    SuppressNotification, ShowNotification, Unhandled
}
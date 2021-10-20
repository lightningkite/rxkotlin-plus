package com.lightningkite.rx.xml


data class AndroidIdHook(
    val name: String,
    val type: String,
    val resourceId: String,
    val optional: Boolean = false
)
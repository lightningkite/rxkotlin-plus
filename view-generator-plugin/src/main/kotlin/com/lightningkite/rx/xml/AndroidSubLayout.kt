package com.lightningkite.rx.xml


data class AndroidSubLayout(
    val name: String,
    val resourceId: String,
    val layoutXmlClass: String,
    val optional: Boolean = false
)

package com.lightningkite.rx

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

private val pJSON = "application/json".toMediaType()
val MediaType.Companion.JSON: MediaType get() = pJSON
private val pTEXT = "text/plain".toMediaType()
val MediaType.Companion.TEXT: MediaType get() = pTEXT
private val pJPEG = "image/jpeg".toMediaType()
val MediaType.Companion.JPEG: MediaType get() = pJPEG


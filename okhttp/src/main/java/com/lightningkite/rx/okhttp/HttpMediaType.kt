package com.lightningkite.rx.okhttp

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

private val pJSON = "application/json".toMediaType()

/**
 * Pre-formed value for "application/json".
 */
val MediaType.Companion.JSON: MediaType get() = pJSON
private val pTEXT = "text/plain".toMediaType()

/**
 * Pre-formed value for "text/plain".
 */
val MediaType.Companion.TEXT: MediaType get() = pTEXT
private val pJPEG = "image/jpeg".toMediaType()

/**
 * Pre-formed value for "image/jpeg".
 */
val MediaType.Companion.JPEG: MediaType get() = pJPEG


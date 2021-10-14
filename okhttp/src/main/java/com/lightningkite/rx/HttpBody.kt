package com.lightningkite.rx

import com.lightningkite.rx.android.resources.*
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.Okio
import java.io.ByteArrayOutputStream
import java.io.File



fun Any?.toJsonRequestBody(): RequestBody {
    val sending = defaultJsonMapper.writeValueAsString(this)
    return sending.toRequestBody(MediaType.JSON)
}

fun MultipartBody.from(vararg parts: MultipartBody.Part): RequestBody = from(parts.toList())

fun MultipartBody.from(parts: List<MultipartBody.Part>): RequestBody {
    return MultipartBody.Builder().setType(MultipartBody.FORM).also {
        for (part in parts) {
            it.addPart(part)
        }
    }.build()
}

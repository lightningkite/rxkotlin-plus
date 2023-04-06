package com.lightningkite.rx.okhttp

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Transforms a given object into a JSON request body using the [defaultJsonMapper].
 */
inline fun <reified T> T.toJsonRequestBody(): RequestBody {
    val sending = defaultJsonMapper.encodeToString(this)
    return sending.toRequestBody(MediaType.JSON)
}

/**
 * Transforms a given object into a JSON request body using the [defaultJsonMapper].
 */
fun <T> T.toJsonRequestBody(serializer: KSerializer<T>): RequestBody {
    val sending = defaultJsonMapper.encodeToString(serializer, this)
    return sending.toRequestBody(MediaType.JSON)
}

/**
 * A shortcut for using the [MultipartBody.Builder].
 */
fun MultipartBody.Companion.from(vararg parts: MultipartBody.Part): RequestBody = from(parts.toList())

/**
 * A shortcut for using the [MultipartBody.Builder].
 */
fun MultipartBody.Companion.from(parts: List<MultipartBody.Part>): RequestBody {
    return MultipartBody.Builder().setType(MultipartBody.FORM).also {
        for (part in parts) {
            it.addPart(part)
        }
    }.build()
}

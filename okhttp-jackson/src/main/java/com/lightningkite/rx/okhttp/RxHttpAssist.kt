package com.lightningkite.rx.okhttp

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.flatMap
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.singleOfError
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import okhttp3.Response

/**
 * Changes unsuccessful responses into [HttpResponseException].
 */
fun Single<Response>.unsuccessfulAsError(): Single<Response> {
    return this.map { it ->
        if (it.isSuccessful) {
            return@map it
        } else {
            throw HttpResponseException(it)
        }
    }
}

/**
 * Properly discards the response.
 */
fun Single<Response>.discard(): Single<Unit> {
    return this.flatMap {
        if (it.isSuccessful) {
            it.discard()
        } else {
            singleOfError(HttpResponseException(it))
        }
    }
}

/**
 * Reads the response into JSON using the [defaultJsonMapper].
 */
inline fun <reified T: Any> Single<Response>.readJson(): Single<T> {
    val type = jacksonTypeRef<T>()
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readJson<T>(type)
        } else {
            singleOfError(HttpResponseException(it))
        }
    }
}

/**
 * Reads the response into JSON using the [defaultJsonMapper], but prints the raw value to [System.out].
 */
inline fun <reified T: Any> Single<Response>.readJsonDebug(): Single<T> {
    val type = jacksonTypeRef<T>()
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readJsonDebug<T>(type)
        } else {
            singleOfError(HttpResponseException(it))
        }
    }
}

/**
 * Reads the text from the [Response].
 */
fun Single<Response>.readText(): Single<String> {
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readText()
        } else {
            singleOfError(HttpResponseException(it))
        }
    }
}

/**
 * Reads the binary data from the [Response].
 */
fun Single<Response>.readByteArray(): Single<ByteArray> {
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readByteArray()
        } else {
            singleOfError(HttpResponseException(it))
        }
    }
}
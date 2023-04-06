package com.lightningkite.rx.okhttp

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.flatMap
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.singleOfError
import kotlinx.serialization.KSerializer
import okhttp3.Response
import kotlin.reflect.typeOf

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
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T: Any> Single<Response>.readJson(): Single<T> {
    val type = typeOf<T>()
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
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T: Any> Single<Response>.readJsonDebug(): Single<T> {
    val type = typeOf<T>()
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readJsonDebug<T>(type)
        } else {
            singleOfError(HttpResponseException(it))
        }
    }
}

/**
 * Reads the response into JSON using the [defaultJsonMapper].
 */
fun <T: Any> Single<Response>.readJson(serializer: KSerializer<T>): Single<T> {
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readJson<T>(serializer)
        } else {
            singleOfError(HttpResponseException(it))
        }
    }
}

/**
 * Reads the response into JSON using the [defaultJsonMapper], but prints the raw value to [System.out].
 */
fun <T: Any> Single<Response>.readJsonDebug(serializer: KSerializer<T>): Single<T> {
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readJsonDebug<T>(serializer)
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
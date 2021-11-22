package com.lightningkite.rx.okhttp

import io.reactivex.rxjava3.core.Single
import okhttp3.Response
import java.lang.reflect.ParameterizedType
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
            Single.error<Unit>(HttpResponseException(it)) as Single<Unit>
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
            Single.error<T>(HttpResponseException(it)) as Single<T>
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
            Single.error<T>(HttpResponseException(it)) as Single<T>
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
            Single.error<String>(HttpResponseException(it))
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
            Single.error<ByteArray>(HttpResponseException(it))
        }
    }
}
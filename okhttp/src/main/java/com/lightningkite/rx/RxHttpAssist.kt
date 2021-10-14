package com.lightningkite.rx

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.readValue
import io.reactivex.rxjava3.core.Single
import okhttp3.Response
import java.lang.reflect.ParameterizedType

fun Single<Response>.unsuccessfulAsError(): Single<Response> {
    return this.map { it ->
        if (it.isSuccessful) {
            return@map it
        } else {
            throw HttpResponseException(it)
        }
    }
}


fun Single<Response>.discard(): Single<Unit> {
    return this.flatMap {
        if (it.isSuccessful) {
            it.discard()
        } else {
            Single.error<Unit>(HttpResponseException(it)) as Single<Unit>
        }
    }
}

inline fun <reified T: Any> Single<Response>.readJson(): Single<T> {
    val type = jacksonTypeRef<T>()
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readJson<T>(type)
        } else {
            Single.error<T>(HttpResponseException(it)) as Single<T>
        }
    }
}

inline fun <reified T: Any> Single<Response>.readJsonDebug(): Single<T> {
    val type = jacksonTypeRef<T>()
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readJsonDebug<T>(type)
        } else {
            Single.error<T>(HttpResponseException(it)) as Single<T>
        }
    }
}

inline fun <reified T: Any> Single<String>.fromJsonString(): Single<T> {
    val type = jacksonTypeRef<T>()
    return this.map { it -> it.fromJsonString<T>(type) }
}

fun Single<Response>.readText(): Single<String> {
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readText()
        } else {
            Single.error<String>(HttpResponseException(it))
        }
    }
}

fun Single<Response>.readByteArray(): Single<ByteArray> {
    return this.flatMap { it ->
        if (it.isSuccessful) {
            it.readByteArray()
        } else {
            Single.error<ByteArray>(HttpResponseException(it))
        }
    }
}
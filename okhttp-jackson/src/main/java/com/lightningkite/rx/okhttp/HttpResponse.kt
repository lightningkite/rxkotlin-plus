package com.lightningkite.rx.okhttp

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.readValue
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Closes the body of the response as a [Single].
 */
fun Response.discard(): Single<Unit> = Single.create<Unit> { em ->
    body!!.close()
    em.onSuccess(Unit)
}.let { HttpClient.threadCorrectly(it) }

/**
 * Reads the body of the response as a [Single].
 */
fun Response.readText(): Single<String> =
    Single.create<String> { em -> em.onSuccess(body!!.use { it.string() }) }.let { HttpClient.threadCorrectly(it) }

/**
 * Reads the body of the response as a [Single].
 */
fun Response.readByteArray(): Single<ByteArray> =
    Single.create<ByteArray> { em -> em.onSuccess(body!!.use { it.bytes() }) }.let { HttpClient.threadCorrectly(it) }

/**
 * Reads the body of the response as a [Single], using [defaultJsonMapper] to parse the JSON.
 */
inline fun <reified T : Any> Response.readJson(): Single<T> = readJson(jacksonTypeRef())

/**
 * Reads the body of the response as a [Single], using [defaultJsonMapper] to parse the JSON.
 */
fun <T : Any> Response.readJson(typeToken: TypeReference<T>): Single<T> = Single.create<T> { em: SingleEmitter<T> ->
    try {
        val result: T = body!!.use {
            defaultJsonMapper.readValue<T>(it.byteStream(), typeToken)
        }
        em.onSuccess(result)
    } catch (e: Throwable) {
        em.tryOnError(e)
    }
}.let { HttpClient.threadCorrectly<T>(it) }


/**
 * Reads the body of the response as a [Single], using [defaultJsonMapper] to parse the JSON.
 * Dumps the raw response to [System.out].
 */
inline fun <reified T : Any> Response.readJsonDebug(): Single<T> = readJsonDebug(jacksonTypeRef())

/**
 * Reads the body of the response as a [Single], using [defaultJsonMapper] to parse the JSON.
 * Dumps the raw response to [System.out].
 */
fun <T : Any> Response.readJsonDebug(typeToken: TypeReference<T>): Single<T> = readText().map { println("HttpResponse got $it"); it.fromJsonString<T>(typeToken) }

package com.lightningkite.rx.okhttp

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> Response.readJson(): Single<T> = readJson(typeOf<T>())

/**
 * Reads the body of the response as a [Single], using [defaultJsonMapper] to parse the JSON.
 */
@OptIn(ExperimentalSerializationApi::class)
fun <T : Any> Response.readJson(typeToken: KType): Single<T> = Single.create<T> { em: SingleEmitter<T> ->
    try {
        @Suppress("UNCHECKED_CAST") val result: T = body!!.use {
            defaultJsonMapper.decodeFromStream(defaultJsonMapper.serializersModule.serializer(typeToken), it.byteStream()) as T
        }
        em.onSuccess(result)
    } catch (e: Throwable) {
        em.tryOnError(e)
    }
}.let { HttpClient.threadCorrectly<T>(it) }

/**
 * Reads the body of the response as a [Single], using [defaultJsonMapper] to parse the JSON.
 */
@OptIn(ExperimentalSerializationApi::class)
fun <T : Any> Response.readJson(serializer: KSerializer<T>): Single<T> = Single.create<T> { em: SingleEmitter<T> ->
    try {
        @Suppress("UNCHECKED_CAST") val result: T = body!!.use {
            defaultJsonMapper.decodeFromStream(serializer, it.byteStream()) as T
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
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> Response.readJsonDebug(): Single<T> = readJsonDebug(typeOf<T>())

/**
 * Reads the body of the response as a [Single], using [defaultJsonMapper] to parse the JSON.
 * Dumps the raw response to [System.out].
 */
fun <T : Any> Response.readJsonDebug(typeToken: KType): Single<T> = readText().map { println("HttpResponse got $it"); it.fromJsonString<T>(typeToken) }
fun <T : Any> Response.readJsonDebug(serializer: KSerializer<T>): Single<T> = readText().map { println("HttpResponse got $it"); it.fromJsonString<T>(serializer) }

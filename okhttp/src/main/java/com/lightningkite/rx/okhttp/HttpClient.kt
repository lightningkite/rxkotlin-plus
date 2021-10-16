package com.lightningkite.rx.okhttp

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.reactivex.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okio.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

object HttpClient {

    var ioScheduler: Scheduler? = null
    var responseScheduler: Scheduler? = null
    fun <T: Any> threadCorrectly(single: Single<T>): Single<T> {
        var current = single
        ioScheduler?.let { current = current.subscribeOn(it) }
        responseScheduler?.let { current = current.observeOn(it) }
        return current
    }

    fun <T: Any> threadCorrectly(observable: Observable<T>): Observable<T> {
        var current = observable
        ioScheduler?.let { current = current.subscribeOn(it) }
        responseScheduler?.let { current = current.observeOn(it) }
        return current
    }

    const val GET = "GET"
    const val POST = "POST"
    const val PUT = "PUT"
    const val PATCH = "PATCH"
    const val DELETE = "DELETE"

    var client = OkHttpClient.Builder().build()

    val defaultOptions = HttpOptions()
    fun getCacheControl(cacheMode: HttpCacheMode): CacheControl = when (cacheMode) {
        HttpCacheMode.Default -> CacheControl.Builder().build()
        HttpCacheMode.NoStore -> CacheControl.Builder().noCache().noStore().build()
        HttpCacheMode.Reload -> CacheControl.Builder().noCache().build()
        HttpCacheMode.NoCache -> CacheControl.Builder().maxAge(1000, TimeUnit.DAYS).build()
        HttpCacheMode.ForceCache -> CacheControl.Builder().maxAge(1000, TimeUnit.DAYS).build()
        HttpCacheMode.OnlyIfCached -> CacheControl.Builder().onlyIfCached().build()
    }

    private fun callInternal(
        url: String,
        method: String = HttpClient.GET,
        headers: Map<String, String> = mapOf(),
        body: RequestBody? = null,
        options: HttpOptions = HttpClient.defaultOptions,
        client: OkHttpClient
    ): Call {
        val request = Request.Builder()
            .url(url)
            .method(method, body)
            .headers(headers.toHeaders())
            .addHeader("Accept-Language", Locale.getDefault().language)
            .cacheControl(getCacheControl(options.cacheMode))
            .build()
        return client.newCall(request)
    }

    private fun OkHttpClient.Builder.apply(options: HttpOptions): OkHttpClient.Builder {
        options.callTimeout?.let {
            callTimeout(it, TimeUnit.MILLISECONDS)
        } ?: run {
            callTimeout(5, TimeUnit.MINUTES)
        }
        options.writeTimeout?.let {
            writeTimeout(it, TimeUnit.MILLISECONDS)
        } ?: run {
            writeTimeout(5, TimeUnit.MINUTES)
        }
        options.readTimeout?.let {
            readTimeout(it, TimeUnit.MILLISECONDS)
        } ?: run {
            readTimeout(5, TimeUnit.MINUTES)
        }
        options.connectTimeout?.let {
            connectTimeout(it, TimeUnit.MILLISECONDS)
        } ?: run {
            connectTimeout(5, TimeUnit.MINUTES)
        }
        return this
    }

    fun call(
        url: String,
        method: String = HttpClient.GET,
        headers: Map<String, String> = mapOf(),
        body: RequestBody? = null,
        options: HttpOptions = HttpClient.defaultOptions
    ): Single<Response> {
        return Single.create<Response> { emitter ->
            try {
                val response = callInternal(
                    url = url,
                    method = method,
                    headers = headers,
                    body = body,
                    options = options,
                    client = client.newBuilder().apply(options).build()
                ).execute()
                emitter.onSuccess(response)
            } catch (e: Exception) {
                emitter.tryOnError(e)
            }
        }.cache().let { threadCorrectly(it) }
    }

    fun <T> callWithProgress(
        url: String,
        method: String = HttpClient.GET,
        headers: Map<String, String> = mapOf(),
        body: RequestBody? = null,
        options: HttpOptions = HttpClient.defaultOptions,
        parse: (Response) -> Single<T>
    ): Observable<HttpProgress<T>> {
        var finished = false
        return Observable.create<HttpProgress<T>> { em ->
            val call = callInternal(
                url = url,
                method = method,
                headers = headers,
                body = body?.let {
                    ProgressRequestBody<T>(it) {
                        if (!finished) {
                            em.onNext(it)
                        }
                    }
                },
                options = options,
                client = client.newBuilder().apply(options).addNetworkInterceptor {
                    val original = it.proceed(it.request())
                    original.newBuilder()
                        .body(original.body?.let {
                            ProgressResponseBody<T>(it) {
                                if (!finished) {
                                    em.onNext(it)
                                }
                            }
                        })
                        .build()
                }.build()
            )
            try {
                val response = call.execute()
                parse(response).subscribe(
                    { parsed ->
                        finished = true
                        em.onNext(HttpProgress(HttpPhase.Done, response = parsed))
                        em.onComplete()
                    },
                    { e ->
                        em.tryOnError(e)
                    }
                )
            } catch (e: Exception) {
                em.tryOnError(e)
            }
        }.share().let { threadCorrectly(it) }
    }

    private class ProgressRequestBody<T>(val base: RequestBody, val onProgress: (HttpProgress<T>) -> Unit) :
        RequestBody() {
        override fun contentType(): MediaType? = base.contentType()
        override fun writeTo(sink: BufferedSink) {
            val bufferedSink: BufferedSink
            val knownSize = base.contentLength()
            val countingSink = if (knownSize == -1L) CountingSink(sink) {
                onProgress(
                    HttpProgress(
                        HttpPhase.Write,
                        estimateProgress(it)
                    )
                )
            } else CountingSink(sink) {
                onProgress(
                    HttpProgress(
                        HttpPhase.Write,
                        (it.toDouble() / knownSize.toDouble()).toFloat()
                    )
                )
            }
            bufferedSink = countingSink.buffer()
            base.writeTo(bufferedSink)
            bufferedSink.flush()
            onProgress(HttpProgress(phase = HttpPhase.Waiting))
        }

        override fun contentLength(): Long = base.contentLength()
    }

    private fun estimateProgress(bytes: Long): Float = 1f - (1.0 / (bytes.toDouble() / 100_000.0 + 1.0)).toFloat()

    private class CountingSink(delegate: Sink, val action: (Long) -> Unit) : ForwardingSink(delegate) {
        var totalWritten = 0L
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            totalWritten += byteCount
            action(totalWritten)
        }
    }

    private class ProgressResponseBody<T>(val body: ResponseBody, val onProgress: (HttpProgress<T>) -> Unit) :
        ResponseBody() {
        override fun contentType(): MediaType? = body.contentType()
        override fun contentLength(): Long = body.contentLength()
        var src: BufferedSource? = null
        override fun source(): BufferedSource {
            src?.let { return it }
            val knownLength = contentLength()
            val src = if (knownLength == -1L) {
                object : ForwardingSource(body.source()) {
                    var totalBytesRead = 0L

                    @Throws(IOException::class)
                    override fun read(sink: Buffer, byteCount: Long): Long {
                        val bytesRead = super.read(sink, byteCount)
                        // read() returns the number of bytes read, or -1 if this source is exhausted.
                        totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                        onProgress(HttpProgress(HttpPhase.Read, estimateProgress(totalBytesRead)))
                        return bytesRead
                    }

                    override fun close() {
                        super.close()
                    }
                }
            } else {
                object : ForwardingSource(body.source()) {
                    var totalBytesRead = 0L

                    @Throws(IOException::class)
                    override fun read(sink: Buffer, byteCount: Long): Long {
                        val bytesRead = super.read(sink, byteCount)
                        // read() returns the number of bytes read, or -1 if this source is exhausted.
                        totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                        onProgress(
                            HttpProgress(
                                HttpPhase.Read,
                                (totalBytesRead.toDouble() / knownLength.toDouble()).toFloat()
                            )
                        )
                        return bytesRead
                    }

                    override fun close() {
                        super.close()
                    }
                }
            }
            val b = src.buffer()
            this.src = b
            return b
        }

    }

    @Deprecated("Just use plain call with options")
    fun call(
        url: String,
        method: String = HttpClient.GET,
        headers: Map<String, String> = mapOf(),
        body: RequestBody? = null,
        callTimeout: Long? = null,
        writeTimeout: Long? = null,
        readTimeout: Long? = null,
        connectTimeout: Long? = null
    ): Single<Response> {
        return call(
            url = url,
            method = method,
            headers = headers,
            body = body,
            options = HttpOptions(
                callTimeout = callTimeout,
                writeTimeout = writeTimeout,
                readTimeout = readTimeout,
                connectTimeout = connectTimeout
            )
        )
    }

    fun webSocket(
        url: String
    ): Observable<ConnectedWebSocket> {
        return Observable.using<ConnectedWebSocket, ConnectedWebSocket>(
            {
                val out = ConnectedWebSocket(url)
                out.underlyingSocket = client.newWebSocket(
                    Request.Builder()
                        .url(url.replace("http", "ws"))
                        .addHeader("Accept-Language", Locale.getDefault().language)
                        .build(),
                    out
                )
                out
            },
            { it.ownConnection },
            { it.onComplete() }
        ).let { threadCorrectly(it) }
    }

}


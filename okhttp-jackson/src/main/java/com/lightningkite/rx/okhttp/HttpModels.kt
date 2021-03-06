//! This file will translate using Khrysalis.
package com.lightningkite.rx.okhttp

import okhttp3.CacheControl
import java.util.concurrent.TimeUnit

/**
 * The current phase of the request.
 */
enum class HttpPhase {
    Connect,
    Write,
    Waiting,
    Read,
    Done
}

/**
 * The current progress of a request.
 */
class HttpProgress<T>(
    val phase: HttpPhase,
    val ratio: Float = 0.5f,
    val response: T? = null
) {
    val approximate: Float get() = when(phase){
        HttpPhase.Connect -> 0f
        HttpPhase.Write -> 0.15f + 0.5f * ratio
        HttpPhase.Waiting -> 0.65f
        HttpPhase.Read -> 0.7f + 0.3f * ratio
        HttpPhase.Done -> 1f
    }
}

/**
 * Timeout and cache options for a request.
 */
data class HttpOptions(
    val callTimeout: Long? = null,
    val writeTimeout: Long? = 10_000L,
    val readTimeout: Long? = 10_000L,
    val connectTimeout: Long? = 10_000L,
    val cacheMode: HttpCacheMode = HttpCacheMode.Default
)

enum class HttpCacheMode {
    /** Use fresh cache matches, use conditional request for stale cache matches, and a full request if not present in cache **/
    Default,
    /** Force full call and don't store the result **/
    NoStore,
    /** Force full call, but store the result **/
    Reload,
    /** Verify with server that the cached copy is correct **/
    NoCache,
    /** Use literally any cached data even if it's out of date, if missing make call **/
    ForceCache,
    /** Use literally any cached data even if it's out of date, error if not present **/
    OnlyIfCached
}
package com.lightningkite.rx.okhttp

import okhttp3.Response

/**
 * An exception representing a [Response] from the server that indicates the request was unsuccessful.
 */
open class HttpResponseException(val response: Response, cause: Throwable? = null): Exception("Got code ${response.code}", cause)

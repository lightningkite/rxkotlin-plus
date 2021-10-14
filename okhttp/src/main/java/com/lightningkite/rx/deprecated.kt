package com.lightningkite.rx

import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response


@Deprecated("Use it directly instead.", ReplaceWith("RequestBody", "okhttp3.RequestBody"))
typealias HttpBody = RequestBody
@Deprecated("Use it directly instead.", ReplaceWith("MultipartBody.Part", "okhttp3.MultipartBody"))
typealias HttpBodyPart = MultipartBody.Part
@Deprecated("Use it directly instead.", ReplaceWith("MediaType", "okhttp3.MediaType"))
typealias HttpMediaType = MediaType
@Deprecated("Use it directly instead.", ReplaceWith("Response", "okhttp3.Response"))
typealias HttpResponse = Response

@Deprecated("Use new naming convention", ReplaceWith("this.toJsonRequestBody()", "com.lightningkite.rx.toJsonRequestBody"), DeprecationLevel.ERROR)
fun Any?.toJsonHttpBody(): RequestBody = throw NotImplementedError()
@Deprecated("Use new naming convention", ReplaceWith("this.toRequestBody(mediaType)", "okhttp3.RequestBody.Companion.toRequestBody"), DeprecationLevel.ERROR)
fun ByteArray.toHttpBody(mediaType: MediaType): RequestBody = throw NotImplementedError()
@Deprecated("Use new naming convention", ReplaceWith("this.toRequestBody(mediaType)", "okhttp3.RequestBody.Companion.toRequestBody"), DeprecationLevel.ERROR)
fun String.toHttpBody(mediaType: MediaType = MediaType.TEXT): RequestBody = throw NotImplementedError()

@Deprecated("Use new naming convention", ReplaceWith("MultipartBody.from(*parts)", "okhttp3.MultipartBody", "com.lightningkite.rx.from"), DeprecationLevel.ERROR)
fun multipartFormBody(vararg parts: MultipartBody.Part): RequestBody {
    return MultipartBody.Builder().setType(MultipartBody.FORM).also {
        for (part in parts) {
            it.addPart(part)
        }
    }.build()
}

@Deprecated("Use new naming convention", ReplaceWith("MultipartBody.from(parts)", "okhttp3.MultipartBody", "com.lightningkite.rx.from"), DeprecationLevel.ERROR)
fun multipartFormBody(parts: List<MultipartBody.Part>): RequestBody {
    return MultipartBody.Builder().setType(MultipartBody.FORM).also {
        for (part in parts) {
            it.addPart(part)
        }
    }.build()
}

@Deprecated("Use new naming convention", ReplaceWith("MultipartBody.Part.createFormData(name, value)", "okhttp3.MultipartBody"), DeprecationLevel.ERROR)
fun multipartFormValuePart(name: String, value: String): MultipartBody.Part = throw NotImplementedError()
@Deprecated("Use new naming convention", ReplaceWith("MultipartBody.Part.createFormData(name, value)", "okhttp3.MultipartBody"), DeprecationLevel.ERROR)
fun multipartFormFilePart(name: String, value: String): MultipartBody.Part = throw NotImplementedError()
@Deprecated("Use new naming convention", ReplaceWith("MultipartBody.Part.createFormData(name, filename, body)", "okhttp3.MultipartBody"), DeprecationLevel.ERROR)
fun multipartFormFilePart(name: String, filename: String? = null, body: RequestBody): MultipartBody.Part = throw NotImplementedError()

@Deprecated("Now on MediaType directly", ReplaceWith("MediaType", "okhttp3.MediaType"))
object HttpMediaTypes


@Deprecated("Use new naming convention", ReplaceWith("this.readByteArray()", "com.lightningkite.rx.readByteArray"), DeprecationLevel.ERROR)
fun Response.readData(): Single<ByteArray> = throw NotImplementedError()

@Deprecated("Use new naming convention", ReplaceWith("this.readByteArray()", "com.lightningkite.rx.readByteArray"), DeprecationLevel.ERROR)
fun Single<Response>.readData(): Single<ByteArray> = throw NotImplementedError()
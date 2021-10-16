package com.lightningkite.rx.android

import android.widget.ImageView
import com.google.android.exoplayer2.ui.PlayerView
import com.lightningkite.rx.android.resources.*
import io.reactivex.rxjava3.core.Observable
import java.util.*

private fun no(): Nothing = throw NotImplementedError()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("image.subscribeAutoDispose(this) { setImage(it) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.resources.setImage"),
    level = DeprecationLevel.ERROR
)
fun ImageView.bindImage(image: Observable<Image>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("image.subscribeAutoDispose(this) { setImage(it.kotlin) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.kotlin", "com.lightningkite.rx.android.resources.setImage"),
    level = DeprecationLevel.ERROR
)
@JvmName("bindImageOptional")
fun ImageView.bindImage(image: Observable<Optional<Image>>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("video.subscribeAutoDispose(this) { setFromVideoThumbnail(it) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.kotlin", "com.lightningkite.rx.android.resources.setFromVideoThumbnail"),
    level = DeprecationLevel.ERROR
)
fun ImageView.bindVideoThumbnail(video: Observable<Video>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("video.subscribeAutoDispose(this) { setFromVideoThumbnail(it.kotlin) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.kotlin", "com.lightningkite.rx.android.resources.setFromVideoThumbnail"),
    level = DeprecationLevel.ERROR
)
@JvmName("bindVideoThumbnailOptional")
fun ImageView.bindVideoThumbnail(video: Observable<Optional<Video>>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("video.subscribeAutoDispose(this) { setVideo(it) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.kotlin", "com.lightningkite.rx.android.resources.setVideo"),
    level = DeprecationLevel.ERROR
)
fun PlayerView.bind(video: Observable<Video>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("video.subscribeAutoDispose(this) { setVideo(it.kotlin) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.kotlin", "com.lightningkite.rx.android.resources.setVideo"),
    level = DeprecationLevel.ERROR
)
@JvmName("bindOptional")
fun PlayerView.bind(video: Observable<Optional<Video>>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("video.subscribeAutoDispose(this) { setVideo(it, true) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.kotlin", "com.lightningkite.rx.android.resources.setVideo"),
    level = DeprecationLevel.ERROR
)
fun PlayerView.bindAndStart(video: Observable<Video>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("video.subscribeAutoDispose(this) { setVideo(it.kotlin, true) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.kotlin", "com.lightningkite.rx.android.resources.setVideo"),
    level = DeprecationLevel.ERROR
)
@JvmName("bindAndStartOptional")
fun PlayerView.bindAndStart(video: Observable<Optional<Video>>): Unit = no()

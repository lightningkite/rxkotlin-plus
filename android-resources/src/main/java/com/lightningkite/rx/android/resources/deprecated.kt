package com.lightningkite.rx.android.resources

import android.widget.ImageView
import io.reactivex.rxjava3.core.Observable
import java.util.*

@JvmName("bindImageNullable")
@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("image.subscribeAutoDispose(this, { setImage(it.kotlin) })", "com.lightningkite.rx.android.subscribeAutoDispose", "com.lightningkite.rx.android.resources.setImage"),
    level = DeprecationLevel.ERROR
)
fun ImageView.bindImage(image: Observable<Optional<Image>>): Unit = throw NotImplementedError()


@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("image.subscribeAutoDispose(this, ImageView::setImage)", "com.lightningkite.rx.android.subscribeAutoDispose", "com.lightningkite.rx.android.resources.setImage"),
    level = DeprecationLevel.ERROR
)
fun ImageView.bindImage(image: Observable<Image>): Unit = throw NotImplementedError()


@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("this.setImage(image)", "com.lightningkite.rx.android.resources.setImage"),
    level = DeprecationLevel.ERROR
)
fun ImageView.loadImage(image:Image?):Unit = throw NotImplementedError()
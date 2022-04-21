package com.lightningkite.rx.dsl

import android.content.Context
import android.view.View

@RxKotlinViewDsl open class ViewDsl(
    val context: Context,
    val defaultSpacing: Int = 8
) {
    @RxKotlinViewDsl fun View.marginDefault() { lparams.setMargins(defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp) }
    @RxKotlinViewDsl fun View.padDefault() { setPadding(defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp) }
}
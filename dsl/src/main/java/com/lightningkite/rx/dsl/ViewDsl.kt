package com.lightningkite.rx.dsl

import android.content.Context
import android.view.View

@RxKotlinViewDsl open class ViewDsl(
    val context: Context,
    val defaultSpacing: Int = 8
) {
    @RxKotlinViewDsl fun <T: View> T.applyDefaultSpacing(): T = apply { lparams.setMargins(defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp) }
    @RxKotlinViewDsl fun <T: View> T.applyDefaultPadding(): T = apply { setPadding(defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp) }
}
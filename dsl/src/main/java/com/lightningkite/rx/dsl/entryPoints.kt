package com.lightningkite.rx.dsl

import android.content.Context
import android.view.View
import com.lightningkite.rx.viewgenerators.ActivityAccess

@RxKotlinViewDsl fun ActivityAccess.dsl(make: ViewDsl.()-> View): View = context.dsl(make)
@RxKotlinViewDsl fun Context.dsl(make: ViewDsl.()-> View): View = ViewDsl(this).let(make)
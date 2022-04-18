package com.lightningkite.rx.viewgenerators


import android.util.DisplayMetrics
import android.view.LayoutInflater
import com.lightningkite.rx.android.ColorResource
import com.lightningkite.rx.android.StringResource
import com.lightningkite.rx.viewgenerators.ActivityAccess
import java.util.*

/**
 * A shortcut for getting resources.
 */
fun ActivityAccess.getString(resource: StringResource): String = context.getString(resource)

/**
 * A shortcut for getting resources.
 */
fun ActivityAccess.getColor(resource: ColorResource): Int = context.resources.getColor(resource)

/**
 * A shortcut for getting resources.
 */
val ActivityAccess.displayMetrics: DisplayMetrics get() = context.resources.displayMetrics

/**
 * A shortcut for getting resources.
 */
val ActivityAccess.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)


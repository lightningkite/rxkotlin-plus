package com.lightningkite.rx.viewgenerators


import android.util.DisplayMetrics
import android.view.LayoutInflater
import com.lightningkite.rx.android.ColorResource
import com.lightningkite.rx.android.StringResource
import com.lightningkite.rx.viewgenerators.ActivityAccess
import java.util.*

fun ActivityAccess.getString(resource: StringResource): String = context.getString(resource)
fun ActivityAccess.getColor(resource: ColorResource): Int = context.resources.getColor(resource)
val ActivityAccess.displayMetrics: DisplayMetrics get() = context.resources.displayMetrics
val ActivityAccess.layoutInflater: LayoutInflater get() = activity!!.layoutInflater


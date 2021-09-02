package com.lightningkite.rxkotlinproperty.viewgenerators

import com.lightningkite.rxkotlinproperty.PropertyStack

fun <T:Any> PropertyStack<T>.backPressPop(): Boolean {
    val last = stack.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return pop()
}

fun <T:Any> PropertyStack<T>.backPressDismiss(): Boolean {
    val last = stack.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return dismiss()
}
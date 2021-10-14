package com.lightningkite.rx.viewgenerators

import com.lightningkite.rx.ValueSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject

typealias StackSubject<T> = ValueSubject<List<T>>
typealias ViewGeneratorStack = StackSubject<ViewGenerator>

fun ViewGeneratorStack.backPressPop(): Boolean {
    val last = value.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return pop()
}

fun ViewGeneratorStack.backPressDismiss(): Boolean {
    val last = value.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return dismiss()
}

fun <T> StackSubject<T>.push(t: T) {
    onNext(value + t)
}
fun <T> StackSubject<T>.swap(t: T) {
    onNext(value.toMutableList().apply {
        removeAt(lastIndex)
        add(t)
    })
}
fun <T> StackSubject<T>.pop(): Boolean {
    if (value.size <= 1) {
        return false
    }
    onNext(value.toMutableList().apply {
        removeAt(lastIndex)
    })
    return true
}
fun <T> StackSubject<T>.dismiss(): Boolean {
    if (value.isEmpty()) {
        return false
    }
    onNext(value.toMutableList().apply {
        removeAt(lastIndex)
    })
    return true
}
fun <T> StackSubject<T>.popTo(t: T) {
    val stack = value.toMutableList()
    var found = false
    for (i in 0..stack.lastIndex) {
        if (found) {
            stack.removeAt(stack.lastIndex)
        } else if (stack[i] === t) {
            found = true
        }
    }
    onNext(stack)
}
fun <T> StackSubject<T>.popTo(predicate: (T) -> Boolean) {
    val stack = value.toMutableList()
    var found = false
    for (i in 0..stack.lastIndex) {
        if (found) {
            stack.removeAt(stack.lastIndex)
        } else if (predicate(stack[i])) {
            found = true
        }
    }
    onNext(stack)
}
fun <T> StackSubject<T>.root() {
    onNext(listOf(value.first()))
}
fun <T> StackSubject<T>.reset(t: T) {
    onNext(listOf(t))
}
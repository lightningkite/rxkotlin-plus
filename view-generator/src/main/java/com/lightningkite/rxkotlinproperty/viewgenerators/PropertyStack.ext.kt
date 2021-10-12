package com.lightningkite.rxkotlinproperty.viewgenerators

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject

typealias StackBehaviorSubject<T> = BehaviorSubject<List<T>>
typealias ViewGeneratorStack = StackBehaviorSubject<ViewGenerator>

fun ViewGeneratorStack.backPressPop(): Boolean {
    val last = value!!.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return pop()
}

fun ViewGeneratorStack.backPressDismiss(): Boolean {
    val last = value!!.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return dismiss()
}

fun <T> StackBehaviorSubject<T>.push(t: T) {
    onNext(value!! + t)
}
fun <T> StackBehaviorSubject<T>.swap(t: T) {
    onNext(value!!.toMutableList().apply {
        removeAt(lastIndex)
        add(t)
    })
}
fun <T> StackBehaviorSubject<T>.pop(): Boolean {
    if (value!!.size <= 1) {
        return false
    }
    onNext(value!!.toMutableList().apply {
        removeAt(lastIndex)
    })
    return true
}
fun <T> StackBehaviorSubject<T>.dismiss(): Boolean {
    if (value!!.isEmpty()) {
        return false
    }
    onNext(value!!.toMutableList().apply {
        removeAt(lastIndex)
    })
    return true
}
fun <T> StackBehaviorSubject<T>.popTo(t: T) {
    val stack = value!!.toMutableList()
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
fun <T> StackBehaviorSubject<T>.popTo(predicate: (T) -> Boolean) {
    val stack = value!!.toMutableList()
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
fun <T> StackBehaviorSubject<T>.root() {
    onNext(listOf(value!!.first()))
}
fun <T> StackBehaviorSubject<T>.reset(t: T) {
    onNext(listOf(t))
}
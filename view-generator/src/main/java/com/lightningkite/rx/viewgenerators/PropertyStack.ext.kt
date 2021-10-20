package com.lightningkite.rx.viewgenerators

import com.lightningkite.rx.ValueSubject

typealias StackSubject<T> = ValueSubject<List<T>>
typealias ViewGeneratorStack = StackSubject<ViewGenerator>

/**
 * Like [pop], but allows the [ViewGenerator] to interrupt the action in [HasBackAction].
 */
fun ViewGeneratorStack.backPressPop(): Boolean {
    val last = value.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return pop()
}

/**
 * Like [dismiss], but allows the [ViewGenerator] to interrupt the action in [HasBackAction].
 */
fun ViewGeneratorStack.backPressDismiss(): Boolean {
    val last = value.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return dismiss()
}

/**
 * Adds an element to the top of the stack and emits an event.
 */
fun <T> StackSubject<T>.push(element: T) {
    onNext(value + element)
}

/**
 * Swaps the top element of the stack for this one and emits an event.
 */
fun <T> StackSubject<T>.swap(element: T) {
    onNext(value.toMutableList().apply {
        removeAt(lastIndex)
        add(element)
    })
}

/**
 * Removes the top element of the stack, as long as there will be at least one element left afterwards.
 * @return if there was any value to remove
 */
fun <T> StackSubject<T>.pop(): Boolean {
    if (value.size <= 1) {
        return false
    }
    onNext(value.toMutableList().apply {
        removeAt(lastIndex)
    })
    return true
}

/**
 * Removes the top element of the stack.
 * @return if there was any value to remove
 */
fun <T> StackSubject<T>.dismiss(): Boolean {
    if (value.isEmpty()) {
        return false
    }
    onNext(value.toMutableList().apply {
        removeAt(lastIndex)
    })
    return true
}

/**
 * Removes all elements going back to an element matching [predicate].
 */
fun <T> StackSubject<T>.popToPredicate(predicate: (T) -> Boolean) {
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

/**
 * Removes all elements but the first.
 */
fun <T> StackSubject<T>.root() {
    onNext(listOf(value.first()))
}

/**
 * Clears out the entire stack, replacing it with a single element.
 */
fun <T> StackSubject<T>.reset(element: T) {
    onNext(listOf(element))
}
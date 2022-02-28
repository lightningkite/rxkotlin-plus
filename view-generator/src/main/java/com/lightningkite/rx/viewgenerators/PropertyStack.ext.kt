package com.lightningkite.rx.viewgenerators

import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit

typealias StackSubject<T> = ValueSubject<List<T>>
typealias ViewGeneratorStack = StackSubject<ViewGenerator>

/**
 * Removes the top element of the stack, as long as there will be at least one element left afterwards.
 * @return if there was any value to remove
 */

/**
 * Like [pop], but allows the [ViewGenerator] to interrupt the action in [HasBackAction].
 */
fun ValueSubject<List<ViewGenerator>>.backPressPop(): Boolean {
    val last = value.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return pop()
}

/**
 * Like [dismiss], but allows the [ViewGenerator] to interrupt the action in [HasBackAction].
 */
fun ValueSubject<List<ViewGenerator>>.backPressDismiss(): Boolean {
    val last = value.lastOrNull()
    if(last is HasBackAction && last.onBackPressed()) return true
    return dismiss()
}

/**
 * Adds an element to the top of the stack and emits an event.
 */
fun ValueSubject<List<ViewGenerator>>.push(element: ViewGenerator) {
    onNext(value + element)
}

/**
 * Swaps the top element of the stack for this one and emits an event.
 */
fun ValueSubject<List<ViewGenerator>>.swap(element: ViewGenerator) {
    onNext(value.toMutableList().apply {
        removeAt(lastIndex)
        add(element)
    })
}

/**
 * Removes the top element of the stack, as long as there will be at least one element left afterwards.
 * @return if there was any value to remove
 */
fun ValueSubject<List<ViewGenerator>>.pop(): Boolean {
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
fun ValueSubject<List<ViewGenerator>>.dismiss(): Boolean {
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
fun ValueSubject<List<ViewGenerator>>.popToPredicate(predicate: (ViewGenerator) -> Boolean) {
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
fun ValueSubject<List<ViewGenerator>>.root() {
    onNext(listOf(value.first()))
}

/**
 * Clears out the entire stack, replacing it with a single element.
 */
fun ValueSubject<List<ViewGenerator>>.reset(element: ViewGenerator) {
    onNext(listOf(element))
}
///**
// * Like [pop], but allows the [ViewGenerator] to interrupt the action in [HasBackAction].
// */
//fun _root_ide_package_.com.lightningkite.rx.ValueSubject<List<_root_ide_package_.com.campchef.iotgrill.ViewGenTransition.backPressPop(): Boolean {
//    val last = value.lastOrNull() ?: return false
//    if(last.viewGen is HasBackAction && last.viewGen.onBackPressed()) return true
//    return pop()
//}

/**
 * Like [dismiss], but allows the [ViewGenerator] to interrupt the action in [HasBackAction].
 */
@JvmName("backPressDismissTransition")
fun ValueSubject<List<ViewGenTransition>>.backPressDismiss(): Boolean {
    val last = value.lastOrNull() ?: return false
    if(last.viewGen is HasBackAction && last.viewGen.onBackPressed()) return true
    return dismiss()
}

/**
 * Adds an element to the top of the stack and emits an event.
 */
@JvmName("pushGenTransition")
fun ValueSubject<List<ViewGenTransition>>.push(element: ViewGenTransition) {
    onNext(value + element)
}

/**
 * Swaps the top element of the stack for this one and emits an event.
 */
@JvmName("swapGenTransition")
fun ValueSubject<List<ViewGenTransition>>.swap(element: ViewGenTransition) {
    onNext(value.toMutableList().apply {
        removeAt(lastIndex)
        add(element)
    })
}


/**
 * Removes the top element of the stack, as long as there will be at least one element left afterwards.
 * @return if there was any value to remove
 */
@JvmName("popGenTransition")
fun ValueSubject<List<ViewGenTransition>>.pop(): Boolean {
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
@JvmName("dismissGenTransition")
fun ValueSubject<List<ViewGenTransition>>.dismiss(): Boolean {
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
@JvmName("popGenTransitionToPredicate")
fun ValueSubject<List<ViewGenTransition>>.popToPredicate(predicate: (ViewGenTransition) -> Boolean) {
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
@JvmName("rootGenTransition")
fun ValueSubject<List<ViewGenTransition>>.root() {
    onNext(listOf(value.first()))
}

/**
 * Clears out the entire stack, replacing it with a single element.
 */
@JvmName("resetWithGenTransition")
fun ValueSubject<List<ViewGenTransition>>.reset(element: ViewGenTransition) {
    onNext(listOf(element))
}

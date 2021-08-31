//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.subjects.PublishSubject

class PropertyStack<T : Any> : Property<List<T>>() {

    companion object {
        fun <T: Any> withFirst(value: T): PropertyStack<T> {
            val result = PropertyStack<T>()
            result.reset(value)
            return result
        }
    }

    override val onChange: PublishSubject<Box<List<T>>> = PublishSubject.create()
    override val value: List<T>
        get() {
            return stack
        }

    val stack: ArrayList<T> = ArrayList<T>()

    fun push(t: T) {
        stack.add(t)
        onChange.onNext(Box.wrap(stack))
    }

    fun swap(t: T) {
        stack.removeAt(stack.lastIndex)
        stack.add(t)
        onChange.onNext(Box.wrap(stack))
    }

    fun pop(): Boolean {
        if (stack.size <= 1) {
            return false
        }
        stack.removeAt(stack.lastIndex)
        onChange.onNext(Box.wrap(stack))
        return true
    }

    fun dismiss(): Boolean {
        if (stack.isEmpty()) {
            return false
        }
        stack.removeAt(stack.lastIndex)
        onChange.onNext(Box.wrap(stack))
        return true
    }

//    fun backPressPop(): Boolean {
//        val last = stack.lastOrNull()
//        if(last is HasBackAction && last.onBackPressed()) return true
//        return pop()
//    }
//
//    fun backPressDismiss(): Boolean {
//        val last = stack.lastOrNull()
//        if(last is HasBackAction && last.onBackPressed()) return true
//        return dismiss()
//    }

    fun popTo(t: T) {
        var found = false
        for (i in 0..stack.lastIndex) {
            if (found) {
                stack.removeAt(stack.lastIndex)
            } else if (stack[i] === t) {
                found = true
            }
        }
        onChange.onNext(Box.wrap(stack))
    }

    fun popTo(predicate: (T) -> Boolean) {
        var found = false
        for (i in 0..stack.lastIndex) {
            if (found) {
                stack.removeAt(stack.lastIndex)
            } else if (predicate(stack[i])) {
                found = true
            }
        }
        onChange.onNext(Box.wrap(stack))
    }

    fun root() {
        popTo(t = stack.first())
    }

    fun reset(t: T) {
        stack.clear()
        stack.add(t)
        onChange.onNext(Box.wrap(stack))
    }
}

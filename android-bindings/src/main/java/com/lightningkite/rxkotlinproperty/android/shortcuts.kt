package com.lightningkite.rxkotlinproperty.android

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java9.util.Optional

infix fun <T: Any> Observable<T>.isEqualTo(other: Observable<T>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left == right }

@JvmName("isEqualToN1")
infix fun <T: Any> Observable<Optional<T>>.isEqualTo(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.kotlin == right }

@JvmName("isEqualToN2")
infix fun <T: Any> Observable<T>.isEqualTo(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left == right.kotlin }

@JvmName("isEqualToOrNullN1")
infix fun <T: Any> Observable<Optional<T>>.isEqualToOrNull(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.isEmpty || left.kotlin == right }

@JvmName("isEqualToOrNullN2")
infix fun <T: Any> Observable<T>.isEqualToOrNull(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> right.isEmpty || left == right.kotlin }

infix fun Observable<Boolean>.and(other: Observable<Boolean>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left && right }

infix fun Observable<Boolean>.or(other: Observable<Boolean>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left || right }

@JvmName("toSubjectStringFromInt")
fun Subject<Int>.toSubjectString(): Subject<String> {
    return map(
        read = { it.toString() },
        write = { it.toIntOrNull() }
    )
}

@JvmName("toSubjectStringFromDouble")
fun Subject<Double>.toSubjectString(): Subject<String> {
    return map(
        read = { it.toString() },
        write = { it.toDoubleOrNull() }
    )
}

@JvmName("toSubjectStringFromOptionalInt")
fun Subject<Optional<Int>>.toSubjectString(): Subject<String> {
    return map(
        read = { it.kotlin?.toString() ?: "" },
        write = { it.toIntOrNull().optional }
    )
}

@JvmName("toSubjectStringFromOptionalDouble")
fun Subject<Optional<Double>>.toSubjectString(): Subject<String> {
    return map(
        read = { it.kotlin?.toString() ?: "" },
        write = { it.toDoubleOrNull().optional }
    )
}

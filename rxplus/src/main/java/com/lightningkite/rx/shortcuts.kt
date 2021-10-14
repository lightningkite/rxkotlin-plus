package com.lightningkite.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.Optional

infix fun <T: Any> Observable<T>.isEqualTo(other: Observable<T>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left == right }

@JvmName("isEqualToN1")
infix fun <T: Any> Observable<Optional<T>>.isEqualTo(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.kotlin == right }

@JvmName("isEqualToN2")
infix fun <T: Any> Observable<T>.isEqualTo(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left == right.kotlin }

@JvmName("isEqualTo")
infix fun <T: Any> Subject<T>.isEqualTo(constant: T): Observable<Boolean>
        = this.map { it == constant }.withWrite { if(it) onNext(constant) }

@JvmName("isEqualToN1")
infix fun <T: Any> Subject<Optional<T>>.isEqualTo(constant: T): Observable<Boolean>
        = this.map { it.kotlin == constant }.withWrite { if(it) onNext(constant.optional) }

@JvmName("isEqualToOrNullN1")
infix fun <T: Any> Observable<Optional<T>>.isEqualToOrNull(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.isEmpty || left.kotlin == right }

@JvmName("isEqualToOrNullN2")
infix fun <T: Any> Observable<T>.isEqualToOrNull(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> right.isEmpty || left == right.kotlin }

infix fun <T: Any> Observable<T>.isIn(other: Observable<Collection<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left in right }

infix fun <T: Any> Subject<Optional<T>>.isEqualToOrNull(constant: T): Observable<Boolean>
        = this.map { it.isEmpty || it.get() == constant }.withWrite { if(it) onNext(constant.optional) }

infix fun <T: Any> ValueSubject<Collection<T>>.contains(constant: T): Observable<Boolean>
        = this.map { it.contains(constant) }.withWrite {
    if(it) onNext(value + constant)
    else onNext(value - constant)
}

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

fun Subject<Float>.toSubjectInt(): Subject<Int> {
    return map(
        read = { it.toInt() },
        write = { it.toFloat() }
    )
}

fun Subject<Int>.toSubjectFloat(): Subject<Float> {
    return map(
        read = { it.toFloat() },
        write = { it.toInt() }
    )
}

operator fun Subject<Int>.plus(amount: Int): Subject<Int> {
    return map(
        read = { it + amount },
        write = { it - amount }
    )
}
operator fun Subject<Int>.times(amount: Int): Subject<Int> {
    return map(
        read = { it * amount },
        write = { it / amount }
    )
}
operator fun Subject<Float>.plus(amount: Float): Subject<Float> {
    return map(
        read = { it + amount },
        write = { it - amount }
    )
}
operator fun Subject<Float>.times(amount: Float): Subject<Float> {
    return map(
        read = { it * amount },
        write = { it / amount }
    )
}

infix fun <T: Any, B: Any> Observable<T>.mapNotNull(mapper: (T)->B?): Observable<B>
        = this.switchMap { mapper(it)?.let{ Observable.just(it) } ?: Observable.empty() }

@JvmName("mapNullable")
infix fun <T: Any, B: Any> Observable<Optional<T>>.mapNullable(mapper: (T)->B?): Observable<Optional<B>>
        = this.map { it.kotlin?.let(mapper).optional }

@JvmName("mapNullable2")
infix fun <T: Any, B: Any> Observable<T>.mapNullable(mapper: (T)->B?): Observable<Optional<B>>
        = this.map { it.let(mapper).optional }

infix fun <T: Any, B: Any> Observable<Optional<T>>.flatMapNullable(mapper: (T)->Observable<B>?): Observable<Optional<B>>
        = this.flatMap { it.kotlin?.let(mapper)?.map { it.optional } ?: Observable.just(Optional.empty<B>()) }

infix fun <T: Any, B: Any> Observable<Optional<T>>.switchMapNullable(mapper: (T)->Observable<B>?): Observable<Optional<B>>
        = this.switchMap { it.kotlin?.let(mapper)?.map { it.optional } ?: Observable.just(Optional.empty<B>()) }

@JvmName("flatMapNullable2")
infix fun <T: Any, B: Any> Observable<Optional<T>>.flatMapNullable(mapper: (T)->Observable<Optional<B>>?): Observable<Optional<B>>
        = this.flatMap { it.kotlin?.let(mapper) ?: Observable.just(Optional.empty<B>()) }

@JvmName("switchMapNullable2")
infix fun <T: Any, B: Any> Observable<Optional<T>>.switchMapNullable(mapper: (T)->Observable<Optional<B>>?): Observable<Optional<B>>
        = this.switchMap { it.kotlin?.let(mapper) ?: Observable.just(Optional.empty<B>()) }

fun <Element : Any, R : Any, OUT : Any> Observable<Element>.combineLatest(
    observable: Observable<R>,
    function: (Element, R) -> OUT
): Observable<OUT> = Observable.combineLatest(this, observable, function)

fun <IN : Any, OUT : Any> List<Observable<IN>>.combineLatest(combine: (List<IN>) -> OUT): Observable<OUT> =
    Observable.combineLatest(this) { stupidArray: Array<Any?> ->
        combine(stupidArray.toList() as List<IN>)
    }

fun <IN : Any> List<Observable<IN>>.combineLatest(): Observable<List<IN>> =
    Observable.combineLatest(this) { stupidArray: Array<Any?> -> stupidArray.toList() as List<IN> }

fun <IN : Any> List<Single<IN>>.zip(): Single<List<IN>> =
    Single.zip(this) { stupidArray: Array<Any?> -> stupidArray.toList() as List<IN> }

fun <Element : Any> Single<Element>.working(property: Subject<Boolean>): Single<Element> {
    return this
        .doOnSubscribe { it -> property.onNext(true) }
        .doFinally { property.onNext(false) }
}

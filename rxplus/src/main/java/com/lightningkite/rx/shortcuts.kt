package com.lightningkite.rx

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.*
import io.reactivex.rxjava3.subjects.Subject
import java.time.*
import java.util.*
import kotlin.reflect.KMutableProperty1

fun <T: Any> Observable<Optional<T>>.subscribeByNullable(
    onError: (Throwable) -> Unit = {  },
    onComplete: () -> Unit = {  },
    onNext: (T?) -> Unit = {  }
): Disposable
    = this.subscribeBy(onError, onComplete) { onNext(it.kotlin) }

fun <T: Any> Single<Optional<T>>.subscribeByNullable(
    onError: (Throwable) -> Unit = { },
    onSuccess: (T?) -> Unit = {  }
): Disposable
    = this.subscribeBy(onError) { onSuccess(it.kotlin) }

fun <T: Any> Maybe<Optional<T>>.subscribeByNullable(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onSuccess: (T?) -> Unit = {}
): Disposable
    = this.subscribeBy(onError, onComplete) { onSuccess(it.kotlin) }

infix fun <T: Any> Observable<T>.isEqualTo(other: Observable<T>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left == right }

infix fun <T: Any> Observable<T>.notEqualTo(other: Observable<T>): Observable<Boolean>
    = Observable.combineLatest(this, other){ left, right -> left != right }

@JvmName("isEqualToN1")
infix fun <T: Any> Observable<Optional<T>>.isEqualTo(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.kotlin == right }

@JvmName("notEqualToN1")
infix fun <T: Any> Observable<Optional<T>>.notEqualTo(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other){ left, right -> left.kotlin != right }

@JvmName("isEqualToN2")
infix fun <T: Any> Observable<T>.isEqualTo(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left == right.kotlin }

@JvmName("notEqualToN2")
infix fun <T: Any> Observable<T>.notEqualTo(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other){ left, right -> left != right.kotlin }

infix fun <T: Any> Subject<T>.isEqualTo(constant: T): Subject<Boolean>
        = this.map { it == constant }.withWrite { if(it) onNext(constant) }

infix fun <T: Any> Subject<T>.notEqualTo(constant: T): Subject<Boolean>
        = this.map { it != constant }.withWrite { if(!it) onNext(constant) }

@JvmName("isEqualToN1")
infix fun <T: Any> Subject<Optional<T>>.isEqualTo(constant: T): Subject<Boolean>
        = this.map { it.kotlin == constant }.withWrite { if(it) onNext(constant.optional) }

@JvmName("notEqualToN1")
infix fun <T: Any> Subject<Optional<T>>.notEqualTo(constant: T): Subject<Boolean>
        = this.map { it.kotlin != constant }.withWrite { if(it) onNext(constant.optional) }

@JvmName("isEqualToOrNullN1")
infix fun <T: Any> Observable<Optional<T>>.isEqualToOrNull(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.isEmpty || left.kotlin == right }

@JvmName("notEqualToOrNullN1")
infix fun <T: Any> Observable<Optional<T>>.notEqualToOrNull(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.isEmpty || left.kotlin != right }

@JvmName("isEqualToOrNullN2")
infix fun <T: Any> Observable<T>.isEqualToOrNull(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> right.isEmpty || left == right.kotlin }

@JvmName("notEqualToOrNullN2")
infix fun <T: Any> Observable<T>.notEqualToOrNull(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> right.isEmpty || left != right.kotlin }

infix fun <T: Any> HasValueSubject<Optional<T>>.isEqualToOrNull(constant: T): Subject<Boolean>
        = this.map { it.isEmpty || it.kotlin == constant }.withWrite { if(it || (!it && this.value.isEmpty)) onNext(constant.optional) else onNext(Optional.empty()) }

infix fun <T: Any> HasValueSubject<Optional<T>>.notEqualToOrNull(constant: T): Subject<Boolean>
        = this.map { it.isEmpty || it.kotlin != constant }.withWrite { if(it || (!it && this.value.isEmpty)) onNext(constant.optional) else onNext(Optional.empty()) }

operator fun <T: Any, V: Any> HasValueSubject<T>.get(property: KMutableProperty1<T, V>): HasValueSubject<V> = mapWithExisting(
    read = { property.get(it) },
    write = { existing, it -> property.set(existing, it); existing }
)

infix fun <T: Any> Observable<T>.isIn(other: Observable<Collection<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left in right }

infix fun <T: Any> HasValueSubject<Collection<T>>.contains(constant: T): Subject<Boolean>
        = this.map { it.contains(constant) }.withWrite {
    if(it) onNext(value + constant)
    else onNext(value - constant)
}

infix fun Observable<Boolean>.and(other: Observable<Boolean>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left && right }

infix fun Observable<Boolean>.or(other: Observable<Boolean>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left || right }

operator fun Subject<Boolean>.not(): Subject<Boolean>
    = map(read = { !it }, write = { !it })

@JvmName("toSubjectStringFromInt")
fun Subject<Int>.toSubjectString(): Subject<String> {
    return mapMaybeWrite(
        read = { it.toString() },
        write = { it.toIntOrNull() }
    )
}

@JvmName("toSubjectStringFromDouble")
fun Subject<Double>.toSubjectString(): Subject<String> {
    return mapMaybeWrite(
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

fun HasValueSubject<ZonedDateTime>.toSubjectLocalDate(): HasValueSubject<LocalDate> {
    return mapWithExisting(
        read = { it.toLocalDate() },
        write = { existing, it -> existing.with(it) }
    )
}
fun HasValueSubject<ZonedDateTime>.toSubjectLocalTime(): HasValueSubject<LocalTime> {
    return mapWithExisting(
        read = { it.toLocalTime() },
        write = { existing, it -> existing.with(it) }
    )
}

@JvmName("Instant_toSubjectLocalDate")
fun HasValueSubject<Instant>.toSubjectLocalDate(): HasValueSubject<LocalDate> {
    return mapWithExisting(
        read = { it.atZone(ZoneId.systemDefault()).toLocalDate() },
        write = { existing, it -> existing.atZone(ZoneId.systemDefault()).with(it).toInstant() }
    )
}
@JvmName("Instant_toSubjectLocalTime")
fun HasValueSubject<Instant>.toSubjectLocalTime(): HasValueSubject<LocalTime> {
    return mapWithExisting(
        read = { it.atZone(ZoneId.systemDefault()).toLocalTime() },
        write = { existing, it -> existing.atZone(ZoneId.systemDefault()).with(it).toInstant() }
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

infix fun <T: Any, B: Any> Observable<Optional<T>>.mapFromNullable(mapper: (T?)->B): Observable<B>
        = this.map { it.kotlin.let(mapper) }

infix fun <T: Any, B: Any> Observable<Optional<T>>.mapNullable(mapper: (T?)->B?): Observable<Optional<B>>
        = this.map { it.kotlin.let(mapper).optional }

infix fun <T: Any, B: Any> Observable<T>.mapToNullable(mapper: (T)->B?): Observable<Optional<B>>
        = this.map { it.let(mapper).optional }

infix fun <T: Any, B: Any> Observable<Optional<T>>.flatMapNotNull(mapper: (T)->Observable<B>?): Observable<Optional<B>>
        = this.flatMap { it.kotlin?.let(mapper)?.map { it.optional } ?: Observable.just(Optional.empty<B>()) }

infix fun <T: Any, B: Any> Observable<Optional<T>>.switchMapNotNull(mapper: (T)->Observable<B>?): Observable<Optional<B>>
        = this.switchMap { it.kotlin?.let(mapper)?.map { it.optional } ?: Observable.just(Optional.empty<B>()) }

@JvmName("flatMapNotNull2")
infix fun <T: Any, B: Any> Observable<Optional<T>>.flatMapNotNull(mapper: (T)->Observable<Optional<B>>?): Observable<Optional<B>>
        = this.flatMap { it.kotlin?.let(mapper) ?: Observable.just(Optional.empty<B>()) }

@JvmName("switchMapNotNull2")
infix fun <T: Any, B: Any> Observable<Optional<T>>.switchMapNotNull(mapper: (T)->Observable<Optional<B>>?): Observable<Optional<B>>
        = this.switchMap { it.kotlin?.let(mapper) ?: Observable.just(Optional.empty<B>()) }

fun <Element : Any, R : Any, OUT : Any> Observable<Element>.combineLatest(
    observable: Observable<R>,
    function: (Element, R) -> OUT
): Observable<OUT> = Observable.combineLatest(this, observable, function)

@Suppress("UNCHECKED_CAST")
fun <IN : Any, OUT : Any> List<Observable<IN>>.combineLatest(combine: (List<IN>) -> OUT): Observable<OUT> =
    Observable.combineLatest(this) { stupidArray: Array<Any?> ->
        combine(stupidArray.toList() as List<IN>)
    }

@Suppress("UNCHECKED_CAST")
fun <IN : Any> List<Observable<IN>>.combineLatest(): Observable<List<IN>> =
    Observable.combineLatest(this) { stupidArray: Array<Any?> -> stupidArray.toList() as List<IN> }

@Suppress("UNCHECKED_CAST")
fun <IN : Any> List<Single<IN>>.zip(): Single<List<IN>> =
    Single.zip(this) { stupidArray: Array<Any?> -> stupidArray.toList() as List<IN> }

fun <Element : Any> Single<Element>.working(property: Subject<Boolean>): Single<Element> {
    return this
        .doOnSubscribe { property.onNext(true) }
        .doFinally { property.onNext(false) }
}
fun <Element : Any> Maybe<Element>.working(property: Subject<Boolean>): Maybe<Element> {
    return this
        .doOnSubscribe { property.onNext(true) }
        .doFinally { property.onNext(false) }
}
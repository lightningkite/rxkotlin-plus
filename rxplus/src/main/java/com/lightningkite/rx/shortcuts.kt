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


/**
 * A subscribeBy wrapper that unwraps optionals.
 * <p>
 * An extension function on observables of type Optional<T> which wraps the regular subscribeBy.
 * This handles unwrapping the optional before passing it to the callers onNext.
 */
fun <T: Any> Observable<Optional<T>>.subscribeByNullable(
    onError: (Throwable) -> Unit = {  },
    onComplete: () -> Unit = {  },
    onNext: (T?) -> Unit = {  }
): Disposable
    = this.subscribeBy(onError, onComplete) { onNext(it.kotlin) }

/**
 * A subscribeBy wrapper that unwraps optionals.
 * <p>
 * An extension function on Singles of type Optional<T> which wraps the regular subscribeBy.
 * This handles unwrapping the optional before passing it to the callers onSuccess.
 */
fun <T: Any> Single<Optional<T>>.subscribeByNullable(
    onError: (Throwable) -> Unit = { },
    onSuccess: (T?) -> Unit = {  }
): Disposable
    = this.subscribeBy(onError) { onSuccess(it.kotlin) }

/**
 * A subscribeBy wrapper that unwraps optionals.
 * <p>
 * An extension function on Maybes of type Optional<T> which wraps the regular subscribeBy.
 * This handles unwrapping the optional before passing it to the callers onSuccess.
 */
fun <T: Any> Maybe<Optional<T>>.subscribeByNullable(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onSuccess: (T?) -> Unit = {}
): Disposable
    = this.subscribeBy(onError, onComplete) { onSuccess(it.kotlin) }


/**
 * Returns an Observable of type Boolean whose value is the equality check between the
 * two different values from this and the other observable.
 */
infix fun <T: Any> Observable<T>.isEqualTo(other: Observable<T>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left == right }


/**
 * Returns an Observable of type Boolean whose value is the negative equality check between the
 * two different values from this and the other observable.
 */
infix fun <T: Any> Observable<T>.notEqualTo(other: Observable<T>): Observable<Boolean>
    = Observable.combineLatest(this, other){ left, right -> left != right }

/**
 * Returns an Observable of type Boolean whose value is the equality check between the
 * two different values from this and the other observable.
 */
@JvmName("isEqualToN1")
infix fun <T: Any> Observable<Optional<T>>.isEqualTo(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.kotlin == right }

/**
 * Returns an Observable of type Boolean whose value is the negative equality check between the
 * two different values from this and the other observable.
 */
@JvmName("notEqualToN1")
infix fun <T: Any> Observable<Optional<T>>.notEqualTo(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other){ left, right -> left.kotlin != right }

/**
 * Returns an Observable of type Boolean whose value is the equality check between the
 * two different values from this and the other observable.
 */
@JvmName("isEqualToN2")
infix fun <T: Any> Observable<T>.isEqualTo(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left == right.kotlin }

/**
 * Returns an Observable of type Boolean whose value is the negative equality check between the
 * two different values from this and the other observable.
 */
@JvmName("notEqualToN2")
infix fun <T: Any> Observable<T>.notEqualTo(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other){ left, right -> left != right.kotlin }

/**
 * Returns a Subject of type Boolean created from comparing this to the constant.
 * <p>
 * Returns a Subject of type Boolean whose value is the equality check between
 * the value of this and the constant provided. If the new Subject has true written to it,
 * the constant will be passed into the on next of this.
 */
infix fun <T: Any> Subject<T>.isEqualTo(constant: T): Subject<Boolean>
        = this.map { it == constant }.withWrite { if(it) onNext(constant) }

/**
 * Returns a Subject of type Boolean created from comparing this to the constant.
 * <p>
 * Returns a Subject of type Boolean whose value is the negative equality check between
 * the value of this and the constant provided. If the new Subject has false written to it,
 * the constant will be passed into the on next of this.
 */
infix fun <T: Any> Subject<T>.notEqualTo(constant: T): Subject<Boolean>
        = this.map { it != constant }.withWrite { if(!it) onNext(constant) }

/**
 * Returns a Subject of type Boolean created from comparing this to the constant.
 * <p>
 * Returns a Subject of type Boolean whose value is the equality check between
 * the value of this and the constant provided. If the new Subject has true written to it,
 * the constant will be passed into the on next of this.
 */
@JvmName("isEqualToN1")
infix fun <T: Any> Subject<Optional<T>>.isEqualTo(constant: T): Subject<Boolean>
        = this.map { it.kotlin == constant }.withWrite { if(it) onNext(constant.optional) }

/**
 * Returns a Subject of type Boolean created from comparing this to the constant.
 * <p>
 * Returns a Subject of type Boolean whose value is the negative equality check between
 * the value of this and the constant provided. If the new Subject has false written to it,
 * the constant will be passed into the on next of this.
 */
@JvmName("notEqualToN1")
infix fun <T: Any> Subject<Optional<T>>.notEqualTo(constant: T): Subject<Boolean>
        = this.map { it.kotlin != constant }.withWrite { if(it) onNext(constant.optional) }

/**
 * Returns an Observable of type Boolean created from comparing this to the other Observable.
 * <p>
 * Returns an Observable of type Boolean whose value is the equality check between the
 * two different values from this and the other observable. If the value of this is an empty optional
 * it will return true.
 */
@JvmName("isEqualToOrNullN1")
infix fun <T: Any> Observable<Optional<T>>.isEqualToOrNull(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.isEmpty || left.kotlin == right }

/**
 * Returns an Observable of type Boolean created from comparing this to the other Observable.
 * <p>
 * Returns an Observable of type Boolean whose value is the negative equality check between the
 * two different values from this and the other observable. If the value of this is an empty optional
 * it will return true.
 */
@JvmName("notEqualToOrNullN1")
infix fun <T: Any> Observable<Optional<T>>.notEqualToOrNull(other: Observable<T>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left.isEmpty || left.kotlin != right }

/**
 * Returns an Observable of type Boolean created from comparing this to the other Observable.
 * <p>
 * Returns an Observable of type Boolean whose value is the equality check between the
 * two different values from this and the other observable. If the value of other is an empty optional
 * it will return true.
 */
@JvmName("isEqualToOrNullN2")
infix fun <T: Any> Observable<T>.isEqualToOrNull(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> right.isEmpty || left == right.kotlin }

/**
 * Returns an Observable of type Boolean created from comparing this to the other Observable.
 * <p>
 * Returns an Observable of type Boolean whose value is the negative equality check between the
 * two different values from this and the other observable. If the value of other is an empty optional
 * it will return true.
 */
@JvmName("notEqualToOrNullN2")
infix fun <T: Any> Observable<T>.notEqualToOrNull(other: Observable<Optional<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> right.isEmpty || left != right.kotlin }

/**
 * Returns a HasValueSubject of type Boolean created from comparing this to the constant.
 * <p>
 * Returns a Subject of type Boolean whose value is the equality check between this and the constant provided.
 * If the value of this is an empty optional it will return true. If true is written to the new subject, or false and this is an empty Optional,
 * the constant will be passed to onNext of this. Else an empty Optional is passed to onNext.
 *
 */
infix fun <T: Any> HasValueSubject<Optional<T>>.isEqualToOrNull(constant: T): Subject<Boolean>
        = this.map { it.isEmpty || it.kotlin == constant }.withWrite { if(it || (!it && this.value.isEmpty)) onNext(constant.optional) else onNext(Optional.empty()) }

/**
 * Returns a HasValueSubject of type Boolean created from comparing this to the constant.
 * <p>
 * Returns a Subject of type Boolean whose value is the negative equality check between this and the constant provided.
 * If the value of this is an empty optional it will return true. If false is written to the new subject, or true and this is an empty Optional,
 * the constant will be passed to onNext of this. Else an empty Optional is passed to onNext.
 *
 */
infix fun <T: Any> HasValueSubject<Optional<T>>.notEqualToOrNull(constant: T): Subject<Boolean>
        = this.map { it.isEmpty || it.kotlin != constant }.withWrite { if(!it || (it && this.value.isEmpty)) onNext(constant.optional) else onNext(Optional.empty()) }

/**
 * A shortcut for mapWithExisting on a HasValueSubject with bracket access using a KMutableProperty.
 * <p>
 * A shortcut for single property maps with mapWithExisting. It reads the property one direction and writes to the
 * property the other direction.
 *
 * Example:
 * data class Item(val first: Int, val second: Int)
 * val itemObs:ValueSubject<Item> = ValueSubject(Item(0, 1))
 * val intObs:HasValueSubject<Int> = itemObs[Item::first]
 */
operator fun <T: Any, V: Any> HasValueSubject<T>.get(property: KMutableProperty1<T, V>): HasValueSubject<V> = mapWithExisting(
    read = { property.get(it) },
    write = { existing, it -> property.set(existing, it); existing }
)

/**
 * Returns an Observable of type Boolean created from checking the collection for this value.
 * <p>
 * Returns an Observable of type Boolean created from doing a check on the collection provided from
 * the other observable. If this value is contained by the collection it returns true, otherwise false.
 */
infix fun <T: Any> Observable<T>.isIn(other: Observable<Collection<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left in right }

/**
 * Returns an Observable of type Boolean created from checking the collection for this value.
 * <p>
 * Returns an Observable of type Boolean created from doing a check on the collection provided from
 * the other observable. If this value is NOT contained by the collection it returns true, otherwise false.
 */
infix fun <T: Any> Observable<T>.notIn(other: Observable<Collection<T>>): Observable<Boolean>
        = Observable.combineLatest(this, other) { left, right -> left !in right }

/**
 * Returns a Subject of type Boolean created from checking the collection for the constant.
 * <p>
 * Returns a Subject of type Boolean created from doing a check on this collection for the provided constant.
 * If this value is contained by the collection it returns true, otherwise false. If true is written to the
 * new subject the constant is added to the collection, then the new collection passed into onNext. If false
 * is written to the new Subject the value is removed from the collection, then the new collection passed into onNext.
 */
infix fun <T: Any> HasValueSubject<Collection<T>>.contains(constant: T): Subject<Boolean>
        = this.map { it.contains(constant) }.withWrite {
    if(it) onNext(value + constant)
    else onNext(value - constant)
}

/**
 * Returns a Subject of type Boolean created from checking the collection for the constant.
 * <p>
 * Returns a Subject of type Boolean created from doing a check on this collection for the provided constant.
 * If this value is NOT contained by the collection it returns true, otherwise false. If true is written to the
 * new Subject the value is removed from the collection, then the new collection passed into onNext. If false
 * is written to the new Subject the constant is added to the collection, then the new collection passed into onNext.
 */
infix fun <T: Any> HasValueSubject<Collection<T>>.doesNotContain(constant: T): Subject<Boolean>
        = this.map { !it.contains(constant) }.withWrite {
    if(it) onNext(value - constant)
    else onNext(value + constant)
}

/**
 * Returns an Observable of type Boolean whose value is the and of each incoming observables.
 */
infix fun Observable<Boolean>.and(other: Observable<Boolean>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left && right }

/**
 * Returns an Observable of type Boolean whose value is the or of each incoming observables.
 */
infix fun Observable<Boolean>.or(other: Observable<Boolean>): Observable<Boolean>
    = Observable.combineLatest(this, other) { left, right -> left || right }

/**
 * Returns a Subject of type Boolean whose value is opposite of the value of this.
 */
operator fun Subject<Boolean>.not(): Subject<Boolean>
    = map(read = { !it }, write = { !it })

/**
 * Returns a Subject of type String whose value comes from the toString function on the value of this.
 * Writing to the new subject will attempt to convert it to an Int and write it to this. If the convert fails
 * it will not write.
 */
@JvmName("toSubjectStringFromInt")
fun Subject<Int>.toSubjectString(): Subject<String> {
    return mapMaybeWrite(
        read = { it.toString() },
        write = { it.toIntOrNull() }
    )
}

/**
 * Returns a Subject of type String whose value comes from the toString function on the value of this.
 * Writing to the new subject will attempt to convert it to a Double and write it to this. If the convert fails
 * it will not write.
 */
@JvmName("toSubjectStringFromDouble")
fun Subject<Double>.toSubjectString(): Subject<String> {
    return mapMaybeWrite(
        read = { it.toString() },
        write = { it.toDoubleOrNull() }
    )
}

/**
 * Returns a Subject of type String whose value comes from the toString function on the value of this.
 * Writing to the new subject will attempt to convert it to an Int and write it to this. If the convert fails
 * it will return an empty Optional.
 */
@JvmName("toSubjectStringFromOptionalInt")
fun Subject<Optional<Int>>.toSubjectString(): Subject<String> {
    return map(
        read = { it.kotlin?.toString() ?: "" },
        write = { it.toIntOrNull().optional }
    )
}

/**
 * Returns a Subject of type String whose value comes from the toString function on the value of this.
 * Writing to the new subject will attempt to convert it to a Double and write it to this. If the convert fails
 * it will return an empty Optional.
 */
@JvmName("toSubjectStringFromOptionalDouble")
fun Subject<Optional<Double>>.toSubjectString(): Subject<String> {
    return map(
        read = { it.kotlin?.toString() ?: "" },
        write = { it.toDoubleOrNull().optional }
    )
}

/**
 * Returns a Subject of type Int mapped from the value of this.
 */
fun Subject<Float>.toSubjectInt(): Subject<Int> {
    return map(
        read = { it.toInt() },
        write = { it.toFloat() }
    )
}

/**
 * Returns a Subject of type Float Int mapped from the value of this.
 */
fun Subject<Int>.toSubjectFloat(): Subject<Float> {
    return map(
        read = { it.toFloat() },
        write = { it.toInt() }
    )
}

/**
 * Returns a HasValueSubject of type LocalDate mapped from the value of this.
 */
fun HasValueSubject<ZonedDateTime>.toSubjectLocalDate(): HasValueSubject<LocalDate> {
    return mapWithExisting(
        read = { it.toLocalDate() },
        write = { existing, it -> existing.with(it) }
    )
}

/**
 * Returns a HasValueSubject of type LocalTime mapped from the value of this.
 */
fun HasValueSubject<ZonedDateTime>.toSubjectLocalTime(): HasValueSubject<LocalTime> {
    return mapWithExisting(
        read = { it.toLocalTime() },
        write = { existing, it -> existing.with(it) }
    )
}

/**
 * Returns a HasValueSubject of type LocalDate mapped from the value of this.
 */
@JvmName("Instant_toSubjectLocalDate")
fun HasValueSubject<Instant>.toSubjectLocalDate(): HasValueSubject<LocalDate> {
    return mapWithExisting(
        read = { it.atZone(ZoneId.systemDefault()).toLocalDate() },
        write = { existing, it -> existing.atZone(ZoneId.systemDefault()).with(it).toInstant() }
    )
}

/**
 * Returns a HasValueSubject of type LocalTime mapped from the value of this.
 */
@JvmName("Instant_toSubjectLocalTime")
fun HasValueSubject<Instant>.toSubjectLocalTime(): HasValueSubject<LocalTime> {
    return mapWithExisting(
        read = { it.atZone(ZoneId.systemDefault()).toLocalTime() },
        write = { existing, it -> existing.atZone(ZoneId.systemDefault()).with(it).toInstant() }
    )
}

/**
 * Returns a Subject of type Int whose value is the value of this plus the provided amount
 */
operator fun Subject<Int>.plus(amount: Int): Subject<Int> {
    return map(
        read = { it + amount },
        write = { it - amount }
    )
}

/**
 * Returns a Subject of type Int whose value is the value of this multiplied by the provided value
 */
operator fun Subject<Int>.times(amount: Int): Subject<Int> {
    return map(
        read = { it * amount },
        write = { it / amount }
    )
}

/**
 * Returns a Subject of type Float whose value is the value of this plus by the provided amount
 */
operator fun Subject<Float>.plus(amount: Float): Subject<Float> {
    return map(
        read = { it + amount },
        write = { it - amount }
    )
}

/**
 * Returns a Subject of type Float whose value is the value of this multiplied by the provided value
 */
operator fun Subject<Float>.times(amount: Float): Subject<Float> {
    return map(
        read = { it * amount },
        write = { it / amount }
    )
}

/**
 * Returns an Observable of type B with a mapper that can fail and returns null. On failures the return Observable is empty.
 */
infix fun <T: Any, B: Any> Observable<T>.mapNotNull(mapper: (T)->B?): Observable<B>
        = this.switchMap { mapper(it)?.let{ Observable.just(it) } ?: Observable.empty() }

/**
 * A map wrapper that turns an Observable<Optional<T>> into an Observable<B>.
 * <p>
 * An extension function on observables of type Optional<T> which wraps the regular map.
 * This handles unwrapping the optional before passing it to the mapper.
 */
infix fun <T: Any, B: Any> Observable<Optional<T>>.mapFromNullable(mapper: (T?)->B): Observable<B>
        = this.map { it.kotlin.let(mapper) }

/**
 * A map wrapper that turns an Observable<Optional<T>> into an Observable<Optional<B>>.
 * <p>
 * An extension function on observables of type Optional<T> which wraps the regular map.
 * This handles unwrapping the optional before passing it to the mapper, then wraps the results in an Optional.
 */
infix fun <T: Any, B: Any> Observable<Optional<T>>.mapNullable(mapper: (T?)->B?): Observable<Optional<B>>
        = this.map { it.kotlin.let(mapper).optional }

/**
 * A map wrapper that turns an Observable<T> into an Observable<Optional<B>>.
 * <p>
 * An extension function on observables of type T which wraps the regular map.
 * This handles wrapping the results of the mapper into an Optional.
 */
infix fun <T: Any, B: Any> Observable<T>.mapToNullable(mapper: (T)->B?): Observable<Optional<B>>
        = this.map { it.let(mapper).optional }

/**
 * A flatMap wrapper that turns an Observable<Optional<T>> into an Observable<Optional<B>>.
 * <p>
 * An extension function on observables of type Optional<T> which wraps the regular flatMap.
 * This handles unwrapping the Optional before the mapper is called, then handles wrapping the results of the mapper into an Optional.
 */
infix fun <T: Any, B: Any> Observable<Optional<T>>.flatMapNotNull(mapper: (T)->Observable<B>?): Observable<Optional<B>>
        = this.flatMap { it.kotlin?.let(mapper)?.map { it.optional } ?: Observable.just(Optional.empty<B>()) }

/**
 * A switchMap wrapper that turns an Observable<Optional<T>> into an Observable<Optional<B>>.
 * <p>
 * An extension function on observables of type Optional<T> which wraps the regular switchMap.
 * This handles unwrapping the Optional before the mapper is called, then handles wrapping the results of the mapper into an Optional.
 */
infix fun <T: Any, B: Any> Observable<Optional<T>>.switchMapNotNull(mapper: (T)->Observable<B>?): Observable<Optional<B>>
        = this.switchMap { it.kotlin?.let(mapper)?.map { it.optional } ?: Observable.just(Optional.empty<B>()) }

/**
 * A flatMap wrapper that turns an Observable<Optional<T>> into an Observable<Optional<B>>.
 * <p>
 * An extension function on observables of type Optional<T> which wraps the regular flatMap.
 * This handles unwrapping the Optional before the mapper is called.
 */
@JvmName("flatMapNotNull2")
infix fun <T: Any, B: Any> Observable<Optional<T>>.flatMapNotNull(mapper: (T)->Observable<Optional<B>>?): Observable<Optional<B>>
        = this.flatMap { it.kotlin?.let(mapper) ?: Observable.just(Optional.empty<B>()) }

/**
 * A switchMap wrapper that turns an Observable<Optional<T>> into an Observable<Optional<B>>.
 * <p>
 * An extension function on observables of type Optional<T> which wraps the regular switchMap.
 * This handles unwrapping the Optional before the mapper is called.
 */
@JvmName("switchMapNotNull2")
infix fun <T: Any, B: Any> Observable<Optional<T>>.switchMapNotNull(mapper: (T)->Observable<Optional<B>>?): Observable<Optional<B>>
        = this.switchMap { it.kotlin?.let(mapper) ?: Observable.just(Optional.empty<B>()) }

/**
 * Convenience for Observable.combineLatest that handles the output typing better.
 */
fun <Element : Any, R : Any, OUT : Any> Observable<Element>.combineLatest(
    observable: Observable<R>,
    function: (Element, R) -> OUT
): Observable<OUT> = Observable.combineLatest(this, observable, function)

/**
 * Convenience for Observable.combineLatest that handles the output typing better.
 */
@Suppress("UNCHECKED_CAST")
fun <IN : Any, OUT : Any> List<Observable<IN>>.combineLatest(combine: (List<IN>) -> OUT): Observable<OUT> =
    Observable.combineLatest(this) { stupidArray: Array<Any?> ->
        combine(stupidArray.toList() as List<IN>)
    }

/**
 * Convenience for Observable.combineLatest that handles the output typing better.
 */
@Suppress("UNCHECKED_CAST")
fun <IN : Any> List<Observable<IN>>.combineLatest(): Observable<List<IN>> =
    Observable.combineLatest(this) { stupidArray: Array<Any?> -> stupidArray.toList() as List<IN> }

/**
 * Convenience for Single.zip that handles the output typing better.
 */
@Suppress("UNCHECKED_CAST")
fun <IN : Any> List<Single<IN>>.zip(): Single<List<IN>> =
    Single.zip(this) { stupidArray: Array<Any?> -> stupidArray.toList() as List<IN> }

/**
 * Takes a Subject of type Boolean and on subscription of this, it will pass true into the onNext
 * of the Subject. After this completes it will pass false into the Subject
 */
fun <Element : Any> Single<Element>.working(property: Subject<Boolean>): Single<Element> {
    return this
        .doOnSubscribe { property.onNext(true) }
        .doFinally { property.onNext(false) }
}

/**
 * Takes a Subject of type Boolean and on subscription of this, it will pass true into the onNext
 * of the Subject. After this completes it will pass false into the Subject
 */
fun <Element : Any> Maybe<Element>.working(property: Subject<Boolean>): Maybe<Element> {
    return this
        .doOnSubscribe { property.onNext(true) }
        .doFinally { property.onNext(false) }
}
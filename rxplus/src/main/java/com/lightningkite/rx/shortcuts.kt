package com.lightningkite.rx

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.doOnAfterFinally
import com.badoo.reaktive.completable.doOnAfterSubscribe
import com.badoo.reaktive.completable.doOnBeforeFinally
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.doOnAfterSubscribe
import com.badoo.reaktive.maybe.doOnBeforeFinally
import com.badoo.reaktive.maybe.maybeFromFunction
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.single.*
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import java.time.*
import kotlin.reflect.KMutableProperty1




/**
 * Returns an Observable of type Boolean whose value is the equality check between the
 * two different values from this and the other observable.
 */
infix fun <T> Observable<T>.isEqualTo(other: Observable<T>): Observable<Boolean> =
    combineLatest(this, other) { left, right -> left == right }


/**
 * Returns an Observable of type Boolean whose value is the negative equality check between the
 * two different values from this and the other observable.
 */
infix fun <T> Observable<T>.notEqualTo(other: Observable<T>): Observable<Boolean> =
    combineLatest(this, other) { left, right -> left != right }


/**
 * Returns a Subject of type Boolean created from comparing this to the constant.
 * <p>
 * Returns a Subject of type Boolean whose value is the equality check between
 * the value of this and the constant provided. If the new Subject has true written to it,
 * the constant will be passed into the on next of this.
 */
infix fun <T> Subject<T>.isEqualTo(constant: T): Subject<Boolean> =
    this.map { it == constant }.withWrite { if (it) onNext(constant) }

/**
 * Returns a Subject of type Boolean created from comparing this to the constant.
 * <p>
 * Returns a Subject of type Boolean whose value is the negative equality check between
 * the value of this and the constant provided. If the new Subject has false written to it,
 * the constant will be passed into the on next of this.
 */
infix fun <T> Subject<T>.notEqualTo(constant: T): Subject<Boolean> =
    this.map { it != constant }.withWrite { if (!it) onNext(constant) }



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
operator fun <T, V> BehaviorSubject<T>.get(property: KMutableProperty1<T, V>): BehaviorSubject<V> =
    mapWithExisting(
        read = { property.get(it) },
        write = { existing, it -> property.set(existing, it); existing }
    )

/**
 * Returns an Observable of type Boolean created from checking the collection for this value.
 * <p>
 * Returns an Observable of type Boolean created from doing a check on the collection provided from
 * the other observable. If this value is contained by the collection it returns true, otherwise false.
 */
infix fun <T> Observable<T>.isIn(other: Observable<Collection<T>>): Observable<Boolean> =
    combineLatest(this, other) { left, right -> left in right }

/**
 * Returns an Observable of type Boolean created from checking the collection for this value.
 * <p>
 * Returns an Observable of type Boolean created from doing a check on the collection provided from
 * the other observable. If this value is NOT contained by the collection it returns true, otherwise false.
 */
infix fun <T> Observable<T>.notIn(other: Observable<Collection<T>>): Observable<Boolean> =
    combineLatest(this, other) { left, right -> left !in right }

/**
 * Returns a Subject of type Boolean created from checking the collection for the constant.
 * <p>
 * Returns a Subject of type Boolean created from doing a check on this collection for the provided constant.
 * If this value is contained by the collection it returns true, otherwise false. If true is written to the
 * new subject the constant is added to the collection, then the new collection passed into onNext. If false
 * is written to the new Subject the value is removed from the collection, then the new collection passed into onNext.
 */
infix fun <T> BehaviorSubject<List<T>>.contains(constant: T): Subject<Boolean> =
    this.map { it.contains(constant) }.withWrite {
        if (it) onNext(value + constant)
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
infix fun <T> BehaviorSubject<List<T>>.doesNotContain(constant: T): Subject<Boolean> =
    this.map { !it.contains(constant) }.withWrite {
        if (it) onNext(value - constant)
        else onNext(value + constant)
    }

/**
 * Returns a Subject of type Boolean created from checking the collection for the constant.
 * <p>
 * Returns a Subject of type Boolean created from doing a check on this collection for the provided constant.
 * If this value is contained by the collection it returns true, otherwise false. If true is written to the
 * new subject the constant is added to the collection, then the new collection passed into onNext. If false
 * is written to the new Subject the value is removed from the collection, then the new collection passed into onNext.
 */
@JvmName("setContains")
infix fun <T> BehaviorSubject<Set<T>>.contains(constant: T): Subject<Boolean> =
    this.map { it.contains(constant) }.withWrite {
        if (it) onNext(value + constant)
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
@JvmName("setDoesNotContain")
infix fun <T> BehaviorSubject<Set<T>>.doesNotContain(constant: T): Subject<Boolean> =
    this.map { !it.contains(constant) }.withWrite {
        if (it) onNext(value - constant)
        else onNext(value + constant)
    }

/**
 * Returns an Observable of type Boolean whose value is the and of each incoming observables.
 */
infix fun Observable<Boolean>.and(other: Observable<Boolean>): Observable<Boolean> =
    combineLatest(this, other) { left, right -> left && right }

/**
 * Returns an Observable of type Boolean whose value is the or of each incoming observables.
 */
infix fun Observable<Boolean>.or(other: Observable<Boolean>): Observable<Boolean> =
    combineLatest(this, other) { left, right -> left || right }

/**
 * Returns a Subject of type Boolean whose value is opposite of the value of this.
 */
operator fun Subject<Boolean>.not(): Subject<Boolean> = map(read = { !it }, write = { !it })

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
fun Subject<Int?>.toSubjectString(): Subject<String> {
    return map(
        read = { it?.toString() ?: "" },
        write = { it.toIntOrNull() }
    )
}

/**
 * Returns a Subject of type String whose value comes from the toString function on the value of this.
 * Writing to the new subject will attempt to convert it to a Double and write it to this. If the convert fails
 * it will return an empty Optional.
 */
@JvmName("toSubjectStringFromOptionalDouble")
fun Subject<Double?>.toSubjectString(): Subject<String> {
    return map(
        read = { it?.toString() ?: "" },
        write = { it.toDoubleOrNull() }
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
fun BehaviorSubject<ZonedDateTime>.toSubjectLocalDate(): BehaviorSubject<LocalDate> {
    return mapWithExisting(
        read = { it.toLocalDate() },
        write = { existing, it -> existing.with(it) }
    )
}

/**
 * Returns a HasValueSubject of type LocalTime mapped from the value of this.
 */
fun BehaviorSubject<ZonedDateTime>.toSubjectLocalTime(): BehaviorSubject<LocalTime> {
    return mapWithExisting(
        read = { it.toLocalTime() },
        write = { existing, it -> existing.with(it) }
    )
}

/**
 * Returns a HasValueSubject of type LocalDate mapped from the value of this.
 */
@JvmName("Instant_toSubjectLocalDate")
fun BehaviorSubject<Instant>.toSubjectLocalDate(): BehaviorSubject<LocalDate> {
    return mapWithExisting(
        read = { it.atZone(ZoneId.systemDefault()).toLocalDate() },
        write = { existing, it -> existing.atZone(ZoneId.systemDefault()).with(it).toInstant() }
    )
}

/**
 * Returns a HasValueSubject of type LocalTime mapped from the value of this.
 */
@JvmName("Instant_toSubjectLocalTime")
fun BehaviorSubject<Instant>.toSubjectLocalTime(): BehaviorSubject<LocalTime> {
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
 * Returns a Subject of type Int whose value is the value of this plus the provided amount
 */
operator fun Subject<Int>.minus(amount: Int): Subject<Int> {
    return map(
        read = { it - amount },
        write = { it + amount }
    )
}

/**
 * Returns a Subject of type Int whose value is the value of this multiplied by the provided value
 */
operator fun Subject<Int>.div(amount: Int): Subject<Int> {
    return map(
        read = { it / amount },
        write = { it * amount }
    )
}

/**
 * Returns a Subject of type Float whose value is the value of this plus by the provided amount
 */
operator fun Subject<Float>.minus(amount: Float): Subject<Float> {
    return map(
        read = { it - amount },
        write = { it + amount }
    )
}

/**
 * Returns a Subject of type Float whose value is the value of this multiplied by the provided value
 */
operator fun Subject<Float>.div(amount: Float): Subject<Float> {
    return map(
        read = { it / amount },
        write = { it * amount }
    )
}


/**
 * Convenience for Observable.combineLatest that handles the output typing better.
 */
fun <Element, R, OUT> Observable<Element>.combineLatest(
    observable: Observable<R>,
    function: (Element, R) -> OUT
): Observable<OUT> = combineLatest(this, observable, function)

/**
 * Convenience for Observable.combineLatest that handles the output typing better.
 */
fun <IN, OUT> List<Observable<IN>>.combineLatest(combine: (List<IN>) -> OUT): Observable<OUT> =
    combineLatest<IN, OUT>(sources = this.toTypedArray(), mapper = combine)

/**
 * Convenience for Observable.combineLatest that handles the output typing better.
 */
fun <IN> List<Observable<IN>>.combineLatest(): Observable<List<IN>> = combineLatest { it }

/**
 * Convenience for Single.zip that handles the output typing better.
 */
fun <IN> List<Single<IN>>.zip(): Single<List<IN>> =
    zip { it }

/**
 * Takes a Subject of type Boolean and on subscription of this, it will pass true into the onNext
 * of the Subject. After this completes it will pass false into the Subject
 */
fun Completable.working(property: Subject<Boolean>): Completable {
    return this
        .doOnAfterSubscribe { property.onNext(true) }
        .doOnBeforeFinally { property.onNext(false) }
}

/**
 * Takes a Subject of type Boolean and on subscription of this, it will pass true into the onNext
 * of the Subject. After this completes it will pass false into the Subject
 */
fun <Element> Single<Element>.working(property: Subject<Boolean>): Single<Element> {
    return this
        .doOnAfterSubscribe { property.onNext(true) }
        .doOnBeforeFinally { property.onNext(false) }
}

/**
 * Takes a Subject of type Boolean and on subscription of this, it will pass true into the onNext
 * of the Subject. After this completes it will pass false into the Subject
 */
fun <Element> Maybe<Element>.working(property: Subject<Boolean>): Maybe<Element> {
    return this
        .doOnAfterSubscribe { property.onNext(true) }
        .doOnBeforeFinally { property.onNext(false) }
}

/**
 * Subscribes only to the observable while [shouldListen]'s most recent value is true.
 */
fun <Element> Observable<Element>.onlyWhile(shouldListen: Observable<Boolean>): Observable<Element> {
    return shouldListen.switchMap {
        if(it) this
        else observableOfNever()
    }
}

/**
 * Subscribes only to the observable while [shouldListen]'s most recent value is true.
 * If it's false, emits [downtimeValue] and waits.
 */
fun <Element> Observable<Element>.onlyWhile(shouldListen: Observable<Boolean>, downtimeValue: Element): Observable<Element> {
    return shouldListen.switchMap {
        if(it) this
        else concat(observableOf(downtimeValue), observableOfNever())
    }
}

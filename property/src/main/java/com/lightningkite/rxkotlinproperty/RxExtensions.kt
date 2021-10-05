package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction


/**
 * Combine latest operator
 */
fun <Element : Any, R : Any, OUT : Any> Observable<Element>.combineLatest(
    observable: Observable<R>,
    function: (Element, R) -> OUT
): Observable<OUT> = Observable.combineLatest(this, observable, BiFunction(function))

fun <IN : Any, OUT : Any> List<Observable<IN>>.combineLatest(combine: (List<IN>) -> OUT): Observable<OUT> =
    Observable.combineLatest(this) { stupidArray: Array<Any?> ->
        combine(stupidArray.toList() as List<IN>)
    }

fun <IN : Any> List<Observable<IN>>.combineLatest(): Observable<List<IN>> =
    Observable.combineLatest(this) { stupidArray: Array<Any?> -> stupidArray.toList() as List<IN> }

fun <IN : Any> List<Single<IN>>.zip(): Single<List<IN>> =
    Single.zip(this) { stupidArray: Array<Any?> -> stupidArray.toList() as List<IN> }

@JvmName("filterNotNullB")
fun <Element> Observable<Box<Element>>.filterNotNull(): Observable<Element> =
    this.filter { it.value != null }.map { it.value }

@JvmName("filterNotNullA")
fun <Element : Any> Observable<Box<Element?>>.filterNotNull(): Observable<Element> =
    this.filter { it.value != null }.map { it.value }

fun <Element, Destination : Any> Observable<Element>.mapNotNull(transform: (Element) -> Destination?): Observable<Destination> =
    this.flatMap {
        val result = transform(it)
        if (result == null)
            Observable.empty()
        else
            Observable.just(result)
    }

fun <Element : Any> Single<Element>.working(property: MutableProperty<Boolean>): Single<Element> {
    return this
        .doOnSubscribe { it -> property.value = true }
        .doFinally { property.value = false }
}

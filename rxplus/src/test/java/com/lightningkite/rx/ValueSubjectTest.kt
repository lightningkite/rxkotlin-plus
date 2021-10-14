package com.lightningkite.rx

import com.lightningkite.rx.ValueSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.internal.util.ExceptionHelper
import org.mockito.Mockito
import org.mockito.ArgumentMatchers
import io.reactivex.rxjava3.observers.TestObserver
import org.mockito.InOrder
import io.reactivex.rxjava3.observers.DefaultObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.RuntimeException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/*
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
class ValueSubjectTest {
    private val testException = Throwable()

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> mockObserver(): Observer<T> = mock(Observer::class.java) as Observer<T>

    @Test
    fun thatSubscriberReceivesDefaultValueAndSubsequentEvents() {
        val subject: ValueSubject<String> = ValueSubject("default")
        val observer: Observer<String> = mockObserver()
        subject.subscribe(observer)
        subject.onNext("one")
        subject.onNext("two")
        subject.onNext("three")
        Mockito.verify(observer, Mockito.times(1)).onNext("default")
        Mockito.verify(observer, Mockito.times(1)).onNext("one")
        Mockito.verify(observer, Mockito.times(1)).onNext("two")
        Mockito.verify(observer, Mockito.times(1)).onNext("three")
        Mockito.verify(observer, Mockito.never()).onError(testException)
        Mockito.verify(observer, Mockito.never()).onComplete()
    }

    @Test
    fun thatSubscriberReceivesLatestAndThenSubsequentEvents() {
        val subject: ValueSubject<String> = ValueSubject("default")
        subject.onNext("one")
        val observer: Observer<String> = mockObserver()
        subject.subscribe(observer)
        subject.onNext("two")
        subject.onNext("three")
        Mockito.verify(observer, Mockito.never()).onNext("default")
        Mockito.verify(observer, Mockito.times(1)).onNext("one")
        Mockito.verify(observer, Mockito.times(1)).onNext("two")
        Mockito.verify(observer, Mockito.times(1)).onNext("three")
        Mockito.verify(observer, Mockito.never()).onError(testException)
        Mockito.verify(observer, Mockito.never()).onComplete()
    }

    @Test
    fun subscribeThenOnComplete() {
        val subject: ValueSubject<String> = ValueSubject("default")
        val observer: Observer<String> = mockObserver()
        subject.subscribe(observer)
        subject.onNext("one")
        subject.onComplete()
        Mockito.verify(observer, Mockito.times(1)).onNext("default")
        Mockito.verify(observer, Mockito.times(1)).onNext("one")
        Mockito.verify(observer, Mockito.never()).onError(
            ArgumentMatchers.any(
                Throwable::class.java
            )
        )
        Mockito.verify(observer, Mockito.times(1)).onComplete()
    }

    @Test
    fun subscribeToCompletedOnlyEmitsOnComplete() {
        val subject: ValueSubject<String> = ValueSubject("default")
        subject.onNext("one")
        subject.onComplete()
        val observer: Observer<String> = mockObserver()
        subject.subscribe(observer)
        Mockito.verify(observer, Mockito.never()).onNext("default")
        Mockito.verify(observer, Mockito.never()).onNext("one")
        Mockito.verify(observer, Mockito.never()).onError(
            ArgumentMatchers.any(
                Throwable::class.java
            )
        )
        Mockito.verify(observer, Mockito.times(1)).onComplete()
    }

    @Test
    fun subscribeToErrorOnlyEmitsOnError() {
        val subject: ValueSubject<String> = ValueSubject("default")
        subject.onNext("one")
        val re = RuntimeException("test error")
        subject.onError(re)
        val observer: Observer<String> = mockObserver()
        subject.subscribe(observer)
        Mockito.verify(observer, Mockito.never()).onNext("default")
        Mockito.verify(observer, Mockito.never()).onNext("one")
        Mockito.verify(observer, Mockito.times(1)).onError(re)
        Mockito.verify(observer, Mockito.never()).onComplete()
    }

    @Test
    fun completedStopsEmittingData() {
        val channel: ValueSubject<Int> = ValueSubject(2013)
        val observerA: Observer<Any> = mockObserver()
        val observerB: Observer<Any> = mockObserver()
        val observerC: Observer<Any> = mockObserver()
        val to = TestObserver(observerA)
        channel.subscribe(to)
        channel.subscribe(observerB)
        val inOrderA = Mockito.inOrder(observerA)
        val inOrderB = Mockito.inOrder(observerB)
        val inOrderC = Mockito.inOrder(observerC)
        inOrderA.verify(observerA).onNext(2013)
        inOrderB.verify(observerB).onNext(2013)
        channel.onNext(42)
        inOrderA.verify(observerA).onNext(42)
        inOrderB.verify(observerB).onNext(42)
        to.dispose()
        inOrderA.verifyNoMoreInteractions()
        channel.onNext(4711)
        inOrderB.verify(observerB).onNext(4711)
        channel.onComplete()
        inOrderB.verify(observerB).onComplete()
        channel.subscribe(observerC)
        inOrderC.verify(observerC).onComplete()
        channel.onNext(13)
        inOrderB.verifyNoMoreInteractions()
        inOrderC.verifyNoMoreInteractions()
    }

    @Test
    fun completedAfterErrorIsNotSent() {
        val subject: ValueSubject<String> = ValueSubject("default")
        val observer: Observer<String> = mockObserver()
        subject.subscribe(observer)
        subject.onNext("one")
        subject.onError(testException)
        subject.onNext("two")
        subject.onComplete()
        Mockito.verify(observer, Mockito.times(1)).onNext("default")
        Mockito.verify(observer, Mockito.times(1)).onNext("one")
        Mockito.verify(observer, Mockito.times(1)).onError(testException)
        Mockito.verify(observer, Mockito.never()).onNext("two")
        Mockito.verify(observer, Mockito.never()).onComplete()
    }

    @Test
    fun completedAfterErrorIsNotSent2() {
        val subject: ValueSubject<String> = ValueSubject("default")
        val observer: Observer<String> = mockObserver()
        subject.subscribe(observer)
        subject.onNext("one")
        subject.onError(testException)
        subject.onNext("two")
        subject.onComplete()
        Mockito.verify(observer, Mockito.times(1)).onNext("default")
        Mockito.verify(observer, Mockito.times(1)).onNext("one")
        Mockito.verify(observer, Mockito.times(1)).onError(testException)
        Mockito.verify(observer, Mockito.never()).onNext("two")
        Mockito.verify(observer, Mockito.never()).onComplete()
        val o2: Observer<Any> = mockObserver()
        subject.subscribe(o2)
        Mockito.verify(o2, Mockito.times(1)).onError(testException)
        Mockito.verify(o2, Mockito.never()).onNext(ArgumentMatchers.any())
        Mockito.verify(o2, Mockito.never()).onComplete()
    }

    @Test
    fun completedAfterErrorIsNotSent3() {
        val subject: ValueSubject<String> = ValueSubject("default")
        val observer: Observer<String> = mockObserver()
        subject.subscribe(observer)
        subject.onNext("one")
        subject.onComplete()
        subject.onNext("two")
        subject.onComplete()
        Mockito.verify(observer, Mockito.times(1)).onNext("default")
        Mockito.verify(observer, Mockito.times(1)).onNext("one")
        Mockito.verify(observer, Mockito.times(1)).onComplete()
        Mockito.verify(observer, Mockito.never()).onError(
            ArgumentMatchers.any(
                Throwable::class.java
            )
        )
        Mockito.verify(observer, Mockito.never()).onNext("two")
        val o2: Observer<Any> = mockObserver()
        subject.subscribe(o2)
        Mockito.verify(o2, Mockito.times(1)).onComplete()
        Mockito.verify(o2, Mockito.never()).onNext(ArgumentMatchers.any())
        Mockito.verify(observer, Mockito.never()).onError(
            ArgumentMatchers.any(
                Throwable::class.java
            )
        )
    }

    @Test
    fun unsubscriptionCase() {
        val src: ValueSubject<String> = ValueSubject("null") // FIXME was plain null which is not allowed
        for (i in 0..9) {
            val o: Observer<Any> = mockObserver()
            val inOrder = Mockito.inOrder(o)
            val v = "" + i
            src.onNext(v)
            System.out.printf("Turn: %d%n", i)
            src.firstElement()
                .toObservable()
                .flatMap { t1 ->
                    Observable.just("$t1, $t1")
                }
                .subscribe(object : DefaultObserver<String>() {
                    override fun onNext(t: String) {
                        o.onNext(t)
                    }

                    override fun onError(e: Throwable) {
                        o.onError(e)
                    }

                    override fun onComplete() {
                        o.onComplete()
                    }
                })
            inOrder.verify(o).onNext("$v, $v")
            inOrder.verify(o).onComplete()
            Mockito.verify(o, Mockito.never()).onError(
                ArgumentMatchers.any(
                    Throwable::class.java
                )
            )
        }
    }


//    @Test
//    @Throws(Exception::class)
//    fun emissionSubscriptionRace() {
//        val s = Schedulers.io()
//        val worker = Schedulers.io().createWorker()
//        try {
//            for (i in 0..49999) {
//                if (i % 1000 == 0) {
//                    println(i)
//                }
//                val rs: ValueSubject<Any> = ValueSubject<Any>(false)
//                val finish = CountDownLatch(1)
//                val start = CountDownLatch(1)
//                worker.schedule {
//                    try {
//                        start.await()
//                    } catch (e1: Exception) {
//                        e1.printStackTrace()
//                    }
//                    rs.onNext(1)
//                }
//                val o = AtomicReference<Any>()
//                rs.subscribeOn(s).observeOn(Schedulers.io())
//                    .subscribe(object : DefaultObserver<Any>() {
//                        override fun onComplete() {
//                            o.set(-1)
//                            finish.countDown()
//                        }
//
//                        override fun onError(e: Throwable) {
//                            o.set(e)
//                            finish.countDown()
//                        }
//
//                        override fun onNext(t: Any) {
//                            o.set(t)
//                            finish.countDown()
//                        }
//                    })
//                start.countDown()
//                if (!finish.await(5, TimeUnit.SECONDS)) {
//                    println(o.get())
//                    println(rs.hasObservers())
//                    rs.onComplete()
//                    Assert.fail("Timeout @ $i")
//                    break
//                } else {
//                    Assert.assertEquals(1, o.get())
//                    worker.schedule { rs.onComplete() }
//                }
//            }
//        } finally {
//            worker.dispose()
//        }
//    }

    @Test
    fun currentStateMethodsNormalSomeStart() {
        val `as`: ValueSubject<Any> = ValueSubject(1 as Any)
        Assert.assertFalse(`as`.hasThrowable())
        Assert.assertFalse(`as`.hasComplete())
        Assert.assertEquals(1, `as`.value)
        Assert.assertNull(`as`.throwable)
        `as`.onNext(2)
        Assert.assertFalse(`as`.hasThrowable())
        Assert.assertFalse(`as`.hasComplete())
        Assert.assertEquals(2, `as`.value)
        Assert.assertNull(`as`.throwable)
        `as`.onComplete()
    }

    @Test
    fun cancelOnArrival() {
        val p: ValueSubject<Any> = ValueSubject<Any>(false)
        Assert.assertFalse(p.hasObservers())
        p.test(true).assertEmpty()
        Assert.assertFalse(p.hasObservers())
    }

    @Test
    fun onSubscribe() {
        val p: ValueSubject<Any> = ValueSubject<Any>(false)
        var bs = Disposable.empty()
        p.onSubscribe(bs)
        Assert.assertFalse(bs.isDisposed)
        p.onComplete()
        bs = Disposable.empty()
        p.onSubscribe(bs)
        Assert.assertTrue(bs.isDisposed)
    }

    @Test
    fun onErrorAfterComplete() {
        val p: ValueSubject<Any> = ValueSubject<Any>(false)
        p.onComplete()
        val errors: List<Throwable> = TestHelper.trackPluginErrors()
        try {
            p.onError(TestException())
            TestHelper.assertUndeliverable(errors, 0, TestException::class.java)
        } finally {
            RxJavaPlugins.reset()
        }
    }

    @Test
    fun cancelOnArrival2() {
        val p: ValueSubject<Any> = ValueSubject<Any>(0)
        val to = p.test()
        p.test(true).assertEmpty()
        p.onNext(1)
        p.onComplete()
        to.assertResult(0, 1)
    }

    @Test
    fun addRemoveRace() {
        for (i in 0 until TestHelper.RACE_DEFAULT_LOOPS) {
            val p: ValueSubject<Any> = ValueSubject<Any>(0)
            val to = p.test()
            val r1 = Runnable { p.test() }
            val r2 = Runnable { to.dispose() }
            TestHelper.race(r1, r2)
        }
    }

    @Test
    fun subscribeOnNextRace() {
        for (i in 0 until TestHelper.RACE_DEFAULT_LOOPS) {
            val p: ValueSubject<Any> = ValueSubject(1 as Any)
            val to = arrayOf<TestObserver<Any>?>(null)
            val r1 = Runnable { to[0] = p.test() }
            val r2 = Runnable { p.onNext(2) }
            TestHelper.race(r1, r2)
            if (to[0]!!.values().size == 1) {
                to[0]!!.assertValue(2).assertNoErrors().assertNotComplete()
            } else {
                to[0]!!.assertValues(1, 2).assertNoErrors().assertNotComplete()
            }
        }
    }

    @Test
    fun innerDisposed() {
        ValueSubject<Any>(false)
            .subscribe(object : Observer<Any> {
                override fun onSubscribe(d: Disposable) {
                    Assert.assertFalse(d.isDisposed)
                    d.dispose()
                    Assert.assertTrue(d.isDisposed)
                }

                override fun onNext(value: Any) {}
                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

//    @Test
//    @Throws(Exception::class)
//    fun completeSubscribeRace() {
//        for (i in 0 until TestHelper.RACE_DEFAULT_LOOPS) {
//            val p: ValueSubject<Any> = ValueSubject<Any>(false)
//            val to = TestObserver<Any>()
//            val r1 = Runnable { p.subscribe(to) }
//            val r2 = Runnable { p.onComplete() }
//            TestHelper.race(r1, r2)
//            to.assertResult(false)
//        }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun errorSubscribeRace() {
//        for (i in 0 until TestHelper.RACE_DEFAULT_LOOPS) {
//            val p: ValueSubject<Any> = ValueSubject<Any>(false)
//            val to = TestObserver<Any>()
//            val ex = TestException()
//            val r1 = Runnable { p.subscribe(to) }
//            val r2 = Runnable { p.onError(ex) }
//            TestHelper.race(r1, r2)
//            to.assertFailure(TestException::class.java, false)
//        }
//    }

    @Test
    fun hasObservers() {
        val bs: ValueSubject<Int> = ValueSubject<Int>(0)
        Assert.assertFalse(bs.hasObservers())
        val to = bs.test()
        Assert.assertTrue(bs.hasObservers())
        to.dispose()
        Assert.assertFalse(bs.hasObservers())
    }
}
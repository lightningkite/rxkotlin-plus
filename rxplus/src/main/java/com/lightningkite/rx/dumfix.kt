//package com.lightningkite.rx
//
//import com.badoo.reaktive.disposable.Disposable
//import com.badoo.reaktive.observable.ObservableObserver
//import com.badoo.reaktive.subject.Subject
//import com.badoo.reaktive.subject.isActive
//import com.badoo.reaktive.subject.replay.ReplaySubject
//import java.util.concurrent.atomic.AtomicReference
//
//fun <T> ReplaySubject(bufferSize: Int = Int.MAX_VALUE): ReplaySubject<T> {
//    require(bufferSize > 0) { "Buffer size must be a positive value" }
//
//    return object : DefaultSubject<T>(), ReplaySubject<T> {
//        private val buffer = ArrayQueue<T>()
//
//        override fun onSubscribed(observer: ObservableObserver<T>): Boolean {
//            buffer.forEach(observer::onNext)
//
//            return true
//        }
//
//        override fun onBeforeNext(value: T) {
//            super.onBeforeNext(value)
//
//            if (buffer.size >= bufferSize) {
//                buffer.poll()
//            }
//            buffer.offer(value)
//        }
//    }
//}
//
//internal open class DefaultSubject<T> : Subject<T> {
//
//    private val observers = SharedList<ObservableObserver<T>>(0)
//    private val serializer = serializer(onValue = ::onSerializedValue)
//
//    private val _status = AtomicReference<Subject.Status>(Subject.Status.Active)
//    override var status: Subject.Status
//        get() = _status.value
//        protected set(value) {
//            _status.value = value
//            onStatusChanged(value)
//        }
//
//    override fun subscribe(observer: ObservableObserver<T>) {
//        serializer.accept(Event.OnSubscribe(observer))
//    }
//
//    override fun onNext(value: T) {
//        serializer.accept(value)
//    }
//
//    override fun onComplete() {
//        serializer.accept(Event.OnComplete)
//    }
//
//    override fun onError(error: Throwable) {
//        serializer.accept(Event.OnError(error))
//    }
//
//    protected open fun onSubscribed(observer: ObservableObserver<T>): Boolean = true
//
//    protected open fun onAfterSubscribe(observer: ObservableObserver<T>) {
//    }
//
//    protected open fun onAfterUnsubscribe(observer: ObservableObserver<T>) {
//    }
//
//    protected open fun onBeforeNext(value: T) {
//    }
//
//    protected open fun onStatusChanged(status: Subject.Status) {
//    }
//
//    private fun onSerializedValue(value: Any?): Boolean {
//        if (value is Event<*>) {
//            @Suppress("UNCHECKED_CAST") // Either Event<T> or T, to avoid unnecessary allocations
//            val event = value as Event<T>
//            when (event) {
//                is Event.OnSubscribe -> onSerializedSubscribe(event.observer)
//                is Event.OnUnsubscribe -> onSerializedUnsubscribe(event.observer)
//                is Event.OnComplete -> onSerializedComplete()
//                is Event.OnError -> onSerializedError(event.error)
//            }
//        } else {
//            @Suppress("UNCHECKED_CAST") // Either Event<T> or T, to avoid unnecessary allocations
//            onSerializedNext(value as T)
//        }
//
//        return true
//    }
//
//    private fun onSerializedSubscribe(observer: ObservableObserver<T>) {
//        val disposable = Disposable { serializer.accept(Event.OnUnsubscribe(observer)) }
//
//        observer.onSubscribe(disposable)
//
//        if (disposable.isDisposed) {
//            return
//        }
//
//        if (!onSubscribed(observer)) {
//            return
//        }
//
//        status.also {
//            when (it) {
//                is Subject.Status.Completed -> {
//                    observer.onComplete()
//                    return
//                }
//
//                is Subject.Status.Error -> {
//                    observer.onError(it.error)
//                    return
//                }
//
//                is Subject.Status.Active -> {
//                }
//            }
//        }
//
//        observers += observer
//
//        onAfterSubscribe(observer)
//    }
//
//    private fun onSerializedUnsubscribe(observer: ObservableObserver<T>) {
//        observers -= observer
//        onAfterUnsubscribe(observer)
//    }
//
//    private fun onSerializedNext(value: T) {
//        if (isActive) {
//            onBeforeNext(value)
//            observers.forEach { it.onNext(value) }
//        }
//    }
//
//    private fun onSerializedComplete() {
//        if (isActive) {
//            status = Subject.Status.Completed
//            observers.forEach(ObservableObserver<*>::onComplete)
//        }
//    }
//
//    private fun onSerializedError(error: Throwable) {
//        if (isActive) {
//            status = Subject.Status.Error(error)
//            observers.forEach { it.onError(error) }
//        }
//    }
//
//    private sealed class Event<out T> {
//        class OnSubscribe<T>(val observer: ObservableObserver<T>) : Event<T>()
//        class OnUnsubscribe<T>(val observer: ObservableObserver<T>) : Event<T>()
//        object OnComplete : Event<Nothing>()
//        class OnError(val error: Throwable) : Event<Nothing>()
//    }
//}
//
//internal class SharedList<T> constructor(
//    initialCapacity: Int
//) : java.util.ArrayList<T>(initialCapacity), MutableList<T>
//
//internal class ArrayQueue<T> : Queue<T> {
//
//    private var queue: Array<T?> = createArray(INITIAL_CAPACITY)
//    private var head = 0
//    private var tail = 0
//    private var isFull = false
//    override val peek: T? get() = queue[head]
//    override val isEmpty: Boolean get() = (head == tail) && !isFull
//
//    override val size: Int
//        get() =
//            when {
//                isFull -> queue.size
//                tail >= head -> tail - head
//                else -> queue.size + tail - head
//            }
//
//    override fun offer(item: T) {
//        ensureCapacity()
//        queue[tail] = item
//        tail++
//        if (tail > queue.lastIndex) {
//            tail = 0
//        }
//        if (tail == head) {
//            isFull = true
//        }
//    }
//
//    override fun poll(): T? {
//        val value = peek
//        queue[head] = null
//        if ((head != tail) || isFull) {
//            head++
//            isFull = false
//            if (head > queue.lastIndex) {
//                head = 0
//            }
//        }
//
//        return value
//    }
//
//    override fun clear() {
//        for (i in 0 until queue.size) {
//            queue[i] = null
//        }
//        head = 0
//        tail = 0
//        isFull = false
//    }
//
//    override fun iterator(): Iterator<T> =
//        object : Iterator<T> {
//            private var counter = size
//            private var index = head
//            private val lastIndex = queue.lastIndex
//
//            override fun hasNext(): Boolean = counter > 0
//
//            override fun next(): T {
//                if (!hasNext()) {
//                    throw NoSuchElementException()
//                }
//                @Suppress("UNCHECKED_CAST")
//                val item = queue[index] as T
//
//                counter--
//                index++
//                if (index > lastIndex) {
//                    index = 0
//                }
//
//                return item
//            }
//        }
//
//    private fun ensureCapacity() {
//        if (!isFull) {
//            return
//        }
//
//        isFull = false
//        val arr = createArray<T>(queue.size shl 1)
//        queue.copyInto(arr, 0, head, queue.size)
//        queue.copyInto(arr, queue.size - head, 0, head)
//        tail = queue.size
//        head = 0
//        queue = arr
//    }
//
//    internal companion object {
//        internal const val INITIAL_CAPACITY = 8
//
//        @Suppress("UNCHECKED_CAST")
//        private fun <T> createArray(size: Int): Array<T?> = arrayOfNulls<Any>(size) as Array<T?>
//    }
//}
//
//internal interface Queue<T> : Iterable<T> {
//
//    val peek: T?
//    val size: Int
//    val isEmpty: Boolean
//
//    fun offer(item: T)
//
//    fun poll(): T?
//
//    fun clear()
//}

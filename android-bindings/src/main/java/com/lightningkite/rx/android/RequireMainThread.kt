package com.lightningkite.rx.android

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.util.concurrent.TimeUnit

val RequireMainThread = TryHandlerScheduler(Handler(Looper.getMainLooper()), true)

class TryHandlerScheduler(val handler: Handler, val async: Boolean): Scheduler() {

    val looper = handler.looper
    val disposed = Disposable.disposed()

    override fun scheduleDirect(run: Runnable): Disposable {
        if(looper.isCurrentThread) {
            run.run()
            return disposed
        } else {
            return scheduleDirect(run, 0L, TimeUnit.NANOSECONDS)
        }
    }

    @SuppressLint("NewApi") // Async will only be true when the API is available to call.
    override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val scheduled = ScheduledRunnable(handler, RxJavaPlugins.onSchedule(run))
        val message = Message.obtain(handler, scheduled)
        if (async) {
            message.isAsynchronous = true
        }
        handler.sendMessageDelayed(message, unit.toMillis(delay))
        return scheduled
    }

    override fun createWorker(): Worker {
        return HandlerWorker(handler, async)
    }

    private class HandlerWorker constructor(private val handler: Handler, private val async: Boolean) :
        Worker() {
        @Volatile
        private var disposed = false
        val looper = handler.looper
        val alreadyDisposed = Disposable.disposed()

        override fun schedule(run: Runnable): Disposable {
            if(looper.isCurrentThread) {
                run.run()
                return alreadyDisposed
            } else {
                return schedule(run, 0L, TimeUnit.NANOSECONDS)
            }
        }

        @SuppressLint("NewApi") // Async will only be true when the API is available to call.
        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            if (disposed) {
                return Disposable.disposed()
            }
            val scheduled = ScheduledRunnable(handler, RxJavaPlugins.onSchedule(run))
            val message = Message.obtain(handler, scheduled)
            message.obj = this // Used as token for batch disposal of this worker's runnables.
            if (async) {
                message.isAsynchronous = true
            }
            handler.sendMessageDelayed(message, unit.toMillis(delay))

            // Re-check disposed state for removing in case we were racing a call to dispose().
            if (disposed) {
                handler.removeCallbacks(scheduled)
                return Disposable.disposed()
            }
            return scheduled
        }

        override fun dispose() {
            disposed = true
            handler.removeCallbacksAndMessages(this /* token */)
        }

        override fun isDisposed(): Boolean {
            return disposed
        }
    }

    private class ScheduledRunnable internal constructor(
        private val handler: Handler,
        private val delegate: Runnable
    ) :
        Runnable, Disposable {
        @Volatile
        private var disposed // Tracked solely for isDisposed().
                = false

        override fun run() {
            try {
                delegate.run()
            } catch (t: Throwable) {
                RxJavaPlugins.onError(t)
            }
        }

        override fun dispose() {
            handler.removeCallbacks(this)
            disposed = true
        }

        override fun isDisposed(): Boolean {
            return disposed
        }
    }
}
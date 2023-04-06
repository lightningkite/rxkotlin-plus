package com.lightningkite.rx.viewgenerators

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.debounce
import com.badoo.reaktive.observable.distinctUntilChanged
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import java.util.concurrent.TimeUnit


object ApplicationAccess {

    private val applicationIsActiveEvent = BehaviorSubject(true)

    /**
     * Whether the application is in the foreground.
     */
    val foreground: Observable<Boolean> = applicationIsActiveEvent
        .debounce(100L, AndroidSchedulers.mainThread())
        .distinctUntilChanged()

    /**
     * Whether the soft input (a.k.a. software keyboard) is up
     */
    val softInputActive = BehaviorSubject(false)

    /**
     * Sets up the [application] to report visibility events to [applicationIsActiveEvent]
     */
    fun applicationIsActiveStartup(application: Application){
        var activeCount = 0
        application.registerActivityLifecycleCallbacks(object: Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                if(activeCount == 0){
                    applicationIsActiveEvent.onNext(true)
                }
                activeCount++
            }
            override fun onActivityPaused(activity: Activity) {
                activeCount--
                if(activeCount == 0){
                    applicationIsActiveEvent.onNext(false)
                }
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        })
    }
}
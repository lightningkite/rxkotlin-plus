package com.lightningkite.rx.viewgenerators

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.lightningkite.rx.ValueSubject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit


object ApplicationAccess {

    private val applicationIsActiveEvent = ValueSubject<Boolean>(true)
    val foreground: Observable<Boolean> = applicationIsActiveEvent
        .debounce(100L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
        .distinctUntilChanged()

    val softInputActive = ValueSubject<Boolean>(false)

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
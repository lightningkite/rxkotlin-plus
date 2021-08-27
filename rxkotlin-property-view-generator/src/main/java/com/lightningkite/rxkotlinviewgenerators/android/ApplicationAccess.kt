package com.lightningkite.rxkotlinviewgenerators.android

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.StandardProperty
import com.lightningkite.rxkotlinproperty.asProperty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


object ApplicationAccess {

    private val applicationIsActiveEvent = PublishSubject.create<Boolean>()
    val foreground: Property<Boolean> = com.lightningkite.rxkotlinviewgenerators.android.ApplicationAccess.applicationIsActiveEvent
        .debounce(100L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
        .distinctUntilChanged()
        .asProperty(true)

    val softInputActive = StandardProperty<Boolean>(false)

    fun applicationIsActiveStartup(application: Application){
        var activeCount = 0
        application.registerActivityLifecycleCallbacks(object: Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                if(activeCount == 0){
                    com.lightningkite.rxkotlinviewgenerators.android.ApplicationAccess.applicationIsActiveEvent.onNext(true)
                }
                activeCount++
            }
            override fun onActivityPaused(activity: Activity) {
                activeCount--
                if(activeCount == 0){
                    com.lightningkite.rxkotlinviewgenerators.android.ApplicationAccess.applicationIsActiveEvent.onNext(false)
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
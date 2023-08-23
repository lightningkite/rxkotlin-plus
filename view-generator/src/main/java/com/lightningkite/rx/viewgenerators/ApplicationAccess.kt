package com.lightningkite.rx.viewgenerators

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.staticApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit


@SuppressLint("MissingPermission")
object ApplicationAccess {

    private val applicationIsActiveEvent = ValueSubject<Boolean>(true)

    /**
     * Whether the application is in the foreground.
     */
    val foreground: Observable<Boolean> = applicationIsActiveEvent
        .debounce(100L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
        .distinctUntilChanged()

    val network: Observable<Boolean> by lazy {
        Observable.create<Boolean> { emitter ->
            val networks: MutableMap<Int, Boolean> = mutableMapOf()

            val accessNetworkStatePermission = ContextCompat.checkSelfPermission(staticApplicationContext, Manifest.permission.ACCESS_NETWORK_STATE)
            if (accessNetworkStatePermission != PackageManager.PERMISSION_GRANTED) {
                throw RuntimeException("must have permission ACCESS_NETWORK_STATE to monitor network");
            }

            val connectionManager = staticApplicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            fun updateInternetAvailable() {
                if (networks.isEmpty()) {
                    emitter.onNext(false)
                } else {
                    val isNetworkAvailable = networks.values.reduce { acc, b -> acc || b }
                    emitter.onNext(isNetworkAvailable)
                }
            }

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    networks[network.hashCode()]
                    updateInternetAvailable()
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    networks[network.hashCode()] = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    updateInternetAvailable()
                }
            }
            val request: NetworkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectionManager.registerNetworkCallback(request, networkCallback)
            emitter.setDisposable(Disposable.fromRunnable {
                connectionManager.unregisterNetworkCallback(networkCallback)
            })
        }.share()
    }

    /**
     * Whether the soft input (a.k.a. software keyboard) is up
     */
    val softInputActive = ValueSubject<Boolean>(false)

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
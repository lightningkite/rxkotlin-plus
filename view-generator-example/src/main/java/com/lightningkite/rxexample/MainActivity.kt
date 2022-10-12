package com.lightningkite.rxexample

import android.os.Bundle
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.SpinnerStyleInterceptor
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.okhttp.HttpClient
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import com.lightningkite.rxexample.dslvg.MainDslVG
import com.lightningkite.rxexample.vg.MainVG
import dev.b3nedikt.viewpump.ViewPump
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : ViewGeneratorActivity() {
    companion object {
        val viewData: ViewGenerator by lazy { MainVG() }
    }

    override val main: ViewGenerator
        get() = viewData

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewPump.init(
            SpinnerStyleInterceptor,
            FocusOnStartupInterceptor
        )
        ApplicationAccess.applicationIsActiveStartup(application)
        staticApplicationContext = applicationContext
        HttpClient.ioScheduler = Schedulers.io()
        HttpClient.responseScheduler = AndroidSchedulers.mainThread()
        super.onCreate(savedInstanceState)
    }


    private var appCompatDelegate: AppCompatDelegate? = null
    override fun getDelegate(): AppCompatDelegate {
        if (appCompatDelegate == null) {
            appCompatDelegate = ViewPumpAppCompatDelegate(
                super.getDelegate(),
                this
            )
        }
        return appCompatDelegate!!
    }
}

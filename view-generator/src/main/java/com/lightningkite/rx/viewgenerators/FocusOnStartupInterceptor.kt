package com.lightningkite.rx.viewgenerators

import io.github.inflationx.viewpump.InflateResult
import io.github.inflationx.viewpump.Interceptor

object FocusOnStartupInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): InflateResult {
        val result = chain.proceed(chain.request())
        if(result.view == null) return result
        val t = result.context.theme.obtainStyledAttributes(result.attrs, intArrayOf(R.attr.focusOnStartup), 0, 0)
        val value = t.getBoolean(0, true)
        t.recycle()
        if(!value) {
            result.view!!.focusAtStartup = value
        }
        return result
    }

}


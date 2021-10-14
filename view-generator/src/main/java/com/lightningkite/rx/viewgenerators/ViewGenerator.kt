//! This file will translate using Khrysalis.
package com.lightningkite.rx.viewgenerators

import android.view.View
import com.lightningkite.rx.android.resources.ViewString
import com.lightningkite.rx.android.resources.ViewStringRaw

interface ViewGenerator {
    val titleString: ViewString
        get() = ViewStringRaw("")

    fun generate(dependency: ActivityAccess): View

    class Default(): ViewGenerator {
        override fun generate(dependency: ActivityAccess): View = View(dependency.context)
    }
}

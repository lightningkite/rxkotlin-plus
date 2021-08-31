//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty.viewgenerators

import android.view.View

abstract class ViewGenerator {
    open val titleString: ViewString
        get() = ViewStringRaw("")

    abstract fun generate(dependency: ActivityAccess): View

    class Default(): ViewGenerator() {
        override fun generate(dependency: ActivityAccess): View = View(dependency.context)
    }
}

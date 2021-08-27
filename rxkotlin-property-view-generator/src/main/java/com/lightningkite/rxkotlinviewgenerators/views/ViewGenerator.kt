//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinviewgenerators.views

import android.view.View
import com.lightningkite.rxkotlinviewgenerators.android.ActivityAccess

abstract class ViewGenerator {
    @Deprecated("Use titleString instead for localizations", ReplaceWith("titleString"))
    open val title: String get() = ""
    @Suppress("DEPRECATION")
    open val titleString: ViewString get() = ViewStringRaw(title)

    abstract fun generate(dependency: ActivityAccess): View

    class Default(): ViewGenerator() {
        override fun generate(dependency: ActivityAccess): View = View(dependency.context)
    }
}

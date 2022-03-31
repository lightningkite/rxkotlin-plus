//! This file will translate using Khrysalis.
package com.lightningkite.rx.viewgenerators

import android.view.View
import com.lightningkite.rx.android.resources.ViewString
import com.lightningkite.rx.android.resources.ViewStringRaw

/**
 * Represents a view or component in Android.
 * Holds data and can render itself to an Android [View] that stays updated.
 */
interface ViewGenerator {
    /**
     * Generates a view representing this object, given access to the activity.
     */
    fun generate(dependency: ActivityAccess): View

    /**
     * An empty view generator.
     */
    class Default(): ViewGenerator {
        override fun generate(dependency: ActivityAccess): View = View(dependency.context)
    }
}

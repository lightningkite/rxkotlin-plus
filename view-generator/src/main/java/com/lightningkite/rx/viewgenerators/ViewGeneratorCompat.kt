//! This file will translate using Khrysalis.
package com.lightningkite.rx.viewgenerators

import android.view.View
import com.lightningkite.rx.android.resources.ViewString
import com.lightningkite.rx.android.resources.ViewStringRaw

@Deprecated("Update to using just plain ViewGenerator")
abstract class ViewGeneratorCompat: ViewGenerator {
    open val title: String get() = ""
    override val titleString: ViewString
        get() = ViewStringRaw(title)
}

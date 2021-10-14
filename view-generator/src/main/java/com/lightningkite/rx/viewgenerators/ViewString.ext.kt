//! This file will translate using Khrysalis.
package com.lightningkite.rx.viewgenerators

import com.lightningkite.rx.android.resources.*


fun ViewString.get(dependency:ActivityAccess):String{
    return this.get(dependency.context)
}

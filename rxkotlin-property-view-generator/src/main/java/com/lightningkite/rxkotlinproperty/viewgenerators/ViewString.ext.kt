//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty.viewgenerators

import com.lightningkite.rxkotlinproperty.android.resources.*


fun ViewString.get(dependency:ActivityAccess):String{
    return this.get(dependency.context)
}

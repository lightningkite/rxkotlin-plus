//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty.viewgenerators

import com.lightningkite.rxkotlinproperty.PropertyStack


interface EntryPoint: HasBackAction {
    fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>){
        println("Empty handler; $schema://$host/$path/$params")
    }
    val mainStack: PropertyStack<ViewGenerator>? get() = null
}

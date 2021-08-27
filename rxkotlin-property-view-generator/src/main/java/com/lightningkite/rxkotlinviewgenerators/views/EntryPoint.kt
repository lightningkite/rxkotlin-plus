//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinviewgenerators.views

import com.lightningkite.rxkotlinproperty.ObservableStack


interface EntryPoint: HasBackAction {
    fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>){
        println("Empty handler; $schema://$host/$path/$params")
    }
    val mainStack: ObservableStack<ViewGenerator>? get() = null
}

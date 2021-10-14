//! This file will translate using Khrysalis.
package com.lightningkite.rx.viewgenerators


interface EntryPoint: HasBackAction {
    fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>){
        println("Empty handler; $schema://$host/$path/$params")
    }
    val mainStack: ViewGeneratorStack? get() = null
}

//! This file will translate using Khrysalis.
package com.lightningkite.rx.viewgenerators

/**
 * Implement this interface on your [ViewGenerator] to handle deep links and the back button being passed to the stack.
 */
interface EntryPoint: HasBackAction {
    fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>){
        println("Empty handler; $schema://$host/$path/$params")
    }
    val mainStack: ViewGeneratorStack? get() = null
    override fun onBackPressed(): Boolean = mainStack?.backPressPop() ?: false
}

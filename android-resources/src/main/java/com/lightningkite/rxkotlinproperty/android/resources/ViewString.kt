//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty.android.resources

import android.content.Context


interface ViewString {
    fun get(context: Context): String
}

class ViewStringRaw(val string: String) : ViewString {
    override fun get(context: Context): String = string
}

class ViewStringResource(val resource: Int) : ViewString {
    override fun get(context: Context): String = context.getString(resource)
}

class ViewStringTemplate(val template: ViewString, val arguments: List<Any>) : ViewString {
    override fun get(context: Context): String {
        val templateResolved = template.get(context)
        val fixedArguments = arguments.map { it -> (it as? ViewString)?.get(context) ?: it }
        return templateResolved.format(*fixedArguments.toTypedArray())
    }
}

class ViewStringComplex(val getter: (Context) -> String) : ViewString {
    override fun get(context: Context): String = getter(context)
}

class ViewStringList(val parts: List<ViewString>, val separator: String = "\n"): ViewString {
    override fun get(context: Context): String {
        return parts.joinToString(separator) { it -> it.get(context) }
    }
}

fun List<ViewString>.joinToViewString(separator: String = "\n"): ViewString {
    if(this.size == 1){
        return this.first()
    }
    return ViewStringList(this, separator)
}

fun ViewString.toDebugString(): String {
    val thing = this
    when (thing) {
        is ViewStringRaw -> return thing.string
        is ViewStringResource -> return thing.resource.toString()
        is ViewStringTemplate -> return thing.template.toDebugString() + "(" + thing.arguments.joinToString { it ->
            if(it is ViewString)
                return@joinToString it.toDebugString()
            else
                return@joinToString "$it"
        } + ")"
        is ViewStringList -> return thing.parts.joinToString(thing.separator) { it -> it.toDebugString() }
        is ViewStringComplex -> return "<Complex string $thing>"
        else -> return "Unknown"
    }
}

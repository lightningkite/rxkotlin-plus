package com.lightningkite.rxkotlinproperty.utils

import groovy.lang.GroovyObject
import java.lang.reflect.Field
import java.lang.reflect.Method

val Any?.groovyObject: GroovyObject? get() = this as? GroovyObject
fun GroovyObject.getPropertyAsObject(key: String): GroovyObject? = getProperty(key) as? GroovyObject

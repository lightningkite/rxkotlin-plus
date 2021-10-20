package com.lightningkite.rx.okhttp

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.datatype.jsr310.JSR310Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef

/**
 * The default JSON mapper from Jackson that is used to serialize and deserialize items in the convenience functions.
 */
val defaultJsonMapper = ObjectMapper()
    .registerModule(KotlinModule())
    .registerModule(JavaTimeModule())
    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    .setDateFormat(StdDateFormat().withLenient(true))

/**
 * Uses the [defaultJsonMapper] to emit the value as a JSON string.
 */
fun Any?.toJsonString(): String = defaultJsonMapper.writeValueAsString(this)

/**
 * Uses the [defaultJsonMapper] to transform the JSON string into a value.
 */
inline fun <reified T> String.fromJsonString(): T = defaultJsonMapper.readValue(this, jacksonTypeRef<T>()) as T

/**
 * Uses the [defaultJsonMapper] to transform the JSON string into a value.
 */
fun <T> String.fromJsonString(type: TypeReference<T>): T = defaultJsonMapper.readValue(this, type)

/**
 * Uses the [defaultJsonMapper] to transform the JSON string into a value.
 */
fun <T> String.fromJsonString(clazz: Class<T>): T = defaultJsonMapper.readValue(this, clazz)

/**
 * Uses the [defaultJsonMapper] to transform the JSON string into a primitive value or a Map<String, Any?> or a List<Any?>.
 */
fun String.fromJsonStringUntyped(): Any = defaultJsonMapper.readValue(this, Any::class.java)


package com.lightningkite.rx

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef

/**
 * Codable is used to indicate that the class in question will be serialized/deserialized using Jackson JSON.
 */

val defaultJsonMapper = ObjectMapper()
    .registerModule(KotlinModule())
    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    .setDateFormat(StdDateFormat().withLenient(true))

fun Any?.toJsonString(): String = defaultJsonMapper.writeValueAsString(this)
inline fun <reified T> String.fromJsonString(): T = defaultJsonMapper.readValue(this, jacksonTypeRef<T>()) as T
fun <T> String.fromJsonString(type: TypeReference<T>): T = defaultJsonMapper.readValue(this, type)
fun <T> String.fromJsonString(clazz: Class<T>): T = defaultJsonMapper.readValue(this, clazz)
fun String.fromJsonStringUntyped(): Any = defaultJsonMapper.readValue(this, Any::class.java)


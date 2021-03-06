package com.lightningkite.rx.okhttp

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.reflect.KType

/**
 * The default JSON mapper from Jackson that is used to serialize and deserialize items in the convenience functions.
 */
var defaultJsonMapper = Json {
    this.ignoreUnknownKeys = true
}
//var defaultJsonMapper = ObjectMapper()
//    .registerModule(KotlinModule())
//    .registerModule(JavaTimeModule())
//    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
//    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
//    .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
//    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
//    .setDateFormat(StdDateFormat().withLenient(true))

/**
 * Uses the [defaultJsonMapper] to emit the value as a JSON string.
 */
inline fun <reified T> T.toJsonString(): String = defaultJsonMapper.encodeToString(this)

/**
 * Uses the [defaultJsonMapper] to emit the value as a JSON string.
 */
fun <T> T.toJsonString(serializer: KSerializer<T>): String = defaultJsonMapper.encodeToString(serializer, this)

/**
 * Uses the [defaultJsonMapper] to transform the JSON string into a value.
 */
inline fun <reified T> String.fromJsonString(): T? = try { defaultJsonMapper.decodeFromString<T>(this) } catch(e: SerializationException) { e.printStackTrace(); null }

/**
 * Uses the [defaultJsonMapper] to transform the JSON string into a value.
 */
fun <T> String.fromJsonString(type: KType): T? = try { defaultJsonMapper.decodeFromString(defaultJsonMapper.serializersModule.serializer(type), this) as? T } catch(e: SerializationException) { e.printStackTrace(); null }

/**
 * Uses the [defaultJsonMapper] to transform the JSON string into a value.
 */
fun <T> String.fromJsonString(serializer: KSerializer<T>): T? = try { defaultJsonMapper.decodeFromString(serializer, this) } catch(e: SerializationException) { e.printStackTrace(); null }

/**
 * Uses the [defaultJsonMapper] to transform the JSON string into a primitive value or a Map<String, Any?> or a List<Any?>.
 */
fun String.fromJsonStringUntyped(): Any? = defaultJsonMapper.parseToJsonElement(this).toPlainKotlin()

fun JsonElement.toPlainKotlin(): Any? = when(this) {
    JsonNull -> null
    is JsonPrimitive -> this.booleanOrNull ?: this.intOrNull ?: this.doubleOrNull
    is JsonObject -> this.entries.associate { it.key to it.value.toPlainKotlin() }
    is JsonArray -> this.map { it.toPlainKotlin() }
}

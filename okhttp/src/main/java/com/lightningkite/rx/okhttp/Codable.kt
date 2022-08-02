package com.lightningkite.rx.okhttp

import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import java.util.*
import kotlin.reflect.KType

val OptionalSerializerModule = SerializersModule {
    contextual(Optional::class) { list ->
        @Suppress("UNCHECKED_CAST")
        OptionalSerializer(list[0] as KSerializer<Any>)
    }
}

/**
 * The default JSON mapper from Jackson that is used to serialize and deserialize items in the convenience functions.
 */
var defaultJsonMapper = Json {
    this.ignoreUnknownKeys = true
    serializersModule = OptionalSerializerModule
}

@Suppress("OPT_IN_USAGE")
class OptionalSerializer<T: Any>(val inner: KSerializer<T>): KSerializer<Optional<T>> {
    val nullable = inner.nullable
    override val descriptor: SerialDescriptor
        get() = SerialDescriptor("Optional<${inner.descriptor.serialName}>", nullable.descriptor)
    override fun deserialize(decoder: Decoder): Optional<T> = Optional.ofNullable(nullable.deserialize(decoder))
    override fun serialize(encoder: Encoder, value: Optional<T>) {
        nullable.serialize(encoder, if(value.isPresent) value.get() else null)
    }
}

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

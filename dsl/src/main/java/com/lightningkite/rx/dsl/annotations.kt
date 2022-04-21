package com.lightningkite.rx.dsl

@RequiresOptIn(message = "This API is experimental. It may be changed in the future without notice.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
annotation class RxKotlinViewDsl() // Opt-in requirement annotation
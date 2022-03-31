//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.models

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    var userId: Long = 0,
    var id: Long = 0,
    var title: String = "",
    var body: String = ""
)

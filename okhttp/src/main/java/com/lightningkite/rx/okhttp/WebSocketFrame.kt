package com.lightningkite.rx.okhttp


class WebSocketFrame(val binary: ByteArray? = null, val text: String? = null) {
    override fun toString(): String {
        return text ?: binary?.let { "<Binary data length ${it.size}" } ?: "<Empty Frame>"
    }
}

package com.lightningkite.rx.okhttp


/**
 * A single frame of a web socket.
 */
class WebSocketFrame(val binary: ByteArray? = null, val text: String? = null) {
    override fun toString(): String {
        return text ?: binary?.let { "<Binary data length ${it.size}" } ?: "<Empty Frame>"
    }
}

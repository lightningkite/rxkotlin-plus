package com.lightningkite.rx.utils

import java.io.InputStream
import java.security.MessageDigest


fun String.camelCase(): String {
    var nextIsUppercase = false
    return buildString(length) {
        for (c in this@camelCase) {
            if (c == '_') {
                nextIsUppercase = true
            } else {
                if (nextIsUppercase) {
                    append(c.toUpperCase())
                    nextIsUppercase = false
                } else {
                    append(c)
                }
            }
        }
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun InputStream.checksum(): String {
    val digest = MessageDigest.getInstance("MD5")
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    while (true) {
        val readBytes = this.read(buffer)
        if (readBytes == -1) break
        digest.update(buffer, 0, readBytes)
    }
    return digest.digest().joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
}

fun String.checksum(): String = this.byteInputStream().use { it.checksum() }

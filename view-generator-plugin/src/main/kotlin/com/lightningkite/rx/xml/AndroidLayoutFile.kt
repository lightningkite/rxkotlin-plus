package com.lightningkite.rx.xml

import java.io.File

data class AndroidLayoutFile(
    val name: String,
    val fileName: String,
    val variants: Set<String>,
    val files: Set<File>,
    val bindings: Map<String, AndroidIdHook>,
    val delegateBindings: Map<String, AndroidDelegateHook>,
    val sublayouts: Map<String, AndroidSubLayout>,
    val emitCurse: Map<String, AndroidAction>
)

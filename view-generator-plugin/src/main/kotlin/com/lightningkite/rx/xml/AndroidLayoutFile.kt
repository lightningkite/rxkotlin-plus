package com.lightningkite.rx.xml

import com.lightningkite.rx.utils.*
import java.io.File

typealias Styles = Map<String, Map<String, String>>

data class AndroidLayoutFile(
    val name: String,
    val fileName: String,
    val variants: Set<String>,
    val files: Set<File>,
    val bindings: Map<String, AndroidIdHook>,
    val delegateBindings: Map<String, AndroidDelegateHook>,
    val sublayouts: Map<String, AndroidSubLayout>,
    val emitCurse: Map<String, AndroidAction>
) {
    companion object {

        fun combine(iter: Sequence<AndroidLayoutFile>): AndroidLayoutFile =
            AndroidLayoutFile(
                name = iter.first().name,
                fileName = iter.first().fileName,
                variants = iter.flatMap { it.variants.asSequence() }.toSet(),
                files = iter.flatMap { it.files }.toSet(),
                bindings = run {
                    (iter.flatMap { it.bindings.asSequence() }.associate { it.toPair() }).mapValues { (key, value) ->
                        if (iter.all { it.bindings[key] != null }) value
                        else value.copy(optional = true)
                    }
                },
                delegateBindings = run {
                    (iter.flatMap { it.delegateBindings.asSequence() }
                        .associate { it.toPair() }).mapValues { (key, value) ->
                            if (iter.all { it.delegateBindings[key] != null }) value
                            else value.copy(optional = true)
                        }
                },
                sublayouts = run {
                    (iter.flatMap { it.sublayouts.asSequence() }.associate { it.toPair() }).mapValues { (key, value) ->
                        if (iter.all { it.sublayouts[key] != null }) value
                        else value.copy(optional = true)
                    }
                },
                emitCurse = run {
                    (iter.flatMap { it.emitCurse.asSequence() }.associate { it.toPair() }).mapValues { (key, value) ->
                        if (iter.all { it.emitCurse[key] != null }) value
                        else value.copy(optional = true)
                    }
                }
            )

        fun parse(resolve: ((String)->File?)?, file: File, variant: String, styles: Styles): AndroidLayoutFile {
            val node = XmlNode.read(file, styles, resolve)
            val fileName = file.nameWithoutExtension.camelCase().capitalize()
            val bindings = ArrayList<AndroidIdHook>()
            val delegateBindings = ArrayList<AndroidDelegateHook>()
            val sublayouts = ArrayList<AndroidSubLayout>()
            val emitCurse = HashMap<String, AndroidAction>()

            fun addBindings(node: XmlNode) {
                if(node.name == "com.google.android.material.tabs.TabItem") {
                    return
                }
                node.allAttributes["android:id"]?.let { raw ->
                    val id = raw.removePrefix("@+id/").removePrefix("@id/")
                    val camelCasedId = id.camelCase()
                    if (node.name == "include") {
                        val layout = node.allAttributes["layout"]!!.removePrefix("@layout/")
                        sublayouts.add(
                            AndroidSubLayout(
                                name = camelCasedId,
                                resourceId = id,
                                layoutXmlClass = layout.camelCase().capitalize() + "Xml"
                            )
                        )
                    } else {
                        val name = raw.removePrefix("@+id/").removePrefix("@id/").camelCase()
                        bindings.add(
                            AndroidIdHook(
                                name = name,
                                type = node.name,
                                resourceId = raw.removePrefix("@+id/").removePrefix("@id/")
                            )
                        )
                        if(node.name == "Spinner") {
                            val raw = node.allAttributes["android:textColor"]
                            val colorText = when {
                                raw == null -> "0xFF000000.asColor()"
                                raw.startsWith("@color/") -> {
                                    val colorName = raw.removePrefix("@color/")
                                    "view.context.getColor(R.color.${colorName})"
                                }
                                raw.startsWith("@android:color/") -> {
                                    val colorName = raw.removePrefix("@android:color/")
                                    "view.context.getColor(R.color.${colorName})"
                                }
                                raw.startsWith("#") -> TODO("Frick you, make it a resource")
                                else -> "0xFF000000.asColor()"
                            }
                            emitCurse["$name-color"] = (
                                    AndroidAction(
                                        name,
                                        "spinnerTextColor = $colorText"
                                    )
                                    )
                            node.attributeAsDouble("android:textSize")?.let { textSize ->
                                emitCurse["$name-size"] = (
                                        AndroidAction(
                                            name,
                                            "spinnerTextSize = $textSize"
                                        )
                                        )
                            }
                        }
                        node.attributeAsBoolean("tools:focusAtStartup")?.let { value ->
                            emitCurse[name] = (
                                    AndroidAction(
                                        name,
                                        "focusAtStartup = $value"
                                    )
                                    )
                        }
                        node.attributeAsEdgeFlagsKotlin("tools:systemEdges")?.let {
                            emitCurse[name] = (
                                    AndroidAction(
                                        name,
                                        "safeInsets($it)"
                                    )
                                    )
                        }
                        node.attributeAsEdgeFlagsKotlin("tools:systemEdgesSizing")?.let {
                            emitCurse[name] = (
                                    AndroidAction(
                                        name,
                                        "safeInsetsSizing($it)"
                                    )
                                    )
                        }
                        node.attributeAsEdgeFlagsKotlin("tools:systemEdgesBoth")?.let {
                            emitCurse[name] = (
                                    AndroidAction(
                                        name,
                                        "safeInsetsBoth($it)"
                                    )
                                    )
                        }
                        (node.allAttributes["app:delegateClass"] ?: node.allAttributes["delegateClass"])?.let {
                            delegateBindings.add(
                                AndroidDelegateHook(
                                    name = raw.removePrefix("@+id/").removePrefix("@id/").camelCase(),
                                    type = it,
                                    resourceId = raw.removePrefix("@+id/").removePrefix("@id/")
                                )
                            )
                        }
                    }
                }
                node.children.forEach {
                    addBindings(it)
                }
            }
            addBindings(node)
            return AndroidLayoutFile(
                name = fileName,
                fileName = file.nameWithoutExtension,
                variants = if(variant.isNotEmpty()) setOf(variant) else setOf(),
                files = setOf(file),
                bindings = bindings.associateBy { it.name },
                delegateBindings = delegateBindings.associateBy { it.name },
                sublayouts = sublayouts.associateBy { it.name },
                emitCurse = emitCurse
            )
        }

        fun parseSet(folder: File, filename: String, styles: Styles): AndroidLayoutFile {
            return folder.listFiles()!!.asSequence().filter { it.name.startsWith("layout") }
                .map { it.resolve(filename) }
                .filter { it.exists() }
                .map { parse(null, it, it.parentFile.name.substringAfter("layout-", ""), styles) }
                .let { combine(it) }
        }

        fun parseAll(folder: File, styles: Styles): Map<String, AndroidLayoutFile> {
            return folder.listFiles()!!.asSequence().filter { it.name.startsWith("layout") }
                .flatMap { it.listFiles()!!.asSequence() }
                .map { it.name }
                .distinct()
                .map { parseSet(folder, it, styles) }
                .associateBy { it.name }
        }
    }
}

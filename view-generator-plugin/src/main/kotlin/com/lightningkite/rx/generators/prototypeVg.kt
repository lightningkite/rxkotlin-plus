package com.lightningkite.rx.generators

import com.lightningkite.rx.gradle.pluginConfiguration
import com.lightningkite.rx.xml.AndroidLayoutFile
import com.lightningkite.rx.utils.*
import java.io.File
import java.lang.IllegalArgumentException

internal val oldWarning = "Any changes made to this file will be overridden unless this comment is removed."

internal fun createPrototypeVG(
    layoutInfo: Map<String, AndroidLayoutFile>,
    styles: Styles,
    viewName: String,
    xml: File,
    target: File,
    viewNodeMap: Map<String, ViewNode>,
    viewNode: ViewNode,
    packageName: String,
    applicationPackage: String
) {
    if (target.exists() && target.useLines { it.any { it.contains(oldWarning) } }) {
        //full override
        target.writeText(
            generateFile(
                layoutInfo,
                xml,
                viewNodeMap,
                viewName,
                packageName,
                applicationPackage,
                viewNode,
                styles
            )
        )
    } else if (target.exists() && target.useLines { it.any { it.contains(CodeSection.overwriteMarker) } }) {
        //partial override
        val generated = CodeSection.read(
            generateFile(
                layoutInfo,
                xml,
                viewNodeMap,
                viewName,
                packageName,
                applicationPackage,
                viewNode,
                styles
            ).lines()
        )
        val existing = CodeSection.read(target.readLines())
        target.writeText(buildString {
            with(TabWriter(this)) {
                existing.mergeOverride(generated).forEach {
                    it.writeWhole(this)
                }
            }
        })
    } else if (!target.exists()) {
        //new file
        target.writeText(
            generateFile(
                layoutInfo,
                xml,
                viewNodeMap,
                viewName,
                packageName,
                applicationPackage,
                viewNode,
                styles
            )
        )
    }

}

private fun generateFile(
    layoutInfo: Map<String, AndroidLayoutFile>,
    xml: File,
    viewNodeMap: Map<String, ViewNode>,
    viewName: String,
    packageName: String,
    applicationPackage: String,
    viewNode: ViewNode,
    styles: Styles
): String {
    val node = XmlNode.read(xml, mapOf())

    fun makeView(otherViewNode: ViewNode, forStack: String?, path: String?): String {
        val totalProvides = viewNode.totalRequires(viewNodeMap) + viewNode.provides
        return otherViewNode
            .totalRequires(viewNodeMap)
            .sortedBy { it.name }
            .filter {
                val included = totalProvides.any { p -> p.satisfies(it) } || it.name == "stack"
                if (!included && it.default == null) throw IllegalArgumentException("Cannot provide arg ${it} for ${otherViewNode.name} in ${viewNode.name}")
                included
            }
            .joinToString(
                ", ",
                "${otherViewNode.name}VG(",
                ")"
            ) { arg ->
                val myName = when {
                    arg.name == "stack" -> "this.$forStack"
                    path != null && arg.onPath == path -> arg.name
                    else -> "this." + arg.name
                }
                arg.name + " = " + myName
            }
    }

    val into = StringBuilder()

    val inits = ArrayList<() -> Unit>()
    val actions = ArrayList<() -> Unit>()


    fun getStackAndFile(node: XmlNode, attribute: String): Pair<String, String> {
        val prefix = attribute.removePrefix("@").substringBefore('/')
        val file = attribute.substringAfter("/").camelCase().capitalize()
        val onStack = node.allAttributes[ViewNode.attributeOnStack] ?: if (prefix != "layout") prefix else "stack"
        return file to onStack
    }

    with(TabWriter(into)) {
        fun handleNodeClick(
            node: XmlNode,
            view: String,
            viewAccess: String,
            makeAction: (action: () -> Unit) -> Unit = { action ->
                val actionName = (view.removePrefix("xml").replace(Regex("\\.[a-zA-Z]")) { result ->
                    result.value.drop(1).toUpperCase()
                } + "Click").decapitalize()
                line("${viewAccess}onClick { this.$actionName() }")
                actions += {
                    line("${CodeSection.sectionMarker} Action $actionName ${CodeSection.overwriteMarker}")
                    line("fun $actionName() {")
                    tab {
                        action()
                    }
                    line("}")
                    line()
                }
            }
        ) {

            node.allAttributes["tools:print"]?.let {
                println(it)
            }
            node.allAttributes[ViewNode.attributePush]?.let {
                val (nodeName, stackName) = getStackAndFile(node, it)
                val otherViewNode = viewNodeMap[nodeName] ?: return@let
                makeAction {
                    line("$stackName.push(${makeView(otherViewNode, stackName, view)})")
                }
            } ?: node.allAttributes[ViewNode.attributeSwap]?.let {
                val (nodeName, stackName) = getStackAndFile(node, it)
                val otherViewNode = viewNodeMap[nodeName] ?: return@let
                makeAction {
                    line("this.$stackName.swap(${makeView(otherViewNode, stackName, view)})")
                }
            } ?: node.allAttributes[ViewNode.attributePopTo]?.let {
                val (nodeName, stackName) = getStackAndFile(node, it)
                val otherViewNode = viewNodeMap[nodeName] ?: return@let
                makeAction {
                    line("this.$stackName.popTo { it -> it is ${otherViewNode}VG }")
                }
            } ?: node.allAttributes[ViewNode.attributeReset]?.let {
                val (nodeName, stackName) = getStackAndFile(node, it)
                val otherViewNode = viewNodeMap[nodeName] ?: return@let
                makeAction {
                    line("this.$stackName.reset(${makeView(otherViewNode, stackName, view)})")
                }
            } ?: node.allAttributes[ViewNode.attributePop]?.let {
                val stackNames = node.allAttributes[ViewNode.attributeOnStack]?.split(';') ?: listOf("stack")
                if (stackNames.size == 1) {
                    makeAction {
                        line("this.${stackNames.first()}.pop()")
                    }
                } else {
                    makeAction {
                        startLine()
                        stackNames.forEachBetween(
                            forItem = {
                                direct.append("if(this.$it.pop()) {}")
                            },
                            between = {
                                direct.append(" else ")
                            }
                        )
                    }
                }
            } ?: node.allAttributes[ViewNode.attributeDismiss]?.let {
                val stackNames = node.allAttributes[ViewNode.attributeOnStack]?.split(';') ?: listOf("stack")
                if (stackNames.size == 1) {
                    makeAction {
                        line("this.${stackNames.first()}.dismiss()")
                    }
                } else {
                    makeAction {
                        startLine()
                        stackNames.forEachBetween(
                            forItem = {
                                direct.append("if(this.$it.dismiss()) {}")
                            },
                            between = {
                                direct.append(" else ")
                            }
                        )
                    }
                }
            } ?: run {
                when (node.name) {
                    "Button", "ImageButton" -> {
                        makeAction {}
                    }
                }
            }
        }

        if (pluginConfiguration.injectKhrysalisAnnotations) {
            line("@file:SharedCode")
        }
        line("//")
        line("// ${viewName}VG.swift")
        line("// Created by RxKotlin-Plus Prototype Generator")
        line("// Sections of this file will be replaced if the marker, '${CodeSection.overwriteMarker}', is left in place.")
        line("//")
        line("package $packageName")
        line()
        line("${CodeSection.sectionMarker} Imports")
        line()
        line("import android.widget.*")
        line("import android.view.*")
        line("import com.lightningkite.rx.*")
        line("import com.lightningkite.rx.android.*")
        line("import com.lightningkite.rx.android.resources.*")
        line("import com.lightningkite.rx.viewgenerators.*")
        if (pluginConfiguration.injectKhrysalisAnnotations) {
            line("import com.lightningkite.khrysalis.*")
        }
        line("import $applicationPackage.R")
        line("import $applicationPackage.databinding.*")
        line("import io.reactivex.rxjava3.core.Observable")
        line()
        line("${CodeSection.sectionMarker} Name ${CodeSection.overwriteMarker}")
        line("""@Suppress("NAME_SHADOWING")""")
        line("class ${viewName}VG(")
        tab {
            line("${CodeSection.sectionMarker} Dependencies ${CodeSection.overwriteMarker}")
            val things = (viewNode.totalRequires(viewNodeMap).sortedBy { it.name })
            things.forEachIndexed { _, it ->
                if ((it.type.contains("->") || it.type.contains("-]")) && !it.type.endsWith('?')) {
                    line("val ${it.name}: ${it.kotlinType}${(if (it.default != null) " = " + it.default else "")},")
                } else if (pluginConfiguration.injectKhrysalisAnnotations && (it.type.contains("VG") || it.type.contains(
                        "ViewGenerator"
                    ))
                ) {
                    line("@Unowned val $it,")
                } else {
                    line("val $it,")
                }
            }
            line("${CodeSection.sectionMarker} Extends")
        }
        line(") : ViewGenerator {")
        line()
        tab {
            line("${CodeSection.sectionMarker} Properties")
            viewNode.provides.sortedBy { it.name }.filter { it.onPath == null }.forEach {
                line()
                line("${CodeSection.sectionMarker} Provides ${it.name} ${CodeSection.overwriteMarker}")
                line(
                    """val ${it.name}: ${it.kotlinType} = ${
                        it.construct(
                            viewNode,
                            viewNodeMap
                        )
                    }"""
                )
            }
            if (viewNode.operations.any { it is ViewStackOp.Embed }) {
                for (op in viewNode.operations.mapNotNull { it as? ViewStackOp.Embed }) {
                    line()
                    line("${CodeSection.sectionMarker} Embedded ${op.replaceId.camelCase()}Vg ${CodeSection.overwriteMarker}")
                    val otherViewNode =
                        viewNodeMap[op.viewName.removePrefix("@layout/").camelCase().capitalize()] ?: break
                    line("val ${op.replaceId.camelCase()}Vg = ${makeView(otherViewNode, null, op.replaceId)}")
                }
            }
            line()
            line("${CodeSection.sectionMarker} Generate Start ${CodeSection.overwriteMarker}")
            line("""override fun generate(dependency: ActivityAccess): View {""")
            line()
            tab {
                line("val xml = ${viewName}Binding.inflate(dependency.layoutInflater)")

                fun handleNode(inside: String, node: XmlNode, prefix: String) {
                    node.allAttributes["android:id"]?.removePrefix("@+id/")?.let { viewId ->
                        val viewIdentifier = viewId.camelCase()
                        val isOptional = prefix.contains('?') || (
                                layoutInfo[inside]?.bindings?.get(viewIdentifier)?.optional
                                    ?: layoutInfo[inside]?.sublayouts?.get(viewIdentifier)?.optional
                                    ?: false
                                )
                        val view = prefix + if (node.name == "include") {
                            if (isOptional)
                                "$viewIdentifier?.root"
                            else
                                "$viewIdentifier.root"
                        } else viewIdentifier

                        val viewAccess = if (isOptional) "$view?." else "$view."

                        if (node.name == "include") {
                            line()
                            line(
                                "${CodeSection.sectionMarker} Set Up ${
                                    view.replace(
                                        "?",
                                        ""
                                    )
                                } ${CodeSection.overwriteMarker}"
                            )
                            node.allAttributes["layout"]?.let {
                                val fileName = it.removePrefix("@layout/")
                                if (fileName.startsWith("component", true)) {
                                    val file = xml.parentFile.resolve(fileName.plus(".xml"))
                                    handleNode(
                                        inside = fileName.camelCase().capitalize(),
                                        node = XmlNode.read(file, styles),
                                        prefix = if (isOptional) "$prefix$viewIdentifier?." else "$prefix$viewIdentifier."
                                    )
                                } else {
                                    //embed
                                    line("${viewAccess}replace(${viewIdentifier}Vg.generate(dependency))")
                                }
                            }
                            return
                        }
                        if (view.contains("dummy", true)) {
                            handleNodeClick(node, view, viewAccess) { action ->
                                val p =
                                    view.replace("dummy", "").removePrefix("xml")
                                        .replace(Regex("\\.[a-zA-Z]")) { result ->
                                            result.value[1].toUpperCase().toString()
                                        }
                                if (p.isEmpty() || !p[0].isJavaIdentifierStart()) return@handleNodeClick
                                val actionName = (p + "Action").decapitalize()
                                actions += {
                                    line("${CodeSection.sectionMarker} Action $actionName ${CodeSection.overwriteMarker}")
                                    line("fun $actionName() {")
                                    tab {
                                        action()
                                    }
                                    line("}")
                                }
                            }
                            return
                        }
                        if (node.name == "com.google.android.material.tabs.TabLayout" && node.children.isNotEmpty()) {
                            line()
                            line(
                                "${CodeSection.sectionMarker} Set Up ${
                                    view.replace(
                                        "?",
                                        ""
                                    )
                                } ${CodeSection.overwriteMarker}"
                            )
                            line("${viewAccess}addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {")
                            tab {
                                line("override fun onTabSelected(tab: TabLayout.Tab) {")
                                tab {
                                    line("when(tab.position) {")
                                    node.children
                                        .filter { it.name == "com.google.android.material.tabs.TabItem" }
                                        .forEachIndexed { index, child ->
                                            val childId =
                                                child.allAttributes["android:id"]?.removePrefix("@+id/")?.camelCase()
                                                    ?: index.toString()
                                            handleNodeClick(child, "$view.$childId", "") { action ->
                                                val actionName =
                                                    (view.removePrefix("xml").replace(Regex("\\.[a-zA-Z]")) { result ->
                                                        result.value.drop(1).toUpperCase()
                                                    } + childId.capitalize()).decapitalize()
                                                line(
                                                    "${CodeSection.sectionMarker} Action for ${
                                                        view.replace(
                                                            "?",
                                                            ""
                                                        )
                                                    }.$childId ${CodeSection.overwriteMarker}"
                                                )
                                                line("$index -> $actionName()")
                                                actions += {
                                                    line("${CodeSection.sectionMarker} Action $actionName ${CodeSection.overwriteMarker}")
                                                    line("fun $actionName() {")
                                                    tab {
                                                        action()
                                                    }
                                                    line("}")
                                                }
                                            }
                                        }
                                    line("${CodeSection.sectionMarker} Action for ${view.replace("?", "")} Done")
                                    line("}")
                                }
                                line("}")
                                line("override fun onTabUnselected(tab: TabLayout.Tab) {}")
                                line("override fun onTabReselected(tab: TabLayout.Tab) = onTabSelected(tab)")
                            }
                            line("})")
                            return
                        }
                        line()
                        line(
                            "${CodeSection.sectionMarker} Set Up ${
                                view.replace(
                                    "?",
                                    ""
                                )
                            } ${CodeSection.overwriteMarker}"
                        )
                        node.allAttributes["tools:text"]?.let {
                            if (it.startsWith("@string")) {
                                line(
                                    """${viewAccess}setText(R.string.${
                                        it.removePrefix(
                                            "@string/"
                                        )
                                    })"""
                                )
                            } else {
                                line(
                                    """${viewAccess}setText("${
                                        it.replace(
                                            "$",
                                            "\\$"
                                        )
                                    }")"""
                                )
                            }
                        }
                        node.allAttributes["tools:src"]?.let {
                            if (it.startsWith("@drawable")) {
                                line("""${viewAccess}setImageResource(R.drawable.${it.removePrefix("@drawable/")})""")
                            }
                        }
                        node.allAttributes["tools:visibility"]?.let {
                            when (it) {
                                "gone" -> line("${viewAccess}visibility = View.GONE")
                                "invisible" -> line("${viewAccess}visibility = View.INVISIBLE")
                                "visible" -> line("${viewAccess}visibility = View.VISIBLE")
                                else -> {
                                }
                            }
                        }
                        node.allAttributes["tools:embed"]?.let {
                            line("${viewAccess}replace(${viewIdentifier.camelCase()}Vg.generate(dependency))")
                        }
                        node.allAttributes["tools:listitem"]?.let {
                            val subName = it.removePrefix("@layout/").camelCase().capitalize()
                            val xmlName = subName.plus("Binding")
                            val otherViewNode = viewNodeMap[subName]
                            line("Observable.just(listOf(1, 2, 3, 4))")
                            line("    .showIn($view) label@ { obs ->")
                            line()
                            tab {
                                line(
                                    "${CodeSection.sectionMarker} Make Subview For ${
                                        view.replace(
                                            "?",
                                            ""
                                        )
                                    } ${CodeSection.overwriteMarker}"
                                )
                                // If sublayout has a VG, use that instead of looping down the layout.
                                if (otherViewNode != null) {
                                    line("val cellVg = ${makeView(otherViewNode, "stack", view)} ")
                                    handleNodeClick(node, "cellView", "cellView.")
                                } else {
                                    line("val cellXml = $xmlName.inflate(dependency.layoutInflater) ")
                                    val file = xml.parentFile.resolve(it.removePrefix("@layout/").plus(".xml"))
                                    handleNode(subName, XmlNode.read(file, styles), "cellXml.")
                                    handleNodeClick(node, "cellXml.root", "cellXml.root.")
                                }
                                line()
                                line(
                                    "${CodeSection.sectionMarker} End Make Subview For ${
                                        view.replace(
                                            "?",
                                            ""
                                        )
                                    } ${CodeSection.overwriteMarker}"
                                )
                                if (otherViewNode != null) {
                                    line("return@label cellVg.generate(dependency)")
                                } else {
                                    line("return@label cellXml.root")
                                }
                            }
                            line("}")

                        }
                        handleNodeClick(node, view, viewAccess)
                        node.allAttributes[ViewNode.attributeStackId]?.let { stackName ->
                            node.allAttributes[ViewNode.attributeStackDefault]?.let stackDefault@{
                                val otherViewNode =
                                    viewNodeMap[it.removePrefix("@layout/").camelCase().capitalize()]
                                        ?: run {
                                            println(
                                                "WARNING: Could not find view ${
                                                    it.removePrefix("@layout/").camelCase().capitalize()
                                                } for default of stack $stackName"
                                            )
                                            return@stackDefault
                                        }
                                val makeView = makeView(otherViewNode, stackName, null)
                                inits.add {
                                    line("${CodeSection.sectionMarker} Set Initial View for $stackName ${CodeSection.overwriteMarker}")
                                    line("this.$stackName.reset($makeView)")
                                    line()
                                }
                            }
                            line("$stackName.showIn($view, dependency)")
                        }
                    } ?: run {
                        if (node.allAttributes.keys.any { it.startsWith("tools:") }) {
                            println("WARNING: ${xml.name}: Element type ${node.name} has tools but no id")
                        }
                    }
                    node.children.forEach {
                        handleNode(inside, it, prefix)
                    }
                }
                handleNode(viewName, node, "xml.")
                line()
                line("${CodeSection.sectionMarker} Generate End ${CodeSection.overwriteMarker}")
                line()
                line("return xml.root")
            }
            line("}")
            line()
            line("${CodeSection.sectionMarker} Init")
            line("init {")
            line()
            tab {
                inits.forEach { it() }
            }
            line("${CodeSection.sectionMarker} Init End")
            line("}")
            line()
            line("${CodeSection.sectionMarker} Actions")
            line()
            actions.forEach { it() }
            line()
            line("${CodeSection.sectionMarker} Body End")
        }
        line("}")
    }
    return into.toString()
}

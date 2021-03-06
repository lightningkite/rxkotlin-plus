package com.lightningkite.rx.generators

import com.lightningkite.rx.utils.MergableCollection
import com.lightningkite.rx.utils.addOrMerge
import com.lightningkite.rx.utils.Styles
import com.lightningkite.rx.utils.XmlNode
import com.lightningkite.rx.utils.camelCase
import java.io.File
import kotlin.math.max

class ViewNode(
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        return other is ViewNode && this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }

    val operations: HashSet<ViewStackOp> = HashSet()
    val requires = MergableCollection<ViewVar>()
    val provides = ArrayList<ViewVar>()
    val instantiates: Set<String> get() = operations.mapNotNull { it.viewName }.toSet()

    var depth: Int = -1

    data class Resolved(
        val node: ViewNode,
        val comesFrom: Set<String>,
        val totalRequires: List<ViewVar.Requirement>,
        val belongsToStacks: Set<String>
    )

    fun resolve(map: Map<String, ViewNode>): Resolved {
        return Resolved(
            node = this,
            comesFrom = this.createdBy(map),
            totalRequires = this.totalRequiresBetter(map).sortedBy { it.viewVar.name },
            belongsToStacks = this.belongsToStacks(map)
        )
    }

    companion object {

        const val attributePush = "tools:goTo"
        const val attributeSwap = "tools:swap"
        const val attributePop = "tools:pop"
        const val attributeDismiss = "tools:dismiss"
        const val attributeReset = "tools:reset"
        const val attributePopTo = "tools:popTo"
        const val attributeOnStack = "tools:onStack"
        const val attributeStackDefault = "tools:stackDefault"
        const val attributeEmbed = "tools:embed"
        const val attributeStackId = "tools:stackId"
        const val attributeRequires = "tools:requires"
        const val attributeProvides = "tools:provides"
        const val attributeIgnore = "tools:ignore"

        //Breadth-first search
        fun estimateDepth(map: Map<String, ViewNode>) {
            map.values.forEach { it.depth = -1 }
            val root = root(map) ?: return
            root.depth = 0
            var highestSeen = 0
            val seen = mutableListOf<String>()
            val stack = mutableListOf(root)
            while (stack.isNotEmpty()) {
                val next = stack.removeAt(0)
                highestSeen = max(highestSeen, next.depth)
                for (item in next.instantiates) {
                    if (item in seen) continue
                    seen.add(item)
                    map[item]?.let {
                        it.depth = next.depth + 1
                        stack.add(it)
                    }
                }
            }

            map.values.forEach {
                if (it.depth == -1) {
                    it.depth = highestSeen
                }
            }
        }

        fun root(map: Map<String, ViewNode>): ViewNode? =
            map["Root"] ?: map["Main"] ?: map["Landing"] ?: map.values.firstOrNull()

        fun assertNoLeaks(map: Map<String, ViewNode>) {
            val root = root(map) ?: return
            val leakMessages = ArrayList<String>()

            for (node in map.values) {
                val tr = node.totalRequiresBetter(map).toList()
                tr.groupBy { it.viewVar.name }.filter { it.value.size > 1 }.forEach {
                    leakMessages += "Found same-name variable requirements with different types on ${node.name}.${it.key}:"
                    it.value.forEach { v ->
                        v.paths.forEach { p ->
                            leakMessages += "    ${v.viewVar.type} from ${p}"
                        }
                    }
                }
            }

            for (leakedVar in root.totalRequiresBetter(map)) {
                leakMessages += leakedVar.paths.map {
                    "${leakedVar.viewVar} leaked through path $it"
                }
            }

            if (leakMessages.isEmpty()) return
            throw Exception("Leak detected!\n${leakMessages.joinToString("\n")}")
        }
    }

    fun totalRequires(map: Map<String, ViewNode>, seen: Set<String> = setOf()): Collection<ViewVar> =
        totalRequiresBetter(map, seen).map { it.viewVar }

    fun totalRequiresBetter(map: Map<String, ViewNode>, seen: Set<String> = setOf()): Collection<ViewVar.Requirement> {

        if (name in seen) return listOf()

        val directRequirements = requires.map { it.requiredByMe(this.name) }

        val indirectRequirements = ArrayList<ViewVar.Requirement>()
        for (inst in instantiates) {
            val subnode = map[inst] ?: continue
            for (x in subnode.totalRequiresBetter(map, seen + name)) {
                if (x.viewVar.name == "stack") continue
                if (x.viewVar.default != null) continue
                if (directRequirements.any { it.viewVar.satisfies(x.viewVar) }) continue
                indirectRequirements.addOrMerge(
                    x.requiredBy(inst),
                    satisfies = { myItem, existing -> existing.merge(myItem) }
                )
            }
        }

        return directRequirements.filter { provides.none { p -> p.sameAs(it.viewVar) } } + indirectRequirements.filter {
            provides.none { p ->
                p.satisfies(
                    it.viewVar
                )
            }
        }
    }

    fun belongsToStacks(map: Map<String, ViewNode>): Set<String> {
        return map.values.asSequence()
            .flatMap { it.operations.asSequence() }
            .filter { it.viewName == name }
            .mapNotNull { it.stack }
            .toSet()
    }

    fun createdBy(map: Map<String, ViewNode>): Set<String> {
        return map.values.asSequence()
            .filter { it.operations.asSequence().any { it.viewName == this.name } }
            .mapNotNull { it.name }
            .toSet()
    }


    fun getStackAndFile(node: XmlNode, attribute: String): Pair<String, String> {
        val prefix = attribute.removePrefix("@").substringBefore('/')
        val file = attribute.substringAfter("/").camelCase().capitalize()
        val onStack = node.allAttributes[attributeOnStack] ?: if (prefix != "layout") prefix else "stack"
        return file to onStack
    }

    fun gather(node: XmlNode, xml: File, styles: Styles, parentPath: String? = "xml") {
        val path = parentPath?.let { parentPath ->
            node.allAttributes["android:id"]?.removePrefix("@+id/")?.camelCase()?.let {
                "$parentPath.$it"
            }
        }
        node.allAttributes[attributePush]?.let {
            val (file, onStack) = getStackAndFile(node, it)
            operations.add(
                ViewStackOp.Push(
                    stack = onStack,
                    viewName = file
                )
            )
            requires.add(
                ViewVar(
                    name = onStack,
                    type = "ViewGeneratorStack",
                    default = null
                )
            )
        }
        node.allAttributes[attributeSwap]?.let {
            val (file, onStack) = getStackAndFile(node, it)
            operations.add(
                ViewStackOp.Swap(
                    stack = onStack,
                    viewName = file
                )
            )
            requires.add(
                ViewVar(
                    name = onStack,
                    type = "ViewGeneratorStack",
                    default = null
                )
            )
        }
        node.allAttributes[attributeReset]?.let {
            val (file, onStack) = getStackAndFile(node, it)
            operations.add(
                ViewStackOp.Reset(
                    stack = onStack,
                    viewName = file
                )
            )
            requires.add(
                ViewVar(
                    name = onStack,
                    type = "ViewGeneratorStack",
                    default = null
                )
            )
        }
        node.allAttributes[attributePopTo]?.let {
            val (file, onStack) = getStackAndFile(node, it)
            operations.add(
                ViewStackOp.PopTo(
                    stack = onStack,
                    viewType = file
                )
            )
            requires.add(
                ViewVar(
                    name = onStack,
                    type = "ViewGeneratorStack",
                    default = null
                )
            )
        }
        node.allAttributes[attributePop]?.let {
            (node.allAttributes[attributeOnStack]?.split(';') ?: listOf("stack")).map { it.trim() }.forEach {
                operations.add(ViewStackOp.Pop(stack = it))
                requires.add(
                    ViewVar(
                        name = it,
                        type = "ViewGeneratorStack",
                        default = null
                    )
                )
            }
        }
        node.allAttributes[attributeDismiss]?.let {
            (node.allAttributes[attributeOnStack]?.split(';') ?: listOf("stack")).map { it.trim() }.forEach {
                operations.add(ViewStackOp.Dismiss(stack = it))
                requires.add(
                    ViewVar(
                        name = it,
                        type = "ViewGeneratorStack",
                        default = null
                    )
                )
            }
        }
        node.allAttributes[attributeEmbed]?.let {
            operations.add(
                ViewStackOp.Embed(
                    replaceId = node.allAttributes["android:id"]!!.removePrefix("@+id/"),
                    viewName = it.removePrefix("@layout/").camelCase().capitalize()
                )
            )
        }
        node.allAttributes[attributeStackId]?.let { stackId ->
            provides.add(ViewVar(stackId, "ViewGeneratorStack", "ValueSubject(listOf())"))
            node.allAttributes[attributeStackDefault]?.let {
                operations.add(
                    ViewStackOp.StartWith(
                        stack = stackId,
                        viewName = it.removePrefix("@layout/").camelCase().capitalize()
                    )
                )
            }
        }
        node.allAttributes[attributeRequires]?.let {
            it.split(';').filter { it.isNotBlank() }.forEach { unfixed ->
                val it = unfixed.removePrefix("val ").removePrefix("var ").trim()
                val newVar = ViewVar(
                    name = it.substringBefore(':').trim(),
                    type = it.substringAfter(':').substringBefore('=').trim(),
                    default = it.substringAfter('=', "").takeUnless { it.isEmpty() }?.trim(),
                    onPath = path
                )
                println("newVar: $newVar")
                requires.add(newVar)
            }
        }
        node.allAttributes[attributeProvides]?.let {
            it.split(';').filter { it.isNotBlank() }.forEach { unfixed ->
                val it = unfixed.removePrefix("val ").removePrefix("var ").trim()
                provides.add(
                    ViewVar(
                        name = it.substringBefore(':').trim(),
                        type = it.substringAfter(':').substringBefore('=').trim(),
                        default = it.substringAfter('=', "").takeUnless { it.isEmpty() }?.trim(),
                        onPath = path
                    )
                )
            }
        }
        if (node.name == "include") {
            node.allAttributes["layout"]?.let {
                val targetName = it.removePrefix("@layout/")
                val nodeId = node.allAttributes["android:id"]?.removePrefix("@+id/")
                if (nodeId != null) {
                    if (targetName.startsWith("component", true)) {
                        val file = xml.parentFile.resolve(targetName.plus(".xml"))
                        gather(XmlNode.read(file, styles), xml, styles, path)
                    } else {
                        operations.add(
                            ViewStackOp.Embed(
                                replaceId = nodeId,
                                viewName = it.removePrefix("@layout/").camelCase().capitalize()
                            )
                        )
                    }
                } else {
                    node.allAttributes[attributeIgnore]?.let { if(it == "true") true else null }
                        ?: throw Exception("Element $targetName was included without an id assigned in ${xml.name}. If you do not wish to add an id add tool:ignore=\"true\" to the element.")
                }
            }
        }
        node.allAttributes["tools:listitem"]?.let {
            val p = it.removePrefix("@layout/")
            if (p.startsWith("component") || p.startsWith("android_")) {
                val file = xml.parentFile.resolve(p.plus(".xml"))
                gather(XmlNode.read(file, styles), xml, styles, parentPath)
            } else {
                operations.add(
                    ViewStackOp.Push(
                        stack = null,
                        viewName = it.removePrefix("@layout/").camelCase().capitalize()
                    )
                )
            }
        }
        node.children.forEach { gather(it, xml, styles, parentPath) }
    }
}

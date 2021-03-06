package com.lightningkite.rx.generators

import com.lightningkite.rx.utils.Mergable

data class ViewVar(val name: String, val type: String, val default: String? = null, val onPath: String? = null):
    Mergable<ViewVar> {
    val kotlinType: String get() = type.replace('[', '<').replace(']', '>')
    override fun toString(): String =
        "$name: $kotlinType" + (if (default != null) " = ${default.replace('[', '<').replace(']', '>')}" else "")

    fun construct(viewNode: ViewNode, viewNodeMap: Map<String, ViewNode>): String {
        return default?.replace('[', '<')?.replace(']', '>') ?: constructors[type]?.invoke(viewNode, viewNodeMap)
        ?: "$kotlinType()"
    }

    companion object {
        val constructors = HashMap<String, (viewNode: ViewNode, viewNodeMap: Map<String, ViewNode>) -> String>()

        init {
            constructors["ViewGeneratorStack"] = { _, _ -> "ValueSubject(listOf())" }
            constructors["Int"] = { _, _ -> "0" }
            constructors["Long"] = { _, _ -> "0L" }
            constructors["String"] = { _, _ -> "\"\"" }
            constructors["Float"] = { _, _ -> "0f" }
            constructors["Double"] = { _, _ -> "0.0" }
            constructors["Boolean"] = { _, _ -> "false" }
        }
    }

    fun satisfies(other: ViewVar): Boolean {
        if (this.name != other.name) return false
        if (this.type == other.type) return true
        if (this.type + "?" == other.type) return true
        if (this.type == "Mutable" + other.type) return true
        return false
    }

    fun sameAs(other: ViewVar): Boolean = this.name == other.name && this.type == other.type
    
    override fun merge(other: ViewVar): ViewVar? {
        if (this.sameAs(other)) {
            if(other.default != null && this.default == null){
                return this.copy(default = other.default)
            } else if(other.default == null && this.default != null){
                return other.copy(default = this.default)
            } else {
                return this
            }
        } else if (this.satisfies(other)) {
            return this
        } else if (other.satisfies(this)) {
            return other
        } else {
            return null
        }
    }

    data class Requirement(
        val viewVar: ViewVar,
        val paths: List<List<String>>
    ): Mergable<Requirement> {
        fun requiredBy(target: String): Requirement =
            Requirement(viewVar, paths.map { listOf(target) + it })

//        fun error(): String = "Variable $viewVar leaked with path: root -> $asRequiredBy"

        override fun merge(other: Requirement): Requirement? {
            val mergedViewVar = this.viewVar.merge(other.viewVar)
            if(mergedViewVar == null) return null
            return Requirement(
                viewVar = mergedViewVar,
                paths = this.paths + other.paths
            )
        }
    }

    fun requiredByMe(name: String): Requirement = Requirement(this, listOf(listOf()))
}

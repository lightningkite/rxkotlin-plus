package com.lightningkite.rx.flow

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.rx.generators.ViewNode
import com.lightningkite.rx.log
import com.lightningkite.rx.utils.XmlNode
import com.lightningkite.rx.utils.camelCase
import com.lightningkite.rx.utils.readXMLStyles
import java.io.File


fun createFlowDocumentation(androidFolder: File) =
    createFlowDocumentation(
        resourcesFolder = androidFolder.resolve("src/main/res"),
        docsOutputFolder = androidFolder.resolve("docs/flow")
    )

fun createFlowDocumentation(
    resourcesFolder: File,
    docsOutputFolder: File
) {
    docsOutputFolder.mkdirs()

    val styles = File(resourcesFolder, "values/styles.xml").readXMLStyles()
    val nodes = HashMap<String, ViewNode>()
    val files = File(resourcesFolder, "layout").walkTopDown()
        .filter { it.extension == "xml" }
        .filter { !it.name.startsWith("android_") }
        .filter { !it.name.contains("component") }

    //Gather graph information
    files.forEach { item ->
        log(item.toString())
        val fileName = item.nameWithoutExtension.camelCase().capitalize()
        val node = ViewNode(fileName)
        node.gather(XmlNode.read(item, styles), item, styles)
        nodes[fileName] = node
    }

    //Sort
    ViewNode.estimateDepth(nodes)

    //Emit info
    ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(docsOutputFolder.resolve("view-nodes.json"), nodes.mapValues { it.value.resolve(nodes) })

    //Emit inaccessible nodes warning
    val inaccessibleNodes = nodes.values.filter { node ->
        nodes.values.asSequence().flatMap { it.operations.asSequence() }.none { it.viewName == node.name }
    }
    inaccessibleNodes.forEach { println("WARNING! Node ${it.name} is not accessible") }

    //Throw on leaks
    ViewNode.assertNoLeaks(nodes)

    //Emit graph
    groupedGraph(docsOutputFolder, nodes)
    sortedGraph(docsOutputFolder, nodes)
    sortedNoReversalsGraph(docsOutputFolder, nodes)
    partialGraphs(docsOutputFolder, nodes)

    //Convert all mermaid diagrams
    try {
        val checksumFile = docsOutputFolder.resolve("mermaid-checksum.json")
        val checksums = checksumFile.takeIf { it.exists() }
            ?.let { jacksonObjectMapper().readValue<Map<String, Int>>(it) } ?: mapOf()
        val newChecksums = HashMap<String, Int>()
        docsOutputFolder.listFiles()?.filter { it.extension == "mmd" }?.forEach {
            val text = it.readText()
            val sum = text.hashCode()
            newChecksums[it.name] = sum
            if (checksums[it.name] != sum) {
                it.mermaidToSvg()
                it.mermaidToPng()
            } else {
                if (!it.parentFile.resolve(it.nameWithoutExtension + ".svg").exists()) {
                    it.mermaidToSvg()
                }
                if (!it.parentFile.resolve(it.nameWithoutExtension + ".png").exists()) {
                    it.mermaidToPng()
                }
            }
        }
        jacksonObjectMapper().writeValue(checksumFile, newChecksums)
    } catch(e:Exception) {
        e.printStackTrace()
        System.err.println("Failed to convert to SVG and PNG.  Have you installed the Mermaid command line tool?  Try 'npm install -g mermaid.cli'.")
    }

}

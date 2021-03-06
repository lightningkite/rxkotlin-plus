package com.lightningkite.rx.generators

import com.lightningkite.rx.log
import com.lightningkite.rx.utils.XmlNode
import com.lightningkite.rx.utils.camelCase
import com.lightningkite.rx.utils.readXMLStyles
import com.lightningkite.rx.xml.AndroidLayoutFile
import java.io.File

fun createPrototypeViewGenerators(androidFolder: File, applicationPackage: String) =
    createPrototypeViewGenerators(
        resourcesFolder = androidFolder.resolve("src/main/res"),
        applicationPackage = applicationPackage,
        outputFolder = androidFolder.resolve("src/main/java/${applicationPackage.replace('.', '/')}/vg")
    )

internal fun createPrototypeViewGenerators(
    resourcesFolder: File,
    applicationPackage: String,
    outputFolder: File
) {
    outputFolder.mkdirs()

    val styles = File(resourcesFolder, "values/styles.xml").takeIf { it.exists() }?.readXMLStyles() ?: mapOf()
    val layoutInfo = AndroidLayoutFile.parseAll(resourcesFolder, styles)

    val packageName =
        outputFolder.absolutePath.replace('\\', '/').substringAfter("src/main/").substringAfter('/').replace('/', '.')
    val nodes = HashMap<String, ViewNode>()
    val files = File(resourcesFolder, "layout").walkTopDown()
        .filter { it.extension == "xml" }
        .filter { !it.name.contains("component") }
        .filter { !it.name.startsWith("android_") }

    //Gather graph information
    files.forEach { item ->
        log(item.toString())
        val fileName = item.nameWithoutExtension.camelCase().capitalize()
        val node = ViewNode(fileName)
        node.gather(XmlNode.read(item, styles), item, styles)
        nodes[fileName] = node
    }

    //Emit inaccessible nodes warning
    val inaccessibleNodes = nodes.values.filter { node ->
        nodes.values.asSequence().flatMap { it.operations.asSequence() }.none { it.viewName == node.name }
    }
    inaccessibleNodes.forEach { println("WARNING! Node ${it.name} is not accessible") }

    //Throw on leaks
    ViewNode.assertNoLeaks(nodes)

    //Emit views
    val scannedDirectoryInfo = outputFolder.walkTopDown().filter { !it.isDirectory }.associate { it.name to it }
    files.forEach { item ->
        log(item.toString())
        val fileName = item.nameWithoutExtension.camelCase().capitalize()
        val node = nodes[fileName] ?: return@forEach
        val targetFileName = fileName + "VG.kt"
        val altTargetFileName = fileName + "VG.shared.kt"
        val targetFile = scannedDirectoryInfo[targetFileName] ?: scannedDirectoryInfo[altTargetFileName] ?: outputFolder.resolve(targetFileName)
        createPrototypeVG(
            layoutInfo = layoutInfo,
            styles = styles,
            viewName = fileName,
            xml = item,
            target = targetFile,
            viewNodeMap = nodes,
            viewNode = node,
            packageName = packageName + (targetFile.relativeTo(outputFolder)
                .path
                .split(File.separator)
                .filter { it.firstOrNull()?.isLowerCase() == true }
                .takeUnless { it.isEmpty() }
                ?.joinToString(".", ".")
                ?: ""),
            applicationPackage = applicationPackage
        )
    }
}

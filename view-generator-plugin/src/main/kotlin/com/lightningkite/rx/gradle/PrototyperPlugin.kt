package com.lightningkite.rx.gradle

import com.lightningkite.rx.PrototyperSettings
import com.lightningkite.rx.flow.createFlowDocumentation
import com.lightningkite.rx.generators.createPrototypeViewGenerators
import com.lightningkite.rx.utils.getPropertyAsObject
import com.lightningkite.rx.utils.groovyObject
import org.gradle.api.Plugin
import org.gradle.api.Project

open class PrototyperPluginExtension {
    open var layoutPackage: String? = null
    open var injectKhrysalisAnnotations: Boolean = false
}

lateinit var pluginConfiguration:PrototyperPluginExtension

class PrototyperPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val project = target
        pluginConfiguration = project.extensions.create<PrototyperPluginExtension>("prototyper", PrototyperPluginExtension::class.java)

        PrototyperSettings.verbose = true
        fun androidBase() = project.projectDir
        fun packageName() =
            pluginConfiguration.layoutPackage ?: project.extensions.findByName("android")?.groovyObject?.getPropertyAsObject("defaultConfig")
                ?.getProperty("applicationId") as? String ?: "unknown.packagename"


        //Prototyping

        project.tasks.create("VGPrototype") { task ->
            task.group = "prototype"
            task.doLast {
                createPrototypeViewGenerators(
                    androidFolder = androidBase(),
                    applicationPackage = pluginConfiguration.layoutPackage ?: packageName()
                )

            }
        }
        project.tasks.create("VGflowDoc") { task ->
            task.group = "prototype"
            task.doLast {
                createFlowDocumentation(
                    androidFolder = androidBase()
                )
            }
        }
    }
}

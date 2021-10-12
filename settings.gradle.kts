
pluginManagement {
    repositories {
        google()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

include("android-bindings")
include("android-resources")
include("view-generator")
include("view-generator-plugin")


pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

include("rxplus")
include("okhttp")
include("okhttp-jackson")
include("okhttp-resources")
include("android-bindings")
include("android-resources")
include("view-generator")
include("view-generator-example")
include("view-generator-plugin")

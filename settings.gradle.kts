
pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

include("android-bindings")
include("android-bluetooth")
include("android-resources")
include("dsl")
include("okhttp")
include("okhttp-jackson")
include("okhttp-resources")
include("rxplus")
include("view-generator")
include("view-generator-plugin")
include("view-generator-fcm")

include("view-generator-example")

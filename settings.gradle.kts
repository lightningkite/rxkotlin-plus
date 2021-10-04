
pluginManagement {
    repositories {
        google()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

include("rxkotlin-property")
include("rxkotlin-property-plugin")
include("rxkotlin-property-android")
include("rxkotlin-property-android-resources")
include("rxkotlin-property-view-generator")
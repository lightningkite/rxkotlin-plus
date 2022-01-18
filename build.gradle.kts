
buildscript {
    val kotlinVersion:String by project
    repositories {
        google()
        maven(url="https://s01.oss.sonatype.org/content/repositories/snapshots/")
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$kotlinVersion")
        classpath("com.lightningkite:deploy-helpers:master-SNAPSHOT")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
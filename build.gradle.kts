
buildscript {
    val kotlinVersion:String by project
    repositories {
//        mavenLocal()
        mavenCentral()
//        maven(url="https://s01.oss.sonatype.org/content/repositories/snapshots/")
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
        classpath("com.lightningkite:deploy-helpers:0.0.5")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/releases/")
        google()
    }
}
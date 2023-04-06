import com.lightningkite.deployhelpers.*

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("signing")
    id("org.jetbrains.dokka")
    `maven-publish`

}

group = "com.lightningkite.rx"

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

val reaktiveVersion: String by project
dependencies {
    api(project(":rxplus"))
    api("com.badoo.reaktive:reaktive:$reaktiveVersion")
    api("androidx.core:core-ktx:1.9.0")
    api("androidx.recyclerview:recyclerview:1.3.0")
    api("com.google.android.material:material:1.8.0")
    api("dev.b3nedikt.viewpump:viewpump:4.0.10")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}


standardPublishing {
    name.set("RxPlus-Android")
    description.set("An Android view binding library built on top of RxPlus.")
    github("lightningkite", "rxkotlin-plus")
    licenses { mit() }

    developers {
        developer {
            id.set("bjsvedin")
            name.set("Brady Svedin")
            email.set("brady@lightningkite.com")
        }
        developer {
            id.set("LightningKiteJoseph")
            name.set("Joseph Ivie")
            email.set("joseph@lightningkite.com")
        }
    }
}

project.afterEvaluate {
    project.android.sourceSets.forEach { sourceSet ->
        if(sourceSet.name.startsWith("main")) {
            println(sourceSet.java.srcDirs)
        }
    }
}
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
    compileSdk = 31
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":rxplus"))
    api("io.reactivex.rxjava3:rxandroid:3.0.0")
    api("androidx.core:core-ktx:1.7.0")
    api("androidx.recyclerview:recyclerview:1.2.1")
    api("com.google.android.material:material:1.5.0")
    api("dev.b3nedikt.viewpump:viewpump:4.0.10")
    api("com.jakewharton.rxbinding4:rxbinding:4.0.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
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
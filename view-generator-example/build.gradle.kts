
plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    //    buildToolsVersion = "28.0.3"
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        multiDexEnabled = true
        applicationId = "com.lightningkite.rxexample"
        versionCode = 5
        versionName = "1.0.5"
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        exclude("META-INF/**")
    }
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    mavenLocal()
}

val kotlin_version = "1.5.30"
dependencies {
    api(project(":view-generator"))
    api(project(":okhttp-resources"))
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
}


tasks.create("wrapper"){

}
tasks.create("prepareKotlinBuildScriptModel"){

}
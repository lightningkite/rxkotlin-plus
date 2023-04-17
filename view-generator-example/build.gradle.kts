val kotlinVersion:String by project

plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("plugin.serialization")
}

android {
    //    buildToolsVersion = "28.0.3"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 31
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
        resources.excludes.add("META-INF/**")
    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")

    implementation(project(":view-generator"))
    implementation(project(":okhttp"))
    implementation(project(":okhttp-resources"))
    implementation(project(":dsl"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
}

tasks.create("wrapper"){

}
tasks.create("prepareKotlinBuildScriptModel"){

}
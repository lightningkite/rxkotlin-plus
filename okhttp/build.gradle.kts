import com.lightningkite.deployhelpers.*

plugins {
    id("kotlin")
    kotlin("plugin.serialization")
    id("signing")
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lightningkite.rx"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.2.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    api("io.reactivex.rxjava3:rxjava:3.1.3")
    api("io.reactivex.rxjava3:rxkotlin:3.0.1")
    api("com.squareup.okhttp3:okhttp:4.9.3")
}

standardPublishing {
    name.set("RxPlus-OkHttp")
    description.set("An OkHttp wrapper based on RxJava using KotlinX Serialization for JSON.")
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
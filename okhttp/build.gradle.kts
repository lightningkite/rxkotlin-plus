import com.lightningkite.deployhelpers.*

plugins {
    id("kotlin")
    kotlin("plugin.serialization")
    id("signing")
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lightningkite.rx"

val reaktiveVersion: String by project
dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.8.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    api("com.badoo.reaktive:reaktive:$reaktiveVersion")
    api("com.squareup.okhttp3:okhttp:4.10.0")
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
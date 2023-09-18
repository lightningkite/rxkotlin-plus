import com.lightningkite.deployhelpers.*

plugins {
    id("kotlin")
    id("signing")
    id("org.jetbrains.dokka")
    `maven-publish`

}

group = "com.lightningkite.rx"

val jacksonVersion = "2.14.2"
val reaktiveVersion: String by project
dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.8.0")
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    api("com.badoo.reaktive:reaktive:$reaktiveVersion")
    api("com.squareup.okhttp3:okhttp:4.10.0")
}

standardPublishing {
    name.set("RxPlus-OkHttp-Jackson")
    description.set("An OkHttp wrapper based on RxJava using Jackson for JSON")
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
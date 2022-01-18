import com.lightningkite.deployhelpers.*

plugins {
    id("kotlin")
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
    testImplementation("org.mockito:mockito-core:4.1.0")
    api("io.reactivex.rxjava3:rxjava:3.1.3")
    api("io.reactivex.rxjava3:rxkotlin:3.0.1")
}

standardPublishing {
    name.set("RxPlus")
    description.set("A set of extensions for RxKotlin, primarily revolving around Subject.")
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
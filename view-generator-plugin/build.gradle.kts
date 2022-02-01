import com.lightningkite.deployhelpers.*

plugins {
    kotlin("jvm")
    java
    `java-gradle-plugin`
    idea
    signing
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lightningkite.rx"

gradlePlugin {
    plugins {
        val prototyperPlugin by creating() {
            id = "com.lightningkite.rx"
            implementationClass = "com.lightningkite.rx.gradle.PrototyperPlugin"
        }
    }
}

repositories {
    mavenCentral()
    google()
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val kotlinVersion: String by project
dependencies {
    api(localGroovy())
    api(gradleApi())

    api(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = kotlinVersion)
    api(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin-api", version = kotlinVersion)

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    api("org.apache.commons:commons-lang3:3.12.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")
    api("net.jodah:xsylum:0.1.0")

    // https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-transcoder
    implementation(group = "org.apache.xmlgraphics", name = "batik-transcoder", version = "1.13")
    implementation(group = "org.apache.xmlgraphics", name = "batik-codec", version = "1.13")

    // https://mvnrepository.com/artifact/net.mabboud.fontverter/FontVerter
    implementation(group = "net.mabboud.fontverter", name = "FontVerter", version = "1.2.22")

    testImplementation("junit:junit:4.13.2")

    val aetherVersion = "1.1.0"
    val mavenVersion = "3.3.9"
    testApi("org.eclipse.aether:aether-api:$aetherVersion")
    testApi("org.eclipse.aether:aether-impl:$aetherVersion")
    testApi("org.eclipse.aether:aether-util:$aetherVersion")
    testApi("org.eclipse.aether:aether-connector-basic:$aetherVersion")
    testApi("org.eclipse.aether:aether-transport-file:$aetherVersion")
    testApi("org.eclipse.aether:aether-transport-http:$aetherVersion")
    testApi("org.apache.maven:maven-aether-provider:$mavenVersion")
}


standardPublishing {
    name.set("ViewGenerators-Plugin")
    description.set("A Gradle plugin that automatically generates ViewGenerators from Android Layout XMLs")
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

import java.util.Properties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven")
    id("signing")
    id("org.jetbrains.dokka") version "1.5.0"
    `maven-publish`
}

group = "com.lightningkite.rxkotlinproperty"
version = "0.0.1"


val props = project.rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { stream ->
    Properties().apply { load(stream) }
}
val signingKey: String? = (System.getenv("SIGNING_KEY")?.takeUnless { it.isEmpty() }
    ?: project.properties["signingKey"]?.toString())
    ?.lineSequence()
    ?.filter { it.trim().firstOrNull()?.let { it.isLetterOrDigit() || it == '=' || it == '/' || it == '+' } == true }
    ?.joinToString("\n")
val signingPassword: String? = System.getenv("SIGNING_PASSWORD")?.takeUnless { it.isEmpty() }
    ?: project.properties["signingPassword"]?.toString()
val useSigning = signingKey != null && signingPassword != null

if (signingKey != null) {
    if (!signingKey.contains('\n')) {
        throw IllegalArgumentException("Expected signing key to have multiple lines")
    }
    if (signingKey.contains('"')) {
        throw IllegalArgumentException("Signing key has quote outta nowhere")
    }
}

val deploymentUser = (System.getenv("OSSRH_USERNAME")?.takeUnless { it.isEmpty() }
    ?: project.properties["ossrhUsername"]?.toString())
    ?.trim()
val deploymentPassword = (System.getenv("OSSRH_PASSWORD")?.takeUnless { it.isEmpty() }
    ?: project.properties["ossrhPassword"]?.toString())
    ?.trim()
val useDeployment = deploymentUser != null || deploymentPassword != null

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

android {
    compileSdkVersion(31)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(31)
        versionCode = 0
        versionName = "0.0.1"
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":property"))
    api(project(":android-resources"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    api("androidx.appcompat:appcompat:1.3.1")
}

tasks {
    val sourceJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets["main"].java.srcDirs)
        from(project.projectDir.resolve("src/include"))
    }
    val javadocJar by creating(Jar::class) {
        dependsOn("dokkaJavadoc")
        archiveClassifier.set("javadoc")
        from(project.file("build/dokka/javadoc"))
    }
    artifacts {
        archives(sourceJar)
        archives(javadocJar)
    }
}

afterEvaluate {
    publishing {
        publications {
            val release by creating(MavenPublication::class) {
                from(components["release"])
                artifact(tasks.getByName("sourceJar"))
                artifact(tasks.getByName("javadocJar"))
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
            val debug by creating(MavenPublication::class) {
                from(components["debug"])
                artifact(tasks.getByName("sourceJar"))
                artifact(tasks.getByName("javadocJar"))
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
        }
    }
    if (useSigning) {
        signing {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(configurations.archives.get())
        }
    }
}

if (useDeployment) {
    tasks.register("uploadSnapshot") {
        group = "upload"
        finalizedBy("uploadArchives")
        doLast {
            project.version = project.version.toString() + "-SNAPSHOT"
        }
    }

    tasks.named<Upload>("uploadArchives") {
        repositories.withConvention(MavenRepositoryHandlerConvention::class) {
            mavenDeployer {
                beforeDeployment {
                    signing.signPom(this)
                }
            }
        }

        repositories.withGroovyBuilder {
            "mavenDeployer"{
                "repository"("url" to "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                    "authentication"(
                        "userName" to deploymentUser,
                        "password" to deploymentPassword
                    )
                }
                "snapshotRepository"("url" to "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
                    "authentication"(
                        "userName" to deploymentUser,
                        "password" to deploymentPassword
                    )
                }
                "pom" {
                    "project" {
                        setProperty("name", "RxKotlin-Property-View-Generators")
                        setProperty("packaging", "aar")
                        setProperty(
                            "description",
                            "A layout manager for Android."
                        )
                        setProperty("url", "https://github.com/lightningkite/rxkotlin-property")

                        "scm" {
                            setProperty("connection", "scm:git:https://github.com/lightningkite/rxkotlin-property.git")
                            setProperty(
                                "developerConnection",
                                "scm:git:https://github.com/lightningkite/rxkotlin-property.git"
                            )
                            setProperty("url", "https://github.com/lightningkite/rxkotlin-property")
                        }

                        "licenses" {
                            "license"{
                                setProperty("name", "The MIT License (MIT)")
                                setProperty("url", "https://www.mit.edu/~amini/LICENSE.md")
                                setProperty("distribution", "repo")
                            }

                        }
                        "developers"{
                            "developer"{
                                setProperty("id", "bjsvedin")
                                setProperty("name", "Brady Svedin")
                                setProperty("email", "brady@lightningkite.com")
                            }
                        }
                    }
                }
            }
        }
    }
}
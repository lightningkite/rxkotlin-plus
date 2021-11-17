import java.util.Properties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("signing")
    id("org.jetbrains.dokka") version "1.5.0"
    `maven-publish`
    id("com.lightningkite.khrysalis")
}

group = "com.lightningkite.rx"
version = "0.0.5"


val props = project.rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { stream ->
    Properties().apply { load(stream) }
} ?: Properties()
val signingKey: String? = (System.getenv("SIGNING_KEY")?.takeUnless { it.isEmpty() }
    ?: props["signingKey"]?.toString())
    ?.lineSequence()
    ?.filter { it.trim().firstOrNull()?.let { it.isLetterOrDigit() || it == '=' || it == '/' || it == '+' } == true }
    ?.joinToString("\n")
val signingPassword: String? = System.getenv("SIGNING_PASSWORD")?.takeUnless { it.isEmpty() }
    ?: props["signingPassword"]?.toString()
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
    ?: props["ossrhUsername"]?.toString())
    ?.trim()
val deploymentPassword = (System.getenv("OSSRH_PASSWORD")?.takeUnless { it.isEmpty() }
    ?: props["ossrhPassword"]?.toString())
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
        versionName = "0.0.4"
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":rxplus"))
    api("io.reactivex.rxjava3:rxandroid:3.0.0")
    api("androidx.core:core-ktx:1.6.0")
    api("androidx.recyclerview:recyclerview:1.2.1")
    api("com.google.android.material:material:1.4.0")
    api("dev.b3nedikt.viewpump:viewpump:4.0.7")
    api("com.jakewharton.rxbinding4:rxbinding:4.0.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
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
            create<MavenPublication>("release") {
                from(components["release"])
                artifact(tasks.getByName("sourceJar"))
                artifact(tasks.getByName("javadocJar"))
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                setPom()
            }
            create<MavenPublication>("debug") {
                from(components["debug"])
                artifact(tasks.getByName("sourceJar"))
                artifact(tasks.getByName("javadocJar"))
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                setPom()
            }
        }
        repositories {
            if (useSigning) {
                maven {
                    name = "MavenCentral"
                    val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                    credentials {
                        this.username = deploymentUser
                        this.password = deploymentPassword
                    }
                }
            }
        }
    }
    if (useSigning) {
        signing {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
        }
    }
}

fun MavenPublication.setPom() {
    pom {
        name.set("RxPlus-Android")
        description.set("An Android view binding library built on top of RxPlus.")
        url.set("https://github.com/lightningkite/rxkotlin-plus")

        scm {
            connection.set("scm:git:https://github.com/lightningkite/rxkotlin-plus.git")
            developerConnection.set("scm:git:https://github.com/lightningkite/rxkotlin-plus.git")
            url.set("https://github.com/lightningkite/rxkotlin-plus")
        }

        licenses {
            license {
                name.set("The MIT License (MIT)")
                url.set("https://www.mit.edu/~amini/LICENSE.md")
                distribution.set("repo")
            }
        }

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
}
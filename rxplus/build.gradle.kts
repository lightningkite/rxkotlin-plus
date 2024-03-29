import com.lightningkite.deployhelpers.*

plugins {
    id("kotlin")
    id("signing")
    id("org.jetbrains.dokka")
    `maven-publish`

}

group = "com.lightningkite.rx"

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.8.0")
    api("io.reactivex.rxjava3:rxjava:3.1.5")
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

sourceSets.forEach {
    val dirSet = objects.sourceDirectorySet("equivalents", "Khrysalis Equivalents")
    dirSet.srcDirs(project.projectDir.resolve("src/${it.name}/equivalents"))
    it.extensions.add("equivalents", dirSet)
    project.tasks.create("equivalentsJar${it.name.capitalize()}", org.gradle.jvm.tasks.Jar::class.java) {
        this.group = "khrysalis"
        this.archiveClassifier.set("equivalents")
        this.from(dirSet)
    }
}

tasks.getByName("equivalentsJarMain").published = true
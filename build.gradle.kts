
buildscript {
    val kotlinVersion = "1.5.31"
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
    dependencies {
//        classpath("com.lightningkite.khrysalis:plugin:0.1.0")
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val versionName by extra("3.3.0")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath(kotlin("gradle-plugin", "1.5.0"))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

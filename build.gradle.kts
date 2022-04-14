buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        google()
    }

    dependencies {
        classpath(Android.tools.build.gradlePlugin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:_")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
    }
}

allprojects {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        google()
    }

    group = project.properties["GROUP"]!!
    version = project.properties["VERSION_NAME"]!!
    if (hasProperty("SNAPSHOT") || System.getenv("SNAPSHOT") != null) {
        version = "$version-SNAPSHOT"
    }
}


buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        classpath(Android.tools.build.gradlePlugin)
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:_")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:_")
    }
}

allprojects {
    repositories {
        google()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
    }

    group = project.properties["GROUP"]!!
    version = project.properties["VERSION_NAME"]!!
    if (hasProperty("SNAPSHOT") || System.getenv("SNAPSHOT") != null) {
        version = "$version-SNAPSHOT"
    }
}

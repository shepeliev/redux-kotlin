import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    androidNativeArm32()
    androidNativeArm64()

    val isMacOsX = DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX
    if (isMacOsX) {
        iosX64()
        iosArm32()
        iosArm64()
        iosSimulatorArm64()
        tvosArm64()
        tvosX64()
        macosX64()
        watchosArm32()
        watchosArm64()
        watchosX86()
    }

    js(BOTH) {
        browser()
        nodejs()

        listOf(compilations["main"], compilations["test"]).forEach {
            with(it.kotlinOptions) {
                moduleKind = "umd"
                sourceMap = true
                sourceMapEmbedSources = "always"
                metaInfo = true
            }
        }
    }

    jvm()
    linuxArm32Hfp()
    linuxArm64()
    linuxMips32()
    linuxMipsel32()
    linuxX64()
    mingwX64()
    mingwX86()
    wasm32()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Testing.mockK.common)
            }
        }

        val fallback: (org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.() -> Unit) = {
            kotlin.srcDir("src/fallbackMain/kotlin")
        }
        val ios: (org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.() -> Unit) = {
            kotlin.srcDir("src/iosMain/kotlin")
        }

        val androidMain by getting {
            // this way we can share the JVM and the Android implementation
            // see https://jeroenmols.com/blog/2021/03/17/share-code-kotlin-multiplatform
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(KotlinX.coroutines.test)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:_")
                implementation(Testing.mockK)
                runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:_")
            }
        }
        val androidNativeArm32Main by getting(fallback)
        val androidNativeArm64Main by getting(fallback)

        if (isMacOsX) {
            val iosX64Main by getting
            val iosArm32Main by getting(ios)
            val iosArm64Main by getting
            val iosSimulatorArm64Main by getting
            val iosMain by creating {
                dependsOn(commonMain)
                iosX64Main.dependsOn(this)
                iosArm32Main.dependsOn(this)
                iosArm64Main.dependsOn(this)
                iosSimulatorArm64Main.dependsOn(this)
            }

            val iosX64Test by getting
            val iosArm32Test by getting(ios)
            val iosArm64Test by getting
            val iosSimulatorArm64Test by getting
            val iosTest by creating {
                dependsOn(commonTest)
                iosX64Test.dependsOn(this)
                iosArm32Test.dependsOn(this)
                iosArm64Test.dependsOn(this)
                iosSimulatorArm64Test.dependsOn(this)
            }

            val tvosArm64Main by getting(ios)
            val tvosX64Main by getting(ios)
            val watchosArm32Main by getting(ios)
            val watchosArm64Main by getting(ios)
            val watchosX86Main by getting(ios)
        }

        val linuxArm32HfpMain by getting(fallback)
        val linuxArm64Main by getting(fallback)
        val linuxMips32Main by getting(fallback)
        val linuxMipsel32Main by getting(fallback)
        val linuxX64Main by getting(fallback)
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(KotlinX.coroutines.test)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:_")
                implementation(Testing.mockK)
                runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:_")
            }
        }

        val mingwX64Main by getting(fallback)
        val mingwX86Main by getting(fallback)
        val wasm32Main by getting(fallback)
    }
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = compileSdk
    }

    sourceSets["main"].run {
        manifest.srcFile("src/commonJvmAndroid/AndroidManifest.xml")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

afterEvaluate {
    tasks {
        // Alias the task names we use elsewhere to the new task names.
        create("installMP").dependsOn("publishKotlinMultiplatformPublicationToMavenLocal")
        create("installLocally") {
            dependsOn("publishKotlinMultiplatformPublicationToTestRepository")
            dependsOn("publishJvmPublicationToTestRepository")
            dependsOn("publishJsPublicationToTestRepository")
            dependsOn("publishMetadataPublicationToTestRepository")
        }
        create("installIosLocally") {
            dependsOn("publishKotlinMultiplatformPublicationToTestRepository")
            dependsOn("publishIosArm32PublicationToTestRepository")
            dependsOn("publishIosArm64PublicationToTestRepository")
            dependsOn("publishIosX64PublicationToTestRepository")
            dependsOn("publishMetadataPublicationToTestRepository")
        }
        // NOTE: We do not alias uploadArchives because CI runs it on Linux and we only want to run it on Mac OS.
        // tasks.create("uploadArchives").dependsOn("publishKotlinMultiplatformPublicationToMavenRepository")
    }
}

apply(from = rootProject.file("gradle/publish.gradle"))

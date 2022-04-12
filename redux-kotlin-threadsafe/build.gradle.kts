plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-atomicfu")
}

kotlin {
    android()
    iosArm64()
    iosX64()
    js(IR) {
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
    linuxX64()
    macosX64()
    mingwX64()
    tvosArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()

//    below are currently not supported by atomicfu
//    mingwX86()
//    androidNativeArm32()
//    androidNativeArm64()
//    iosArm32()
//    wasm32("wasm")
//    linuxArm32Hfp("linArm32")
//    linuxMips32("linMips32")
//    linuxMipsel32("linMipsel32")
//    linuxArm64()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":redux-kotlin"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Testing.mockK.common)
                implementation(KotlinX.coroutines.core)
            }
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

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = compileSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets["main"].run {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        resources.srcDirs(
            "src/androidMain/resources",
            "src/commonMain/resources",
        )
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

// todo
//apply(from = rootProject.file("gradle/publish.gradle"))

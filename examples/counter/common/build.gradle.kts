plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    jvm()

    iosArm32()
    iosArm64()
    iosX64()
    // TODO
    // iosSimulatorArm64()

    js(IR) {
        browser()
        binaries.executable()

        listOf(compilations["main"], compilations["test"]).forEach {
            with(it.kotlinOptions) {
                moduleKind = "umd"
                sourceMap = true
                sourceMapEmbedSources = "always"
                metaInfo = true
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":redux-kotlin"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Testing.mockK.common)
            }
        }
        val androidTest by getting {
            dependencies {
                dependsOn(commonTest)
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(Testing.mockK)
            }
        }
        val jvmTest by getting {
            dependencies {
                dependsOn(androidTest)
            }
        }
        val jsTest by getting {
            dependencies {
                dependsOn(commonTest)
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
    }

    sourceSets["main"].run {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

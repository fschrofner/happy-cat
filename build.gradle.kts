val toolVersion: String by project
val koinVersion: String by project
val cliktVersion: String by project
val coroutinesVersion: String by project
val serializationVersion: String by project
val datetimeVersion: String by project
val ktorVersion: String by project

plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "fi.schro"
version = toolVersion

application {
    mainClass.set("fi.schro.Main")
}

repositories {
    mavenCentral()
}

kotlin {
    //java compilation
    jvm {
        withJava()
    }

    //native compilation
    val hostOs = System.getProperty("os.name")

    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable("hc") {
                entryPoint = "fi.schro.main"
            }
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xdisable-phases=EscapeAnalysis"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$datetimeVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
            }
        }
        val commonTest by getting
        val nativeMain by getting
        val jvmMain by getting
    }
}
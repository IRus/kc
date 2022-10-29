plugins {
    kotlin("jvm").version("1.7.20")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions {
        languageVersion = "1.7"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-progressive",
            "-opt-in=kotlin.ExperimentalStdlibApi",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xemit-jvm-type-annotations",
            "-Xcontext-receivers",
            "-Xexplicit-api=warning",
        )
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

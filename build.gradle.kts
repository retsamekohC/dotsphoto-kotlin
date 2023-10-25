plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    kotlin("jvm") version embeddedKotlinVersion
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
}

dependencies {
    val ktor_version: String by project

    dependencies {
        implementation("io.ktor:ktor-client-core:2.3.5")
    }
}

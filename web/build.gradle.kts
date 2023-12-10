plugins {
    kotlin("multiplatform")
}

kotlin {
    js (IR){
        browser {
        }
        binaries.executable()
    }
    sourceSets {
        val ktorVersion: String by project
        val jsMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("org.jetbrains:kotlin-react:17.0.1-pre.148-kotlin-1.4.21")
                implementation("org.jetbrains:kotlin-react-dom:17.0.1-pre.148-kotlin-1.4.21")
                implementation(npm("react", "17.0.1"))
                implementation(npm("react-dom", "17.0.1"))
                implementation("org.jetbrains:kotlin-styled:5.2.1-pre.148-kotlin-1.4.21")
                implementation(npm("styled-components", "~5.2.1"))
                implementation(npm("react-player", "2.12.0"))
                implementation(npm("react-share", "4.4.1"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
    }
}
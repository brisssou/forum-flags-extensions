plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.compose.html.core)
                implementation(libs.compose.runtime)
                implementation(project(":js-lib"))
                implementation(project(":common"))
            }
        }
    }
}

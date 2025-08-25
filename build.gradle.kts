plugins {
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinMultiplatform)
}

group = "com.composeweb.chrome"
version = "1.0.0-alpha01"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
//composeCompiler {
//    includeSourceInformation = true
//
//    featureFlags = setOf(
//        ComposeFeatureFlag.StrongSkipping.disabled(),
//        ComposeFeatureFlag.OptimizeNonSkippingGroups
//    )
//}
kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.html.core)
                implementation(compose.runtime)
            }
        }
    }
}

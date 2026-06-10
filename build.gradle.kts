plugins {
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.git.versioning)
}

allprojects {
    group = "com.myflags"
}

// Version derived entirely from git (propagated to every module by the plugin):
// a `vX.Y.Z` tag -> `X.Y.Z`; any other ref (branch, detached HEAD) -> the short
// commit SHA. No literal fallback — without git it stays Gradle's "unspecified".
gitVersioning.apply {
    refs {
        tag("v(?<version>[0-9].*)") { version = "\${ref.version}" }
        branch(".+") { version = "\${commit.short}" }
    }
    rev { version = "\${commit.short}" }
}

subprojects {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
pluginManagement {
    val kotlinMppVersion: String by settings
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    plugins {
        kotlin("multiplatform") version kotlinMppVersion
        id("org.jetbrains.compose") version "1.7.3"
    }
}
rootProject.name = "compose-chrome-extension-template"


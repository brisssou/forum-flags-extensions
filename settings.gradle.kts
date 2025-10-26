pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    plugins {
    }
}
rootProject.name = "forum-flags-extension"

include("common", "worker", "popup", "prefs", "package")

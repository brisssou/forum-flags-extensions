pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    plugins {
    }
}
rootProject.name = "forum-flags-extension"

include("js-lib", "common", "worker", "popup", "prefs", "package")

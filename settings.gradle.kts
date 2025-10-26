pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    plugins {
    }
}
rootProject.name = "forum-flags-extension"

include("common")
include("worker")
include("popup")
include("prefs")
include("package")

plugins {
    base
}

val copyTarget = layout.buildDirectory.dir("dist")
val theCopy by tasks.registering(Copy::class) {
    from(project(":common").layout.buildDirectory.dir("/dist/js/productionExecutable"))
    from(project(":popup").layout.buildDirectory.dir("/dist/js/productionExecutable"))
    from(project(":prefs").layout.buildDirectory.dir("/dist/js/productionExecutable"))
    from(project(":worker").layout.buildDirectory.dir("/dist/js/productionExecutable"))
    from(layout.projectDirectory.dir("src/main/resources"))
    into(copyTarget)
}

val pack by tasks.registering(Zip::class) {
    from(copyTarget)
    from(layout.buildDirectory.dir("src/main/resources"))
}

tasks {
    theCopy {
        dependsOn(
            ":common:jsBrowserDistribution",
            ":popup:jsBrowserDistribution",
            ":prefs:jsBrowserDistribution",
            ":worker:jsBrowserDistribution"
        )
    }

    pack {
        dependsOn(theCopy)
    }

    assemble {
        dependsOn(pack)
    }
}